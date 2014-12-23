package br.eti.clairton.vraptor.crud.security;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import javax.security.auth.login.CredentialNotFoundException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.util.test.MockHttpServletResponse;

public class SessionControllerTest {
	private final File file = new File("target/token.txt");
	private SessionController controller;
	private final String user = "admin";
	private final String password = "123456";
	private Authenticator authenticator = new AuthenticatorInMemory();
	final TokenManager tokenManager = new TokenManagerInMemory(authenticator);
	private HttpServletResponse response;

	@Before
	public void setUp() throws IOException {
		Files.deleteIfExists(file.toPath());
		final FileOutputStream out = new FileOutputStream(file);
		final PrintWriter printWriter = new PrintWriter(out);
		response = new MockHttpServletResponse() {
			public PrintWriter getWriter() {
				return printWriter;
			};
		};
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
	public void testDestroy() throws CredentialNotFoundException, IOException {
		controller.create(user, password);
		final String token = FileUtils.readFileToString(file);
		controller.destroy(token);
	}

}
