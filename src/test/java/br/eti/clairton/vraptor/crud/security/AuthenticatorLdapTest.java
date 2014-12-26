package br.eti.clairton.vraptor.crud.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.integ.CreateLdapServerRule;
import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

@CreateDS(name = "Test", partitions = { @CreatePartition(name = "Test", suffix = "o=TEST") })
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP", port = 9389) })
@ApplyLdifFiles("data.ldif")
public class AuthenticatorLdapTest	 {
	@ClassRule
	public static CreateLdapServerRule ldapRule = new CreateLdapServerRule();

	private Authenticator authenticator;

	@Before
	public void setUp() {
		authenticator = new AuthenticatorLdap(
				LogManager.getLogger(AuthenticatorLdap.class),
				"ldap://localhost:9389", "com.sun.jndi.ldap.LdapCtxFactory",
				"simple",
				"cn=Admin Istrator+sn=Istrator+uid=admin,ou=ABC,o=TEST",
				"123456", "cn,sn,uid,ou,o", "o=TEST", "uid");
	}

	@Test
	public void testIsValid() {
		assertTrue(authenticator.isValid("user", "789ABC"));
	}

	@Test
	public void testIsInvalidUser() {
		assertFalse(authenticator.isValid("admin", "outra senha"));
	}

	@Test
	public void testIsInvalidPassword() {
		assertFalse(authenticator.isValid("outrouser", "123456"));
	}

}
