package br.eti.clairton.vraptor.crud;

import static javax.enterprise.inject.spi.CDI.current;

import org.junit.runners.BlockJUnit4ClassRunner;

import br.com.caelum.vraptor.ioc.cdi.CDIBasedContainer;
import br.com.caelum.vraptor.test.container.CdiContainer;
import br.com.caelum.vraptor.test.requestflow.UserFlow;
import br.com.caelum.vraptor.test.requestflow.VRaptorNavigation;

public class CdiJUnit4Runner extends BlockJUnit4ClassRunner {

	private static CdiContainer cdiContainer;

	private static CDIBasedContainer cdiBasedContainer;

	public CdiJUnit4Runner(final Class<?> klass)
			throws org.junit.runners.model.InitializationError {
		super(klass);
		start();
	}

	private void start() {
		if (cdiContainer == null) {
			cdiContainer = new CdiContainer();
			cdiContainer.start();
			cdiBasedContainer = current().select(CDIBasedContainer.class).get();
			System.setProperty(Resource.ENVIROMENT_PARAM, "test");
		}
	}

	@Override
	protected Object createTest() throws Exception {
		return current().select(getTestClass().getJavaClass()).get();
	}

	public static UserFlow navigate() {
		final VRaptorNavigation navigation = cdiBasedContainer
				.instanceFor(VRaptorNavigation.class);
		navigation.setContainer(cdiContainer);
		return navigation.start().withoutJsp();
	}
}