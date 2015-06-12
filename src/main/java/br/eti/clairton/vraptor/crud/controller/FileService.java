package br.eti.clairton.vraptor.crud.controller;

import java.io.File;
import java.util.Collection;

public interface FileService {
	String toFile(Collection<?> collection);

	File toFile(String path);
}
