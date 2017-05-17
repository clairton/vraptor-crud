package br.eti.clairton.vraptor.crud.controller;

import static java.lang.String.format;

import java.util.Map;

import javax.enterprise.inject.spi.CDI;

import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.cdise.weld.WeldContainerControl;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.caelum.vraptor.ioc.cdi.CDIBasedContainer;
import br.com.caelum.vraptor.test.VRaptorIntegration;
import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.container.CdiContainer;
import br.com.caelum.vraptor.test.container.Contexts;
import br.com.caelum.vraptor.test.requestflow.UserFlow;
import br.com.caelum.vraptor.test.requestflow.VRaptorNavigation;
import net.vidageek.mirror.dsl.Mirror;

public class ControllerIntegration extends VRaptorIntegration {

	protected Gson gson;

	protected final String authType = "Bearer";

	protected String token = "asfdlhashsajhjksh==";

	protected String json = "";
	
	protected static CDIBasedContainer cdiBasedContainer;

	protected static CdiContainer cdiContainer;

	@Before
	public void setUp() {
		final GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
	}
	
	protected String token() {
		return authType + " " + token;
	}

	protected void authenticate(final String user, final String password) {
		final String pattern = "{'user': '%s', 'password': '%s'}";
		final String json = format(pattern, user, password);
		final VRaptorTestResult result = navigate()
											.post("/sessions")
											.addHeader("Content-Type", "application/json; charset=utf-8")
											.setContent(json)
											.execute();
		result.wasStatus(201);
		final String response = result.getResponseBody();
		token = gson.fromJson(response, Map.class).get("token").toString();
	}

	@BeforeClass
	public static void startCDIContainer(){
		final WeldContainerControl container = (WeldContainerControl) CdiContainerLoader.getCdiContainer();
		final Mirror mirror = new Mirror();
		final Weld weld = (Weld) mirror.on(container).get().field("weld");
		final WeldContainer weldContainer = (WeldContainer) mirror.on(container).get().field("weldContainer");
		cdiContainer = new CdiContainer(weld, weldContainer, new Contexts());
		cdiContainer.start();
		cdiBasedContainer = CDI.current().select(CDIBasedContainer.class).get();
	}
	
	protected static UserFlow navigate(){
		final VRaptorNavigation navigation = cdiBasedContainer.instanceFor(VRaptorNavigation.class);
		navigation.setContainer(cdiContainer);
		return navigation.start();
	}
}
