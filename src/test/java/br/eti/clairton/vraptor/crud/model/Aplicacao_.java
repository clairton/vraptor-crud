package br.eti.clairton.vraptor.crud.model;

import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import br.eti.clairton.model.Base_;

@StaticMetamodel(Aplicacao.class)
public abstract class Aplicacao_ extends Base_ {

	public static volatile SingularAttribute<Aplicacao, String> nome;
	public static volatile CollectionAttribute<Aplicacao, Recurso> recursos;

}
