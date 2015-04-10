package br.eti.clairton.vraptor.crud.hypermedia;

import static br.eti.clairton.vraptor.crud.hypermedia.CurrentResource.getResource;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Locale;

public class CurrentMethodProducerTest {
	private Inflector inflector = Inflector.getForLocale(Locale.pt_BR);

	@Test
	public void testGetResourceOnly() {
		assertEquals("aplicacao", getResource("/aplicacoes", inflector));
	}

	@Test
	public void testGetResourcePathParam() {
		assertEquals("aplicacao", getResource("/aplicacoes/123", inflector));
	}

	@Test
	public void testGetResourceQueryParam() {
		assertEquals("aplicacao",
				getResource("/aplicacoes?per_page=1", inflector));
	}

	@Test
	public void testGetResourceWithoutFirstSlash() {
		assertEquals("aplicacao",
				getResource("aplicacoes?per_page=1", inflector));
	}

	// @Test
	// public void testGetResourceNested() {
	// assertEquals("aplicacao", getResource("/another/aplicacoes", inflector));
	// }
	//
	// @Test
	// public void testGetResourceNestedPathParam() {
	// assertEquals("aplicacao",
	// getResource("/another/aplicacoes/123", inflector));
	// }
	//
	// @Test
	// public void testGetResourceNestedPathParamAndQueryParam() {
	// assertEquals(
	// "aplicacao",
	// getResource("/another/aplicacoes/123?page=1&per_page=10",
	// inflector));
	// }
}
