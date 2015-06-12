package br.eti.clairton.vraptor.crud.controller;

import static br.com.caelum.vraptor.view.Results.json;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.com.caelum.vraptor.Get;
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
	@Post("/export")
	@Protected
	@Authenticated
	@ExceptionVerifier
	default void export() {
		final Collection<T> collection = find();
		final String path = toFile(collection);
		final Map<String, String> map = new HashMap<>();
		map.put("file_path", path);
		getResult().use(json()).from(map).serialize();
	}
	
	/**
	 * Recupera o arquivo gerado.<br/>
	 */
	@Get("/export/{path}")
	@Protected
	@Authenticated
	@ExceptionVerifier
	default void download(String path) {
		final File file = toFile(path);
	}

	@Ignore
	PaginatedCollection<T, Meta> find();

	@Ignore
	String toFile(Collection<T> collection);

	@Ignore
	File toFile(String file);

	@Ignore
	Result getResult();
}
