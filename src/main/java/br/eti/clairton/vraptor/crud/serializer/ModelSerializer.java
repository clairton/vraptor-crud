package br.eti.clairton.vraptor.crud.serializer;

import java.lang.reflect.Type;

import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import net.vidageek.mirror.dsl.Mirror;

import org.apache.logging.log4j.LogManager;
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
@Vetoed
public class ModelSerializer implements JsonSerializer<Model> {
	private final Logger logger = LogManager.getLogger(JpaSerializer.class);
	private final JpaSerializer<Model> jpaSerializer;

	@Inject
	public ModelSerializer(final Mirror mirror) {
		jpaSerializer = new JpaSerializer<Model>(mirror, logger) {
		};
	}

	public void addIgnoredField(@NotNull final String field) {
		jpaSerializer.addIgnoredField(field);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public JsonElement serialize(final Model src, final Type type,
			final JsonSerializationContext context) {
		return jpaSerializer.serialize(src, type, context);
	}
}
