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
import br.eti.clairton.model.Model;
import br.eti.clairton.paginated.collection.Meta;
import br.eti.clairton.paginated.collection.PaginatedCollection;
import br.eti.clairton.paginated.collection.PaginatedMetaList;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.repository.http.Page;
import br.eti.clairton.repository.http.QueryParser;
import br.eti.clairton.vraptor.crud.GsonBuilderWrapper;
import br.eti.clairton.vraptor.crud.GsonJSONSerialization;
import br.eti.clairton.vraptor.crud.model.Aplicacao;
import br.eti.clairton.vraptor.crud.model.Recurso;
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
		{
			record("recursos");
		}

		@Override
		public String getRootTag(final Model src) {
			return tag;
		}
		
		@Override
		public String getResource(final Model src) {
			return "coracao";
		};
		
		@Override
		public String getResource(final Collection<Model> src) {
			return "coracao";
		};
	};

	private final JsonSerializer<Model> serializerRecurso = new ModelSerializer(inflector, Mockito.mock(EntityManager.class)){
		private static final long serialVersionUID = 1L;

		@Override
		public String getRootTag(final Model src) {
			return "recurso";
		}
		
		public String getRootTagCollection(java.util.Collection<Model> collection) {
			return "recursos";
		};
		
		@Override
		public String getResource(final Model src) {
			return "recurso";
		};
		
		@Override
		public String getResource(final Collection<Model> src) {
			return "recurso";
		};
	};
	private final String nome = "Nome da Aplicação Número: " +  new Date().getTime();
	private final Repository repository = new Repository(null){
		private static final long serialVersionUID = 1L;
		private final Recurso recurso = new Recurso("abc");
		private final Aplicacao aplicacao = new Aplicacao(nome, recurso);
		private final PaginatedCollection<Model, Meta> collection = new PaginatedMetaList<>(Arrays.asList(aplicacao), new Meta(1l, 100l));
		
		@SuppressWarnings("unchecked")
		public <T, Y> T byId(@NotNull final Class<T> klass, @NotNull final Y id) throws NoResultException {
			return (T) aplicacao;
		}
		
		@SuppressWarnings("unchecked")
		public <T> br.eti.clairton.paginated.collection.PaginatedCollection<T,Meta> collection(Integer page, Integer perPage) {
			return (PaginatedCollection<T, Meta>) collection;
		};
		
		public <T> Repository from(java.lang.Class<T> type) {
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
				getGsonBuilder().registerTypeAdapter(Recurso.class, serializerRecurso);
				return getGsonBuilder().create();
			}
		};
		final Environment environment = new DefaultEnvironment(EnvironmentType.TEST);
		final TypeNameExtractor extractor = new DefaultTypeNameExtractor();
		final TagableExtractor tagableExtractor = new DefaultTagableExtrator(new MockInstanceImpl<>(new ArrayList<Tagable<?>>())){
			@Override
			@SuppressWarnings("unchecked")
			protected Tagable<Object> getTagable(final Object object) {
				return (Tagable<Object>) serializer;
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
		assertEquals("{\"xpto\":{\"recursos\":[{\"nome\":\"abc\"}],\"nome\":\""+nome+"\"}}", result.serializedResult());
	}
	
	@Test
	public void testCollection() throws Exception {
		controller.index();
		assertEquals("{\"xptos\":[{\"recursos\":[{\"nome\":\"abc\"}],\"nome\":\""+nome+"\"}]}", result.serializedResult());
	}
	
	@Test
	public void testEmptyCollection() throws Exception {
		final Repository repository = new Repository(null){
			private static final long serialVersionUID = 1L;
			private final PaginatedCollection<Model, Meta> collection = new PaginatedMetaList<>(new ArrayList<>(), new Meta(1l, 100l));
		
			
			@SuppressWarnings("unchecked")
			public <T> br.eti.clairton.paginated.collection.PaginatedCollection<T,Meta> collection(Integer page, Integer perPage) {
				return (PaginatedCollection<T, Meta>) collection;
			};
			
			public <T> Repository from(java.lang.Class<T> type) {
				return this;
			};
			
			public Repository distinct() {
				return this;
			};
		};

		controller = new AplicacaoController(repository, result, inflector, request, Mockito.mock(QueryParser.class), response){
			@Override
			protected Page paginate() {
				return new Page(1, 100);
			}
		};
		controller.index();
		assertEquals("{\"coracoes\":[]}", result.serializedResult());
	}
}
