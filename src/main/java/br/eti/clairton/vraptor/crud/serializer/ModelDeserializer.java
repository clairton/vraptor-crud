package br.eti.clairton.vraptor.crud.serializer;

import java.lang.reflect.Type;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import net.vidageek.mirror.dsl.Mirror;

import org.apache.logging.log4j.Logger;

import br.eti.clairton.jpa.serializer.JpaDeserializer;
import br.eti.clairton.repository.Model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * Serializa os objetos da de forma a integrar com o modo ActiveSerializer.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Dependent
public class ModelDeserializer extends JpaDeserializer<Model> implements
		JsonDeserializer<Model> {

	@Deprecated
	protected ModelDeserializer() {
		this(null, null, null);
	}

	@Inject
	public ModelDeserializer(@NotNull final EntityManager entityManager,
			@NotNull final Mirror mirror, @NotNull final Logger logger) {
		super(entityManager, mirror, logger);
	}

	@Override
	public Model deserialize(final JsonElement element, final Type type,
			final JsonDeserializationContext context) throws JsonParseException {
		return super.deserialize(element, type, context);
	}
}
