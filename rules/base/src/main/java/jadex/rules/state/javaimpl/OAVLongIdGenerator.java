package jadex.rules.state.javaimpl;

import java.util.LinkedHashMap;
import java.util.Map;

import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVObjectType;

/**
 *  Id generator based on long values.
 */
public class OAVLongIdGenerator implements IOAVIdGenerator
{
	//-------- attributes --------
	
	/** The id counter. */
	protected long	nextid;
	
	/** The flag indicating if content ids should be produced. */
	protected boolean iscontentid;
		
	//-------- constructor --------
	
	/**
	 *  Create a new id generator.
	 */
	public OAVLongIdGenerator()
	{
		this(false);
	}
	
	/**
	 *  Create a new id generator.
	 */
	public OAVLongIdGenerator(boolean iscontentid)
	{
		nextid = Long.MIN_VALUE;
		this.iscontentid = iscontentid;
	}
	
	//-------- methods --------

	/**
	 *  Create a unique id.
	 *  @param state	The state.
	 *  @param type	The object type.
	 *  @return The new id.
	 */
	public Object	createId(IOAVState state, OAVObjectType type)
	{
		long start = nextid;
		Object ret = iscontentid? new OAVContentId(nextid++): new OAVObjectId(nextid++);

		if(state.containsObject(ret))
			System.out.println("Warning, id overflow.");
		
		while(state.containsObject(ret) && nextid!=start)
			ret = new OAVObjectId(nextid++);
		
		if(state.containsObject(ret))
			throw new RuntimeException("No free id available.");
		
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
		
	/**
	 *  Main for testing.
	 *  @param args
	 */
	public static void main(String[] args)
	{
		IOAVState state = OAVStateFactory.createOAVState(null);
		OAVLongIdGenerator gen = new OAVLongIdGenerator();
		for(long i=0;; i++)
		{
			Object id = gen.createId(state, null);
			if(i%1000000==0)
				System.out.println(i+": "+id);
		}
	}

	//-------- helper classes --------
	
	/**
	 *  An id for an OAV object.
	 */
	private static class OAVObjectId
	{
		//-------- attributes --------
		
		/** The id value. */
		protected long	id;
		
		//-------- constructors --------
		
		/**
		 *  Create an OAV object id with the given id value.
		 *  @param id The id value.
		 */
		public OAVObjectId(long id)
		{
			this.id	= id;
		}
	
		//-------- methods --------
		
		/**
		 *  Create a string representation of this OAV object id.
		 */
		public String	toString()
		{
			return "OAVObjectId("+id+")";
		}
		
		/**
		 *  Test if two id's are equal.
		 *  @param id The OAV object id to compare to.
		 * /
		public boolean equals(Object id)
		{
			return id instanceof OAVObjectId && this.id==((OAVObjectId)id).id;
		}*/
	
		/**
		 *  Return the hashcode for this id.
		 *  @return The hashcode for this id.
		 * /
		public int	hashCode()
		{
			// Taken from java.lang.Long
			// Todo: is this useful?
			return (int)(id ^ (id >>> 32));
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
		public OAVContentId(long id)
		{
			super(id);
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
