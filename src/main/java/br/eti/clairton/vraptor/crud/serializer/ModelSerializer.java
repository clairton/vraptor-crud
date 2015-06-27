package br.eti.clairton.vraptor.crud.serializer;

import br.eti.clairton.jpa.serializer.JpaSerializer;
import br.eti.clairton.repository.Model;

import com.google.gson.JsonSerializer;

/**
 * Deserializa os objetos da de forma a integrar com o modo ActiveSerializer.
 *
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
public class ModelSerializer extends JpaSerializer<Model> implements JsonSerializer<Model> {
}
