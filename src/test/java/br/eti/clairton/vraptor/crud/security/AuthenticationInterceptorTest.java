package br.eti.clairton.vraptor.crud.security;

import static org.mockito.Mockito.mock;

import java.util.Random;

import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

public class AuthenticationInterceptorTest {
	private AuthenticationInterceptor interceptor;
	private InvocationContext context = mock(InvocationContext.class);
	private Boolean isValid;
	private HttpServletRequest request = mock(HttpServletRequest.class);
	private TokenManager tokenManager = new TokenManager() {

		@Override
		public Boolean isValid(final String token) {
			return isValid;
		}

		@Override
		public void destroy(final String token) {

		}

		@Override
		public String create(final String user, final String password) {
			return new Random().toString();
		}
	};

	@Before
	public void setUp() throws Exception {
		interceptor = new AuthenticationInterceptor(tokenManager, request);
	}

	@Test
	public void testInvoke() throws Throwable {
		isValid = Boolean.TRUE;
		interceptor.invoke(context);
	}

	@Test(expected = UnauthenticatedException.class)
	public void testInvokeWithSession() throws Throwable {
		isValid = Boolean.FALSE;
		interceptor.invoke(context);
	}
}
