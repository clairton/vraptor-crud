package br.eti.clairton.vraptor.crud;

import static br.eti.clairton.vraptor.crud.VRaptorRunner.navigate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.requestflow.UserFlow;

import com.google.gson.Gson;

@RunWith(VRaptorRunner.class)
public class TenantControllerInterceptorTest {
	private final Gson gson = new Gson();
	private @Inject EntityManager entityManager;
	private @Inject Connection connection;

	@Before
	public void init() throws Exception {
		entityManager.getTransaction().begin();
		final String sql = "DELETE FROM recursos;DELETE FROM aplicacoes;";
		connection.createStatement().execute(sql);
		entityManager.getTransaction().commit();
		entityManager.getTransaction().begin();
		// este registro não aparece
		Aplicacao aplicacao = new Aplicacao(Resource.TENANT);
		entityManager.persist(aplicacao);
		aplicacao = new Aplicacao("TesteOutro");
		entityManager.persist(aplicacao);
		aplicacao = new Aplicacao("Testezinho");
		entityManager.persist(aplicacao);
		entityManager.getTransaction().commit();
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
		// se mostra 3 é pq não aplicou o tenant
		assertEquals(2, aplicacoes.size());
	}
}
