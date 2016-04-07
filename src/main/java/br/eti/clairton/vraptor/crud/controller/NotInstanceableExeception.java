package br.eti.clairton.vraptor.crud.controller;

/**
 * Exceção para avisar que entidade não é instanciavel.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
public class NotInstanceableExeception extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Construtor Padrão.
	 * 
	 * @param cause
	 *            causa
	 */
	public NotInstanceableExeception(final Throwable cause) {
		super(cause);
	}
}
