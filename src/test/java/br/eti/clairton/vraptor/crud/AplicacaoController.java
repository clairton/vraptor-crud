package br.eti.clairton.vraptor.crud;

import javax.inject.Inject;
import javax.servlet.ServletRequest;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Result;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Language;
import br.eti.clairton.repository.Repository;

@Controller
@TenantController
public class AplicacaoController extends CrudController<Aplicacao> {
	@Deprecated
	protected AplicacaoController() {
		this(null, null, null, null, null, null);
	}

	@Inject
	public AplicacaoController(final Repository repository,
			final Result result, @Language final Inflector inflector,
			final Mirror mirror, final ServletRequest request,
			final QueryParamParser queryParser) {
		super(Aplicacao.class, repository, result, inflector, mirror, request,
				queryParser);
	}
}