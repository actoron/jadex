package jadex.rules.tools.reteviewer;

import jadex.rules.rulesystem.rete.nodes.INode;

/**
 *  An edge has start node, end node and a type (object or tuple).
 */
public class ReteEdge
{
	//-------- attributes --------
	
	/** The start node. */
	protected INode	start;

	/** The end node. */
	protected INode	end;

	/** Flag indicating, if the edge is an object edge (false) or a tuple edge (true). */
	protected boolean	tuple;
	
	//-------- constructors --------
	
	/**
	 *  Create a new edge.
	 */
	public ReteEdge(INode start, INode end, boolean tuple)
	{
		this.start	= start;
		this.end	= end;
		this.tuple	= tuple;
	}

	//-------- methods -------- 

	/**
	 *  Return the start node of this edge. 
	 */
	public INode getStart()
	{
		return start;
	}

	/**
	 *  Return the end node of this edge. 
	 */
	public INode getEnd()
	{
		return end;
	}

	/**
	 *  Check, if this edge is a tuple edge (or an object edge). 
	 */
	public boolean isTuple()
	{
		return tuple;
	}

	/**
	 *  Hash code of the edge.
	 */
	public int hashCode()
	{
		int result = 31 + ((end == null) ? 0 : end.hashCode());
		result = 31 * result + ((start == null) ? 0 : start.hashCode());
		result = 31 * result + (tuple ? 1231 : 1237);
		return result;
	}

	/**
	 *  Check if this edge equals a given object.
	 */
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;

		boolean	ret = obj instanceof ReteEdge;
		if(ret)
		{
			final ReteEdge edge = (ReteEdge)obj;
			ret	= start.equals(edge.start)
				&& end.equals(edge.end)
				&& tuple==edge.tuple;
		}
		return ret;
	}
	
	/**
	 *  Return a string representation
	 */
	public String	toString()
	{
		return "ReteEdge("+start+", "+end+", tuple="+tuple+")";
	}
}