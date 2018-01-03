package banoun.aneece.filters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

@WebFilter(filterName = "mainFilter", urlPatterns = "/*", asyncSupported = true)
public class MainFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
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
		String baseUrl = httpRequest.getRequestURL().substring(0, httpRequest.getRequestURL().length()
				- httpRequest.getRequestURI().length() + httpRequest.getContextPath().length()) + "/";
		String performanceUrl = baseUrl + "dbPerformance";
		String dbJmsPerformanceUrl = baseUrl + "mongodbOverJmsTest";
		String link = "<a id='%s' href='%s'style='color: %s'>%s</a>";
		String externallink = "<a href='%s'style='color: %s' target='_blank'>%s</a>";
		String performanceLink = String.format(link, "performanceLinkId", performanceUrl, "yellow", "Performance Test");
		String dbJmsPerformanceLink = String.format(link, "dbJmsPerformanceLinkId", dbJmsPerformanceUrl, "lightblue", "Mongodb Behind Broker Test");
		String homeLink = String.format(link, "homeLinkId", baseUrl, "white", "HOME");
		String page = new String(bytes);
		String now = getFormattedDateTime();
		String authorLinkedIn = String.format(externallink, "https://www.linkedin.com/in/aneecebanoun/", "gold", "Aneece Banoun");
		String gitUrl = String.format(externallink, "https://github.com/aneecebanoun/SpringDataEmbeddedMongoDemo", "gold", "GitHub");
		String author = String.format("\u00A9 Created by %s; %s", authorLinkedIn, gitUrl);
		String bodyTag = getTagStarting(page, "body");
		String bodyClosingTag = "</body>";
		String appTitle = "SPRING DATA MONGODB DEMO!";
		String pageHeader = 
				String.format("%s<center>%s <label3 style='color: gold'>(%s)</label3><br/>%s %s<br/><h2 style='color: gold'>%s</h2>",
				bodyTag, homeLink, author, performanceLink, dbJmsPerformanceLink, appTitle);
		String pageFooter = String.format("<h3 style='color: red'>%s</h3></center>%s", now, bodyClosingTag);
		page = page.replace(bodyTag, pageHeader);
		page = page.replace(bodyClosingTag, pageFooter);
		response.getOutputStream().write(page.getBytes());
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
