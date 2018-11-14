package jadex.rules.rulesystem.rete.nodes;

import java.util.HashMap;

import jadex.commons.SReflect;
import jadex.commons.collection.SCollection;

/**
 *  Abstract super class for all kinds of nodes.
 */
public abstract class AbstractNode implements INode
{	
	//-------- attributes --------
	
	/** The node id. */
	protected int nodeid;
	
	//-------- constructors --------

	/**
	 *  Create a new node.
	 */
	public AbstractNode(int nodeid)
	{
		this.nodeid = nodeid;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the nodeid.
	 *  @return The nodeid.
	 */
	public int getNodeId()
	{
		return nodeid;
	}

	/**
	 *  Get the hashcode.
	 *  @return The hash code.
	 */
	public int hashCode()
	{
		return nodeid;
	}

	/**
	 *  Test for equality.
	 *  @return True, if equal.
	 */
	public boolean equals(Object obj)
	{
		return (obj instanceof INode) && ((INode)obj).getNodeId()==nodeid;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return toString("");
	}
	
	/**
	 *  Customizable string representation.
	 *  The given string will be inserted.
	 */
	protected String	toString(String insert)
	{
		return SReflect.getInnerClassName(this.getClass())
			+ "(id="+nodeid
			+ insert
//			+ ", relevants="+getRelevantAttributes()
			+ ")";
	}

	//-------- cloneable --------
	
	/** The thread local. */
	protected static final ThreadLocal clones = new ThreadLocal();
	
	/**
	 *  Clone this object.
	 *  @return A clone of this object.
	 */
	public Object clone()
	{
		Object ret = null;
	
		boolean creator = false;
		HashMap cls = (HashMap)clones.get();
		if(cls==null)
		{
			cls = SCollection.createHashMap();
			clones.set(cls);
			creator = true;
		}
		else
		{
			ret = cls.get(this);
		}
	
		if(ret==null)
		{
			try
			{
				ret = super.clone();
			}
			catch(CloneNotSupportedException exception)
			{
				throw new RuntimeException("Cloning not supported: "+this);
			}
	
			// Save the clone immediately to make it accessible for other elements.
			cls.put(this, ret);
	
			doClone(ret);
		}

		// Delete clones hashmap if element was creator.
		if(creator)
			clones.set(null);
	
		return ret;
	}
	
	/**
	 *  Do clone makes a deep clone without regarding cycles.
	 *  Method is overridden by subclasses to actually incorporate their attributes.
	 *  @param theclone The clone.
	 */
	protected abstract void doClone(Object theclone);

	//-------- debugging --------

	/**
	 *  Check the consistency of the node.
	 */
	public boolean	checkNodeConsistency(ReteMemory mem)
	{
		// Mark node as checked (for debugging).
		Object	node	= this;
		while(!(node instanceof ReteNode))
		{
			if(node instanceof IObjectConsumerNode)
			{
				node	= ((IObjectConsumerNode)node).getObjectSource();
			}
			else if(node instanceof ITupleConsumerNode)
			{
				node	= ((ITupleConsumerNode)node).getTupleSource();
			}
			else
			{
				throw new RuntimeException("Unhandled node type: "+node);
			}
		}
		((ReteNode)node).checked.add(this);

		// Empty default implementation.
		return true;
	}
}
