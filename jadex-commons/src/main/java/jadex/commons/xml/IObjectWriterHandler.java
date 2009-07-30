package jadex.commons.xml;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public interface IObjectWriterHandler
{
	/**
	 *  Get all subobjects of an object.
	 */
	public Object[] getAttributesAndSubobjects(Object object, TypeInfo typeinfo);

	/**
	 *  Get the tag for an object.
	 */
	public String getTag(Object object, TypeInfo typeinfo);
}
