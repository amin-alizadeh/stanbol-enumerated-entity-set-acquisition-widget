package com.conatix.EntitySetAdder;

import java.util.List;

/**
 * This class gets the url, label (title), language, and the format and returns
 * a standard RDF format.
 */
public class RDFGenerator {
	/**
	 * Constructor of the class to generate the standard RDF format.
	 */
	public RDFGenerator() {
		super();
	}
	
	/**
	 * Generates a single RDF entity and returns as a String.
	 * 
	 * @param url
	 *            the URL to the entity.
	 * @param label
	 *            The <i>Title</i> of the entity.
	 * @param language
	 *            The language of the entity.
	 * @return The RDF formatted String.
	 */

	public String RDFEntity(String url, String label, String language) {
		return RDFEntity(url, label, language,
				"<http://www.w3.org/2000/01/rdf-schema#label>");
	}

	/**
	 * Generates a single RDF entity and returns as a String.
	 * 
	 * @param url
	 *            the URL to the entity.
	 * @param label
	 *            The <i>Title</i> of the entity.
	 * @param language
	 *            The language of the entity.
	 * @param format
	 *            The format of the entity. The default value is <http://www.w3.org/2000/01/rdf-schema#label>.
	 * @return The RDF formatted String.
	 */
	public String RDFEntity(String url, String label, String language,
			String format) {
		String rdfFormatEntity = "";
		rdfFormatEntity = "<" + url + "> " + format + " \"" + label + "\"@"
				+ language + " .";
		return rdfFormatEntity;
	}

	/**
	 * Gets the URL, Title, Language, and Format of a list of entities and generates the RDF formatted String. 
	 * @param urls	The List of URLs to the entities.
	 * @param labels The List of Labels to the entities.
	 * @param language	The Language of the entities.
	 * @return	String of RDF Formatted entities. 
	 */
	public String RDFListEntities(List<String> urls, List<String> labels,
			String language) {
		return RDFListEntities(urls, labels, language,
				"<http://www.w3.org/2000/01/rdf-schema#label>");
	}

	/**
	 * Gets the URL, Title, Language, and Format of a list of entities and generates the RDF formatted String. 
	 * @param urls	The List of URLs to the entities.
	 * @param labels The List of Labels to the entities.
	 * @param language	The Language of the entities.
	 * @param format	The Format of the entities. The default value is <http://www.w3.org/2000/01/rdf-schema#label>
	 * @return	String of RDF Formatted entities. 
	 */
	public String RDFListEntities(List<String> urls, List<String> labels,
			String language, String format) {
		String rdfList = "";

		if (urls.size() == labels.size()) {
			for (int i = 0; i < urls.size(); i++) {
				rdfList += RDFEntity(urls.get(i), labels.get(i), language,
						format);
			}
		}

		return rdfList;
	}

}
