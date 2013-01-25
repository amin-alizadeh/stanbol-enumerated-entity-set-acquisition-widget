package com.conatix.EntitySetAdder;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Reads the XML formatted file of feed URLs and returns a List.
 */
public class URLFeeder {
	/**
	 * Constructor of the object. Reads the XML formatted file of feed URLs and
	 * returns a List.
	 */
	public URLFeeder() {
		super();
	}

	/**
	 * Reads the XML formatted file of feed URLs and returns a List.
	 * @param filename	The XML file where the URLs are stored.
	 * @return	The List of URLs.
	 */
	public List<String> UrlList(String filename) {
		List<String> urls = new ArrayList<String>();

		Document dom = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(filename);
			Element docEle = dom.getDocumentElement();
			Element fieldsElements = (Element) docEle.getElementsByTagName(
					"Urls").item(0);
			NodeList fieldsNode = fieldsElements.getElementsByTagName("Url");
			for (int i = 0; i < fieldsNode.getLength(); i++) {
				urls.add(fieldsNode.item(i).getFirstChild().getTextContent()
						.trim());
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return urls;
	}

}
