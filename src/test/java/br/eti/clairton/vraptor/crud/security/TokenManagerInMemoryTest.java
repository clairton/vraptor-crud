package br.eti.clairton.vraptor.crud.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.security.auth.login.CredentialNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.eti.clairton.vraptor.crud.CdiJUnit4Runner;

@RunWith(CdiJUnit4Runner.class)
public class TokenManagerInMemoryTest {

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

}
