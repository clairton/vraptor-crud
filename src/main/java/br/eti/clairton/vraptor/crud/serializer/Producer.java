package br.eti.clairton.vraptor.crud.serializer;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import net.vidageek.mirror.dsl.Mirror;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.eti.clairton.repository.Model;
import br.eti.clairton.vraptor.hypermedia.HypermediableRule;
import br.eti.clairton.vraptor.hypermedia.Operation;
import br.eti.clairton.vraptor.hypermedia.Resource;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public class Producer {
	private final Logger logger = LogManager.getLogger(ModelSerializer.class);

	@Produces
	public JsonDeserializer<Model> deserializer(
			final @NotNull EntityManager entityManager,
			final @NotNull Mirror mirror) {
		return new ModelDeserializer(entityManager, mirror, logger);
	}

	@Produces
	public JsonSerializer<Model> serializer(final @NotNull Mirror mirror,
			final HypermediableRule navigator,
			final @Operation String operation, final @Resource String resource) {
		return new ModelSerializer(mirror, logger, navigator, operation,
				resource);
	}
}
