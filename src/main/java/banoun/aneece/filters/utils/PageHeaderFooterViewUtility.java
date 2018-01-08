package banoun.aneece.filters.utils;

import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class PageHeaderFooterViewUtility {

	public void addPageHeaderAndFooter(ServletRequest request, ServletResponse response, byte[] bytes)
			throws IOException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		Map<String, String> colours = getColours();
		String page = new String(bytes);
		page = addPageHeader(page, httpRequest, bytes, colours);
		page = addPageFooter(page, colours);
		themeResponse(page, colours, response, httpRequest);
	}

	private void themeResponse(String page, Map<String, String> colours, ServletResponse response,
			HttpServletRequest httpRequest) throws IOException {
		Boolean theme = (Boolean) httpRequest.getSession().getAttribute("theme");
		if (theme == null) {
			theme = true;
			httpRequest.getSession().setAttribute("theme", theme);
		}
		if (theme) {
			colours.put("page", page);
			colours.keySet().forEach(key -> {
				if (!key.equals("page")) {
					colours.put("page", colours.get("page").replace(key, colours.get(key)));
				}
			});
			response.getOutputStream().write(colours.get("page").getBytes());
		} else {
			response.getOutputStream().write(page.getBytes());
		}
	}

	private String addPageFooter(String page, Map<String, String> colours) {
		String pageFooter = getPageFooter();
		String bodyClosingTag = "</body>";
		if (!page.contains(pageFooter)) {
			page = page.replaceFirst(bodyClosingTag, pageFooter);
		}
		colours.remove("pageHeader");
		return page;
	}

	private String addPageHeader(String page, HttpServletRequest httpRequest, byte[] bytes,
			Map<String, String> colours) {
		String pageHeader = getPageHeader(httpRequest, bytes);
		String bodyTag = getTagStarting(page, "body");
		colours.put("pageHeader", pageHeader);
		colours.keySet().forEach(key -> {
			if (!key.equals("pageHeader")) {
				colours.put("pageHeader", colours.get("pageHeader").replace(key, colours.get(key)));
			}
		});
		if (!page.contains(pageHeader) && !page.contains(colours.get("pageHeader"))) {
			page = page.replaceFirst(bodyTag, pageHeader);
		}
		return page;
	}

	private String getPageFooter() {
		String now = getFormattedDateTime();
		String bodyClosingTag = "</body>";
		return String.format("<h3 id='footerId' style='color: red; %s'>%s</h3></center>%s",
				getFont().replace("text-decoration: underline;", ""), now, bodyClosingTag);
	}

	private String getPageHeader(HttpServletRequest httpRequest, byte[] bytes) {
		String dbJmsPerformanceLink = createLink("dbJmsPerformanceLinkId", "mongodbOverJmsTest", "lightblue",
				"Mongodb Behind Broker Test", httpRequest);
		String homeLink = createLink("homeLinkId", "", "white", "HOME", httpRequest);
		String performanceLink = createLink("performanceLinkId", "dbPerformance", "yellow", "Performance Test",
				httpRequest);
		String fxLink = createLink("fxLinkId", "fxRates", "lightgray", "Currency Conversion Service", httpRequest);
		String toggleColourLink = createLink("toggleColourLinkId", "toggleColourLink", "lightgray", "Toggle Theme",
				httpRequest);
		String page = new String(bytes);
		String authorLinkedIn = createLink("authorLinkId", "https://www.linkedin.com/in/aneecebanoun/", "gold",
				"Aneece Banoun", httpRequest);
		String gitUrl = createLink("gitLinkId", "https://github.com/aneecebanoun/SpringDataEmbeddedMongoDemo", "gold",
				"GitHub", httpRequest);
		String author = String.format("\u00A9 Created by %s; %s", authorLinkedIn, gitUrl);
		String bodyTag = getTagStarting(page, "body");
		Object[] formatParams = {bodyTag, author, homeLink, fxLink, performanceLink, dbJmsPerformanceLink, toggleColourLink};
		return String.format("%s<center>%s<br/>%s %s<br/>%s %s<br/>%s", formatParams);
	}

	private String getFont() {
		return "font-family: \"Times New Roman\";font-size: 14px; font-weight: normal; vertical-align: middle; text-decoration: underline;";
	}

	private String createLink(String id, String uri, String colour, String displayText,
			HttpServletRequest httpRequest) {
		String url = "";
		String target = "";
		if (uri.contains("http:") || uri.contains("https:")) {
			url = uri;
			target = "target='_blank'";
		} else {
			String baseUrl = httpRequest.getRequestURL().substring(0, httpRequest.getRequestURL().length()
					- httpRequest.getRequestURI().length() + httpRequest.getContextPath().length()) + "/";
			url = baseUrl + uri;
		}
		String font = getFont();
		String link = "<a id='%s' href='%s'style='color: %s; %s' %s>%s</a>";
		return String.format(link, id, url, colour, font, target, displayText);
	}

	private String getTagStarting(String page, String tagName) {
		String tag = "<" + tagName;
		int tagStartIndex = page.indexOf(tag) + tagName.length() + 1;
		for (int i = tagStartIndex; i < page.length(); i++) {
			if (page.charAt(i) == '>') {
				tag += '>';
				break;
			}
			tag += page.charAt(i);
		}
		return tag;
	}

	private String getFormattedDateTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd/MMMM/yyyy (HH:mm)");
		return LocalDateTime.now().format(formatter);
	}

	private Map<String, String> getColours() {
		Map<String, String> colours;
		colours = new HashMap<>();
		colours.put("black", "#fffffd");
		colours.put("white", "darkgray");
		colours.put("green", "gray");
		colours.put("yellow", "blue");
		colours.put("lightgray", "#153e4e");
		colours.put("lightblue", "#183e4e");
		colours.put("gold", "#999999");
		return colours;
	}

}
