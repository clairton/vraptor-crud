package br.eti.clairton.vraptor.crud.controller;

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.eti.clairton.security.Authenticated;
import br.eti.clairton.security.Protected;
import br.eti.clairton.vraptor.crud.interceptor.ExceptionVerifier;

@Controller
public class DownloadController {
	private final HttpServletResponse response;
	private final FileService service;
	
	@Deprecated
	public DownloadController() {
		this(null, null);
	}

	@Inject
	public DownloadController(final HttpServletResponse response, final FileService service) {
		this.response = response;
		this.service = service;
	}

	/**
	 * Recupera o arquivo gerado.<br/>
	 */
	@Get("/{path}")
	@Protected
	@Authenticated
	@ExceptionVerifier
	public void get(final @NotNull String path) {
		final File file = service.toFile(path);
		try {
			response.setContentType("application/octet-stream");
			response.setContentLength((int) file.length());
			final String disposition = format("attachment; filename=\"%s\"", file.getName());
			response.setHeader("Content-Disposition", disposition);

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
}
