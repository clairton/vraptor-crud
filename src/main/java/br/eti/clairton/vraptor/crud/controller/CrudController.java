package br.eti.clairton.vraptor.crud.controller;

import static br.com.caelum.vraptor.view.Results.http;
import static org.apache.logging.log4j.LogManager.getLogger;

import java.lang.reflect.Constructor;

import javax.servlet.ServletRequest;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.Logger;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Patch;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.serialization.gson.WithRoot;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Language;
import br.eti.clairton.model.Base;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.repository.http.QueryParser;
import br.eti.clairton.security.Authenticated;
import br.eti.clairton.security.Operation;
import br.eti.clairton.security.Protected;
import br.eti.clairton.vraptor.crud.interceptor.ExceptionVerifier;

/**
 * Controller abstrato para servir como base para um CRUD.
 * 
 * @author Clairton Rodrigo Heinzen clairton.rodrigo@gmail.com
 *
 * @param <T>
 *            tipo do modelo
 */
public abstract class CrudController<T extends Base> extends RetrieveController<T>{
	private final Logger logger = getLogger(CrudController.class);

	private final Repository repository;

	private final Class<T> modelType;

	private final Result result;

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
	public CrudController(
			final @NotNull Class<T> modelType, 
			final @NotNull Repository repository,
			final @NotNull Result result, 
			final @Language @NotNull Inflector inflector,
			final @NotNull ServletRequest request, 
			final @NotNull QueryParser queryParser) {
		super(modelType, repository, result, inflector, request, queryParser);
		this.repository = repository;
		this.result = result;
		this.modelType = modelType;
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
	@Put("{id}")
	@Patch("{id}")
	@Protected
	@Authenticated
	@ExceptionVerifier
	public void update(final T model, Long id) {
		logger.debug("Atualizando registro");
		updateAndSerializeRecord(model);
	}

	/**
	 * Atualiza o model no banco de dados e serializa a reposta.
	 *
	 * @param model
	 *            recurso a ser atualizado e serializado
	 */
	protected void updateAndSerializeRecord(final T model) {
		final T response = updateRecord(model);
		serialize(response);
	}

	/**
	 * Cria o model no model de dados e serializa a reposta.
	 *
	 * @param model
	 *            recurso a ser criado e serializado
	 */
	protected void createAndSerializeRecord(final T model) {
		final T response = createRecord(model);
		serialize(response);
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
	 * @param modelType
	 *            tipo do registro a ser removido
	 * 
	 * @param id
	 *            id do registro a ser removido
	 */
	protected void removeAndSerializeResource(final Class<T> modelType, final Long id) {
		removeRecord(modelType, id);
		result.use(http()).setStatusCode(204);
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
	 * Recupera um registro do banco de dados.
	 * 
	 * @param id
	 *            identificador do registro
	 * @return registro recuperado
	 */
	protected T retrieveRecordToEdit(final Long id) {
		return retrieveRecord(id);
	}
}