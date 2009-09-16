package eis.exceptions;


/**
 * This exception is thrown if something unexpected happens when attempting to add 
 * or remove an entity.
 * 
 * @author tristanbehrens
 *
 */
@SuppressWarnings("serial")
public class EntityException extends EnvironmentInterfaceException {

	public EntityException(String string) {
		super(string);
	}

}
