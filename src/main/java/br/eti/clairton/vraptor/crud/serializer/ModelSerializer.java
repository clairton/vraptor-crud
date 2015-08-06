package br.eti.clairton.vraptor.crud.serializer;

import static br.eti.clairton.inflector.Inflector.getForLocale;
import static br.eti.clairton.inflector.Locale.pt_BR;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.repository.Model;

/**
 * Serializa os objetos da de forma a integrar com o modo
 * ActiveSerializerSerializer do ember.
 *
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Default
@TagableTo(Model.class)
public class ModelSerializer extends AbstractModelSerializer<Model> implements JsonSerializer<Model>, JsonDeserializer<Model> {
	private static final long serialVersionUID = 1L;

	@Inject
	public ModelSerializer(@NotNull final Inflector inflector, @NotNull EntityManager em) {
		super(inflector, em);
	}

	public ModelSerializer() {
		this(getForLocale(pt_BR), null);
	}
}
