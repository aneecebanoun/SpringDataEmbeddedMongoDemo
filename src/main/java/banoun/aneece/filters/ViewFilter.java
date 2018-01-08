package banoun.aneece.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import banoun.aneece.filters.utils.HtmlCharacterResponseWrapper;
import banoun.aneece.filters.utils.PageHeaderFooterViewUtility;

@WebFilter(filterName = "viewFilter", urlPatterns = "/*", asyncSupported = true)
public class ViewFilter implements Filter {

	PageHeaderFooterViewUtility  pageHeaderFooterViewService = new PageHeaderFooterViewUtility();;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		((HttpServletResponse)response).setHeader("Cache-Control", "no-cache");
		HtmlCharacterResponseWrapper responseWrapper = new HtmlCharacterResponseWrapper((HttpServletResponse) response);
		chain.doFilter(request, responseWrapper);
		byte[] bytes = responseWrapper.getByteArray();
		try {
			if (responseWrapper.getContentType().contains("text/html")) {
				pageHeaderFooterViewService.addPageHeaderAndFooter(request, response, bytes);
			} else {
				response.getOutputStream().write(bytes);
			}
		} catch (Exception e) {
			response.getOutputStream().write(bytes);
		}
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
