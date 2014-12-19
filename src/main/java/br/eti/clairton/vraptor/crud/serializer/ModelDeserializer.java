package br.eti.clairton.vraptor.crud.serializer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.enterprise.context.Dependent;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import net.vidageek.mirror.dsl.AccessorsController;
import net.vidageek.mirror.dsl.ClassController;
import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.set.dsl.FieldSetter;
import net.vidageek.mirror.set.dsl.SetterHandler;
import br.eti.clairton.repository.Model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Serializa os objetos da de forma a integrar com o modo ActiveSerializer.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Dependent
public class ModelDeserializer implements JsonDeserializer<Model> {
	private final Mirror mirror = new Mirror();

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public Model deserialize(final JsonElement json, final Type type,
			final JsonDeserializationContext context) throws JsonParseException {
		try {
			final Class<?> klazz = Class.forName(type.getTypeName());
			final Model model = (Model) klazz.newInstance();
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
					final List<Object> list = new ArrayList<>();
					final JsonArray array = entry.getValue().getAsJsonArray();
					for (final JsonElement element : array) {
						final Model object = (Model) elementType.newInstance();
						final SetterHandler handler = mirror.on(object).set();
						final FieldSetter fieldSetter = handler.field("id");
						fieldSetter.withValue(element.getAsLong());
						list.add(object);
					}
					value = list;
				} else if (field.isAnnotationPresent(ManyToOne.class)) {
					if (JsonNull.class.isInstance(entry.getValue())) {
						value = null;
					} else {
						value = field.getType().newInstance();
						final SetterHandler handler = mirror.on(value).set();
						final FieldSetter fieldSetter = handler.field("id");
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
			throw new JsonParseException(e);
		}
	}
}
