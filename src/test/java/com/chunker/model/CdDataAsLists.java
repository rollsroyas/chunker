package com.chunker.model;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * Contrived data to test JsonArrays
 * @author rollsroyas@alumni.ncsu.edu
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {})
public class CdDataAsLists {

	@XmlElement(name = "INFO")
	private List<String> infos;
	@XmlElement(name = "PRICE")
	private List<BigDecimal> prices;
	@XmlElement(name = "YEAR")
	private List<Integer> years;
	@XmlElement(name = "FAVORITE")
	private List<Boolean> favorites;

	public List<String> getInfos() {
		return infos;
	}

	public void setInfos(List<String> infos) {
		this.infos = infos;
	}

	public List<BigDecimal> getPrices() {
		return prices;
	}

	public void setPrices(List<BigDecimal> prices) {
		this.prices = prices;
	}

	public List<Integer> getYears() {
		return years;
	}

	public void setYears(List<Integer> years) {
		this.years = years;
	}

	public List<Boolean> getFavorites() {
		return favorites;
	}

	public void setFavorite(List<Boolean> favorites) {
		this.favorites = favorites;
	}

}
