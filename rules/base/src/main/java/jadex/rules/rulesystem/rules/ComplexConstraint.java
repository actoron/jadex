package jadex.rules.rulesystem.rules;

import java.util.ArrayList;
import java.util.List;

/**
 *  A complex constraint consists of a number of constraints
 *  connected by the same operator (and / or).
 */
public class ComplexConstraint implements IConstraint
{
	//-------- attributes --------
	
	/** The constraints. */
	protected List constraints;
	
	//-------- constructors --------
	
	/**
	 *  Create a new complex constraint.
	 *  @param firstconst The first constraint.
	 *  @param secondconst The second constraint.
	 */
	public ComplexConstraint(IConstraint firstconst, IConstraint secondconst)
	{
		this.constraints = new ArrayList();
		addConstraint(firstconst);
		addConstraint(secondconst);
	}
	
	/**
	 *  Create a new complex constraint.
	 *  @param consts The constraints
	 */
	public ComplexConstraint(IConstraint[] consts)
	{
		this.constraints = new ArrayList();
		for(int i=0; consts!=null && i<consts.length; i++)
			constraints.add(consts[i]);
	}

	//-------- methods --------
	
	/**
	 *  Add a constraints.
	 *  @param constraint The constraint. 
	 */
	public void addConstraint(IConstraint constraint)
	{
		constraints.add(constraint);
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
