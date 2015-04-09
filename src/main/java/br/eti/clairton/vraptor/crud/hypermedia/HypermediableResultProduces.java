package br.eti.clairton.vraptor.crud.hypermedia;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import br.com.caelum.vraptor.Result;
import br.eti.clairton.vraptor.crud.annotation.Operation;
import br.eti.clairton.vraptor.crud.annotation.Resource;

@Dependent
public class HypermediableResultProduces {
	private final Result result;
	private final String resource;
	private final String operation;

	@Inject
	public HypermediableResultProduces(final @Default Result result,
			@Resource final String resource, @Operation final String operation) {
		this.resource = resource;
		this.operation = operation;
		this.result = result;
	}

	@Produces
	@Hypermediable
	public Result getResult(final InjectionPoint ip) {
		// final Bean<?> bean = ip.getBean();
		// final CreationalContext<?> ctx = bm.createCreationalContext(null);
		// final Class<?> type = method.getController().getType();
		// final Object object = bm.getReference(bean, type, ctx);
		// final String resource =
		// method.getMethod().getName();//extractor.getResource(object);
		// final String operation =
		// "aplicacao";//extractor.getOperation(method.getMethod());
		return new HypermediableResult(result, resource, operation);
	}
}
