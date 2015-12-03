package jadex.rules.rulesystem.rules;

import java.util.ArrayList;
import java.util.List;

import jadex.rules.state.OAVObjectType;

/**
 *  A condition that contains constraints belonging to one object.
 *  The tests may contain simple constant tests as well as joins
 *  with other objects.
 */
public class ObjectCondition extends ConstrainableCondition
{
	//-------- attributes --------
	
	/** The object type. */
	protected OAVObjectType type;
	
	//-------- constructors --------
	
	/**
	 *  Create a new object condition.
	 */
	public ObjectCondition(OAVObjectType type)
	{
		this(type, new ArrayList());
	}
	
	/**
	 *  Create a new object condition.
	 */
	public ObjectCondition(OAVObjectType type, List constraints)
	{
		super(constraints);
		this.type = type;
	}
	
	//-------- methods --------

	/**
	 *  Get the object type.
	 *  @return The object type.
	 */
	public OAVObjectType getObjectType()
	{
		return type;
	}
	
	/**
	 *  Get the object type.
	 */
	public void setObjectType(OAVObjectType type)
	{
		this.type = type;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer ret = new StringBuffer();
		ret.append("(");
		ret.append(type.getName());
		for(int i=0; i<getConstraints().size(); i++)
		{
			ret.append(" ");
			ret.append(getConstraints().get(i).toString());//+"\n");
		}
		ret.append(")");
		return ret.toString();
	}
}
