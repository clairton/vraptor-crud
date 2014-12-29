package br.eti.clairton.vraptor.crud;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import br.eti.clairton.repository.Model_;

@StaticMetamodel(Aplicacao.class)
public abstract class Aplicacao_ extends Model_ {

	public static volatile SingularAttribute<Aplicacao, String> nome;

}
