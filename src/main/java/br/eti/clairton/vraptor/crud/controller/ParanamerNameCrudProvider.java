package br.eti.clairton.vraptor.crud.controller;

import static java.util.logging.Level.FINE;
import static java.util.logging.Logger.getLogger;
import static javax.enterprise.inject.spi.CDI.current;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Specializes;
import javax.enterprise.inject.spi.BeanManager;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.eti.clairton.model.Base;
import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.set.dsl.SetterHandler;

/**
 * Adequa os parametros dos metodos herdados do {@link CrudController}.
 * 
 * @author Clairton Rodrigo Heinzen clairton.rodrigo@gmail.com
 */
@Specializes
public class ParanamerNameCrudProvider extends ParanamerNameProvider {
	private static final Mirror mirror = new Mirror();

	private static final Logger logger = getLogger(ParanamerNameCrudProvider.class.getSimpleName());

	/**
	 * Altera o nome do parametro que pode ser recebido nos metodos do
	 * {@link CrudController}. Sem isso todo JSON vindo teria que ser
	 * "{'model':{'nome':'teste'}}", como essa customização ele podera ter a
	 * raiz do JSON com o nome do recurso "{'aplicacao':{'nome':'teste'}}"
	 */
	@Override
	public Parameter[] parametersFor(final AccessibleObject executable) {
		final java.lang.reflect.Parameter[] parameters = getMethodParameters(executable);
		final Parameter[] out = new Parameter[parameters.length];

		for (int i = 0; i < out.length; i++) {
			checkIfNameIsPresent(parameters[i]);
			out[i] = new Parameter(i, parameters[i].getName(), executable);
		}

		logger.log(FINE, "parameter names for {0}: {1}", new Object[] { executable, Arrays.toString(out) });
		if (isActive() && executable instanceof Method) {
			final Method method = (Method) executable;
			final Class<?> klass = method.getDeclaringClass();
			if (CrudController.class.isAssignableFrom(klass)) {
				for (final Parameter p : out) {
					if ("model".equals(p.getName()) && Base.class.isAssignableFrom(p.getType())) {
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
		return out;
	}

	private void checkIfNameIsPresent(java.lang.reflect.Parameter parameter) {
		if (!parameter.isNamePresent()) {
			final String msg = String
					.format("Parameters aren't present for %s. You must compile your code with -parameters argument.",
							parameter.getDeclaringExecutable().getName());
			throw new AssertionError(msg);
		}

	}

	private java.lang.reflect.Parameter[] getMethodParameters(AccessibleObject executable) {
		if(!(executable instanceof Executable)){
			throw new IllegalStateException("Only methods or constructors are available");
		}
		return ((Executable) executable).getParameters();
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
