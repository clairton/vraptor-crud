package br.eti.clairton.vraptor.crud;

import javax.inject.Inject;
import javax.interceptor.AroundConstruct;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.logging.log4j.Logger;

import br.eti.clairton.repository.Repository;
import br.eti.clairton.tenant.TenantValue;

/**
 * Intercepta a contrução de beans anotadas com {@link TenantController}.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Interceptor
@TenantController
public class TenantControllerInterceptor {
	private final Object tenantValue;

	private final Logger logger;

	/**
	 * Construtor padrão.
	 * 
	 * @param tenantValue
	 *            valor do tenant
	 * @param logger
	 *            instancia de {@link Logger}
	 */
	@Inject
	public TenantControllerInterceptor(final @TenantValue Object tenantValue,
			final Logger logger) {
		this.tenantValue = tenantValue;
		this.logger = logger;
	}

	/**
	 * Intercepta o construtor adicionar o tenant ao repository.
	 * 
	 * @param invocationContext
	 *            contexto da invocação
	 * @return o retorno do metodo do context
	 */
	@AroundConstruct
	public Object invoke(final InvocationContext context) throws Throwable {
		logger.debug("Setando tenant com valor \"{}\"", tenantValue);
		final Repository repository = (Repository) context.getParameters()[0];
		repository.tenantValue(tenantValue);
		return context.proceed();
	}
}
