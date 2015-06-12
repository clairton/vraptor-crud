package br.eti.clairton.vraptor.crud.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Result;
import br.eti.clairton.paginated.collection.Meta;
import br.eti.clairton.paginated.collection.PaginatedCollection;
import br.eti.clairton.paginated.collection.PaginatedMetaList;
import br.eti.clairton.vraptor.crud.model.Aplicacao;

public class AplicacaoController2 implements ExportControllerMixin<Aplicacao> {
	private final Result result;
	private final HttpServletResponse response;

	@Inject
	public AplicacaoController2(final Result result,
			final HttpServletResponse response) {
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
				return "src/test/resources/test.csv";
			}

			@Override
			@Ignore
			public File toFile(final String path) {
				return new File(path);
			}
		};
	}

	@Override
	public PaginatedCollection<Aplicacao, Meta> find() {
		return new PaginatedMetaList<Aplicacao>(new ArrayList<>(), new Meta(1l, 1l));
	}
}