package br.eti.clairton.vraptor.crud.controller;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import br.eti.clairton.paginated.collection.Meta;
import br.eti.clairton.paginated.collection.PaginatedCollection;
import br.eti.clairton.paginated.collection.PaginatedMetaList;
import br.eti.clairton.vraptor.crud.model.Aplicacao;

public class AplicacaoController2 implements ExportControllerMixin<Aplicacao> {
	private final HttpServletResponse response;

	@Inject
	public AplicacaoController2(final HttpServletResponse result) {
		this.response = result;
	}

	@Ignore
	@Override
	public HttpServletResponse getResponse() {
		return response;
	}

	@Override
	@Ignore
	public FileService getService() {
		return new FileServiceMock();
	}

	@Override
	public PaginatedCollection<Aplicacao, Meta> find() {
		return new PaginatedMetaList<Aplicacao>(new ArrayList<>(), new Meta(1l,
				1l));
	}
}