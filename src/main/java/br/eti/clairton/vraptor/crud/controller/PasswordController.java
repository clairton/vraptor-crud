package br.eti.clairton.vraptor.crud.controller;

import static br.com.caelum.vraptor.view.Results.http;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.serialization.gson.WithRoot;
import br.eti.clairton.security.Lock;
import br.eti.clairton.vraptor.crud.interceptor.ExceptionVerifier;


@Controller
@Path("passwords")
public class PasswordController {
	private final Lock lock;

	private final Result result;

	@Deprecated
	public PasswordController() {
		this(null, null);
	}
	
	@Inject
	public PasswordController(final Lock lock, final Result result) {
		super();
		this.lock = lock;
		this.result = result;
	}
	
	@Put
	@ExceptionVerifier
	@Consumes(value="application/json", options=WithRoot.class)
	public void update(@NotNull @Size(min = 1) final String user, 
						@NotNull @Size(min = 1) final String currentPassword, 
						@NotNull @Size(min = 1) final String newPassword){
		lock.update(user, currentPassword, newPassword);
		result.use(http()).setStatusCode(204);
	}
}
