package jadex.extension.envsupport.environment.space2d;

import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.math.IVector2;

import java.util.Collection;

public class GridController implements ISpaceController {
	
	AbstractEnvironmentSpace space;
	
	Grid2D gridspace;
	
	public GridController(AbstractEnvironmentSpace space) 
	{
		this.space = space;
		
		if(space instanceof Grid2D)
		{
			gridspace = (Grid2D) space;
		}
	}

	public Collection getSpaceObjectsByGridPosition(IVector2 position,
			Object type) 
	{
		return 	gridspace.getSpaceObjectsByGridPosition(position, type);
	}

}
