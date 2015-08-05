package br.eti.clairton.vraptor.crud.serializer;

public interface TagableExtractor {

	<T> String extract(T object);
}
