package br.eti.clairton.vraptor.crud.controller;

import static java.lang.Boolean.FALSE;

import java.lang.reflect.Field;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

import net.vidageek.mirror.list.dsl.Matcher;

public class JpaMatcher implements Matcher<Field> {
	private final Metamodel meta;

	public JpaMatcher(final Metamodel meta) {
		super();
		this.meta = meta;
	}

	@Override
	public boolean accepts(final Field element) {
		final Class<?> type = element.getDeclaringClass();
		try {
			final ManagedType<?> managed = meta.managedType(type);
			final Attribute<?, ?> attribute = managed.getAttribute(element.getName());
			return SingularAttribute.class.isInstance(attribute);
		} catch (final IllegalArgumentException e) {
			return FALSE;
		}
	}
}
