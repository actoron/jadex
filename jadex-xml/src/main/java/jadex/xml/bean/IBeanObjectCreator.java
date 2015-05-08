package jadex.xml.bean;

import jadex.xml.IContext;

import java.util.Map;

/**
 *  Interface for creator objects that can create different 
 *  kinds of objects according to the xml attributes.
 */
public interface IBeanObjectCreator
{
	/**
	 *  Create an object.
	 *  @param context The context.
	 *  @param rawattributes The raw attributes.
	 *  @return The created object (or null for none).
	 */
	public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception;
}
