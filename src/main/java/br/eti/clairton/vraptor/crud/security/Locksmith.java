package br.eti.clairton.vraptor.crud.security;

import javax.security.auth.login.CredentialNotFoundException;
import javax.validation.constraints.NotNull;

/**
 * Controla a chaves de acesso dos usuários.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 *
 */
public interface Locksmith {

	/**
	 * Cria um nova chave se os dados recebidos forem validos.
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
	 * Destroy a chave com o token/usuario recebido como parametro.
	 * 
	 * @param key
	 *            usuario ou token que terá a sessão encerrada
	 */
	void invalidate(@NotNull final String key);

	/**
	 * Valida se a chave esta valido
	 * 
	 * @param token
	 *            token a ser validado
	 * @return true/false
	 */
	Boolean isValid(@NotNull final String token);

	/**
	 * Retorna o nome do usuario ao qual a chave pertence.
	 * 
	 * @param token
	 *            chave do usuario
	 * @return usuario dono da chave
	 */
	String getUserByToken(@NotNull final String token);
}
