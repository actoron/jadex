package jadex.rules.rulesystem.rules;

import jadex.rules.rulesystem.ICondition;
import jadex.rules.state.OAVObjectType;

import java.util.ArrayList;
import java.util.List;

/**
 *  A condition that contains constraints belonging to one object.
 *  The tests may contain simple constant tests as well as joins
 *  with other objects.
 */
public class ObjectCondition implements ICondition
{
	//-------- attributes --------
	
	/** The object type. */
	protected OAVObjectType type;
	
	/** The constraints. */
	protected List constraints;
	
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
		this.type = type;
		this.constraints = constraints;
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
	 *  Add a constraint.
	 *  @param constraint The constraint- 
	 */
	public void addConstraint(IConstraint con)
	{
		constraints.add(con);
	}
	
	/**
	 *  Get the constraints.
	 *  @return The constraints.
	 */
	public List getConstraints()
	{
		return constraints;
	}
	
	/**
	 *  Get all bound constraints.
	 *  @return The bound constraints.
	 */
	public List getBoundConstraints()
	{
		List ret = new ArrayList();
		for(int i=0; i<constraints.size(); i++)
		{
			if(constraints.get(i) instanceof BoundConstraint)
				ret.add(constraints.get(i));
		}
		return ret;
	}
		
	/**
	 *  Get the variables.
	 *  @return The declared variables.
	 */
	public List getVariables()
	{
		List ret = new ArrayList();
		for(int i=0; i<constraints.size(); i++)
		{
			List tmp = ((IConstraint)constraints.get(i)).getVariables();
			for(int j=0; j<tmp.size(); j++)
			{
				if(!ret.contains(tmp.get(j)))
					ret.add(tmp.get(j));
			}
		}
		return ret;
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
		for(int i=0; i<constraints.size(); i++)
		{
			ret.append(" ");
			ret.append(constraints.get(i).toString());//+"\n");
		}
		ret.append(")");
		return ret.toString();
	}
}
