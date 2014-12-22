package br.eti.clairton.vraptor.crud.security;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;
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
			@NotNull final HttpServletRequest request) {
		this.tokenManager = tokenManager;
		final String header = request.getHeader("Authorization");
		if (null == header) {

			throw new UnauthenticatedException(
					"Header \"Authorization\" must be present");
		}
		this.token = header.replaceAll("Basic ", "");
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
				throw new UnauthenticatedException();
			}
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}
}
