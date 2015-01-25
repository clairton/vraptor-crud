package br.eti.clairton.serializer;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import net.vidageek.mirror.dsl.AccessorsController;
import net.vidageek.mirror.dsl.Mirror;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public abstract class JpaSerializer<T> implements JsonSerializer<T> {
	private final Mirror mirror;
	private final Logger logger;

	private final List<String> ignored = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;

		{
			add("serialVersionUID");
			add("MIRROR");
		}
	};

	public JpaSerializer(final Mirror mirror, final Logger logger) {
		super();
		this.mirror = mirror;
		this.logger = logger;
	}

	public void addIgnoredField(final String field) {
		ignored.add(field);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public JsonElement serialize(final T src, final Type type,
			final JsonSerializationContext context) {
		try {
			final JsonObject json = new JsonObject();
			final String name = type.getTypeName();
			final List<Field> fields = mirror.on(name).reflectAll().fields();
			final AccessorsController controller = mirror.on(src);
			for (final Field field : fields) {
				final String tag = field.getName();
				if (ignored.contains(tag)) {
					continue;
				}
				final Object value;
				if (field.isAnnotationPresent(OneToMany.class)
						|| field.isAnnotationPresent(ManyToMany.class)) {
					final Collection<Object> ids = new ArrayList<Object>();
					final Object v = controller.get().field(tag);
					final Collection<?> models = Collection.class.cast(v);
					for (Object model : models) {
						ids.add(mirror.on(model).get().field("id"));
					}
					value = ids;
				} else if (field.isAnnotationPresent(ManyToOne.class)
						|| field.isAnnotationPresent(OneToOne.class)) {
					final Object v = controller.get().field(tag);
					value = mirror.on(v).get().field("id");
				} else {
					value = mirror.on(src).get().field(tag);
				}
				final JsonElement element;
				if (value == null) {
					element = context.serialize(value);
				} else {
					element = context.serialize(value, value.getClass());
				}
				json.add(tag, element);
			}
			return json;
		} catch (final Exception e) {
			logger.error("Erro ao serializar " + src, e);
			throw new JsonParseException(e);
		}
	}
}