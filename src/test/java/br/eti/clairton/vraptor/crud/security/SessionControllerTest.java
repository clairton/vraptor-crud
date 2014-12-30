package br.eti.clairton.vraptor.crud.security;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import javax.inject.Inject;
import javax.security.auth.login.CredentialNotFoundException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.util.test.MockHttpServletResponse;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.vraptor.crud.CdiJUnit4Runner;

@RunWith(CdiJUnit4Runner.class)
public class SessionControllerTest extends AbstractLdapTest {
	private final File file = new File("target/token.txt");
	private SessionController controller;
	private final String user = "admin";
	private final String password = "123456";
	private Authenticator authenticator;
	private TokenManager tokenManager;
	private @Inject Repository repository;
	private @Inject Logger logger;

	private HttpServletResponse response;

	@Before
	public void setUp() throws IOException {
		authenticator = new AuthenticatorLdap(
				LogManager.getLogger(AuthenticatorLdap.class),
				"ldap://localhost:9389", "com.sun.jndi.ldap.LdapCtxFactory",
				"simple",
				"cn=Admin Istrator+sn=Istrator+uid=admin,dc=child,dc=root",
				"123456", "cn,sn,uid,dc", "dc=root", "uid");
		tokenManager = new TokenManagerPersistent(logger, authenticator,
				repository, "18000", "SHA-256");
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
