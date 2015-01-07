package br.eti.clairton.vraptor.crud;

import static br.com.caelum.vraptor.view.Results.http;
import static br.com.caelum.vraptor.view.Results.json;
import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.interceptor.InvocationContext;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.view.HttpResult;
import br.eti.clairton.vraptor.crud.security.UnauthenticatedException;
import br.eti.clairton.vraptor.crud.security.UnauthorizedException;

public class ExceptionVerifierInterceptorTest {
	private ExceptionVerifierInterceptor interceptor;
	private InvocationContext context;
	private ConstraintValidationAdapter adapter;
	private Logger logger = LogManager.getLogger("Test");
	private Result result;
	private HttpResult httpResult;
	private JSONSerialization jsonResult;

	@Before
	public void setUp() {
		context = mock(InvocationContext.class);
		adapter = mock(ConstraintValidationAdapter.class);
		result = mock(Result.class);
		httpResult = mock(HttpResult.class);
		jsonResult = mock(JSONSerialization.class);
		when(result.use(http())).thenReturn(httpResult);
		when(result.use(json())).thenReturn(jsonResult);
		final Serializer serializer = mock(Serializer.class);
		when(jsonResult.from(anyObject(), anyString())).thenReturn(serializer);
		interceptor = new ExceptionVerifierInterceptor(result, logger, adapter);
	}

	@Test
	public void testVerifyUnauthorizedException() throws Throwable {
		final Throwable exception = new UnauthorizedException("lskhdflksdhg");
		when(context.proceed()).thenThrow(exception);
		interceptor.invoke(context);
		verify(httpResult).setStatusCode(403);
		verify(jsonResult).from(eq(wrap(exception.getMessage())), eq("errors"));
	}

	@Test
	public void testVerifyUnauthenticatedException() throws Throwable {
		final Throwable exception = new UnauthenticatedException(
				"sgdsalkghhhyk");
		when(context.proceed()).thenThrow(exception);
		interceptor.invoke(context);
		verify(httpResult).setStatusCode(401);
		verify(jsonResult).from(eq(wrap(exception.getMessage())), eq("errors"));
	}

	@Test
	public void testVerifyOptimisticLockException() throws Throwable {
		final Throwable exception = new OptimisticLockException("putz");
		when(context.proceed()).thenThrow(exception);
		interceptor.invoke(context);
		verify(httpResult).setStatusCode(409);
		verify(jsonResult).from(eq(wrap(exception.getMessage())), eq("errors"));
	}

	@Test
	public void testVerifyNoResultException() throws Throwable {
		final Throwable exception = new NoResultException();
		when(context.proceed()).thenThrow(exception);
		interceptor.invoke(context);
		verify(httpResult).setStatusCode(404);
		verify(jsonResult).from(eq(wrap(exception.getMessage())), eq("errors"));
	}

	@Test
	public void testVerifyConstraintViolationException() throws Throwable {
		final JSONSerialization jsonResult = mock(JSONSerialization.class);
		when(result.use(json())).thenReturn(jsonResult);
		final Validator v = buildDefaultValidatorFactory().getValidator();
		final ConstraintViolationException e = new ConstraintViolationException(
				"", v.validate(new Aplicacao()));
		when(context.proceed()).thenThrow(e);
		final Serializer serializer = mock(Serializer.class);
		when(jsonResult.from(anyObject(), anyString())).thenReturn(serializer);
		interceptor.invoke(context);
		verify(httpResult).setStatusCode(422);
		verify(jsonResult).from(any(Set.class), eq("errors"));
	}

	@Test(expected = Exception.class)
	public void testVerifyThrowable() throws Throwable {
		when(context.proceed()).thenThrow(new Exception());
		interceptor.invoke(context);
	}

	private Map<String, List<String>> wrap(final String message) {
		return new HashMap<String, List<String>>() {
			private static final long serialVersionUID = 1L;

			{
				put("error", Arrays.asList(message));
			}
		};
	}
}
