package br.eti.clairton.vraptor.crud.serializer;

import static br.eti.clairton.inflector.Inflector.getForLocale;
import static br.eti.clairton.inflector.Locale.pt_BR;

import java.util.Collection;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import com.google.gson.JsonSerializer;

import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.jpa.serializer.JpaSerializer;
import br.eti.clairton.repository.Model;

/**
 * Deserializa os objetos da de forma a integrar com o modo ActiveSerializer.
 *
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
//@TagableTo(Model.class)
public class ModelSerializer extends JpaSerializer<Model>implements JsonSerializer<Model> {
	private final br.eti.clairton.jpa.serializer.Tagable<Model> tagable;

	@Inject
	public ModelSerializer(@NotNull final Inflector inflector) {
		this.tagable = new Tagable<>(inflector);
	}

	public ModelSerializer() {
		this(getForLocale(pt_BR));
	}

	@Override
	public String getRootTagCollection(final Collection<Model> collection) {
		return tagable.getRootTagCollection(collection);
	}
}
