package com.chunker.json.impl;

import java.util.concurrent.Callable;

import javax.xml.bind.JAXBException;

import com.chunker.model.CatalogWithDataAsLists;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.chunker.json.api.JsonChunkFactory;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public class CdDataAsListsChunkFactoryImpl implements JsonChunkFactory<CatalogWithDataAsLists> {
	private ObjectMapper mapper;

	public CdDataAsListsChunkFactoryImpl() throws JAXBException {
		mapper = new ObjectMapper();
		mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
	}

	@Override
	public Callable<CatalogWithDataAsLists> create(final String chunk, boolean last) {
		return new Callable<CatalogWithDataAsLists>() {
			public CatalogWithDataAsLists call() throws Exception {
				return mapper.readValue(chunk, CatalogWithDataAsLists.class);
			}
		};
	}
}
