package jadex.javaparser;

/**
 *  Simple map like access for java objects in the parser.
 *  Allows for using abbreviated expressions like $object.a
 *  which is translated to $object.get(a)
 */
public interface IMapAccess
{
	/**
	 *  Get an object from the map.
	 *  @param key The key
	 *  @return The value.
	 */
	public Object get(Object key);
}
