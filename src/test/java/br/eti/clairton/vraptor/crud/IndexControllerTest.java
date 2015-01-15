package br.eti.clairton.vraptor.crud;

import static br.eti.clairton.vraptor.crud.CdiJUnit4Runner.navigate;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.requestflow.UserFlow;

@RunWith(CdiJUnit4Runner.class)
public class IndexControllerTest {
	@Test
	public void testIndex() {
		final UserFlow userFlow = navigate().get("/");
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(200, result.getResponse().getStatus());
	}

}
