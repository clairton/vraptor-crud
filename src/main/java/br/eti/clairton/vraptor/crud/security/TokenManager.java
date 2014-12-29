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
	 * 
	 * @throws CredentialNotFoundException
	 *             caso usuario/senha não existam ou sejam incompativeis
	 */
	String create(@NotNull final String user, @NotNull final String password)
			throws CredentialNotFoundException;

	/**
	 * Destroy a sessão com o token/usuario recebido como parametro.
	 * 
	 * @param key
	 *            usuario ou token que terá a sessão encerrada
	 */
	void destroy(@NotNull final String key);

	/**
	 * Valida se o token esta valido
	 * 
	 * @param token
	 *            token a ser validado
	 * @return true/false
	 */
	Boolean isValid(@NotNull final String token);

	/**
	 * Retorna o nome do usuario através do token.
	 * 
	 * @param token
	 *            token do usuario
	 * @return usuario do token
	 */
	String getUserByToken(@NotNull final String token);
}
