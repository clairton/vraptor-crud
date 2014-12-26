package br.eti.clairton.vraptor.crud.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.security.auth.login.CredentialNotFoundException;

import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.integ.CreateLdapServerRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.eti.clairton.vraptor.crud.CdiJUnit4Runner;

@RunWith(CdiJUnit4Runner.class)
@CreateDS(name = "Test", partitions = { @CreatePartition(name = "Test", suffix = "o=TEST") })
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP", port = 9389) })
@ApplyLdifFiles("data.ldif")
public class TokenManagerInMemoryTest {
	@ClassRule
	public static CreateLdapServerRule ldapRule = new CreateLdapServerRule();

	private @Inject TokenManager tokenManager;

	@Test
	public void testCreate() throws CredentialNotFoundException {
		tokenManager.create("admin", "123456");
	}

	@Test
	public void testDestroy() throws CredentialNotFoundException {
		final String token = tokenManager.create("admin", "123456");
		assertTrue(tokenManager.isValid(token));
		tokenManager.destroy(token);
		assertFalse(tokenManager.isValid(token));
	}

	@Test
	public void testIsValid() throws CredentialNotFoundException {
		final String token = tokenManager.create("admin", "123456");
		assertTrue(tokenManager.isValid(token));
	}

	@Test
	public void testGetUserByToken() throws CredentialNotFoundException {
		final String token = tokenManager.create("admin", "123456");
		assertEquals("admin", tokenManager.getUserByToken(token));
	}

}
