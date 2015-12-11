package jadex.rules.rulesystem.rete.nodes;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.rules.rulesystem.rete.Tuple;

/**
 *  Memory for the collect node.
 */
public class CollectMemory
{
	//-------- attributes --------
	
	/** The working memory (index tuple -> result tuple). */
	protected Map workingmem;
	
	/** The result memory. */
	protected Set resultmem;

	//-------- methods --------
	
	/**
	 *  Add a tuple to the result.
	 *  @param tuple The result node. 
	 *  @return True, if could be added.
	 */
	public boolean	addResultTuple(Tuple tuple)
	{
		if(resultmem==null)
			resultmem = new LinkedHashSet();
		
		return resultmem.add(tuple);
	}
	
	/**
	 *  Remove from the result. 
	 *  @param tuple The tuple.
	 *  @return True, if could be removed.
	 */
	public boolean	removeResultTuple(Tuple tuple)
	{
		return resultmem==null || resultmem.remove(tuple);
	}
	
	/**
	 *  Test if tuple is contained in result memory.
	 *  @param tuple The tuple.
	 *  @return True, if contained.
	 */
	public boolean resultMemoryContains(Tuple tuple)
	{
		return resultmem==null? false: resultmem.contains(tuple);
	}
	
	/**
	 *  Get the result memory.
	 *  @return The result memory.
	 */
	public Collection getResultMemory()
	{
		return resultmem;
	}
	
	/**
	 *  Add a tuple to the result.
	 *  @param tuple The result node. 
	 */
	public void	putWorkingTuple(Tuple key, Tuple result)
	{
		if(workingmem==null)
			workingmem = new LinkedHashMap();
		
		workingmem.put(key, result);
	}
	
	/**
	 *  Add a tuple to the result.
	 *  @param tuple The result node. 
	 *  @return True, if could be added.
	 */
	public Tuple getWorkingTuple(Tuple key)
	{
		return workingmem==null? null: (Tuple)workingmem.get(key);
	}
	
	/**
	 *  Remove from the result. 
	 *  @param tuple The tuple.
	 */
	public void	removeWorkingTuple(Tuple key)
	{
		if(workingmem!=null)
			workingmem.remove(key);
	}
	
	/**
	 *  Get the result memory.
	 *  @return The result memory.
	 */
	public Map getWorkingMemory()
	{
		return workingmem;
	}
		
	/**
	 *  Get the size of the beta memory (including indexed memories).
	 *  @return The size of the memory.
	 */
	public int size()
	{
		return resultmem!=null? resultmem.size(): 0;
	}
	
	/**
	* Get the string representation.
	* @return The string representation. 
	*/
	public String toString()
	{
		StringBuffer ret = new StringBuffer();
		ret.append(SReflect.getInnerClassName(this.getClass()));
		ret.append("(resultmem=");
		ret.append(resultmem);
		ret.append(", workingmem=");
		ret.append(workingmem);
		ret.append(")");
		return ret.toString();
	}
}