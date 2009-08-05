package jadex.commons.xml.bean;

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
	public Object createObject(Object context, Map rawattributes, ClassLoader classloader) throws Exception;
}
