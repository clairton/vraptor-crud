package br.eti.clairton.vraptor.crud.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.Metamodel;

import net.vidageek.mirror.list.dsl.Matcher;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.eti.clairton.vraptor.crud.model.Aplicacao;

@RunWith(CdiTestRunner.class)
public class JpaMatcherTest {
	private Matcher<Field> matcher;
	private @Inject EntityManager em;
	
	@Before
	public void init(){
		final Metamodel meta = em.getMetamodel();
		matcher = new JpaMatcher(meta);
	}
	
	@Test
	public void testAccepts() throws Exception {
		assertTrue(matcher.accepts(Aplicacao.class.getDeclaredField("nome")));
	}
	
	@Test
	public void testNotAcceptsOneToMany() throws Exception {
		assertFalse(matcher.accepts(Aplicacao.class.getDeclaredField("recursos")));
	}
	
	@Test
	public void testNotAcceptsTransientField() throws Exception {
		assertFalse(matcher.accepts(Aplicacao.class.getDeclaredField("serialVersionUID")));
	}
	
	@Test
	public void testNotAcceptsUnManaged() throws Exception {
		assertFalse(matcher.accepts(JpaMatcherTest.class.getDeclaredField("matcher")));
	}
}
