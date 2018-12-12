package jadex.rules.rulesystem.rete.constraints;

import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

/**
 *  A not constraint evaluator checks if the left tuple
 *  corresponds to the beginning of the right object,
 *  which also has to be a tuple.
 */
public class NotConstraintEvaluator implements IConstraintEvaluator
{
	//-------- methods --------
	
	/**
	 *  Evaluate the constraints given the right object, left tuple 
	 *  (null for alpha nodes) and the state.
	 *  @param right The right input object.
	 *  @param left The left input tuple. 
	 *  @param state The working memory.
	 */
	public boolean evaluate(Object right, Tuple left, IOAVState state)
	{
//		System.out.println("Not match: "+left+", "+right);
		Tuple	tright	= (Tuple)right;
		int diff	= tright.size() - left.size();
		for(int i=0; i<diff; i++)
			tright	= tright.getLastTuple();
		return left.equals(tright);
	}
	
	/**
	 *  Test if a constraint evaluator is affected from a 
	 *  change of a certain attribute.
	 *  @param tupleindex The tuple index.
	 *  @param attr The attribute.
	 *  @return True, if affected.
	 */
	public boolean isAffected(int tupleindex, OAVAttributeType attr)
	{
		return false;
	}
	
	/**
	 *  Get the set of relevant attribute types.
	 */
	public AttributeSet	getRelevantAttributes()
	{
		return AttributeSet.EMPTY_ATTRIBUTESET;
	}
	
	/**
	 *  Get the set of indirect attribute types.
	 *  I.e. attributes of objects, which are not part of an object conditions
	 *  (e.g. for chained extractors) 
	 *  @return The relevant attribute types.
	 */
	public AttributeSet	getIndirectAttributes()
	{
		return AttributeSet.EMPTY_ATTRIBUTESET;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return " not ";
	}

	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof NotConstraintEvaluator;
	}
}
