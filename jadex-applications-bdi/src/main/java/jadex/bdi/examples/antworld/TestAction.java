package jadex.bdi.examples.antworld;

import java.util.HashMap;
import java.util.Map;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.bdi.examples.antworld.foraging.ProducePheromoneAction;
import jadex.commons.SimplePropertyObject;

public class TestAction extends SimplePropertyObject implements ISpaceAction {

	public Object perform(Map parameters, IEnvironmentSpace space) {
		// TODO Auto-generated method stub

		IVector2 position1 = new Vector2Int(9, 5);
		IVector2 position2 = new Vector2Int(8, 5);
		Map props1 = new HashMap();
		props1.put(Space2D.PROPERTY_POSITION, position1);
		props1.put(ProducePheromoneAction.STRENGTH, new Integer(10));

		Map props2 = new HashMap();
		props2.put(Space2D.PROPERTY_POSITION, position1);
		props2.put(ProducePheromoneAction.STRENGTH, new Integer(10));
		// props.put("creation_age", new Double(clock.getTick()));
		// props.put("clock", clock);

//		((Space2D)space).setPosition(oid, pos);
		
		if (parameters == null) {
			System.out.println("#TestAction# Destination Sign!");
			space.createSpaceObject("destinationSign", props1, null);
		} else {
			System.out.println("#TestAction# Trace Route!");
			space.createSpaceObject("traceRoute", props2, null);
		}

		return null;
	}

}
