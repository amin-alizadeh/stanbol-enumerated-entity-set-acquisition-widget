/*
 * Copyright (c) CONATIX UK LTD. 2012 www.conatix.com
 * Author Amin Alizadeh, amin.alizadeh@conatix.com/amin.alizadeh@gmail.com
 * Contribution of CONATIX UK LTD. to Apache Stanbol.
 */

package com.conatix.EntitySetAdder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Copyright (c) Conatix UK Ltd. www.conatix.com
 * <p>
 * Stanbol Entity Set Acquisition Widget is a contribution of Conatix UK Ltd. to
 * Apache Stanbol for the early adapters program of IKS,
 * http://www.iks-project.eu/ SESAW is specifically built to fetch the pages
 * from wikipedia, get the title and the URL of the respective title and add
 * them to entityhub of the Apache Stanbol. When they're added to the entityhub,
 * the entities can be used as Named Entities. It can be used to add entities
 * for a language for instance. Another use of it is to add a category or a set
 * of categories as well as portals.
 * 
 * @author Amin Alizadeh, amin.alizadeh@conatix.com/amin.alizadeh@gmail.com
 * 
 */

public class EntitySet {
	static String validateUrl;
	static String format = "<http://www.w3.org/2000/01/rdf-schema#label>";
	static String language = "fa";
	static String fileName = "wikis/PersianMeasurement.rdf";
	static String configsFileName = "configs.xml";
	static boolean append = false;
	static String entityUrl = "http://5.9.86.210:8082/entityhub/entity";
	static String logFileName = "entitystanbol";
	static String rdfFileName = "persian-wiki.rdf";
	static int intervalPeriods = 100;
	static int reqPerSec = 10;
	static List<String> allPageUrls = new ArrayList<String>();
	static List<String> allPageTitles = new ArrayList<String>();

	/**
	 * Main thread which starts the program.
	 * 
	 * @param args
	 *            (optional): If command line args are not used, the default
	 *            configs.xml file is used. You can change the the properties in
	 *            configs.xml or create a new XML file under a new name. Note
	 *            that the command line args override the XML or default values.
	 *            <ul>
	 *            <li>-URLs: the filename where the URLs are located in the XML
	 *            format.
	 * 
	 *            <li>-RDFFormat: Default is
	 *            <http://www.w3.org/2000/01/rdf-schema#label>.
	 * 
	 *            <li>-Language: The language for the RDF request. The default
	 *            value is Farsi (Persian).
	 * 
	 *            <li>-RDFFile: The RDF file where entities are stored in RDF
	 *            Standard format. The default filename is persian-wiki.rdf.
	 * 
	 *            <li>-ValidateUrl: When fetching the pages from a list in
	 *            Wikipedia, the addresses are relative. This will add the
	 *            correct part at the beginning of the URL so they are valid
	 *            URLs. The default value is http://fa.wikipedia.org
	 * 
	 *            <li>-EntityUrl: The entity where Apache Stanbol is installed.
	 *            It ends with /entity/entityhub . The default value is
	 *            http://dev.iks-project.eu:8081/entityhub/entity
	 * 
	 *            <li>-LogFile: LogFile is where the logs are stored. It logs in
	 *            the result of the entity fetch and entity add.
	 * 
	 *            <li>-RequestsPerSecond: The number of requests made per second
	 *            to the website from where the fetching and parsing is done.
	 *            This is a politeness factor which limits the number of
	 *            HTTPRequests to a website to avoid artificial traffic in the
	 *            domain.
	 * 
	 *            <li>-RDFAppend: In the case the RDFFile already exists, you
	 *            can append the entities to the file. This allows you to add
	 *            them to the entityhub later.
	 *            </ul>
	 * @see {@link #ConfigureVariables(String, String[])}
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		ConfigureVariables(configsFileName, args);

		LogWriter logger = new LogWriter(logFileName);
		logger.openLogFile();

		FileWriter rdfFile = new FileWriter(rdfFileName, true);
		BufferedWriter rdfWriter = new BufferedWriter(rdfFile);
		rdfWriter
				.write("#Persian pages in WikiPedia added by Amin Alizadeh, Conatix.\n");

		URLFeeder urlList = new URLFeeder();

		List<String> allUrlsFromFile = new ArrayList<String>();
		Timer entityScheduler = new Timer();

		allUrlsFromFile = urlList.UrlList(fileName);

		entityScheduler.schedule(new EntitySetScheduled(entityScheduler,
				allUrlsFromFile, validateUrl, language, rdfWriter, logger,
				entityUrl), 0, intervalPeriods);

	}

	/**
	 * 
	 * @param configsFile
	 *            the xml file where the configurations are stored. The
	 *            configuration variables are static variables within the Main
	 *            class.
	 * @param args
	 *            The String array of command line arguments. They overwrite the
	 *            configs.xml attributes.
	 */
	@SuppressWarnings("static-access")
	private static void ConfigureVariables(String configsFile, String[] args) {

		SetConfigurations configure = new SetConfigurations(configsFile);

		if (args.length > 0) {

			for (int i = 0; i < args.length; i++) {
				if ("-URLs".equals(args[i])) {
					configure.setFileName(args[i + 1]);
					i++;
				} else if ("-RDFFormat".equals(args[i])) {
					configure.setFormat(args[i + 1]);
					i++;
				} else if ("-Language".equals(args[i])) {
					configure.setLanguage(args[i + 1]);
					i++;
				} else if ("-RDFFile".equals(args[i])) {
					configure.setRdfFileName(args[i + 1]);
					i++;
				} else if ("-ValidateUrl".equals(args[i])) {
					configure.setValidateUrl(args[i + 1]);
					i++;
				} else if ("-EntityUrl".equals(args[i])) {
					configure.setEntityUrl(args[i + 1]);
					i++;
				} else if ("-LogFile".equals(args[i])) {
					configure.setLogFileName(args[i + 1]);
					i++;
				} else if ("-RequestsPerSecond".equals(args[i])) {
					configure.setIntervalPeriods(1000 / Integer
							.parseInt(args[i + 1]));
					i++;
				} else if ("-RDFAppend".equals(args[i])) {
					configure.setAppend(Boolean.parseBoolean(args[i + 1]));
					i++;
				}
			}

		}

		fileName = configure.getFileName();
		language = configure.getLanguage();
		rdfFileName = configure.getRdfFileName();
		entityUrl = configure.getEntityUrl();
		logFileName = configure.getLogFileName();
		intervalPeriods = configure.getIntervalPeriods();
		format = configure.getFormat();
		validateUrl = configure.getValidateUrl();
		append = configure.isAppend();
		intervalPeriods = configure.getIntervalPeriods();

	}
}
