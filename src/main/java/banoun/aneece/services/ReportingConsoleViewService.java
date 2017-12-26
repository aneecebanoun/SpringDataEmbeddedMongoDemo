package banoun.aneece.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import banoun.aneece.model.TradeEntry;

@Service
public class ReportingConsoleViewService {

	@Autowired
	private ReportingDataService reportingDataService;

	private boolean[] toggleFlags = new boolean[10];
	
	private String[] tableHeaders = { "ID", "Trader Name", "In/Out", "Amount", "Settlement Date", "Instruction Date",
			"Currency", "Agreed Fx", "Units", "Unit Price" };

	public Boolean getToggleHeaderFlage(String header){
		return toggleFlags[Arrays.asList(tableHeaders).indexOf(header)];
	}
	
	public void toggleHeaderFlage(String header){
		toggleFlags[Arrays.asList(tableHeaders).indexOf(header.trim())] = !toggleFlags[Arrays.asList(tableHeaders).indexOf(header.trim())];
	}

	
	public String printReport(List<TradeEntry> tradeEnties, String header) {
		StringBuffer result = new StringBuffer();
		String cornerChar = "*";
		String lineChar = "-";
		int[] columnsWidth = adjustWidthForData(tradeEnties, tableHeaders);
		int[] htmlColumnsWidth = adjustWidthForHtmlData(tradeEnties, tableHeaders);

		String tableAlignFormat = getTableAlignFormat(columnsWidth);
		String tableRowsAlignFormat = getTableAlignFormat(htmlColumnsWidth);
		String lineSeperator = getLineSeperator(lineChar, cornerChar, columnsWidth);
		String headerAlignFormat = "| %-" + (lineSeperator.length() - 6) + "s |%n";
		result.append(String.format(lineSeperator.replace(cornerChar, lineChar).replaceFirst(lineChar, cornerChar)
				.substring(0, lineSeperator.length() - 3) + cornerChar + "%n"));
		result.append(String.format(headerAlignFormat, header));
		result.append(String.format(lineSeperator));
		result.append(String.format(tableAlignFormat, tableHeaders));
		result.append(String.format(lineSeperator));

		for (TradeEntry tradeEnty : tradeEnties) {

			String fName = getToAddress(tradeEnty.getTrader().getName(), "tname");
			result.append(String.format(tableRowsAlignFormat, getToAddress(tradeEnty.getId(), "tid"), fName,
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
		return addHtmlTagToTableHeaders(tableHeaders, result.toString());
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

	private int[] adjustWidthForHtmlData(List<TradeEntry> tradeEnties, String[] cHeaders) {
		int[] cWidths = new int[cHeaders.length];
		for (int n = 0; n < cHeaders.length; n++) {
			cWidths[n] = cHeaders[n].length();
		}
		for (TradeEntry trader : tradeEnties) {
			String fName = getToAddress(trader.getTrader().getName(), "tname");
			String id = getToAddress(trader.getId(), "tid");
			int[] tWidths = { id.length(), fName.length(), 9, String.format("%.2f", trader.getAmount()).length(), 14,
					16, 8, 9, 6, 10 };
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
	
	private String getToAddress(String text, String name){
		String colour = name.equals("tid")?"red":"yellow";
		return String.format("<label name='%s' style='color:%s'>%s</label>", name, colour, text);
	}
	
	private static String addHtmlTagToTableHeaders(String[] tableHeaders, String table){
		for(String header : tableHeaders){
			table = table.replace(header, addHtmlTag(header, "label name='header' style='color:white'"));
		}
		return table;
	}
	
	private static String addHtmlTag(String text, String tagNameAndAttribute){
		return String.format("<%s>%s</%s>", tagNameAndAttribute, text, tagNameAndAttribute.split(" ")[0]);
	}

}
