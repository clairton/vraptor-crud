package br.eti.clairton.vraptor.crud.controller;

import static br.eti.clairton.vraptor.crud.controller.VRaptorRunner.navigate;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import br.com.caelum.vraptor.test.VRaptorTestResult;
import br.com.caelum.vraptor.test.requestflow.UserFlow;

import com.google.gson.Gson;

public class ExportControllerMixinIntegrationTest {
	private final Gson gson = new Gson();

	@Test
	public void testCreateFile() {
		final UserFlow flow = navigate().post("/aplicacoes.csv");
		final VRaptorTestResult result = flow.execute();
		assertEquals(200, result.getResponse().getStatus());
		final String response = result.getResponseBody();
		final Map<?, ?> o = gson.fromJson(response, HashMap.class);
		assertEquals("test.csv", o.get("file_path"));
	}
}
