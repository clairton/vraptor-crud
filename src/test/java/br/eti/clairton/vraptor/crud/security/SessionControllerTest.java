package br.eti.clairton.vraptor.crud.security;

import static br.com.caelum.vraptor.controller.HttpMethod.DELETE;
import static br.com.caelum.vraptor.controller.HttpMethod.POST;
import static br.eti.clairton.vraptor.crud.controller.VRaptorRunner.navigate;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.security.auth.login.CredentialNotFoundException;
import javax.servlet.http.HttpServletResponse;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.VRaptorRequest;
import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.http.Parameters;
import br.com.caelum.vraptor.test.requestflow.UserFlow;
import br.com.caelum.vraptor.util.test.MockHttpServletResponse;
import br.eti.clairton.security.Locksmith;
import br.eti.clairton.vraptor.crud.controller.VRaptorRunner;

import com.google.gson.GsonBuilder;

@RunWith(VRaptorRunner.class)
public class SessionControllerTest {
	private String user = "admin";
	private String password = "123456";
	private @Inject Mirror mirror;
	private final String url = "/sessions";

	private Parameters parameters = new Parameters() {
		@Override
		public void fill(final MockHttpServletRequest request) {
			final String json = String.format("{'user': '%s', 'password': '%s'}", user, password);
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
		assertEquals("{\"errors\":{\"error\":[\"Usuário/Senha não existe(m)!\"]}}", result.getResponseBody());

	}

	@Test
	public void testCreateInValidUser() {
		user = "outrouser";
		final UserFlow userFlow = navigate().to(url, POST, parameters);
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(401, result.getResponse().getStatus());
		assertEquals("{\"errors\":{\"error\":[\"Usuário/Senha não existe(m)!\"]}}", result.getResponseBody());
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

	@Test
	public void testCreateParam() throws CredentialNotFoundException {
		final MutableRequest request = new VRaptorRequest(new MockHttpServletRequest());
		final Result result = mock(Result.class);
		final HttpServletResponse response = new MockHttpServletResponse();
		final Locksmith locksmith = mock(Locksmith.class);
		final String token = "987ytrdfasjkdflgçh";
		when(locksmith.create(anyString(), anyString())).thenReturn(token);
		final SessionController controller = new SessionController(locksmith,response, request, result);
		controller.create("abc", "123");
		assertEquals("Bearer "+token, request.getParameter("Authorization"));
	}
}
