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
import br.eti.clairton.security.Service;
import br.eti.clairton.vraptor.crud.interceptor.ExceptionVerifier;


@Controller
@Path("passwords")
public class PasswordController {
	private final Service service;

	private final Result result;

	@Deprecated
	public PasswordController() {
		this(null, null);
	}
	
	@Inject
	public PasswordController(final Service service, final Result result) {
		super();
		this.service = service;
		this.result = result;
	}
	
	@Put
	@ExceptionVerifier
	@Consumes(value="application/json", options=WithRoot.class)
	public void update(@NotNull @Size(min = 1) final String user, 
						@NotNull @Size(min = 1) final String currentPassword, 
						@NotNull @Size(min = 1) final String newPassword){
		service.update(user, currentPassword, newPassword);
		result.use(http()).setStatusCode(204);
	}
}
