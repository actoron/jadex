package jadex.tools.ruleprofiler;

import jadex.rules.state.IProfiler.ProfilingInfo;
import jadex.tools.common.jtreetable.TreeTableNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;


/**
 *  A node representing an entry for the profile tree.
 */
public class CopyOfProfileNode	implements TreeTableNode
{
	//-------- attributes --------
	
	/** The parent of this node. */
	protected CopyOfProfileNode parent;
	
	/** The type of this node. */
	protected String type;
	
	/** The ordering of node types used in the current node set. */
	// Required for correct equals implementation.
	protected String[]	ordering;
	
	/** A characterizing profiling info for this node. */
	protected ProfilingInfo	info;
	
	/** The total time (sum of all profiling infos, i.e. subnodes, for this node). */
	protected long	time;
	
	/** The total number of occurrences (sum of all profiling infos, i.e. subnodes, for this node). */
	protected int	occurrences;
	
	/** The subnodes of this node. */
	protected List	subnodes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new profile node.  
	 */
	public CopyOfProfileNode(CopyOfProfileNode parent, String type, String[] ordering, ProfilingInfo info)
	{
		this.parent	= parent;
		this.type	= type;
		this.ordering	= ordering;
		this.info	= info;
		this.time	= 0;
		this.occurrences	= 0;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the ordering.
	 */
	public String[]	getOrdering()
	{
		return ordering;
	}
	
	/**
	 *  Get the type.
	 */
	public String	getType()
	{
		return type;
	}
	
	/**
	 *  Add a subnode.
	 */
	public void	addSubnode(CopyOfProfileNode node)
	{
		if(subnodes==null)
			subnodes	= new ArrayList();
		subnodes.add(node);
	}
	
	/**
	 *  Accumulate profiling info.
	 * /
	public void	accumulate(ProfilingInfo info)
	{
		this.time	+= info.time;
		
		// Count all occurrences, but only count first occurrences of rules.
		if(info.first || !IProfiler.TYPE_RULE.equals(type) || !IProfiler.TYPE_RULE.equals(ordering[0]))
			occurrences++;
	}*/
	
	/**
	 *  Recursively sort all subnodes
	 *  by the given comparator.
	 */
	// Todo: support other sort criteria
	public void	sort(Comparator comp)
	{
		if(subnodes!=null)
		{
			Collections.sort(subnodes, comp);
			for(int i=0; i<subnodes.size(); i++)
				((CopyOfProfileNode)subnodes.get(i)).sort(comp);
		}
	}
	
	/**
	 *  Create a string representation of this node.
	 * /
	public String	toString()
	{
		String	ret	= "";
		if(IProfiler.TYPE_RULE.equals(type))
		{
			ret	= info.rule!=null ? info.rule.getName() : "<external>";
		}
		else if(IProfiler.TYPE_OBJECT.equals(type))
		{
			ret	= info.otype.getName();
		}
		else if(IProfiler.TYPE_OBJECTEVENT.equals(type))
		{
			ret	= info.event;
//			if(IProfiler.OBJECT_MODIFIED.equals(info.profilingtype))
//				ret	+= "["+info.attrtype.getName()+"]";
		}
		
//		ret	+= " (time="+time+", occurrences="+occurrences+")";
		return ret;
	}*/
	
	/**
	 *  Test, if two nodes are equal.
	 * /
	public boolean	equals(Object o)
	{
		boolean	ret	= o instanceof CopyOfProfileNode;
		if(ret)
		{
			// Root node is treated like object.
			if(IProfiler.TYPE_ROOT.equals(this.type))
				return super.equals(o);
			
			CopyOfProfileNode	node	= (CopyOfProfileNode)o;
			ret	= Arrays.equals(ordering, node.ordering);
			for(int i=0; ret && i<ordering.length; i++)
			{
				if(IProfiler.TYPE_RULE.equals(ordering[i]))
				{
					ret	= SUtil.equals(info.rule, node.info.rule);
				}
				else if(IProfiler.TYPE_OBJECT.equals(ordering[i]))
				{
					ret	= SUtil.equals(info.otype, node.info.otype);
				}
				else if(IProfiler.TYPE_OBJECTEVENT.equals(ordering[i]))
				{
					ret	= SUtil.equals(info.event, node.info.event)
						&& SUtil.equals(info.attrtype, node.info.attrtype);
				}
				else //if(IProfiler.TYPE_NODE.equals(ordering[i]))
				{
					throw new UnsupportedOperationException("Ordering of node type not supported: "+ordering[i]);
				}

				// Do not check parts ordered below the node type
				// i.e. if ordering is {rule, object} a rule node
				// would only compare the rules, but an object node
				// would compare both.
				if(type.equals(ordering[i]))
					break;
			}
		}
		return ret;
	}*/

	/**
	 *  Test, if two nodes are equal.
	 * /
	public int	hashCode()
	{
		// Root node is treated like object.
		if(IProfiler.TYPE_ROOT.equals(this.type))
			return super.hashCode();
			
		int	ret	= 1;

		for(int i=0; i<ordering.length; i++)
		{
			if(IProfiler.TYPE_RULE.equals(ordering[i]))
			{
				ret	= ret*31 + (info.rule!=null ? info.rule.hashCode() : 0);
			}
			else if(IProfiler.TYPE_OBJECT.equals(ordering[i]))
			{
				ret	= ret*31 + info.otype.hashCode();
			}
			else if(IProfiler.TYPE_OBJECTEVENT.equals(ordering[i]))
			{
				ret	= ret*31 + info.event.hashCode();
				ret	= ret*31 + (info.attrtype!=null ? info.attrtype.hashCode() : 0);
			}
			else if(IProfiler.TYPE_NODE.equals(ordering[i]))
			{
				ret	= ret*31 + info.event.hashCode();
				ret	= ret*31 + (info.attrtype!=null ? info.attrtype.hashCode() : 0);
			}
			else //if(IProfiler.TYPE_NODE.equals(ordering[i]))
			{
				throw new UnsupportedOperationException("Ordering type not supported: "+ordering[i]);
			}
	
			// Do not include parts ordered below the node type
			// i.e. if ordering is {rule, object} a rule node
			// would only include the rule hashcode, but an object node
			// would include rule and object hashcodes.
			if(type.equals(ordering[i]))
				break;
		}

		return ret;
	}*/
	
	//-------- TreeNode interface --------

	/**
	 *  Returns the child <code>TreeNode</code> at index 
	 *  <code>index</code>.
	 */
	public TreeNode	getChildAt(int index)
	{
		return (TreeNode)subnodes.get(index);
	}
	
	/**
	 *  Returns the number of children <code>TreeNode</code>s the receiver
	 *  contains.
	 */
	public int	getChildCount()
	{
		return subnodes!=null ? subnodes.size() : 0;
	}
	
	/**
	 *  Returns the parent <code>TreeNode</code> of the receiver.
	 */
	public TreeNode	getParent()
	{
		return parent;
	}
	
	/**
	 *  Returns the index of <code>node</code> in the receivers children.
	 *  If the receiver does not contain <code>node</code>, -1 will be
	 *  returned.
	 */
	public int	getIndex(TreeNode node)
	{
		return subnodes!=null ? subnodes.indexOf(node) : -1;
	}
	
	/**
	 *  Returns true if the receiver allows children.
	 */
	public boolean	getAllowsChildren()
	{
		return true;
	}
	
	/**
	 *  Returns true if the receiver is a leaf.
	 */
	public boolean	isLeaf()
	{
		return subnodes==null;
	}
	
	/**
	 *  Returns the children of the receiver as an <code>Enumeration</code>.
	 */
	public Enumeration	children()
	{
		return Collections.enumeration(subnodes);
	}

	//-------- TreeTableNode interface --------

	/**
	 *  Get the value at a specific column.
	 */
	public Object	getValue(int column)
	{
		switch(column)
		{
			case	0:
				return toString();
			case	1:
				return getTimeString();
			case	2:
				return ""+occurrences;
//			case	3:
//				return getRelativeTimeString();
		}
		throw new IllegalArgumentException("No such column: "+column);
	}
	
	/**
	 *  Get a string representing the time.
	 *  Includes absolute and relative values.
	 */
	protected String	getTimeString()
	{
		TreeNode	root	= this;
		while(root.getParent()!=null)
			root	= root.getParent();
		long	total	= ((CopyOfProfileNode)root).time;

		String	rel	= Double.toString(time*100.0/total);
		int	dot	= rel.indexOf('.');
		if(dot!=-1)
			rel	= rel.substring(0, dot+2);
		
		String	timestring;
		if(time<1000)
			timestring	= time + " ns";
		else if(time<100000)
			timestring	= (time/100)/10.0 + " 탎";
		else if(time<1000000)
			timestring	= time/1000 + " 탎";
		else if(time<100000000)
			timestring	= (time/100000)/10.0 + " ms";
		else if(time<1000000000)
			timestring	= time/1000000 + " ms";
		else if(time<100000000000L)
			timestring	= (time/100000000)/10.0 + " s";
		else //if(time<1000000000000L)
			timestring	= time/1000000000 + " s";
		
		return timestring + " ("+rel+"%)";
	}

//	/**
//	 *  Get a string representing the relative time (time per occurrence).
//	 *  Includes absolute and relative values.
//	 */
//	protected String	getRelativeTimeString()
//	{
//		TreeNode	root	= this;
//		while(root.getParent()!=null)
//			root	= root.getParent();
//		long	totaltime	= ((ProfileNode)root).time;
//		long	totalocc	= ((ProfileNode)root).occurrences;
//
//		String	rel	= Double.toString(time*100.0/totaltime);
//		int	dot	= rel.indexOf('.');
//		if(dot!=-1)
//			rel	= rel.substring(0, dot+2);
//		
//		String	timestring;
//		if(time<1000)
//			timestring	= time + " ns";
//		else if(time<100000)
//			timestring	= (time/100)/10.0 + " 탎";
//		else if(time<1000000)
//			timestring	= time/1000 + " 탎";
//		else if(time<100000000)
//			timestring	= (time/100000)/10.0 + " ms";
//		else if(time<1000000000)
//			timestring	= time/1000000 + " ms";
//		else if(time<100000000000L)
//			timestring	= (time/100000000)/10.0 + " s";
//		else //if(time<1000000000000L)
//			timestring	= time/1000000000 + " s";
//		
//		return timestring + " ("+rel+"%)";
//	}
}
