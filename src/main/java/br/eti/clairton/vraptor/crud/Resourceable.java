package br.eti.clairton.vraptor.crud;

import br.eti.clairton.repository.Model;

/**
 * Contrato para um recurso poder ser oferecido. Deve ser implementado pelo
 * controller para ser usado no interceptor ativado pela anootation
 * {@link Authorized}
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 *
 */
public abstract class Resourceable {
	private final String resourceName;

	/**
	 * Construtor padrão.
	 * 
	 * @param resource
	 *            classe do recurso que será oferecido.
	 */
	public Resourceable(final Class<? extends Model> resource) {
		super();
		if (resource != null) {
			final StringBuilder builder = new StringBuilder();
			final String simpleName = resource.getSimpleName();
			builder.append(simpleName.substring(0, 1).toLowerCase());
			builder.append(simpleName.substring(1));
			this.resourceName = builder.toString();
		} else {
			resourceName = null;
		}
	}

	/**
	 * Devolve o nome do recurso, por padrão devolve o nome da entidade/modelo
	 * ao qual se refere o controller, por exemplo, se o tipo for
	 * com.dominio.Pessoa, o nome do recurso seria pessoa
	 * 
	 * @return nome do recurso
	 */
	public String getResourceName() {
		return resourceName;
	}
}
