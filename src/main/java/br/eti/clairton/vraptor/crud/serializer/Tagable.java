package br.eti.clairton.vraptor.crud.serializer;

import java.util.Collection;

import br.eti.clairton.inflector.Inflector;

public class Tagable<T> extends br.eti.clairton.jpa.serializer.Tagable<T> {
	private final Inflector inflector;

	public Tagable(final Inflector inflector) {
		this.inflector = inflector;
	}

	@Override
	public String getRootTagCollection(final Collection<T> collection) {
		final T src = getFirst(collection);
		final String tag = getRootTag(src);
		final String collectionTag = inflector.pluralize(tag);
		return collectionTag;
	}
}
