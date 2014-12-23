package br.eti.clairton.vraptor.crud.security;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.validation.constraints.NotNull;

/**
 * INterceptor para metodos anotados com {@link Authorized}.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 *
 */
@Interceptor
@Authenticated
public class AuthenticationInterceptor {
	private final TokenManager tokenManager;
	private final String token;

	@Inject
	public AuthenticationInterceptor(@NotNull final TokenManager tokenManager,
			@Token final String token) {
		this.tokenManager = tokenManager;
		this.token = token;
	}

	/**
	 * Intercepta cada chamada de método anotado com {@link Authorized}.
	 * 
	 * @param context
	 *            contexto da invocação
	 * @return retorno do método que foi invocado
	 * @throws Throwable
	 *             caso ocorra algum problema na chamada do metodo do contexto,
	 *             ou o usuário não esteja autorizado
	 */
	@AroundInvoke
	public Object invoke(final InvocationContext context) throws Throwable {
		try {
			if (tokenManager.isValid(token)) {
				return context.proceed();
			} else {
				throw new UnauthenticatedException("Token " + token
						+ " is invalid");
			}
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}
}
