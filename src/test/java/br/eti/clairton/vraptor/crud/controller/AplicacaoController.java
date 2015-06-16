package br.eti.clairton.vraptor.crud.controller;

import javax.inject.Inject;
import javax.servlet.ServletRequest;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Result;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Language;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.repository.Tenant;
import br.eti.clairton.vraptor.crud.model.Aplicacao;
import br.eti.clairton.vraptor.crud.query.QueryParser;

@Controller
public class AplicacaoController extends CrudController<Aplicacao> implements
		ExportControllerMixin<Aplicacao> {
	private final Result result;

	@Deprecated
	protected AplicacaoController() {
		this(null, null, null, null, null);
	}

	@Inject
	public AplicacaoController(@Tenant final Repository repository,
			final Result result, @Language final Inflector inflector,
			final ServletRequest request, final QueryParser queryParser) {
		super(Aplicacao.class, repository, result, inflector, request,
				queryParser);
		this.result = result;
	}

	@Ignore
	@Override
	public Result getResult() {
		return result;
	}

	@Override
	@Ignore
	public FileService getService() {
		return new FileServiceMock();
	}
}