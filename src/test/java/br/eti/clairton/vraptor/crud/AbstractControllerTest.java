package br.eti.clairton.vraptor.crud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.test.VRaptorIntegration;
import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.http.Parameters;
import br.com.caelum.vraptor.test.requestflow.UserFlow;
import br.eti.clairton.repository.Repository;

import com.google.gson.Gson;

@RunWith(CdiJUnit4Runner.class)
public class AbstractControllerTest extends VRaptorIntegration {
	private final Gson gson = new Gson();

	private @Inject Repository repository;

	private @Inject Mirror mirror;
	private @Inject EntityManager entityManager;

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

	@Before
	public void init() throws Exception {
		entityManager.getTransaction().begin();
		final Connection connection = entityManager.unwrap(Connection.class);
		final String sql = "DELETE FROM aplicacoes;";
		connection.createStatement().execute(sql);
		entityManager.getTransaction().commit();
		Aplicacao aplicacao = new Aplicacao("Teste");
		repository.save(aplicacao);
		id = aplicacao.getId();
		aplicacao = new Aplicacao("TesteOutro");
		repository.save(aplicacao);
		aplicacao = new Aplicacao("Testezinho");
		repository.save(aplicacao);
	}

	@Test
	public void testCreate() {
		final Long count = repository.from(Aplicacao.class).count() + 1;
		json = "{\"model\":{\"nome\":\"teste\"}}";
		final UserFlow userFlow = navigate().post("/aplicacoes", parameters);
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(200, result.getResponse().getStatus());
		assertAplicacao(new Aplicacao("teste"), result.getResponseBody());
		assertEquals(count, repository.from(Aplicacao.class).count());
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
				request.addParameter("page", "1");
				request.addParameter("per_page", "2");
			}
		};
		json = "{}";
		final VRaptorTestResult result = navigate().get(url, parameters)
				.execute();
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
				// f[]=nome&o[nome]=*=&v[nome][]=Pass&f[]=id&o[id]=>=&v[id][]=0
				final String nome = "nome";
				request.addParameter(Param.field(), nome);
				request.addParameter(Param.operation(nome), "*=");
				request.addParameter(Param.value(nome), "Teste");
				final String id = "id";
				request.addParameter(Param.field(), id);
				request.addParameter(Param.operation(id), ">=");
				request.addParameter(Param.value(id),
						AbstractControllerTest.this.id.toString());
			}
		};
		json = "{}";
		final String url = "/aplicacoes";
		final VRaptorTestResult result = navigate().get(url, parameters)
				.execute();
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
		final VRaptorTestResult result = navigate().get("/aplicacoes")
				.execute();
		assertEquals(200, result.getResponse().getStatus());
		final Map<?, ?> o = gson.fromJson(result.getResponseBody(),
				HashMap.class);
		assertNotNull(o);
		final List<?> aplicacoes = (List<?>) o.get("aplicacoes");
		assertNotNull(aplicacoes);
		assertEquals(3, aplicacoes.size());
	}

	@Test
	public void testShow() {
		final VRaptorTestResult result = navigate().get("/aplicacoes/" + id)
				.execute();
		assertEquals(200, result.getResponse().getStatus());
		final Map<?, ?> o = gson.fromJson(result.getResponseBody(),
				HashMap.class);
		final Map<?, ?> aplicacao = (Map<?, ?>) o.get("aplicacao");
		assertEquals("Teste", aplicacao.get("nome"));
		assertEquals(Double.valueOf(id), aplicacao.get("id"));
	}

	@Test
	public void testDelete() {
		entityManager.clear();
		final Long count = repository.from(Aplicacao.class).count() - 1;
		final UserFlow userFlow = navigate().to("/aplicacoes/" + id,
				HttpMethod.DELETE, new Parameters());
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(200, result.getResponse().getStatus());
		assertEquals("", result.getResponseBody());
		assertEquals(count, repository.from(Aplicacao.class).count());
	}

	@Test
	public void testUpdate() {
		final Aplicacao aplicacaoAtualizar = repository.byId(Aplicacao.class,
				id);
		final String nome = "abc" + new Date().getTime();
		mirror.on(aplicacaoAtualizar).set().field("nome").withValue(nome);
		json = "{\"model\":{\"nome\":\"" + nome + "\", \"id\":\"" + id + "\"}}";
		final UserFlow userFlow = navigate().to("/aplicacoes/" + id,
				HttpMethod.PUT, parameters);
		final VRaptorTestResult result = userFlow.execute();
		assertEquals(200, result.getResponse().getStatus());
		assertAplicacao(aplicacaoAtualizar, result.getResponseBody());
		final Aplicacao resultado = repository.byId(Aplicacao.class, id);
		assertEquals(nome, resultado.getNome());
	}

	private void assertAplicacao(final Aplicacao aplicacao, final String json) {
		final Map<?, ?> o = gson.fromJson(json, HashMap.class);
		final Map<?, ?> model = (Map<?, ?>) o.get("aplicacao");
		assertEquals(aplicacao.getNome(), model.get("nome"));
		assertNotNull(model.get("id"));
	}
}
