package banoun.aneece.servlets;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;

import com.keypoint.PngEncoder;

import banoun.aneece.services.LineChartService;

public class ChartViewServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	@Autowired
	public LineChartService lineChartService;
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Cache-Control", "no-cache");
		final Boolean theme = (Boolean) request.getSession().getAttribute("theme");
		final String inCurrency = request.getParameter("inCurrency");
		final String[] currencies = request.getParameterValues("currencies");
		final JFreeChart chart = inCurrency!=null? lineChartService.getJFreeChart(theme, inCurrency, currencies) : lineChartService.getJFreeChart(theme, currencies);
		final BufferedImage buf = chart.createBufferedImage(550, 285, null); 
		final PngEncoder encoder = new PngEncoder( buf, false, 0, 9 );
        response.setContentType( "image/png" );
		response.getOutputStream().write( encoder.pngEncode() );
	}

}