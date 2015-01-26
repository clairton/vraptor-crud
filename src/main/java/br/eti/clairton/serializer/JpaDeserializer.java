package br.eti.clairton.serializer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

import net.vidageek.mirror.dsl.AccessorsController;
import net.vidageek.mirror.dsl.ClassController;
import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.set.dsl.FieldSetter;
import net.vidageek.mirror.set.dsl.SetterHandler;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public abstract class JpaDeserializer<T> implements JsonDeserializer<T> {
	private final Mirror mirror;
	private final Logger logger;
	private final EntityManager entityManager;

	public JpaDeserializer(EntityManager entityManager, final Mirror mirror, final Logger logger) {
		super();
		this.mirror = mirror;
		this.logger = logger;
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public T deserialize(final JsonElement json, final Type type,
			final JsonDeserializationContext context) throws JsonParseException {
		try {
			@SuppressWarnings("unchecked")
			final Class<T> klazz = (Class<T>) Class.forName(type.getTypeName());
			final T model = klazz.cast(klazz.newInstance());
			final ClassController<?> controller = mirror.on(klazz);
			final AccessorsController accessor = mirror.on(model);
			final JsonObject jsonObject = (JsonObject) json;
			for (final Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				final Field field = controller.reflect().field(entry.getKey());
				final Object value;
				if (field.isAnnotationPresent(OneToMany.class)
						|| field.isAnnotationPresent(ManyToMany.class)) {
					final Type fielType = field.getGenericType();
					final ParameterizedType pType = (ParameterizedType) fielType;
					final Type[] arr = pType.getActualTypeArguments();
					final Class<?> elementType = (Class<?>) arr[0];
					final List<Object> list = new ArrayList<Object>();
					final JsonArray array = entry.getValue().getAsJsonArray();
					for (final JsonElement element : array) {
						final Object object = elementType.newInstance();
						final SetterHandler handler = mirror.on(object).set();
						EntityType<?> entity = entityManager.getMetamodel().entity(elementType);
						javax.persistence.metamodel.Type<?> idType = entity.getIdType();
						Attribute<?, ?> attribute = entity.getId(idType.getJavaType());
						final String fieldIdName = attribute.getName();
						final FieldSetter fieldSetter = handler.field(fieldIdName);
						fieldSetter.withValue(element.getAsLong());
						list.add(object);
					}
					value = list;
				} else if (field.isAnnotationPresent(ManyToOne.class)
						|| field.isAnnotationPresent(OneToOne.class)) {
					if (JsonNull.class.isInstance(entry.getValue())) {
						value = null;
					} else {
						value = field.getType().newInstance();
						EntityType<?> entity = entityManager.getMetamodel().entity(value.getClass());
						javax.persistence.metamodel.Type<?> idType = entity.getIdType();
						Attribute<?, ?> attribute = entity.getId(idType.getJavaType());
						final String fieldIdName = attribute.getName();
						final SetterHandler handler = mirror.on(value).set();
						final FieldSetter fieldSetter = handler.field(fieldIdName);
						fieldSetter.withValue(entry.getValue().getAsLong());
					}
				} else {
					value = context.deserialize(entry.getValue(),
							field.getType());
				}
				accessor.set().field(field).withValue(value);
			}
			return model;
		} catch (final Exception e) {
			logger.error("Erro ao deserializar " + json, e);
			throw new JsonParseException(e);
		}
	}
}
