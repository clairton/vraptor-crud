package br.eti.clairton.vraptor.crud;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;

/***
 * Serve para conseguir recuperar os arquivos estaticos.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 *
 */
@Controller
@Path(value = "", priority = 1)
public class StaticFilesController {
	private Logger logger;
	
	@Deprecated
	protected StaticFilesController() {}
	
	@Inject
	public StaticFilesController(final @Default Logger logger) {
		super();
		this.logger = logger;
	}

	@Get("/")
	public void index() {
		logger.debug("Requisitando root path");
	}

	@Get("/{file}.js")
	public void js(final String file) {
		logger.debug("Requisitando arquivo {}", file);
	}

	@Get("/{file}.html")
	public void html(final String file) {
		logger.debug("Requisitando arquivo {}", file);
	}

	@Get("assets/{file}")
	public void assets(final String file) {
		logger.debug("Requisitando arquivo {}", file);
	}

	@Get("fonts/{file}")
	public void fonts(final String file) {
		logger.debug("Requisitando arquivo {}", file);
	}
}
