package com.chunker.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={})
@XmlRootElement(name = "CD")
public class Cd {

    @XmlElement(name = "TITLE")
    protected String title;
    @XmlElement(name = "ARTIST")
    protected String artist;
    @XmlElement(name = "COUNTRY")
    protected String country;
    @XmlElement(name = "COMPANY")
    protected String company;
    @XmlElement(name = "PRICE")
    protected BigDecimal price;
    @XmlElement(name = "YEAR")
    protected Integer year;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}

}
