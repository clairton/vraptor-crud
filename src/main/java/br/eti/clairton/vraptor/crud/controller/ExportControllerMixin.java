package br.eti.clairton.vraptor.crud.controller;

import static br.com.caelum.vraptor.view.Results.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.eti.clairton.paginated.collection.Meta;
import br.eti.clairton.paginated.collection.PaginatedCollection;
import br.eti.clairton.repository.Model;
import br.eti.clairton.security.Authenticated;
import br.eti.clairton.security.Protected;
import br.eti.clairton.vraptor.crud.interceptor.ExceptionVerifier;

public interface ExportControllerMixin<T extends Model> {

	/**
	 * Exporta os recursos.<br/>
	 * Parametros para pesquisa são mandados na URL.
	 */
	@Post(".{format}")
	@Protected
	@Authenticated
	@ExceptionVerifier
	default void export(String format) {
		final Collection<T> collection = find();
		final String path = getService().toFile(collection);
		final Map<String, String> map = new HashMap<>();
		map.put("file_path", path);
		getResult().use(json()).withoutRoot().from(map).serialize();
	}

	@Ignore
	PaginatedCollection<T, Meta> find();

	@Ignore
	FileService getService();

	@Ignore
	Result getResult();
}
