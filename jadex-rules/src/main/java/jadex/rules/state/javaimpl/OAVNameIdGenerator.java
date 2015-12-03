package jadex.rules.state.javaimpl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVObjectType;

/**
 *  Id generator using easy to read names and numbers.
 */
public class OAVNameIdGenerator implements IOAVIdGenerator
{
	//-------- attributes -------- 
	
	/** The id counter map (type -> count). */
	protected Map	counters;
	
	/** The flag indicating if content ids should be produced. */
	protected boolean iscontentid;
	
	//-------- constructors --------

	/**
	 *  Create a new id generator.
	 */
	public OAVNameIdGenerator()
	{
		this(false);
	}
	
	/**
	 *  Create a new id generator.
	 */
	public OAVNameIdGenerator(boolean iscontentid)
	{
		this.counters	= new HashMap();
		this.iscontentid = iscontentid;
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
		Object ret = iscontentid? new OAVContentId(type, id++): new OAVObjectId(type, id++);

		if(state.containsObject(ret))
			System.out.println("Warning, id overflow.");
		
		while(state.containsObject(ret) && id!=start)
		{
			ret = new OAVObjectId(/*state,*/ type, id++);
		}
		
		if(state.containsObject(ret))
			throw new RuntimeException("No free id available.");
		
		counters.put(type, Long.valueOf(id));
		
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
		return id!=null && id instanceof OAVObjectId;
	}
		
	//-------- helper classes --------
	
	/**
	 *  An id for an OAV object.
	 */
	private static class OAVObjectId
	{
		//-------- attributes --------
		
		/** The state. */
//		protected IOAVState state;
		
		/** The object type. */
		protected OAVObjectType type;

		/** The id value. */
		protected long	id;
		
		//-------- constructors --------
		
		/**
		 *  Create an OAV object id with the given id value.
		 *  @param id The id value.
		 */
		public OAVObjectId(/*IOAVState state,*/ OAVObjectType type, long id)
		{
//			this.state	= state;
			this.type	= type;
			this.id	= id;
		}
	
		//-------- methods --------
		
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
		 * /
		public boolean equals(Object object)
		{
			boolean	ret	= object instanceof OAVObjectId;
			if(ret)
			{
				OAVObjectId	other	= (OAVObjectId)object;
				ret	= this.id==other.id
					&& this.state.equals(other.state)
					&& this.type.equals(((OAVObjectId)object).type);
			}
			return ret;
		}*/
	
		/**
		 *  Return the hashcode for this id.
		 *  @return The hashcode for this id.
		 * /
		public int	hashCode()
		{
			int	ret	= 31 + (int)(id ^ (id >>> 32));
			ret	= 31 * ret + type.hashCode();
			return ret;
		}*/
	}
	
	/**
	 *  An id for an OAV object.
	 */
	private static class OAVContentId extends OAVObjectId implements IOAVContentId
	{
		//-------- attributes --------
		
		/** The content map. */
		protected Map	content;
		
		//-------- constructors --------
		
		/**
		 *  Create an OAV object id with the given id value.
		 *  @param id The id value.
		 */
		public OAVContentId(OAVObjectType type, long id)
		{
			super(type, id);
			this.content = new LinkedHashMap();
		}
	
		//-------- methods --------
		
		/**
		 *  Get the content.
		 *  @return The content.
		 */
		public Map getContent()
		{
			return content;
		}
	}
}
