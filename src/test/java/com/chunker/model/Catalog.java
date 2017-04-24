package com.chunker.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

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
