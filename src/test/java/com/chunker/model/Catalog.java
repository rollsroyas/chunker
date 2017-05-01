package com.chunker.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {})
@XmlRootElement(name = "CATALOG")
public class Catalog {

	@XmlElement(name = "CD")
	private List<Cd> cds;

	public List<Cd> getCds() {
		if (cds == null) {
			cds = new ArrayList<>();
		}
		return this.cds;
	}

}
