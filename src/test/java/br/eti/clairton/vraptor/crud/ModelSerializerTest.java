package br.eti.clairton.vraptor.crud;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Before;
import org.junit.Test;

import br.eti.clairton.repository.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

public class ModelSerializerTest {
	private final Mirror mirror = new Mirror();
	private Gson gson;

	@Before
	public void init() {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		final JsonSerializer<Model> serializer = new ModelSerializer();
		gsonBuilder.registerTypeAdapter(Aplicacao.class, serializer);
		gsonBuilder.registerTypeAdapter(Recurso.class, serializer);
		gson = gsonBuilder.create();
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
		final List<Recurso> recursos = Arrays.asList(recurso, recurso2);
		Collections.sort(recursos, new Comparator<Recurso>() {

			@Override
			public int compare(final Recurso o1, final Recurso o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		object.adicionar(recursos);
		assertEquals(json, gson.toJson(object));
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
		final String expected = "{\"aplicacao\":1000,\"nome\":\"teste\",\"id\":2000}";
		final String result = gson.toJson(object, Recurso.class);
		assertEquals(expected, result);
	}

}
