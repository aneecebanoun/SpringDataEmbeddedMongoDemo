package banoun.aneece.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import banoun.aneece.model.TradeEntry;
import banoun.aneece.repositories.TradeEntryRepository;

@Service
public class ReportingService {
	
	@Autowired
	TradeEntryRepository tradeEntryRepository;
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
		StringBuffer result = new StringBuffer();
		List<TradeEntry> traderEntries = getAllTraderEntriesOrderedByCriteria(header, reportingConsoleViewService.getToggleHeaderFlage(header));
		this.toggleHeaderFlage(header);
		result.append(reportingConsoleViewService.printReport(traderEntries, "All Buy/Sell Traders (Outgoing/Incoming)"));
		return result.toString();
	}
	
	private String printFilteredReport(String key, String type) {
		StringBuffer result = new StringBuffer();
		result.append(reportingConsoleViewService.printReport(getAllTraderEntriesOrderedByCriteria("*", true)
				.stream()
				.filter(trader -> type.equals("tid")? trader.getId().equals(key):trader.getTrader().getName().equals(key))
				.collect(Collectors.toList()), "Look up result for: ***"+key+"***"));

		return result.toString();
	}
	
	private List<TradeEntry> getAllTraderEntriesOrderedByCriteria(String criteria, Boolean ascOrder){
		List<TradeEntry> orderedTraderEntries = new ArrayList<>();
		switch (criteria) {
		case "ID":
			orderedTraderEntries = tradeEntryRepository.findAllByOrderByAmountDesc().stream().sorted(
					(trader1, trader2) -> ascOrder ? trader1.getId().compareTo(trader2.getId()) : trader2.getId().compareTo(trader1.getId()) ).collect(Collectors.toList());
			break;
		case "Trader Name":
			orderedTraderEntries = ascOrder ? tradeEntryRepository.findAllByOrderByTraderDescAmountDesc() : tradeEntryRepository.findAllByOrderByTraderAscAmountDesc();
			break;
		case "In/Out":
			orderedTraderEntries = ascOrder ? tradeEntryRepository.findAllByOrderByBuySellFlagDescAmountDesc() : tradeEntryRepository.findAllByOrderByBuySellFlagAscAmountDesc();
			break;
		case "Settlement Date":
			orderedTraderEntries = ascOrder ?  tradeEntryRepository.findAllByOrderBySettlementDateDescAmountDesc() : tradeEntryRepository.findAllByOrderBySettlementDateAscAmountDesc();
			break;
		case "Instruction Date":
			orderedTraderEntries = ascOrder ? tradeEntryRepository.findAllByOrderByInstructionDateDescAmountDesc() : tradeEntryRepository.findAllByOrderByInstructionDateAscAmountDesc();
			break;
		case "Currency":
			orderedTraderEntries = ascOrder ? tradeEntryRepository.findAllByOrderByCurrencyDescAmountDesc() : tradeEntryRepository.findAllByOrderByCurrencyAscAmountDesc();
			break;
		case "Agreed Fx":
			orderedTraderEntries = ascOrder ? tradeEntryRepository.findAllByOrderByAgreedFxDescAmountDesc() : tradeEntryRepository.findAllByOrderByAgreedFxAscAmountDesc();
			break;
		case "Units":
			orderedTraderEntries = ascOrder ? tradeEntryRepository.findAllByOrderByUnitsDescAmountDesc() : tradeEntryRepository.findAllByOrderByUnitsAscAmountDesc();
			break;
		case "Unit Price":
			orderedTraderEntries = ascOrder ? tradeEntryRepository.findAllByOrderByUnitPriceDescAmountDesc() : tradeEntryRepository.findAllByOrderByUnitPriceAscAmountDesc();
			break;
		default:
			orderedTraderEntries = ascOrder ? tradeEntryRepository.findAllByOrderByAmountDesc() : tradeEntryRepository.findAllByOrderByAmountAsc();
		}
		return orderedTraderEntries;
	}

}