package jadex.rules.tools.profiler;

import java.io.ObjectStreamException;
import java.io.Serializable;

import jadex.commons.SUtil;


/**
 *  Struct for profiling information.
 */
public class	ProfilingInfo	implements Serializable
{
	//-------- attributes --------
	
	/** The profiling event type. */
	public String	type;

	/** The profiling item. */
	public Object	item;

	/** The parent profiling info. */
	public ProfilingInfo	parent;

	/** The total time. */
	public long time;

	/** The inherent total time. */
	public long inherent;

	//-------- constructors --------
	
	/**
	 *  Create a new profiling info.
	 */
	public ProfilingInfo(String type, Object item, ProfilingInfo parent, long time, long inherent)
	{
		this.type	= type;
		this.item	= item;
		this.parent	= parent;
		this.time	= time;
		this.inherent	= inherent;
	}
	
	//-------- methods --------
	
	/**
	 *  Create a string representation of the profile. 
	 */
	public String	toString()
	{
		return type+"(item="+item+" time="+time+" inherent="+inherent+")";
	}

	/**
	 *  Test, if two nodes are equal.
	 */
	public boolean	equals(Object o)
	{
		boolean	ret	= o instanceof ProfilingInfo;
		if(ret)
		{
			ProfilingInfo	info	= (ProfilingInfo)o;
			ret	= SUtil.equals(type, info.type) && SUtil.equals(item, info.item);
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
	//-------- serialization handling --------
	
	/**
	 *  Close frame before serialization.
	 */
	protected Object    writeReplace() throws ObjectStreamException
	{
		item	= item!=null ? item.toString() : null;
	    return this;
	}
}