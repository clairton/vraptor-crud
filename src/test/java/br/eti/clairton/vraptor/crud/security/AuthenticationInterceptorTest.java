package br.eti.clairton.vraptor.crud.security;

import static org.mockito.Mockito.mock;

import javax.interceptor.InvocationContext;

import org.junit.Test;

public class AuthenticationInterceptorTest {
	private AuthenticationInterceptor interceptor;
	private InvocationContext context = mock(InvocationContext.class);
	private Authenticator authenticator = new AuthenticatorInMemory();
	private TokenManager tokenManager = new TokenManagerInMemory(authenticator);

	@Test
	public void testInvoke() throws Throwable {
		final String token = tokenManager.create("admin", "123456");
		interceptor = new AuthenticationInterceptor(tokenManager, token);
		interceptor.invoke(context);
	}

	@Test(expected = UnauthenticatedException.class)
	public void testInvokeWithSession() throws Throwable {
		interceptor = new AuthenticationInterceptor(tokenManager,
				"hash_nao_valido");
		interceptor.invoke(context);
	}
}
