package br.eti.clairton.vraptor.crud.security;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
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
@Authorized
public class AuthorizationInterceptor {
	private final String user;
	private final Authorizator authorizator;

	/**
	 * CDI eyes only.
	 */
	@Deprecated
	protected AuthorizationInterceptor() {
		this(null, null);
	}

	@Inject
	public AuthorizationInterceptor(@User final String user,
			final Authorizator authorizator) {
		super();
		this.user = user;
		this.authorizator = authorizator;
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
		final Object target = context.getTarget();
		final Resourceable resourceable = (Resourceable) target;
		final String resource = resourceable.getResourceName();
		final String operation = context.getMethod().getName();
		if (!authorizator.isAble(user, resource, operation)) {
			throw new UnauthorizedException(user, resource, operation);
		} else {
			try {
				return context.proceed();
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}
	}
}
