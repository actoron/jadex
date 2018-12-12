package jadex.bdibpmn.examples.marsworld;

import jadex.bridge.fipa.IComponentAction;
import jadex.extension.envsupport.environment.ISpaceObject;


/**
 *  Java class for concept RequestProduction of mars_beans ontology.
 */
public class RequestProduction implements IComponentAction
{
	//-------- attributes ----------

	/** Attribute for slot target. */
	protected ISpaceObject	target;

	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new RequestProduction.
	 */
	public RequestProduction()
	{
	}

	/**
	 *  Create a new RequestProduction.
	 */
	public RequestProduction(ISpaceObject target)
	{
		this();
		setTarget(target);
	}

	//-------- accessor methods --------

	/**
	 *  Get the target of this RequestProduction.
	 * @return target
	 */
	public ISpaceObject getTarget()
	{
		return this.target;
	}

	/**
	 *  Set the target of this RequestProduction.
	 * @param target the value to be set
	 */
	public void setTarget(ISpaceObject target)
	{
		this.target = target;
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this RequestProduction.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "RequestProduction("+target+")";
	}
}
