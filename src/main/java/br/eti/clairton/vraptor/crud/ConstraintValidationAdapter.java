package br.eti.clairton.vraptor.crud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.validation.ConstraintViolation;

@Dependent
public class ConstraintValidationAdapter {

	public Map<String, List<String>> to(
			final Set<ConstraintViolation<?>> violations) {
		final Map<String, List<String>> errors = new HashMap<String, List<String>>();
		for (final ConstraintViolation<?> violation : violations) {
			final String key = violation.getPropertyPath().toString();
			if (!errors.containsKey(key)) {
				errors.put(key, new ArrayList<String>());
			}
			errors.get(key).add(violation.getMessage());
		}
		return errors;
	}

}
