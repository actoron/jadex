package antworld;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.math.IVector2;

import java.util.Collection;
import java.util.Map;

/**
 * Action for dropping food on a nest field.
 */
public class DropFoodAction extends SimplePropertyObject implements ISpaceAction {
	/**
	 * Performs the action.
	 * 
	 * @param parameters
	 *            parameters for the action
	 * @param space
	 *            the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space) {
		// System.out.println("drop waste action: "+parameters);

		Grid2D grid = (Grid2D) space;

		IComponentDescription owner = (IComponentDescription) parameters.get(ISpaceAction.ACTOR_ID);
		ISpaceObject so = grid.getAvatar(owner);
		IVector2 pos = (IVector2) so.getProperty(Grid2D.PROPERTY_POSITION);

		// TODO:
		assert so.getProperty("food") != null : so;

		ISpaceObject food = (ISpaceObject) so.getProperty("food");
		grid.setPosition(food.getId(), pos);
		so.setProperty("food", null);

		Collection nests = grid.getSpaceObjectsByGridPosition((IVector2) so.getProperty(Grid2D.PROPERTY_POSITION), "nest");
		ISpaceObject nest = (ISpaceObject) nests.iterator().next();
		int stock = ((Integer) nest.getProperty("stock")).intValue();
		nest.setProperty("stock", new Integer(stock + 1));

		// System.out.println("Agent dropped garbage: "+owner+" "+pos);

		return null;
	}

	/**
	 * Returns the ID of the action.
	 * 
	 * @return ID of the action
	 */
	public Object getId() {
		// todo: remove here or from application xml?
		return "drop";
	}
}
