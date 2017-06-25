package br.eti.clairton.vraptor.crud.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.eti.clairton.model.Model;

/**
 * Representa uma Aplicação.
 * 
 * @author Clairton Rodrigo Heinzen clairton.rodrigo@gmail.com
 */
@Entity
@Table(name = "aplicacoes", uniqueConstraints = { @UniqueConstraint(columnNames = "nome") })
public class Aplicacao extends Model {
	private static final long serialVersionUID = 1L;

	@NotNull
	@OneToMany(mappedBy = "aplicacao", cascade = CascadeType.ALL)
	private @Valid Collection<Recurso> recursos = new HashSet<Recurso>();

	@NotNull
	@Size(min = 1, max = 250)
	private String nome;

	/**
	 * Construtor padrão.
	 */
	public Aplicacao() {
	}

	/**
	 * Construtor com argumentos.
	 * 
	 * @param nome
	 *            nome da aplicação
	 * @param recursos
	 *            recursos da aplicação
	 */
	public Aplicacao(final String nome, final Collection<Recurso> recursos) {
		super();
		this.nome = nome;
		adicionar(recursos);
	}

	/**
	 * Construtor com argumentos.
	 * 
	 * @param nome
	 *            nome da aplicação
	 * @param recurso
	 *            recurso da aplicação
	 */
	public Aplicacao(final String nome, final Recurso recurso) {
		this(nome, Arrays.asList(recurso));
	}

	/**
	 * Construtor com parametros.
	 * 
	 * @param nome
	 *            da aplicação
	 */
	public Aplicacao(final String nome) {
		this(nome, Collections.<Recurso> emptyList());
	}

	public void adicionar(final Recurso recurso) {
		recursos.add(recurso);
	}

	public void adicionar(final Collection<Recurso> recursos) {
		this.recursos.addAll(recursos);
	}

	public void remover(final Recurso recurso) {
		recursos.remove(recurso);
	}

	public Collection<Recurso> getRecursos() {
		// teste para verificar se lançara exceção para lambdas em
		// ParanamerNameCrudProvider
		recursos.forEach(r -> System.out.println(r));
		return Collections.unmodifiableCollection(recursos);
	}

	public String getNome() {
		return nome;
	}
}
