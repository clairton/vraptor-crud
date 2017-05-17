package br.eti.clairton.vraptor.crud.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.Gson;

import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.http.Parameters;
import br.com.caelum.vraptor.test.requestflow.UserFlow;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Locale;
import br.eti.clairton.vraptor.crud.model.Aplicacao;
import br.eti.clairton.vraptor.crud.model.Recurso;
import net.vidageek.mirror.dsl.Mirror;

@RunWith(CdiTestRunner.class)
public class CrudControllerIntegrationTest extends ControllerIntegration{
	private final Gson gson = new Gson();

	private @Inject Mirror mirror;
	private @Inject EntityManager entityManager;
	private @Inject Connection connection;

	private Long id;

	private Long recursoId;
	private TransactionManager tm;

	@Before
	public void init() throws Exception {
		final InitialContext context = new InitialContext();
		final Class<TransactionManager> t = TransactionManager.class;
		final String jndi = "java:/TransactionManager";
		tm = t.cast(context.lookup(jndi));
		tm.begin();
		final String sql = "DELETE FROM recursos;DELETE FROM aplicacoes;";
		connection.createStatement().execute(sql);
		Aplicacao aplicacao = new Aplicacao("Teste");
		final Recurso recurso = new Recurso(aplicacao, "Teste");
		entityManager.persist(recurso);
		aplicacao = new Aplicacao("TesteOutro");
		entityManager.persist(aplicacao);
		aplicacao = new Aplicacao("Testezinho");
		entityManager.persist(aplicacao);
		entityManager.joinTransaction();
		entityManager.flush();
		entityManager.clear();
		tm.commit();
		recursoId = recurso.getId();
		id = aplicacao.getId();
	}

	@Test
	public void testCreate() {
		json = "{aplicacao:{nome:'teste'}}";
		final UserFlow userFlow = navigate()
									.addHeader("Content-Type", "application/json; charset=utf-8")
									.addHeader("Authorization", token())
									.post("/aplicacoes");
		final VRaptorTestResult result = userFlow.execute();
		result.wasStatus(200);
		final Long id = assertAplicacao(new Aplicacao("teste"), result.getResponseBody());
		navigate().get("/aplicacoes/" + id).execute().wasStatus(200);
	}

	@Test
	public void testNew() {
		json = "";
		final UserFlow userFlow = navigate()
									.get("/aplicacoes/new")
									.addHeader("Content-Type", "application/json; charset=utf-8")
									.addHeader("Authorization", token());
		final VRaptorTestResult result = userFlow.execute();
		result.wasStatus(200);
		final String json = result.getResponseBody();
		final Map<?, ?> o = gson.fromJson(json, HashMap.class);
		final Map<?, ?> aplicacao = (Map<?, ?>) o.get("aplicacao");
		final List<?> recursos = (List<?>) aplicacao.get("recursos");
		assertTrue(recursos.isEmpty());
		assertFalse(aplicacao.containsKey("nome"));
		assertFalse(aplicacao.containsKey("id"));
	}

	@Test
	public void testEdit() {
		json = "";
		final UserFlow userFlow = navigate()
									.get("/aplicacoes/" + id + "/edit")
									.addHeader("Content-Type", "application/json; charset=utf-8")
									.addHeader("Authorization", token());
		final VRaptorTestResult result = userFlow.execute();
		result.wasStatus(200);
		final String json = result.getResponseBody();
		final Map<?, ?> o = gson.fromJson(json, HashMap.class);
		final Map<?, ?> aplicacao = (Map<?, ?>) o.get("aplicacao");
		final List<?> recursos = (List<?>) aplicacao.get("recursos");
		assertTrue(recursos.isEmpty());
		assertEquals("Testezinho", aplicacao.get("nome"));
		assertEquals(id, Long.valueOf(new BigDecimal(aplicacao.get("id")
				.toString()).longValue()));
	}

