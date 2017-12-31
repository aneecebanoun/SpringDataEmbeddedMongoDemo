package banoun.aneece.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import banoun.aneece.model.TradeEntry;
import banoun.aneece.repositories.TradeEntryRepository;
import banoun.aneece.repositories.TraderRepository;
import banoun.aneece.services.ReportingConsoleViewService.HEADERS;

@Service
public class ReportingService {
	
	@Autowired
	TradeEntryRepository tradeEntryRepository;
	@Autowired
	TraderRepository traderRepository;
	@Autowired
	ReportingConsoleViewService reportingConsoleViewService;
	
	public String runTradeReporting(String sortingOption){
		StringBuffer result = new StringBuffer();
		result.append(printSortedReport(sortingOption));
		return result.toString();
	}
	
	public String runTradeFilteredReporting(String key, String type){
		StringBuffer result = new StringBuffer();
		result.append(printFilteredReport(key, type));
		return result.toString(); 
	}
	
	public void toggleHeaderFlage(String header){
		reportingConsoleViewService.toggleHeaderFlage(header);
	}
	
	private String printSortedReport(String header) {
		Map<String, String> map = new HashMap<>();
		StringBuffer result = new StringBuffer();
		List<TradeEntry> traderEntries = getAllTraderEntriesOrderedByCriteria(header, reportingConsoleViewService.getToggleHeaderFlage(header));
		this.toggleHeaderFlage(header);
		String qSize = reportingConsoleViewService.stringForKey(map, "(*"+traderEntries.size()+""+"*)", "th");
		result.append(reportingConsoleViewService.printReport(traderEntries, "All Buy/Sell Traders (Outgoing/Incoming) Total Query Size: "+ qSize, map));
		return result.toString();
	}
	
	private String printFilteredReport(String key, String type) {
		Map<String, String> map = new HashMap<>();
		List<TradeEntry> trades = type.equals("tid")?  
			Arrays.asList(tradeEntryRepository.findAllById(Arrays.asList(key)).iterator().next()):
			tradeEntryRepository.findByTraderInOrderByAmountDesc(traderRepository.findAllByName(key)).parallel().collect(Collectors.toList());
		String traderId = reportingConsoleViewService.stringForKey(map, "("+trades.get(0).getTrader().getId()+")", "th");
		String traderName = reportingConsoleViewService.stringForKey(map, "("+trades.get(0).getTrader().getName()+")", "th");
		String rsSize = reportingConsoleViewService.stringForKey(map, "(*"+trades.size()+""+"*)", "th");
		String tableTitle = String.format("Look up result for: ***%s*** total number of result set: %s; ID for %s is: %s", key, rsSize, traderName, traderId);
		return 	new StringBuffer(reportingConsoleViewService.printReport( trades, tableTitle, map)).toString();
	}
	
	private List<TradeEntry> getAllTraderEntriesOrderedByCriteria(String criteria, Boolean ascOrder){
		List<TradeEntry> orderedTraderEntries = new ArrayList<>();
		switch (HEADERS.getMatch(criteria)) {
		case ID:
			orderedTraderEntries = tradeEntryRepository.findAllByOrderByAmountDesc().sorted(
					(trader1, trader2) -> ascOrder ? trader1.getId().compareTo(trader2.getId()) : trader2.getId().compareTo(trader1.getId()) ).collect(Collectors.toList());
			break;
		case NAME:
			orderedTraderEntries = ascOrder ? 
					tradeEntryRepository.findAllByOrderByTraderDescAmountDesc().parallel().collect(Collectors.toList()) : 
					tradeEntryRepository.findAllByOrderByTraderAscAmountDesc().parallel().collect(Collectors.toList());
			break;
		case IN_OUT:
			orderedTraderEntries = ascOrder ? 
					tradeEntryRepository.findAllByOrderByBuySellFlagDescAmountDesc().parallel().collect(Collectors.toList()) : 
					tradeEntryRepository.findAllByOrderByBuySellFlagAscAmountDesc().parallel().collect(Collectors.toList());
			break;
		case SETTELMENT:
			orderedTraderEntries = ascOrder ?  
					tradeEntryRepository.findAllByOrderBySettlementDateDescAmountDesc().parallel().collect(Collectors.toList()) : 
					tradeEntryRepository.findAllByOrderBySettlementDateAscAmountDesc().parallel().collect(Collectors.toList());
			break;
		case INSTRUCTION:
			orderedTraderEntries = ascOrder ? 
					tradeEntryRepository.findAllByOrderByInstructionDateDescAmountDesc().parallel().collect(Collectors.toList()) : 
					tradeEntryRepository.findAllByOrderByInstructionDateAscAmountDesc().parallel().collect(Collectors.toList());
			break;
		case CURRENCY:
			orderedTraderEntries = ascOrder ? 
					tradeEntryRepository.findAllByOrderByCurrencyDescAmountDesc().parallel().collect(Collectors.toList()) : 
					tradeEntryRepository.findAllByOrderByCurrencyAscAmountDesc().parallel().collect(Collectors.toList());
			break;
		case FX:
			orderedTraderEntries = ascOrder ? 
					tradeEntryRepository.findAllByOrderByAgreedFxDescAmountDesc().parallel().collect(Collectors.toList()) : 
					tradeEntryRepository.findAllByOrderByAgreedFxAscAmountDesc().parallel().collect(Collectors.toList());
			break;
		case UNITS:
			orderedTraderEntries = ascOrder ? 
					tradeEntryRepository.findAllByOrderByUnitsDescAmountDesc().parallel().collect(Collectors.toList()) : 
					tradeEntryRepository.findAllByOrderByUnitsAscAmountDesc().parallel().collect(Collectors.toList());
			break;
		case UNIT_PRICE:
			orderedTraderEntries = ascOrder ? 
					tradeEntryRepository.findAllByOrderByUnitPriceDescAmountDesc().parallel().collect(Collectors.toList()) : 
					tradeEntryRepository.findAllByOrderByUnitPriceAscAmountDesc().parallel().collect(Collectors.toList());
			break;
		default:
			orderedTraderEntries = ascOrder ? 
					tradeEntryRepository.findAllByOrderByAmountDesc().parallel().collect(Collectors.toList()) : 
					tradeEntryRepository.findAllByOrderByAmountAsc().parallel().collect(Collectors.toList());
		}
		return orderedTraderEntries;
	}

}
