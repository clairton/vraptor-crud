package br.eti.clairton.vraptor.crud.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;

import br.eti.clairton.security.Lock;

public class LockLdapTest extends AbstractLdapTest {

	private Lock lock;

	@Before
	public void setUp() {
		lock = new LockLdap(
				LogManager.getLogger(LockLdap.class),
				"ldap://localhost:19389", "com.sun.jndi.ldap.LdapCtxFactory",
				"simple",
				"cn=Admin Istrator+sn=Istrator+uid=admin,dc=child,dc=root",
				"123456", "cn,sn,uid,dc", "dc=root", "uid");
	}

	@Test
	public void testIsValid() {
		assertTrue(lock.isValid("user", "789ABC"));
	}

	@Test
	public void testIsInvalidUser() {
		assertFalse(lock.isValid("admin", "outra senha"));
	}

	@Test
	public void testIsInvalidPassword() {
		assertFalse(lock.isValid("outrouser", "123456"));
	}

}
