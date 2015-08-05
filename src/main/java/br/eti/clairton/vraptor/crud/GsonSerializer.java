package br.eti.clairton.vraptor.crud;

import java.io.Writer;

import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.gson.GsonSerializerBuilder;
import br.eti.clairton.vraptor.crud.serializer.TagableExtractor;

@Vetoed
public class GsonSerializer extends br.com.caelum.vraptor.serialization.gson.GsonSerializer {
	private final TagableExtractor tagableExtractor;

	@Inject
	public GsonSerializer(final GsonSerializerBuilder builder, final Writer writer, final TypeNameExtractor extractor, final TagableExtractor tagableExtractor) {
		super(builder, writer, extractor);
		this.tagableExtractor = tagableExtractor;
	}

	@Override
	public <T> Serializer from(final T object) {
		final String alias = tagableExtractor.extract(object);
		return super.from(object, alias);
	}
}
