package banoun.aneece.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tradeEntry")
public class TradeEntry{

    @Id
    private String _id;
	@DBRef
	private Trader trader;
	private String currency;
	private Double unitPrice;
	private Integer units;
	private Double agreedFx;
	private Double amount;
	private Character buySellFlag;
	private LocalDate settlementDate;
	private LocalDate instructionDate;

	public TradeEntry(){
		super();		
	}
	
	public TradeEntry(Trader trader, String currency, Double unitPrice, Integer units, Double agreedFx,
			Character buySellFlag, LocalDate settlementDate, LocalDate instructionDate) {
		super();
		this.trader = trader;
		this.currency = currency;
		this.unitPrice = unitPrice;
		this.units = units;
		this.agreedFx = agreedFx;
		this.buySellFlag = buySellFlag;
		setSettlementDate(settlementDate);
		setInstructionDate(instructionDate);
	}

	public LocalDate getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(LocalDate settlementDate) {
		this.settlementDate = getAdjustedSettlementDate(settlementDate);
	}

	public LocalDate getInstructionDate() {
		return instructionDate;
	}

	public void setInstructionDate(LocalDate instructionDate) {
		this.instructionDate = instructionDate;
	}

	public String getId() {
		return _id;
	}

	public void setId(String id) {
		this._id = id;
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

	public void setAmount(Double amount) {
		this.amount = this.unitPrice * this.units * this.agreedFx; 
	}
	
	public Double getAmount() {
		this.amount = this.unitPrice * this.units * this.agreedFx;
		return this.amount;
	}
	
	public Trader getTrader() {
		return trader;
	}

	public void setTrader(Trader trader) {
		this.trader = trader;
	}

	@Override
	public String toString() {
		return "[Trader Name : " + this.getTrader().getName() + " Amount: " + this.getAmount() + " Buy/Sell Flag: "
				+ this.getBuySellFlag() + " AgreedFx: " + this.getAgreedFx() + " Currency: " + this.getCurrency()
				+ " Instruction Date: " + this.instructionDate + " Settlement Date: " + this.settlementDate
				+ " Units: " + this.getUnits() + " Unit Price: " + this.getUnitPrice() + "]";
	}

	private Boolean isWeekEnd(LocalDate localDate) {
		if (this.getCurrency().equals("AED") || this.getCurrency().equals("SAR")) {
			return EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.FRIDAY).contains(localDate.getDayOfWeek());
		} else {
			return EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(localDate.getDayOfWeek());
		}
	}

	private LocalDate getAdjustedSettlementDate(LocalDate localDateIn) {
		LocalDate localDate = LocalDate.ofEpochDay(localDateIn.toEpochDay());
		while (isWeekEnd(localDate)) {
			localDate = localDate.plusDays(1);
		}
		return localDate;
	}
}
