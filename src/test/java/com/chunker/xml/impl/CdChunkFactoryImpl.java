package com.chunker.xml.impl;

import java.io.StringReader;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.chunker.model.Catalog;

import net.chunker.xml.api.XmlChunkFactory;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class CdChunkFactoryImpl implements XmlChunkFactory<Catalog> {
	private Unmarshaller unmashaller;
	
	public CdChunkFactoryImpl() throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(Catalog.class);
		this.unmashaller = jc.createUnmarshaller();
	}

	@Override
	public Callable<Catalog> create(final String chunk, boolean last) {
		return new Callable<Catalog>() {
			public Catalog call() throws Exception {
				StringReader reader = new StringReader(chunk);
				return (Catalog)unmashaller.unmarshal( reader );
			}
		};
	}
}
