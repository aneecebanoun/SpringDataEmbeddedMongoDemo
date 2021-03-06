package banoun.aneece.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import banoun.aneece.model.TradeEntry;

@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ReportingConsoleViewService {

    private static Character[] characters;
    
	@Autowired
	private ReportingDataService reportingDataService;

	private boolean[] toggleFlags = new boolean[10];
	
	private String[] tableHeaders = Stream.of(HEADERS.values()).map(e -> e.toString()).toArray(String[]::new); 

	public Boolean getToggleHeaderFlage(String header){
		if(Arrays.asList(tableHeaders).indexOf(header.trim()) < 0){
			header = "Amount";
		}
		return toggleFlags[Arrays.asList(tableHeaders).indexOf(header)];
	}
	
	public void toggleHeaderFlage(String header){
		if(Arrays.asList(tableHeaders).indexOf(header.trim()) < 0){
			header = "Amount";
		}
		toggleFlags[Arrays.asList(tableHeaders).indexOf(header.trim())] = !toggleFlags[Arrays.asList(tableHeaders).indexOf(header.trim())];
	}
	
	public String printReport(List<TradeEntry> tradeEnties, String header, Map<String, String> map) {
		StringBuffer result = new StringBuffer();
		String cornerChar = "*";
		String lineChar = "-";
		int[] columnsWidth = adjustWidthForData(tradeEnties, tableHeaders);
		String tableAlignFormat = getTableAlignFormat(columnsWidth);
		String tableRowsAlignFormat = getTableAlignFormat(columnsWidth);
		String lineSeperator = getLineSeperator(lineChar, cornerChar, columnsWidth);
		String headerAlignFormat = "| %-" + (lineSeperator.length() - 6) + "s |%n";
		result.append(String.format(lineSeperator.replace(cornerChar, lineChar).replaceFirst(lineChar, cornerChar)
				.substring(0, lineSeperator.length() - 3) + cornerChar + "%n"));
		result.append(String.format(headerAlignFormat, header));
		result.append(String.format(lineSeperator));
		Object[] encodedtableHeaders = new String[tableHeaders.length];
		for(int i = 0; i < tableHeaders.length; i++){
			encodedtableHeaders[i] = stringForKey(map, tableHeaders[i], "th");
		}
		result.append(String.format(tableAlignFormat, encodedtableHeaders));
		result.append(String.format(lineSeperator));

		for (TradeEntry tradeEnty : tradeEnties) {
			String fName = stringForKey(map, tradeEnty.getTrader().getName(), "tname");
			String id = stringForKey(map, tradeEnty.getId(), "tid");
			result.append(String.format(tableRowsAlignFormat, id, fName,
					tradeEnty.getBuySellFlag() == 'B' ? "Outgoing" : "Incoming",
					String.format("%.2f", tradeEnty.getAmount()),
					tradeEnty.getSettlementDate().equals(tradeEnty.getInstructionDate())
							? reportingDataService.getFormattedLocalDate(tradeEnty.getSettlementDate())
							: reportingDataService.getFormattedLocalDate(tradeEnty.getSettlementDate()) + "*",
					reportingDataService.getFormattedLocalDate(tradeEnty.getInstructionDate()),
					tradeEnty.getCurrency(), String.format("%.2f", tradeEnty.getAgreedFx()), tradeEnty.getUnits(),
					String.format("%.2f", tradeEnty.getUnitPrice())));
		}
		result.append(String.format(lineSeperator));
		return replaceKeysForHtmlField(result.toString(), map);
	}

	private int[] adjustWidthForData(List<TradeEntry> tradeEnties, String[] cHeaders) {
		int[] cWidths = new int[cHeaders.length];
		for (int n = 0; n < cHeaders.length; n++) {
			cWidths[n] = cHeaders[n].length();
		}
		for (TradeEntry trader : tradeEnties) {
			int[] tWidths = { trader.getId().length(), trader.getTrader().getName().length(), 9,
					String.format("%.2f", trader.getAmount()).length(), 14, 16, 8, 9, 6, 10 };
			for (int i = 0; i < tWidths.length; i++) {
				if (tWidths[i] > cWidths[i]) {
					cWidths[i] = tWidths[i];
				}
			}
		}
		return cWidths;
	}
	
	private String getTableAlignFormat(int... columns) {
		String tableAlignFormat = "";
		for (int column : columns) {
			tableAlignFormat += "| %-" + column + "s ";
		}
		return tableAlignFormat + "|%n";
	}

	private String getLineSeperator(String lineChar, String cornerChar, int... columns) {
		String seperator = cornerChar;
		for (int column : columns) {
			column = column + 2;
			for (int n = 0; n < column; n++) {
				seperator += lineChar;
			}
			seperator += cornerChar;
		}
		return seperator + "%n";
	}
	
	private String tagRowField(String text, String name){
		String colour = name.equals("tid")?"red":"yellow";
		return String.format("<label name='%s' style='color:%s'>%s</label>", name, colour, text);
	}
	
	private String replaceKeysForHtmlField(String table, Map<String, String> map){
		String tableKey = stringForKey(map, "*ANY*", "th");
		map.put(tableKey, table);
		map.keySet().stream().forEach(key -> map.put(tableKey, map.get(tableKey).replace(key, map.get(key))));
		return map.get(tableKey);
	}

    private String randomSequenceForText(String inText){
        return getRandomSequence(inText.length());
    }

	public static String getRandomSequence(int numberOfCharacters) {
		StringBuffer word = new StringBuffer();
        for(int i = 0; i < numberOfCharacters; i++){
            int randomIndex = (int)(Math.random() * characters.length) ;
            word.append(characters[randomIndex].toString());
        }
		return word.toString();
	}
	
	public String stringForKey(Map<String, String> map, String inText, String formatFlag){
		String key = randomSequenceForText(inText);
		String htmlFormattedText = "th".equals(formatFlag)? addHtmlTag(inText, "label name='header' style='color:white'") : tagRowField(inText, formatFlag);
		if(map.containsValue(htmlFormattedText)){
			return map.keySet().stream().filter(k -> map.get(k).equals(htmlFormattedText) ).collect(Collectors.toList()).get(0);
		}
		while(map.containsKey(key)){
			key = randomSequenceForText(inText);
		}
		map.put(key, htmlFormattedText);
		return key;
	}
	
	private String addHtmlTag(String text, String tagNameAndAttribute){
		return String.format("<%s>%s</%s>", tagNameAndAttribute, text, tagNameAndAttribute.split(" ")[0]);
	}
    
    static{
    	List<Character> charactersList = new ArrayList<>();
        for(char c = '0'; c < '9'+1;c++)
            charactersList.add(c);
        for(char c = 'a'; c < 'z'+1;c++)
            charactersList.add(c);
        for(char c = 'A'; c < 'Z'+1;c++)
            charactersList.add(c);
        characters =  charactersList.stream().toArray(Character[]::new);
    }

    public static enum HEADERS{
    	ID("Transaction ID"),
    	NAME("Trader Name"),
    	IN_OUT("In/Out"),
    	AMOUNT("Amount"),
    	SETTELMENT("Settlement Date"),
    	INSTRUCTION("Instruction Date"),
    	CURRENCY("Currency"),
    	FX("Agreed Fx"),
    	UNITS("Units"),
    	UNIT_PRICE("Unit Price");

    	private final String message;
    	
    	HEADERS(String message){
    		this.message = message;
    	}
    	
    	public String getMessage(){
    		return message;
    	}

    	@Override
    	public String toString() {
    		return message;
    	}
    	
    	static HEADERS getMatch(String matchText){
    		try{
    			return Stream.of(HEADERS.values()).filter(e -> e.toString().equals(matchText)).collect(Collectors.toList()).get(0);
    		}catch(Exception e){
    			return AMOUNT;
    		}
    	}
    }

}
