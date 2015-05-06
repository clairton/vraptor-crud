package br.eti.clairton.vraptor.crud.interceptor;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.interceptor.InvocationContext;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Payload;
import javax.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import br.com.caelum.vraptor.util.test.MockResult;
import br.eti.clairton.vraptor.crud.model.Aplicacao;

public class ConstraintValidationAdapterTest {
	private final ConstraintValidationAdapter adapter = new ConstraintValidationAdapter();
	private final Validator validator = buildDefaultValidatorFactory()
			.getValidator();

	@Test
	public void testSerialize() throws Throwable {
		final MockResult result = new MockResult();
		final Logger logger = LogManager.getLogger();
		final ExceptionVerifierInterceptor interceptor = new ExceptionVerifierInterceptor(
				result, logger, adapter);
		final InvocationContext invocationContext = mock(InvocationContext.class);
		final Throwable e = new ConstraintViolationException(getViolations());
		when(invocationContext.proceed()).thenThrow(e);
		interceptor.invoke(invocationContext);
	}

	@Test
	public void testConvert() {
		final Map<String, List<String>> errors = adapter.to(getViolations());
		assertTrue(errors.containsKey("nome"));
	}

	@Test
	public void testValidacaoDeClass() {
		final TestClass bean = new TestClass();
		final Set<ConstraintViolation<TestClass>> violations = validator
				.validate(bean);
		final Map<String, List<String>> errors = adapter.to(violations);
		assertTrue(errors.containsKey("testClass"));
		assertEquals("xpto", errors.get("testClass").get(0));
	}

	private Set<ConstraintViolation<Aplicacao>> getViolations() {
		final Aplicacao aplicacao = new Aplicacao(null);
		return validator.validate(aplicacao);
	}

	@TestCheck
	private static class TestClass {

	}

	@Target(TYPE)
	@Retention(RUNTIME)
	@Documented
	@Constraint(validatedBy = { TestCheckValidator.class })
	private static @interface TestCheck {
		String message() default "xpto";

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};
	}

	public static class TestCheckValidator implements
			ConstraintValidator<TestCheck, TestClass> {

		@Override
		public void initialize(TestCheck constraintAnnotation) {

		}

		@Override
		public boolean isValid(TestClass value,
				ConstraintValidatorContext context) {
			return false;
		}

	}
}
