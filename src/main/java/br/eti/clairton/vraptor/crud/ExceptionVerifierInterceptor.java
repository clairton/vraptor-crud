package br.eti.clairton.vraptor.crud;

import static br.com.caelum.vraptor.view.Results.http;
import static br.com.caelum.vraptor.view.Results.json;
import static java.lang.String.format;

import java.lang.reflect.InvocationTargetException;
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
	 * CDI eyes only.
	 */
	@Deprecated
	protected ExceptionVerifierInterceptor() {
		this(null, null, null);
	}

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
	public Object verify(final InvocationContext invocationContext)
			throws Throwable {
		try {
			return invocationContext.proceed();
		} catch (final NoResultException e) {
			logger.fine(format("NoResult: %s", e.getMessage()));
			result.notFound();
		} catch (final UnauthorizedException e) {
			logger.fine(format("Unauthorized: %s", e.getMessage()));
			result.use(http()).sendError(413, e.getMessage());
		} catch (final UnauthenticatedException e) {
			logger.fine(format("Unauthenticated: %s", e.getMessage()));
			result.use(http()).sendError(401, e.getMessage());
		} catch (final ConstraintViolationException e) {
			logger.fine(format("Violation: %s", e.getMessage()));
			result.use(http()).setStatusCode(422);
			final Map<?, ?> errors = adapter.to(e.getConstraintViolations());
			result.use(json()).from(errors, "errors").serialize();
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		} catch (final Throwable e) {
			logger.log(Level.SEVERE, "Throwable", e);
			throw e;
		}
		return null;
	}
}
