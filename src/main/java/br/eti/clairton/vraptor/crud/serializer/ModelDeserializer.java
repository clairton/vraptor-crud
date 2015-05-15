package br.eti.clairton.vraptor.crud.serializer;

import java.lang.reflect.Type;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import net.vidageek.mirror.dsl.Mirror;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.eti.clairton.jpa.serializer.JpaDeserializer;
import br.eti.clairton.jpa.serializer.JpaSerializer;
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
public class ModelDeserializer implements JsonDeserializer<Model> {
	private final JpaDeserializer<Model> delegate;
	private final Logger logger = LogManager.getLogger(JpaSerializer.class);

	@Deprecated
	protected ModelDeserializer() {
		this(null, null);
	}

	@Inject
	public ModelDeserializer(@NotNull final EntityManager entityManager,
			@NotNull final Mirror mirror) {
		delegate = new JpaDeserializer<Model>(entityManager, mirror, logger) {
		};
	}

	@Override
	public Model deserialize(final JsonElement element, final Type type,
			final JsonDeserializationContext context) throws JsonParseException {
		return delegate.deserialize(element, type, context);
	}
}
