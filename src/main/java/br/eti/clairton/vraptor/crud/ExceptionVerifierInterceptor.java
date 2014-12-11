package br.eti.clairton.vraptor.crud;

import static br.com.caelum.vraptor.view.Results.http;
import static br.com.caelum.vraptor.view.Results.json;
import static java.lang.String.format;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolationException;

import br.com.caelum.vraptor.Result;

@Interceptor
@ExceptionVerifier
public class ExceptionVerifierInterceptor {
	private final Result result;

	private final Logger logger;

	private final ConstraintValidationAdapter adapter;

	/**
	 * @deprecated CDI eyes only
	 */
	protected ExceptionVerifierInterceptor() {
		this(null, null, null);
	}

	@Inject
	public ExceptionVerifierInterceptor(final Result result,
			final Logger logger, final ConstraintValidationAdapter adapter) {
		this.result = result;
		this.logger = logger;
		this.adapter = adapter;
	}

	@AroundInvoke
	public Object verify(final InvocationContext invocationContext)
			throws Exception {
		try {
			return invocationContext.proceed();
		} catch (final NoResultException e) {
			logger.fine(format("NoResult: %s", e.getMessage()));
			result.notFound();
		} catch (final ConstraintViolationException e) {
			logger.fine(format("Violation: %s", e.getMessage()));
			result.use(http()).setStatusCode(422);
			final Map<?, ?> errors = adapter.to(e.getConstraintViolations());
			result.use(json()).from(errors, "errors").serialize();
		} catch (final Throwable e) {
			logger.log(Level.SEVERE, "Throwable", e);
			throw e;
		}
		return null;
	}
}
