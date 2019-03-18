package br.eti.clairton.vraptor.crud.serializer;

import javax.persistence.EntityManager;

import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.jpa.serializer.NodesProgramatic;
import br.eti.clairton.model.Base;

@Deprecated
public class AbstractModelSerializer<T extends Base> extends AbstractBaseSerializer<T>{
	private static final long serialVersionUID = -4143443415186586243L;

	public AbstractModelSerializer(final Inflector inflector, final EntityManager em) {
		super(new NodesProgramatic(), inflector, em);
	}
}
