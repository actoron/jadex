package jadex.rules.rulesystem.rete.nodes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.state.IOAVState;


/**
 *  The rete memory for all nodes.
 */
public class ReteMemory
{
	//-------- attributes --------
	
	/** The memory. */
	protected Map memory;
	
	/** The state. */
	protected IOAVState	state;
	
//	/** Used for logging debug output. */
//	protected List	debug;
	
	//-------- constructors --------
	
	/**
	 *  Create a new rete memory.
	 */
	public ReteMemory(IOAVState state)
	{
		this.state	= state;
		this.memory = new HashMap();
	}
	
	//-------- collection methods --------
	
	/**
	 *  Test if there is a memory for a node.
	 *  @param node The node.
	 *  @return True, if there is a memory.
	 */
	public boolean	hasNodeMemory(INode node)
	{
		return memory.containsKey(node);
	}
	
//	protected Set	empties	= new HashSet();
//	protected Set	unempties	= new HashSet();
	
	/**
	 *  Get the memory for a node.
	 *  @param node The node.
	 *  @return The memory.
	 */
	public Object getNodeMemory(INode node)
	{
		Object ret = memory.get(node);
		if(ret == null)
		{
			ret = node.createNodeMemory(state);
			memory.put(node, ret);
		}
		
//		// Debbuging code to check if node memory is created but never used.
//		else if(!unempties.contains(ret))
//		{
//			boolean	empty	= ret instanceof Collection ? ((Collection)ret).isEmpty() : ((BetaMemory)ret).size()==0;
//			if(empty)
//			{
//				empties.add(ret);
//				System.out.println("Empties: "+empties.size());
//			}
//			else
//			{
//				empties.remove(ret);
//				unempties.add(ret);
//			}
//		}
		
		return ret;
	}
	
	//-------- tuple memory methods --------
	
	/**
	 *  Get a tuple.
	 *  Returns an existing tuple from the cache, if present.
	 *  Otherwise a new one is created.
	 */
	public Tuple	getTuple(IOAVState state, Tuple left, Object right)
	{
		return new Tuple(state, left, right);
//		Tuple	ret;
//		Map	lefties	= (Map)tuplememory.get(left);
//		if(lefties==null)
//		{
//			lefties	= new WeakHashMap();
//			tuplememory.put(left, lefties);
//			ret	= new Tuple(left, right);
//			lefties.put(right, ret);
//		}
//		else
//		{
//			ret	= (Tuple)lefties.get(right);
//			if(ret==null)
//			{				
//				ret	= new Tuple(left, right);
//				lefties.put(right, ret);
//			}
//		}
//		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the total memory size
	 *  @return The memory size.
	 */
	public int getTotalMemorySize()
	{
		int	size	= 0;
		for(Iterator it=memory.values().iterator(); it.hasNext(); )
		{
			Object o = it.next();
			if(o instanceof Collection)
			{
				size += ((Collection)o).size();
			}
			else if(o instanceof NotMemory)
			{
				size += ((NotMemory)o).size();
			}
			else if(o instanceof BetaMemory)
			{
				size += ((BetaMemory)o).size();
			}
		}
		return size;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		//return Srules.getInnerClassName(this.getClass())+"(values="+memory+")";
		StringBuffer ret = new StringBuffer(SReflect.getInnerClassName(this.getClass()));
		ret.append(", size="+getTotalMemorySize());
		ret.append(" : \n");
		for(Iterator it=memory.keySet().iterator(); it.hasNext(); )
		{
			Object node = it.next();
			ret.append(node).append(" : ");
			ret.append(memory.get(node)).append("\n");
		}
		return ret.toString();
	}
}
