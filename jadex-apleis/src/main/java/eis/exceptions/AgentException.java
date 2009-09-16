package eis.exceptions;


/**
 * This exception is thrown if an attempt to register or unregister an agent
 * has failed.
 * 
 * @author tristanbehrens
 *
 */
@SuppressWarnings("serial")
public class AgentException extends EnvironmentInterfaceException {

	public AgentException(String string) {
		super(string);
	}

}
