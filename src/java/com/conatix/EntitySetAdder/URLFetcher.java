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

/**
 * The object to fetch URLs from Wikipedia and lists the pages mentioned in
 * those URLs as well as categories. It identifies them and returns the Lists of
 * Titles and URLs of pages and categories.
 */
public class URLFetcher {
	private static List<String> pagesUrls = new ArrayList<String>();
	private static List<String> pagesTitles = new ArrayList<String>();

	private static List<String> categoriesUrls = new ArrayList<String>();
	private static List<String> categoriesTitles = new ArrayList<String>();

	private static String feedUrl;

	private static String siteContent;
	private static String validateUrl;

	/**
	 * The constructor of the object. The object fetches URLs from Wikipedia and
	 * lists the pages mentioned in those URLs as well as categories. It
	 * identifies them and returns the Lists of Titles and URLs of pages and
	 * categories.
	 * 
	 * @param feedUrl
	 *            The URL of the page where the list of other pages and
	 *            categories/subcategories is listed.
	 * @param validateUrl
	 *            When fetching the pages from a list in Wikipedia, the
	 *            addresses are relative. This will add the correct part at the
	 *            beginning of the URL so they are valid URLs.
	 */
	public URLFetcher(String feedUrl, String validateUrl) {
		this.feedUrl = feedUrl;
		this.validateUrl = validateUrl;
		this.siteContent = getUrlContent(feedUrl);

		getPagesTitlesAndUrls();
		getCategoriesTitlesAndUrls();

	}

	/**
	 * Only retrieves the part where the subcategories in a wikipedia page are
	 * listed and based on that content fetches and separates the Titles and
	 * URLs and stores them in the static variables common within the whole
	 * class.
	 * 
	 * @see {@link #getMWsubCategories()}, {@link #getTitlesAndUrls(String)},
	 *      {@link #makeValidURLs(List, String)}
	 */
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

	/**
	 * When fetching the pages from a list in Wikipedia, the addresses are
	 * relative. This will add the correct part at the beginning of the URL so
	 * they are valid URLs. It also checks whether the URLs are actually
	 * existing or not.
	 * 
	 * @param allUrls
	 *            The list of URLs to be validated.
	 * @param webUrl
	 *            Makes the URLs valid. Since the URLs in Wikipedia pages are
	 *            relative, it makes all of them absolute by adding this part at
	 *            the beginning of each relative URL.
	 * @return List of validated URLs.
	 * @see {@link #isValidURL(String)}
	 */
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

	/**
	 * Checks if the construction of a URL is correct.
	 * 
	 * @param strUrl
	 *            The URL to be checked.
	 * @return If the URL is valid it returns <i>true</i>, otherwise if the URL
	 *         is malformed it returns <i>false</i>.
	 */
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

	/**
	 * Only retrieves the part where the subpages in a wikipedia page are listed
	 * and based on that content fetches and separates the Titles and URLs and
	 * stores them in the static variables common within the whole class.
	 * 
	 * @see {@link #getMWSubPages(String)}, {@link #getTitlesAndUrls(String)},
	 *      {@link #makeValidURLs(List, String)}
	 */
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

	/**
	 * Gets the Titles and URLs in a wikipedia page by analyzing the site
	 * content using regular expressions.
	 * 
	 * @param siteContent
	 *            The content of the site or a part of the site containing the
	 *            titles and URLs. siteContent is the content of the wikipedia
	 *            page where subpages are located.
	 * @return List of Titles and URLs in <i>ListOfTitlesUrls</i>.
	 * @see {@link #getTitle(String)}, {@link #getHrefURL(String)}
	 */
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

	/**
	 * Gets the Titles and URLs in a wikipedia page by analyzing the site
	 * content using regular expressions.
	 * 
	 * @param siteContent
	 *            The content of the site or a part of the site containing the
	 *            titles and URLs. siteContent is the content of the wikipedia
	 *            page where subcategories are located.
	 * @return List of Titles and URLs in <i>ListOfTitlesUrls</i>.
	 * @see {@link #getTitle(String)}, {@link #getHrefURL(String)}
	 */
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

	/**
	 * Retrieves the URL of the page from the HTML tag of the wikipedia page.
	 * 
	 * @param tag
	 *            The HTML tag retrieved from the content of the wikipedia page.
	 * @return the URL to the entity.
	 */
	private static String getHrefURL(String tag) {
		String hrefURL = "";
		String[] tagSplits1 = tag.split("\\shref=\"");
		hrefURL = tagSplits1[1].split("\".*>")[0];
		return hrefURL;
	}

	/**
	 * Retrieves the Title of the page from the HTML tag of the wikipedia page.
	 * 
	 * @param tag
	 *            The HTML tag retrieved from the content of the wikipedia page.
	 * @return the Title of the entity.
	 */
	private static String getTitle(String tag) {
		String title = "";
		tag = tag.replaceAll("<a\\s.*href=\".*\"\\w*>", "");
		title = tag.replaceAll("</a>", "");
		return title;
	}

	/**
	 * Retrieves the part of the content of a wikipedia page containing
	 * subcategories.
	 * 
	 * @return The section of the page where the subcategories are.
	 */
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

	/**
	 * Retrieves the part of the content of a wikipedia page containing
	 * subpages.
	 * 
	 * @return The section of the page where the subpages are.
	 */
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

	/**
	 * Reads the entire page given its URL.
	 * 
	 * @param recUrl
	 *            the URL of the page to be fetched.
	 * @return The content of the page.
	 */
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

	/**
	 * @return the List of the titles of the pages.
	 */
	public List<String> getPagesTitle() {
		return pagesTitles;
	}

	/**
	 * @return the List of the URLs of the pages.
	 */
	public List<String> getPagesUrls() {
		return pagesUrls;
	}

	/**
	 * @return the List of the titles of the categories.
	 */
	public List<String> getCategoriesTitle() {
		return categoriesTitles;
	}

	/**
	 * @return the List of the URLs of the categories.
	 */
	public List<String> getCategoriesUrls() {
		return categoriesUrls;
	}

}
