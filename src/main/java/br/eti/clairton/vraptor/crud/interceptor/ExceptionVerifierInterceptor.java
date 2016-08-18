package br.eti.clairton.vraptor.crud.interceptor;

import static br.com.caelum.vraptor.view.Results.http;
import static br.com.caelum.vraptor.view.Results.json;
import static java.util.Arrays.asList;
import static org.apache.logging.log4j.LogManager.getLogger;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.security.auth.login.CredentialNotFoundException;
import javax.validation.ConstraintViolationException;

import org.apache.logging.log4j.Logger;

import br.com.caelum.vraptor.Result;
import br.eti.clairton.security.InvalidUserException;
import br.eti.clairton.security.PasswordExpiredException;
import br.eti.clairton.security.PasswordPolicyException;
import br.eti.clairton.security.UnauthenticatedException;
import br.eti.clairton.security.UnauthorizedException;
import br.eti.clairton.vraptor.crud.controller.NotInstanceableExeception;

/**
 * Verifica o encaminhando diante das exceções lançadas para os metodos
 * annotados com {@link ExceptionVerifier}.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Interceptor
@ExceptionVerifier
public class ExceptionVerifierInterceptor {
	private static final Logger logger = getLogger(ExceptionVerifierInterceptor.class);

	private final Result result;

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
	public ExceptionVerifierInterceptor(final Result result, final ConstraintValidationAdapter adapter) {
		this.result = result;
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
	public Object invoke(final InvocationContext invocationContext) throws Throwable {
		Object errors;
		Integer status;
		try {
			return invocationContext.proceed();
		} catch (final NoResultException e) {
			logger.debug("NoResult: {}", e.getMessage());
			status = 404;
			errors = asMessage(e.getMessage());
		} catch (final UnauthorizedException e) {
			logger.debug("Unauthorized: {}", e.getMessage());
			status = 403;
			errors = asMessage(e.getMessage());
		} catch (final UnauthenticatedException e) {
			logger.debug("Unauthenticated: {}", e.getMessage());
			status = 401;
			errors = asMessage(e.getMessage());
		} catch (final PasswordExpiredException e) {
			logger.debug("PasswordExpired: {}", e.getMessage());
			status = 403;
			final Map<String, Integer> message = new HashMap<>();
			message.put("status", 403);
			errors = message;
		}  catch (final ConstraintViolationException e) {
			logger.debug("ConstraintViolation: {}", e.getMessage());
			@SuppressWarnings("rawtypes")
			final Set violations = e.getConstraintViolations();
			@SuppressWarnings("unchecked")
			final Object b = adapter.to(violations);
			errors = b;
			status = 422;
		} catch (final InvalidUserException e) {
			logger.debug("InvalidUserException: {}", e.getMessage());
			final String m = "Usuário não pode ser criado pois esta invalido!!";
			errors = asMessage(m);
			status = 422;
		} catch (final OptimisticLockException e) {
			logger.debug("OptimisticLock: {}", e.getMessage());
			status = 409;
			errors = asMessage(e.getMessage());
		} catch (final CredentialNotFoundException e) {
			logger.debug("CredentialNotFound: {}", e.getMessage());
			status = 401;
			final String m;
			if (e.getMessage() == null) {
				m = "Usuário/Senha não existe(m)!";
			} else {
				m = e.getMessage();
			}
			errors = asMessage(m);
		} catch (final PasswordPolicyException e) {
			final String m = "A senha informada não atende a politica de segurança!!";
			logger.debug("PasswordPolicyException: " + m, e);
			status = 401;
			errors = asMessage(m);
		} catch (final NotInstanceableExeception e) {
			logger.debug("NotNewableExeception: {}", e.getMessage());
			errors = asMessage(e.getCause().getMessage());
			status = 422;
		} catch (final InvocationTargetException e) {
			logger.debug("InvocationTarget: {}", e.getTargetException());
			throw e.getTargetException();
		} catch (final Throwable e) {
			// logger.error("Throwable", e);
			throw e;
		}
		/*
		 * Para adicionar a mensagem somente uma vez.
		 */
		// if (!result.used()) {
		result.use(http()).setStatusCode(status);
		result.use(json()).from(errors, "errors").serialize();
		// }
		return null;
	}

	private Map<String, List<String>> asMessage(final String message) {
		final Map<String, List<String>> messages = new HashMap<String, List<String>>();
		messages.put("error", asList(message));
		return messages;
	}
}
