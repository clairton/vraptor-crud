package br.eti.clairton.vraptor.crud.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import br.eti.clairton.model.Base_;

@StaticMetamodel(Recurso.class)
public abstract class Recurso_ extends Base_ {

	public static volatile SingularAttribute<Recurso, Aplicacao> aplicacao;
	public static volatile SingularAttribute<Recurso, String> nome;

}
