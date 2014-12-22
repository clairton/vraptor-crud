package br.eti.clairton.vraptor.crud.security;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

public class AuthenticatorInMemory implements Authenticator {
	public static final Map<String, String> USERS = new HashMap<>();
	static {
		USERS.put("admin", "123456");
	}

	@Override
	public Boolean isValid(@NotNull final String user,
			@NotNull final String password) {
		try {
			return USERS.get(user).equals(password);
		} catch (final NullPointerException e) {
			return Boolean.FALSE;
		}
	}

}
