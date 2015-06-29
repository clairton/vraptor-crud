package br.eti.clairton.vraptor.crud.serializer;

import java.lang.reflect.Type;

import javax.inject.Inject;

import br.com.caelum.vraptor.serialization.gson.RegisterStrategy;
import br.com.caelum.vraptor.serialization.gson.RegisterType;
import br.eti.clairton.jpa.serializer.Mode;
import br.eti.clairton.repository.Model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;


@RegisterStrategy(RegisterType.SINGLE)
public class OutroModelDeserializer implements JsonDeserializer<OutroModel> {
	private final JsonDeserializer<Model> delegate;

	@Inject
	public OutroModelDeserializer(final ModelDeserializer delegate) {
		delegate.nodes().put("recursos", Mode.RECORD);
		this.delegate = delegate;
	}

	@Override
	public OutroModel deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context) throws JsonParseException {
		return (OutroModel) delegate.deserialize(json, type, context);
	}
}