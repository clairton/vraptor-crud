package br.eti.clairton.vraptor.crud;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import br.com.caelum.vraptor.controller.BeanClass;
import br.com.caelum.vraptor.http.route.PathAnnotationRoutesParser;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.util.StringUtils;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Language;

/**
 * Deixa o nome do controller no plural.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Specializes
@ApplicationScoped
public class PluralRoutesParser extends PathAnnotationRoutesParser {
	private final Inflector inflector;

	@Inject
	public PluralRoutesParser(final Router router,
			@Language final Inflector inflector) {
		super(router);
		this.inflector = inflector;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Route> rulesFor(BeanClass controller) {
		final List<Route> routes = super.rulesFor(controller);
		return routes;
	}

	/**
	 * Retorna somente o nome do controller, o resto das operações é baseada nos
	 * verbos HTTP e não no nome dos metodos.
	 */
	@Override
	protected String defaultUriFor(final String controllerName,
			final String methodName) {
		return controllerName;
	}

	/**
	 * {@inheritDoc}. adiciona o nome do controller para ficar /aplicacoes/{id}
	 */
	@Override
	protected void fixURIs(final Class<?> type, final String[] uris) {
		super.fixURIs(type, uris);
		final Collection<String> collection = new ArrayList<>();
		final String controllerName = extractControllerNameFrom(type);
		for (final String uri : uris) {
			// adiciona o nome do controller para ficar /aplicacoes/{id}
			collection.add(controllerName + uri);
		}
		collection.toArray(uris);
	}

	/**
	 * Extrai o nome do recurso, que é o nome do controller no plural.
	 */
	@Override
	protected String extractControllerNameFrom(final Class<?> type) {
		final String prefix = extractPrefix(type);
		if (prefix == null || prefix.isEmpty()) {
			String baseName = StringUtils.lowercaseFirst(type.getSimpleName());
			if (baseName.endsWith("Controller")) {
				baseName = baseName.substring(0,
						baseName.lastIndexOf("Controller"));
			}
			return "/" + inflector.pluralize(baseName);
		} else {
			return prefix;
		}
	}

	/**
	 * Se o method não estiver anotado com {@link Ignore} chama super.
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isEligible(Method javaMethod) {
		if (javaMethod.isAnnotationPresent(Ignore.class)) {
			return Boolean.FALSE;
		} else {
			return super.isEligible(javaMethod);
		}
	}
}
