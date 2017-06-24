package br.eti.clairton.vraptor.crud.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import br.eti.clairton.vraptor.crud.model.Aplicacao;

public class CrudeControllerTest {
	private MockHttpServletResponse response;
	private CrudeController<Aplicacao> controller;
	private FileService service;

	@Before
	public void init() {
		response = new MockHttpServletResponse();
		service = new FileServiceMock();
		controller = new AplicacaoController2(Aplicacao.class, null, null, null, null, null, response, service);
	}

	@Test
	public void testExport() throws Exception {
		controller.export(".csv");
		final String content = response.getContentAsString();
		assertEquals("qwerty", content);
	}

}
