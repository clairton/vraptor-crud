package br.eti.clairton.vraptor.crud;

import javax.inject.Inject;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Result;

@Controller
@Path(value = "", priority = 1)
public class IndexController {
	private final Result result;

	@Deprecated
	protected IndexController() {
		this(null);
	}

	@Inject
	public IndexController(Result result) {
		this.result = result;
	}

	@Get
	public void index() {
		result.redirectTo("/index.html");
	}
}
