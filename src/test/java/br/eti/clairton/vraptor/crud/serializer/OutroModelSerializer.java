package br.eti.clairton.vraptor.crud.serializer;

import java.lang.reflect.Type;

import javax.inject.Inject;

import br.eti.clairton.repository.Model;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class OutroModelSerializer implements JsonSerializer<OutroModel> {
	private final JsonSerializer<Model> serializer;

	@Inject
	public OutroModelSerializer(final ModelSerializer serializer) {
		serializer.addIgnoredField("nome");
		this.serializer = serializer;
	}

	@Override
	public JsonElement serialize(final OutroModel src, final Type type, final JsonSerializationContext context) {
		return serializer.serialize(src, type, context);
	}
}