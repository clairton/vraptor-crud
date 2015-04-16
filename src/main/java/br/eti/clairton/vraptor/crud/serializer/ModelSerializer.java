package br.eti.clairton.vraptor.crud.serializer;

import java.lang.reflect.Type;

import javax.enterprise.inject.Vetoed;
import javax.validation.constraints.NotNull;

import net.vidageek.mirror.dsl.Mirror;

import org.apache.logging.log4j.Logger;

import br.eti.clairton.jpa.serializer.JpaSerializer;
import br.eti.clairton.repository.Model;
import br.eti.clairton.vraptor.hypermedia.Hypermediable;
import br.eti.clairton.vraptor.hypermedia.HypermediableRule;
import br.eti.clairton.vraptor.hypermedia.HypermediableSerializer;

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
	private final JpaSerializer<Model> jpaSerializer;
	private final HypermediableSerializer hypermediaSerializer;

	public ModelSerializer(final Mirror mirror, final Logger logger,
			final HypermediableRule navigator, final String operation,
			final String resource) {
		jpaSerializer = new JpaSerializer<Model>(mirror, logger) {};
		hypermediaSerializer = new HypermediableSerializer(navigator, operation, resource){};
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
		if (Hypermediable.class.isInstance(src)) {
			return hypermediaSerializer.serialize((Hypermediable) src, type,
					context);
		} else {
			return jpaSerializer.serialize(src, type, context);
		}
	}
}
