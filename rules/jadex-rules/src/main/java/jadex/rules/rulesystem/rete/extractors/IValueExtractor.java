package jadex.rules.rulesystem.rete.extractors;

import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

/**
 *  A value extractor is responsible for extracting a certain
 *  attribute or object value from a tuple or object.
 */
public interface IValueExtractor
{
	/**
	 *  Get the value of an attribute from an object or tuple.
	 *  @param left The left input tuple. 
	 *  @param right The right input object.
	 *  @param prefix The prefix input object (last value from previous extractor in a chain).
	 *  @param state The working memory.
	 */
	public Object getValue(Tuple left, Object right, Object prefix, IOAVState state);

	/**
	 *  Test if a constraint evaluator is affected from a 
	 *  change of a certain attribute.
	 *  @param tupleindex The tuple index (-1 for object).
	 *  @param attr The attribute.
	 *  @return True, if affected.
	 */
	public boolean isAffected(int tupleindex, OAVAttributeType attr);

	/**
	 *  Get the set of relevant attribute types.
	 *  @return The relevant attribute types.
	 */
	public AttributeSet	getRelevantAttributes();


	/**
	 *  Get the set of indirect attribute types.
	 *  I.e. attributes of objects, which are not part of an object conditions
	 *  (e.g. for chained extractors) 
	 *  @return The relevant attribute types.
	 */
	public AttributeSet	getIndirectAttributes();
}
