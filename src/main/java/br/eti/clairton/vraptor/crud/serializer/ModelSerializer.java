package br.eti.clairton.vraptor.crud.serializer;

import java.lang.reflect.Type;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import net.vidageek.mirror.dsl.Mirror;

import org.apache.logging.log4j.Logger;

import br.eti.clairton.jpa.serializer.JpaSerializer;
import br.eti.clairton.repository.Model;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Deserializa os objetos da de forma a integrar com o modo ActiveSerializer.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Dependent
public class ModelSerializer extends JpaSerializer<Model> implements
		JsonSerializer<Model> {

	@Inject
	public ModelSerializer(@NotNull final Mirror mirror,
			@NotNull final Logger logger) {
		super(mirror, logger);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public JsonElement serialize(final Model src, final Type type,
			final JsonSerializationContext context) {
		return super.serialize(src, type, context);
	}
}
