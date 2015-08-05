package br.eti.clairton.vraptor.crud.serializer;

import static br.eti.clairton.inflector.Inflector.getForLocale;
import static br.eti.clairton.inflector.Locale.pt_BR;

import java.util.Collection;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import com.google.gson.JsonDeserializer;

import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.jpa.serializer.JpaDeserializer;
import br.eti.clairton.repository.Model;

/**
 * Serializa os objetos da de forma a integrar com o modo ActiveSerializer.
 *
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
public class ModelDeserializer extends JpaDeserializer<Model>implements JsonDeserializer<Model> {
	private final br.eti.clairton.jpa.serializer.Tagable<Model> tagable;

	@Deprecated
	public ModelDeserializer() {
		this(null, getForLocale(pt_BR));
	}

	@Inject
	public ModelDeserializer(@NotNull final EntityManager entityManager, @NotNull final Inflector inflector) {
		super(entityManager);
		this.tagable = new Tagable<>(inflector);
	}

	@Override
	public String getRootTagCollection(final Collection<Model> collection) {
		return tagable.getRootTagCollection(collection);
	}
}
