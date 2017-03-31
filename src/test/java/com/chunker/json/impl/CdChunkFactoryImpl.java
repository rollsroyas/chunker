package com.chunker.json.impl;

import java.util.concurrent.Callable;

import javax.xml.bind.JAXBException;

import com.chunker.model.Catalog;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.chunker.json.api.JsonChunkFactory;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class CdChunkFactoryImpl implements JsonChunkFactory<Catalog> {
	private ObjectMapper mapper;
	
	public CdChunkFactoryImpl() throws JAXBException {
		mapper = new ObjectMapper();
		mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);  
	}

	@Override
	public Callable<Catalog> create(final String chunk, boolean last) {
		return new Callable<Catalog>() {
			public Catalog call() throws Exception {
				return mapper.readValue(chunk, Catalog.class );
			}
		};
	}
}
