package jadex.rules.rulesystem.rete.constraints;

import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

import java.util.Set;

/**
 *  Interface for all constraint evaluators. They are used
 *  to abstract away the tests nodes have to perform.
 */
public interface IConstraintEvaluator
{
	/**
	 *  Evaluate the constraints given the right object, left tuple 
	 *  (null for alpha nodes) and the state.
	 *  @param right The right input object.
	 *  @param left The left input tuple. 
	 *  @param state The working memory.
	 */
	public boolean evaluate(Object right, Tuple left, IOAVState state);
	
	/**
	 *  Test if a constraint evaluator is affected from a 
	 *  change of a certain attribute.
	 *  @param tupleindex The tuple index.
	 *  @param attr The attribute.
	 *  @return True, if affected.
	 */
	public boolean isAffected(int tupleindex, OAVAttributeType attr);

	/**
	 *  Get the set of relevant attribute types.
	 */
	public Set	getRelevantAttributes();
}
