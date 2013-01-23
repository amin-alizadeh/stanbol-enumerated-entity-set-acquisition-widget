package com.conatix.EntitySetAdder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

public class EntityAdder {

	public EntityAdder() {
		super();
	}
	
	public int PutEntity (String rdfRequest, String entityUrl) throws IOException{
		return PutEntity(rdfRequest, entityUrl, "text/rdf+n3");
	}
	
	public int PutEntity (String rdfRequest, String entityUrl, String contentType) throws IOException{
		
//		entityUrl = "http://5.9.86.210:8082/entityhub/entity";
		String type = "PUT";
//		rdfRequest = "<http://fa.wikipedia.org/wiki/نظریه_بازی‌ها> <http://www.w3.org/2000/01/rdf-schema#label> \"نظریه بازی‌ها\"@fa .";
		
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
