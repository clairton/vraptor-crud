package br.eti.clairton.vraptor.crud.controller;

import static java.lang.String.format;

import java.util.Map;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Before;
import org.springframework.mock.web.HeaderValueHolderTest;
import org.springframework.mock.web.MockHttpServletRequest;

import br.com.caelum.vraptor.test.VRaptorIntegration;
import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.http.Parameters;
import br.com.caelum.vraptor.test.requestflow.UserFlow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ControllerIntegration extends VRaptorIntegration {

	protected final Mirror mirror = new Mirror();

	protected Gson gson;

	protected final String authType = "Bearer";

	protected String token = "asfdlhashsajhjksh==";

	protected Parameters parameters = new Parameters() {
		@Override
		public void fill(MockHttpServletRequest request) {
			final Object field = mirror.on(request).get().field("headers");
			@SuppressWarnings("unchecked")
			final Map<String, HeaderValueHolderTest> headers = (Map<String, HeaderValueHolderTest>) field;
			final HeaderValueHolderTest valueHolder = new HeaderValueHolderTest();
			valueHolder.setValue(authType + " " + token);
			headers.put("Authorization", valueHolder);
		}
	};

	@Before
	public void setUp() {
		final GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
	}

	protected void authenticate(final String user, final String password) {
		final Parameters p = new Parameters() {
			@Override
			public void fill(final MockHttpServletRequest request) {
				final String pattern = "{'user': '%s', 'password': '%s'}";
				final String json = format(pattern, user, password);
				mirror.on(request).set().field("content")
						.withValue(json.getBytes());
				mirror.on(request).set().field("contentType")
						.withValue("application/json");
			}
		};
		final UserFlow flow = navigate().post("/sessions", p);
		final VRaptorTestResult result = flow.execute();
		final String json = result.getResponseBody();
		token = gson.fromJson(json, Map.class).get("token").toString();
	}

}
