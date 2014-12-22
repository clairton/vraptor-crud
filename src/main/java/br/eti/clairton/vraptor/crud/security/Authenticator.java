package br.eti.clairton.vraptor.crud.security;

import javax.validation.constraints.NotNull;

public interface Authenticator {
	Boolean isValid(@NotNull final String user, @NotNull final String password);
}
