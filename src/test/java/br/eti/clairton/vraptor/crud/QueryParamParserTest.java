package br.eti.clairton.vraptor.crud;

import static br.eti.clairton.vraptor.crud.CdiJUnit4Runner.navigate;
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

@RunWith(CdiJUnit4Runner.class)
public class QueryParamParserTest {
	private @Inject QueryParamParser queryParser;

	private MockHttpServletRequest request = new MockHttpServletRequest();

	@Before
	public void init() {
		// para registrar os converters
		navigate().get("/").execute();
		// f[]=nome&o[nome]=*=&v[nome][]=Pass&f[]=id&o[id]=>=&v[id][]=0
		final String nome = "nome";
		request.addParameter(Param.field(), nome);
		request.addParameter(Param.operation(nome), "*=");
		request.addParameter(Param.value(nome), "Pass");
		final String id = "id";
		request.addParameter(Param.field(), id);
		request.addParameter(Param.operation(id), ">=");
		request.addParameter(Param.value(id), "0");
	}

	@Test
	public void testParse() {
		final Collection<Predicate> predicates = queryParser.parse(request,
				Aplicacao.class);
		assertEquals(2, predicates.size());
		final Iterator<Predicate> interator = predicates.iterator();
		final Predicate predicateNome = interator.next();
		assertEquals("Pass", predicateNome.getValue());
		assertEquals("*=", predicateNome.getOperator().toString());
		assertTrue(Aplicacao_.nome.equals(predicateNome.getAttribute()));
		final Predicate predicateId = interator.next();
		assertEquals(Long.valueOf(0), predicateId.getValue());
		assertEquals(">=", predicateId.getOperator().toString());
		assertEquals("id", predicateId.getAttribute().getName());
	}
}
