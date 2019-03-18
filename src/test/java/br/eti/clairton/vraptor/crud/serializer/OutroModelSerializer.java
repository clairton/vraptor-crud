package br.eti.clairton.vraptor.crud.serializer;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import br.com.caelum.vraptor.serialization.gson.RegisterStrategy;
import br.com.caelum.vraptor.serialization.gson.RegisterType;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.jpa.serializer.Mode;
import br.eti.clairton.vraptor.crud.model.OutroModel;

@RegisterStrategy(RegisterType.SINGLE)
public class OutroModelSerializer extends AbstractBaseSerializer<OutroModel> implements JsonSerializer<OutroModel>, JsonDeserializer<OutroModel> {
	private static final long serialVersionUID = 1L;

	@Inject
	public OutroModelSerializer(final Inflector inflector, final EntityManager em) {
		super(inflector, em);
		ignore("nome");
		nodes().put("recursos", Mode.RECORD);
	}
}