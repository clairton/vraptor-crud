package br.eti.clairton.vraptor.crud.model;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import br.eti.clairton.repository.Model_;
import br.eti.clairton.vraptor.crud.model.Token.Status;

@StaticMetamodel(Token.class)
public abstract class Token_ extends Model_ {

	public static volatile SingularAttribute<Token, String> hash;
	public static volatile SingularAttribute<Token, String> user;
	public static volatile SingularAttribute<Token, Date> validAt;
	public static volatile SingularAttribute<Token, Status> status;

}
