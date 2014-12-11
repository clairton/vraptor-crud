package br.eti.clairton.vraptor.crud;

import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.junit.Test;

import br.com.caelum.vraptor.util.test.MockResult;

public class ConstraintValidationAdapterTest {
	private final ConstraintValidationAdapter adapter = new ConstraintValidationAdapter();

	@Test
	public void testSerialize() throws Exception {
		final MockResult result = new MockResult();
		final Logger logger = Logger.getAnonymousLogger();
		final ExceptionVerifierInterceptor interceptor = new ExceptionVerifierInterceptor(
				result, logger, adapter);
		final InvocationContext invocationContext = mock(InvocationContext.class);
		final Throwable e = new ConstraintViolationException(getViolations());
		when(invocationContext.proceed()).thenThrow(e);
		interceptor.verify(invocationContext);
	}

	@Test
	public void testConvert() {
		final Map<String, List<String>> errors = adapter.to(getViolations());
		assertTrue(errors.containsKey("nome"));
	}

	@SuppressWarnings("unchecked")
	private Set<ConstraintViolation<?>> getViolations() {
		final Validator validator = buildDefaultValidatorFactory()
				.getValidator();
		final Aplicacao aplicacao = new Aplicacao(null);
		final Set<?> violations = validator.validate(aplicacao);
		return (Set<ConstraintViolation<?>>) violations;
	}

}
