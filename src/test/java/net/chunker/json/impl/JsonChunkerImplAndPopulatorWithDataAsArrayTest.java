package net.chunker.json.impl;

import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.chunker.json.impl.CdDataAsListsChunkFactoryImpl;
import com.chunker.model.CatalogWithDataAsLists;
import com.chunker.model.CdDataAsLists;

import net.chunker.json.api.JsonChunkFactory;

/**
 * @author rollsroyas@alumni.ncsu.edu
 *
 */
public class JsonChunkerImplAndPopulatorWithDataAsArrayTest extends BaseJsonChunkerImplAndPopulatorTest<CatalogWithDataAsLists> {
	
	protected int getSize(CatalogWithDataAsLists catalog) {
		List<CdDataAsLists> cds = catalog.getCds();			
		return cds.size();
	}

	protected JsonChunkFactory<CatalogWithDataAsLists> createFactory() throws JAXBException {
		JsonChunkFactory<CatalogWithDataAsLists> factory = new CdDataAsListsChunkFactoryImpl();
		return factory;
	}

	protected InputStream getInputStream() {
		return JsonChunkerImplAndPopulatorTest.class.getResourceAsStream("/json/cd_catalog_data_as_arrays.json");
	}
}
