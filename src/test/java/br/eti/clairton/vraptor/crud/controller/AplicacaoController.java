package br.eti.clairton.vraptor.crud.controller;

import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Result;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.repository.http.QueryParser;
import br.eti.clairton.vraptor.crud.model.Aplicacao;

@Controller
public class AplicacaoController extends CrudeController<Aplicacao>{

	@Deprecated
	protected AplicacaoController() {
		this(null, null, null, null, null, null, null);
	}

	@Inject
	public AplicacaoController(
			final Repository repository,
			final Result result, 
			final Inflector inflector,
			final ServletRequest request, 
			final QueryParser queryParser,
			final HttpServletResponse response,
			final FileService service) {
		super(Aplicacao.class, repository, result, inflector, request, queryParser, response, service);
	}
}