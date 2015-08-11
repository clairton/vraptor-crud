package br.eti.clairton.vraptor.crud.serializer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Locale;
import br.eti.clairton.jpa.serializer.Tagable;

public class TagableTest {
	private final String resource = "sdyurwqr" + new Date().getTime();
	private final Inflector inflector = Inflector.getForLocale(Locale.pt_BR);
	private final Tagable<String> tagable = new br.eti.clairton.vraptor.crud.serializer.Tagable<String>(inflector){
		private static final long serialVersionUID = 1L;

		@Override
		public String getResource() {
			return resource;
		}
	};

	@Test
	public void testGetRootTag() {
		assertEquals("string", tagable.getRootTag(""));
	}

	@Test
	public void testGetRootTagNullObject() {
		assertEquals(resource, tagable.getRootTag(null));
	}

	@Test
	public void testGetRootTagCollection() {
		assertEquals("strings", tagable.getRootTagCollection(Arrays.asList("abc")));
	}

	@Test
	public void testGetRootTagInEmptyCollection() {
		assertEquals(resource+"s", tagable.getRootTagCollection(new ArrayList<String>()));
	}

	@Test
	public void testPluralize() {
		inflector.addUncountable("lápis");
		assertEquals("corações", tagable.pluralize("coração"));
		assertEquals("lápis", tagable.pluralize("lápis"));
		assertEquals("sons", tagable.pluralize("som"));
	}

}
