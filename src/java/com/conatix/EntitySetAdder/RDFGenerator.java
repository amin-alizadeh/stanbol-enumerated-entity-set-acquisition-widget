package com.conatix.EntitySetAdder;

import java.util.List;

public class RDFGenerator {

	public RDFGenerator() {
		super();
	}

	public String RDFEntity (String url, String label, String language){
		return RDFEntity (url, label, language, "<http://www.w3.org/2000/01/rdf-schema#label>");
	}
	
	public String RDFEntity (String url, String label, String language, String format){
		String rdfFormatEntity = "";
		rdfFormatEntity = "<" + url + "> " + format + " \"" + label + "\"@" + language + " .";
		return rdfFormatEntity;
	}

	public String RDFListEntities (List<String> urls, List<String> labels, String language){
		return RDFListEntities (urls, labels, language, "<http://www.w3.org/2000/01/rdf-schema#label>");
	}
	
	public String RDFListEntities (List<String> urls, List<String> labels, String language, String format){
		String rdfList = "";
		
		if (urls.size() == labels.size()){
			for (int i = 0; i < urls.size(); i++){
				rdfList += RDFEntity(urls.get(i), labels.get(i), language, format); 
			}
		}
		
		return rdfList;
	}
	
}
