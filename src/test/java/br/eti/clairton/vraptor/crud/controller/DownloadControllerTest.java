package br.eti.clairton.vraptor.crud.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import br.eti.clairton.vraptor.crud.controller.DownloadController;

public class DownloadControllerTest {
	private MockHttpServletResponse response;
	private DownloadController controller;
	private final String file = "test.csv";

	@Before
	public void init() {
		response = new MockHttpServletResponse();
		controller = new DownloadController(response, new FileServiceMock());
	}

	@Test
	public void testGet() throws Exception {
		controller.get(file);
		final String content = response.getContentAsString();
		assertEquals("qwerty", content);
	}
}
