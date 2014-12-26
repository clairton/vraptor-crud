package br.eti.clairton.vraptor.crud.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;

@ApplicationScoped
public class AuthorizatorInMemory implements Authorizator {
	public static final Map<String, Map<String, List<String>>> ROLES = new HashMap<String, Map<String, List<String>>>() {
		private static final long serialVersionUID = 1L;

		{
			put("Pass", new HashMap<String, List<String>>() {
				private static final long serialVersionUID = 1L;
				{
					put("aplicacao", Arrays.asList("create", "update"));
				}
			});
		}
	};
	public static final Map<String, Map<String, Map<String, List<String>>>> AUTHORIZATIONS = new HashMap<>();
	static {
		AUTHORIZATIONS.put("admin", ROLES);
	}

	@Override
	public Boolean isAble(@NotNull final String user,
			@NotNull final String app, @NotNull final String resource,
			@NotNull final String operation) {
		return AUTHORIZATIONS.get(user).get(app).get(resource).contains(operation);
	}

}