package com.conatix.EntitySetAdder;

import java.util.ArrayList;
import java.util.List;

/**
 * The object consist of two lists to keep the URLs and Titles together.
 */
public class ListOfTitlesUrls {
	public List<String> allTitles = new ArrayList<String>();
	public List<String> allUrls = new ArrayList<String>();

	/**
	 * @return	the size of the Object List
	 */
	public int size() {
		return allTitles.size();
	}

	/**
	 * Creates a String of Titles and URLs and returns it.
	 */
	public String toString() {
		String titlesNUrls = "";
		for (int i = 0; i < size(); i++) {
			titlesNUrls += "Title: " + allTitles.get(i) + "\nURL: "
					+ allUrls.get(i) + "\n";
		}
		return titlesNUrls;
	}
}
