package br.eti.clairton.vraptor.crud.model;

public class OutroModel extends Aplicacao {
	private static final long serialVersionUID = 6016230217349046379L;
	private String outroValor = "PSADGKSADGLDSLÇ";

	@Deprecated
	public OutroModel() {
		this(null);
	}

	public OutroModel(final String nome) {
		super(nome);
	}

	public String getOutroValor() {
		return outroValor;
	}
}