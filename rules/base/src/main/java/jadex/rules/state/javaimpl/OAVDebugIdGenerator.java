package jadex.rules.state.javaimpl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVObjectType;

/**
 *  Id generator displaying contents of objects for debugging.
 */
public class OAVDebugIdGenerator implements IOAVIdGenerator
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
	public OAVDebugIdGenerator()
	{
		this(false);
	}
	
	/**
	 *  Create a new id generator.
	 */
	public OAVDebugIdGenerator(boolean iscontentid)
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
		Object ret = iscontentid? new OAVContentId(state, type, id++): new OAVObjectId(state, type, id++);

		if(state.containsObject(ret))
			System.out.println("Warning, id overflow.");
		
		while(state.containsObject(ret) && id!=start)
		{
			ret = new OAVObjectId(state, type, id++);
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
		protected IOAVState state;
		
		/** The object type. */
		protected OAVObjectType type;

		/** The id value. */
		protected long	id;
		
		//-------- constructors --------
		
		/**
		 *  Create an OAV object id with the given id value.
		 *  @param id The id value.
		 */
		public OAVObjectId(IOAVState state, OAVObjectType type, long id)
		{
			this.state	= state;
			this.type	= type;
			this.id	= id;
		}
	
		//-------- methods --------
		
		/**
		 *  Create a string representation of this OAV object id.
		 */
		public String	toString()
		{
			StringBuffer	buf	= new StringBuffer();
			buf.append(type.getName());
			buf.append("_");
			buf.append(id);
			buf.append("@");
			buf.append(state.hashCode());

//			buf.append("(");
//			OAVObjectType	tmptype	= type;
//			boolean	first	= true;
//			while(tmptype!=null)
//			{
//				for(Iterator attrs=tmptype.getDeclaredAttributeTypes().iterator(); attrs.hasNext(); )
//				{
//					OAVAttributeType	attr	= (OAVAttributeType)attrs.next();
//					if(OAVAttributeType.NONE.equals(attr.getMultiplicity()))
//					{
//						Object	value	= state.getAttributeValue(this, attr);
//						if(value!=null)
//						{
//							if(!first)
//							{
//								buf.append(", ");
//							}
//							else
//							{
//								first	= false;
//							}
//							
//							buf.append(attr.getName());
//							buf.append("=");
//							if(value instanceof OAVObjectId)
//							{
//								buf.append(((OAVObjectId)value).type.getName());								
//								buf.append("_");								
//								buf.append(((OAVObjectId)value).id);								
//							}
//							else
//							{
//								buf.append(value);								
//							}
//						}
//					}
//				}
//				tmptype	= tmptype.getSupertype();
//			}
//			buf.append(")");
			
			return buf.toString();
		}
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
		public OAVContentId(IOAVState state, OAVObjectType type, long id)
		{
			super(state, type, id);
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
