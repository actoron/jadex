package jadex.rules.rulesystem.rules;


/**
 *  And connected constraint tests.
 */
public class AndConstraint extends ComplexConstraint
{
	//-------- constructors --------
	
	/**
	 *  Create a new and constraint.
	 *  @param firstconst The first constraint.
	 *  @param secondconst The second constraint.
	 */
	public AndConstraint(IConstraint firstconst, IConstraint secondconst)
	{
		super(firstconst, secondconst);
	}
	
	/**
	 *  Create a new and constraint.
	 *  @param consts The constraints
	 */
	public AndConstraint(IConstraint[] consts)
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
				buf.append(" & ");
		}
		return buf.toString();
	}
}
