package br.eti.clairton.vraptor.crud.controller;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.enterprise.inject.Instance;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.environment.DefaultEnvironment;
import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.environment.EnvironmentType;
import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.serialization.Serializee;
import br.com.caelum.vraptor.serialization.gson.GsonSerializerBuilder;
import br.com.caelum.vraptor.serialization.xstream.XStreamBuilderImpl;
import br.com.caelum.vraptor.util.test.MockHttpServletResponse;
import br.com.caelum.vraptor.util.test.MockInstanceImpl;
import br.com.caelum.vraptor.util.test.MockSerializationResult;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Locale;
import br.eti.clairton.jpa.serializer.Tagable;
import br.eti.clairton.paginated.collection.Meta;
import br.eti.clairton.paginated.collection.PaginatedCollection;
import br.eti.clairton.paginated.collection.PaginatedMetaList;
import br.eti.clairton.repository.Model;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.repository.vraptor.Page;
import br.eti.clairton.repository.vraptor.QueryParser;
import br.eti.clairton.vraptor.crud.GsonBuilderWrapper;
import br.eti.clairton.vraptor.crud.GsonJSONSerialization;
import br.eti.clairton.vraptor.crud.model.Aplicacao;
import br.eti.clairton.vraptor.crud.serializer.DefaultTagableExtrator;
import br.eti.clairton.vraptor.crud.serializer.ModelSerializer;
import br.eti.clairton.vraptor.crud.serializer.TagableExtractor;
import net.vidageek.mirror.dsl.Mirror;

public class CrudControllerTest {
	private final Instance<JsonSerializer<?>> jsonSerializers = new MockInstanceImpl<>(new ArrayList<JsonSerializer<?>>());
	private final Instance<JsonDeserializer<?>> jsonDeserializers = new MockInstanceImpl<>(new ArrayList<JsonDeserializer<?>>());

	private final Inflector inflector = Inflector.getForLocale(Locale.pt_BR);

	private final JsonSerializer<Model> serializer = new ModelSerializer(inflector, Mockito.mock(EntityManager.class)){
		private static final long serialVersionUID = 1L;

		@Override
		public String getRootTag(final Model src) {
			return tag;
		}
	};
	private String nome = "Nome da Aplicação Número: " +  new Date().getTime();
	private Repository repository = new Repository(null, null, null, null){
		private static final long serialVersionUID = 1L;
		private final Aplicacao aplicacao = new Aplicacao(nome);
		private final PaginatedCollection<Model, Meta> collection = new PaginatedMetaList<>(Arrays.asList(aplicacao), new Meta(1l, 100l));
		
		@SuppressWarnings("unchecked")
		public <T extends Model, Y> T byId(@NotNull final Class<T> klass, @NotNull final Y id) throws NoResultException {
			return (T) aplicacao;
		}
		
		@SuppressWarnings("unchecked")
		public <T extends Model> br.eti.clairton.paginated.collection.PaginatedCollection<T,Meta> collection(Integer page, Integer perPage) {
			return (PaginatedCollection<T, Meta>) collection;
		};
		
		public <T extends Model> Repository from(java.lang.Class<T> type) {
			return this;
		};
		
		public Repository distinct() {
			return this;
		};
	};
	
	private CrudController<Aplicacao> controller;

	private MockSerializationResult result;

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;
	
	private String tag = "xpto";

	private Long id = 1001l;

	@Before
	public void init() throws Exception{
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		final GsonSerializerBuilder builder = new GsonBuilderWrapper(jsonSerializers,  jsonDeserializers, new Serializee()){
			@Override
			public Gson create() {
				getGsonBuilder().registerTypeAdapter(Aplicacao.class, serializer);
				return getGsonBuilder().create();
			}
		};
		final Environment environment = new DefaultEnvironment(EnvironmentType.TEST);
		final TypeNameExtractor extractor = new DefaultTypeNameExtractor();
		final TagableExtractor tagableExtractor = new DefaultTagableExtrator(new MockInstanceImpl<>(new ArrayList<Tagable<?>>())){
			@Override
			public String extract(final Object object) {
				if(Collection.class.isInstance(object)){
					return inflector.pluralize(tag);
				}
				return tag;
			}
		};
		final GsonJSONSerialization serialization = new GsonJSONSerialization(response, extractor, builder, environment, tagableExtractor);
		result = new MockSerializationResult(new JavassistProxifier(), XStreamBuilderImpl.cleanInstance(), builder, environment){
			
			@Override
			public <T extends View> T use(final Class<T> view) {
				return view.cast(serialization);
			}
		};
		new Mirror().on(result).set().field("response").withValue(response);
		controller = new AplicacaoController(repository, result, inflector, request, Mockito.mock(QueryParser.class), response){
			@Override
			protected Page paginate() {
				return new Page(1, 100);
			}
		};
	}
	
	@Test
	public void testSingle() throws Exception {
		controller.edit(id);
		assertEquals("{\"xpto\":{\"recursos\":[],\"nome\":\""+nome+"\"}}", result.serializedResult());
	}
	
	@Test
	public void testCollection() throws Exception {
		controller.index();
		assertEquals("{\"xptos\":[{\"recursos\":[],\"nome\":\""+nome+"\"}]}", result.serializedResult());
	}
}
