package br.eti.clairton.vraptor.crud.serializer;

import static br.eti.clairton.inflector.Inflector.getForLocale;
import static br.eti.clairton.inflector.Locale.pt_BR;

import java.lang.reflect.Type;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.model.Base;

/**
 * Serializa os objetos da de forma a integrar com o modo
 * ActiveSerializerSerializer do ember.
 *
 * @author Clairton Rodrigo Heinzen clairton.rodrigo@gmail.com
 */
@Default
@TagableTo(Base.class)
public class ModelSerializer extends AbstractModelSerializer<Base> implements JsonSerializer<Base>, JsonDeserializer<Base> {
	private static final long serialVersionUID = 1L;

	@Inject
	public ModelSerializer(@NotNull final Inflector inflector, @NotNull EntityManager em) {
		super(inflector, em);
	}

	public ModelSerializer() {
		this(getForLocale(pt_BR), null);
	}
	
	@Override
	public JsonElement serialize(final Base src, final Type type, final JsonSerializationContext context) {
		return super.serialize(src, type, context);
	}
	
	@Override
	public Base deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context) throws JsonParseException {
		return super.deserialize(json, type, context);
	}
}
