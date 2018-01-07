package banoun.aneece.model;

public class CurrencyExchange {

	private String time;
	private String currency;
	private String rate;
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	@Override
	public String toString() {
		return "CurrencyExchange{" + "time=" + time + ", currency=" + currency + ", rate=" + rate + ", description="
				+ description + '}';
	}

}
