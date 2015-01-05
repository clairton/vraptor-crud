package br.eti.clairton.vraptor.crud.security;

import static br.com.caelum.vraptor.controller.HttpMethod.DELETE;
import static br.com.caelum.vraptor.controller.HttpMethod.POST;
import static br.eti.clairton.vraptor.crud.CdiJUnit4Runner.navigate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.inject.Inject;
import javax.security.auth.login.CredentialNotFoundException;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.http.Parameters;
import br.com.caelum.vraptor.test.requestflow.UserFlow;
import br.eti.clairton.vraptor.crud.CdiJUnit4Runner;

@RunWith(CdiJUnit4Runner.class)
public class SessionControllerTest extends AbstractLdapTest {
	private String user = "admin";
	private String password = "123456";
	private @Inject TokenManager tokenManager;
	private @Inject Mirror mirror;
	private final String url = "/sessions";

	private Parameters parameters = new Parameters() {
		@Override
		public void fill(final MockHttpServletRequest request) {
			final String json = String.format(
					"{'user': '%s', 'password': '%s'}", user, password);
			mirror.on(request).set().field("content")
					.withValue(json.getBytes());
			mirror.on(request).set().field("contentType")
					.withValue("application/json");
		}
	};

	@Before
	public void setUp() throws IOException {
	}

	@Test
	public void testCreateInValidPassword() {
		password = "abcdef";
		final UserFlow userFlow = navigate().to(url, POST, parameters);
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(401, result.getResponse().getStatus());
		assertEquals("{\"errors\":\"Usuário/Senha não existe(m)!\"}",
				result.getResponseBody());

	}

	@Test
	public void testCreateInValidUser() {
		user = "outrouser";
		final UserFlow userFlow = navigate().to(url, POST, parameters);
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(401, result.getResponse().getStatus());
		assertEquals("{\"errors\":\"Usuário/Senha não existe(m)!\"}",
				result.getResponseBody());
	}

	@Test
	public void testCreateValid() throws CredentialNotFoundException,
			IOException {
		final UserFlow userFlow = navigate().to(url, POST, parameters);
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(200, result.getResponse().getStatus());
		assertNotNull(result.getResponseBody());
	}

	@Test
	public void testDestroyWithToken() throws CredentialNotFoundException,
			IOException, IOException {
		final String token = tokenManager.create(user, password);
		assertTrue(tokenManager.isValid(token));
		final Parameters parameters = new Parameters() {
			@Override
			public void fill(final MockHttpServletRequest request) {
				final String json = String.format("{'key': '%s'}", token);
				mirror.on(request).set().field("content")
						.withValue(json.getBytes());
				mirror.on(request).set().field("contentType")
						.withValue("application/json");
			}
		};
		final UserFlow userFlow = navigate().to(url, DELETE, parameters);
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(200, result.getResponse().getStatus());
		assertFalse(tokenManager.isValid(token));
	}

	@Test
	public void testDestroyWithUser() throws CredentialNotFoundException,
			IOException, IOException {
		final String token = tokenManager.create(user, password);
		assertTrue(tokenManager.isValid(token));
		final Parameters parameters = new Parameters() {
			@Override
			public void fill(final MockHttpServletRequest request) {
				final String json = String.format("{'key': '%s'}", user);
				mirror.on(request).set().field("content")
						.withValue(json.getBytes());
				mirror.on(request).set().field("contentType")
						.withValue("application/json");
			}
		};
		final UserFlow userFlow = navigate().to(url, DELETE, parameters);
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(200, result.getResponse().getStatus());
		assertFalse(tokenManager.isValid(token));
	}
}
