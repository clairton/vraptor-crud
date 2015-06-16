package br.eti.clairton.vraptor.crud.controller;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public interface FileService {
	<T>String toFile(Collection<T> collection, Map<String, Object> parameters, String extension);

	File toFile(String path);
}
