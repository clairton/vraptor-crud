package br.eti.clairton.vraptor.crud.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.auth.login.CredentialNotFoundException;
import javax.validation.constraints.NotNull;

@ApplicationScoped
public class TokenManagerInMemory implements TokenManager {
	private final static Map<String, String> REPOSITORY = new HashMap<>();
	private final MessageDigest crypt;

	private final Authenticator authenticator;

	@Inject
	public TokenManagerInMemory(final Authenticator authenticator)
			throws NoSuchAlgorithmException {
		crypt = MessageDigest.getInstance("SHA-1");
		this.authenticator = authenticator;
	}

	@Override
	public String create(@NotNull final String user,
			@NotNull final String password) throws CredentialNotFoundException {
		if (authenticator.isValid(user, password)) {
			byte[] bytes = (user + password + new Random().nextLong())
					.getBytes();
			return new String(crypt.digest(bytes));
		} else {
			throw new CredentialNotFoundException();
		}
	}

	@Override
	public void destroy(@NotNull final String token) {
		for (final Entry<String, String> entry : REPOSITORY.entrySet()) {
			if (token.equals(entry.getValue())) {
				REPOSITORY.remove(entry.getKey());
				break;
			}
		}
	}

	@Override
	public Boolean isValid(@NotNull final String token) {
		return REPOSITORY.containsValue(token);
	}

}
