package jadex.rules.rulesystem.rules;

/**
 *  Or connected constraint tests.
 */
public class OrConstraint extends ComplexConstraint
{
	//-------- constructors --------
	
	/**
	 *  Create a new or constraint.
	 *  @param firstconst The first constraint.
	 *  @param secondconst The second constraint.
	 */
	public OrConstraint(IConstraint firstconst, IConstraint secondconst)
	{
		super(firstconst, secondconst);
	}
	
	/**
	 *  Create a new or constraint.
	 *  @param consts The constraints
	 */
	public OrConstraint(IConstraint[] consts)
	{
		super(consts);
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		for(int i=0; i<constraints.size(); i++)
		{
			buf.append(constraints.get(i));
			if(i+1<constraints.size())
				buf.append(" | ");
		}
		return buf.toString();
	}
}
