package br.eti.clairton.vraptor.crud.controller;

import static br.eti.clairton.vraptor.crud.controller.VRaptorRunner.navigate;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.http.Parameters;
import br.com.caelum.vraptor.test.requestflow.UserFlow;

@RunWith(VRaptorRunner.class)
public class PasswordControllerTest {
	@Test
	public void testUpdate() {
		Parameters parameters = new Parameters();
		parameters.add("user", "a");
		parameters.add("currentPassword", "b");
		parameters.add("newPassword", "c");
		final UserFlow userFlow = navigate().to("/passwords", HttpMethod.PUT, parameters);
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(204, result.getResponse().getStatus());
	}
}
