package jadex.extension.envsupport.environment.space2d;

import java.util.Collection;

import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;


public class GridController implements ISpaceController
{

	AbstractEnvironmentSpace	space;

	Grid2D						gridspace;

	public GridController(AbstractEnvironmentSpace space)
	{
		this.space = space;

		if(space instanceof Grid2D)
		{
			gridspace = (Grid2D)space;
		}
	}

	public Collection getSpaceObjectsByGridPosition(IVector2 position, Object type)
	{
		return gridspace.getSpaceObjectsByGridPosition(position, type);
	}

	public Object getProperty(String name)
	{
		return space.getProperty(name);
	}


	public void setProperty(String name, Object value)
	{
		space.setProperty(name, value);

	}

	public ISpaceObject getSpaceObject(Object id)
	{
		return gridspace.getSpaceObject(id);
	}

}
