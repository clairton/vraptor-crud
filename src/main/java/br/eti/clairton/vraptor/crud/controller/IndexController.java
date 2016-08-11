package br.eti.clairton.vraptor.crud.controller;

import javax.inject.Inject;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Result;

@Controller
@Path("/")
public class IndexController {
	private final Result result;
	
	@Deprecated
	public IndexController() {
		this(null);
	}
	
	@Inject
	public IndexController(final Result result) {
		super();
		this.result = result;
	}

	@Get
	public void index(){
		result.forwardTo("index.html");
	}
	
}
