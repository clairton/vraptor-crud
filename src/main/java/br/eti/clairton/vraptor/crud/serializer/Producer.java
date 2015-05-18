package br.eti.clairton.vraptor.crud.serializer;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import net.vidageek.mirror.dsl.Mirror;
import br.eti.clairton.repository.Model;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public class Producer {

	@Produces
	public JsonDeserializer<Model> deserializer(
			final @NotNull EntityManager entityManager,
			final @NotNull Mirror mirror) {
		return new ModelDeserializer(entityManager, mirror);
	}

	@Produces
	public JsonSerializer<Model> serializer(final @NotNull Mirror mirror) {
		return new ModelSerializer(mirror);
	}
}
