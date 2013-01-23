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
 * 
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
 * 
 */
public class EntitySet {
	static String validateUrl;
	static String url = "http://fa.wikipedia.org/wiki/%D8%B1%D8%AF%D9%87:%D9%85%D9%87%D9%86%D8%AF%D8%B3%DB%8C";
	static String format = "<http://www.w3.org/2000/01/rdf-schema#label>";
	static String language = "fa";
	static String fileName = "wikis/PersianMeasurement.rdf";
	static String configsFileName = "configs.xml";
	static boolean append = false;
	static List<String> allPageUrls = new ArrayList<String>();
	static List<String> allPageTitles = new ArrayList<String>();
	static String entityUrl = "http://5.9.86.210:8082/entityhub/entity";
	static String logFileName = "entitystanbol";
	static String rdfFileName = "persian-wiki.rdf";
	static int intervalPeriods = 100;

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

		allUrlsFromFile = urlList.UrlList("persianwikipedia.txt");
		for (int j = 0; j < allUrlsFromFile.size(); j++) {
			String fUrl = allUrlsFromFile.get(j);
			entityScheduler
					.schedule(new EntitySetScheduled(fUrl, validateUrl,
							language, rdfWriter, logger, entityUrl), 0,
							intervalPeriods);
		}

		URLFetcher wikiUrls = new URLFetcher(url, validateUrl);
		List<String> pUrls = wikiUrls.getPagesUrls();
		List<String> pTitles = wikiUrls.getPagesTitle();
		List<String> cUrls = wikiUrls.getCategoriesUrls();
		List<String> cTitles = wikiUrls.getCategoriesTitle();

		// for (int i = 0; i<pUrls.size(); i++){
		// String currentUrl = pUrls.get(i);
		// if (correctUrl(currentUrl)){
		// allPageUrls.add(currentUrl);
		// allPageTitles.add(pTitles.get(i));
		// }
		// }

		// rdfWriter.close();
		// logger.closeLogFile();
	}

	@SuppressWarnings("static-access")
	private static void ConfigureVariables(String configsFile, String[] args) {
		
		SetConfigurations configure = new SetConfigurations(configsFile);;
		
		if (args.length > 0) {

			/*
			 * this.fileName = docEle.getElementsByTagName("URLS").item(0)
			 * .getFirstChild().getTextContent(); this.language =
			 * docEle.getElementsByTagName("Language").item(0)
			 * .getFirstChild().getTextContent(); this.rdfFileName =
			 * docEle.getElementsByTagName("RDFFile").item(0)
			 * .getFirstChild().getTextContent(); this.entityUrl =
			 * docEle.getElementsByTagName("EntityUrl").item(0)
			 * .getFirstChild().getTextContent(); this.logFileName =
			 * docEle.getElementsByTagName("LogFile").item(0)
			 * .getFirstChild().getTextContent(); this.reqPerSec =
			 * Integer.parseInt(docEle
			 * .getElementsByTagName("RequestsPerSecond").item(0)
			 * .getFirstChild().getTextContent()); this.format =
			 * docEle.getElementsByTagName("RDFFormat").item(0)
			 * .getFirstChild().getTextContent(); this.validateUrl =
			 * docEle.getElementsByTagName("ValidateURL").item(0)
			 * .getFirstChild().getTextContent(); this.append =
			 * Boolean.parseBoolean(docEle.getElementsByTagName("RDFAppend").item(0)
			 * .getFirstChild().getTextContent());
			 */

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
					configure.setIntervalPeriods(1000 / Integer.parseInt(args[i + 1]));
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

	private static String getUrlContent(String recUrl) {

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
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
