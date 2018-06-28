package jadex.bdiv3.model;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.modelinfo.UnparsedExpression;

/**
 *  Represents an initial / end belief (set).
 */
public class MConfigBeliefElement	extends MElement
{
	/** The initial / end facts. */
	protected List<UnparsedExpression>	facts;
	
	/**
	 *  The value to set.
	 *  @param value The value to set
	 */
	public void setFacts(List<UnparsedExpression> facts)
	{
		this.facts = facts;
	}
	
	
	/**
	 *  Get the value.
	 *  @return The value
	 */
	public List<UnparsedExpression> getFacts()
	{
		return facts;
	}

	/**
	 *  The value to set.
	 *  @param value The value to set
	 */
	public void addFact(UnparsedExpression fact)
	{
		if(facts==null)
			facts = new ArrayList<UnparsedExpression>();
		facts.add(fact);
	}
}
