package br.eti.clairton.vraptor.crud;

import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.metamodel.Attribute;
import javax.servlet.ServletRequest;

import br.com.caelum.vraptor.converter.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.eti.clairton.repository.AttributeBuilder;
import br.eti.clairton.repository.Model;
import br.eti.clairton.repository.Operator;
import br.eti.clairton.repository.Operators;
import br.eti.clairton.repository.Predicate;

@Dependent
public class QueryParamParser {
    private final AttributeBuilder attributeBuilder;
    
    private final Converters converters;
    
    @Deprecated
    protected QueryParamParser() {
        this(null, null);
    }
    
    @Inject
    public QueryParamParser(final AttributeBuilder attributeBuilder, final Converters converters) {
        super();
        this.attributeBuilder = attributeBuilder;
        this.converters = converters;
    }
    
    public Collection<Predicate> parse(final ServletRequest request, final Class<? extends Model> modelType) {
        final Collection<Predicate> predicates = new ArrayList<>();
        final String[] fields;
        if(request.getParameterValues(Param.field()) != null){
        	fields = request.getParameterValues(Param.field());
        }else{
        	fields = new String[]{};
        }
        for (final String field : fields) {
            final String value = request.getParameter(Param.value(field));
            final String operation;
            if (request.getParameter(Param.operation(field)) != null) {
                operation = request.getParameter(Param.operation(field));
            } else {
                operation = "==";
            }
            final Predicate predicate = to(field, value, operation, modelType);
            predicates.add(predicate);
        }
        return predicates;
    }
    
    private <T> Predicate to(final String field, final String value, final String symbol,
            final Class<? extends Model> modelType) {
        final Attribute<?, ?>[] attributes = attributeBuilder.with(modelType, field);
        @SuppressWarnings("unchecked")
        final Class<T> type = ( Class<T> ) attributes[attributes.length - 1].getJavaType();
        final Converter<T> converter = converters.to(type);
        final T object = converter.convert(value, type);
        final Operator operator = Operators.bySymbol(symbol);
        final Predicate predicate = new Predicate(object, operator, attributes);
        return predicate;
    }
}
