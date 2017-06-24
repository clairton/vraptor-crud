package br.eti.clairton.vraptor.crud.security;

import static br.com.caelum.vraptor.controller.HttpMethod.DELETE;
import static br.com.caelum.vraptor.controller.HttpMethod.POST;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.login.CredentialNotFoundException;
import javax.servlet.http.HttpServletResponse;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

import com.google.gson.GsonBuilder;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.VRaptorRequest;
import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.http.Parameters;
import br.com.caelum.vraptor.test.requestflow.UserFlow;
import br.com.caelum.vraptor.util.test.MockHttpServletResponse;
import br.eti.clairton.security.Locksmith;
import br.eti.clairton.vraptor.crud.controller.ControllerIntegration;

@RunWith(CdiTestRunner.class)
public class SessionControllerTest extends ControllerIntegration{
	private String user = "admin";
	private String password = "123456";
	private final String url = "/sessions";

	@Test
	public void testCreateInValidPassword() {
		password = "abcdef";
		final UserFlow userFlow = navigate()
									.to(url, POST, new Parameters())
									.addHeader("Content-Type", "application/json")
									.setContent(String.format("{'user': '%s', 'password': '%s'}", user, password));
		final VRaptorTestResult result = userFlow.execute();
		result.wasStatus(401);
		assertEquals("{\"errors\":{\"error\":[\"Usuário/Senha não existe(m)!\"]}}", result.getResponseBody());

	}

	@Test
	public void testCreateInValidUser() {
		user = "outrouser";
		final UserFlow userFlow = navigate()
									.to(url, POST, new Parameters())
									.addHeader("Content-Type", "application/json")
									.setContent(String.format("{'user': '%s', 'password': '%s'}", user, password));
		final VRaptorTestResult result = userFlow.execute();
		result.wasStatus(401);
		assertEquals("{\"errors\":{\"error\":[\"Usuário/Senha não existe(m)!\"]}}", result.getResponseBody());
	}

	@Test
	public void testCreateValid() throws CredentialNotFoundException,
			IOException {
		final UserFlow userFlow = navigate()
									.to(url, POST, new Parameters())
									.addHeader("Content-Type", "application/json")
									.setContent(String.format("{'user': '%s', 'password': '%s'}", user, password));
		final VRaptorTestResult result = userFlow.execute();
		result.wasStatus(201);
	}

	@Test
	public void testDestroyWithToken() throws CredentialNotFoundException,
			IOException, IOException {
		UserFlow userFlow = navigate()
								.to(url, POST, new Parameters())
								.addHeader("Content-Type", "application/json")
								.setContent(String.format("{'user': '%s', 'password': '%s'}", user, password));
		VRaptorTestResult result = userFlow.execute();
		final String token = new GsonBuilder().create()
				.fromJson(result.getResponseBody(), Map.class).get("token")
				.toString();
		userFlow = navigate().to(url, DELETE, new Parameters())
				.addHeader("Content-Type", "application/json")
				.setContent(String.format("{'key':'%s'}", token));
		result = userFlow.execute();
		result.wasStatus(204);
	}

	@Test
	public void testDestroyWithUser() throws CredentialNotFoundException,
			IOException, IOException {
		UserFlow userFlow = navigate()
								.to(url, POST, new Parameters())
								.addHeader("Content-Type", "application/json")
								.setContent(String.format("{'user': '%s', 'password': '%s'}", user, password));
		VRaptorTestResult result = userFlow.execute();
		userFlow = navigate()
					.to(url, DELETE, new Parameters())
					.addHeader("Content-Type", "application/json")
					.setContent(String.format("{'key':'%s'}", user));
		result = userFlow.execute();
		result.wasStatus(204);
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
