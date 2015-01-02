package br.eti.clairton.vraptor.crud.security;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import javax.inject.Inject;
import javax.security.auth.login.CredentialNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.vidageek.mirror.dsl.Mirror;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

import br.com.caelum.vraptor.serialization.gson.GsonBuilderWrapper;
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
	private @Inject GsonBuilderWrapper builder;
	private @Inject Mirror mirror;

	private HttpServletResponse response;
	private HttpServletRequest request = new MockHttpServletRequest();

	public void setUp(final String user, final String password)
			throws IOException {
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
		final String json = String.format("{'user': '%s', 'password': '%s'}",
				user, password);
		mirror.on(request).set().field("content").withValue(json.getBytes());
		controller = new SessionController(tokenManager, request, response,
				builder);
	}

	@Test(expected = CredentialNotFoundException.class)
	public void testCreateInValidPassword() throws CredentialNotFoundException,
			IOException {
		setUp(user, "abcdef");
		controller.create();
	}

	@Test(expected = CredentialNotFoundException.class)
	public void testCreateInValidUser() throws CredentialNotFoundException,
			IOException {
		setUp("outrouser", password);
		controller.create();
	}

	@Test
	public void testCreateValid() throws CredentialNotFoundException,
			IOException {
		setUp(user, password);
		controller.create();
	}

	@Test
	public void testDestroy() throws CredentialNotFoundException, IOException,
			IOException {
		setUp(user, password);
		controller.create();
		final String token = FileUtils.readFileToString(file);
		controller.destroy(token);
	}

}
