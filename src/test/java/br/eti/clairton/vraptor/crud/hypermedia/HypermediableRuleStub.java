package br.eti.clairton.vraptor.crud.hypermedia;

import java.util.HashSet;
import java.util.Set;

import br.eti.clairton.vraptor.hypermedia.HypermediableRule;
import br.eti.clairton.vraptor.hypermedia.Link;

public class HypermediableRuleStub implements HypermediableRule {

	@Override
	public Set<Link> from(Object target, String resource, String operation) {
		final Set<Link> links = new HashSet<>();
		// if ("show".equals(operation)) {
		// links.add(new Link("/pessoas/1", "remove", "Remover", "DELETE",
		// "application/json"));
		links.add(new Link("/pessoas/1", "update", "Salvar", "PUT",
				"application/json"));
		// }
		return links;
	}

}
