package br.eti.clairton.vraptor.crud.serializer;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.serialization.gson.GsonBuilderWrapper;
import br.eti.clairton.vraptor.crud.Aplicacao;
import br.eti.clairton.vraptor.crud.CdiJUnit4Runner;
import br.eti.clairton.vraptor.crud.Recurso;

import com.google.gson.Gson;

@RunWith(CdiJUnit4Runner.class)
public class ModelSerializerTest {
	private @Inject Mirror mirror;
	private @Inject GsonBuilderWrapper builder;
	private Gson gson;

	@Before
	public void init() {
		gson = builder.create();
	}

	@Test
	public void testOneToMany() {
		final Aplicacao object = new Aplicacao("Teste");
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
		final String json = gson.toJson(object);
		final Map<?, ?> resultado = gson.fromJson(json, HashMap.class);
		final List<?> list = (List<?>) resultado.get("recursos");
		assertEquals(2, list.size());
		assertEquals("Teste", resultado.get("nome"));
		assertEquals(0.0, resultado.get("id"));
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
		final String json = gson.toJson(object, Recurso.class);
		final Map<?, ?> resultado = gson.fromJson(json, HashMap.class);
		assertEquals("teste", resultado.get("nome"));
		assertEquals(2000.0, resultado.get("id"));
		assertEquals(1000.0, resultado.get("aplicacao"));
	}

}
