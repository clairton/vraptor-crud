package br.eti.clairton.vraptor.crud.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import br.com.caelum.vraptor.util.test.MockSerializationResult;
import br.eti.clairton.vraptor.crud.model.Aplicacao;

public class ExportControllerMixinTest {
	private MockSerializationResult result;
	private MockHttpServletResponse response;
	private ExportControllerMixin<Aplicacao> controller;
	private final String file = "src/test/resources/test.csv";

	@Before
	public void init() {
		result = new MockSerializationResult();
		response = new MockHttpServletResponse();
		controller = new AplicacaoController2(result, response);

	}

	@Test
	public void testDownload() throws Exception {
		controller.download(file);
		final String content = response.getContentAsString();
		assertEquals("qwerty", content);
	}

	@Test
	public void testExport() throws Exception {
		controller.export();
		final String json = result.serializedResult();
		assertEquals("{\"file_path\":\"" + file + "\"}", json);
	}

}
