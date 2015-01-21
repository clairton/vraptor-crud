package br.eti.clairton.vraptor.crud;

import static javax.enterprise.inject.spi.CDI.current;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Specializes;
import javax.enterprise.inject.spi.BeanManager;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.set.dsl.SetterHandler;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.eti.clairton.repository.Model;

/**
 * Adequa os parametros dos metodos heradados do {@link CrudController}.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Specializes
public class ParanamerNameCrudProvider extends ParanamerNameProvider {
	private final Mirror mirror = new Mirror();

	/**
	 * Altera o nome do parametro que pode ser recebido nos metodos do
	 * {@link CrudController}. Sem isso todo JSON vindo teria que ser
	 * "{'model':{'nome':'teste'}}", como essa customização ele podera ter a
	 * raiz do JSON com o nome do recurso "{'aplicacao':{'nome':'teste'}}"
	 */
	@Override
	public Parameter[] parametersFor(final AccessibleObject executable) {
		final Parameter[] parameters = super.parametersFor(executable);
		if (isActive() && executable instanceof Method) {
			final Method method = (Method) executable;
			final Class<?> klass = method.getDeclaringClass();
			if (CrudController.class.equals(klass)) {
				for (final Parameter p : parameters) {
					if ("model".equals(p.getName())
							&& Model.class.equals(p.getType())) {
						final ControllerMethod controllerMethod = getControllerMethod();
						if (controllerMethod == null) {
							continue;
						}
						final String resource = getResourceController(controllerMethod);
						final SetterHandler handler = mirror.on(p).set();
						handler.field("name").withValue(resource);
					}
				}
			}
		}
		return parameters;
	}

	private boolean isActive() {
		try {
			final BeanManager beanManager = current().getBeanManager();
			final Context scope = beanManager.getContext(RequestScoped.class);
			return scope.isActive();
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}

	final private ControllerMethod getControllerMethod() {
		final Class<ControllerMethod> type = ControllerMethod.class;
		final Instance<ControllerMethod> instance = current().select(type);
		final ControllerMethod controllerMethod = instance.get();
		return controllerMethod;
	}

	final private String getResourceController(final ControllerMethod cm) {
		try {
			final Class<?> type = cm.getController().getType();
			final Constructor<?> constructor = type.getDeclaredConstructor();
			final Boolean accessible = constructor.isAccessible();
			constructor.setAccessible(Boolean.TRUE);
			final CrudController<?> i = (CrudController<?>) constructor.newInstance();
			constructor.setAccessible(accessible);
			return i.getResourceName();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
}
