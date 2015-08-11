package br.eti.clairton.vraptor.crud.serializer;

import static br.eti.clairton.inflector.Inflector.getForLocale;
import static br.eti.clairton.inflector.Locale.pt_BR;
import static javax.enterprise.inject.spi.CDI.current;

import java.lang.reflect.Constructor;
import java.util.Collection;

import javax.enterprise.inject.Instance;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.jpa.serializer.GsonJpaSerializer;
import br.eti.clairton.repository.Model;
import br.eti.clairton.vraptor.crud.controller.CrudController;

public abstract class AbstractModelSerializer<T extends Model> extends GsonJpaSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T>, Resourceable {
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

			@Override
			public String getResource() {
				return  AbstractModelSerializer.this.getResource();
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
	
	@Override
	public String getResource() {
		return getResourceController(getControllerMethod());
	}
	

	final private ControllerMethod getControllerMethod() {
		final Class<ControllerMethod> type = ControllerMethod.class;
		final Instance<ControllerMethod> instance = current().select(type);
		final ControllerMethod controllerMethod = instance.get();
		return controllerMethod;
	}

	final private String getResourceController(final ControllerMethod cm) {
		try {
			final Class<?> type = cm.getController().getType();
			final Constructor<?> constructor = type.getDeclaredConstructor();
			final Boolean accessible = constructor.isAccessible();
			constructor.setAccessible(Boolean.TRUE);
			final CrudController<?> i = (CrudController<?>) constructor.newInstance();
			constructor.setAccessible(accessible);
			return i.getResourceName();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
}