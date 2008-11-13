package jadex.bdi.examples.cleanerworld2.cleaner;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bdi.planlib.simsupport.common.graphics.drawable.DrawableCombiner;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.RotatingColoredRectangle;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.RotatingColoredTriangle;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.ScalableRegularPolygon;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.ScalableTexturedRectangle;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector1Double;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.planlib.simsupport.environment.ISimulationEventListener;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.simobject.task.MoveObjectTask;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class InitializeCleanerPlan extends Plan
{
	public void body()
	{
		DrawableCombiner drawable = new DrawableCombiner();
		String cleanerImage = "jadex/bdi/examples/cleanerworld2/images/cleaner.png";
		drawable.addDrawable(new ScalableRegularPolygon(new Vector2Double(3.0), 3, Color.RED));
		drawable.addDrawable(new ScalableTexturedRectangle(new Vector2Double(1.0), cleanerImage));
		
		String envName = (String) getBeliefbase().getBelief("environment_name").getFact();
		IGoal currentGoal = createGoal("sim_connect_environment");
		currentGoal.getParameter("environment_name").setValue(envName);
		dispatchSubgoalAndWait(currentGoal);
		
		currentGoal = createGoal("sim_create_object");
		currentGoal.getParameter("type").setValue("cleaner");
		Map properties = new HashMap();
		properties.put("battery",new Vector1Double(100.0));
		currentGoal.getParameter("properties").setValue(properties);
		List tasks = new ArrayList();
		tasks.add(new MoveObjectTask(new Vector2Double(0.0)));
		currentGoal.getParameter("tasks").setValue(tasks);
		IVector2 position = new Vector2Double(0.0);
		currentGoal.getParameter("position").setValue(position);
		currentGoal.getParameter("drawable").setValue(drawable);
		currentGoal.getParameter("signal_destruction").setValue(Boolean.TRUE);
		currentGoal.getParameter("listen").setValue(Boolean.TRUE);
		dispatchSubgoalAndWait(currentGoal);
		Integer objectId = (Integer) currentGoal.getParameter("object_id").getValue();
		getBeliefbase().getBelief("simobject_id").setFact(objectId);
		currentGoal = null;
		
		while (true)
		{
			currentGoal = createGoal("sim_get_random_position");
			currentGoal.getParameter("distance").setValue(new Vector2Double(1.0));
			dispatchSubgoalAndWait(currentGoal);
			IVector2 destination = (IVector2) currentGoal.getParameter("position").getValue();
			currentGoal = null;

			currentGoal = createGoal("sim_set_destination");
			currentGoal.getParameter("object_id").setValue(objectId);
			currentGoal.getParameter("destination").setValue(destination);
			currentGoal.getParameter("speed").setValue(new Vector1Double(1.0));
			currentGoal.getParameter("tolerance").setValue(new Vector1Double(0.1));
			dispatchSubgoalAndWait(currentGoal);
			
			waitForInternalEvent("simulation_event");
		}
	}
}
