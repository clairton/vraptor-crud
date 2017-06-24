package br.eti.clairton.vraptor.crud.controller;

import static br.com.caelum.vraptor.controller.HttpMethod.PUT;
import static org.junit.Assert.assertEquals;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.http.Parameters;
import br.com.caelum.vraptor.test.requestflow.UserFlow;

@RunWith(CdiTestRunner.class)
public class PasswordControllerTest extends ControllerIntegration {
	@Test
	public void testUpdate() {
		Parameters parameters = new Parameters();
		parameters.add("user", "a");
		parameters.add("currentPassword", "b");
		parameters.add("newPassword", "c");
		final UserFlow userFlow = navigate().to("/passwords", PUT, parameters);
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(204, result.getResponse().getStatus());
	}
}
