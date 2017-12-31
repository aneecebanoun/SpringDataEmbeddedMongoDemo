package banoun.aneece.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import banoun.aneece.model.TradeEntry;
import banoun.aneece.model.Trader;
import banoun.aneece.repositories.TradeEntryRepository;

@Service
public class ReportingDataService {

	private  int dataSize;
	private final String DATE_PATTERN = "dd MMM yyyy";
	private  String[] months;

	TradeEntryRepository tradeEntryRepository;

	@Autowired
	public ReportingDataService(TradeEntryRepository tradeEntryRepository, @Value("${numberofrows}") int dataSize){
		this.tradeEntryRepository = tradeEntryRepository;
		this.dataSize = dataSize ;
		loadData();
	}
	
	private void loadData() {
		months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

		Trader trader = new Trader();
		trader.setName("Trader*");
		
		TradeEntry buyer1 = new TradeEntry();
		buyer1.setBuySellFlag('B');
		buyer1.setAgreedFx(0.50);
		buyer1.setCurrency("AED");
		buyer1.setInstructionDate(getLocalDateFromString("02 Jan 2016"));
		buyer1.setSettlementDate(getLocalDateFromString("02 Jan 2016"));
		buyer1.setUnits(200);
		buyer1.setUnitPrice(100.25);
		buyer1.setTrader(trader);

		TradeEntry buyer2 = new TradeEntry();
		buyer2.setBuySellFlag('B');
		buyer2.setAgreedFx(0.50);
		buyer2.setCurrency("SAR");
		buyer2.setInstructionDate(getLocalDateFromString("01 Jan 2016"));
		buyer2.setSettlementDate(getLocalDateFromString("01 Jan 2016"));
		buyer2.setUnits(500);
		buyer2.setUnitPrice(300.25);
		buyer2.setTrader(trader);
		
		TradeEntry buyer3 = new TradeEntry();
		buyer3.setBuySellFlag('B');
		buyer3.setAgreedFx(0.50);
		buyer3.setCurrency("SAR");
		buyer3.setInstructionDate(getLocalDateFromString("28 Feb 2015"));
		buyer3.setSettlementDate(getLocalDateFromString("28 Feb 2015"));
		buyer3.setUnits(1500);
		buyer3.setUnitPrice(131d);
		buyer3.setTrader(trader);

		TradeEntry seller1 = new TradeEntry();
		seller1.setBuySellFlag('S');
		seller1.setAgreedFx(0.22);
		seller1.setCurrency("ANY");
		seller1.setInstructionDate(getLocalDateFromString("08 Jan 2016"));
		seller1.setSettlementDate(getLocalDateFromString("08 Jan 2016"));
		seller1.setUnits(450);
		seller1.setUnitPrice(150.5);
		seller1.setTrader(trader);

		TradeEntry seller2 = new TradeEntry();
		seller2.setBuySellFlag('S');
		seller2.setAgreedFx(0.22);
		seller2.setCurrency("ANY");
		seller2.setInstructionDate(getLocalDateFromString("05 Jan 2016"));
		seller2.setSettlementDate(getLocalDateFromString("05 Jan 2016"));
		seller2.setUnits(550);
		seller2.setUnitPrice(170.5);
		seller2.setTrader(trader);

		TradeEntry seller3 = new TradeEntry();
		seller3.setBuySellFlag('S');
		seller3.setAgreedFx(0.22);
		seller3.setCurrency("ANY");
		seller3.setInstructionDate(getLocalDateFromString("28 Feb 2015"));
		seller3.setSettlementDate(getLocalDateFromString("28 Feb 2015"));
		seller3.setUnits(750);
		seller3.setUnitPrice(599.5);
		seller3.setTrader(trader);

		tradeEntryRepository.save(seller1);
		tradeEntryRepository.save(seller2);
		tradeEntryRepository.save(seller3);
		tradeEntryRepository.save(buyer1);
		tradeEntryRepository.save(buyer2);
		tradeEntryRepository.save(buyer3);
		loadData(dataSize);
	}

	public List<TradeEntry> loadData(int numberOfRows) {
		Trader trader = new Trader();
		trader.setName("TRADER");
		int traderFrequency =  getRandomFromRange((int)dataSize/18,(int)dataSize/12);
		List<TradeEntry> tradeEntries = new ArrayList<>();
		for(int i = 0; i < numberOfRows; i++){
			TradeEntry traderEntry = new TradeEntry();

			if(i%traderFrequency == 0){
				trader = new Trader();
				trader.setName("TRADER: "+i);
				traderFrequency = getRandomFromRange((int)dataSize/18,(int)dataSize/12);
			}
			traderEntry.setTrader(trader);
			if(i%2 == 0){
				traderEntry.setBuySellFlag('S');
			}else{
				traderEntry.setBuySellFlag('B');
			}

			traderEntry.setAgreedFx(getRandomFromRange(.01, .8));
			if(i%5 == 0){
				traderEntry.setCurrency("AED");
			}else if(i%4 == 0){
				traderEntry.setCurrency("SAR");
			}else{
				traderEntry.setCurrency("ANY");
			}
			LocalDate day =getRandomLocalDate();
			traderEntry.setInstructionDate(day);
			traderEntry.setSettlementDate(day);
			traderEntry.setUnits(getRandomFromRange(10, 15000));
			traderEntry.setUnitPrice(getRandomFromRange(30d, 15000d));
			tradeEntryRepository.save(traderEntry);
			tradeEntries.add(traderEntry);
		}
		return tradeEntries;
	}
	
	

	private String getRandomDay(){
		String date = "";
		String dayOfMonth = String.format("%02d", getRandomFromRange(1, 31));
		String month = months[getRandomFromRange(0, months.length-1)];
		String year = String.format("%4d", getRandomFromRange(1901, 2070));
		date += dayOfMonth +" " + month +" "+ year;
		return date;
	}
	
	public String getRandomCorrectDay(){
		String randomDate = getRandomDay();
		while(!isCorrectDate(randomDate, DATE_PATTERN)){
			randomDate = getRandomDay();
		}
		return randomDate;
	}
	
	public LocalDate getRandomLocalDate(){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
		return LocalDate.parse(getRandomCorrectDay(), formatter);
	}

	public LocalDate getLocalDateFromString(String inDate){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
		return LocalDate.parse(inDate, formatter);
	}
	
	public String getFormattedLocalDate(LocalDate inDate){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
		return inDate.format(formatter);
	}

	
	private Boolean isCorrectDate(String inDate, String pattern){
		Boolean correctDate = false;
		try{
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
			LocalDate.parse(inDate, formatter);
			correctDate = true;
		}catch(DateTimeParseException e){
			//L0gEE
		}
		return correctDate;
	}
	
	public int getRandomFromRange(int min, int max){
		int range = max - min + 1;
		return (int) (min + Math.random() * range);
	}
	
	public double getRandomFromRange(double min, double max){
		double range = max - min;
		return  min + Math.random() * range;
	}
	
	public String timing(long start, long end){
		String time = "";
		int mSeconds = (int) (end - start);
		int seconds = (int) (mSeconds/1000);
		int  minutes =  (int)(seconds/60);
		seconds = seconds%60;
		mSeconds = mSeconds%1000;
		time = "m:s:ms "+ minutes + ":"+seconds+":"+mSeconds;
		return time;
	}


}
