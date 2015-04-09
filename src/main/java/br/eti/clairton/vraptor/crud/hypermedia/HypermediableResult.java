package br.eti.clairton.vraptor.crud.hypermedia;

import java.util.Map;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.eti.clairton.vraptor.hypermedia.HypermediaJsonSerialization;

@Vetoed
public class HypermediableResult implements Result {
	private final Result result;
	private final String operation;
	private final String resource;

	/**
	 * @deprecated CDI eyes only
	 */
	protected HypermediableResult() {
		this(null, null, null);
	}

	public HypermediableResult(final Result result, final String resource,
			final String operation) {
		this.result = result;
		this.operation = operation;
		this.resource = resource;
	}

	@Override
	public Result include(final String key, final Object value) {
		return result.include(key, value);
	}

	@Override
	public Result include(final Object value) {
		return result.include(value);
	}

	@Override
	public <T extends View> T use(final Class<T> view) {
		final T toUse = result.use(view);
		if (HypermediaJsonSerialization.class.equals(view)) {
			try {
				((HypermediaJsonSerialization) toUse).resource(resource)
						.operation(operation);
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
		return toUse;
	}

	@Override
	public Result on(final Class<? extends Exception> exception) {
		return result.on(exception);
	}

	@Override
	public boolean used() {
		return result.used();
	}

	@Override
	public Map<String, Object> included() {
		return result.included();
	}

	@Override
	public void forwardTo(final String uri) {
		result.forwardTo(uri);
	}

	@Override
	public void redirectTo(final String uri) {
		result.redirectTo(uri);
	}

	@Override
	public <T> T forwardTo(final Class<T> controller) {
		return result.forwardTo(controller);
	}

	@Override
	public <T> T redirectTo(final Class<T> controller) {
		return result.redirectTo(controller);
	}

	@Override
	public <T> T of(final Class<T> controller) {
		return result.of(controller);
	}

	@Override
	public <T> T redirectTo(final T controller) {
		return result.redirectTo(controller);
	}

	@Override
	public <T> T forwardTo(final T controller) {
		return result.forwardTo(controller);
	}

	@Override
	public <T> T of(final T controller) {
		return result.of(controller);
	}

	@Override
	public void nothing() {
		result.nothing();
	}

	@Override
	public void notFound() {
		result.notFound();
	}

	@Override
	public void permanentlyRedirectTo(final String uri) {
		result.permanentlyRedirectTo(uri);
	}

	@Override
	public <T> T permanentlyRedirectTo(final Class<T> controller) {
		return result.permanentlyRedirectTo(controller);
	}

	@Override
	public <T> T permanentlyRedirectTo(final T controller) {
		return result.permanentlyRedirectTo(controller);
	}
}
