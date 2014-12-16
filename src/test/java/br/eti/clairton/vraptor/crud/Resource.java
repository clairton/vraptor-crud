package br.eti.clairton.vraptor.crud;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.Metamodel;

import org.mockito.Mockito;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.cache.LRU;
import br.com.caelum.vraptor.cache.LRUCacheStore;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Language;
import br.eti.clairton.inflector.Locale;
import br.eti.clairton.repository.AttributeBuilder;

/**
 * Produz os recursos.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@ApplicationScoped
public class Resource {
	public static final String UNIT_NAME = "default";

	public static final String ENVIROMENT_PARAM = "br.com.caelum.vraptor.environment";

	private final Mirror mirror = new Mirror();

	private EntityManagerFactory emf;

	private EntityManager em;

	private AttributeBuilder attributeBuilder;

	@PostConstruct
	public void init() {
		emf = Persistence.createEntityManagerFactory(UNIT_NAME);
		em = emf.createEntityManager();
		attributeBuilder = new AttributeBuilder(em);
	}

	@Produces
	public EntityManager getEm() {
		return em;
	}

	@Produces
	public Metamodel getMetamodel() {
		return em.getMetamodel();
	}

	@Produces
	public Cache getCache() {
		return Mockito.mock(Cache.class);
	}

	@Produces
	public Logger produceLogger(final InjectionPoint injectionPoint) {
		final Class<?> type = injectionPoint.getMember().getDeclaringClass();
		final String klass = type.getName();
		return Logger.getLogger(klass);
	}

	@Produces
	public Mirror getMirror() {
		return mirror;
	}

	/**
	 * dependencia que o weld reclamava nos testes.
	 */
	@LRU
	@Produces
	public CacheStore<String, String> getCache(final InjectionPoint ip) {
		final LRU annotation = ip.getAnnotated().getAnnotation(LRU.class);
		final int capacity = annotation.capacity();
		return new LRUCacheStore<>(capacity);
	}

	@Produces
	public Inflector getForLanguage(final InjectionPoint ip) {
		final String language;
		if (ip.getAnnotated().isAnnotationPresent(Language.class)) {
			language = ip.getAnnotated().getAnnotation(Language.class).value();
		} else {
			language = Locale.pt_BR;
		}
		final Inflector inflector = Inflector.getForLocale(language);
		return inflector;
	}

	@Produces
	public AttributeBuilder getAttributeBuilder() {
		return attributeBuilder;
	}
}
