package br.eti.clairton.vraptor.crud.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;

import javax.inject.Inject;
import javax.security.auth.login.CredentialNotFoundException;

import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.integ.CreateLdapServerRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.eti.clairton.vraptor.crud.CdiJUnit4Runner;

@RunWith(CdiJUnit4Runner.class)
@CreateDS(name = "Test", partitions = { @CreatePartition(name = "Test", suffix = "o=TEST") })
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP", port = 9389) })
@ApplyLdifFiles("data.ldif")
public class TokenManagerPersistentTest {
	@ClassRule
	public static CreateLdapServerRule ldapRule = new CreateLdapServerRule();

	private @Inject TokenManager tokenManager;
	private @Inject Connection connection;
	private String token;

	@Before
	public void setUp() throws Exception {
		final String sql = "DELETE from tokens;";
		connection.createStatement().execute(sql);
		token = tokenManager.create("admin", "123456");
	}

	@Test
	public void testDestroyByToken() throws CredentialNotFoundException {
		tokenManager.destroy(token);
		assertFalse(tokenManager.isValid(token));
	}

	@Test
	public void testDestroyByUser() throws CredentialNotFoundException {
		tokenManager.destroy(token);
		assertFalse(tokenManager.isValid(token));
	}

	@Test
	public void testIsValid() throws CredentialNotFoundException {
		assertTrue(tokenManager.isValid(token));
	}

	@Test
	public void testGetUserByToken() throws CredentialNotFoundException {
		assertEquals("admin", tokenManager.getUserByToken(token));
	}

}
