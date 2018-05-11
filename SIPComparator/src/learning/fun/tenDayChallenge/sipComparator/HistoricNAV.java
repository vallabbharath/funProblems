package learning.fun.tenDayChallenge.sipComparator;

import java.security.InvalidParameterException;
import java.util.Date;

public class HistoricNAV {

	//Can remove this date input for performance, as we maintain date in hashmap key.
	private Date date;
	private Double salePrice;
	private Double repurchasePrice;
	private Double nav;

	public HistoricNAV(Double nav, Double repurchasePrice, Double salePrice,
			Date date) {
		if(nav == null || repurchasePrice == null || salePrice == null || date == null) {
			throw new InvalidParameterException();
		}
		this.setNav(nav);
		this.setRepurchasePrice(repurchasePrice);
		this.setSalePrice(salePrice);
		this.setDate(date);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Double salePrice) {
		this.salePrice = salePrice;
	}

	public Double getRepurchasePrice() {
		return repurchasePrice;
	}

	public void setRepurchasePrice(Double repurchasePrice) {
		this.repurchasePrice = repurchasePrice;
	}

	public Double getNav() {
		return nav;
	}

	public void setNav(Double nav) {
		this.nav = nav;
	}
	
	@Override
	public String toString() {
		return "\nNAV = " + nav + "; salePrice = " + salePrice + "; repurchasePrice = "
					+ repurchasePrice + "; Date = " + date;
	}
}
