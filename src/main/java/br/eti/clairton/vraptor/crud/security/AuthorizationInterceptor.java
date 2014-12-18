package br.eti.clairton.vraptor.crud.security;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.logging.log4j.Logger;

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
	private final String app;
	private final Authorizator authorizator;
	private final Logger logger;

	@Inject
	public AuthorizationInterceptor(@App final String app,
			@User final String user, final Authorizator authorizator,
			final Logger logger) {
		super();
		this.app = app;
		this.user = user;
		this.authorizator = authorizator;
		this.logger = logger;
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
		logger.debug("Interceptando {}#{}", new Object[] {
				target.getClass().getSimpleName(), operation });
		if (authorizator.isAble(user, app, resource, operation)) {
			try {
				return context.proceed();
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		} else {
			throw new UnauthorizedException(user, app, resource, operation);
		}
	}
}
