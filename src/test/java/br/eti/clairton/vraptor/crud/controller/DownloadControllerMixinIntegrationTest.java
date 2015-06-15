package br.eti.clairton.vraptor.crud.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.requestflow.UserFlow;

@RunWith(VRaptorRunner.class)
public class DownloadControllerMixinIntegrationTest extends ControllerIntegration{

	@Test
	public void testDownalodFile() throws Exception {
		authenticate("admin", "123456");
		final UserFlow flow = navigate().get("/downloads/test.csv", parameters);
		final VRaptorTestResult result = flow.execute();
		//test only is path in accessible
		assertEquals(401, result.getResponse().getStatus());
	}
}
