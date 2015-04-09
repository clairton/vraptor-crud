package br.eti.clairton.vraptor.crud.hypermedia;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.UrlToControllerTranslator;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.security.Extractor;
import br.eti.clairton.vraptor.crud.annotation.Current;
import br.eti.clairton.vraptor.hypermedia.Operation;
import br.eti.clairton.vraptor.hypermedia.Resource;

@Specializes
public class CurrentMethodProducer extends
		br.eti.clairton.vraptor.hypermedia.CurrentMethodProducer {
	private final ControllerMethod method;
	private final String resource;
	private final String operation;

	@Inject
	public CurrentMethodProducer(final UrlToControllerTranslator translator,
			final MutableRequest request, final Extractor extractor,
			final Inflector inflector) {
		super(translator, request);
		this.method = translator.translate(request);
		this.resource = getResource(request.getRequestedUri(), inflector);
		this.operation = getOperation(method, extractor);
	}

	@Produces
	@Current
	public ControllerMethod getControllerMethod() {
		return method;
	}

	@Produces
	@Resource
	public String getResource() {
		return resource;
	}

	@Produces
	@Operation
	public String getOperation() {
		return operation;
	}

	private String getOperation(ControllerMethod method,
			final Extractor extractor) {
		return extractor.getOperation(method.getMethod());
	}

	public static String getResource(final String uri, final Inflector inflector) {
		final String withouQuery = uri.split("\\?")[0];
		final String[] splitSlash = withouQuery.split("/");
		final String withoutSlash = splitSlash[splitSlash.length >= 2 ? 1 : 0];
		return inflector.singularize(withoutSlash);
	}
}
