package jadex.rules.rulesystem.rete.constraints;

import java.util.Set;

import jadex.commons.SUtil;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.rulesystem.rete.extractors.IValueExtractor;
import jadex.rules.rulesystem.rete.extractors.ObjectExtractor;
import jadex.rules.rulesystem.rete.extractors.TupleExtractor;
import jadex.rules.rulesystem.rete.nodes.BetaMemory;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

/**
 *  The constraint indexer has the purpose to index equal constraints
 *  via the beta memory of the node, i.e. for each side a memory is
 *  kept that allows direct fetching of all matching values via the
 *  "indexed" attribute.
 */
public class ConstraintIndexer
{
	//-------- attributes --------
	
	/** The value extractor 1 (left). */
	protected IValueExtractor extractor1;
	
	/** The value extractor 2 (right). */
	protected IValueExtractor extractor2;
		
	//-------- constructors --------
	
	/**
	 *  Create a new indexed constraint evaluator.
	 */
	public ConstraintIndexer(IValueExtractor extractor1, IValueExtractor extractor2)
	{
		this.extractor1 = extractor1;
		this.extractor2 = extractor2;
		assert extractor1!=null;
		assert extractor2!=null;
	}
	
	//-------- methods --------
	
	/**
	 *  Find all objects for a tuple.
	 *  @param left The tuple.
	 *  @param bmem The beta memory.
	 *  @return The result collection.
	 */
	public Set	findObjects(Tuple left, BetaMemory bmem)
	{
		return bmem.getObjects(left, this);
	}
	
	/**
	 *  Find all tuples for an object.
	 *  @param right The object.
	 *  @param bmem The beta memory.
	 *  @return The result collection.
	 */
	public Set findTuples(Object right, BetaMemory bmem)
	{
		return bmem.getTuples(right, this);
	}
		
	/**
	 *  Add an object to the memory.
	 *  @param right The object.
	 *  @param state The state.
	 *  @param bmem The beta memory.
	 */
	public void addObject(Object right, IOAVState state, BetaMemory bmem)
	{
		Object rvalue = extractor2.getValue(null, right, null, state);
		bmem.addObject(rvalue, right, this);
	}
	
	/**
	 *  Add a tuple to the memory.
	 *  @param left The tuple.
	 *  @param state The state.
	 *  @param bmem The beta memory.
	 */
	public void addTuple(Tuple left, IOAVState state, BetaMemory bmem)
	{
		Object lvalue = extractor1.getValue(left, null, null, state);
		bmem.addTuple(lvalue, left, this);
	}
	
	/**
	 *  Remove an object from the memory.
	 *  @param right The object.
	 *  @param bmem The beta memory.
	 */
	public void removeObject(Object right, BetaMemory bmem)
	{
		bmem.removeObject(right, this);
	}
	
	/**
	 *  Remove a tuple from the memory.
	 *  @param left The tuple.
	 *  @param bmem The beta memory.
	 */
	public void removeTuple(Tuple left, BetaMemory bmem)
	{
		bmem.removeTuple(left, this);
	}
	
	/**
	 *  Test if the indexer uses the given attribute type for
	 *  right side indexing.
	 *  @return True, if type is used for indexing.
	 */
	public boolean isRightIndex(OAVAttributeType type)
	{
		boolean ret = false;
		
		if(extractor2 instanceof ObjectExtractor)
		{
			ObjectExtractor oex = (ObjectExtractor)extractor2;
			ret = SUtil.equals(oex.getAttribute(), type);
		}
		
		return ret;
	}
	
	/**
	 *  Test if the indexer uses the given index and attribute type for
	 *  left side indexing.
	 *  @return True, if combination is used for indexing.
	 */
	public boolean isLeftIndex(int tupleindex, OAVAttributeType type)
	{
		boolean ret = false;
		
		if(extractor1 instanceof TupleExtractor)
		{
			TupleExtractor tex = (TupleExtractor)extractor1;
			ret = SUtil.equals(tex.getAttribute(), type) && tex.getTupleIndex()==tupleindex;
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
	public AttributeSet getRelevantAttributes()
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
		AttributeSet ret	= new AttributeSet();
		ret.addAll(extractor1.getIndirectAttributes());
		ret.addAll(extractor2.getIndirectAttributes());
		return ret;
	}

	/**
	 *  Create a string representation. 
	 *  @return The string representation.
	 */
	public String	toString()
	{
		/*return Srules.getInnerClassName(this.getClass())
			+ "("+extractor1
			+ ", "+extractor2
			+ ")";*/
		
		return extractor1+" == "+extractor2+" (indexed)";
	}

	/**
	 *  Test if the evaluator equals an object.
	 */
	public boolean equals(Object obj)
	{
		if(this==obj)
			return true;

		boolean	ret	= false;
		if(obj instanceof ConstraintIndexer)
		{
			ConstraintIndexer other = (ConstraintIndexer)obj;
			ret	= SUtil.equals(extractor1, other.extractor1)
				&& SUtil.equals(extractor2, other.extractor2);
		}
		return ret;
	}
	
	/**
	 *  Get the hash code.
	 */
	public int hashCode()
	{
		return 31 + extractor1.hashCode() + 31 * extractor2.hashCode();
	}
}

