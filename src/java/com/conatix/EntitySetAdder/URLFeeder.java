package com.conatix.EntitySetAdder;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class URLFeeder {

	public URLFeeder() {
		super();
	}

	@SuppressWarnings("deprecation")
	public List<String> UrlList(String filename){
		List<String> urls = new ArrayList<String>();
		
		try {
			FileInputStream fStream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fStream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = br.readLine()) != null){
				urls.add(URLDecoder.decode(line));
			}
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return urls;
	}

}
