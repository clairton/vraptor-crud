package br.eti.clairton.vraptor.crud.controller;

import static java.lang.String.format;
import static javax.enterprise.inject.spi.CDI.current;

import java.util.Map;

import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.cdise.weld.WeldContainerControl;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.caelum.vraptor.ioc.cdi.CDIBasedContainer;
import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.container.CdiContainer;
import br.com.caelum.vraptor.test.requestflow.UserFlow;
import br.com.caelum.vraptor.test.requestflow.VRaptorNavigation;
import net.vidageek.mirror.dsl.Mirror;

public class ControllerIntegration {

	protected Gson gson;

	protected static CDIBasedContainer cdiBasedContainer;

	private static CdiContainer cdiContainer;

	protected final String authType = "Bearer";

	protected String token = "asfdlhashsajhjksh==";
	
	@BeforeClass
	public static void startCDIContainer(){
		final WeldContainerControl container = (WeldContainerControl) CdiContainerLoader.getCdiContainer();
		final Mirror mirror = new Mirror();
		final Weld weld = (Weld) mirror.on(container).get().field("weld");
		final WeldContainer weldContainer = (WeldContainer) mirror.on(container).get().field("weldContainer");
		cdiContainer = new CdiContainer(weld, weldContainer, null);
		cdiContainer.start();
		cdiBasedContainer = current().select(CDIBasedContainer.class).get();
	}


	@Before
	public void setUp() {
		final GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
	}

	protected void authenticate(final String user, final String password) {
		final String pattern = "{'user': '%s', 'password': '%s'}";
		final String auth = format(pattern, user, password);
		final UserFlow flow = navigate()
								.post("/sessions")
								.setContent(auth)
								.addHeader("Content-Type", "application/json");
		final VRaptorTestResult result = flow.execute();
		final String json = result.getResponseBody();
		token = gson.fromJson(json, Map.class).get("token").toString();
	}

	public String token(){
		return authType + " " + token;
	}
	
	protected static UserFlow navigate(){
		final VRaptorNavigation navigation = cdiBasedContainer.instanceFor(VRaptorNavigation.class);
		navigation.setContainer(cdiContainer);
		return navigation.start();
	}
}
