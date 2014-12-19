package br.eti.clairton.vraptor.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.Metamodel;

import net.vidageek.mirror.dsl.Mirror;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.Mockito;

import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.cache.LRU;
import br.com.caelum.vraptor.cache.LRUCacheStore;
import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Language;
import br.eti.clairton.inflector.Locale;
import br.eti.clairton.repository.AttributeBuilder;
import br.eti.clairton.vraptor.crud.security.App;
import br.eti.clairton.vraptor.crud.security.User;

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
		return LogManager.getLogger(klass);
	}

	@Produces
	public Mirror getMirror() {
		return mirror;
	}

	@User
	@Produces
	public String getUser() {
		return "jose";
	}

	@App
	@Produces
	public String getApp() {
		return "test";
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
