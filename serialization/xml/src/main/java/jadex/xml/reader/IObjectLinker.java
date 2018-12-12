package jadex.xml.reader;

import jadex.xml.stax.QName;

/**
 *  Interface for sequential linker. 
 */
public interface IObjectLinker
{
	/**
	 *  Link an object to its parent.
	 *  @param object The object.
	 *  @param parent The parent object.
	 *  @param linkinfo The link info.
	 *  @param tagname The current tagname (for name guessing).
	 *  @param context The context.
	 *  //@return boolean True, if linker has linked the objects.
	 */
	public void linkObject(Object object, Object parent, Object linkinfo, 
		QName[] pathname, AReadContext context) throws Exception;
}
