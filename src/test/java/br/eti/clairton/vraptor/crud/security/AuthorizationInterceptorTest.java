package br.eti.clairton.vraptor.crud.security;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import javax.interceptor.InvocationContext;

import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;

import br.eti.clairton.vraptor.crud.Aplicacao;

public class AuthorizationInterceptorTest {
	private AuthorizationInterceptor interceptor;
	private InvocationContext context;
	private Authorizator authorizator;
	private final String usuario = "jose";
	private final String app = "test";

	@Before
	public void setUp() throws Exception {
		authorizator = mock(Authorizator.class);
		interceptor = new AuthorizationInterceptor(app, usuario, authorizator, LogManager.getLogger());
		final Object[] parameters = {};
		final Resourceable target = new Resourceable(Aplicacao.class) {
			@Authorized
			public void test() {
				System.out.println("Teste Called");
			}
		};
		final Method method = target.getClass().getMethod("test");
		context = new InvocationContext() {

			@Override
			public void setParameters(Object[] params) {

			}

			@Override
			public Object proceed() throws Exception {
				return method.invoke(target, parameters);
			}

			@Override
			public Object getTimer() {
				return null;
			}

			@Override
			public Object getTarget() {
				return target;
			}

			@Override
			public Object[] getParameters() {
				return parameters;
			}

			@Override
			public Method getMethod() {
				return method;
			}

			@Override
			public Map<String, Object> getContextData() {
				return null;
			}

			@Override
			public Constructor<?> getConstructor() {
				return null;
			}
		};
	}

	@Test(expected = UnauthorizedException.class)
	public void testInvokeUnauthorized() throws Throwable {
		interceptor.invoke(context);
	}

	@Test
	public void testInvokeAuthorized() throws Throwable {
		when(
				authorizator.isAble(anyObject(), anyObject(), anyString(),
						anyString())).thenReturn(Boolean.TRUE);
		final Object expected = "çegjweargihjjhjsfkhgsdlçgjusdayjicodtaiotiow";
		final InvocationContext spy = spy(context);
		when(spy.proceed()).thenReturn(expected);
		assertEquals(expected, interceptor.invoke(spy));
	}

	@Test(expected = TestException.class)
	public void testOriginalException() throws Throwable {
		when(
				authorizator.isAble(anyObject(), anyObject(), anyString(),
						anyString())).thenThrow(new TestException());
		interceptor.invoke(context);
	}
}

class TestException extends RuntimeException {
	private static final long serialVersionUID = 1L;

}
