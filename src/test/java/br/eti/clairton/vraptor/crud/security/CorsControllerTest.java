package br.eti.clairton.vraptor.crud.security;

import static br.com.caelum.vraptor.controller.HttpMethod.OPTIONS;
import static br.eti.clairton.vraptor.crud.controller.VRaptorRunner.navigate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.http.Parameters;
import br.com.caelum.vraptor.test.requestflow.UserFlow;
import br.eti.clairton.vraptor.crud.controller.VRaptorRunner;

@RunWith(VRaptorRunner.class)
public class CorsControllerTest {
	private final Parameters p = new Parameters();
	private final String h = "Access-Control-Allow-Methods";

	@Test
	public void testOptionsOnCreate() {
		final UserFlow userFlow = navigate().to("/aplicacoes", OPTIONS, p);
		final VRaptorTestResult result = userFlow.execute();
		final HttpServletResponse response = result.getResponse();
		assertEquals(204, response.getStatus());
		final String actual = response.getHeaders(h).iterator().next();
		assertEquals("GET, POST, OPTIONS", actual);
	}

	@Test
	public void testOptionsOnEdit() {
		final String url = "/aplicacoes/1001/edit";
		final UserFlow userFlow = navigate().to(url, OPTIONS, p);
		final VRaptorTestResult result = userFlow.execute();
		final HttpServletResponse response = result.getResponse();
		assertEquals(204, response.getStatus());
		final String actual = response.getHeaders(h).iterator().next();
		assertEquals("GET, OPTIONS", actual);
	}

	@Test
	public void testOptionsOnExist() {
		final UserFlow userFlow = navigate().to("/aplicacoes/1001", OPTIONS, p);
		final VRaptorTestResult result = userFlow.execute();
		final HttpServletResponse response = result.getResponse();
		assertEquals(204, response.getStatus());
		final String actual = response.getHeaders(h).iterator().next();
		List<String> methods = Arrays.asList(actual.split(", "));
		assertTrue(methods.contains("GET"));
		assertTrue(methods.contains("PUT"));
		assertTrue(methods.contains("DELETE"));
		assertTrue(methods.contains("OPTIONS"));
	}

	@Test
	public void testOptionsOnNew() {
		final UserFlow userFlow = navigate().to("/aplicacoes/new", OPTIONS, p);
		final VRaptorTestResult result = userFlow.execute();
		final HttpServletResponse response = result.getResponse();
		assertEquals(204, response.getStatus());
		final String actual = response.getHeaders(h).iterator().next();
		List<String> methods = Arrays.asList(actual.split(", "));
		assertTrue(methods.contains("GET"));
		assertTrue(methods.contains("OPTIONS"));
	}
}
