package br.eti.clairton.vraptor.crud.security;


/**
 * Serviço que verifica se o usuario está autorizado.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
public interface Authorizator {

	/**
	 * Verifica se o usuario esta habilitado a acessar.
	 * 
	 * @param user
	 *            usuario
	 * @param resource
	 *            recurso
	 * @param operation
	 *            operation
	 * @return true/false
	 */
	public boolean isAble(final String user, final String resource,
			final String operation);
}
