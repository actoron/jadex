package eis.exceptions;

/**
 * This action is thrown if an attempt to perform an action or to retrieve percepts
 * has failed.
 * 
 * @author tristanbehrens
 *
 */
@SuppressWarnings("serial")
public class PerceiveException extends EnvironmentInterfaceException {

	public PerceiveException(String string) {
		super(string);
	}

	public PerceiveException(String message, Exception cause) {
		super(message);
		
		this.initCause(cause);
		
	} 
	
}
