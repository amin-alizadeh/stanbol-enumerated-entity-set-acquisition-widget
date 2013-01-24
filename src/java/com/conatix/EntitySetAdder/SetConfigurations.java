package com.conatix.EntitySetAdder;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class SetConfigurations {

	private static String validateUrl = "http://fa.wikipedia.org";
	private static String format = "<http://www.w3.org/2000/01/rdf-schema#label>";
	private static String language = "fa";
	private static String fileName = "persianwikipedia.xml";
	private static boolean append = false;
	private static String entityUrl = "http://dev.iks-project.eu:8081/entityhub/entity";
	private static String logFileName = "entitystanbol";
	private static String rdfFileName = "persian-wiki.rdf";
	private static int intervalPeriods = 100;
	private static int reqPerSec = 10;
	private static String configFileName = "configs.xml";
	private static boolean defaultConfigs = true;

	/**
	 * Creates an object which holds all the configurations. If configFileName
	 * is NOT null, it reads the properties and holds them. If configFileName is
	 * null, just sets the values to their default values. Setters and Getters
	 * can be accessed to change or get the values of the configurations.
	 * 
	 * @param configFileName
	 *            is the XML file name where the configurations are set there.
	 *            Note that the command line args overwrite the values over the
	 *            configurations of the XML file if any of them is appearing in
	 *            the command line args.
	 * 
	 * @param fileName
	 *            The file in XML format
	 * @param validateUrl
	 *            When fetching the pages from a list in Wikipedia, the
	 *            addresses are relative. This will add the correct part at the
	 *            beginning of the URL so they are valid URLs. The default value
	 *            is http://fa.wikipedia.org
	 * 
	 * @param URLs
	 *            the filename where the URLs are located in the XML format.
	 * 
	 * @param RDFFormat
	 *            Default is <http://www.w3.org/2000/01/rdf-schema#label>. Note
	 *            that in the XML file remove "<" and ">" as they are illegal in
	 *            the XML formatting.
	 * 
	 * @param Language
	 *            The language for the RDF request. The default value is Farsi
	 *            (Persian).
	 * 
	 * @param RDFFile
	 *            The RDF file where entities are stored in RDF Standard format.
	 *            The default filename is persian-wiki.rdf.
	 * 
	 * @param EntityUrl
	 *            The entity where Apache Stanbol is installed. It ends with
	 *            /entity/entityhub . The default value is
	 * 
	 * @param LogFile
	 *            LogFile is where the logs are stored. It logs in the result of
	 *            the entity fetch and entity add.
	 * 
	 * @param RequestsPerSecond
	 *            The number of requests made per second to the website from
	 *            where the fetching and parsing is done. This is a politeness
	 *            factor which limits the number of HTTPRequests to a website to
	 *            avoid artificial traffic in the domain.
	 * 
	 * @param RDFAppend
	 *            In the case the RDFFile already exists, you can append the
	 *            entities to the file. This allows you to add them to the
	 *            entityhub later.
	 * 
	 */
	@SuppressWarnings("static-access")
	public SetConfigurations(String configFileName) {
		super();
		if (configFileName != null) {
			this.configFileName = configFileName;
			this.defaultConfigs = false;

			Document dom = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				dom = db.parse("configs.xml");
				Element docEle = dom.getDocumentElement();
				this.fileName = docEle.getElementsByTagName("URLS").item(0)
						.getFirstChild().getTextContent();
				this.language = docEle.getElementsByTagName("Language").item(0)
						.getFirstChild().getTextContent();
				this.rdfFileName = docEle.getElementsByTagName("RDFFile")
						.item(0).getFirstChild().getTextContent();
				this.entityUrl = docEle.getElementsByTagName("EntityUrl")
						.item(0).getFirstChild().getTextContent();
				this.logFileName = docEle.getElementsByTagName("LogFile")
						.item(0).getFirstChild().getTextContent();
				this.reqPerSec = Integer.parseInt(docEle
						.getElementsByTagName("RequestsPerSecond").item(0)
						.getFirstChild().getTextContent());
				this.format = "<" + docEle.getElementsByTagName("RDFFormat").item(0)
						.getFirstChild().getTextContent() + ">";
				this.validateUrl = docEle.getElementsByTagName("ValidateURL")
						.item(0).getFirstChild().getTextContent();
				this.append = Boolean.parseBoolean(docEle
						.getElementsByTagName("RDFAppend").item(0)
						.getFirstChild().getTextContent());

				this.intervalPeriods = 1000 / this.reqPerSec;

			} catch (ParserConfigurationException e) {
				defaultConfigs = true;
				e.printStackTrace();
			} catch (SAXException e) {
				defaultConfigs = true;
				e.printStackTrace();
			} catch (IOException e) {
				defaultConfigs = true;
				e.printStackTrace();
			}
		}
	}

	public static String getValidateUrl() {
		return validateUrl;
	}

	public static String getFormat() {
		return format;
	}

	public static String getLanguage() {
		return language;
	}

	public static String getFileName() {
		return fileName;
	}

	public static boolean isAppend() {
		return append;
	}

	public static String getEntityUrl() {
		return entityUrl;
	}

	public static String getLogFileName() {
		return logFileName;
	}

	public static String getRdfFileName() {
		return rdfFileName;
	}

	public static int getIntervalPeriods() {
		return intervalPeriods;
	}

	public static boolean isDefaultConfigs() {
		return defaultConfigs;
	}

	public static void setValidateUrl(String validateUrl) {
		SetConfigurations.validateUrl = validateUrl;
	}

	public static void setFormat(String format) {
		SetConfigurations.format = format;
	}

	public static void setLanguage(String language) {
		SetConfigurations.language = language;
	}

	public static void setFileName(String fileName) {
		SetConfigurations.fileName = fileName;
	}

	public static void setAppend(boolean append) {
		SetConfigurations.append = append;
	}

	public static void setEntityUrl(String entityUrl) {
		SetConfigurations.entityUrl = entityUrl;
	}

	public static void setLogFileName(String logFileName) {
		SetConfigurations.logFileName = logFileName;
	}

	public static void setRdfFileName(String rdfFileName) {
		SetConfigurations.rdfFileName = rdfFileName;
	}

	public static void setIntervalPeriods(int intervalPeriods) {
		SetConfigurations.intervalPeriods = intervalPeriods;
	}

	public static void setDefaultConfigs(boolean defaultConfigs) {
		SetConfigurations.defaultConfigs = defaultConfigs;
	}

}
