package br.eti.clairton.vraptor.crud;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.runners.BlockJUnit4ClassRunner;

public class CdiJUnit4Runner extends BlockJUnit4ClassRunner {

	private final Class<?> klass;
	private final Weld weld;
	private final WeldContainer container;

	public CdiJUnit4Runner(final Class<?> klass)
			throws org.junit.runners.model.InitializationError {
		super(klass);
		this.klass = klass;
		this.weld = new Weld();
		this.container = weld.initialize();
	}

	@Override
	protected Object createTest() throws Exception {
		final Object test = container.instance().select(klass).get();
		return test;
	}
}