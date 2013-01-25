package com.conatix.EntitySetAdder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

/**
 * Creates an object to add a Named Entity to the entityhub of Apache Stanbol.
 */
public class EntityAdder {
	/**
	 * Constructor of the object.
	 */
	public EntityAdder() {
		super();
	}

	/**
	 * Adds the entity which is in RDF format to the entityhub by sending a PUT
	 * request with the content type <code>contentType</code>. The default value
	 * for <code>contentType</code> is <i>text/rdf+n3</i>.
	 * 
	 * @param rdfRequest
	 *            the RDF format of the entity to be added. It contains the URL
	 *            to the entity, the styling, the title, and the language of the
	 *            entity.
	 * @param entityUrl
	 *            The urls where entityhub is located.
	 * @return The integer value for the request value is returned.
	 * @throws IOException
	 */

	public int PutEntity(String rdfRequest, String entityUrl)
			throws IOException {
		return PutEntity(rdfRequest, entityUrl, "text/rdf+n3");
	}

	/**
	 * Adds the entity which is in RDF format to the entityhub by sending a PUT
	 * request with the content type <code>contentType</code>. The default value
	 * for <code>contentType</code> is <i>text/rdf+n3</i>.
	 * 
	 * @param rdfRequest
	 *            the RDF format of the entity to be added. It contains the URL
	 *            to the entity, the styling, the title, and the language of the
	 *            entity.
	 * @param entityUrl
	 *            The urls where entityhub is located.
	 * @param contentType
	 *            The type of the connection stream's content. The default value
	 *            for <code>contentType</code> is <i>text/rdf+n3</i>.
	 * @return The integer value for the request value is returned.
	 * @throws IOException
	 */
	public int PutEntity(String rdfRequest, String entityUrl, String contentType)
			throws IOException {

		String type = "PUT";
		URL reqUrl = new URL(entityUrl);
		HttpURLConnection conn = (HttpURLConnection) reqUrl.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod(type);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setConnectTimeout(60000); // 60 secs
		conn.setReadTimeout(60000); // 60 secs
		conn.setRequestProperty("Content-Type", contentType);
		conn.setRequestProperty("Accept-Encoding", contentType);

		OutputStream out = conn.getOutputStream();
		out.write(rdfRequest.getBytes(Charset.forName("UTF-8")));
		IOUtils.write(rdfRequest, out, "UTF-8");

		int code = conn.getResponseCode();
		IOUtils.close(conn);

		return code;
	}

}
