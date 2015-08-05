package br.eti.clairton.vraptor.crud.serializer;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import javax.inject.Inject;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.eti.clairton.vraptor.crud.model.Aplicacao;

@RunWith(CdiTestRunner.class)
public class DefaultTagableExtratorTest {
	private @Inject TagableExtractor extractor;

	@Test
	public void testExtractSingle() {
		assertEquals("aplicacao", extractor.extract(new Aplicacao()));
	}

	@Test
	public void testExtractCollection() {
		assertEquals("aplicacoes", extractor.extract(Arrays.asList(new Aplicacao())));
	}

}
