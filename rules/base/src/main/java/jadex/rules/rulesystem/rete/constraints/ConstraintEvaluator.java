package jadex.rules.rulesystem.rete.constraints;

import jadex.commons.SUtil;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.rulesystem.rete.extractors.IValueExtractor;
import jadex.rules.rulesystem.rules.ILazyValue;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

/**
 *  A constraint evaluator is responsible for evaluating constraints.
 *  It operates on an operator and two extractors. It uses the extractors
 *  to fetch the values and invokes the operator subsequently with the
 *  values.
 */
public class ConstraintEvaluator implements IConstraintEvaluator
{
	//-------- attributes --------
	
	/** The operator. */
	protected IOperator operator;
	
	/** The value extractor 1. */
	protected IValueExtractor extractor1;
	
	/** The value extractor 2. */
	protected IValueExtractor extractor2;
	
	//-------- constructors --------
	
	/**
	 *  Create a new constraint evaluator.
	 *  @param operator The operator.
	 *  @param extractor1 The first extractor.
	 *  @param extractor2 The second extractor.
	 */
	public ConstraintEvaluator(IOperator operator, IValueExtractor extractor1, IValueExtractor extractor2)
	{
		assert operator!=null;
		assert extractor1!=null;
		assert extractor2!=null;
		
		this.operator = operator;
		this.extractor1 = extractor1;
		this.extractor2 = extractor2;
	}
	
	//-------- methods --------
	
	/**
	 *  Evaluate the constraints given the right object, left tuple 
	 *  (null for alpha nodes) and the state.
	 *  @param right The right input object.
	 *  @param left The left input tuple. 
	 *  @param state The working memory.
	 */
	public boolean evaluate(final Object right, final Tuple left, final IOAVState state)
	{
		boolean ret = false;
		try
		{
			ILazyValue val1 = new ILazyValue()
			{
				public Object getValue()
				{
					return extractor1.getValue(left, right, null, state);
				}
			};
			ILazyValue val2 = new ILazyValue()
			{
				public Object getValue()
				{
					return extractor2.getValue(left, right, null, state);
				}
			};
			ret = operator.evaluate(state, val1, val2); 
//			System.out.println(toString()+" "+ret);//+" "+extractor1.getValue(left, right, state)+" "+extractor2.getValue(left, right, state));
		}
		catch(Exception e)
		{
			// Catch extractor exception and return false in this case.
			System.out.println("eval error: "+this);
			e.printStackTrace();
		}
		return ret;
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
		return extractor1.isAffected(tupleindex, attr) 
			|| extractor2.isAffected(tupleindex, attr);
	}
	
	/**
	 *  Get the set of relevant attribute types.
	 */
	public AttributeSet	getRelevantAttributes()
	{
		AttributeSet ret = new AttributeSet();
		ret.addAll(extractor1.getRelevantAttributes());
		ret.addAll(extractor2.getRelevantAttributes());
		return ret;
	}
	
	/**
	 *  Get the set of indirect attribute types.
	 *  I.e. attributes of objects, which are not part of an object conditions
	 *  (e.g. for chained extractors) 
	 *  @return The relevant attribute types.
	 */
	public AttributeSet	getIndirectAttributes()
	{
		AttributeSet ret = new AttributeSet();
		ret.addAll(extractor1.getIndirectAttributes());
		ret.addAll(extractor2.getIndirectAttributes());
		return ret;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return extractor1+" "+operator+" "+extractor2;
	}
	
	/**
	 *  Get the operator.
	 */
	public IOperator getOperator()
	{
		return operator;
	}

	/**
	 *  Get the first value extractor.
	 */
	public IValueExtractor getExtractor1()
	{
		return extractor1;
	}

	/**
	 *  Get the second value extractor.
	 */
	public IValueExtractor getExtractor2()
	{
		return extractor2;
	}

	/**
	 *  Get the hash code.
	 */
	public int hashCode()
	{
		int result = 31 + ((extractor1 == null) ? 0 : extractor1.hashCode());
		result = 31 * result + ((extractor2 == null) ? 0 : extractor2.hashCode());
		result = 31 * result + ((operator == null) ? 0 : operator.hashCode());
		return result;
	}

	/**
	 *  Test if the evaluator equals an object.
	 */
	public boolean equals(Object obj)
	{
		if(this==obj)
			return true;

		boolean	ret	= false;
		if(obj instanceof ConstraintEvaluator)
		{
			ConstraintEvaluator other = (ConstraintEvaluator)obj;
			ret	= SUtil.equals(extractor1, other.getExtractor1())
				&& SUtil.equals(extractor2, other.getExtractor2())
				&& SUtil.equals(operator, other.getOperator());
		}
		return ret;
	}
}
