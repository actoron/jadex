package jadex.bdi.examples.marsworld;

import jadex.bridge.fipa.IComponentAction;


/**
 *  Java class for concept RequestCarry of mars_beans ontology.
 */
public class RequestCarry implements IComponentAction
{
	//-------- attributes ----------

	/** Attribute for slot target id. */
	protected Object	target;

	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new RequestCarry.
	 */
	public RequestCarry()
	{
	}

	/**
	 *  Default Constructor.
	 *  Create a new RequestCarry.
	 */
	public RequestCarry(Object target)
	{
		this();
		setTarget(target);
	}

	//-------- accessor methods --------

	/**
	 *  Get the target of this RequestCarry.
	 * @return target
	 */
	public Object getTarget()
	{
		return this.target;
	}

	/**
	 *  Set the target of this RequestCarry.
	 * @param target the value to be set
	 */
	public void setTarget(Object target)
	{
		this.target = target;
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this RequestCarry.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "RequestCarry("+target+")";
	}
}
