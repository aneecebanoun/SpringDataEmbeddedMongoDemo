package banoun.aneece.services;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
public class LineChartViewService {

	@Autowired
	LineChartDataService chartDataService;
	
	public JFreeChart createPerformanceChart(final Boolean theme, final String inCurrency, final String...currencies) {
		final Map<String, Object> currencyPerformanceDatasetsMap = chartDataService.currencyPerformanceDatasets(inCurrency, currencies);
		final TimeSeriesCollection currencyDataSets = (TimeSeriesCollection)currencyPerformanceDatasetsMap.get("dataSets");
		final String title = (String)currencyPerformanceDatasetsMap.get("chartTitle");
		final JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "", "Currency Value", currencyDataSets, true, true,
				false);
		final XYPlot plot = (XYPlot) chart.getPlot();
		stylePlotAndChart(plot, chart, theme==null?Color.WHITE:theme?Color.BLACK:Color.WHITE);
		styleLineChart(plot, currencyDataSets);
		return chart;
	}
	
	public JFreeChart createHistoryChart(final Boolean theme, final String...currencies) {
		final Map<String, Object> currencyPerformanceDatasetsMap = chartDataService.currenciesDatasets(currencies);
		final TimeSeriesCollection currencyDataSets = (TimeSeriesCollection)currencyPerformanceDatasetsMap.get("dataSets");
		final String title = (String)currencyPerformanceDatasetsMap.get("chartTitle");
		final JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "", "Currency Value", currencyDataSets, true, true, false);
		final XYPlot plot = (XYPlot) chart.getPlot();
		stylePlotAndChart(plot, chart, theme==null?Color.WHITE:theme?Color.BLACK:Color.WHITE);
		styleLineChart(plot, currencyDataSets);
		return chart;
	}
	
	private void stylePlotAndChart(final XYPlot plot, final JFreeChart chart, final Color mainColor){
		chart.setTextAntiAlias( false );
	    chart.setAntiAlias( false );
	    plot.getRangeAxis().setAutoRange(true);
		plot.setDomainGridlinePaint(mainColor);
		plot.setRangeGridlinePaint(mainColor);
		plot.setBackgroundPaint(invertColor(mainColor));
		chart.setBackgroundPaint(invertColor(mainColor));
		chart.getTitle().setPaint(mainColor);
	
		plot.getDomainAxis().setLabelPaint(mainColor);
		plot.getDomainAxis().setTickLabelPaint(mainColor);
		plot.getDomainAxis().setTickMarkPaint(mainColor);
		plot.getDomainAxis().setAxisLinePaint(mainColor);
		
		plot.getRangeAxis().setLabelPaint(mainColor);
		plot.getRangeAxis().setTickLabelPaint(mainColor);
		plot.getRangeAxis().setTickMarkPaint(mainColor);
		plot.getRangeAxis().setAxisLinePaint(mainColor);
	}

	private void styleLineChart(final XYPlot plot, final TimeSeriesCollection currencyDataSets) {
		for (int i = 0; i < currencyDataSets.getSeries().size(); i++) {
			plot.getRendererForDataset(currencyDataSets).setSeriesStroke(i, new BasicStroke(5.0f));
			plot.getRendererForDataset(currencyDataSets).setSeriesPaint(i, getRandomColor());
		}
	}

	private Color getRandomColor() {
		return new Color((int) (Math.random() * 0x1000000));
	}
	
	private Color invertColor(Color color){
		return new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
	}

}
