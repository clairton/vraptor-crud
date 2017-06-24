package br.eti.clairton.vraptor.crud.controller;

import java.util.ArrayList;

import javax.enterprise.inject.Vetoed;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Result;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.paginated.collection.Meta;
import br.eti.clairton.paginated.collection.PaginatedCollection;
import br.eti.clairton.paginated.collection.PaginatedMetaList;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.repository.http.QueryParser;
import br.eti.clairton.vraptor.crud.model.Aplicacao;


@Vetoed
public class AplicacaoController2 extends CrudeController<Aplicacao> {

	public AplicacaoController2(
			Class<Aplicacao> modelType, 
			Repository repository, 
			Result result, 
			Inflector inflector,
			ServletRequest request, 
			QueryParser queryParser, 
			HttpServletResponse response, 
			FileService service) {
		super(modelType, repository, result, inflector, request, queryParser, response, service);
	}

	@Override
	public PaginatedCollection<Aplicacao, Meta> find() {
		return new PaginatedMetaList<Aplicacao>(new ArrayList<>(), new Meta(1l, 1l));
	}
}