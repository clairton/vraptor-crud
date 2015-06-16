package br.eti.clairton.vraptor.crud.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletResponse;

import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.requestflow.UserFlow;

@RunWith(VRaptorRunner.class)
public class DownloadControllerMixinIntegrationTest{

	@Test
	public void testDownalodFile() throws Exception {
		final UserFlow flow = VRaptorRunner.navigate().get("/downloads/test.csv");
		final VRaptorTestResult result = flow.execute();
		final MockHttpServletResponse response = result.getResponse();
		assertEquals(200, response.getStatus());
		assertEquals("qwerty", response.getContentAsString());
	}
}
