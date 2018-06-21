package jadex.rules.state.javaimpl;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

// #ifndef MIDP

import jadex.commons.collection.WeakEntry;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVObjectType;

/**
 *  Id generator using easy to read names and numbers.
 */
public class OAVWeakIdGenerator implements IOAVIdGenerator
{
	//-------- attributes -------- 
	
	/** The id counter map (type -> count). */
	protected Map	counters;
	
	/** The reference queue for stale external ids. */
	protected ReferenceQueue queue;
	
	//-------- constructors --------

	/**
	 *  Create a new id generator.
	 */
	public OAVWeakIdGenerator(ReferenceQueue queue)
	{
		this.counters	= new HashMap();
		this.queue = queue;
	}
	
	/**
	 *  Create a unique id.
	 *  @param state	The state.
	 *  @param type	The object type.
	 *  @return The new id.
	 */
	public Object createId(IOAVState state, OAVObjectType type)
	{
		long start	= 1;
		if(counters.containsKey(type))
		{
			start	= ((Long)counters.get(type)).longValue();
		}
		
		long	id	= start;
		OAVInternalObjectId ret = new OAVInternalObjectId(type, id++, queue);

		if(state.containsObject(ret))
			System.out.println("Warning, id overflow.");
		
		while(state.containsObject(ret) && id!=start)
		{
			ret = new OAVInternalObjectId(type, id++, queue);
		}
		
		if(state.containsObject(ret))
			throw new RuntimeException("No free id available.");
		
		counters.put(type, Long.valueOf(id));
		
//		System.out.println("generated: "+ret);
		return ret;
	}
	
	/**
	 *  Test if an object is an id.
	 *  @param state	The state.
	 *  @param type	The object type.
	 *  @return The new id.
	 */
	public boolean	isId(Object id)
	{
		return id!=null && (id instanceof OAVInternalObjectId || id instanceof OAVExternalObjectId);
	}
	
	//-------- helper classes --------
	
	/**
	 *  An id for an OAV object.
	 */
	public static class OAVInternalObjectId
	{
		//-------- attributes --------
		
		/** The object type. */
		protected OAVObjectType type;

		/** The id value. */
		protected long	id;
		
		/** The weak reference to the external id. */
		protected WeakReference extid;

		/** The weak reference to the phantom external id
		 * (e.g. used in rete memory, but doesn't prevent garbage collection). */
		protected WeakReference extid2;
		
		/** The reference queue. */
		protected ReferenceQueue queue;
		
		//-------- constructors --------
		
		/**
		 *  Create an OAV object id with the given id value.
		 *  @param id The id value.
		 */
		OAVInternalObjectId(OAVObjectType type, long id, ReferenceQueue queue)
		{
			this.type	= type;
			this.id	= id;
			this.queue = queue;
		}
	
		//-------- methods --------
		
		
		/**
		 *  Get the weak external id.
		 */
		public OAVExternalObjectId getWeakExternalId()
		{
			OAVExternalObjectId ret = extid==null? null: (OAVExternalObjectId)extid.get();
			if(ret==null)
			{
				ret = new OAVExternalObjectId(this);
				extid = new WeakEntry(ret, this, queue);
			}
			return ret;
		}
		
		/**
		 *  Get the phantom external id (e.g. used in events).
		 */
		public OAVExternalObjectId getPhantomExternalId()
		{
			OAVExternalObjectId ret = extid2==null? null: (OAVExternalObjectId)extid2.get();
			if(ret==null)
			{
				ret = new OAVExternalObjectId(this);
				extid2 = new WeakReference(ret);
			}
			return ret;
		}
		
		/**
		 *  Test if no external references exist.
		 *  @return True, if no exist.
		 */
		public boolean isClear()
		{
			return (extid==null? null: (OAVExternalObjectId)extid.get())==null;
		}
		
		/**
		 *  Create a string representation of this OAV object id.
		 */
		public String	toString()
		{
			return type.getName()+"_"+id;
		}
		
		/**
		 *  Test if two object are equal.
		 *  @param object The object to compare to.
		 */
		public boolean equals(Object object)
		{
			return object instanceof OAVInternalObjectId
				&& this.id==((OAVInternalObjectId)object).id
				&& this.type.equals(((OAVInternalObjectId)object).type);
		}
	
		/**
		 *  Return the hashcode for this id.
		 *  @return The hashcode for this id.
		 */
		public int	hashCode()
		{
			int	ret	= 31 + (int)(id ^ (id >>> 32));
			ret	= 31 * ret + type.hashCode();
			return ret;
		}
	}
	
	/**
	 *  An id for an OAV object.
	 */
	public static class OAVExternalObjectId
	{
		//-------- attributes --------
		
		/** The object type. */
		protected OAVInternalObjectId id;

		//-------- constructors --------
		
		/**
		 *  Create an OAV object id with the given id value.
		 *  @param id The id value.
		 */
		private OAVExternalObjectId(OAVInternalObjectId id)
		{
			this.id = id;
		}
	
		//-------- methods --------
		
		/**
		 *  Get the internal id.
		 */
		public OAVInternalObjectId getInternalId()
		{
			return id;
		}
		
		/**
		 *  Create a string representation of this OAV object id.
		 */
		public String	toString()
		{
			return "extid_"+id.toString();
		}
		
		/**
		 *  Test if two object are equal.
		 *  @param object The object to compare to.
		 */
		public boolean equals(Object object)
		{
			return object!=null && object.equals(id); // Order is important!
		}
	
		/**
		 *  Return the hashcode for this id.
		 *  @return The hashcode for this id.
		 */
		public int	hashCode()
		{
			return id.hashCode();
		}
	}
}
// #endif
