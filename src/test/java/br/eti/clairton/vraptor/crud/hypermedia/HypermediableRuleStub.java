package br.eti.clairton.vraptor.crud.hypermedia;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import br.eti.clairton.vraptor.hypermedia.Hypermediable;
import br.eti.clairton.vraptor.hypermedia.HypermediableRule;
import br.eti.clairton.vraptor.hypermedia.Link;

public class HypermediableRuleStub implements HypermediableRule {

	@Override
	public Set<Link> from(final Collection<Hypermediable> model,
			final String resource, final String operation) {
		final Set<Link> links = new HashSet<>();
		links.add(new Link("/pessoas/1", "update", "Salvar", "PUT",
				"application/json"));
		return links;
	}

	@Override
	public Set<Link> from(final Hypermediable model, final String resource,
			final String operation) {
		final Set<Link> links = new HashSet<>();
		links.add(new Link("/pessoas/1", "update", "Salvar", "PUT",
				"application/json"));
		return links;
	}
}