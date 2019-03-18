package br.eti.clairton.vraptor.crud.controller;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;

import org.junit.Test;

import br.com.caelum.vraptor.core.ReflectionProvider;
import br.com.caelum.vraptor.http.route.Router;
import br.eti.clairton.inflector.Inflector;

public class PluralRoutesParserTest {

	@Test
	public void testGetUrisMustEmpty() throws Exception {
		final Inflector i = mock(Inflector.class);
		final Router r = mock(Router.class);
		final ReflectionProvider rp = mock(ReflectionProvider.class);
		final PluralRoutesParser p = new PluralRoutesParser(r, rp, i);
		final Class<?> type = RetrieveController.class;
		final Method method = type.getDeclaredMethod("getResourceName");
		assertFalse(p.isEligible(method));
	}
}
