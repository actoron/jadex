package jadex.bdi.examples.marsworld_classic;

import jadex.bridge.fipa.IComponentAction;


/**
 *  Java class for concept RequestCarry of mars_beans ontology.
 */
public class RequestCarry implements IComponentAction
{
	//-------- attributes ----------

	/** Attribute for slot target. */
	protected Target	target;

	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new RequestCarry.
	 */
	public RequestCarry()
	{
	}

	//-------- accessor methods --------

	/**
	 *  Get the target of this RequestCarry.
	 * @return target
	 */
	public Target getTarget()
	{
		return this.target;
	}

	/**
	 *  Set the target of this RequestCarry.
	 * @param target the value to be set
	 */
	public void setTarget(Target target)
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
		return "RequestCarry(" + ")";
	}
}
