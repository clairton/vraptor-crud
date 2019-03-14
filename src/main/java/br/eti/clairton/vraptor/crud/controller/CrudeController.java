package br.eti.clairton.vraptor.crud.controller;

import static java.lang.String.format;
import static java.nio.file.Files.probeContentType;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Result;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Language;
import br.eti.clairton.model.Base;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.repository.http.QueryParser;
import br.eti.clairton.security.Authenticated;
import br.eti.clairton.security.Operation;
import br.eti.clairton.security.Protected;
import br.eti.clairton.vraptor.crud.interceptor.ExceptionVerifier;

public abstract class CrudeController<T extends Base> extends CrudController<T>{
	private final HttpServletResponse response;
	private final FileService service;

	public CrudeController(
			@NotNull final Class<T> modelType, 
			@NotNull final Repository repository,
			@NotNull final Result result, 
			@Language @NotNull final Inflector inflector,
			@NotNull final ServletRequest request, 
			@NotNull final QueryParser queryParser,
			@NotNull final HttpServletResponse response,
			@NotNull final FileService service) {
		super(modelType, repository, result, inflector, request, queryParser);
		this.response = response;
		this.service = service;
	}
	
	/**
	 * Exporta os recursos.
	 * Parametros para pesquisa são mandados na URL.
	 * 	
	 * @param format formato de saida da exportação
	 */
	@Ignore
	public void export(String format) {
		final Collection<T> collection = find();
		final String path = service.toFile(collection, getParameters(), "." + format);
		final File file = service.toFile(path);
		export(file);
	}

	
	@Ignore
	public Map<String, Object> getParameters(){
		return new HashMap<>();
	}
	
	
	@Ignore
	public void export(final File file) {
		try {
			response.setContentLength((int) file.length());
			final String disposition = format("attachment; filename=\"%s\"", file.getName());
			response.setHeader("Content-Disposition", disposition);
			response.setContentType(probeContentType(file.toPath()));
			final OutputStream out = response.getOutputStream();
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
	 * Exporta os recursos para csv.
	 * Parametros para pesquisa são mandados na URL.
	 */
	@Get(".csv")
	@Protected
	@Authenticated
	@ExceptionVerifier
	@Operation("export")
	public void csv() {
		export("csv");
	}

	/**
	 * Exporta os recursos para html.
	 * Parametros para pesquisa são mandados na URL.
	 */
	@Get(".pdf")
	@Protected
	@Authenticated
	@ExceptionVerifier
	@Operation("export")
	public void pdf() {
		export("pdf");
	}
}
