package br.eti.clairton.vraptor.crud;

import static org.junit.Assert.*;

import org.junit.Test;

import br.eti.clairton.repository.Model;
import br.eti.clairton.vraptor.crud.security.Resourceable;

public class ResourceableTest {

	@Test
	public void testGetResourceName() {
		final String resourceName = "aplicacao";
		final Class<? extends Model> resource = Aplicacao.class;
		final Resourceable resourceable = new Resourceable(resource) {
		};
		assertEquals(resourceName, resourceable.getResourceName());
	}

}
