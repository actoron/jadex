package jadex.commons.xml;

/**
 * 
 */
public interface IObjectWriterHandler
{
	/**
	 *  Get all subobjects of an object.
	 */
	public Object[] getAttributesContentAndSubobjects(Object object, TypeInfo typeinfo);

}
