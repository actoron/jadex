package jadex.extension.envsupport.environment;

import java.util.Collection;

import jadex.extension.envsupport.math.IVector2;

/**
 *Importand Interfance
 * connects the Abstract Space with the 3d MonkeyWorld
 * 
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 *
 */
public interface ISpaceController 
{
	public Collection getSpaceObjectsByGridPosition(IVector2 position, Object type);
	
	public Object getProperty(String name);
	
	public void setProperty(String name, Object value);
	
	public ISpaceObject getSpaceObject(Object id);

}