	@Test
	public void testPaginate() {
		final String url = "/aplicacoes?per_page=2&page=1";
		json = "{}";
		final UserFlow flow = navigate()
								.addHeader("Content-Type", "application/json; charset=utf-8")
								.addHeader("Authorization", token())
								.get(url);
		final VRaptorTestResult result = flow.execute();
		result.wasStatus(200);
		final String response = result.getResponseBody();
		final Map<?, ?> o = gson.fromJson(response, HashMap.class);
		assertNotNull(o);
		final List<?> aplicacoes = (List<?>) o.get("aplicacoes");
		assertNotNull(aplicacoes);
		assertEquals(2, aplicacoes.size());
	}

	@Test
	public void testQueryString() {
		json = "{}";
		final String url = "/aplicacoes?nome=*Teste&id=>="+id;
		final UserFlow flow = navigate()
									.get(url)
									.addHeader("Content-Type", "application/json; charset=utf-8")
									.addHeader("Authorization", token());
		final VRaptorTestResult result = flow.execute();
		result.wasStatus(200);
		final String response = result.getResponseBody();
		final Map<?, ?> o = gson.fromJson(response, HashMap.class);
		assertNotNull(o);
		final List<?> aplicacoes = (List<?>) o.get("aplicacoes");
		assertNotNull(aplicacoes);
		assertEquals(1, aplicacoes.size());
	}

	@Test
	public void testOrder() {
		json = "{}";
		final String url = "/aplicacoes?nome=*Teste&direction=ASC&sort=name";
		final UserFlow flow = navigate()
								.get(url)
								.addHeader("Content-Type", "application/json; charset=utf-8")
								.addHeader("Authorization", token());
		final VRaptorTestResult result = flow.execute();
		result.wasStatus(200);
		final String response = result.getResponseBody();
		final Map<?, ?> o = gson.fromJson(response, Map.class);
		assertNotNull(o);
		final List<?> aplicacoes = (List<?>) o.get("aplicacoes");
		assertNotNull(aplicacoes);
		assertEquals(3, aplicacoes.size());
		assertEquals("Teste", ((Map<?, ?>) aplicacoes.get(0)).get("nome"));
		assertEquals("TesteOutro", ((Map<?, ?>) aplicacoes.get(1)).get("nome"));
		assertEquals("Testezinho", ((Map<?, ?>) aplicacoes.get(2)).get("nome"));
	}

	@Test
	public void testIndex() {
		final UserFlow flow = navigate().get("/aplicacoes");
		final VRaptorTestResult result = flow.execute();
		result.wasStatus(200);
		final String response = result.getResponseBody();
		final Map<?, ?> o = gson.fromJson(response, HashMap.class);
		assertNotNull(o);
		final List<?> aplicacoes = (List<?>) o.get("aplicacoes");
		assertNotNull(aplicacoes);
		assertEquals(3, aplicacoes.size());
	}

	@Test
	public void testShow() {
		final UserFlow flow = navigate().get("/aplicacoes/" + id);
		final VRaptorTestResult result = flow.execute();
		result.wasStatus(200);
		final String response = result.getResponseBody();
		final Map<?, ?> o = gson.fromJson(response, HashMap.class);
		final Map<?, ?> aplicacao = (Map<?, ?>) o.get("aplicacao");
		assertEquals("Testezinho", aplicacao.get("nome"));
		assertEquals(Double.valueOf(id), aplicacao.get("id"));
	}

	@Test
	public void testShowWithTenant() throws Exception {
		/*
		 * Criado uma aplicação com o nome filtrado no tenant como não pode
		 * encontrar deve retornar 404
		 */
		tm.begin();
		final Aplicacao aplicacao = new Aplicacao(Resource.TENANT);
		entityManager.persist(aplicacao);
		entityManager.joinTransaction();
		entityManager.flush();
		tm.commit();
		Long id = aplicacao.getId();
		final UserFlow flow = navigate().get("/aplicacoes/" + id);
		final VRaptorTestResult result = flow.execute();
		result.wasStatus(404);
	}

