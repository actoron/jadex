package jadex.rules.state.javaimpl;

import java.util.LinkedHashMap;
import java.util.Map;

import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVObjectType;

/**
 *  Simplest possible id generatorusing plain objects.
 */
public class OAVObjectIdGenerator implements IOAVIdGenerator
{
	//-------- attributes --------
	
	/** The flag indicating if content ids should be produced. */
	protected boolean iscontentid;
		
	//-------- constructor --------
	
	/**
	 *  Create a new id generator.
	 */
	public OAVObjectIdGenerator()
	{
		this(false);
	}
	
	/**
	 *  Create a new id generator.
	 */
	public OAVObjectIdGenerator(boolean iscontentid)
	{
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
		return iscontentid? new OAVContentId(): new Object();

	}
	
	/**
	 *  Test if an object is an id.
	 *  @param state	The state.
	 *  @param type	The object type.
	 *  @return The new id.
	 */
	public boolean	isId(Object id)
	{
		// note: does not always work.
		return id!=null && (id.getClass()==Object.class || id instanceof OAVContentId);
	}

	//-------- helper classes --------
	
	/**
	 *  An id for an OAV object.
	 */
	private static class OAVContentId implements IOAVContentId
	{
		//-------- attributes --------
		
		/** The content map. */
		protected Map	content;
		
		//-------- constructors --------
		
		/**
		 *  Create an OAV object id with the given id value.
		 *  @param id The id value.
		 */
		public OAVContentId()
		{
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
