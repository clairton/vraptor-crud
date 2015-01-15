package br.eti.clairton.vraptor.crud;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;

@Controller
@Path(value = "", priority = 1)
public class IndexController {

	@Get("/")
	public void index() {
	}

	@Get("/{file}.js")
	public void js(final String file) {
	}

	@Get("/{file}.html")
	public void html(final String file) {
	}

	@Get("assets/{file}")
	public void assets(final String file) {
	}

	@Get("fonts/{file}")
	public void fonts(final String file) {
	}
}
