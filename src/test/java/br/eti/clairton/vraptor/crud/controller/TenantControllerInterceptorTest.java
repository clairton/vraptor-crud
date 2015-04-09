package br.eti.clairton.vraptor.crud.controller;

import static br.eti.clairton.vraptor.crud.controller.VRaptorRunner.navigate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.requestflow.UserFlow;
import br.eti.clairton.vraptor.crud.model.Aplicacao;

import com.google.gson.Gson;

@RunWith(VRaptorRunner.class)
public class TenantControllerInterceptorTest {
	private final Gson gson = new Gson();
	private @Inject EntityManager entityManager;
	private @Inject Connection connection;
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
		// este registro não aparece
		Aplicacao aplicacao = new Aplicacao(Resource.TENANT);
		entityManager.persist(aplicacao);
		aplicacao = new Aplicacao("TesteOutro");
		entityManager.persist(aplicacao);
		aplicacao = new Aplicacao("Testezinho");
		entityManager.persist(aplicacao);
		entityManager.joinTransaction();
		entityManager.flush();
		entityManager.clear();
		tm.commit();
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
