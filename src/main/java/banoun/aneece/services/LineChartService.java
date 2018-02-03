package banoun.aneece.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import org.jfree.chart.JFreeChart;

@Service
@RequestScope
public class LineChartService {
	
	@Autowired
	public LineChartViewService chartViewService;
	
	public JFreeChart getJFreeChart(final Boolean theme, final String... currencies){
		return chartViewService.createHistoryChart(theme, currencies);
	}

	public JFreeChart getJFreeChart(final Boolean theme, final String inCurrency, final String... currencies){
 		return chartViewService.createPerformanceChart(theme, inCurrency, currencies);
	}

}
