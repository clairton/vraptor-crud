package br.eti.clairton.vraptor.crud;

import static br.com.caelum.vraptor.view.Results.http;
import static br.com.caelum.vraptor.view.Results.json;
import static java.lang.String.format;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolationException;

import org.apache.logging.log4j.Logger;

import br.com.caelum.vraptor.Result;
import br.eti.clairton.vraptor.crud.security.UnauthenticatedException;
import br.eti.clairton.vraptor.crud.security.UnauthorizedException;

/**
 * Verifica o encaminhando diante das exceções lançadas para os metodos
 * annotados com {@link ExceptionVerifier}.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Interceptor
@ExceptionVerifier
public class ExceptionVerifierInterceptor {
	private final Result result;

	private final Logger logger;

	private final ConstraintValidationAdapter adapter;

	/**
	 * Construtor padrão.
	 * 
	 * @param result
	 *            instancia de {@link Result}
	 * @param logger
	 *            instancia de {@link Logger}
	 * @param adapter
	 *            instancia de {@link ConstraintValidationAdapter}
	 */
	@Inject
	public ExceptionVerifierInterceptor(final Result result,
			final Logger logger, final ConstraintValidationAdapter adapter) {
		this.result = result;
		this.logger = logger;
		this.adapter = adapter;
	}

	/**
	 * Chama os metodos protegendo contra as exceção.
	 * 
	 * @param invocationContext
	 *            contexto da invocação
	 * @return o retorno do metodo do context
	 * @throws Exception
	 *             caso a exceção lançada pelo metodo ainda não seja controlada
	 *             ela é propagada
	 */
	@AroundInvoke
	public Object invoke(final InvocationContext invocationContext)
			throws Throwable {
		Object errors;
		Integer status;
		try {
			return invocationContext.proceed();
		} catch (final NoResultException e) {
			logger.debug(format("NoResult: %s", e.getMessage()));
			status = 404;
			errors = e.getMessage();
		} catch (final UnauthorizedException e) {
			logger.debug(format("Unauthorized: %s", e.getMessage()));
			status = 413;
			errors = e.getMessage();
		} catch (final UnauthenticatedException e) {
			logger.debug(format("Unauthenticated: %s", e.getMessage()));
			status = 401;
			errors = e.getMessage();
		} catch (final ConstraintViolationException e) {
			logger.debug(format("Violation: %s", e.getMessage()));
			errors = adapter.to(e.getConstraintViolations());
			status = 422;
		} catch (final InvocationTargetException e) {
			logger.error("InvocationTarget", e.getTargetException());
			throw e.getTargetException();
		} catch (final Throwable e) {
			logger.error("Throwable", e);
			throw e;
		}
		result.use(http()).setStatusCode(status);
		result.use(json()).from(errors, "errors").serialize();
		return null;
	}
}
