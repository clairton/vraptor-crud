package br.eti.clairton.vraptor.crud.security;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;

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
	private final HttpServletRequest request;

	@Inject
	public AuthenticationInterceptor(final TokenManager tokenManager,
			final HttpServletRequest request) {
		this.tokenManager = tokenManager;
		this.request = request;
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
			final String token = request.getHeader("Authorization");
			if (tokenManager.isValid(token)) {
				return context.proceed();
			} else {
				throw new UnauthenticatedException();
			}
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}
}
