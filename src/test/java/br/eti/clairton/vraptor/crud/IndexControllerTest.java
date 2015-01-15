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
		assertEquals(302, result.getResponse().getStatus());
	}

	@Test
	public void testCss() {
		final UserFlow userFlow = navigate().get("/assets/vendor.css");
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(302, result.getResponse().getStatus());
	}

	@Test
	public void testJs() {
		final UserFlow userFlow = navigate().get("/assets/vendor.js");
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(302, result.getResponse().getStatus());
	}

	@Test
	public void testFonts() {
		final UserFlow userFlow = navigate().get("/fonts/glyphicons-halflings-regular.svg");
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(302, result.getResponse().getStatus());
	}
}
