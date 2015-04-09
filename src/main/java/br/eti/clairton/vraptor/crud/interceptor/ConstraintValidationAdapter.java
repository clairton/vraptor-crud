package br.eti.clairton.vraptor.crud.interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.validation.ConstraintViolation;

/**
 * Transforma as mensagens do BeanValidation em um map.
 * 
 * Posteriormente será serializado de uma forma que o front-end possa entender.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 */
@Dependent
public class ConstraintValidationAdapter {

	/**
	 * Transforma as mensagens do BeanValidation em um map.
	 * 
	 * @param violations
	 *            violações do bean validation
	 * @return map com os erros, sendo a chave o nome do atributo
	 */
	public Map<String, List<String>> to(final Set<ConstraintViolation<?>> violations) {
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
