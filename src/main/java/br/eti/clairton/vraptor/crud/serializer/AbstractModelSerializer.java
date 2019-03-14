package br.eti.clairton.vraptor.crud.serializer;

import static br.eti.clairton.inflector.Inflector.getForLocale;
import static br.eti.clairton.inflector.Locale.pt_BR;
import static javax.enterprise.inject.spi.CDI.current;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.persistence.EntityManager;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.jpa.serializer.GsonJpaSerializer;
import br.eti.clairton.jpa.serializer.Nodes;
import br.eti.clairton.jpa.serializer.NodesProgramatic;
import br.eti.clairton.model.Base;
import br.eti.clairton.security.Resource;

public abstract class AbstractModelSerializer<T extends Base> extends GsonJpaSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T>, Resourceable<T> {
	private static final long serialVersionUID = 1L;
	private final br.eti.clairton.jpa.serializer.Tagable<T> tagable;


	public AbstractModelSerializer(final Inflector inflector, final EntityManager em) {
		this(new NodesProgramatic(), inflector, em);
	}
	
	public AbstractModelSerializer(final Nodes nodes, final Inflector inflector, final EntityManager em) {
		super(nodes, em);
		this.tagable = new Tagable<T>(inflector){
			private static final long serialVersionUID = 1L;

			@Override
			public String getRootTag(final T src) {
				return AbstractModelSerializer.this.getRootTag(src);
			}

			@Override
			public String getResource(final T src) {
				return  AbstractModelSerializer.this.getResource(src);
			}

			@Override
			public String getResource(final Collection<T> src) {
				return  AbstractModelSerializer.this.getResource(src);
			}
		};
	}

	public AbstractModelSerializer() {
		this(getForLocale(pt_BR), null);
	}

	@Override
	public String getRootTagCollection(final Collection<T> collection) {
		return tagable.getRootTagCollection(collection);
	}
	
	@Override
	public String getResource(final T src) {
		return current().select(String.class, RQ).get();
	}
	
	@Override
	public String getResource(final Collection<T> src) {
		return current().select(String.class, RQ).get();
	}

	private static final Resource RQ = new Resource() {

		@Override
		public Class<? extends Annotation> annotationType() {
			return Resource.class;
		}

		@Override
		public String value() {
			return "";
		}
	};
}