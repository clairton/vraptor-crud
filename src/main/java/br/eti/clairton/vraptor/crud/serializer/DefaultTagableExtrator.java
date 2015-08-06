package br.eti.clairton.vraptor.crud.serializer;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.eti.clairton.jpa.serializer.Tagable;
import br.eti.clairton.repository.Model;

public class DefaultTagableExtrator implements TagableExtractor {
	private final Instance<br.eti.clairton.jpa.serializer.Tagable<?>> instances;

	@Inject
	public DefaultTagableExtrator(final @Any Instance<br.eti.clairton.jpa.serializer.Tagable<?>> instances) {
		super();
		this.instances = instances;
	}

	@Override
	public String extract(final Object object) {
		final Annotation qualifier = getType(object.getClass());
		final Instance<br.eti.clairton.jpa.serializer.Tagable<?>> instance = instances.select(qualifier);
		final br.eti.clairton.jpa.serializer.Tagable<?> tagable;
		if (!instance.isUnsatisfied()) {
			tagable = instances.select(qualifier).get();
		} else {
			tagable = instances.select(getType(Model.class)).get();
		}
		@SuppressWarnings("unchecked")
		final br.eti.clairton.jpa.serializer.Tagable<Object> tagableTyped = (Tagable<Object>) tagable;
		final String alias;
		if (Collection.class.isInstance(object)) {
			@SuppressWarnings("unchecked")
			final Collection<Object> collection = (Collection<Object>) object;
			alias = tagableTyped.getRootTagCollection(collection);
		} else {
			alias = tagableTyped.getRootTag(object);
		}
		return alias;
	}

	private <T> Annotation getType(final Class<T> type) {
		return new TagableTo() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return TagableTo.class;
			}

			@Override
			public Class<?> value() {
				return type;
			}
		};
	}
}