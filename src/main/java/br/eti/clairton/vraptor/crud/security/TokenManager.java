package br.eti.clairton.vraptor.crud.security;

import javax.security.auth.login.CredentialNotFoundException;
import javax.validation.constraints.NotNull;

/**
 * Controla a sessões de usuários.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 *
 */
public interface TokenManager {

	/**
	 * Cria um nova sessão se os dados recebidos por padrão forem validos.
	 * 
	 * @param user
	 *            usuário
	 * @param password
	 *            senha
	 * 
	 * @return token para identificação do usuário
	 */
	String create(@NotNull final String user, @NotNull final String password)
			throws CredentialNotFoundException;

	/**
	 * Destroy a sessão com o token recebido como parametro.
	 * 
	 * @param token
	 *            que terá a sessão encerrada
	 */
	void destroy(@NotNull final String token);

	/**
	 * Valida se o token esta valido
	 * 
	 * @param token
	 *            token a ser validado
	 * @return true/false
	 */
	Boolean isValid(@NotNull final String token);
}
