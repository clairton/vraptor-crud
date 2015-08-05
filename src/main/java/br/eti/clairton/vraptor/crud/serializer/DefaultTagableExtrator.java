package br.eti.clairton.vraptor.crud.serializer;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.eti.clairton.jpa.serializer.model.Model;

public class DefaultTagableExtrator implements TagableExtractor {
	private final Instance<br.eti.clairton.jpa.serializer.Tagable<?>> instances;

	@Inject
	public DefaultTagableExtrator(@Any Instance<br.eti.clairton.jpa.serializer.Tagable<?>> instances) {
		super();
		this.instances = instances;
	}

	@Override
	public <T> String extract(final T object) {
		final Annotation qualifier = getType(object);
		final Instance<br.eti.clairton.jpa.serializer.Tagable<?>> instance = instances.select(qualifier);
		final Tagable<T> tagable;
		if (!instance.isUnsatisfied()) {
			@SuppressWarnings("unchecked")
			tagable = (Tagable<T>) instances.select(qualifier).get();
		} else {
			@SuppressWarnings("unchecked")
			tagable = (Tagable<T>) instances.select(getType(Model.class)).get();
		}
		final String alias;
		if (Collection.class.isInstance(object)) {
			@SuppressWarnings("unchecked")
			final Collection<T> collection = (Collection<T>) object;
			alias = tagable.getRootTagCollection(collection);
		} else {
			alias = tagable.getRootTag(object);
		}
		return alias;
	}

	private <T> Annotation getType(final T object) {
		return new TagableTo() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return TagableTo.class;
			}

			@Override
			public Class<?> value() {
				return object.getClass();
			}
		};
	}
}