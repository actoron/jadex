package eis.exceptions;

/**
 * This action is thrown if an attempt to perform an action or to retrieve percepts
 * has failed.
 * 
 * @author tristanbehrens
 *
 */
@SuppressWarnings("serial")
public class NoEnvironmentException extends EnvironmentInterfaceException {

	public NoEnvironmentException(String string) {
		super(string);
	}

	public NoEnvironmentException(String message, Exception cause) {
		super(message);
		
		this.initCause(cause);
		
	} 
	
}
