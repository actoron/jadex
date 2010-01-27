package jadex.xml.reader;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * 
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
	 */
	public void linkObject(Object object, Object parent, Object linkinfo, QName[] pathname, 
		Object context, ClassLoader classloader, Object root) throws Exception;
}
