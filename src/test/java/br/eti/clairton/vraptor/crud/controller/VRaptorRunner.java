package br.eti.clairton.vraptor.crud.controller;

import static javax.enterprise.inject.spi.CDI.current;

import org.junit.runners.BlockJUnit4ClassRunner;

import br.com.caelum.vraptor.ioc.cdi.CDIBasedContainer;
import br.com.caelum.vraptor.test.container.CdiContainer;
import br.com.caelum.vraptor.test.requestflow.UserFlow;
import br.com.caelum.vraptor.test.requestflow.VRaptorNavigation;

public class VRaptorRunner extends BlockJUnit4ClassRunner {

	private static CdiContainer cdiContainer;

	public VRaptorRunner(final Class<?> klass)
			throws org.junit.runners.model.InitializationError {
		super(klass);
		start();
	}

	private void start() {
		cdiContainer = new CdiContainer();
		cdiContainer.start();
		System.setProperty(Resource.ENVIROMENT_PARAM, "test");
		cdiContainer.startRequest();
	}

	@Override
	protected Object createTest() throws Exception {
		return current().select(getTestClass().getJavaClass()).get();
	}

	public static UserFlow navigate() {
		CDIBasedContainer cdiBasedContainer = current().select(
				CDIBasedContainer.class).get();
		final VRaptorNavigation navigation = cdiBasedContainer
				.instanceFor(VRaptorNavigation.class);
		navigation.setContainer(cdiContainer);
		return navigation.start().withoutJsp();
	}
}