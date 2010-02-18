package jadex.xml.reader;

import jadex.xml.IContext;

import java.util.List;

/**
 * 
 */
public interface IBulkObjectLinker
{
	/**
	 *  Bulk link an object to its parent.
	 *  @param parent The parent object.
	 *  @param children The children objects (link datas).
	 *  @param context The context.
	 *  @param classloader The classloader.
	 *  @param root The root object.
	 */
	public void bulkLinkObjects(Object parent, List children, ReadContext context) throws Exception;
}
