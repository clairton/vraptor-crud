package br.eti.clairton.vraptor.crud;

import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.view.DefaultPathResolver;

@Specializes
public class SinglePagePathResolver extends DefaultPathResolver {
	@Inject
	public SinglePagePathResolver(final FormatResolver resolver) {
		super(resolver);
	}

	@Override
	public String pathFor(ControllerMethod method) {
		return "index.html";
	}
}
