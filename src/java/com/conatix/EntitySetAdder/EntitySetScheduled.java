package com.conatix.EntitySetAdder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.TimerTask;

public class EntitySetScheduled extends TimerTask {

	private static String url;
	private static String validateUrl;
	private static String language;
	private static BufferedWriter rdfWriter;
	private static LogWriter logger;
	private static String entityUrl;

	public EntitySetScheduled(String url, String validateUrl, String language, BufferedWriter rdfWriter, LogWriter logger, String entityUrl) {
		this.url = url;
		this.validateUrl = validateUrl;
		this.language = language;
		this.rdfWriter = rdfWriter;
		this.logger = logger;
		this.entityUrl = entityUrl;
	}

	@Override
	public void run() {

		URLFetcher wikiUrls = new URLFetcher(url, validateUrl);
		List<String> pUrls = wikiUrls.getPagesUrls();
		List<String> pTitles = wikiUrls.getPagesTitle();
		List<String> cUrls = wikiUrls.getCategoriesUrls();
		List<String> cTitles = wikiUrls.getCategoriesTitle();

		for (int i = 0; i < pUrls.size(); i++) {
			String currentUrl = pUrls.get(i);
			String currentTitle = pTitles.get(i);
			if (correctUrl(currentUrl)) {
//				allPageUrls.add(currentUrl);
//				allPageTitles.add(currentTitle);
				RDFGenerator rdfStanbol = new RDFGenerator();
				String rdfFormat = rdfStanbol.RDFEntity(currentUrl,
						currentTitle, language);
				// writes the rdf formatted file so it could be also used later.
				try {
					rdfWriter.write(rdfFormat + "\n");
					rdfWriter.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				EntityAdder stanbolEntity = new EntityAdder();
				int resultCode;
				try {
					resultCode = stanbolEntity.PutEntity(rdfFormat, entityUrl);
					logger.writeLog(rdfFormat + " with result " + resultCode);
					System.out.println(rdfFormat + " with result " + resultCode);

				} catch (IOException e) {
					// TODO Auto-generated catch block
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

	}

	public static boolean correctUrl(String recUrl){
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		try {
			url = new URL(recUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line.toLowerCase() + "\n";
				if (line.contains("<title>Wikimedia Error</title>")){
					return false;
				}
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
