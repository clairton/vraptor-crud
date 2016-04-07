package br.eti.clairton.vraptor.crud.controller;

import static br.com.caelum.vraptor.view.Results.http;
import static br.com.caelum.vraptor.view.Results.json;
import static br.eti.clairton.inflector.Inflector.getForLocale;
import static br.eti.clairton.inflector.Locale.pt_BR;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import br.eti.clairton.paginated.collection.Meta;
import br.eti.clairton.paginated.collection.PaginatedCollection;
import br.eti.clairton.repository.Model;
import br.eti.clairton.repository.Order;
import br.eti.clairton.repository.Predicate;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.repository.vraptor.Page;
import br.eti.clairton.repository.vraptor.QueryParser;
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
	private final Logger logger = LogManager.getLogger(CrudController.class);

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
	public CrudController(final @NotNull Class<T> modelType, final @NotNull Repository repository,
			final @NotNull Result result, final @Language @NotNull Inflector inflector,
			final @NotNull ServletRequest request, final @NotNull QueryParser queryParser) {
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
	 * Cria um novo registro do recurso.
	 * 
	 * @param model
	 *            novo registro
	 */
	@Post
	@Protected
	@Authenticated
	@ExceptionVerifier
	@Consumes(value = "application/json", options = WithRoot.class)
	public void create(final T model) {
		logger.debug("Salvando registro");
		createAndSerializeRecord(model);
		result.use(http()).setStatusCode(201);
	}

	/**
	 * Mostra os recursos.<br/>
	 * Parametros para pesquisa são mandados na URL.
	 */
	@Get
	@Protected
	@Authenticated
	@ExceptionVerifier
	public void index() {
		logger.debug("Recuperando registros");
		findAndSerializeRecord();
	}

	/**
	 * Cria um recurso novo.
	 * 
	 * @throws NotInstanceableExeception
	 *             caso não consiga criar uma nova instancia
	 */
	@Get("new")
	@Protected
	@Authenticated
	@Operation("new")
	@ExceptionVerifier
	public void new_() {
		logger.debug("Criando registro");
		instanceAndSerializeRecord();
	}

	/**
	 * Edita um recurso.
	 * 
	 * @param id
	 *            do recurso
	 */
	@Get("{id}/edit")
	@Protected
	@Authenticated
	@ExceptionVerifier
	public void edit(final Long id) {
		logger.debug("Editando registro");
		retrieveAndSerializeRecordToEdit(id);
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
		logger.debug("Mostrando registro");
		retrieveAndSerializeRecordToShow(id);
	}

	/**
	 * Remove um recurso.
	 * 
	 * @param id
	 *            id do recurso
	 */
	@Delete("{id}")
	@Protected
	@Authenticated
	@ExceptionVerifier
	public void remove(final Long id) {
		logger.debug("Removendo registro");
		removeAndSerializeResource(modelType, id);
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
		logger.debug("Atualizando registro");
		updateAndSerializeRecord(model);
	}

	/**
	 * Nome do recurso atual.
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
	 * Atualiza o model no banco de dados e serializa a reposta.
	 */
	protected void updateAndSerializeRecord(final T model) {
		final T response = updateRecord(model);
		serialize(response);
	}

	/**
	 * Cria o model no model de dados e serializa a reposta.
	 */
	protected void createAndSerializeRecord(final T model) {
		final T response = createRecord(model);
		serialize(response);
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
	 * Instancia um novo registro e serializa a reposta.
	 */
	protected void instanceAndSerializeRecord() {
		final T response = instanceRecord();
		serialize(response);
	}

	/**
	 * Salva um registro no banco de dados.
	 * 
	 * @param model
	 *            registro a ser salvo
	 * 
	 * @return registro salvo
	 */
	protected T saveRecord(final T model) {
		return repository.save(model);
	}

	/**
	 * Atualiza um registro no banco de dados.
	 * 
	 * @param model
	 *            registro a ser atualizado
	 * 
	 * @return registro atualizado
	 */
	protected T updateRecord(T model) {
		return saveRecord(model);
	}

	/**
	 * Cria um registro no banco de dados.
	 * 
	 * @param model
	 *            registro a ser criado
	 * 
	 * @return registro criado
	 */
	protected T createRecord(T model) {
		return saveRecord(model);
	}

	/**
	 * Remove um regitrso do banco de dados
	 * 
	 * @param modelType
	 *            tipo do registro
	 * @param id
	 *            identificador do registro
	 */
	protected void removeRecord(final Class<T> modelType, final Long id) {
		repository.remove(modelType, id);
	}

	/**
	 * Cria um novo registro.
	 * 
	 * @return novo registro
	 */
	protected T instanceRecord() {
		try {
			final Constructor<T> constructor = modelType.getConstructor();
			return constructor.newInstance();
		} catch (final Exception e) {
			throw new NotInstanceableExeception(e);
		}
	}

	/**
	 * Remove um registro do banco de dados e serializa reposta.
	 * 
	 * @param model
	 *            registro a ser removido
	 */
	protected void removeAndSerializeResource(final Class<T> modelType, final Long id) {
		removeRecord(modelType, id);
		result.use(http()).setStatusCode(204);
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
	 * Recupera um registro para editar do banco de dados e serializa.
	 * 
	 * @param id
	 *            identificador do registro
	 */
	protected void retrieveAndSerializeRecordToEdit(final Long id) {
		final T response = retrieveRecordToEdit(id);
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
	protected T retrieveRecordToEdit(final Long id) {
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
		return queryParser.parse(request, modelType);
	}

	/**
	 * Analiza os dados da requisição e os transforma nos dados de paginação.
	 * 
	 * @return dados de paginação
	 */
	protected Page paginate() {
		return queryParser.paginate(request, modelType);
	}

	/**
	 * Analisa os dados da requisição e recupera a ordem.
	 * 
	 * @return ordem
	 */
	protected List<Order> orders() {
		return queryParser.order(request, modelType);
	}
}