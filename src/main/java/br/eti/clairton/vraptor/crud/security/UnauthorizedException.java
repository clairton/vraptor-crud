package br.eti.clairton.vraptor.crud.security;

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

	/**
	 * Construtor com possibilidade de detalhamento da não autorização.
	 * 
	 * @param user
	 *            usuario que não esta autorizado
	 * @param app
	 *            aplicação em que não esta autorizado
	 * @param resource
	 *            recurso que não esta autorizado
	 * @param operation
	 *            operação que não esta autorizado
	 */
	public UnauthorizedException(final String user, final String app,
			final String resource, final String operation) {
		this(String.format("\"%s\" não está autorizado(a) a acessar \"%s#%s\" em \"%s\"", user,
				resource, operation, app));
	}

}
