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
import org.mockito.Mockito;

import br.eti.clairton.vraptor.crud.Aplicacao;

public class LockInterceptorTest {
	private GateInterceptor interceptor;
	private InvocationContext context;
	private Gate authorizator;
	private final String usuario = "jose";
	private final String app = "test";

	@Before
	public void setUp() throws Exception {
		authorizator = mock(Gate.class);
		interceptor = new GateInterceptor(app, usuario, authorizator,
				LogManager.getLogger());
		final Object[] parameters = {};
		final Resourceable target = new TestResourceable();
		final Method method = target.getClass().getMethod("test");
		context = new TestInvocationContext(parameters, target, method);
	}

	@Test(expected = UnauthorizedException.class)
	public void testInvokeUnauthorized() throws Throwable {
		interceptor.invoke(context);
	}

	@Test
	public void testInvokeAuthorized() throws Throwable {
		when(
				authorizator.isOpen(anyObject(), anyObject(), anyString(),
						anyString())).thenReturn(Boolean.TRUE);
		final Object expected = "çegjweargihjjhjsfkhgsdlçgjusdayjicodtaiotiow";
		final InvocationContext spy = spy(context);
		when(spy.proceed()).thenReturn(expected);
		assertEquals(expected, interceptor.invoke(spy));
	}

	@Test(expected = TestException.class)
	public void testOriginalException() throws Throwable {
		when(
				authorizator.isOpen(anyObject(), anyObject(), anyString(),
						anyString())).thenThrow(new TestException());
		interceptor.invoke(context);
	}
}

class TestException extends RuntimeException {
	private static final long serialVersionUID = 1L;

}

class TestResourceable extends Resourceable {

	public TestResourceable() {
		super(Aplicacao.class);
	}

	@Authorized
	public void test() {

	}

}

class TestInvocationContext implements InvocationContext {
	private final Object[] parameters;
	private final Resourceable target;
	private final Method method;

	public TestInvocationContext(Object[] parameters, Resourceable target,
			Method method) {
		super();
		this.parameters = parameters;
		this.target = target;
		this.method = method;
	}

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
		return Mockito.mock(Constructor.class);
	}
}