package jadex.xml.reader;

/* if_not[android] */
import javax.xml.namespace.QName;
/* else[android]
import javaxx.xml.namespace.QName;
end[android] */

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
	 */
	public void linkObject(Object object, Object parent, Object linkinfo, 
		QName[] pathname, ReadContext context) throws Exception;
}
