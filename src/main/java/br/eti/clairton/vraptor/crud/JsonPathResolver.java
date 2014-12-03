package br.eti.clairton.vraptor.crud;

import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.view.DefaultPathResolver;

@Specializes
public class JsonPathResolver extends DefaultPathResolver {
    @Inject
    public JsonPathResolver(final FormatResolver resolver) {
        super(resolver);
    }
    
    /**
     * Sobreescre a extensão padrão, que será JSON e não JSP. {@inheritDoc}.
     */
    @Override
    protected String getExtension() {
        return "json";
    }
}
