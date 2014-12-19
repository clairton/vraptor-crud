package br.eti.clairton.vraptor.crud.security;

public interface Authenticator {
	Boolean isValid(final String user, final String password);
}
