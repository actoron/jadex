package eis.exceptions;

/**
 * This exception is thrown if something unexpected happens when accessing 
 * the environment-interface.

 * @author tristanbehrens
 *
 */
@SuppressWarnings("serial")
public class EnvironmentInterfaceException extends Exception {

	public EnvironmentInterfaceException(String string) {
		super(string);
	}

}
