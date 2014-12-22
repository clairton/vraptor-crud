package br.eti.clairton.vraptor.crud.security;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.auth.login.CredentialNotFoundException;
import javax.validation.constraints.NotNull;

import org.jboss.weld.exceptions.IllegalStateException;

@ApplicationScoped
public class TokenManagerInMemory implements TokenManager {
	private final Map<String, String> repository = new HashMap<>();
	private final MessageDigest crypt;

	private final Authenticator authenticator;
	private final Charset charset;

	@Inject
	public TokenManagerInMemory(final Authenticator authenticator) {
		try {
			crypt = MessageDigest.getInstance("SHA-1");
			this.authenticator = authenticator;
			charset = Charset.forName("UTF-8");
		} catch (final Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String create(@NotNull final String user,
			@NotNull final String password) throws CredentialNotFoundException {
		if (authenticator.isValid(user, password)) {
			byte[] bytes = (user + password + new Random().nextLong())
					.getBytes();
			final String token = new String(crypt.digest(bytes), charset);
			repository.put(user, token);
			return token;
		} else {
			throw new CredentialNotFoundException();
		}
	}

	@Override
	public void destroy(@NotNull final String token) {
		for (final Entry<String, String> entry : repository.entrySet()) {
			if (token.equals(entry.getValue())) {
				repository.remove(entry.getKey());
				break;
			}
		}
	}

	@Override
	public Boolean isValid(@NotNull final String token) {
		return repository.containsValue(token);
	}
}
