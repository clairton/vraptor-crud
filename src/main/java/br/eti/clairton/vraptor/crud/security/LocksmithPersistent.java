package br.eti.clairton.vraptor.crud.security;

import static br.eti.clairton.repository.Comparators.IN;
import static br.eti.clairton.repository.Operators.OR;
import static br.eti.clairton.vraptor.crud.model.Token.valids;
import static br.eti.clairton.vraptor.crud.model.Token_.hash;
import static br.eti.clairton.vraptor.crud.model.Token_.status;
import static br.eti.clairton.vraptor.crud.model.Token_.user;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Random;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.security.auth.login.CredentialNotFoundException;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.Logger;

import br.com.caelum.vraptor.environment.Property;
import br.eti.clairton.repository.Predicate;
import br.eti.clairton.repository.Repository;
import br.eti.clairton.vraptor.crud.model.Token;
import br.eti.clairton.vraptor.crud.model.Token.Status;
import br.eti.clairton.vraptor.crud.model.Token_;

@RequestScoped
public class LocksmithPersistent implements Locksmith {
	private final MessageDigest crypt;
	private final Lock authenticator;
	private final Repository repository;
	private final Charset charset;
	private final Logger logger;
	private final Long lifeTime;

	@Inject
	public LocksmithPersistent(
			@NotNull final Logger logger,
			@NotNull final Lock authenticator,
			@NotNull final Repository repository,
			@NotNull final @Property(value = "token.lifetime", defaultValue = "18000") String lifeTime,
			@NotNull final @Property(value = "token.algorithm", defaultValue = "SHA-1") String algorithm) {
		try {
			crypt = MessageDigest.getInstance(algorithm);
			this.authenticator = authenticator;
			this.repository = repository;
			charset = Charset.forName("UTF-8");
			this.logger = logger;
			this.lifeTime = Long.valueOf(lifeTime);
		} catch (final Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String create(@NotNull final String user,
			@NotNull final String password) throws CredentialNotFoundException {
		if (authenticator.isValid(user, password)) {
			try {
				final Predicate p1 = new Predicate(OR, user, Token_.user);
				final Predicate p2 = new Predicate(Token.valids(), IN, status);
				final Token token = repository.tenant(Boolean.FALSE)
						.from(Token.class).where(p1).and(p2).single();
				invalidate(token);
			} catch (final NoResultException e) {
			}
			final String info = (user + password + new Random().nextLong());
			final byte[] bytes = info.getBytes(charset);
			crypt.update(bytes);
			final byte[] digest = crypt.digest();
			final StringBuffer sb = new StringBuffer();
			for (final byte b : digest) {
				sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			}
			final String hash = sb.toString();
			final Long now = new Date().getTime();
			final Date validAt = new Date(now + (lifeTime * 1000l));
			final Token token = new Token(hash, validAt, user);
			repository.tenant(Boolean.FALSE).save(token);
			return hash;
		} else {
			throw new CredentialNotFoundException();
		}
	}

	@Override
	public void invalidate(@NotNull final String key) {
		final Predicate f1 = new Predicate(key, Token_.hash);
		final Predicate f2 = new Predicate(key, Token_.user);
		final Predicate f3 = new Predicate(valids(), IN, status);
		try {
			final Token token = repository.tenant(Boolean.FALSE)
					.from(Token.class).where(f1).or(f2).and(f3).single();
			invalidate(token);
		} catch (final NoResultException e) {
		}
	}

	@Override
	public Boolean isValid(@NotNull final String key) {
		logger.debug("Validando key: {}", key);
		final Predicate f1 = new Predicate(OR, key, user);
		final Predicate f2 = new Predicate(key, hash);
		final Predicate f3 = new Predicate(valids(), IN, status);
		final Boolean isValid = repository.tenant(Boolean.FALSE)
				.from(Token.class).where(f1).or(f2).and(f3).exist();
		logger.debug("key {} está {}", key, isValid ? "valida" : "invalida");
		return isValid;
	}

	@Override
	public String getUserByToken(@NotNull final String token) {
		logger.debug("Buscando Usuario para token {}", token);
		return getToken(token).getUser();
	}

	private Token getToken(@NotNull final String hash) {
		logger.debug("Buscando Token para hash {}", hash);
		final Predicate predicate = new Predicate(hash, Token_.hash);
		final Token token = repository.tenant(Boolean.FALSE).from(Token.class)
				.where(predicate).first();
		return token;
	}

	private void invalidate(final Token token) {
		token.setStatus(Status.INVALIDATED);
		repository.tenant(Boolean.FALSE).save(token);
	}
}
