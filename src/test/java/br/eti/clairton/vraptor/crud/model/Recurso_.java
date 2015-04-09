package br.eti.clairton.vraptor.crud.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import br.eti.clairton.repository.Model_;

@StaticMetamodel(Recurso.class)
public abstract class Recurso_ extends Model_ {

	public static volatile SingularAttribute<Recurso, Aplicacao> aplicacao;
	public static volatile SingularAttribute<Recurso, String> nome;

}
