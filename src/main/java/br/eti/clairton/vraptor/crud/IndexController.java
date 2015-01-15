package br.eti.clairton.vraptor.crud;

import static java.lang.String.format;

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

	@Get("assets/{file}")
	public void assets(final String file) {
		result.redirectTo(format("/assets/%s", file));
	}

	@Get("fonts/{file}")
	public void fonts(final String file) {
		result.redirectTo(format("/fonts/%s", file));
	}
}
