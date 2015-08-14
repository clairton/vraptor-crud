package br.eti.clairton.vraptor.crud.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.eti.clairton.identificator.Identificator;

@Entity
@Table(name = "recursos")
public class Recurso extends br.eti.clairton.repository.Model {
	private static final long serialVersionUID = 1L;

	@ManyToOne(cascade = CascadeType.ALL)
	@NotNull
	private Aplicacao aplicacao;

	@NotNull
	@Size(min = 1, max = 50)
	@Identificator
	private String nome;

	@Deprecated
	public Recurso() {
		this(null, null);
	}

	public Recurso(final String nome) {
		super();
		this.nome = nome;
	}

	public Recurso(final Aplicacao aplicacao, final String nome) {
		this(nome);
		this.aplicacao = aplicacao;
	}

	public String getNome() {
		return nome;
	}

	public Aplicacao getAplicacao() {
		return aplicacao;
	}
}
