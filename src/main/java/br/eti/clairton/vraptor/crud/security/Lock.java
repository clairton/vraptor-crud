package br.eti.clairton.vraptor.crud.security;

import javax.validation.constraints.NotNull;

public interface Lock {
	Boolean isValid(@NotNull final String user, @NotNull final String password);
}
