package br.eti.clairton.vraptor.crud.serializer;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.model.Model;

@Deprecated
public class ModelSerializer extends AbstractBaseSerializer<Model> {
	private static final long serialVersionUID = 3309903637065249719L;
	
	@Deprecated
	public ModelSerializer() {
		this(null, null);
	}

	@Inject
	public ModelSerializer(final Inflector inflector, final EntityManager em) {
		super(inflector, em);
	}
}
