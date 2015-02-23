package br.eti.clairton.vraptor.crud.security;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.environment.Property;
import br.com.caelum.vraptor.view.Results;
import br.com.caelum.vraptor.view.Status;

/**
 * Adiciona os valores configurados para o CORS.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Intercepts
public class CORSInterceptor {

	@Inject
	private Result result;

	@Inject
	private HttpServletRequest request;

	@Inject
	@Property(value = "Access-Control-Allow-Origin", defaultValue = "*")
	private String origin;

	@Inject
	@Property(value = "Access-Control-Allow-Credentials", defaultValue = "true")
	private String credentials;

	@Inject
	@Property(value = "Access-Control-Expose-Headers", defaultValue = "Content-Type, Location")
	private String expose;

	@BeforeCall
	public void intercept() throws InterceptionException {
		final String origin;
		if (request.getHeader("origin") != null) {
			origin = request.getHeader("origin");
		} else {
			origin = this.origin;
		}
		final Status status = result.use(Results.status());
		status.header("Access-Control-Allow-Origin", origin);
		status.header("Access-Control-Allow-Credentials", credentials);
		status.header("Access-Control-Expose-Headers", expose);
	}
}