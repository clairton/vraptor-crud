package br.eti.clairton.vraptor.crud.security;

import javax.security.auth.login.CredentialNotFoundException;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SessionControllerTest {
	private SessionController controller;
	private final String user = "admin";
	private final String password = "123456";
	private Authenticator authenticator = new AuthenticatorInMemory();
	final TokenManager tokenManager = new TokenManagerInMemory(authenticator);
	private HttpServletResponse response;

	@Before
	public void setUp() {
		response = Mockito.mock(HttpServletResponse.class);
		controller = new SessionController(tokenManager, response);
	}

	@Test(expected = CredentialNotFoundException.class)
	public void testCreateInValidPassword() throws CredentialNotFoundException {
		controller.create(user, "abcdef");
	}

	@Test(expected = CredentialNotFoundException.class)
	public void testCreateInValidUser() throws CredentialNotFoundException {
		controller.create("outrouser", password);
	}

	@Test
	public void testCreateValid() throws CredentialNotFoundException {
		controller.create(user, password);
	}

	@Test
	public void testDestroy() throws CredentialNotFoundException {
		controller.create(user, password);
		controller.destroy(user);
	}

}
