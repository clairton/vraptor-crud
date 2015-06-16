package br.eti.clairton.vraptor.crud.controller;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FileServiceMock implements FileService {
	@Override
	public <T> String toFile(final Collection<T> collection, final Map<String, Object> parameters, final String format) {
		return "test.csv";
	}

	@Override
	public File toFile(final String path) {
		return new File("src/test/resources/" + path);
	}
}
