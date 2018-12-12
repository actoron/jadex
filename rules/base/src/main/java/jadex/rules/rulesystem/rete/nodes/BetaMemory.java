package jadex.rules.rulesystem.rete.nodes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.constraints.ConstraintIndexer;
import jadex.rules.state.IOAVState;

/**
 *  Memory for a beta node.
 *  It consists of:
 *  - indexed memories for each equal constraint (optional for speed)
 *  - a result memory (collection) 
 */
public class BetaMemory
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState state;
	
	/** The indexed memories (indexed constraint evaluator -> memory). */
	protected Map xmems;
	
	/** The result memory. */
	protected Set resultmem;
	
	//-------- constructors --------
	
	/**
	 *  Create a new beta memory.
	 *  @param node The beta node.
	 */
	public BetaMemory(IOAVState state)
	{
		this.state	= state;
		
	}
	
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
		return resultmem!=null && resultmem.remove(tuple);
	}
	
	/**
	 *  Get the result memory.
	 *  @return The result memory.
	 */
	public Collection getResultMemory()
	{
		return resultmem!=null ? resultmem : Collections.EMPTY_SET;
	}
	
	/**
	 *  Get the indexed objects per tuple.
	 *  @param tuple The indexed tuple.
	 *  @param ci The constraint indexer. 
	 */
	public Set getObjects(Tuple tuple, ConstraintIndexer ci)
	{
		IndexedConstraintMemory	mem	= getIndexedMemory(ci);
		Object	value	= mem.getTupleValue(tuple);
		return mem.getObjects(value);
	}
	
	/**
	 *  Get the indexed tuples per object.
	 *  @param object The indexed object.
	 *  @param ci The constraint indexer. 
	 */
	public Set getTuples(Object object, ConstraintIndexer ci)
	{
		IndexedConstraintMemory	mem	= getIndexedMemory(ci);
		Object	value	= mem.getObjectValue(object);
		return mem.getTuples(value);
	}
	
	/**
	 *  Add an value -> object pair to the object index.
	 *  @param value The value.
	 *  @param object The object.
	 *  @param ci The constraint indexer.
	 */
	public void addObject(Object value, Object object, ConstraintIndexer ci)
	{
		getIndexedMemory(ci).addObject(value, object);
	}
	
	/**
	 *  Remove an object from the object index.
	 *  @param object The object.
	 *  @param ci The constraint indexer.
	 */
	public void removeObject(Object object, ConstraintIndexer ci)
	{
		getIndexedMemory(ci).removeObject(object);
	}
	
	/**
	 *  Add an value -> object pair to the tuple index.
	 *  @param value The value.
	 *  @param tuple The tuple.
	 *  @param ci The constraint indexer.
	 */
	public void addTuple(Object value, Tuple tuple, ConstraintIndexer ci)
	{
		getIndexedMemory(ci).addTuple(value, tuple);
	}
	
	/**
	 *  Remove an value -> object pair from the tuple index.
	 *  @param object The object.
	 *  @param ci The constraint indexer.
	 */
	public void removeTuple(Tuple tuple, ConstraintIndexer ci)
	{
		getIndexedMemory(ci).removeTuple(tuple);
	}
	
	/**
	 *  Get the indexed memory per constraint indexer.
	 *  @param ci The constraint indexer.
	 */
	protected IndexedConstraintMemory getIndexedMemory(ConstraintIndexer ci)
	{
		if(xmems==null)
			xmems	= new IdentityHashMap();
		if(xmems.get(ci)==null)
			xmems.put(ci, new IndexedConstraintMemory(state));			
		return (IndexedConstraintMemory)xmems.get(ci);
	}
	
	/**
	 *  Get the size of the beta memory (including indexed memories).
	 *  @return The size of the memory.
	 */
	public int size()
	{
		int ret = resultmem!=null? resultmem.size(): 0;
		if(xmems!=null)
		{
			for(Iterator it=xmems.keySet().iterator(); it.hasNext();)
			{
				IndexedConstraintMemory icm = getIndexedMemory((ConstraintIndexer)it.next());
				ret += icm.size();
			}
		}
		return ret;
	}
	
	/**
	* Get the string representation.
	* @return The string representation. 
	*/
	public String toString()
	{
		StringBuffer ret = new StringBuffer();
		ret.append(SReflect.getInnerClassName(this.getClass()));
		ret.append("(results=");
		ret.append(resultmem);
		ret.append(", xmems=");
		ret.append(xmems);
		ret.append(")");
		return ret.toString();
	}
	
	/**
	 *  A memory for an indexed constraint.
	 */
	public static class IndexedConstraintMemory
	{
		//-------- attributes --------
		
		/** The map for (value -> objects). */
		protected Map objects;
		
		/** The map for (value -> tuples). */
		protected Map tuples;
		
		/** The cached values (object -> value).
		 *  Required, because objects can change and old value (for removal) can no longer be obtained. */
		protected Map ovalues;
		
		/** The cached values (tuple -> value).
		 *  Required, because objects can change and old value (for removal) can no longer be obtained. */
		protected Map tvalues;
		
		//-------- constructors --------
		
		/**
		 *  Create a new constraint memory.
		 */
		public IndexedConstraintMemory(IOAVState state)
		{
			this.objects = state.isJavaIdentity() ? (Map) new MixedIdentityHashMap(state) : new HashMap();
			this.tuples = state.isJavaIdentity() ? (Map) new MixedIdentityHashMap(state) : new HashMap();
			this.ovalues = state.isJavaIdentity() ? (Map) new MixedIdentityHashMap(state) : new HashMap();
			this.tvalues = new HashMap();
		}	
		
		//-------- methods --------
		
		/**
		 *  Get object for value.
		 *  @param value The value.
		 *  @return The objects.
		 */
		public Set getObjects(Object value)
		{
			return (Set)objects.get(value);
		}
		
		/**
		 *  Get the tuples for a value.
		 *  @param value The value.
		 *  @return The tuples.
		 */
		public Set getTuples(Object value)
		{
			return (Set)tuples.get(value);
		}
		
		/**
		 *  Get the value for an object.
		 *  @param object The object.
		 *  @return The value.
		 */
		public Object	getObjectValue(Object object)
		{
			return ovalues.get(object);
		}
		
		/**
		 *  Get the value for a tuple.
		 *  @param tuple The tuple.
		 *  @return The value.
		 */
		public Object	getTupleValue(Tuple tuple)
		{
			return tvalues.get(tuple);
		}
		
		/**
		 *  Add an object to the memory.
		 *  @param value The index.
		 *  @param object The object.
		 */
		public void addObject(Object value, Object object)
		{
			if(value instanceof Tuple)
				throw new RuntimeException("Tuples are not objects here: "+value);
			
			Set os = (Set)objects.get(value);
			if(os==null)
			{
				os = new LinkedHashSet();
				objects.put(value, os);
			}
			os.add(object);
			ovalues.put(object, value);
		}
		
		/**
		 *  Remove an object from the memory.
		 *  @param object The object.
		 */
		public void removeObject(Object object)
		{
			Object	value	= ovalues.remove(object);
			Set os = (Set)objects.get(value);
			
			if(os!=null)
			{
				if(os.size()==1)
				{
					objects.remove(value);
				}
				else if(os.size()>1)
				{
					os.remove(object);
				}
			}
		}
		
		/**
		 *  Add a tuple to the memory.
		 *  @param value The index.
		 *  @param tuple The object.
		 */
		public void addTuple(Object value, Tuple tuple)
		{
			Set os = (Set)tuples.get(value);
			if(os==null)
			{
				os = new LinkedHashSet();
				tuples.put(value, os);
			}
			os.add(tuple);
			tvalues.put(tuple, value);
		}
		
		/**
		 *  Remove a tuple from the memory.
		 *  @param tuple The tuple.
		 */
		public void removeTuple(Tuple tuple)
		{
			Object value	= tvalues.remove(tuple);
			Set os = (Set)tuples.get(value);
			if(os!=null)
			{
				if(os.size()==1)
				{
					tuples.remove(value);
				}
				else if(os.size()>1)
				{
					os.remove(tuple);
				}
			}
		}
		
		/**
		 *  Get the size of the beta memory (including indexed memories).
		 *  @return The size of the memory.
		 */
		public int size()
		{
			int ret = 0;
			for(Iterator it=objects.keySet().iterator(); it.hasNext();)
			{
				Collection c = (Collection)objects.get(it.next());
				ret += c.size();
			}
			for(Iterator it=tuples.keySet().iterator(); it.hasNext();)
			{
				Collection c = (Collection)tuples.get(it.next());
				ret += c.size();
			}
			return ret;
		}
		
		/**
		 *  Create a string representation. 
		 *  @return The string representation.
		 */
		public String	toString()
		{
			return SReflect.getInnerClassName(this.getClass())
				+ "( objects="+objects
				+ ", tuples="+tuples
				+ ")";
		}
		
	}
}
