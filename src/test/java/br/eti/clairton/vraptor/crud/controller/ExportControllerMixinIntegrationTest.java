package br.eti.clairton.vraptor.crud.controller;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletResponse;

import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.requestflow.UserFlow;

@RunWith(VRaptorRunner.class)
public class ExportControllerMixinIntegrationTest extends ControllerIntegration {

	@Test
	public void testExport() throws UnsupportedEncodingException {
		authenticate("admin", "123456");
		List<String> formats = Arrays.asList("csv", "pdf");
		for (String format : formats) {
			final UserFlow flow = navigate().get("/aplicacoes."+format, parameters);
			final VRaptorTestResult result = flow.execute();
			assertEquals(200, result.getResponse().getStatus());
			final MockHttpServletResponse response = result.getResponse();
			assertEquals(200, response.getStatus());
			if (format.equals("csv")) {
				assertEquals("qwerty", response.getContentAsString());
			}

		}

	}	
	
	//@Test
	public void testUnauthenticated() throws UnsupportedEncodingException {
		token = "dgsdgdgdggdsgdsdsg";
		List<String> formats = Arrays.asList("csv", "pdf");
		for (String format : formats) {
			final UserFlow flow = navigate().get("/aplicacoes."+format, parameters);
			final VRaptorTestResult result = flow.execute();
			assertEquals(401, result.getResponse().getStatus());

		}

	}
}
