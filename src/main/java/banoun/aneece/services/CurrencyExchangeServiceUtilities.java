package banoun.aneece.services;

import java.time.DayOfWeek;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.HashMap;
import java.util.stream.Collectors;

import banoun.aneece.model.CurrencyExchange;

public class CurrencyExchangeServiceUtilities {
	
	public static final Integer[] FROM_UPDATE_TIME = {14, 15};
	public static final Integer[] TO_UPDATE_TIME = {16, 10};
	public static final String FX_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
	public static final String FX_HISTORY_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml";
	public static final List<String> currencyDetails = Arrays.asList(
			"USD:US dollar", "JPY:Japanese yen", "GBP:Pound sterling",
			"AUD:Australian dollar", "CAD:Canadian dollar", "HKD:Hong Kong dollar",
			"NZD:New Zealand dollar", "SGD:Singaporean dollar", "KRW:South Korean won", 
			"ZAR:South African rand", "EUR:Euro", "RUB:Russian Ruble", "INR:Indian Rupee", 
			"CNY:China Yuan", "BRL:Brazilian Real", "ILS:Israeli Sheqel", "MXN:Mexican Peso");
	public static final List<String> currencies = currencyDetails.stream()
			.map(currencyDetail -> currencyDetail.split(":")[0].trim()).collect(Collectors.toList());
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	public static final String DATE_PATTERN_ = new StringBuilder(DATE_PATTERN).reverse().toString();
	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
	public static final DateTimeFormatter FORMATTER_ = DateTimeFormatter.ofPattern(DATE_PATTERN_);

	public static final Map<String, List<CurrencyExchange>> dailyCurrencyExchangeRates = new ConcurrentHashMap<>();
	public static Map<String, List<CurrencyExchange>> currencyExchangeHistory = new ConcurrentHashMap<>();
	public static String calculatedLastUpdateDate;
	
	private CurrencyExchangeServiceUtilities(){}
	
	public static Boolean isNull(final Object...os){
		return Arrays.asList(os).stream().map(e->e==null).collect(Collectors.toList()).contains(true);
	}
	
	public static Boolean validAmount(final String amount){
		try{
			Double.valueOf(amount);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}

	
	public static Boolean isWeekEnd(final LocalDateTime localDate) {
		return EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(localDate.getDayOfWeek());
	}

	public static LocalDateTime getLastUpdateDay(final LocalDateTime localDateIn) {
		LocalDateTime localDate = localDateIn.plusNanos(0);
		if(beforeUpdateTime()){
			localDate = localDate.plusDays(-1);
		}
		while (isWeekEnd(localDate)) {
			localDate = localDate.plusDays(-1);
		}
		return localDate;
	}
	
	public static Boolean withinUpdateTime(){
		final LocalDateTime from = fromToUpdateTimes()[0];
		final LocalDateTime to = fromToUpdateTimes()[1];
		final LocalDateTime now =LocalDateTime.now();
		final Boolean timeForUpdate = now.isAfter(from) && now.isBefore(to);
		final Boolean noUpdateYet = dailyCurrencyExchangeRates.get(now.format(FORMATTER_)) == null;
		return !isWeekEnd(now) && noUpdateYet && timeForUpdate;
	}
	
	public static Boolean beforeUpdateTime(){
		final LocalDateTime from = fromToUpdateTimes()[0];
		return LocalDateTime.now().isBefore(from);
	}
	
	public static LocalDateTime[] fromToUpdateTimes(){
		final LocalDateTime from = LocalDate.now().atTime(FROM_UPDATE_TIME[0], FROM_UPDATE_TIME[1]);
		final LocalDateTime to = LocalDate.now().atTime(TO_UPDATE_TIME[0], TO_UPDATE_TIME[1]);
		return new LocalDateTime[]{from, to};
	}
	
	public static String getFormattedDay() {
		final LocalDateTime day = getLastUpdateDay(LocalDateTime.now());
		return day.format(FORMATTER_);
	}

	public static String displayDate(final String inDate) {
		return LocalDate.parse(inDate, FORMATTER).format(FORMATTER_);
	}
	
	public static String displayDate(final String inDate, final String inPattern, final String outPattern) {
		final DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern(inPattern);
		final DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern(outPattern);
		return LocalDate.parse(inDate, inFormatter).format(outFormatter);
	}
	
	public static LocalDate textToLocalDate(final String inDate, final String pattern) {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return LocalDate.parse(inDate, formatter);
	}
	
	public static LocalDateTime textToLocalDateTime(final String inDate, final String pattern) {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return LocalDateTime.parse(inDate, formatter);
	}
	
}
