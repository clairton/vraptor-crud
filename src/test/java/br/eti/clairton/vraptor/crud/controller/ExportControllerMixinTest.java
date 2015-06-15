package br.eti.clairton.vraptor.crud.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.util.test.MockSerializationResult;
import br.eti.clairton.vraptor.crud.model.Aplicacao;

public class ExportControllerMixinTest {
	private MockSerializationResult result;
	private ExportControllerMixin<Aplicacao> controller;
	private final String file = "test.csv";

	@Before
	public void init() {
		result = new MockSerializationResult();
		controller = new AplicacaoController2(result);
	}

	@Test
	public void testExport() throws Exception {
		controller.export("csv");
		final String json = result.serializedResult();
		assertEquals("{\"file_path\":\"" + file + "\"}", json);
	}

}
