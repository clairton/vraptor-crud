package br.eti.clairton.vraptor.crud;

import static br.com.caelum.vraptor.view.Results.http;
import static br.com.caelum.vraptor.view.Results.json;

import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletRequest;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.serialization.Serializer;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Language;
import br.eti.clairton.repository.Model;
import br.eti.clairton.repository.Predicate;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.vraptor.crud.security.Authenticated;
import br.eti.clairton.vraptor.crud.security.Authorized;
import br.eti.clairton.vraptor.crud.security.Resourceable;

/**
 * Controller abstrato para servir como base para um crud.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 *
 * @param <T>
 *            tipo do modelo
 */
public abstract class CrudController<T extends Model> extends Resourceable {
	private final Repository repository;

	private final Class<T> modelType;

	private final Result result;

	private final Inflector inflector;

	private final Mirror mirror;

	private final ServletRequest request;

	private final QueryParamParser queryParser;

	/**
	 * CDI only.
	 */
	@Deprecated
	protected CrudController() {
		this(null, null, null, null, null, null, null);
	}

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
	public CrudController(final Class<T> modelType,
			final Repository repository, final Result result,
			@Language final Inflector inflector, final Mirror mirror,
			final ServletRequest request, final QueryParamParser queryParser) {
		super(modelType);
		this.repository = repository;
		this.result = result;
		this.modelType = modelType;
		this.inflector = inflector;
		this.mirror = mirror;
		this.request = request;
		this.queryParser = queryParser;
	}

	/**
	 * Cria um novo registro do recurso.
	 * 
	 * @param model
	 *            novo registro
	 */
	@Consumes(value = "application/json")
	@Post
	@Authorized
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
	@Authorized
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
		if (!predicates.isEmpty()) {
			repository.where(predicates);
		}
		final Collection<?> collection = repository.collection(page, perPage);
		final String plural = inflector.pluralize(modelType.getSimpleName());
		final String tag = inflector.uncapitalize(plural);
		serialize(result.use(json()).from(collection, tag));
	}

	/**
	 * Mostra um recurso.
	 * 
	 * @param id
	 *            id do recurso
	 */
	@Get("{id}")
	@Authorized
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
	@Authorized
	@Authenticated
	@ExceptionVerifier
	public void delete(final Long id) {
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
	@Consumes(value = "application/json")
	@Put("{id}")
	@Authorized
	@Authenticated
	@ExceptionVerifier
	public void update(final Long id, final T model) {
		mirror.on(model).set().field("id").withValue(id);
		final T response = repository.save(model);
		serialize(response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Ignore
	public String getResourceName() {
		return super.getResourceName();
	}

	/**
	 * Serializa um model.
	 * 
	 * @param model
	 *            model a ser serilizado
	 */
	protected void serialize(final T model) {
		final Serializer serializer = result.use(json()).from(model);
		serialize(serializer);
	}

	private void serialize(final Serializer serializer) {
		serializer.serialize();
	}
}