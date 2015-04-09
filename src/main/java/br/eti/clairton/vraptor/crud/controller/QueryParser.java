package br.eti.clairton.vraptor.crud.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.metamodel.Attribute;
import javax.servlet.ServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.caelum.vraptor.converter.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.eti.clairton.repository.AttributeBuilder;
import br.eti.clairton.repository.Comparator;
import br.eti.clairton.repository.Comparators;
import br.eti.clairton.repository.Model;
import br.eti.clairton.repository.Predicate;

@Dependent
public class QueryParser {
	private final AttributeBuilder builder;

	private final Converters converters;

	private final Logger logger = LogManager.getLogger(getClass());

	private final Pattern escaper = Pattern.compile("([^a-zA-z0-9])");

	@Deprecated
	protected QueryParser() {
		this(null, null);
	}

	@Inject
	public QueryParser(final AttributeBuilder attributeBuilder,
			final Converters converters) {
		super();
		this.builder = attributeBuilder;
		this.converters = converters;
	}

	public Collection<Predicate> parse(final ServletRequest request,
			final Class<? extends Model> modelType) {
		final Collection<Predicate> predicates = new ArrayList<Predicate>();
		final Enumeration<String> parameters = request.getParameterNames();
		while (parameters.hasMoreElements()) {
			final String field = parameters.nextElement();
			if (Param.PAGE.equals(field) || Param.PER_PAGE.equals(field)) {
				continue;
			}
			final Predicate predicate;
			final Attribute<?, ?>[] attrs = builder.with(modelType, field);
			final String[] values = request.getParameterValues(field);
			predicate = to(attrs, values);
			predicates.add(predicate);
		}
		return predicates;
	}

	protected Record to(final String[] values) {
		if (values.length == 1) {
			for (final Comparator c : Comparators.values()) {
				final String string = values[0];
				final String value = escaper.matcher(c.toString()).replaceAll(
						"\\\\$1");
				final String regex = "^" + value + ".*";
				logger.debug(regex);
				if (string.matches(regex)) {
					return new Record(string.replaceAll(value, ""), c);
				}
			}
			return new Record(values[0], Comparators.EQUAL);
		} else {
			return new Record(values, Comparators.EQUAL);
		}
	}

	private <T> Predicate to(final Attribute<?, ?>[] attrs, final String[] value) {
		final Attribute<?, ?> lastAttr = attrs[attrs.length - 1];
		@SuppressWarnings("unchecked")
		final Class<T> type = (Class<T>) lastAttr.getJavaType();
		final Converter<T> converter = converters.to(type);
		final Record record = to(value);
		final Object object;
		final Comparator comparator;
		if (value.length > 1) {
			final List<T> a = new ArrayList<>();
			for (final String s : value) {
				a.add(converter.convert(s, type));
			}
			object = a;
			comparator = Comparators.LIKE;
		} else {
			object = converter.convert(record.value.toString(), type);
			comparator = record.comparator;
		}
		final Predicate predicate = new Predicate(object, comparator, attrs);
		return predicate;
	}

}

class Record {
	public final Object value;
	public final Comparator comparator;

	public Record(final Object value, final Comparator comparator) {
		this.value = value;
		this.comparator = comparator;
	}
}
