package br.eti.clairton.vraptor.crud.serializer;

import java.lang.reflect.Type;

import javax.enterprise.inject.Vetoed;
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
@Vetoed
public class ModelSerializer implements JsonSerializer<Model> {
	private final JpaSerializer<Model> delegate;

	@Deprecated
	protected ModelSerializer() {
		this(null, null);
	}

	@Inject
	public ModelSerializer(@NotNull final Mirror mirror,
			@NotNull final Logger logger) {
		delegate = new JpaSerializer<Model>(mirror, logger) {
		};
	}

	public void addIgnoredField(@NotNull final String field) {
		delegate.addIgnoredField(field);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public JsonElement serialize(final Model src, final Type type,
			final JsonSerializationContext context) {
		return delegate.serialize(src, type, context);
	}
}
