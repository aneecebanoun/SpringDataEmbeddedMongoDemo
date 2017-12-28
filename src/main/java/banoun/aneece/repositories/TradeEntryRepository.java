package banoun.aneece.repositories;
import java.util.stream.Stream;
import org.springframework.data.repository.CrudRepository;
import banoun.aneece.model.TradeEntry;
import banoun.aneece.model.Trader;

public interface  TradeEntryRepository extends CrudRepository<TradeEntry, String>{
	
	Stream<TradeEntry> findAllByOrderByTraderAscAmountDesc();
	Stream<TradeEntry> findAllByOrderByTraderDescAmountDesc();
	
	Stream<TradeEntry> findByTraderOrderByAmountDesc(Trader trader);

	Stream<TradeEntry> findAllByOrderByCurrencyAscAmountDesc();
	Stream<TradeEntry> findAllByOrderByCurrencyDescAmountDesc();

	Stream<TradeEntry> findAllByOrderByUnitPriceAscAmountDesc();
	Stream<TradeEntry> findAllByOrderByUnitPriceDescAmountDesc();

	Stream<TradeEntry> findAllByOrderByUnitsAscAmountDesc();
	Stream<TradeEntry> findAllByOrderByUnitsDescAmountDesc();

	Stream<TradeEntry> findAllByOrderByAgreedFxAscAmountDesc();
	Stream<TradeEntry> findAllByOrderByAgreedFxDescAmountDesc();

	Stream<TradeEntry> findAllByOrderByBuySellFlagAscAmountDesc();
	Stream<TradeEntry> findAllByOrderByBuySellFlagDescAmountDesc();
	
	Stream<TradeEntry> findAllByOrderByAmountAsc();
	Stream<TradeEntry> findAllByOrderByAmountDesc();
	
	Stream<TradeEntry> findAllByOrderBySettlementDateAscAmountDesc();
	Stream<TradeEntry> findAllByOrderBySettlementDateDescAmountDesc();
	
	Stream<TradeEntry> findAllByOrderByInstructionDateAscAmountDesc();
	Stream<TradeEntry> findAllByOrderByInstructionDateDescAmountDesc();

}
