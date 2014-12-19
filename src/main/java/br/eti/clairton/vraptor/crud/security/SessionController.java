package br.eti.clairton.vraptor.crud.security;

import java.io.Serializable;

import javax.inject.Inject;
import javax.security.auth.login.CredentialNotFoundException;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Post;

@Controller
public class SessionController implements Serializable {
	private static final long serialVersionUID = 1L;
	private final TokenManager tokenManager;
	private final HttpServletResponse response;

	@Inject
	public SessionController(final TokenManager session,
			final HttpServletResponse response) {
		this.tokenManager = session;
		this.response = response;
	}

	@Post
	public void login(final String user, final String password)
			throws CredentialNotFoundException {
		final String token = tokenManager.create(user, password);
		response.addHeader("Authorization", token);
	}

	@Delete
	public void logout(final String user) {
		tokenManager.destroy(user);
	}

}
