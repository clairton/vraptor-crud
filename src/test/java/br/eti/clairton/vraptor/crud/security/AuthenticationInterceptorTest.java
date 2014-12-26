package br.eti.clairton.vraptor.crud.security;

import static org.mockito.Mockito.mock;

import javax.interceptor.InvocationContext;

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
import org.junit.runner.RunWith;

import br.eti.clairton.vraptor.crud.CdiJUnit4Runner;

@RunWith(CdiJUnit4Runner.class)
@CreateDS(name = "Test", partitions = { @CreatePartition(name = "Test", suffix = "o=TEST") })
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP", port = 9389) })
@ApplyLdifFiles("data.ldif")
public class AuthenticationInterceptorTest {
	@ClassRule
	public static CreateLdapServerRule ldapRule = new CreateLdapServerRule();
	private AuthenticationInterceptor interceptor;
	private InvocationContext context = mock(InvocationContext.class);
	private Authenticator authenticator;
	private TokenManager tokenManager;

	@Before
	public void setUp() {
		authenticator = new AuthenticatorLdap(
				LogManager.getLogger(AuthenticatorLdap.class),
				"ldap://localhost:9389", "com.sun.jndi.ldap.LdapCtxFactory",
				"simple",
				"cn=Admin Istrator+sn=Istrator+uid=admin,ou=ABC,o=TEST",
				"123456", "cn,sn,uid,ou,o", "o=TEST", "uid");
		tokenManager = new TokenManagerInMemory(authenticator);
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