	@Test
	public void testShowRecursive() {
		final UserFlow flow = navigate().get("/recursos/" + recursoId);
		final VRaptorTestResult result = flow.execute();
		assertEquals(200, result.getResponse().getStatus());
		final String response = result.getResponseBody();
		final Map<?, ?> o = gson.fromJson(response, HashMap.class);
		final Map<?, ?> recurso = (Map<?, ?>) o.get("recurso");
		assertEquals("Teste", recurso.get("nome"));
		assertNotNull(recurso.get("aplicacao"));
		assertEquals(Double.valueOf(recursoId), recurso.get("id"));
	}

	@Test
	public void testRemove() {
		entityManager.clear();
		final HttpMethod method = HttpMethod.DELETE;
		final String url = "/recursos/" + recursoId;
		final UserFlow userFlow = navigate().to(url, method, new Parameters());
		final VRaptorTestResult result = userFlow.execute();
		result.wasStatus(204);
		assertEquals("", result.getResponseBody());
		navigate().get(url).execute().wasStatus(404);
	}

	@Test
	public void testUpdateWithPut() {
		final Class<Aplicacao> type = Aplicacao.class;
		final Aplicacao atualizar = entityManager.find(type, id);
		final String nome = "abc" + new Date().getTime();
		mirror.on(atualizar).set().field("nome").withValue(nome);
		json = "{'aplicacao':{'nome':'" + nome + "', 'id':'" + id + "'},recursos:[{'nome':'outroRecurso'}]}";
		final String url = "/aplicacoes/" + id;
		final HttpMethod method = HttpMethod.PUT;
		final UserFlow flow = navigate()
								.to(url, method, new Parameters())
								.addHeader("Content-Type", "application/json; charset=utf-8")
								.addHeader("Authorization", token());
		final VRaptorTestResult result = flow.execute();
		assertEquals(200, result.getResponse().getStatus());
		assertAplicacao(atualizar, result.getResponseBody());
		final Aplicacao resultado = entityManager.find(type, id);
		assertEquals(nome, resultado.getNome());
	}

	@Test
	public void testUpdateWithPatch() {
		final Class<Aplicacao> type = Aplicacao.class;
		final Aplicacao atualizar = entityManager.find(type, id);
		final String nome = "def" + new Date().getTime();
		mirror.on(atualizar).set().field("nome").withValue(nome);
		json = "{'aplicacao':{'nome':'" + nome + "', 'id':'" + id + "'},recursos:[{'nome':'outroRecursoDiferente'}]}";
		final String url = "/aplicacoes/" + id;
		final HttpMethod method = HttpMethod.PATCH;
		final UserFlow flow = navigate()
									.to(url, method, new Parameters())
									.addHeader("Content-Type", "application/json; charset=utf-8")
									.addHeader("Authorization", token());
		final VRaptorTestResult result = flow.execute();
		result.wasStatus(200);
		assertAplicacao(atualizar, result.getResponseBody());
		final Aplicacao resultado = entityManager.find(type, id);
		assertEquals(nome, resultado.getNome());
	}

	private Long assertAplicacao(final Aplicacao aplicacao, final String json) {
		final Map<?, ?> o = gson.fromJson(json, HashMap.class);
		final Map<?, ?> model = (Map<?, ?>) o.get("aplicacao");
		assertEquals(aplicacao.getNome(), model.get("nome"));
		assertNotNull(model.get("id"));
		return new BigDecimal(model.get("id").toString()).longValue();
	}

	@Test
	public void testGetResourceName() {
		final String resourceName = "aplicacao";
		final CrudController<Aplicacao> resourceable = new CrudController<Aplicacao>(
				Aplicacao.class, null, null, Inflector.getForLocale(Locale.pt_BR), null, null) {
		};
		assertEquals(resourceName, resourceable.getResourceName());
	}
}
