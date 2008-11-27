package jadex.bdi.examples.cleanerworld2.cleaner;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.examples.cleanerworld2.cleaner.task.BatteryDischargeTask;
import jadex.bdi.examples.cleanerworld2.cleaner.task.LowBatteryWarnTask;
import jadex.bdi.examples.cleanerworld2.environment.process.StaticObjectSensorProcess;
import jadex.bdi.examples.cleanerworld2.environment.process.WasteSensorProcess;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.DrawableCombiner;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.RotatingColoredTriangle;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.ScalableRegularPolygon;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.ScalableTexturedRectangle;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector1Double;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.planlib.simsupport.environment.process.IEnvironmentProcess;
import jadex.bdi.planlib.simsupport.environment.simobject.task.MoveObjectTask;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.awt.Color;
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
		drawable.addDrawable(new ScalableRegularPolygon(new Vector2Double(Configuration.CLEANER_VISUAL_RANGE.getAsDouble() * 2.0), 24, new Color(1.0f, 1.0f, 0.0f, 0.5f)));
		drawable.addDrawable(new ScalableTexturedRectangle(new Vector2Double(1.0), cleanerImage));
		//drawable.addDrawable(new RotatingColoredTriangle(new Vector2Double(1.0), new Vector2Double(1.0), new Vector2Double(0.0), Color.BLUE));
		
		String envName = (String) getBeliefbase().getBelief("environment_name").getFact();
		IGoal currentGoal = createGoal("sim_connect_environment");
		currentGoal.getParameter("environment_name").setValue(envName);
		dispatchSubgoalAndWait(currentGoal);
		
		currentGoal = createGoal("sim_create_object");
		currentGoal.getParameter("type").setValue("cleaner");
		Map properties = new HashMap();
		properties.put("battery",new Vector1Double(100.0));
		properties.put("waste_capacity", Configuration.MAX_WASTE_CAPACITY.copy());
		currentGoal.getParameter("properties").setValue(properties);
		List tasks = new ArrayList();
		tasks.add(new MoveObjectTask(new Vector2Double(0.0)));
		tasks.add(new BatteryDischargeTask());
		tasks.add(new LowBatteryWarnTask());
		currentGoal.getParameter("tasks").setValue(tasks);
		IVector2 position = new Vector2Double(0.0);
		currentGoal.getParameter("position").setValue(position);
		currentGoal.getParameter("drawable").setValue(drawable);
		currentGoal.getParameter("signal_destruction").setValue(Boolean.TRUE);
		currentGoal.getParameter("listen").setValue(Boolean.TRUE);
		dispatchSubgoalAndWait(currentGoal);
		Integer objectId = (Integer) currentGoal.getParameter("object_id").getValue();
		getBeliefbase().getBelief("simobject_id").setFact(objectId);
		
		// Enable waste bin sensor
		String processName = StaticObjectSensorProcess.DEFAULT_NAME + objectId.toString();
		IEnvironmentProcess sensorProcess = new StaticObjectSensorProcess(processName, objectId);
		IGoal addProcess = createGoal("sim_add_environment_process");
		addProcess.getParameter("process").setValue(sensorProcess);;
		dispatchSubgoalAndWait(addProcess);
		
		// Enable waste sensor
		currentGoal = createGoal("enable_waste_sensor");
		dispatchSubgoalAndWait(currentGoal);
		
		// Start looking for waste
		currentGoal = createGoal("look_for_waste");
		dispatchTopLevelGoal(currentGoal);
	}
}
