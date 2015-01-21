package br.eti.clairton.vraptor.crud.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;

public class AuthenticatorLdapTest extends AbstractLdapTest {

	private Authenticator authenticator;

	@Before
	public void setUp() {
		authenticator = new AuthenticatorLdap(
				LogManager.getLogger(AuthenticatorLdap.class),
				"ldap://localhost:19389", "com.sun.jndi.ldap.LdapCtxFactory",
				"simple",
				"cn=Admin Istrator+sn=Istrator+uid=admin,dc=child,dc=root",
				"123456", "cn,sn,uid,dc", "dc=root", "uid");
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
