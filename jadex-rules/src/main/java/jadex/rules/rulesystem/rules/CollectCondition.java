package jadex.rules.rulesystem.rules;

import jadex.rules.rulesystem.ICondition;

import java.util.ArrayList;
import java.util.List;

/**
 *  A condition that contains constraints belonging to a collection of objects.
 */
public class CollectCondition implements ICondition
{
	//-------- attributes --------
	
	/** The object condition (contains constraints for objects in the collection). */
	protected List ocons;
	
	/** The constraints on the collection as a whole. */
	protected List constraints;
	
	//-------- constructors --------
	
	/**
	 *  Create a new object condition.
	 */
	public CollectCondition(ObjectCondition ocon)
	{
		this(ocon, new ArrayList());
	}
	
	/**
	 *  Create a new object condition.
	 */
	public CollectCondition(ObjectCondition ocon, List constraints)
	{
		this(createList(ocon), constraints);
	}
	
	/**
	 *  Create a new object condition.
	 */
	public CollectCondition(List ocons, List constraints)
	{
		this.ocons = ocons;
		this.constraints = constraints==null? new ArrayList(): constraints;
	}
	
	/**
	 *  Create a new complex condition.
	 */
	public CollectCondition(ObjectCondition[] oconditions, List constraints)
	{
		this.ocons = new ArrayList();
		for(int i=0; i<oconditions.length; i++)
			this.ocons.add(oconditions[i]);
		this.constraints = constraints==null? new ArrayList(): constraints;
	}
	
	//-------- methods --------

	/**
	 *  Get the object condition.
	 *  @return The object condition.
	 * /
	public ObjectCondition getObjectCondition()
	{
		return (ObjectCondition)ocons.get(0);
	}*/
	
	/**
	 *  Get the object conditions.
	 *  @return The object conditions.
	 */
	public List getObjectConditions()
	{
		return ocons;
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
		StringBuffer ret = new StringBuffer("(collect "+ocons+" ");
		for(int i=0; i<constraints.size(); i++)
			ret.append(constraints.get(i).toString());//+"\n");
		ret.append(")");
		return ret.toString();
	}
	
	/**
	 *  Create a list of object conditions.
	 *  @param ocond The object condition.
	 *  @return A list with the condition.
	 */
	protected static List createList(ObjectCondition ocon)
	{
		List ret = new ArrayList();
		ret.add(ocon);
		return ret;
	}
}
