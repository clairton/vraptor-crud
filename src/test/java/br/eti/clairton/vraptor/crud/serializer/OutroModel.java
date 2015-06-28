package br.eti.clairton.vraptor.crud.serializer;

import br.eti.clairton.vraptor.crud.model.Aplicacao;

public class OutroModel extends Aplicacao {
	private static final long serialVersionUID = 6016230217349046379L;
	private String outroValor = "PSADGKSADGLDSLÇ";

	public OutroModel(final String nome) {
		super(nome);
	}

	public String getOutroValor() {
		return outroValor;
	}
}