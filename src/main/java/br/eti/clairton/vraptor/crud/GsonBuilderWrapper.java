package br.eti.clairton.vraptor.crud;

import static java.util.Collections.singletonList;
import static java.util.logging.Level.CONFIG;
import static java.util.logging.Logger.getLogger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import br.com.caelum.vraptor.serialization.Serializee;
import br.com.caelum.vraptor.serialization.gson.Exclusions;
import br.com.caelum.vraptor.serialization.gson.RegisterStrategy;
import br.com.caelum.vraptor.serialization.gson.RegisterType;
import br.eti.clairton.iterablebypriority.Iterables;

@Specializes
public class GsonBuilderWrapper extends br.com.caelum.vraptor.serialization.gson.GsonBuilderWrapper {
	private final Logger logger = getLogger(GsonBuilderWrapper.class.getSimpleName());
	private final Iterable<JsonSerializer<?>> jsonSerializers;
	private final Iterable<JsonDeserializer<?>> jsonDeserializers;
	private List<ExclusionStrategy> exclusions;

	@Inject
	public GsonBuilderWrapper(final @Any Instance<JsonSerializer<?>> jsonSerializers, final @Any Instance<JsonDeserializer<?>> jsonDeserializers, final Serializee serializee) {
		super(jsonSerializers, jsonDeserializers, serializee);
		this.jsonSerializers = Iterables.sort(jsonSerializers);
		this.jsonDeserializers = Iterables.sort(jsonDeserializers);
		ExclusionStrategy exclusion = new Exclusions(serializee);
		exclusions = singletonList(exclusion);
	}

	@Override
	public Gson create() {
		for (final JsonSerializer<?> adapter : jsonSerializers) {
			registerAdapter(getAdapterType(adapter), adapter);
		}

		for (final JsonDeserializer<?> adapter : jsonDeserializers) {
			registerAdapter(getAdapterType(adapter), adapter);
		}

		for (final ExclusionStrategy exclusion : exclusions) {
			getGsonBuilder().addSerializationExclusionStrategy(exclusion);
		}

		return getGsonBuilder().create();
	}

	protected void registerAdapter(final Class<?> adapterType, final Object adapter) {
		logger.log(CONFIG, "Add adapter {0} to type {1}", new Object[]{adapter.getClass().getSimpleName(), adapterType.getSimpleName()});
		final RegisterStrategy registerStrategy = adapter.getClass().getAnnotation(RegisterStrategy.class);
		if ((registerStrategy != null) && (registerStrategy.value().equals(RegisterType.SINGLE))) {
			getGsonBuilder().registerTypeAdapter(adapterType, adapter);
		} else {
			getGsonBuilder().registerTypeHierarchyAdapter(adapterType, adapter);
		}
	}

	protected Class<?> getAdapterType(final Object adapter) {
		final Class<?> klazz;
		if (adapter.getClass().getName().contains("$Proxy$")) {
			final String[] split = adapter.getClass().getName().split("\\$Proxy\\$");
			try {
				klazz = Class.forName(split[0]);
			} catch (final ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		} else {
			klazz = adapter.getClass();
		}
		final Type[] genericInterfaces = klazz.getGenericInterfaces();
		final ParameterizedType type = (ParameterizedType) genericInterfaces[0];
		final Type actualType = type.getActualTypeArguments()[0];

		if (actualType instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) actualType).getRawType();
		} else {
			return (Class<?>) actualType;
		}
	}
}
