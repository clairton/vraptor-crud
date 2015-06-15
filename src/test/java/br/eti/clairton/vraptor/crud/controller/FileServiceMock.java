package br.eti.clairton.vraptor.crud.controller;

import java.io.File;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FileServiceMock implements FileService {
	@Override
	public String toFile(Collection<?> collection) {
		return "test.csv";
	}

	@Override
	public File toFile(final String path) {
		return new File("src/test/resources/" + path);
	}
}
