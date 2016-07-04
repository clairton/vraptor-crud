package br.eti.clairton.vraptor.crud.controller;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.Metamodel;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.Mockito;

import br.eti.clairton.inflector.Inflector;
import br.eti.clairton.inflector.Language;
import br.eti.clairton.inflector.Locale;
import br.eti.clairton.repository.AttributeBuilder;
import br.eti.clairton.security.App;
import br.eti.clairton.security.Gate;
import br.eti.clairton.security.GateInMemory;
import br.eti.clairton.security.Lock;
import br.eti.clairton.security.LockInMemory;
import br.eti.clairton.security.Locksmith;
import br.eti.clairton.security.LocksmithInMemory;
import br.eti.clairton.security.Service;
import br.eti.clairton.security.ServiceInMemory;
import br.eti.clairton.security.Token;
import br.eti.clairton.security.UnauthenticatedException;
import br.eti.clairton.security.User;
import net.vidageek.mirror.dsl.Mirror;

/**
 * Produz os recursos.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@ApplicationScoped
public class Resource {
	public static final String TENANT = "valorQueNãoPodeAparecer";
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
	public String getUser(@Token final String token,
			@Default final Locksmith locksmith) {
		return locksmith.getUserByToken(token);
	}

	@Token
	@Produces
	public String getToken(@Default final HttpServletRequest request) {
		final String header = request.getHeader("Authorization");
		if (null == header) {
			throw new UnauthenticatedException(
					"Header \"Authorization\" must be present");
		}
		return header.replaceAll("Basic ", "");
	}

	@App
	@Produces
	public String getApp() {
		return "test";
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

	@Produces
	@ApplicationScoped
	public Connection getConnection(final @Default EntityManager em) {
		try {
			/*
			 * O hibernate não implementa o entityManager de forma a recuperar a
			 * o connection
			 */
			final Class<?> klass = Class
					.forName("org.hibernate.internal.SessionImpl");
			final Object session = em.unwrap(klass);
			return (Connection) klass.getDeclaredMethod("connection").invoke(
					session);
		} catch (final Exception e) {
			return em.unwrap(Connection.class);
		}
	}

	@Produces
	public Lock getLock() {
		return new LockInMemory();
	}

	@Produces
	public Locksmith getLocksmith(@Default Lock lock) {
		return new LocksmithInMemory(lock);
	}

	@Produces
	public Service getService(@Default Lock lock) {
		return new ServiceInMemory(lock);
	}

	@Produces
	public Gate getGate() {
		final Map<String, Map<String, List<String>>> roles = new HashMap<String, Map<String, List<String>>>() {
			private static final long serialVersionUID = 1L;

			{
				put("Pass", new HashMap<String, List<String>>() {
					private static final long serialVersionUID = 1L;
					{
						put("aplicacao", Arrays.asList("create", "update"));
						put("download", Arrays.asList("get"));
					}
				});
			}
		};
		Map<String, Map<String, Map<String, List<String>>>> authorizations = new HashMap<String, Map<String, Map<String, List<String>>>>();
		authorizations.put("admin", roles);
		return new GateInMemory(authorizations);
	}
}
