package net.chunker.json.event;

import static net.chunker.json.event.NamedEventFactory.namedEventFactory;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.json.stream.JsonParser.Event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import net.chunker.json.api.JsonEvent;

/**
 * @author rollsroyas@alumni.ncsu.edu
 */
public final class NamedEventFactoryTest {

	@Mock
	JsonEvent jsonEvent;
	
	@Before
	public void begin() {
		initMocks(this);
	}

	@Test
	public void factory() {
		NamedEventFactory factory = namedEventFactory();
		assertEquals("Expecting to calls to namedEventFactory() to return same object",
		             factory, 
		             namedEventFactory());
	}
	
	@Test
	public void create_START_ARRAY() {
		createNamedEventForEventThenAssertInstanceOfClass(Event.START_ARRAY, NamedStartArrayEvent.class);
	}

	public NamedEvent createNamedEventForEventThenAssertInstanceOfClass(Event event, Class<? extends NamedEvent> clazz) {
		NamedEvent namedEvent = createNamedEventForEvent(event);
		assertObjectInstanceOfClass(namedEvent, clazz);
		return namedEvent;
	}

	public void assertObjectInstanceOfClass(Object o, Class<?> clazz) {
		assertNotNull(o);
		assertEquals(clazz, o.getClass());
	}

	public NamedEvent createNamedEventForEvent(Event event) {
		when(jsonEvent.getEvent()).thenReturn(event);
		NamedEvent namedEvent = namedEventFactory().create(jsonEvent, "currentName");
		return namedEvent;
	}
	
	@Test
	public void create_START_OBJECT() {
		createNamedEventForEventThenAssertInstanceOfClass(Event.START_OBJECT, NamedStartObjectEvent.class);
	}
	
	@Test
	public void create_END_ARRAY() {
		createNamedEventForEventThenAssertInstanceOfClass(Event.END_ARRAY, NamedEndEvent.class);
	}

	@Test
	public void create_END_OBJECT() {
		createNamedEventForEventThenAssertInstanceOfClass(Event.END_OBJECT, NamedEndEvent.class);
	}	

	@Test
	public void create_VALUE_FALSE() {
		createNamedEventForEventThenAssertInstanceOfClass(Event.VALUE_FALSE, NamedFalseEvent.class);
	}

	@Test
	public void create_VALUE_TRUE() {
		createNamedEventForEventThenAssertInstanceOfClass(Event.VALUE_TRUE, NamedTrueEvent.class);
	}

	@Test
	public void create_VALUE_NULL() {
		createNamedEventForEventThenAssertInstanceOfClass(Event.VALUE_NULL, NamedNullEvent.class);
	}

	@Test
	public void create_VALUE_NUMBER() {
		createNamedEventForEventThenAssertInstanceOfClass(Event.VALUE_NUMBER, NamedNumberEvent.class);
	}
	
	@Test
	public void create_VALUE_STRING() {
		createNamedEventForEventThenAssertInstanceOfClass(Event.VALUE_STRING, NamedStringEvent.class);
	}

	@Test(expected=IllegalStateException.class)
	public void create_KEY_NAME() {
		createNamedEventForEvent(Event.KEY_NAME);
	}

}