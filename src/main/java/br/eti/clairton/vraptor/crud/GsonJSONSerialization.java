package br.eti.clairton.vraptor.crud;

import java.io.IOException;

import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.core.ReflectionProvider;
import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.serialization.gson.GsonSerializerBuilder;
import br.com.caelum.vraptor.view.ResultException;
import br.eti.clairton.vraptor.crud.serializer.TagableExtractor;

@Specializes
public class GsonJSONSerialization extends br.com.caelum.vraptor.serialization.gson.GsonJSONSerialization {
	private final TypeNameExtractor extractor;
	private final GsonSerializerBuilder builder;
	private final TagableExtractor tagableExtractor;
	private final HttpServletResponse response;
	private final ReflectionProvider reflectionProvider;

	@Deprecated
	public GsonJSONSerialization() {
		this(null, null, null, null, null, null);
	}

	@Inject
	public GsonJSONSerialization(
			final HttpServletResponse response, 
			final TypeNameExtractor extractor, 
			final GsonSerializerBuilder builder, 
			final Environment environment, 
			final TagableExtractor tagableExtractor,
			final ReflectionProvider reflectionProvider) {
		super(response, extractor, builder, environment, reflectionProvider);
		this.extractor = extractor;
		this.builder = builder;
		this.tagableExtractor = tagableExtractor;
		this.response = response;
		this.reflectionProvider = reflectionProvider;
	}

	@Override
	protected SerializerBuilder getSerializer() {
		try {
			return new GsonSerializer(builder, response.getWriter(), extractor, tagableExtractor, reflectionProvider);
		} catch (IOException e) {
			throw new ResultException("Unable to serialize data", e);
		}
	}

	@Override
	public <T> Serializer from(final T object) {
		final String alias = tagableExtractor.extract(object);
		return from(object, alias);
	}
}
