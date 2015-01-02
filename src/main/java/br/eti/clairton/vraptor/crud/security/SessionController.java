package br.eti.clairton.vraptor.crud.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Map;

import javax.inject.Inject;
import javax.security.auth.login.CredentialNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.serialization.gson.GsonBuilderWrapper;
import br.eti.clairton.vraptor.crud.ExceptionVerifier;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;

@Controller
public class SessionController implements Serializable {
	private static final long serialVersionUID = 1L;
	private final TokenManager tokenManager;
	private final HttpServletResponse response;
	private final HttpServletRequest request;
	private final Gson gson;

	/**
	 * CDI eye only.
	 */
	@Deprecated
	protected SessionController() {
		this(null, null, null, null);
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
			HttpServletRequest request, final HttpServletResponse response,
			GsonBuilderWrapper builder) {
		this.tokenManager = tokenManager;
		this.request = request;
		this.response = response;
		this.gson = builder.create();
	}

	@Post
	@Consumes("application/json")
	@ExceptionVerifier
	public void create() throws CredentialNotFoundException {
		try {
			final BufferedReader reader = request.getReader();
			final String json = CharStreams.toString(reader);
			final Map<?, ?> hash = gson.fromJson(json, Map.class);
			final String user = hash.get("user").toString();
			final String password = hash.get("password").toString();
			final String token = tokenManager.create(user, password);
			final PrintWriter writer = response.getWriter();
			writer.print(token);
			writer.flush();
			writer.close();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Delete
	public void destroy(final String user) {
		tokenManager.destroy(user);
	}

}
