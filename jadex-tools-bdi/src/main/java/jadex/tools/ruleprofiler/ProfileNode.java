package jadex.tools.ruleprofiler;

import jadex.commons.SUtil;
import jadex.commons.gui.jtreetable.TreeTableNode;
import jadex.rules.state.IProfiler.ProfilingInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;


/**
 *  A node representing an entry for the profile tree.
 */
public class ProfileNode	implements TreeTableNode
{
	//-------- attributes --------
	
	/** The parent of this node. */
	protected ProfileNode	parent;
	
	/** The event type of this node. */
	protected String	type;
	
	/** The item (if any) of this node. */
	protected Object	item;
	
	/** The total time (sum of all profiling infos for this node). */
	protected long	time;
	
	/** The total inherent time (sum of all profiling infos for this node). */
	protected long	inherent;
	
	/** The total number of occurrences (sum of all profiling infos for this node). */
	protected int	occurrences;
	
	/** The subnodes of this node. */
	protected List	subnodes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new profile node.  
	 */
	public ProfileNode(ProfileNode parent, String type, Object item)
	{
		this.parent	= parent;
		this.type	= type;
		this.item	= item;
		this.time	= 0;
		this.inherent	= 0;
		this.occurrences	= 0;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the type.
	 */
	public String	getType()
	{
		return type;
	}	
	/**
	 *  Get the item.
	 */
	public Object	getItem()
	{
		return item;
	}
	
	/**
	 *  Add a subnode.
	 */
	public void	addSubnode(ProfileNode node)
	{
		if(subnodes==null)
			subnodes	= new ArrayList();
		subnodes.add(node);
	}
	
	/**
	 *  Accumulate profiling info.
	 */
	public void	accumulate(ProfilingInfo info)
	{
		if(SUtil.equals(info.type, type) && SUtil.equals(info.item, item))
		{
			this.time	+= info.time;
			this.inherent	+= info.inherent;
			this.occurrences++;
		}
		
		// Hack!!! For root node sum up times/occurrences of 1st level nodes.
		else if(parent==null && info.parent==null)
		{
			this.time	+= info.time;
			this.occurrences++;
		}
	}
	
	/**
	 *  Recursively sort all subnodes
	 *  by the given comparator.
	 */
	public void	sort(Comparator comp)
	{
		if(subnodes!=null)
		{
			Collections.sort(subnodes, comp);
			for(int i=0; i<subnodes.size(); i++)
				((ProfileNode)subnodes.get(i)).sort(comp);
		}
	}
	
	/**
	 *  Create a string representation of this node.
	 */
	public String	toString()
	{
		StringBuffer	ret	= new StringBuffer();
		ret.append(type);
		ret.append("(");
		ret.append(item);
		ret.append(")");
		return ret.toString();
	}
	
	/**
	 *  Test, if two nodes are equal.
	 */
	public boolean	equals(Object o)
	{
		boolean	ret	= o instanceof ProfileNode;
		if(ret)
		{
			ProfileNode	node	= (ProfileNode)o;
			ret	= SUtil.equals(type, node.getType()) && SUtil.equals(item, node.getItem());
		}
		return ret;
	}

	/**
	 *  Test, if two nodes are equal.
	 */
	public int	hashCode()
	{
		int	ret	= 1;
		ret	= ret*31 + (type!=null ? type.hashCode() : 0);
		ret	= ret*31 + (item!=null ? item.hashCode() : 0);

		return ret;
	}
	
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
				TreeNode	root	= this;
				while(root.getParent()!=null)
					root	= root.getParent();
				long	total	= ((ProfileNode)root).time;
				return toTimeString(time, total);
			case	2:
				root	= this;
				while(root.getParent()!=null)
					root	= root.getParent();
				total	= ((ProfileNode)root).time;
				return toTimeString(inherent, total);
			case	3:
				return ""+occurrences;
		}
		throw new IllegalArgumentException("No such column: "+column);
	}
	
	/**
	 *  Get a string representing the time.
	 *  Includes absolute and relative values.
	 */
	protected static String	toTimeString(long time, long total)
	{
		String	rel	= Double.toString(time*100.0/total);
		int	dot	= rel.indexOf('.');
		if(dot!=-1)
			rel	= rel.substring(0, dot+2);
		
		String	timestring;
		if(time<1000)
			timestring	= time + " ns";
		else if(time<100000)
			timestring	= (time/100)/10.0 + " µs";
		else if(time<1000000)
			timestring	= time/1000 + " µs";
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

}
