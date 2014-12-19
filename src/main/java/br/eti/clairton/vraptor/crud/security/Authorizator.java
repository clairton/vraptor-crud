package br.eti.clairton.vraptor.crud.security;

import javax.validation.constraints.NotNull;

/**
 * Serviço que verifica se o usuario está autorizado.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
public interface Authorizator {

	/**
	 * Verifica se o usuario esta habilitado a acessar o recurso discriminado.
	 * 
	 * @param app
	 *            aplicacão
	 * 
	 * @param user
	 *            usuario
	 * @param resource
	 *            recurso
	 * @param operation
	 *            operation
	 * @return true/false
	 */
	public Boolean isAble(@NotNull final String user,
			@NotNull final String app, @NotNull final String resource,
			@NotNull final String operation);
}
