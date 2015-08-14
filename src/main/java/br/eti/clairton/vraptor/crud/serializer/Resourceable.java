package br.eti.clairton.vraptor.crud.serializer;

import java.util.Collection;

public interface Resourceable<T> {
	
	String getResource(final T src);
	
	String getResource(final Collection<T> src);
}
