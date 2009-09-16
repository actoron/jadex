package eis.exceptions;


/**
 * This exception is thrown if an attempt to manipulate the agents-entities-relation
 * has failed.
 * 
 * @author tristanbehrens
 *
 */
@SuppressWarnings("serial")
public class RelationException extends EnvironmentInterfaceException {

	public RelationException(String string) {
		super(string);
	}

}
