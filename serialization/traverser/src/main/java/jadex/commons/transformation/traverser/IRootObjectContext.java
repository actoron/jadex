package jadex.commons.transformation.traverser;

/**
 *  Context containing the origina root object.
 *
 */
public interface IRootObjectContext extends IUserContextContainer
{
	/**
	 *  Get the rootobject.
	 *  @return the rootobject.
	 */
	public Object getRootObject();
}
