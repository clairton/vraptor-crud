package br.eti.clairton.vraptor.crud.controller;

import static br.com.caelum.vraptor.view.Results.http;
import static br.eti.clairton.vraptor.hypermedia.HypermediaJsonSerialization.jsonHypermedia;

import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.validation.constraints.NotNull;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.gson.WithRoot;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Language;
import br.eti.clairton.repository.Model;
import br.eti.clairton.repository.Predicate;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.security.Authenticated;
import br.eti.clairton.security.Protected;
import br.eti.clairton.security.Resource;
import br.eti.clairton.vraptor.crud.interceptor.ExceptionVerifier;

/**
 * Controller abstrato para servir como base para um CRUD.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 *
 * @param <T>
 *            tipo do modelo
 */
public abstract class CrudController<T extends Model> {
	private final Repository repository;

	private final Class<T> modelType;

	private final Result result;

	private final Inflector inflector;

	private final Mirror mirror;

	private final ServletRequest request;

	private final QueryParser queryParser;

	private final String resourceName;

	/**
	 * Construtor Padrão.
	 * 
	 * @param modelType
	 *            tipo do modelo
	 * @param repository
	 *            instancia do repository
	 * @param result
	 *            instancia de result
	 * @param inflector
	 *            instancia de inflector
	 * @param mirror
	 *            instancia de mirror
	 * @param request
	 *            instancia de request
	 * @param queryParser
	 *            instancia de quey parser
	 */
	public CrudController(final @NotNull Class<T> modelType,
			final @NotNull Repository repository, final @NotNull Result result,
			@Language final @NotNull Inflector inflector,
			final @NotNull Mirror mirror,
			final @NotNull ServletRequest request,
			final @NotNull QueryParser queryParser) {
		this.repository = repository;
		this.result = result;
		this.modelType = modelType;
		this.inflector = inflector;
		this.mirror = mirror;
		this.request = request;
		this.queryParser = queryParser;
		if (modelType != null) {
			final StringBuilder builder = new StringBuilder();
			final String simpleName = modelType.getSimpleName();
			builder.append(simpleName.substring(0, 1).toLowerCase());
			builder.append(simpleName.substring(1));
			this.resourceName = builder.toString();
		} else {
			resourceName = null;
		}
	}

	/**
	 * Cria um novo registro do recurso.
	 * 
	 * @param model
	 *            novo registro
	 */
	@Consumes(value = "application/json", options=WithRoot.class)
	@Post
	@Protected
	@Authenticated
	@ExceptionVerifier
	public void create(final T model) {
		final T response = repository.save(model);
		serialize(response);
	}

	/**
	 * Mostra os recursos. Parametros para filtagem são mandados na URL.
	 */
	@Get
	@Protected
	@Authenticated
	@ExceptionVerifier
	public void index() {
		final Map<String, String[]> params = request.getParameterMap();
		final Integer page;
		final Integer perPage;
		if (params != null && params.containsKey(Param.PAGE)
				&& params.containsKey(Param.PER_PAGE)) {
			page = Integer.valueOf(params.get(Param.PAGE)[0]);
			perPage = Integer.valueOf(params.get(Param.PER_PAGE)[0]);
		} else {
			page = 0;
			perPage = 0;
		}
		final Collection<Predicate> predicates = queryParser.parse(request,
				modelType);
		repository.from(modelType);
		repository.distinct();
		if (!predicates.isEmpty()) {
			repository.where(predicates);
		}
		final Collection<T> collection = repository.collection(page, perPage);
		serialize(collection);
	}

	/**
	 * Mostra um recurso.
	 * 
	 * @param id
	 *            id do recurso
	 */
	@Get("{id}")
	@Protected
	@Authenticated
	@ExceptionVerifier
	public void show(final Long id) {
		final T response = repository.byId(modelType, id);
		serialize(response);
	}

	/**
	 * Deleta um recurso.
	 * 
	 * @param id
	 *            id do recurso
	 */
	@Delete("{id}")
	@Protected
	@Authenticated
	@ExceptionVerifier
	public void remove(final Long id) {
		repository.remove(modelType, id);
		result.use(http()).setStatusCode(200);
	}

	/**
	 * Atualiza um recurso.
	 * 
	 * @param id
	 *            id do recurso
	 * @param model
	 *            recurso a ser atualizado
	 */
	@Consumes(value = "application/json", options=WithRoot.class)
	@Put("{id}")
	@Protected
	@Authenticated
	@ExceptionVerifier
	public void update(final Long id, final T model) {
		mirror.on(model).set().field("id").withValue(id);
		final T response = repository.save(model);
		serialize(response);
	}

	/**
	 * serialize {@inheritDoc}
	 */
	@Resource
	@Ignore
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * Serializa um model.
	 * 
	 * @param model
	 *            model a ser serilizado
	 */
	protected void serialize(final T model) {
		final Serializer serializer = result.use(jsonHypermedia()).from(model);
		serialize(serializer);
	}

	protected void serialize(final Serializer serializer) {
		serializer.serialize();
	}

	protected void serialize(final Collection<T> collection) {
		final String plural = inflector.pluralize(modelType.getSimpleName());
		final String tag = inflector.uncapitalize(plural);
		serialize(result.use(jsonHypermedia()).from(collection, tag));
	}
}