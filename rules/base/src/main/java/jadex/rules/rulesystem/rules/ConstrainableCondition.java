package jadex.rules.rulesystem.rules;

import java.util.ArrayList;
import java.util.List;

import jadex.rules.rulesystem.ICondition;

/**
 *  A condition that contains constraints belonging to some object or value.
 */
public abstract class ConstrainableCondition implements ICondition
{
	//-------- attributes --------
	
	/** The constraints. */
	protected List constraints;
	
	//-------- constructors --------
	
	/**
	 *  Create an empty constrainable condition.
	 */
	public ConstrainableCondition()
	{
		this(new ArrayList());
	}
	
	/**
	 *  Create a condition with constraints.
	 */
	public ConstrainableCondition(List constraints)
	{
		this.constraints = constraints;
	}
	
	//-------- methods --------

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
}
