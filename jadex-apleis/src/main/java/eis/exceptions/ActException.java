package eis.exceptions;

/**
 * This action is thrown if an attempt to perform an action or to retrieve percepts
 * has failed.
 * 
 * @author tristanbehrens
 *
 */
@SuppressWarnings("serial")
public class ActException extends EnvironmentInterfaceException {

	public ActException(String string) {
		super(string);
	}

	public ActException(String message, Exception cause) {
		super(message);
		
		this.initCause(cause);
		
	} 
	
}
