package banoun.aneece.repositories;

import java.util.List;


import org.springframework.data.repository.CrudRepository;

import banoun.aneece.model.TradeEntry;

public interface  TradeEntryRepository extends CrudRepository<TradeEntry, String>{
	
	List<TradeEntry> findAllByOrderByTraderAscAmountDesc();
	List<TradeEntry> findAllByOrderByTraderDescAmountDesc();

	List<TradeEntry> findAllByOrderByCurrencyAscAmountDesc();
	List<TradeEntry> findAllByOrderByCurrencyDescAmountDesc();

	List<TradeEntry> findAllByOrderByUnitPriceAscAmountDesc();
	List<TradeEntry> findAllByOrderByUnitPriceDescAmountDesc();

	List<TradeEntry> findAllByOrderByUnitsAscAmountDesc();
	List<TradeEntry> findAllByOrderByUnitsDescAmountDesc();

	List<TradeEntry> findAllByOrderByAgreedFxAscAmountDesc();
	List<TradeEntry> findAllByOrderByAgreedFxDescAmountDesc();

	List<TradeEntry> findAllByOrderByBuySellFlagAscAmountDesc();
	List<TradeEntry> findAllByOrderByBuySellFlagDescAmountDesc();
	
	List<TradeEntry> findAllByOrderByAmountAsc();
	List<TradeEntry> findAllByOrderByAmountDesc();
	
	List<TradeEntry> findAllByOrderBySettlementDateAscAmountDesc();
	List<TradeEntry> findAllByOrderBySettlementDateDescAmountDesc();
	
	List<TradeEntry> findAllByOrderByInstructionDateAscAmountDesc();
	List<TradeEntry> findAllByOrderByInstructionDateDescAmountDesc();

}
