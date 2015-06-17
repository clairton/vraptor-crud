package br.eti.clairton.vraptor.crud.controller;

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Get;
import br.eti.clairton.paginated.collection.Meta;
import br.eti.clairton.paginated.collection.PaginatedCollection;
import br.eti.clairton.repository.Model;
import br.eti.clairton.security.Authenticated;
import br.eti.clairton.security.Operation;
import br.eti.clairton.security.Protected;
import br.eti.clairton.vraptor.crud.interceptor.ExceptionVerifier;

public interface ExportControllerMixin<T extends Model> {

	/**
	 * Exporta os recursos.<br/>
	 * Parametros para pesquisa são mandados na URL.
	 */
	@Ignore
	default void export(String format) {
		final Collection<T> collection = find();
		final String path = getService().toFile(collection, new HashMap<>(), "." + format);
		final File file = getService().toFile(path);
		try {
			getResponse().setContentType("application/octet-stream");
			getResponse().setContentLength((int) file.length());
			final String disposition = format("attachment; filename=\"%s\"", file.getName());
			getResponse().setHeader("Content-Disposition", disposition);

			final OutputStream out = getResponse().getOutputStream();
			try (final FileInputStream in = new FileInputStream(file)) {
				byte[] buffer = new byte[4096];
				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
			}
			out.flush();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Exporta os recursos para csv.<br/>
	 * Parametros para pesquisa são mandados na URL.
	 */
	@Get(".csv")
	@Protected
	@Authenticated
	@ExceptionVerifier
	@Operation("export")
	default void csv() {
		export("csv");
	}

	/**
	 * Exporta os recursos para html.<br/>
	 * Parametros para pesquisa são mandados na URL.
	 */
	@Get(".pdf")
	@Protected
	@Authenticated
	@ExceptionVerifier
	@Operation("export")
	default void pdf() {
		export("pdf");
	}

	@Ignore
	PaginatedCollection<T, Meta> find();

	@Ignore
	FileService getService();

	@Ignore
	HttpServletResponse getResponse();
}
