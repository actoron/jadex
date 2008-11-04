package jadex.rules.rulesystem.rete.nodes;

import jadex.rules.rulesystem.rete.Tuple;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *  Node memory for not nodes. 
 */
public class NotMemory	extends BetaMemory
{
	//-------- attributes --------
	
	/** The mappings (lefttuple -> {rightvalues}). */
	protected Map mappings;

	//-------- constructors --------
	
	/**
	 *  Create a new not memory.
	 */
	public NotMemory(NotNode node)
	{
		super(node);
		this.mappings = new LinkedHashMap();
	}

	//-------- methods --------

	/**
	 *  Add a mapping for the key to the memory.
	 *  Multiples mappings for the same key will be stored in a set.
	 */
	public void	addMapping(Tuple key, Object value)
	{
		Set	values	= (Set)mappings.get(key);
		if(values==null)
		{
			values	= new HashSet();
			mappings.put(key, values);
		}

		values.add(value);
	}
	
	/**
	 *  Remove a mapping for the key from the memory.
	 *  The value will be stored in a set.
	 */
	public boolean	removeMapping(Tuple key, Object value)
	{
		boolean	ret	= false;
		Set	values	= (Set)mappings.get(key);
		if(values!=null)
			ret	= values.remove(value);
		return ret;
	}

	/**
	 *  Remove all mappings of the tuple from memory
	 */
	public void	removeMappings(Tuple key)
	{
		mappings.remove(key);
	}

	/**
	 *  Get the mappings for a given key.
	 */
	public Set	getMappings(Tuple key)
	{
		return mappings.containsKey(key) ? (Set)mappings.get(key) : Collections.EMPTY_SET;
	}
	
	/**
	 *  Get the size of the memory.
	 *  @return The size.
	 */
	public int size()
	{
		int ret = super.size();
		for(Iterator it=mappings.keySet().iterator(); it.hasNext();)
		{
			Collection c = (Collection)mappings.get(it.next());
			ret += c.size();
		}
		return ret;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		String ret = super.toString();
		ret	= ret.substring(0, ret.length()-1);
		ret	+= ", mappings=";
		ret	+= mappings;
		ret	+= ")";
		return ret;
	}
}
