package br.eti.clairton.vraptor.crud.serializer;

import static br.eti.clairton.inflector.Inflector.getForLocale;
import static br.eti.clairton.inflector.Locale.pt_BR;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.jpa.serializer.GsonJpaSerializer;
import br.eti.clairton.repository.Model;

public abstract class AbstractModelSerializer<T extends Model> extends GsonJpaSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T>  {
	private static final long serialVersionUID = 1L;
	private final br.eti.clairton.jpa.serializer.Tagable<T> tagable;

	public AbstractModelSerializer(@NotNull final Inflector inflector, @NotNull EntityManager em) {
		super(em);
		this.tagable = new Tagable<T>(inflector){
			private static final long serialVersionUID = 1L;

			@Override
			public String getRootTag(final T src) {
				return AbstractModelSerializer.this.getRootTag(src);
			}
		};
	}

	public AbstractModelSerializer() {
		this(getForLocale(pt_BR), null);
	}

	@Override
	public String getRootTagCollection(final Collection<T> collection) {
		return tagable.getRootTagCollection(collection);
	}
}