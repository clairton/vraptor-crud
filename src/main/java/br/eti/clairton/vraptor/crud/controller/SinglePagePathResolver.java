package br.eti.clairton.vraptor.crud.controller;

import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.view.DefaultPathResolver;

/**
 * Torna os controllers possíveis de serem usados por uma cliente SPA.</br>
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Specializes
public class SinglePagePathResolver extends DefaultPathResolver {
	/**
	 * @deprecated CDI eyes only
	 */
	protected SinglePagePathResolver() {
		this(null);
	}

	@Inject
	public SinglePagePathResolver(final FormatResolver resolver) {
		super(resolver);
	}

	/**
	 * Sempre deve retorna "index.html", pq em uma SPA não há navegação.
	 */
	@Override
	public String pathFor(final ControllerMethod method) {
		return "index.html";
	}
}
