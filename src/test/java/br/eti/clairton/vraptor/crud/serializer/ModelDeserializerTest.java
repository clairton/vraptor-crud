package br.eti.clairton.vraptor.crud.serializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import javax.inject.Inject;

import net.vidageek.mirror.dsl.Mirror;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.serialization.gson.GsonBuilderWrapper;
import br.eti.clairton.vraptor.crud.model.Aplicacao;
import br.eti.clairton.vraptor.crud.model.ModelManyToMany;
import br.eti.clairton.vraptor.crud.model.ModelOneToOne;
import br.eti.clairton.vraptor.crud.model.OutroModel;
import br.eti.clairton.vraptor.crud.model.Recurso;

import com.google.gson.Gson;

@RunWith(CdiTestRunner.class)
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
		mirror.on(object).set().field("id").withValue(0l);
		mirror.on(recurso).set().field("id").withValue(1l);
		object.adicionar(Arrays.asList(recurso));
		final Aplicacao result = gson.fromJson(json, Aplicacao.class);
		assertEquals("Teste", result.getNome());
		assertEquals("0", result.getId().toString());
		assertEquals(2, result.getRecursos().size());
	}

	@Test
	public void testManyToOneWithNull() {
		final String json = "{'nome': 'Teste', 'aplicacao': null}";
		final Recurso result = gson.fromJson(json, Recurso.class);
		assertEquals("Teste", result.getNome());
		assertNull(result.getId());
		assertNull(result.getAplicacao());
	}

	@Test
	public void testOneToOneWithNull() {
		final String json = "{'id': '1111', 'aplicacao': [444]}";
		final ModelOneToOne result = gson.fromJson(json, ModelOneToOne.class);
		assertEquals(Long.valueOf(1111l), result.getId());
		assertEquals(Long.valueOf(444l), result.getAplicacao().getId());
	}

	@Test
	public void testOutroModel() {
		final String json = "{'id': '1111', 'nome': 'Teste', 'recursos': [{'nome':'NomeDoRecurso'}]}";
		final OutroModel result = gson.fromJson(json, OutroModel.class);
		assertEquals(Long.valueOf(1111l), result.getId());
		assertEquals("Teste", result.getNome());
		assertEquals("NomeDoRecurso", result.getRecursos().iterator().next().getNome());
	}

	@Test
	public void testManyToMany() {
		final ModelManyToMany object = new ModelManyToMany();
		final String json = "{'id': '1111', 'aplicacoes': [444,555]}";
		mirror.on(object).set().field("id").withValue(0l);
		final ModelManyToMany result = gson.fromJson(json, ModelManyToMany.class);
		assertEquals(Long.valueOf(444l), result.getAplicacoes().get(0).getId());
		assertEquals(Long.valueOf(555l), result.getAplicacoes().get(1).getId());
	}
}
