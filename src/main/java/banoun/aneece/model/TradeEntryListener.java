package banoun.aneece.model;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

public class TradeEntryListener extends AbstractMongoEventListener<TradeEntry> {

	 @Autowired
	 private MongoOperations mongoOperations;

	@Override
	public void onBeforeConvert(BeforeConvertEvent<TradeEntry> event) {
		TradeEntry tradeEntry = event.getSource();
		tradeEntry.getAmount();
		if (tradeEntry.getTrader() != null) {
			 mongoOperations.save(tradeEntry.getTrader());
		}
	}
}
