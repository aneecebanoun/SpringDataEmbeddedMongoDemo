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
@WebFilter(filterName = "mainFilter", urlPatterns = "/*")
public class MainFilter implements Filter{


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HtmlCharacterResponseWrapper responseWrapper = new HtmlCharacterResponseWrapper((HttpServletResponse)response);
		chain.doFilter(request, responseWrapper);
		byte[] bytes = responseWrapper.getByteArray();
		   if (responseWrapper.getContentType().contains("text/html")) {
	            addPageHeaderAndFooter(request, response, bytes);
	        }else {
	            response.getOutputStream().write(bytes);
	        }
	}

	private void addPageHeaderAndFooter(ServletRequest request, ServletResponse response, byte[] bytes) throws IOException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		String baseUrl = httpRequest.getRequestURL().substring(0, httpRequest.getRequestURL().length() - httpRequest.getRequestURI().length() + httpRequest.getContextPath().length()) + "/";
		String performanceUrl =  baseUrl + "dbPerformance";
		String link = "<a href='%s'style='color: %s'>%s</a>";
		String performanceLink = String.format(link, performanceUrl, "yellow","Performance Test");
		String homeLink = String.format(link, baseUrl, "white","HOME");
		String page = new String(bytes);
		String now = getFormattedDateTime();	            
		String bodyTag = getTagStarting(page, "body");
		String bodyClosingTag = "</body>";
		String appTitle = "SPRING DATA MONGODB DEMO!";
		String pageHeader = String.format("%s<center>%s<***>%s<br/><h2 style='color: gold'>%s (%s)</h2></center>", bodyTag, homeLink, performanceLink, appTitle, now);
		String pageFooter = String.format("<center><h3 style='color: red'>%s</h3></center>%s", now.toString(), bodyClosingTag);
		page = page.replace(bodyTag, pageHeader);
		page = page.replace(bodyClosingTag, pageFooter);
		response.getOutputStream().write(page.getBytes());
	}
	
	private String getTagStarting(String page, String tagName){
		String tag = "<"+tagName;
		for(int i = page.indexOf(tag)+5; i < page.length(); i++){
			if(page.charAt(i) == '>'){
				tag += '>';
				break;
			}
			tag += page.charAt(i);
		}
		return tag;
	}
	
	private String getFormattedDateTime(){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		return LocalDateTime.now().format(formatter);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//Hello
	}

	@Override
	public void destroy() {
		// Goodbye
	}

}
