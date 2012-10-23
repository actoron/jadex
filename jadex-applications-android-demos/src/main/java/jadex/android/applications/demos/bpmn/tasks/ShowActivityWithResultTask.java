package jadex.android.applications.demos.bpmn.tasks;

import jadex.bpmn.annotation.Task;
import jadex.bpmn.annotation.TaskParameter;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Intent;
import android.os.Bundle;

@Task(description="Show an activity on the screen and expect a result", parameters={
		@TaskParameter(name="androidContext", clazz=android.content.Context.class, direction=TaskParameter.DIRECTION_IN, description="The context of the Android application, used to activate the activity."),
		@TaskParameter(name="activityClass", clazz=Class.class, direction=TaskParameter.DIRECTION_IN, description="The activity class (used for explicitly starting Activity)."),
		@TaskParameter(name="attributes", clazz=HashMap.class, direction=TaskParameter.DIRECTION_INOUT, description="A HashMap containing all attributes that shall be passed to the activity.")
})
public class ShowActivityWithResultTask implements ITask, Serializable
{
	/** This future is used to indicate once this task is completed */
	private static Future returnFuture;
	
	/** This map is used to retrieve attributes for the current Activity and to store its results for the next task */
	private static HashMap<String, Serializable> attributes;
	
	public IFuture<Void> execute(ITaskContext taskContext, BpmnInterpreter process)
	{
		// get task attributes
		android.content.Context androidContext = (android.content.Context)taskContext.getParameterValue("androidContext");
		Class activityClass = (Class)taskContext.getParameterValue("activityClass");
		attributes = (HashMap<String, Serializable>)taskContext.getParameterValue("attributes"); // it is expected, that the attribute map only contains serializable values to pass to the activity
		
		// create intent
		Intent intent = new Intent(androidContext, activityClass);
		
		// put all attributes into this intent
		Iterator<String> iter = attributes.keySet().iterator();
		while(iter.hasNext())
		{
			String key = iter.next();
			intent.putExtra(key, attributes.get(key));
		}
		
		// start the activity
		androidContext.startActivity(intent);

		// Controlflow is given back to the process once 'setResult' is called on the returnFuture (see finish())
		returnFuture = new Future();
		return returnFuture;
	}
	
	/**
	 * Indicate that the current activity is finished.
	 * @param intent  Resulting intent may contain arguments that shall be forwarded to the next BPMN task.
	 */
	public static void finish(Intent intent)
	{
		// extract all attributes from the intent and put it in the argument map
		if (intent != null)
		{
			Bundle b = intent.getExtras();
			Iterator<String> iter = b.keySet().iterator();
			while (iter.hasNext())
			{
				String key = iter.next();
				String value = intent.getStringExtra(key);
				
				attributes.put(key, value);
			}
		}
		returnFuture.setResult(null);
	}

	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture cancel(final BpmnInterpreter instance)
	{
		final Future ret = new Future();
		ret.setResult(null);
		return ret;
	}
}
