package br.eti.clairton.vraptor.crud.controller;

import java.io.File;
import java.util.Collection;

import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Result;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Language;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.repository.Tenant;
import br.eti.clairton.vraptor.crud.model.Aplicacao;

@Controller
public class AplicacaoController extends CrudController<Aplicacao> implements
		ExportControllerMixin<Aplicacao> {
	private final Result result;
	private final HttpServletResponse response;

	@Deprecated
	protected AplicacaoController() {
		this(null, null, null, null, null, null);
	}

	@Inject
	public AplicacaoController(@Tenant final Repository repository,
			final Result result, @Language final Inflector inflector,
			final ServletRequest request, final QueryParser queryParser, final HttpServletResponse response) {
		super(Aplicacao.class, repository, result, inflector, request,
				queryParser);
		this.result = result;
		this.response = response;
	}

	@Ignore
	@Override
	public Result getResult() {
		return result;
	}
	
	@Override
	@Ignore
	public HttpServletResponse getResponse() {
		return response;
	}

	@Override
	@Ignore
	public FileService getService() {
		return new FileService() {
			@Override
			@Ignore
			public String toFile(Collection<?> collection) {
				return "src/test/resources/test.properties";
			}

			@Override
			@Ignore
			public File toFile(final String path) {
				return new File(path);
			}
		};
	}
}