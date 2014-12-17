package br.eti.clairton.vraptor.crud.security;

import static org.mockito.Mockito.mock;

import javax.interceptor.InvocationContext;

import org.junit.Before;
import org.junit.Test;

public class AuthenticationInterceptorTest {
	private AuthenticationInterceptor interceptor;
	private InvocationContext context = mock(InvocationContext.class);

	@Before
	public void setUp() throws Exception {
		interceptor = new AuthenticationInterceptor();
	}

	@Test
	public void testInvoke() throws Throwable {
		interceptor.invoke(context);
	}
}
