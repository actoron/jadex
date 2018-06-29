package jadex.rules.rulesystem.rules;

import java.util.ArrayList;
import java.util.List;

/**
 *  A condition that contains constraints belonging to a collection of objects.
 */
public class CollectCondition extends ConstrainableCondition
{
	//-------- attributes --------
	
	/** The object condition (contains constraints for objects in the collection). */
	protected List ocons;
	
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
		super(constraints==null? new ArrayList(): constraints);
		this.ocons = ocons;
	}
	
	/**
	 *  Create a new complex condition.
	 */
	public CollectCondition(ObjectCondition[] oconditions, List constraints)
	{
		super(constraints==null? new ArrayList(): constraints);
		this.ocons = new ArrayList();
		for(int i=0; i<oconditions.length; i++)
			this.ocons.add(oconditions[i]);
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
	 *  Get the variables.
	 *  @return The declared variables.
	 * /
	// Todo: include inner variables? no because not visible in outer scope???
	public List getVariables()
	{
		List ret = super.getVariables();
		for(int i=0; i<ocons.size(); i++)
		{
			List tmp = ((ICondition)ocons.get(i)).getVariables();
			for(int j=0; j<tmp.size(); j++)
			{
				if(!ret.contains(tmp.get(j)))
					ret.add(tmp.get(j));
			}
		}
		return ret;
	}*/
	
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
