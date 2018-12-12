package jadex.rules.state.javaimpl;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jadex.rules.state.OAVTypeModel;

/**
 *  An object holding the state as
 *  OAV triples (object, attribute, value).
 */
public class OAVContentIdState	extends OAVAbstractState
{
	//-------- attributes --------
	
	/** The objects table. */
	protected Set objects;
	
	//-------- constructors --------
	
	/**
	 *  Create a new empty OAV state representation.
	 */
	public OAVContentIdState(OAVTypeModel tmodel)
	{
		super(tmodel);
		this.objects = new LinkedHashSet();
	}
	
	/**
	 *  Create an id generator.
	 *  @return The id generator.
	 */
	public IOAVIdGenerator createIdGenerator()
	{
//		return new OAVLongIdGenerator(true);
//		return new OAVNameIdGenerator(true);
		return new OAVDebugIdGenerator(true);
//		return new OAVObjectIdGenerator(true);
	}
	
	//-------- object management --------
	
	/**
	 *  Add an external usage of a state object (oid). This prevents
	 *  the oav object of being garbage collected as long
	 *  as external references are present.
	 *  @param id The oav object id.
	 *  @param external The user object.
	 */
	public void addExternalObjectUsage(Object id, Object external)
	{
		// #ifndef MIDP
		if(!generator.isId(id))
			System.out.println("driss: "+id);
		assert nocheck || generator.isId(id);
		// #endif
	}
	
	/**
	 *  Remove an external usage of a state object (oid). This allows
	 *  the oav object of being garbage collected when no
	 *  further external references and no internal references
	 *  are present.
	 *  @param id The oav object id.
	 *  @param external The state external object.
	 */
	public void removeExternalObjectUsage(Object id, Object external)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif
	}
	
	/**
	 *  Test if an object is externally used.
	 *  @param id The id.
	 *  @return True, if externally used.
	 */
	protected boolean isExternallyUsed(Object id)
	{
		return false;
	}
	
	//-------- internal object handling --------
	
	/**
	 *  Internally create an object.
	 *  @param id The id.
	 *  @return The content map of the new object.
	 */
	protected Map internalCreateObject(Object id)
	{
		objects.add(id);
		return ((IOAVContentId)id).getContent();
	}
	
	/**
	 *  Remove an object from the state objects.
	 *  @param id The id.
	 *  @return The content map of the object.
	 */
	protected Map internalRemoveObject(Object id)
	{
		objects.remove(id);
		return ((IOAVContentId)id).getContent();
	}
	
	/**
	 *  Get the object content of an object.
	 *  @param id The id.
	 *  @return The content map of the object.
	 */
	protected Map internalGetObjectContent(Object id)
	{
		return ((IOAVContentId)id).getContent();
	}
	
	/**
	 *  Test if an object is contained in the state.
	 *  @param id The id.
	 *  @return True, if object is contained.
	 */
	protected boolean internalContainsObject(Object id)
	{
		return objects.contains(id);
	}
	
	/**
	 *  Test how many object are contained in the state.
	 *  @return The number of objects.
	 */
	protected int internalObjectsSize()
	{
		return objects.size();
	}
	
	/**
	 *  Get a set of the internal state objects.
	 *  @return A set of the state objects. 
	 */
	protected Set internalGetObjects()
	{
		return objects;
	}

	/**
	 *  Test if reading the object (oid) is allowed.
	 *  Reading is allowed on removed objects as long as there are external references.
	 *  @param id The object (oid).
	 *  @return True, if valid.
	 */
	protected boolean checkValidStateObjectRead(Object id)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif

		return true;	// Hack!!! Needed, because isExternallyUsed always returns false.
	}
}