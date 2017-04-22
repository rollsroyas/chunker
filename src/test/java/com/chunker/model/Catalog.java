package com.chunker.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {})
@XmlRootElement(name = "CATALOG")
public class Catalog {

	@XmlElement(name = "CD")
	protected List<Cd> cd;

	public List<Cd> getCd() {
		if (cd == null) {
			cd = new ArrayList<>();
		}
		return this.cd;
	}

}
