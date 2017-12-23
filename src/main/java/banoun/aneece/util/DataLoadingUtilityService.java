package banoun.aneece.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import banoun.aneece.model.TradeEntry;
import banoun.aneece.repositories.TradeEntryRepositories;

@Service
public class DataLoadingUtilityService {
	
	public String[] months;
	public final int RANDOM_ROWs = 3000;
	
	TradeEntryRepositories tradeEntryRepositories;
	
	public List<TradeEntry> getAllTraderEntriesFromDB(){
		List<TradeEntry> tradeEntries = new ArrayList<>();
		System.out.println("tradeEntryRepositories: "+tradeEntryRepositories);
		tradeEntryRepositories.findAll().forEach(tradeEntries::add);
		return tradeEntries;
	}
	
	@Autowired
	public DataLoadingUtilityService(TradeEntryRepositories tradeEntryRepositories){
		this.tradeEntryRepositories = tradeEntryRepositories;
		months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

		TradeEntry buyer1 = new TradeEntry();
		buyer1.setTraderName("buyer1");
		buyer1.setBuySellFlag('B');
		buyer1.setAgreedFx(0.50);
		buyer1.setCurrency("AED");
		buyer1.setInstructionDate("02 Jan 2016");
		buyer1.setSettlementDate("02 Jan 2016");
		buyer1.setUnits(200);
		buyer1.setUnitPrice(100.25);

		TradeEntry buyer2 = new TradeEntry();
		buyer2.setTraderName("buyer2");
		buyer2.setBuySellFlag('B');
		buyer2.setAgreedFx(0.50);
		buyer2.setCurrency("SAR");
		buyer2.setInstructionDate("01 Jan 2016");
		buyer2.setSettlementDate("01 Jan 2016");
		buyer2.setUnits(500);
		buyer2.setUnitPrice(300.25);
		
		TradeEntry buyer3 = new TradeEntry();
		buyer3.setTraderName("buyer3");
		buyer3.setBuySellFlag('B');
		buyer3.setAgreedFx(0.50);
		buyer3.setCurrency("SAR");
		buyer3.setInstructionDate("28 Feb 2015");
		buyer3.setSettlementDate("28 Feb 2015");
		buyer3.setUnits(1500);
		buyer3.setUnitPrice(131d);

		TradeEntry seller1 = new TradeEntry();
		seller1.setTraderName("seller1");
		seller1.setBuySellFlag('S');
		seller1.setAgreedFx(0.22);
		seller1.setCurrency("ANY");
		seller1.setInstructionDate("08 Jan 2016");
		seller1.setSettlementDate("08 Jan 2016");
		seller1.setUnits(450);
		seller1.setUnitPrice(150.5);

		TradeEntry seller2 = new TradeEntry();
		seller2.setTraderName("seller2");
		seller2.setBuySellFlag('S');
		seller2.setAgreedFx(0.22);
		seller2.setCurrency("ANY");
		seller2.setInstructionDate("05 Jan 2016");
		seller2.setSettlementDate("05 Jan 2016");
		seller2.setUnits(550);
		seller2.setUnitPrice(170.5);

		TradeEntry seller3 = new TradeEntry();
		seller3.setTraderName("seller3");
		seller3.setBuySellFlag('S');
		seller3.setAgreedFx(0.22);
		seller3.setCurrency("ANY");
		seller3.setInstructionDate("28 Feb 2015");
		seller3.setSettlementDate("28 Feb 2015");
		seller3.setUnits(750);
		seller3.setUnitPrice(599.5);
		tradeEntryRepositories.save(seller1);
		tradeEntryRepositories.save(seller2);
		tradeEntryRepositories.save(seller3);
		tradeEntryRepositories.save(buyer1);
		tradeEntryRepositories.save(buyer2);
		tradeEntryRepositories.save(buyer3);
		
		for(int i = 0; i < RANDOM_ROWs; i++){
			TradeEntry trader = new TradeEntry();

			if(i%2 == 0){
				trader.setBuySellFlag('S');
				trader.setTraderName("SELLER: "+i);
			}else{
				trader.setBuySellFlag('B');
				trader.setTraderName("BUYER: "+i);
			}
			
			trader.setAgreedFx(getRandomFromRange(.01, .8));
			if(i%5 == 0){
				trader.setCurrency("AED");
			}else if(i%4 == 0){
				trader.setCurrency("SAR");
			}else{
				trader.setCurrency("ANY");
			}
			String day =getRandomDay();
			trader.setInstructionDate(day);
			trader.setSettlementDate(day);
			trader.setUnits(getRandomFromRange(10, 15000));
			trader.setUnitPrice(getRandomFromRange(30d, 15000d));
			tradeEntryRepositories.save(trader);
		}
	}

	public String getRandomDay(){
		String date = "";
		String dayOfMonth = String.format("%02d", getRandomFromRange(1, 30));
		String month = months[getRandomFromRange(0, months.length-1)];
		String year = String.format("%4d", getRandomFromRange(1901, 2070));
		if(month.equals("Feb")){
			dayOfMonth = String.format("%02d", getRandomFromRange(1, 28));
		}
		date += dayOfMonth +" " + month +" "+ year;
		return date;
	}
	
	public int getRandomFromRange(int min, int max){
		int range = max - min + 1;
		return (int) (min + Math.random() * range);
	}
	
	public double getRandomFromRange(double min, double max){
		double range = max - min;
		return  min + Math.random() * range;
	}

}
