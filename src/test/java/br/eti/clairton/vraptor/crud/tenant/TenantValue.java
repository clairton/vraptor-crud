package br.eti.clairton.vraptor.crud.tenant;

import javax.enterprise.context.RequestScoped;

import br.eti.clairton.vraptor.crud.controller.Resource;

@RequestScoped
public class TenantValue implements
		br.eti.clairton.repository.TenantValue<String> {

	@Override
	public String get() {
		return Resource.TENANT;
	}

}
