package br.eti.clairton.vraptor.crud.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import br.eti.clairton.vraptor.crud.model.Aplicacao;

public class ExportControllerMixinTest {
	private MockHttpServletResponse response;
	private ExportControllerMixin<Aplicacao> controller;

	@Before
	public void init() {
		response = new MockHttpServletResponse();
		controller = new AplicacaoController2(response);
	}

	@Test
	public void testExport() throws Exception {
		controller.export(".csv");
		final String content = response.getContentAsString();
		assertEquals("qwerty", content);
	}

}
