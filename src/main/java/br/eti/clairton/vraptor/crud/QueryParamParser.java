package br.eti.clairton.vraptor.crud;

import static br.eti.clairton.vraptor.crud.Param.field;
import static br.eti.clairton.vraptor.crud.Param.operation;
import static br.eti.clairton.vraptor.crud.Param.value;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.metamodel.Attribute;
import javax.servlet.ServletRequest;

import br.com.caelum.vraptor.converter.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.eti.clairton.repository.AttributeBuilder;
import br.eti.clairton.repository.Comparator;
import br.eti.clairton.repository.Comparators;
import br.eti.clairton.repository.Model;
import br.eti.clairton.repository.Predicate;

@Dependent
public class QueryParamParser {
	private final AttributeBuilder builder;

	private final Converters converters;

	@Deprecated
	protected QueryParamParser() {
		this(null, null);
	}

	@Inject
	public QueryParamParser(final AttributeBuilder attributeBuilder,
			final Converters converters) {
		super();
		this.builder = attributeBuilder;
		this.converters = converters;
	}

	public Collection<Predicate> parse(final ServletRequest request,
			final Class<? extends Model> modelType) {
		final Collection<Predicate> predicates = new ArrayList<Predicate>();
		final String[] fields;
		if (request.getParameterValues(field()) != null) {
			fields = request.getParameterValues(field());
		} else {
			fields = new String[] {};
		}
		final Map<String, String[]> params = request.getParameterMap();
		for (final String field : fields) {
			final Predicate predicate;
			final String aField = value(field) + "[]";
			final Attribute<?, ?>[] attrs = builder.with(modelType, field);
			if (params.containsKey(value(field))) {
				final String value = request.getParameter(value(field));
				final String operation;
				if (request.getParameter(operation(field)) != null) {
					operation = request.getParameter(operation(field));
				} else {
					operation = "==";
				}
				predicate = to(attrs, value, operation);
			} else if (params.containsKey(aField)) {
				final String[] values = request.getParameterValues(aField);
				final String operation;
				if (request.getParameter(operation(field)) != null) {
					operation = request.getParameter(operation(field));
				} else {
					operation = "[]";
				}
				final Comparator comparator = Comparators.bySymbol(operation);
				predicate = new Predicate(asList(values), comparator, attrs);
			} else {
				predicate = new Predicate("", Comparators.NULL, attrs);
			}
			predicates.add(predicate);
		}
		return predicates;
	}

	private <T> Predicate to(final Attribute<?, ?>[] attrs, final String value,
			final String symbol) {
		final Attribute<?, ?> lastAttr = attrs[attrs.length - 1];
		@SuppressWarnings("unchecked")
		final Class<T> type = (Class<T>) lastAttr.getJavaType();
		final Converter<T> converter = converters.to(type);
		final T object = converter.convert(value, type);
		final Comparator comparator = Comparators.bySymbol(symbol);
		final Predicate predicate = new Predicate(object, comparator, attrs);
		return predicate;
	}
}
