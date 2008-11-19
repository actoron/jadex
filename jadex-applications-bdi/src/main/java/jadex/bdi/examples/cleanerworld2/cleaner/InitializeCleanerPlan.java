package jadex.bdi.examples.cleanerworld2.cleaner;

import jadex.bdi.planlib.simsupport.common.graphics.drawable.DrawableCombiner;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.ScalableTexturedRectangle;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector1Double;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.planlib.simsupport.environment.simobject.task.MoveObjectTask;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitializeCleanerPlan extends Plan
{
	public void body()
	{
		DrawableCombiner drawable = new DrawableCombiner();
		String cleanerImage = "jadex/bdi/examples/cleanerworld2/images/cleaner.png";
		//drawable.addDrawable(new ScalableRegularPolygon(new Vector2Double(3.0), 3, Color.RED));
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
		
		// Enable waste sensor
		currentGoal = createGoal("enable_waste_sensor");
		dispatchSubgoalAndWait(currentGoal);
		
		// Start looking for waste
		currentGoal = createGoal("performlookforwaste");
		dispatchTopLevelGoal(currentGoal);
	}
}
