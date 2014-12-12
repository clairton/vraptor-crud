package br.eti.clairton.vraptor.crud;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import net.vidageek.mirror.dsl.AccessorsController;
import net.vidageek.mirror.dsl.Mirror;
import br.eti.clairton.repository.Model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Deserializa os objetos da de forma a integrar com o modo ActiveSerializer.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Dependent
public class ModelSerializer implements JsonSerializer<Model> {
	private final Mirror mirror = new Mirror();

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public JsonElement serialize(final Model src, final Type type,
			final JsonSerializationContext context) {
		try {
			final JsonObject json = new JsonObject();
			final String name = type.getTypeName();
			final List<Field> fields = mirror.on(name).reflectAll().fields();
			final AccessorsController controller = mirror.on(src);
			for (final Field field : fields) {
				final String tag = field.getName();
				if ("serialVersionUID".equals(tag) || "MIRROR".equals(tag)) {
					continue;
				}
				final Object value;
				if (field.isAnnotationPresent(OneToMany.class)
						|| field.isAnnotationPresent(ManyToMany.class)) {
					final Collection<Long> ids = new ArrayList<>();
					final Object v = controller.get().field(tag);
					final Collection<?> models = Collection.class.cast(v);
					for (Object model : models) {
						ids.add(((Model) model).getId());
					}
					value = ids;
				} else if (field.isAnnotationPresent(ManyToOne.class)) {
					final Model v = (Model) controller.get().field(tag);
					value = v.getId();
				} else {
					value = mirror.on(src).get().field(tag);
				}
				final JsonElement element = context.serialize(value);
				json.add(tag, element);
			}
			return json;
		} catch (final Exception e) {
			throw new JsonParseException(e);
		}
	}
}
