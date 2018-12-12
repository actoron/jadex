package jadex.rules.state;

import java.io.ObjectStreamException;
import java.io.Serializable;

import jadex.commons.SUtil;

/**
 *  A profiler stores execution information, such as
 *  time and memory consumption. The application has
 *  to call the start()/stop()   for information
 *  on specific items being collected.
 */
public interface IProfiler
{
	//-------- constants --------
	
	/** The dummy root type (not used for ordering). */
	public static final String	TYPE_ROOT	= "root";
	
	/** The rule type. */
	public static final String	TYPE_RULE	= "rule";
	
	/** The object-type type. */
	public static final String	TYPE_OBJECT	= "object";
	
	/** The object event type (see objectevent_xxx for possible items). */
	public static final String	TYPE_OBJECTEVENT	= "objectevent";
	
	/** The rete-node type. */
	public static final String	TYPE_NODE	= "node";
	
	/** The rete-node event type (see nodeevent_xxx for possible items). */
	public static final String	TYPE_NODEEVENT	= "nodeevent";
	
	/** The object added event item. */
	public static final String	OBJECTEVENT_ADDED	= "StateObjectAdded";

	/** The object removed event item. */
	public static final String	OBJECTEVENT_REMOVED	= "StateObjectRemoved";

	/** The object modified event item. */
	public static final String	OBJECTEVENT_MODIFIED	= "StateObjectModified";

	/** The node object added event item. */
	public static final String	NODEEVENT_OBJECTADDED	= "NodeObjectAdded";

	/** The node object removed event item. */
	public static final String	NODEEVENT_OBJECTREMOVED	= "NodeObjectRemoved";

	/** The node object modified event item. */
	public static final String	NODEEVENT_OBJECTMODIFIED	= "NodeObjectModified";

	/** The node tuple added event item. */
	public static final String	NODEEVENT_TUPLEADDED	= "NodeTupleAdded";

	/** The node tuple removed event item. */
	public static final String	NODEEVENT_TUPLEREMOVED	= "NodeTupleRemoved";

	/** The node tuple modified event item. */
	public static final String	NODEEVENT_TUPLEMODIFIED	= "NodeTupleModified";

	//-------- methods --------
	
	/**
	 *  Start profiling an item. Nesting (i.e. calling start() several times before
	 *  calling corresponding stops) is allowed.
	 *  @param type A constant representing the event or activity type being profiled (e.g. object added).
	 *  @param item The element corresponding to the activity (e.g. the object type).
	 */
	public void	start(String type, Object item);

	/**
	 *  Stop profiling the current item.
	 *  Calls to stop() have match the last unstopped call to start(), with respect to the supplied types and items.
	 *  @param type A constant representing the event or activity type being profiled (e.g. object added).
	 *  @param item The element corresponding to the activity (e.g. the object type).
	 */
	public void	stop(String type, Object item);
	
	/**
	 *  Get the current profiling infos from the given start index.
	 *  @param start	The start index (use 0 for all profiling infos).
	 */
	public ProfilingInfo[] getProfilingInfos(int start);
	
	/**
	 *  Struct for profiling information.
	 */
	public static class	ProfilingInfo	implements Serializable
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
}


