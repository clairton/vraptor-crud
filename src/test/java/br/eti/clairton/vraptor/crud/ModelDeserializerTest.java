package br.eti.clairton.vraptor.crud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import javax.inject.Inject;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.serialization.gson.GsonBuilderWrapper;

import com.google.gson.Gson;

@RunWith(CdiJUnit4Runner.class)
public class ModelDeserializerTest {
	private @Inject Mirror mirror;
	private @Inject GsonBuilderWrapper builder;
	private Gson gson;

	@Before
	public void init() {
		gson = builder.create();
	}

	@Test
	public void testManyToOne() {
		final Long idAplicacao = 1000l;
		final Aplicacao aplicacao = new Aplicacao("Teste");
		mirror.on(aplicacao).set().field("id").withValue(idAplicacao);
		mirror.on(aplicacao).set().field("nome").withValue(null);
		final Recurso object = new Recurso(aplicacao, "teste");
		final Long idRecurso = 2000l;
		mirror.on(object).set().field("id").withValue(idRecurso);
		final String json = "{id:'2000',nome:'Teste',aplicacao:'1000'}";
		final Recurso result = gson.fromJson(json, Recurso.class);
		assertEquals("Teste", result.getNome());
		assertEquals(idRecurso, result.getId());
		assertEquals(idAplicacao, aplicacao.getId());
		assertNull(aplicacao.getNome());
	}

	@Test
	public void testOneToMany() {
		final Aplicacao object = new Aplicacao("Teste");
		final String json = "{\"recursos\":[1,2],\"nome\":\"Teste\",\"id\":0}";
		final Recurso recurso = new Recurso(object, "Teste");
		final Recurso recurso2 = new Recurso(object, "Outro");
		mirror.on(object).set().field("id").withValue(0l);
		mirror.on(recurso).set().field("id").withValue(1l);
		mirror.on(recurso2).set().field("id").withValue(2l);
		object.adicionar(Arrays.asList(recurso, recurso2));
		final Aplicacao result = gson.fromJson(json, Aplicacao.class);
		assertEquals("Teste", result.getNome());
		assertEquals("0", result.getId().toString());
		assertEquals(2, result.getRecursos().size());
	}

}
