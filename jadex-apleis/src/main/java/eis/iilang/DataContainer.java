package eis.iilang;

import java.util.LinkedList;

/**
 * A superclass for actions, events, et cetera.
 * 
 * @author tristanbehrens
 *
 */
public abstract class DataContainer extends IILElement {
	
	/** The name of the DataContainer. */
	protected String name = null;
	
	/** A list of parameters. */
	protected LinkedList<Parameter> params = new LinkedList<Parameter>();

	protected DataContainer() {}
	
	
	/** 
	 * Contructs an DataContainer.
	 * 
	 * @param name
	 * @param parameters
	 */
	public DataContainer(String name, Parameter... parameters) {
		
		this.name = name;
		
		for( Parameter p : parameters )
			this.params.add(p);
		
	}
	
	/**
	 * Returns the name.
	 * 
	 * @return
	 */
	public String getName() {
		
		return name;
		
	}

	/** 
	 * Returns the parameters.
	 * 
	 * @return
	 */
	public LinkedList<Parameter> getParameters() {
		
		return params;
		
	}

	/** 
	 * Converts a data container to a percept.
	 * 
	 * @param container
	 * @return
	 */
	public static Percept toPercept(DataContainer container) {
		
		Parameter[] parameters = new Parameter[container.params.size()];
		
		for(int a = 0 ; a < parameters.length ; a++ )
			parameters[a] = container.params.get(a);
		
		return new Percept(container.getName(), parameters);
		
	}

}
