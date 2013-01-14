package jadex.extension.envsupport.environment;

import jadex.extension.envsupport.math.IVector2;

import java.util.Collection;

public interface ISpaceController 
{
	public Collection getSpaceObjectsByGridPosition(IVector2 position, Object type);
	
	public Object getProperty(String name);
	
	public void setProperty(String name, Object value);

}
