package jadex.extension.envsupport;

import java.util.Map;

/**
 * 
 */
public interface IObjectCreator
{
	/**
	 * @throws Exception TODO
	 * 
	 */
	public Object createObject(Map args) throws Exception;
}
