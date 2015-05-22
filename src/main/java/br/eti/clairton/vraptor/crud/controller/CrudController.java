package br.eti.clairton.vraptor.crud.controller;

import static br.com.caelum.vraptor.view.Results.http;
import static br.com.caelum.vraptor.view.Results.json;
import static br.eti.clairton.vraptor.crud.controller.Param.PAGE;
import static br.eti.clairton.vraptor.crud.controller.Param.PER_PAGE;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.validation.constraints.NotNull;

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
import br.eti.clairton.security.Operation;
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
			final @NotNull Repository repository, 
			final @NotNull Result result,
			final @Language @NotNull Inflector inflector,
			final @NotNull ServletRequest request,
			final @NotNull QueryParser queryParser) {
		this.repository = repository;
		this.result = result;
		this.modelType = modelType;
		this.inflector = inflector;
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
	@Consumes(value = "application/json", options = WithRoot.class)
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
		final Page paginate = paginate();
		final Integer page = paginate.offset;
		final Integer perPage = paginate.limit;
		final Collection<Predicate> predicates = getPredicates();
		repository.from(modelType);
		repository.distinct();
		if (!predicates.isEmpty()) {
			repository.where(predicates);
		}
		final Collection<T> collection = repository.collection(page, perPage);
		serialize(collection);
	}

	/**
	 * Cria um recurso novo.
	 * 
	 * @throws NotNewableExeception
	 *             caso não consiga criar uma nova instancia
	 */
	@Get("new")
	@Protected
	@Authenticated
	@ExceptionVerifier
	@Operation("new")
	public void new_() {
		final T response;
		try {
			final Constructor<T> constructor = modelType.getConstructor();
			response = constructor.newInstance();
		} catch (final Exception e) {
			throw new NotNewableExeception(e);
		}
		serialize(response);
	}

	@Get("{id}/edit")
	@Protected
	@Authenticated
	@ExceptionVerifier
	public void edit(final Long id) {
		retrieve(id);
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
		retrieve(id);
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
	@Consumes(value = "application/json", options = WithRoot.class)
	@Put("{model.id}")
	@Protected
	@Authenticated
	@ExceptionVerifier
	public void update(final T model) {
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
	 *            model a ser seriliazado
	 */
	protected void serialize(final T model) {
		final Serializer serializer = result.use(json()).from(model);
		serialize(serializer);
	}

	protected void serialize(final Serializer serializer) {
		serializer.serialize();
	}

	protected void serialize(final Collection<T> collection) {
		final String plural = inflector.pluralize(modelType.getSimpleName());
		final String tag = inflector.uncapitalize(plural);
		serialize(result.use(json()).from(collection, tag));
	}

	private void retrieve(final Long id) {
		final T response = repository.byId(modelType, id);
		serialize(response);
	}

	private Collection<Predicate> getPredicates() {
		return queryParser.parse(request, modelType);
	}

	private Page paginate() {
		final Map<String, String[]> params;
		if (request.getParameterMap() != null) {
			params = request.getParameterMap();
		} else {
			params = new HashMap<>();
		}
		final Integer page;
		final Integer perPage;
		if (params.containsKey(PAGE) && params.containsKey(PER_PAGE)) {
			page = Integer.valueOf(params.get(PAGE)[0]);
			perPage = Integer.valueOf(params.get(PER_PAGE)[0]);
		} else {
			page = 0;
			perPage = 0;
		}
		return new Page(page, perPage);
	}

	private static class Page {
		public final Integer offset;
		public final Integer limit;

		public Page(final Integer offet, final Integer limit) {
			super();
			this.offset = offet;
			this.limit = limit;
		}
	}
}