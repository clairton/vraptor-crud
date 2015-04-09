package br.eti.clairton.vraptor.crud.controller;

import static br.eti.clairton.vraptor.crud.controller.VRaptorRunner.navigate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.http.Parameters;
import br.com.caelum.vraptor.test.requestflow.UserFlow;
import br.eti.clairton.vraptor.crud.model.Aplicacao;
import br.eti.clairton.vraptor.crud.model.Recurso;

import com.google.gson.Gson;

@RunWith(VRaptorRunner.class)
public class CrudControllerTest {
	private final Gson gson = new Gson();

	private @Inject Mirror mirror;
	private @Inject EntityManager entityManager;
	private @Inject Connection connection;

	private Parameters parameters = new Parameters() {
		@Override
		public void fill(MockHttpServletRequest request) {
			mirror.on(request).set().field("content")
					.withValue(json.getBytes());
			mirror.on(request).set().field("contentType")
					.withValue("application/json");
		}
	};

	private String json;

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
		json = "{'aplicacao':{'nome':'teste'}}";
		final UserFlow userFlow = navigate().post("/aplicacoes", parameters);
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(200, result.getResponse().getStatus());
		Long id = assertAplicacao(new Aplicacao("teste"),
				result.getResponseBody());
		assertEquals(200, navigate().get("/aplicacoes/" + id).execute()
				.getResponse().getStatus());
	}

	@Test
	public void testPaginate() {
		final String url = "/aplicacoes";
		final Parameters decorator = parameters;
		parameters = new Parameters() {
			@Override
			public void fill(final MockHttpServletRequest request) {
				decorator.fill(request);
				// page=1&per_page=2
				request.addParameter(Param.PAGE, "1");
				request.addParameter(Param.PER_PAGE, "2");
			}
		};
		json = "{}";
		final UserFlow flow = navigate().get(url, parameters);
		final VRaptorTestResult result = flow.execute();
		assertEquals(200, result.getResponse().getStatus());
		final String response = result.getResponseBody();
		final Map<?, ?> o = gson.fromJson(response, HashMap.class);
		assertNotNull(o);
		final List<?> aplicacoes = (List<?>) o.get("aplicacoes");
		assertNotNull(aplicacoes);
		assertEquals(2, aplicacoes.size());
	}

	@Test
	public void testQueryString() {
		final Parameters decorator = parameters;
		parameters = new Parameters() {
			@Override
			public void fill(final MockHttpServletRequest request) {
				decorator.fill(request);
				// nome=*Teste&id]=>=0
				request.addParameter("nome", "*Teste");
				request.addParameter("id", ">=" + CrudControllerTest.this.id);
			}
		};
		json = "{}";
		final String url = "/aplicacoes";
		final UserFlow flow = navigate().get(url, parameters);
		final VRaptorTestResult result = flow.execute();
		assertEquals(200, result.getResponse().getStatus());
		final String response = result.getResponseBody();
		final Map<?, ?> o = gson.fromJson(response, HashMap.class);
		assertNotNull(o);
		final List<?> aplicacoes = (List<?>) o.get("aplicacoes");
		assertNotNull(aplicacoes);
		assertEquals(1, aplicacoes.size());
	}

	@Test
	public void testIndex() {
		final UserFlow flow = navigate().get("/aplicacoes");
		final VRaptorTestResult result = flow.execute();
		assertEquals(200, result.getResponse().getStatus());
		final String response = result.getResponseBody();
		final Map<?, ?> o = gson.fromJson(response, HashMap.class);
		assertNotNull(o);
		final List<?> aplicacoes = (List<?>) o.get("aplicacoes");
		assertNotNull(aplicacoes);
		assertEquals(3, aplicacoes.size());
		final List<?> links = (List<?>) o.get("links");
		assertEquals(1, links.size());
	}

	@Test
	public void testShow() {
		final UserFlow flow = navigate().get("/aplicacoes/" + id);
		final VRaptorTestResult result = flow.execute();
		assertEquals(200, result.getResponse().getStatus());
		final String response = result.getResponseBody();
		final Map<?, ?> o = gson.fromJson(response, HashMap.class);
		final Map<?, ?> aplicacao = (Map<?, ?>) o.get("aplicacao");
		assertEquals("Testezinho", aplicacao.get("nome"));
		final List<?> links = (List<?>) aplicacao.get("links");
		assertEquals(1, links.size());
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
		assertEquals(404, result.getResponse().getStatus());
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
		assertEquals(200, result.getResponse().getStatus());
		assertEquals("", result.getResponseBody());
		assertEquals(404, navigate().get(url).execute().getResponse()
				.getStatus());
	}

	@Test
	public void testUpdate() {
		final Class<Aplicacao> type = Aplicacao.class;
		final Aplicacao atualizar = entityManager.find(type, id);
		final String nome = "abc" + new Date().getTime();
		mirror.on(atualizar).set().field("nome").withValue(nome);
		json = "{'aplicacao':{'nome':'" + nome + "', 'id':'" + id
				+ "'},recursos:[{'nome':'outroRecurso'}]}";
		final String url = "/aplicacoes/" + id;
		final HttpMethod method = HttpMethod.PUT;
		final UserFlow flow = navigate().to(url, method, parameters);
		final VRaptorTestResult result = flow.execute();
		assertEquals(200, result.getResponse().getStatus());
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
				Aplicacao.class, null, null, null, null, null, null) {
		};
		assertEquals(resourceName, resourceable.getResourceName());
	}
}