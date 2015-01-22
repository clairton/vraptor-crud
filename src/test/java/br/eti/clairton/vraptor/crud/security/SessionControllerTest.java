package br.eti.clairton.vraptor.crud.security;

import static br.com.caelum.vraptor.controller.HttpMethod.DELETE;
import static br.com.caelum.vraptor.controller.HttpMethod.POST;
import static br.eti.clairton.vraptor.crud.VRaptorRunner.navigate;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.security.auth.login.CredentialNotFoundException;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.http.Parameters;
import br.com.caelum.vraptor.test.requestflow.UserFlow;
import br.eti.clairton.vraptor.crud.VRaptorRunner;

import com.google.gson.GsonBuilder;

@RunWith(VRaptorRunner.class)
public class SessionControllerTest extends AbstractLdapTest {
	private String user = "admin";
	private String password = "123456";
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

	@Test
	public void testCreateInValidPassword() {
		password = "abcdef";
		final UserFlow userFlow = navigate().to(url, POST, parameters);
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(401, result.getResponse().getStatus());
		assertEquals(
				"{\"errors\":{\"error\":[\"Usuário/Senha não existe(m)!\"]}}",
				result.getResponseBody());

	}

	@Test
	public void testCreateInValidUser() {
		user = "outrouser";
		final UserFlow userFlow = navigate().to(url, POST, parameters);
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(401, result.getResponse().getStatus());
		assertEquals(
				"{\"errors\":{\"error\":[\"Usuário/Senha não existe(m)!\"]}}",
				result.getResponseBody());
	}

	@Test
	public void testCreateValid() throws CredentialNotFoundException,
			IOException {
		final UserFlow userFlow = navigate().to(url, POST, parameters);
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(201, result.getResponse().getStatus());
	}

	@Test
	public void testDestroyWithToken() throws CredentialNotFoundException,
			IOException, IOException {
		UserFlow userFlow = navigate().to(url, POST, parameters);
		VRaptorTestResult result = userFlow.execute();
		final String token = new GsonBuilder().create()
				.fromJson(result.getResponseBody(), Map.class).get("token")
				.toString();
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
		userFlow = navigate().to(url, DELETE, parameters);
		result = userFlow.execute();
		assertEquals(200, result.getResponse().getStatus());
	}

	@Test
	public void testDestroyWithUser() throws CredentialNotFoundException,
			IOException, IOException {
		UserFlow userFlow = navigate().to(url, POST, parameters);
		VRaptorTestResult result = userFlow.execute();
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
		userFlow = navigate().to(url, DELETE, parameters);
		result = userFlow.execute();
		assertEquals(200, result.getResponse().getStatus());
	}

}
