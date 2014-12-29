package br.eti.clairton.vraptor.crud.model;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.eti.clairton.identificator.Identificator;
import br.eti.clairton.repository.Model;

/**
 * Armazena as informaçõe de token.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Entity
@Table(name = "tokens", uniqueConstraints = { @UniqueConstraint(columnNames = "hash") })
public class Token extends Model {
	private static final long serialVersionUID = 1L;

	public enum Status {
		CREATED, EXPIRED, RENEWED, INVALIDATED;
	}

	@Identificator
	@NotNull
	@Size(min = 3)
	private final String user;

	@Identificator
	@NotNull
	@Size(min = 6)
	private final String hash;

	@Identificator
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_at")
	private final Date createAt;

	@Identificator
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "valid_at")
	private Date validAt;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Status status = Status.CREATED;

	/**
	 * Construtor padrão somente para Proxy.
	 */
	@Deprecated
	protected Token() {
		this(null, null, null);
	}

	/**
	 * Construtor com Parâmetros.
	 * 
	 * @param hash
	 *            hash unico para identificar a sessão
	 * @param validAt
	 *            até quando token será válido
	 * @param user
	 *            identificação do usuário ao qual pertence o token
	 */
	public Token(final String hash, final Date validAt, final String user) {
		super();
		this.hash = hash;
		this.validAt = validAt;
		this.user = user;
		createAt = new Date();
	}

	public String getHash() {
		return hash;
	}

	public Date getValidAt() {
		return validAt;
	}

	public String getUser() {
		return user;
	}

	/**
	 * Renova o token até a data recebida como parametro.
	 * 
	 * @param validAt
	 *            data de expiração do token
	 */
	public void renewAt(final Date validAt) {
		this.validAt = validAt;
		status = Status.RENEWED;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * Retorna os estados em que os tokens são válidos.
	 * 
	 * @return Array com tokens válidos
	 */
	public static Collection<Status> valids() {
		return asList(Status.CREATED, Status.RENEWED);
	}
}
