package jadex.micro.examples.heatbugs;

import java.util.Map;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.environment.space2d.action.GetPosition;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.commons.SimplePropertyObject;

/**
 * 
 */
public class MoveAction extends SimplePropertyObject implements ISpaceAction
{
	/** The constant identifier for this action. */
	public static final String EMIT_HEAT = "set_position";
	
	// Default Action Parameters
	public static final String PARAMETER_HEAT = "heat";

	
	/**
	 *  Perform an action.
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		Grid2D grid = (Grid2D)space;
		Object id = parameters.get(ISpaceAction.OBJECT_ID);
		ISpaceObject heatbug = grid.getSpaceObject(id);
		double output_heat = ((Double)heatbug.getProperty("output_heat")).doubleValue();
		IVector2 pos = (IVector2)parameters.get(GetPosition.PARAMETER_POSITION);		
		
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
		patch.setProperty("heat", new Double(Math.min(1, heat+output_heat)));
		
		return null;
	}
}
