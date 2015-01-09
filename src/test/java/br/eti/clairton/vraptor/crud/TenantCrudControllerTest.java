package br.eti.clairton.vraptor.crud;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.inject.Inject;
import javax.servlet.ServletRequest;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.util.test.MockResult;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.repository.Repository;

@RunWith(CdiJUnit4Runner.class)
public class TenantCrudControllerTest {
	private TenantCrudController<Aplicacao> controller;
	private final Repository repository = mock(Repository.class);
	private final Result result = new MockResult();
	private @Inject Inflector inflector;
	private @Inject Mirror mirror;
	private final ServletRequest request = new MockHttpServletRequest();
	private @Inject QueryParamParser queryParser;
	private Object tenantValue = "admin";

	@Test
	public void test() throws Exception {
		controller = new TenantCrudController<Aplicacao>(Aplicacao.class,
				repository, result, inflector, mirror, request, queryParser,
				tenantValue) {
		};
		controller.setTenant();
		verify(repository).tenantValue(eq(tenantValue));
	}
}
