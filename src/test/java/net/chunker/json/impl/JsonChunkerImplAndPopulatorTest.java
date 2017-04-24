package net.chunker.json.impl;

import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.chunker.json.impl.CdChunkFactoryImpl;
import com.chunker.model.Catalog;
import com.chunker.model.Cd;

import net.chunker.json.api.JsonChunkFactory;

/**
 * @author rollsroyas@alumni.ncsu.edu
 *
 */
public class JsonChunkerImplAndPopulatorTest extends BaseJsonChunkerImplAndPopulatorTest<Catalog> {
	
	protected int getSize(Catalog catalog) {
		List<Cd> cds = catalog.getCds();			
		int size = cds.size();
		return size;
	}

	protected JsonChunkFactory<Catalog> createFactory() throws JAXBException {
		JsonChunkFactory<Catalog> factory = new CdChunkFactoryImpl();
		return factory;
	}

	protected InputStream getInputStream() {
		return JsonChunkerImplAndPopulatorTest.class.getResourceAsStream("/json/cd_catalog.json");
	}
}
