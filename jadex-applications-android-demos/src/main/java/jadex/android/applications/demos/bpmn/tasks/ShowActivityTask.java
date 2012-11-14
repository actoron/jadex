package jadex.android.applications.demos.bpmn.tasks;

import jadex.bpmn.annotation.Task;
import jadex.bpmn.annotation.TaskParameter;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Intent;

@Task(description="Show an activity on the screen", parameters={
		@TaskParameter(name="androidContext", clazz=android.content.Context.class, direction=TaskParameter.DIRECTION_IN, description="The context of the Android application, used to activate the activity."),
		@TaskParameter(name="activityClass", clazz=Class.class, direction=TaskParameter.DIRECTION_IN, description="The activity class (used for explicitly starting Activity)."),
		@TaskParameter(name="attributes", clazz=HashMap.class, direction=TaskParameter.DIRECTION_INOUT, description="A HashMap containing all attributes that shall be passed to the activity.")
})
public class ShowActivityTask extends AbstractTask
{

	public void doExecute(ITaskContext taskContext, BpmnInterpreter instance) throws Exception
	{
		System.out.println("ShowActivityTask: activityClass=" + ((Class)taskContext.getParameterValue("activityClass")).getName());
		
		// get task attributes
		android.content.Context androidContext = (android.content.Context)taskContext.getParameterValue("androidContext");
		Class activityClass = (Class)taskContext.getParameterValue("activityClass");
		HashMap<String, Serializable> attributes = (HashMap<String, Serializable>)taskContext.getParameterValue("attributes"); // it is expected, that the attribute map only contains serializable values to pass to the activity
		
		// create intent
		Intent i = new Intent(androidContext, activityClass);
		
		// put all attributes into this intent
		Iterator<String> iter = attributes.keySet().iterator();
		while(iter.hasNext())
		{
			String key = iter.next();
			i.putExtra(key, attributes.get(key));
		}
		
		// start the activity
		androidContext.startActivity(i);
	}
}
