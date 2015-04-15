package br.eti.clairton.vraptor.crud.hypermedia;

import java.lang.reflect.Method;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.security.Extractor;
import br.eti.clairton.vraptor.hypermedia.Operation;
import br.eti.clairton.vraptor.hypermedia.Resource;

@Specializes
public class CurrentResource extends
		br.eti.clairton.vraptor.hypermedia.CurrentResource {
	private final String resource;
	private final String operation;
	private final MethodInvocation<Object> interceptor = new MethodInvocation<Object>() {

		@Override
		public Object intercept(Object proxy, Method method, Object[] args,
				SuperMethod superMethod) {
			return null;
		}
	};

	@Inject
	public CurrentResource(final MutableRequest request,
			final Extractor extractor, final Inflector inflector,
			final ControllerMethod method, final Proxifier proxifier) {
		super(request, method);
		if (method != null) {
			final Class<?> controller = method.getController().getType();
			final Object proxy = proxifier.proxify(controller, interceptor);
			this.resource = extractor.getResource(proxy);
			this.operation = extractor.getOperation(method.getMethod());
		} else {
			this.resource = null;
			this.operation = null;
		}
	}

	@Produces
	@Resource
	public String getResource() {
		return resource;
	}

	@Produces
	@Operation
	public String getOperation() {
		return operation;
	}

	public static String getResource(final String uri, final Inflector inflector) {
		final String withouQuery = uri.split("\\?")[0];
		final String[] splitSlash = withouQuery.split("/");
		final String withoutSlash = splitSlash[splitSlash.length >= 2 ? 1 : 0];
		return inflector.singularize(withoutSlash);
	}
}
