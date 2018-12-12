package jadex.xml.reader;

import java.util.List;

/**
 *  Interface for bulk linker.
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
	public void bulkLinkObjects(Object parent, List<LinkData> children, AReadContext context) throws Exception;
}
