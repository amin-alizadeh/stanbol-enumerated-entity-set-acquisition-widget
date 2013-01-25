package com.conatix.EntitySetAdder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class for running the actual fetching the URLs and validating the entities.
 * The entities come from wikipedia pages. There are two interesting types of
 * pages in wikipedia:
 * <ul>
 * <li>Categories: Which are not actual entities, but the list of pages
 * (entities) can be found there.
 * <li>Pages: Which are the actual entities. They are then sent to the entityhub
 * of Apache Stanbol and also stored in the RDF file.
 * </ul>
 */
public class EntitySetScheduled extends TimerTask {

	private static List<String> allUrls;
	private static String validateUrl;
	private static String language;
	private static BufferedWriter rdfWriter;
	private static LogWriter logger;
	private static String entityUrl;
	private static int count;
	private static Timer entitySchedule;

	/**
	 * Constructor method for the class.
	 * 
	 * @param entitySchedule
	 *            the Timer variable which is initially created in the main
	 *            method. This is passed to this class so the scheduler can be
	 *            stopped.
	 * @param allUrls
	 *            The list of URLs to be fetched, analyzed, and parsed to find
	 *            further categories and pages. The discovered pages are stored
	 *            as Named Entities in Apache Stanbol entityhub.
	 * @param validateUrl
	 *            When fetching the pages from a list in Wikipedia, the
	 *            addresses are relative. This will add the correct part at the
	 *            beginning of the URL so they are valid URLs. The default value
	 *            is http://fa.wikipedia.org
	 * @param language
	 *            The language for the RDF request. The default value is Farsi
	 *            (Persian).
	 * @param rdfWriter
	 *            The RDF file where entities are stored in RDF Standard format.
	 *            The default filename is persian-wiki.rdf.
	 * @param logger
	 *            LogFile is where the logs are stored. It logs in the result of
	 *            the entity fetch and entity add.
	 * @param entityUrl
	 *            The entity where Apache Stanbol is installed. It ends with
	 *            /entity/entityhub . The default value is
	 *            http://dev.iks-project.eu:8081/entityhub/entity
	 * 
	 * @see {@link #run()}, {@link #correctUrl(String)}
	 */

	public EntitySetScheduled(Timer entitySchedule, List<String> allUrls,
			String validateUrl, String language, BufferedWriter rdfWriter,
			LogWriter logger, String entityUrl) {
		this.allUrls = allUrls;
		this.validateUrl = validateUrl;
		this.language = language;
		this.rdfWriter = rdfWriter;
		this.logger = logger;
		this.entityUrl = entityUrl;
		this.count = 0;
		this.entitySchedule = entitySchedule;
	}

	/**
	 * This method is run in intervals which is configurable in the XML file of
	 * configurations or on the command line. It fetches each url within the
	 * allUrls parameter, parses, and discovers the pages and categories in each
	 * url which comes from the urls.xml file.
	 */

	@SuppressWarnings("unused")
	@Override
	public void run() {

		URLFetcher wikiUrls = new URLFetcher(allUrls.get(count), validateUrl);
		List<String> pUrls = wikiUrls.getPagesUrls();
		List<String> pTitles = wikiUrls.getPagesTitle();
		List<String> cUrls = wikiUrls.getCategoriesUrls();
		List<String> cTitles = wikiUrls.getCategoriesTitle();

		for (int i = 0; i < pUrls.size(); i++) {
			String currentUrl = pUrls.get(i);
			String currentTitle = pTitles.get(i);
			if (correctUrl(currentUrl)) {
				RDFGenerator rdfStanbol = new RDFGenerator();
				String rdfFormat = rdfStanbol.RDFEntity(currentUrl,
						currentTitle, language);
				// writes the rdf formatted file so it could be also used later.
				try {
					rdfWriter.write(rdfFormat + "\n");
					rdfWriter.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}

				EntityAdder stanbolEntity = new EntityAdder();
				int resultCode;
				try {
					resultCode = stanbolEntity.PutEntity(rdfFormat, entityUrl);
					logger.writeLog(rdfFormat + " with result " + resultCode);
					System.out
							.println(rdfFormat + " with result " + resultCode);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		for (int k = 0; k < cUrls.size(); k++) {
			String currentCategory = cUrls.get(k);
			if (correctUrl(currentCategory)) {
				URLFetcher wikiCats = new URLFetcher(currentCategory,
						validateUrl);
				List<String> wikiPGUrl = wikiCats.getPagesUrls();
				List<String> wikiPGTitles = wikiCats.getPagesTitle();
			}
		}

		// Stop criteria: if all the Urls in the list are fetched, the scheduler
		// stops.
		count++;
		if (count >= allUrls.size()) {
			entitySchedule.cancel();
			entitySchedule.purge();
			try {
				rdfWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			logger.closeLogFile();
		}

	}

	/**
	 * Checks if the URL is valid.
	 * @param recUrl
	 *            String of the URL to be checked.
	 * @return If the URL is a valid page in wikipedia, it returns
	 *         <code>true</code>, otherwise it returns <code>false</code>
	 */
	private static boolean correctUrl(String recUrl) {
		URL url;
		HttpURLConnection conn;
		try {
			url = new URL(recUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			int conResult = conn.getResponseCode();
			if (conResult != 200){
				return false;
			}
			conn.disconnect();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
