package br.eti.clairton.vraptor.crud;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

/**
 * Annotação Interceptar construção de controllers para adicionar tenant.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Inherited
@Documented
@InterceptorBinding
@Target(ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface TenantController {
}