package br.eti.clairton.vraptor.crud.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ManyToMany;

import net.vidageek.mirror.dsl.Mirror;
import br.eti.clairton.model.Model;

public class ModelManyToMany extends Model {
	private static final long serialVersionUID = 6016230217349046379L;

	@ManyToMany
	private final List<Aplicacao> aplicacoes = new ArrayList<Aplicacao>();

	public ModelManyToMany() {
		final Aplicacao aplicacao = new Aplicacao("Teste");
		new Mirror().on(aplicacao).set().field("id").withValue(100l);
		aplicacoes.add(aplicacao);
		final Aplicacao aplicacao2 = new Aplicacao("OutroTeste");
		new Mirror().on(aplicacao2).set().field("id").withValue(200l);
		aplicacoes.add(aplicacao2);
	}

	public List<Aplicacao> getAplicacoes() {
		return aplicacoes;
	}
}