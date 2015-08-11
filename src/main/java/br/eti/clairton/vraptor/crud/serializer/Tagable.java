package br.eti.clairton.vraptor.crud.serializer;

import java.util.Collection;

import br.eti.clairton.inflector.Inflector;

public abstract class Tagable<T> extends br.eti.clairton.jpa.serializer.Tagable<T> implements Resourceable{
	private static final long serialVersionUID = 1L;
	private final Inflector inflector;

	public Tagable(final Inflector inflector) {
		this.inflector = inflector;
	}
	
	@Override
	public String pluralize(final String tag){
		return inflector.pluralize(tag);
	}
	
	@Override
	public String getRootTag(final T src) {
		if(src == null){
			return getResource();
		}
		return super.getRootTag(src);
	}
	
	@Override
	public String getRootTagCollection(final Collection<T> collection) {
		if(collection == null || collection.isEmpty()){
			return pluralize(getResource());
		}
		return super.getRootTagCollection(collection);
	}
}
