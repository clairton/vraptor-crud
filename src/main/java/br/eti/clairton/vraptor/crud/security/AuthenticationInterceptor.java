package br.eti.clairton.vraptor.crud.security;

import java.lang.reflect.InvocationTargetException;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * INterceptor para metodos anotados com {@link Authorized}.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 *
 */
@Interceptor
@Authenticated
public class AuthenticationInterceptor {

	/**
	 * CDI eyes only.
	 */
	@Deprecated
	protected AuthenticationInterceptor() {
		// this(null, null);
	}

	// @Inject
	// public AuthorizationInterceptor() {
	// super();
	// }

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
			return context.proceed();
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}
}
