package br.eti.clairton.vraptor.crud.serializer;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import net.vidageek.mirror.dsl.Mirror;

import org.apache.logging.log4j.Logger;

import br.eti.clairton.repository.Model;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public class Producer {

	@Produces
	public JsonDeserializer<Model> deserializer(
			final @NotNull EntityManager entityManager,
			final @NotNull Mirror mirror, final @NotNull Logger logger) {
		return new ModelDeserializer(entityManager, mirror, logger);
	}

	@Produces
	public JsonSerializer<Model> serializer(final @NotNull Mirror mirror,
			final @NotNull Logger logger) {
		return new ModelSerializer(mirror, logger);
	}
}
