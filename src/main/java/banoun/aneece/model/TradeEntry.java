package banoun.aneece.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class TradeEntry {

    @Id
    private ObjectId _id;
	private String traderName;
	private String currency;
	private Double unitPrice;
	private Integer units;
	private Double agreedFx;
	private Character buySellFlag;
	private String instructionDate;
	private String settlementDate;
	private String pattern = "dd MMM yyyy";

	public ObjectId getId() {
		return _id;
	}

	public void setId(ObjectId id) {
		this._id = id;
	}
	
	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getTraderName() {
		return traderName;
	}

	public void setTraderName(String traderName) {
		this.traderName = traderName;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getUnits() {
		return units;
	}

	public void setUnits(Integer units) {
		this.units = units;
	}

	public Double getAgreedFx() {
		return agreedFx;
	}

	public void setAgreedFx(Double agreedFx) {
		this.agreedFx = agreedFx;
	}

	public Character getBuySellFlag() {
		return buySellFlag;
	}

	public void setBuySellFlag(Character buySellFlag) {
		this.buySellFlag = buySellFlag;
	}

	public String getInstructionDate() {
		return instructionDate;
	}

	public void setInstructionDate(String instructionDate) {
		this.instructionDate = instructionDate;
	}

	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;

	}

	public LocalDate getSettlementDate(String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		LocalDate localDate = LocalDate.parse(this.settlementDate, formatter);
		localDate = getSettlementDate(localDate);
		return localDate;
	}

	public String getSettlementDate() {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		LocalDate localDate = LocalDate.parse(this.settlementDate, formatter);
		localDate = getSettlementDate(localDate);
		return LocalDateTime.of(localDate, LocalTime.NOON).format(formatter);

	}

	public Double getAmountOfTrade() {
		return this.unitPrice * this.units * this.agreedFx;
	}

	@Override
	public String toString() {
		return "[Trader Name : " + this.getTraderName() + " Amount: " + this.getAmountOfTrade() + " Buy/Sell Flag: "
				+ this.getBuySellFlag() + " AgreedFx: " + this.getAgreedFx() + " Currency: " + this.getCurrency()
				+ " Instruction Date: " + this.getInstructionDate() + " Settlement Date: " + this.getSettlementDate()
				+ " Units: " + this.getUnits() + " Unit Price: " + this.getUnitPrice() + "]";
	}

	private Boolean isWeekEnd(LocalDate localDate) {
		if (this.getCurrency().equals("AED") || this.getCurrency().equals("SAR")) {
			return EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.FRIDAY).contains(localDate.getDayOfWeek());
		} else {
			return EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(localDate.getDayOfWeek());
		}
	}

	private LocalDate getSettlementDate(LocalDate localDate) {
		while (isWeekEnd(localDate)) {
			localDate = localDate.plusDays(1);
		}
		return localDate;
	}

}
