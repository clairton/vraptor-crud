package br.eti.clairton.vraptor.crud.hypermedia;

import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.gson.GsonSerializerBuilder;
import br.eti.clairton.vraptor.crud.annotation.Operation;
import br.eti.clairton.vraptor.crud.annotation.Resource;
import br.eti.clairton.vraptor.hypermedia.GsonHypermediaJSONSerialization;
import br.eti.clairton.vraptor.hypermedia.HypermediaJsonSerialization;
import br.eti.clairton.vraptor.hypermedia.HypermediableRole;

@Specializes
public class GsonHypermediaJsonSerialization extends
		GsonHypermediaJSONSerialization implements HypermediaJsonSerialization {
	private final String operation;
	private final String resource;

	@Deprecated
	protected GsonHypermediaJsonSerialization() {
		this(null, null, null, null, null, null, null);
	}

	@Inject
	public GsonHypermediaJsonSerialization(HttpServletResponse response,
			TypeNameExtractor extractor, HypermediableRole navigator,
			GsonSerializerBuilder builder, Environment environment,
			@Resource final String resource, @Operation final String operation) {
		super(response, extractor, navigator, builder, environment);
		this.operation = operation;
		this.resource = resource;
	}

	@Override
	public <T> Serializer from(T object, String alias) {
		resource(resource).operation(operation);
		return super.from(object, alias);
	}
}
