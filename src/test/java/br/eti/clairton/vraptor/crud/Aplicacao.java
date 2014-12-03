package br.eti.clairton.vraptor.crud;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.eti.clairton.repository.Identificador;

/**
 * Representa uma Aplicação.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Entity
@Table(name = "aplicacoes")
public class Aplicacao extends br.eti.clairton.repository.Model {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = 1, max = 250)
	@Identificador
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
	 */
	public Aplicacao(final String nome) {
		super();
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}
}
