package jadex.bdibpmn.examples.marsworld;

import jadex.bridge.fipa.IComponentAction;
import jadex.extension.envsupport.environment.ISpaceObject;


/**
 *  Java class for concept RequestCarry of mars_beans ontology.
 */
public class RequestCarry implements IComponentAction
{
	//-------- attributes ----------

	/** Attribute for slot target. */
	protected ISpaceObject	target;

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
	public RequestCarry(ISpaceObject target)
	{
		this();
		setTarget(target);
	}

	//-------- accessor methods --------

	/**
	 *  Get the target of this RequestCarry.
	 * @return target
	 */
	public ISpaceObject getTarget()
	{
		return this.target;
	}

	/**
	 *  Set the target of this RequestCarry.
	 * @param target the value to be set
	 */
	public void setTarget(ISpaceObject target)
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
