package br.eti.clairton.vraptor.crud.security;

import static org.mockito.Mockito.mock;

import javax.inject.Inject;
import javax.interceptor.InvocationContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.eti.clairton.repository.Repository;
import br.eti.clairton.vraptor.crud.CdiJUnit4Runner;

@RunWith(CdiJUnit4Runner.class)
public class AuthenticationInterceptorTest extends AbstractLdapTest {
	private AuthenticationInterceptor interceptor;
	private InvocationContext context = mock(InvocationContext.class);
	private Authenticator authenticator;
	private TokenManager tokenManager;
	private @Inject Repository repository;
	private @Inject Logger logger;

	@Before
	public void setUp() {
		authenticator = new AuthenticatorLdap(
				LogManager.getLogger(AuthenticatorLdap.class),
				"ldap://localhost:9389", "com.sun.jndi.ldap.LdapCtxFactory",
				"simple",
				"cn=Admin Istrator+sn=Istrator+uid=admin,dc=child,dc=root",
				"123456", "cn,sn,uid,dc", "dc=root", "uid");
		tokenManager = new TokenManagerPersistent(logger, authenticator,
				repository, "18000", "MD5");
	}

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
