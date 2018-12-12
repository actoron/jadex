package jadex.micro.examples.heatbugs;

import java.util.Map;

import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

/**
 *  Action for moving a bug to one of its neighbor fields.
 */
public class MoveAction extends SimplePropertyObject implements ISpaceAction
{
	//-------- constants --------
	
	/** The constant identifier for this action. */
	public static final String EMIT_HEAT = "set_position";
	
	// Default Action Parameters
	public static final String PARAMETER_HEAT = "heat";

	/** The position parameter. */
	public static final String PARAMETER_POSITION = "position";
	
	//-------- methods --------
	
	/**
	 *  Perform an action.
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		Grid2D grid = (Grid2D)space;
		Object id = parameters.get(ISpaceAction.OBJECT_ID);
		ISpaceObject heatbug = grid.getSpaceObject(id);
		double output_heat = ((Double)heatbug.getProperty("output_heat")).doubleValue();
		IVector2 pos = (IVector2)parameters.get(PARAMETER_POSITION);		
		
		// Move if patch is not occupied
		if(grid.getSpaceObjectsByGridPosition(pos, "heatbug")==null)
		{
			((Space2D)space).setPosition(id, pos);
		}
		else
		{
			pos = (IVector2)heatbug.getProperty(Space2D.PROPERTY_POSITION);
		}
		
		// Emit heat.
		ISpaceObject patch = (ISpaceObject)grid.getSpaceObjectsByGridPosition(pos, "patch").iterator().next();
		double heat = ((Double)patch.getProperty("heat")).doubleValue();
		patch.setProperty("heat", Double.valueOf(heat+output_heat));
		
		return null;
	}
}
