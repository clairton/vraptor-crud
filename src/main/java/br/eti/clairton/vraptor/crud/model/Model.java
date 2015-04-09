package br.eti.clairton.vraptor.crud.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Transient;

import br.eti.clairton.vraptor.hypermedia.Hypermediable;
import br.eti.clairton.vraptor.hypermedia.Link;

/**
 * Model implementando {@link Hypermediable}.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
public class Model extends br.eti.clairton.repository.Model implements
		Hypermediable {
	private static final long serialVersionUID = 1L;
	private @Transient final Set<Link> links = new HashSet<>();

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public Set<Link> getlinks() {
		return links;
	}

}
