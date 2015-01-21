package br.eti.clairton.vraptor.crud.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.inject.Inject;
import javax.security.auth.login.CredentialNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Post;
import br.eti.clairton.vraptor.crud.ExceptionVerifier;

@Controller
public class SessionController implements Serializable {
	private static final long serialVersionUID = 1L;
	private final TokenManager tokenManager;
	private final HttpServletResponse response;

	/**
	 * CDI eye only.
	 */
	@Deprecated
	protected SessionController() {
		this(null, null);
	}

	/**
	 * Construtor com parâmetros com parametros.
	 * 
	 * @param tokenManager
	 *            manager de tokens
	 * @param response
	 *            resposta servlet
	 */
	@Inject
	public SessionController(final TokenManager tokenManager,
			final HttpServletResponse response) {
		this.tokenManager = tokenManager;
		this.response = response;
	}

	@Post
	@ExceptionVerifier
	@Consumes("application/json")
	public void create(@NotNull final String user,
			@NotNull final String password) throws CredentialNotFoundException {
		try {
			final String token = tokenManager.create(user, password);
			final PrintWriter writer = response.getWriter();
			response.setStatus(201);
			final String json = String.format("{\"token\": \"%s\"}", token);
			writer.print(json);
			writer.flush();
			writer.close();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Delete
	@ExceptionVerifier
	@Consumes("application/json")
	public void destroy(final String key) {
		tokenManager.destroy(key);
	}

}