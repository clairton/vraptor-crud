package br.eti.clairton.vraptor.crud.controller;

import javax.inject.Inject;
import javax.servlet.ServletRequest;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Result;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Language;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.vraptor.crud.controller.CrudController;
import br.eti.clairton.vraptor.crud.controller.QueryParser;
import br.eti.clairton.vraptor.crud.model.Recurso;

@Controller
public class RecursoController extends CrudController<Recurso> {
	@Deprecated
	protected RecursoController() {
		this(null, null, null, null, null, null);
	}

	@Inject
	public RecursoController(final Repository repository, final Result result,
			@Language final Inflector inflector, final Mirror mirror,
			final ServletRequest request, final QueryParser queryParser) {
		super(Recurso.class, repository, result, inflector, mirror, request,
				queryParser);
	}
}