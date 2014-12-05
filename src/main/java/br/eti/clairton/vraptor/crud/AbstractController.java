package br.eti.clairton.vraptor.crud;

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
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Language;
import br.eti.clairton.repository.Model;
import br.eti.clairton.repository.Predicate;
import br.eti.clairton.repository.Repository;

public abstract class AbstractController<T extends Model> {
	private final Repository repository;

	private final Class<T> modelType;

	private final Result result;

	private final Inflector inflector;

	private final Mirror mirror;

	private final ServletRequest request;

	private final QueryParamParser queryParser;

	@Deprecated
	public AbstractController() {
		this(null, null, null, null, null, null, null);
	}

	public AbstractController(final Class<T> modelType,
			final Repository repository, final Result result,
			@Language final Inflector inflector, final Mirror mirror,
			final ServletRequest request, final QueryParamParser queryParser) {
		super();
		this.repository = repository;
		this.result = result;
		this.modelType = modelType;
		this.inflector = inflector;
		this.mirror = mirror;
		this.request = request;
		this.queryParser = queryParser;
	}

	@Consumes(value = "application/json")
	public @ExceptionVerifier @Post void create(final T model) {
		final T response = repository.save(model);
		result.use(json()).from(response).serialize();
	}

	// @Consumes(value = "application/json")
	public @ExceptionVerifier @Get void index() {
		final Map<String, String[]> params = request.getParameterMap();
		final Integer page;
		final Integer perPage;
		if (params != null && params.containsKey(Param.PAGE)
				&& params.containsKey(Param.PER_PAGE)) {
			page = Integer.valueOf(params.get(Param.PAGE)[0]);
			perPage = Integer.valueOf(params.get(Param.PER_PAGE)[0]);
		} else {
			page = -1;
			perPage = -1;
		}
		final Collection<Predicate> predicates = queryParser.parse(request,
				modelType);
		final Collection<?> collection = repository.from(modelType)
				.where(predicates).collection(page, perPage);
		final String plural = inflector.pluralize(modelType.getSimpleName());
		final String tag = inflector.uncapitalize(plural);
		result.use(json()).from(collection, tag).serialize();
	}

	@Consumes(value = "application/json")
	public @ExceptionVerifier @Get("{id}") void show(final Long id) {
		final T response = repository.byId(modelType, id);
		result.use(json()).from(response).serialize();
	}

	@Consumes(value = "application/json")
	public @ExceptionVerifier @Delete("{id}") void delete(final Long id) {
		final T response = repository.byId(modelType, id);
		repository.remove(response);
	}

	@Consumes(value = "application/json")
	public @ExceptionVerifier @Put("{id}") void update(final Long id,
			final T model) {
		mirror.on(model).set().field("id").withValue(id);
		final T response = repository.save(model);
		result.use(json()).from(response).serialize();
	}
}