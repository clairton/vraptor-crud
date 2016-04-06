package br.eti.clairton.vraptor.crud.tenant;

import javax.enterprise.context.RequestScoped;

import br.eti.clairton.repository.tenant.Value;
import br.eti.clairton.vraptor.crud.controller.Resource;

@RequestScoped
public class TenantValue implements Value<String> {

	@Override
	public String get() {
		return Resource.TENANT;
	}

}
