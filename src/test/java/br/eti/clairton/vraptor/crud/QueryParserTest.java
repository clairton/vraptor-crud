package br.eti.clairton.vraptor.crud;

import static br.eti.clairton.vraptor.crud.VRaptorRunner.navigate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

import br.eti.clairton.repository.Comparators;
import br.eti.clairton.repository.Predicate;

@RunWith(VRaptorRunner.class)
public class QueryParserTest {
	private @Inject QueryParser queryParser;

	private MockHttpServletRequest request = new MockHttpServletRequest();

	@Before
	public void init() {
		// para registrar os converters
		navigate().get("/").execute();
	}

	@Test
	public void testParseArray() {
		// nome[]=remove&nome[]=update
		final String[] values = new String[] { "remove", "update" };
		request.addParameter("nome", values);
		final Collection<Predicate> predicates = queryParser.parse(request,
				Recurso.class);
		assertEquals(1, predicates.size());
		final Iterator<Predicate> interator = predicates.iterator();
		final Predicate predicateNome = interator.next();
		assertEquals(Arrays.asList(values), predicateNome.getValue());
		assertEquals("*", predicateNome.getComparator().toString());
		assertEquals("nome", predicateNome.getAttribute().getName());
	}

	@Test
	public void testParseComplex() {
		request.addParameter("aplicacao.nome", "=*Pass");
		request.addParameter("aplicacao.id", ">=0");
		final Collection<Predicate> predicates = queryParser.parse(request,
				Recurso.class);
		assertEquals(2, predicates.size());
		final Iterator<Predicate> interator = predicates.iterator();
		final Predicate predicateNome = interator.next();
		assertEquals("=*", predicateNome.getComparator().toString());
		assertEquals("Pass", predicateNome.getValue());
		assertTrue(Recurso_.aplicacao.equals(predicateNome.getAttributes()[0]));
		assertTrue(Aplicacao_.nome.equals(predicateNome.getAttributes()[1]));
		final Predicate predicateId = interator.next();
		assertEquals(Long.valueOf(0), predicateId.getValue());
		assertEquals(">=", predicateId.getComparator().toString());
		assertEquals("aplicacao", predicateId.getAttributes()[0].getName());
		assertEquals("id", predicateId.getAttributes()[1].getName());
	}

	@Test
	public void testParseSimple() {
		// nome=Pass&id=>=0
		request.addParameter("nome", "=*Pass");
		request.addParameter("id", ">=0");
		final Collection<Predicate> predicates = queryParser.parse(request,
				Aplicacao.class);
		assertEquals(2, predicates.size());
		final Iterator<Predicate> interator = predicates.iterator();
		final Predicate predicateNome = interator.next();
		assertEquals("Pass", predicateNome.getValue());
		assertEquals("=*", predicateNome.getComparator().toString());
		assertTrue(Aplicacao_.nome.equals(predicateNome.getAttribute()));
		final Predicate predicateId = interator.next();
		assertEquals(Long.valueOf(0), predicateId.getValue());
		assertEquals(">=", predicateId.getComparator().toString());
		assertEquals("id", predicateId.getAttribute().getName());
	}

	@Test
	public void testIgual() {
		assertEquals(Comparators.EQUAL,
				queryParser.to(new String[] { "abc" }).comparator);
	}

	@Test
	public void testIgualComSimbolo() {
		assertEquals(Comparators.EQUAL,
				queryParser.to(new String[] { "==abc" }).comparator);
	}

	@Test
	public void testIgualNaoSensitive() {
		assertEquals(Comparators.EQUAL_IGNORE_CASE,
				queryParser.to(new String[] { "=*abc" }).comparator);
	}

	@Test
	public void testDiferente() {
		assertEquals(Comparators.NOT_EQUAL,
				queryParser.to(new String[] { "<>abc" }).comparator);
	}

	@Test
	public void testExiste() {
		assertEquals(Comparators.EXIST,
				queryParser.to(new String[] { "∃" }).comparator);
	}

	@Test
	public void testNaoNulo() {
		assertEquals(Comparators.NOT_NULL,
				queryParser.to(new String[] { "!∅" }).comparator);
	}

	@Test
	public void testNulo() {
		assertEquals(Comparators.NULL,
				queryParser.to(new String[] { "∅" }).comparator);
	}

	@Test
	public void testMaior() {
		assertEquals(Comparators.GREATER_THAN,
				queryParser.to(new String[] { ">45" }).comparator);
	}

	@Test
	public void testMaiorOuIgual() {
		assertEquals(Comparators.GREATER_THAN_OR_EQUAL,
				queryParser.to(new String[] { ">=45" }).comparator);
	}

	@Test
	public void testMenorOuIgual() {
		assertEquals(Comparators.LESS_THAN_OR_EQUAL,
				queryParser.to(new String[] { "<=45" }).comparator);
	}

	@Test
	public void testMenor() {
		assertEquals(Comparators.LESS_THAN,
				queryParser.to(new String[] { "<45" }).comparator);
	}
}