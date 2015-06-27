package br.eti.clairton.vraptor.crud.serializer;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import br.eti.clairton.jpa.serializer.JpaDeserializer;
import br.eti.clairton.repository.Model;

import com.google.gson.JsonDeserializer;

/**
 * Serializa os objetos da de forma a integrar com o modo ActiveSerializer.
 *
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
public class ModelDeserializer extends JpaDeserializer<Model> implements JsonDeserializer<Model> {

	@Deprecated
	public ModelDeserializer() {
		this(null);
	}

	@Inject
	public ModelDeserializer(@NotNull final EntityManager entityManager) {
		super(entityManager);
	}
}
