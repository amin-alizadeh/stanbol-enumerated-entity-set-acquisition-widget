package com.conatix.EntitySetAdder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLFetcher {
	private static List<String> pagesUrls = new ArrayList<String>();
	private static List<String> pagesTitles = new ArrayList<String>();

	private static List<String> categoriesUrls = new ArrayList<String>();
	private static List<String> categoriesTitles = new ArrayList<String>();

	private static String feedUrl;

	private static String siteContent;
	private static String validateUrl;

	@SuppressWarnings("static-access")
	public URLFetcher(String feedUrl, String validateUrl) {
		this.feedUrl = feedUrl;
		this.validateUrl = validateUrl;
		this.siteContent = getUrlContent(feedUrl);

		getPagesTitlesAndUrls();
		getCategoriesTitlesAndUrls();

	}

	private void getCategoriesTitlesAndUrls() {
		String subCategoriesContent = getMWsubCategories();

		if (subCategoriesContent != null) {
			ListOfTitlesUrls subCats = new ListOfTitlesUrls();
			subCats = getTitlesAndUrls(subCategoriesContent);
			List<String> validPagesURLs = makeValidURLs(subCats.allUrls,
					validateUrl);
			categoriesTitles.addAll(validPagesURLs);
			categoriesUrls.addAll(subCats.allUrls);
		}
	}

	@SuppressWarnings("deprecation")
	private static List<String> makeValidURLs(List<String> allUrls,
			String webUrl) {
		List<String> validURLs = new ArrayList<String>();
		for (int i = 0; i < allUrls.size(); i++) {
			String url = allUrls.get(i);
			String validUrl = "";
			if (isValidURL(url)) {
				validUrl = url;
			} else {
				validUrl = webUrl + url;
			}
			validUrl = URLDecoder.decode(validUrl);
			validURLs.add(validUrl);
		}
		return validURLs;
	}

	private static boolean isValidURL(String strUrl) {
		boolean isValid = false;
		try {
			URL validURL = new URL(strUrl);
			isValid = true;
		} catch (MalformedURLException e) {
			isValid = false;
		}

		return isValid;
	}

	private static void getPagesTitlesAndUrls() {
		String subPagesContent = getMWSubPages(siteContent);

		if (subPagesContent != null) {
			ListOfTitlesUrls pgs = new ListOfTitlesUrls();
			pgs = getTitlesAndUrls(subPagesContent);
			List<String> validPagesUrls = makeValidURLs(pgs.allUrls,
					validateUrl);
			pagesTitles.addAll(pgs.allTitles);
			pagesUrls.addAll(validPagesUrls);
		}

		ListOfTitlesUrls listUrls = new ListOfTitlesUrls();
		listUrls = getListUrls(siteContent);
		List<String> validListUrls = makeValidURLs(listUrls.allUrls,
				validateUrl);
		pagesTitles.addAll(listUrls.allTitles);
		pagesUrls.addAll(validListUrls);
	}

	private static ListOfTitlesUrls getListUrls(String siteContent) {
		// <li><span class="flagicon"><img alt="flag of azerbaijan.svg"
		// src="//upload.wikimedia.org/wikipedia/commons/thumb/d/dd/flag_of_azerbaijan.svg/22px-flag_of_azerbaijan.svg.png"
		// width="22" height="11" class="thumbborder"
		// srcset="//upload.wikimedia.org/wikipedia/commons/thumb/d/dd/flag_of_azerbaijan.svg/33px-flag_of_azerbaijan.svg.png 1.5x, //upload.wikimedia.org/wikipedia/commons/thumb/d/dd/flag_of_azerbaijan.svg/44px-flag_of_azerbaijan.svg.png 2x"
		// />&#160;</span><a
		// href="/wiki/%d8%ac%d9%85%d9%87%d9%88%d8%b1%db%8c_%d8%a2%d8%b0%d8%b1%d8%a8%d8%a7%db%8c%d8%ac%d8%a7%d9%86"
		// title="جمهوری آذربایجان">جمهوری آذربایجان</a></li>

		List<String> allTitles = new ArrayList<String>();
		List<String> allUrls = new ArrayList<String>();
		ListOfTitlesUrls subTitlesNUrls = new ListOfTitlesUrls();

		Pattern pLi = Pattern
				.compile("<li>.*<a\\s.*href=\".*\".*>.*</a>.*</li>");
		Matcher mLi = pLi.matcher(siteContent);
		String listContent = "";
		String hrefURL, title;
		Pattern pTU = Pattern.compile("<a\\s.*href=\".*\".*</a>");
		while (mLi.find()) {
			listContent = mLi.group();
			Matcher matchTU = pTU.matcher(listContent);
			String tag = "";
			while (matchTU.find())
				tag = matchTU.group();

			hrefURL = getHrefURL(tag);
			title = getTitle(tag);
			allUrls.add(hrefURL);
			allTitles.add(title);
		}
		subTitlesNUrls.allTitles.addAll(allTitles);
		subTitlesNUrls.allUrls.addAll(allUrls);

		return subTitlesNUrls;
	}

	private static ListOfTitlesUrls getTitlesAndUrls(String subTitlesContent) {

		List<String> allTitles = new ArrayList<String>();
		List<String> allUrls = new ArrayList<String>();
		ListOfTitlesUrls subTitlesNUrls = new ListOfTitlesUrls();

		Pattern pCategories = Pattern.compile("<a\\s.*href=\".*\".*</a>");
		Matcher mCategories = pCategories.matcher(subTitlesContent);
		String tag = "";
		String hrefURL, title;

		while (mCategories.find()) {
			tag = mCategories.group();
			hrefURL = getHrefURL(tag);
			title = getTitle(tag);
			allUrls.add(hrefURL);
			allTitles.add(title);
		}
		
		subTitlesNUrls.allTitles.addAll(allTitles);
		subTitlesNUrls.allUrls.addAll(allUrls);

		return subTitlesNUrls;
	}

	private static String getHrefURL(String tag) {
		String hrefURL = "";
		String[] tagSplits1 = tag.split("\\shref=\"");
		hrefURL = tagSplits1[1].split("\".*>")[0];
		return hrefURL;
	}

	private static String getTitle(String tag) {
		String title = "";
		tag = tag.replaceAll("<a\\s.*href=\".*\"\\w*>", "");
		title = tag.replaceAll("</a>", "");
		return title;
	}

	private static String getMWsubCategories() {
		if (siteContent.contains("<div id=\"mw-subcategories\">")) {
			String[] contentSplits = siteContent
					.split("<div id=\"mw-subcategories\">");
			String mwPageAndAll = contentSplits[1];
			String[] divSplits = mwPageAndAll
					.split("</div><div id=\"mw-pages\">");

			return divSplits[0];
		} else {
			return null;
		}
	}

	private static String getMWSubPages(String siteContent) {
		if (siteContent.contains("<div id=\"mw-pages\">")) {
			String[] contentSplits = siteContent.split("<div id=\"mw-pages\">");
			String mwPageAndAll = contentSplits[1];
			String[] divSplits = mwPageAndAll
					.split("</div>.*<!-- /bodycontent -->");

			return divSplits[0];
		} else {
			return null;
		}
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

	public List<String> getPagesTitle() {
		return pagesTitles;
	}

	public List<String> getPagesUrls() {
		return pagesUrls;
	}

	public List<String> getCategoriesTitle() {
		return categoriesTitles;
	}

	public List<String> getCategoriesUrls() {
		return categoriesUrls;
	}

}
