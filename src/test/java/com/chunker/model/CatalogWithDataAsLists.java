package com.chunker.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Contrived data to test JsonArrays
 * @author rollsroyas@alumni.ncsu.edu
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {})
@XmlRootElement(name = "CATALOG")
public class CatalogWithDataAsLists {

	@XmlElement(name = "CD")
	private List<CdDataAsLists> cds;

	public List<CdDataAsLists> getCds() {
		if (cds == null) {
			cds = new ArrayList<>();
		}
		return this.cds;
	}

}
