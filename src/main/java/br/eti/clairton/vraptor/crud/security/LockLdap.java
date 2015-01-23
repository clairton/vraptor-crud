package br.eti.clairton.vraptor.crud.security;

import java.util.Hashtable;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.Logger;

import br.com.caelum.vraptor.environment.Property;

/**
 * Autentica pelo LDAP.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Dependent
public class LockLdap implements Lock {
	private final Hashtable<String, String> config = new Hashtable<String, String>();
	private final String[] ldapAttributes;
	private final String base;
	private final String filter;
	private final InitialDirContext context;
	private final Logger logger;

	/**
	 * Construtor padrão.
	 * 
	 * @param url
	 *            url de conexão com o LDAP
	 * @param factory
	 *            context factory para o ldap
	 * @param security
	 *            tipo do algoritmo de seguranca
	 * @param user
	 *            usuario administrador que ira conectar na base LDAP
	 * @param password
	 *            senha usuario administrador que ira conectar na base LDAP
	 * @param attributes
	 *            atributos que devem ser retornados na pesquisa pelo usuario
	 * @param base
	 *            base para pesquisar pelo usuario
	 * @param filter
	 *            filtro que deve ser usado para a pesquisa do usuario
	 * 
	 */
	@Inject
	public LockLdap(final Logger logger,
			final @Property(value = "ldap.url") String url,
			@Property(value = "ldap.factory") final String factory,
			@Property(value = "ldap.security") final String security,
			@Property(value = "ldap.user") final String user,
			@Property(value = "ldap.password") final String password,
			@Property(value = "ldap.attributes") final String attributes,
			@Property(value = "ldap.base") final String base,
			@Property(value = "ldap.filter") final String filter) {
		this.ldapAttributes = attributes.split(",");
		this.base = base;
		this.filter = filter;
		this.logger = logger;
		config.put(Context.INITIAL_CONTEXT_FACTORY, factory);
		config.put(Context.PROVIDER_URL, url);
		config.put(Context.SECURITY_AUTHENTICATION, security);
		config.put(Context.SECURITY_PRINCIPAL, user);
		config.put(Context.SECURITY_CREDENTIALS, password);
		try {
			context = new InitialDirContext(config);
			logger.debug("Conectado em {} como {}", url, user);
		} catch (final NamingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Boolean isValid(@NotNull final String user,
			@NotNull final String password) {
		try {
			final SearchControls ctrls = new SearchControls();
			ctrls.setReturningAttributes(ldapAttributes);
			ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			final NamingEnumeration<SearchResult> answers = context.search(
					base, String.format("(%s=%s)", filter, user), ctrls);
			final SearchResult result;
			if (answers.hasMoreElements()) {
				result = answers.nextElement();
			} else {
				throw new AuthenticationException();
			}
			final String ldapUser = result.getNameInNamespace();

			config.put(Context.SECURITY_PRINCIPAL, ldapUser);
			config.put(Context.SECURITY_CREDENTIALS, password);

			new InitialDirContext(config);
			logger.debug("Usuário {} válido", user);
			return Boolean.TRUE;
		} catch (final AuthenticationException e) {
			logger.debug("Usuário {} inválido, detalhe: {}", user,
					e.getMessage());
			return Boolean.FALSE;
		} catch (final Exception e) {
			logger.error("Erro ao atenticar usuario", e);
			throw new RuntimeException(e);
		}
	}

}
