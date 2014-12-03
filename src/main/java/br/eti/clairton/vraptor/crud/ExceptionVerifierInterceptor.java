package br.eti.clairton.vraptor.crud;

import static br.com.caelum.vraptor.view.Results.http;
import static br.com.caelum.vraptor.view.Results.json;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolationException;

import br.com.caelum.vraptor.Result;

@Interceptor
@ExceptionVerifier
public class ExceptionVerifierInterceptor {
    private final Result result;
    
    private final Logger logger;
    
    /**
     * @deprecated CDI eyes only
     */
    protected ExceptionVerifierInterceptor() {
        this(null, null);
    }
    
    @Inject
    public ExceptionVerifierInterceptor(final Result result, final Logger logger) {
        this.result = result;
        this.logger = logger;
    }
    
    @AroundInvoke
    public Object verify(final InvocationContext invocationContext) throws Exception {
        Object object = null;
        try {
            object = invocationContext.proceed();
        } catch (final NoResultException e) {
            logger.fine(String.format("NoResultException: %s", e.getMessage()));
            result.notFound();
        } catch (final ConstraintViolationException e) {
            logger.fine(String.format("ConstraintViolationException: %s", e.getMessage()));
            result.use(http()).setStatusCode(412);
            result.use(json()).from(e.getConstraintViolations()).serialize();
        } catch (final Throwable e) {
            logger.log(Level.SEVERE, "Exceção não tratada", e);
            throw e;
        }
        return object;
    }
}
