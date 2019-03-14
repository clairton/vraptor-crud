package br.eti.clairton.vraptor.crud.controller;

import static br.com.caelum.vraptor.view.Results.json;
import static br.eti.clairton.inflector.Inflector.getForLocale;
import static br.eti.clairton.inflector.Locale.pt_BR;
import static java.util.logging.Level.FINE;
import static java.util.logging.Logger.getLogger;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletRequest;
import javax.validation.constraints.NotNull;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.serialization.Serializer;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Language;
import br.eti.clairton.paginated.collection.Meta;
import br.eti.clairton.paginated.collection.PaginatedCollection;
import br.eti.clairton.repository.Order;
import br.eti.clairton.repository.Predicate;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.repository.http.Page;
import br.eti.clairton.repository.http.QueryParser;
import br.eti.clairton.security.Authenticated;
import br.eti.clairton.security.Protected;
import br.eti.clairton.security.Resource;
import br.eti.clairton.vraptor.crud.interceptor.ExceptionVerifier;

/**
 * Controller abstrato para servir como base para um CRUD.
 * 
 * @author Clairton Rodrigo Heinzen clairton.rodrigo@gmail.com
 *
 * @param <T>
 *            tipo do modelo
 */
public abstract class RetrieveController<T> {
	private final Logger logger = getLogger(RetrieveController.class.getSimpleName());

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
	 * @param request
	 *            instancia de request
	 * @param queryParser
	 *            instancia de quey parser
	 */
	public RetrieveController(
			final @NotNull Class<T> modelType, 
			final @NotNull Repository repository,
			final @NotNull Result result, 
			final @Language @NotNull Inflector inflector,
			final @NotNull ServletRequest request, 
			final @NotNull QueryParser queryParser) {
		this.repository = repository;
		this.result = result;
		this.modelType = modelType;
		//TODO why?
		this.inflector = inflector != null ? inflector : getForLocale(pt_BR);
		this.request = request;
		this.queryParser = queryParser;
		this.resourceName = resourceName();
	}
	/**
	 * Mostra os recursos.
	 * Parametros para pesquisa são mandados na URL.
	 */
	@Get
	@Protected
	@Authenticated
	@ExceptionVerifier
	public void index() {
		logger.log(FINE, "Recuperando registros");
		findAndSerializeRecord();
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
		logger.log(FINE, "Mostrando registro");
		retrieveAndSerializeRecordToShow(id);
	}

	/**
	 * @return Nome do recurso atual
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
		serializer.serialize();
	}

	/**
	 * Serializa um coleção.
	 * 
	 * @param collection
	 *            coleção a ser serializada
	 */
	protected void serialize(final PaginatedCollection<T, Meta> collection) {
		result.use(json()).from(collection).serialize();
	}

	/**
	 * Recupera o nome do recurso,
	 * 
	 * @return String
	 */
	protected String resourceName() {
		if (modelType != null) {
			final String simpleName = modelType.getSimpleName();
			final String resource = inflector.uncapitalize(simpleName);
			return resource;
		} else {
			return null;
		}
	}

	/**
	 * Busca pelos registros no banco de dados aplicando filtro e paginação.
	 * 
	 * @return {@link PaginatedCollection}
	 */
	@Ignore
	public PaginatedCollection<T, Meta> find() {
		final Page paginate = paginate();
		final Collection<Predicate> predicates = predicates();
		repository.from(modelType);
		repository.distinct();
		if (!predicates.isEmpty()) {
			repository.where(predicates);
		}
		final List<Order> orders = orders();
		repository.orderBy(orders);
		return repository.collection(paginate.offset, paginate.limit);
	}

	/**
	 * Busca pelos registros aplicandos filtros e paginação, depois serializa a
	 * reposta.
	 */
	protected void findAndSerializeRecord() {
		final PaginatedCollection<T, Meta> collection = find();
		serialize(collection);
	}

	/**
	 * Recupera um registro do banco de dados e serializa.
	 * 
	 * @param id
	 *            identificador do registro
	 */
	protected void retrieveAndSerializeRecord(final Long id) {
		final T response = retrieveRecord(id);
		serialize(response);
	}
	
	/**
	 * Recupera um registro para mostrar do banco de dados e serializa.
	 * 
	 * @param id
	 *            identificador do registro
	 */
	protected void retrieveAndSerializeRecordToShow(final Long id) {
		final T response = retrieveRecordToShow(id);
		serialize(response);
	}

	/**
	 * Recupera um registro do banco de dados para mostrar.
	 * 
	 * @param id
	 *            identificador do registro
	 * @return registro recuperado
	 */
	protected T retrieveRecordToShow(final Long id) {
		return retrieveRecord(id);
	}
	
	/**
	 * Recupera um registro do banco de dados.
	 * 
	 * @param id
	 *            identificador do registro
	 * @return registro recuperado
	 */
	protected T retrieveRecord(final Long id) {
		return repository.byId(modelType, id);
	}

	/**
	 * Analiza os dados da requisição e os transforma em uma coleção de
	 * Predicados.
	 * 
	 * @return coleção de predicados
	 */
	protected Collection<Predicate> predicates() {
		return queryParser.parse(request.getParameterMap(), modelType);
	}

	/**
	 * Analiza os dados da requisição e os transforma nos dados de paginação.
	 * 
	 * @return dados de paginação
	 */
	protected Page paginate() {
		return queryParser.paginate(request.getParameterMap(), modelType);
	}

	/**
	 * Analisa os dados da requisição e recupera a ordem.
	 * 
	 * @return ordem
	 */
	protected List<Order> orders() {
		return queryParser.order(request.getParameterMap(), modelType);
	}
}