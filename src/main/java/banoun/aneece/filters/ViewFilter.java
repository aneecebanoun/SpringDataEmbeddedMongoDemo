package banoun.aneece.filters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import banoun.aneece.filters.utils.HtmlCharacterResponseWrapper;

@WebFilter(filterName = "viewFilter", urlPatterns = "/*", asyncSupported = true)
public class ViewFilter implements Filter {
	
	Map<String, String> colours;
	
	{
		colours = new HashMap<>();
		colours.put("black", "#fffffd");
		colours.put("white", "darkgray");
		colours.put("green", "gray");
		colours.put("yellow", "blue");
		colours.put("lightgray", "#153e4e");
		colours.put("lightblue", "#183e4e");
		colours.put("gold", "#999999");
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		((HttpServletResponse)response).setHeader("Cache-Control", "no-cache");
		HtmlCharacterResponseWrapper responseWrapper = new HtmlCharacterResponseWrapper((HttpServletResponse) response);
		chain.doFilter(request, responseWrapper);
		byte[] bytes = responseWrapper.getByteArray();
		try {
			if (responseWrapper.getContentType().contains("text/html")) {
				addPageHeaderAndFooter(request, response, bytes);
			} else {
				response.getOutputStream().write(bytes);
			}
		} catch (Exception e) {
			response.getOutputStream().write(bytes);
		}
	}

	private void addPageHeaderAndFooter(ServletRequest request, ServletResponse response, byte[] bytes)
			throws IOException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		Boolean theme = (Boolean) httpRequest.getSession().getAttribute("theme");
		if(theme == null){
			theme = true;
			httpRequest.getSession().setAttribute("theme", theme);
		}
		
		String baseUrl = httpRequest.getRequestURL().substring(0, httpRequest.getRequestURL().length()
				- httpRequest.getRequestURI().length() + httpRequest.getContextPath().length()) + "/";
		String performanceUrl = baseUrl + "dbPerformance";
		String fxUrl = baseUrl + "fxRates";
		String toggleColourUrl = baseUrl + "toggleColourLink";
		String dbJmsPerformanceUrl = baseUrl + "mongodbOverJmsTest";
		String font = "font-family: \"Times New Roman\";font-size: 14px; font-weight: normal; vertical-align: middle; text-decoration: underline;";
		String link = "<a id='%s' href='%s'style='color: %s; %s'>%s</a>";
		String externallink = "<a href='%s'style='color: %s; %s' target='_blank'>%s</a>";
		String performanceLink = String.format(link, "performanceLinkId", performanceUrl, "yellow",font, "Performance Test");
		String dbJmsPerformanceLink = String.format(link, "dbJmsPerformanceLinkId", dbJmsPerformanceUrl, "lightblue", font, "Mongodb Behind Broker Test");
		String homeLink = String.format(link, "homeLinkId", baseUrl, "white", font, "HOME");
		String fxLink = String.format(link, "fxLinkId", fxUrl, "lightgray", font, "Currency Conversion Service");
		String toggleColourLink = String.format(link, "toggleColourLink", toggleColourUrl, "lightgray", font, "Toggle Theme");
		String page = new String(bytes);
		String now = getFormattedDateTime();
		String authorLinkedIn = String.format(externallink, "https://www.linkedin.com/in/aneecebanoun/", "gold", font, "Aneece Banoun");
		String gitUrl = String.format(externallink, "https://github.com/aneecebanoun/SpringDataEmbeddedMongoDemo", "gold", font, "GitHub");
		String author = String.format("\u00A9 Created by %s; %s", authorLinkedIn, gitUrl);
		String bodyTag = getTagStarting(page, "body");
		String bodyClosingTag = "</body>";
		String pageHeader = 
				String.format("%s<center>%s<br/>%s %s<br/>%s %s<br/>%s",
				bodyTag, author, homeLink, fxLink, performanceLink, dbJmsPerformanceLink/*, appTitle*/,toggleColourLink);
		String pageFooter = String.format("<h3 id='footerId' style='color: red; %s'>%s</h3></center>%s", font.replace("text-decoration: underline;", ""), now, bodyClosingTag);
		
		colours.put("pageHeader", pageHeader);
		colours.keySet().forEach(key ->{
			if(!key.equals("pageHeader")){
				colours.put("pageHeader", colours.get("pageHeader").replace(key, colours.get(key)));
			}
		});
		if(!page.contains(pageHeader) && !page.contains(colours.get("pageHeader"))){
			page = page.replaceFirst(bodyTag, pageHeader);
		}
		if(!page.contains(pageFooter)){
			page = page.replaceFirst(bodyClosingTag, pageFooter);
		}
		colours.remove("pageHeader");
		if(theme){
			colours.put("page", page);
			colours.keySet().forEach(key ->{
				if(!key.equals("page")){
					colours.put("page", colours.get("page").replace(key, colours.get(key)));
				}
			});
			response.getOutputStream().write(colours.get("page").getBytes());
		}else{
			response.getOutputStream().write(page.getBytes());
		}
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

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Hello
	}

	@Override
	public void destroy() {
		// Goodbye
	}

}
