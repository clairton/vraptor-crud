package br.eti.clairton.vraptor.crud.controller;

import static br.com.caelum.vraptor.view.Results.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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
		final String path = getService().toFile(collection);
		final Map<String, String> map = new HashMap<>();
		map.put("file_path", path);
		getResult().use(json()).withoutRoot().from(map).serialize();
	}
	
	/**
	 * Recupera o arquivo gerado.<br/>
	 */
	@Get("/export/{path}")
	@Protected
	@Authenticated
	@ExceptionVerifier
	default void download(String path) {
		final File file = getService().toFile(path);
		try{
			getResponse().setContentType("application/octet-stream");
			getResponse().setContentLength((int) file.length());
			getResponse().setHeader( "Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
	
			OutputStream out = getResponse().getOutputStream();
			try (FileInputStream in = new FileInputStream(file)) {
			    byte[] buffer = new byte[4096];
			    int length;
			    while ((length = in.read(buffer)) > 0) {
			        out.write(buffer, 0, length);
			    }
			}
			out.flush();
		}catch(final Exception e){
			throw new RuntimeException(e);
		}
	}

	@Ignore
	PaginatedCollection<T, Meta> find();

	@Ignore
	FileService getService();

	@Ignore
	Result getResult();

	@Ignore
	HttpServletResponse getResponse();
}
