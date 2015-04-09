package br.eti.clairton.vraptor.crud.controller;

import javax.inject.Inject;
import javax.servlet.ServletRequest;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Result;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Language;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.repository.Tenant;
import br.eti.clairton.vraptor.crud.controller.CrudController;
import br.eti.clairton.vraptor.crud.controller.QueryParser;
import br.eti.clairton.vraptor.crud.hypermedia.Hypermediable;
import br.eti.clairton.vraptor.crud.model.Aplicacao;

@Controller
public class AplicacaoController extends CrudController<Aplicacao> {
	@Deprecated
	protected AplicacaoController() {
		this(null, null, null, null, null, null);
	}

	@Inject
	public AplicacaoController(@Tenant final Repository repository,
			final @Hypermediable Result result, @Language final Inflector inflector,
			final Mirror mirror, final ServletRequest request,
			final QueryParser queryParser) {
		super(Aplicacao.class, repository, result, inflector, mirror, request,
				queryParser);
	}
}