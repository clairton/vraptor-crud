package br.eti.clairton.vraptor.crud;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.validation.constraints.NotNull;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.Result;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Language;
import br.eti.clairton.repository.Model;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.tenant.TenantValue;

public abstract class TenantCrudController<T extends Model> extends
		CrudController<T> {

	private final Object tenantValue;
	private final Repository repository;

	/**
	 * CDI only.
	 */
	@Deprecated
	protected TenantCrudController() {
		this(null, null, null, null, null, null, null, null);
	}

	/**
	 * Construtor Padrão.
	 * 
	 * @param modelType
	 *            tipo do modelo
	 * @param repository
	 *            instancia do repository
	 * @param result
	 *            instancia de result
	 * @param inflector
	 *            instancia de inflector
	 * @param mirror
	 *            instancia de mirror
	 * @param request
	 *            instancia de request
	 * @param queryParser
	 *            instancia de quey parser
	 * @param tenantValue
	 *            valor do tenant
	 */
	public TenantCrudController(final @NotNull Class<T> modelType,
			final @NotNull Repository repository, final @NotNull Result result,
			@Language final @NotNull Inflector inflector,
			final @NotNull Mirror mirror,
			final @NotNull ServletRequest request,
			final @NotNull QueryParamParser queryParser,
			final @NotNull @TenantValue Object tenantValue) {
		super(modelType, repository, result, inflector, mirror, request,
				queryParser);
		this.tenantValue = tenantValue;
		this.repository = repository;
	}

	@PostConstruct
	public void setTenant() {
		repository.tenantValue(tenantValue);
	}
}
