package br.eti.clairton.vraptor.crud.security;

import static org.mockito.Mockito.*;

import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

public class AuthenticationInterceptorTest {
	private AuthenticationInterceptor interceptor;
	private InvocationContext context = mock(InvocationContext.class);
	private HttpServletRequest request = mock(HttpServletRequest.class);
	private Authenticator authenticator = new AuthenticatorInMemory();
	private TokenManager tokenManager = new TokenManagerInMemory(authenticator);

	@Before
	public void setUp() throws Exception {
		interceptor = new AuthenticationInterceptor(tokenManager, request);
	}

	@Test
	public void testInvoke() throws Throwable {
		final String token = tokenManager.create("admin", "123456");
		when(request.getHeader("Authorization")).thenReturn(token);
		interceptor.invoke(context);
	}

	@Test(expected = UnauthenticatedException.class)
	public void testInvokeWithSession() throws Throwable {
		interceptor.invoke(context);
	}
}
