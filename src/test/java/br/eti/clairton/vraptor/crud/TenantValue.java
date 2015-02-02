package br.eti.clairton.vraptor.crud;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class TenantValue implements
		br.eti.clairton.repository.TenantValue<String> {

	@Override
	public String get() {
		return Resource.TENANT;
	}

}
