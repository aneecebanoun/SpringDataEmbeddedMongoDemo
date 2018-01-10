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

	private TradeEntryRepository tradeEntryRepository;

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
		TradeEntry buyer1 = new TradeEntry(trader, "AED", 100.25, 200, 0.50, 'B', 
				getLocalDateFromString("02 Jan 2016"), getLocalDateFromString("02 Jan 2016"));
		TradeEntry buyer2 = new TradeEntry(trader, "SAR", 300.25, 500, 0.50, 'B', 
						getLocalDateFromString("01 Jan 2016"), getLocalDateFromString("01 Jan 2016"));
		TradeEntry buyer3 = new TradeEntry(trader, "SAR", 131d, 1500, 0.50, 'B', 
				getLocalDateFromString("28 Feb 2015"), getLocalDateFromString("28 Feb 2015"));
		TradeEntry seller1 = new TradeEntry(trader, "ANY", 150.5, 450, 0.22, 'S', 
				getLocalDateFromString("08 Jan 2016"), getLocalDateFromString("08 Jan 2016"));
		TradeEntry seller2 = new TradeEntry(trader, "ANY", 170.5, 550, 0.22, 'S', 
				getLocalDateFromString("05 Jan 2016"), getLocalDateFromString("05 Jan 2016"));
		TradeEntry seller3 = new TradeEntry(trader, "ANY", 599.5, 750, 0.22, 'S', 
				getLocalDateFromString("28 Feb 2015"), getLocalDateFromString("28 Feb 2015"));
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
