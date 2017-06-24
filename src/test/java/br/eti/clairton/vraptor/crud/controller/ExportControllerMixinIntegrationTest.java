package br.eti.clairton.vraptor.crud.controller;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletResponse;

import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.requestflow.UserFlow;

@RunWith(CdiTestRunner.class)
public class ExportControllerMixinIntegrationTest extends ControllerIntegration {

	@Test
	public void testExport() throws UnsupportedEncodingException {
		authenticate("admin", "123456");
		List<String> formats = Arrays.asList("csv", "pdf");
		for (String format : formats) {
			final UserFlow flow = navigate()
										.get("/aplicacoes."+format)
										.addHeader("Content-Type", "application/json")
										.addHeader("Authorization", token());
			final VRaptorTestResult result = flow.execute();
			result.wasStatus(200);
			final MockHttpServletResponse response = result.getResponse();
			result.wasStatus(200);
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
			final UserFlow flow = navigate()
									.get("/aplicacoes."+format)
									.addHeader("Content-Type", "application/json")
									.addHeader("Authorization", token());
			final VRaptorTestResult result = flow.execute();
			assertEquals(401, result.getResponse().getStatus());

		}

	}
}
