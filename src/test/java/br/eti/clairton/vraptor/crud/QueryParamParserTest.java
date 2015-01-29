package br.eti.clairton.vraptor.crud;

import static br.eti.clairton.vraptor.crud.VRaptorRunner.navigate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

import br.eti.clairton.repository.Predicate;

@RunWith(VRaptorRunner.class)
public class QueryParamParserTest {
	private @Inject QueryParamParser queryParser;

	private MockHttpServletRequest request = new MockHttpServletRequest();

	@Before
	public void init() {
		// para registrar os converters
		navigate().get("/").execute();
	}

	@Test
	public void testParseComplex() {
		//{f[]=aplicacao[nome], o[][aplicacao[nome]]=*=, v[][aplicacao[nome]]=Auth}
		//{f[]=aplicacao.nome, o[aplicacao.nome]=*=, v[aplicacao.nome]=Auth}
		final String nome = "aplicacao.nome";
		request.addParameter(Param.field(), nome);
		request.addParameter(Param.operation(nome), "*=");
		request.addParameter(Param.value(nome), "Pass");
		final String id = "aplicacao[id]";
		request.addParameter(Param.field(), id);
		request.addParameter(Param.operation(id), ">=");
		request.addParameter(Param.value(id), "0");
		final Collection<Predicate> predicates = queryParser.parse(request,
				Recurso.class);
		assertEquals(2, predicates.size());
		final Iterator<Predicate> interator = predicates.iterator();
		final Predicate predicateNome = interator.next();
		assertEquals("Pass", predicateNome.getValue());
		assertEquals("*=", predicateNome.getComparator().toString());
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
		// f[]=nome&o[nome]=*=&v[nome][]=Pass&f[]=id&o[id]=>=&v[id][]=0
		final String nome = "nome";
		request.addParameter(Param.field(), nome);
		request.addParameter(Param.operation(nome), "*=");
		request.addParameter(Param.value(nome), "Pass");
		final String id = "id";
		request.addParameter(Param.field(), id);
		request.addParameter(Param.operation(id), ">=");
		request.addParameter(Param.value(id), "0");
		final Collection<Predicate> predicates = queryParser.parse(request,
				Aplicacao.class);
		assertEquals(2, predicates.size());
		final Iterator<Predicate> interator = predicates.iterator();
		final Predicate predicateNome = interator.next();
		assertEquals("Pass", predicateNome.getValue());
		assertEquals("*=", predicateNome.getComparator().toString());
		assertTrue(Aplicacao_.nome.equals(predicateNome.getAttribute()));
		final Predicate predicateId = interator.next();
		assertEquals(Long.valueOf(0), predicateId.getValue());
		assertEquals(">=", predicateId.getComparator().toString());
		assertEquals("id", predicateId.getAttribute().getName());
	}
}
