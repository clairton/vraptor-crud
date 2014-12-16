package br.eti.clairton.vraptor.crud;

/**
 * Exceção lançada quando o usuario não tem autorização.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 *
 */
public class UnauthorizedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Construtor Padrão.
	 */
	public UnauthorizedException() {
		super();
	}

	/**
	 * Construtor com Parametros.
	 * 
	 * @param message
	 *            mensagem a ser mostrada
	 */
	public UnauthorizedException(final String message) {
		super(message);
	}
}
