package br.eti.clairton.vraptor.crud.controller;

import java.util.ArrayList;

import javax.inject.Inject;

import br.com.caelum.vraptor.Result;
import br.eti.clairton.paginated.collection.Meta;
import br.eti.clairton.paginated.collection.PaginatedCollection;
import br.eti.clairton.paginated.collection.PaginatedMetaList;
import br.eti.clairton.vraptor.crud.model.Aplicacao;

public class AplicacaoController2 implements ExportControllerMixin<Aplicacao> {
	private final Result result;

	@Inject
	public AplicacaoController2(final Result result) {
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

	@Override
	public PaginatedCollection<Aplicacao, Meta> find() {
		return new PaginatedMetaList<Aplicacao>(new ArrayList<>(), new Meta(1l, 1l));
	}
}