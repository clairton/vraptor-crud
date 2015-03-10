package br.eti.clairton.vraptor.crud;

import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.view.DefaultPathResolver;

/**
 * Torna os controllers possíveis de serem usados por uma interface Single Page
 * Application.
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
	public SinglePagePathResolver(FormatResolver resolver) {
		super(resolver);
	}

	/**
	 * Sempre deve retorna "index.html", pq em uma SPA não há navegação.
	 */
	@Override
	public String pathFor(ControllerMethod method) {
		return "index.html";
	}
}
