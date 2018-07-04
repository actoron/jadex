package jadex.android.applications.demos.bpmn.tasks;

import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.context.IContextService;
import jadex.commons.future.IFuture;

import java.io.Serializable;
import java.util.HashMap;

@Task(description="Show an activity on the screen", parameters={
		@TaskParameter(name="androidContext", clazz=android.content.Context.class, direction=TaskParameter.DIRECTION_IN, description="The context of the Android application, used to activate the activity."),
		@TaskParameter(name="activityClass", clazz=Class.class, direction=TaskParameter.DIRECTION_IN, description="The activity class (used for explicitly starting Activity)."),
		@TaskParameter(name="attributes", clazz=HashMap.class, direction=TaskParameter.DIRECTION_INOUT, description="A HashMap containing all attributes that shall be passed to the activity.")
})
public class ShowActivityTask extends AbstractTask
{

	public void doExecute(ITaskContext taskContext, IInternalAccess process) throws Exception
	{
		System.out.println("ShowActivityTask: activityClass=" + ((Class)taskContext.getParameterValue("activityClass")).getName());
		
		// retrieve service that is set as required in workflow definition
		IFuture<Object> requiredService = process.getComponentFeature(IRequiredServicesFeature.class).getService("androidcontext");
		IContextService contextService = (IContextService) requiredService.get();
		
		// get task attributes
		android.content.Context androidContext = (android.content.Context)taskContext.getParameterValue("androidContext");
		Class activityClass = (Class)taskContext.getParameterValue("activityClass");
		HashMap<String, Serializable> attributes = (HashMap<String, Serializable>)taskContext.getParameterValue("attributes"); // it is expected, that the attribute map only contains serializable values to pass to the activity
		
		// send event to start new activity
		StartActivityEvent event = new StartActivityEvent(activityClass, attributes);
		contextService.dispatchEvent(event);
	}
}
