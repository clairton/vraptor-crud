package br.eti.clairton.vraptor.crud.serializer;

import java.lang.reflect.Type;

import br.com.caelum.vraptor.serialization.gson.RegisterStrategy;
import br.com.caelum.vraptor.serialization.gson.RegisterType;
import br.eti.clairton.jpa.serializer.JpaSerializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

@RegisterStrategy(RegisterType.SINGLE)
public class OutroModelSerialiazer extends JpaSerializer<OutroModel> {
	
	public OutroModelSerialiazer() {
		addIgnoredField("nome");
	}
	
	@Override
	public JsonElement serialize(final OutroModel src, final Type type, final JsonSerializationContext context) {
		return super.serialize(src, type, context);
	}
}