package jadex.bdibpmn.task;


/**
 *  Create a task for a space object.
 */
public class WaitForSpaceObjectTaskTask	//implements ITask
{
//	/**
//	 *  Execute the task.
//	 *  @param context	The accessible values.
//	 *  @param process	The process instance executing the task.
//	 *  @param listener	To be notified, when the task has completed.
//	 */
//	public IFuture	execute(ITaskContext context, IInternalAccess process)
//	{
//		Future ret = new Future();
//		
//		IEnvironmentSpace	space	= (IEnvironmentSpace)context.getParameterValue("space");
//		Object	objectid	= context.getParameterValue("objectid");
//		Object	taskid	= context.getParameterValue("taskid");
//		space.addTaskListener(taskid, objectid, 
//			process.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)));
//		
//		return ret;
//	}
//	
//	/**
//	 *  Compensate in case the task is canceled.
//	 *  @return	To be notified, when the compensation has completed.
//	 */
//	public IFuture cancel(final IInternalAccess instance)
//	{
//		return IFuture.DONE;
//	}
//	
//	//-------- static methods --------
//	
//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static TaskMetaInfo getMetaInfo()
//	{
//		String desc = "The wait for space object task task can be used to wait for completion of a task in an" +
//			"EnvSupport environment.";
//		
//		ParameterMetaInfo spacemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//			IEnvironmentSpace.class, "space", null, "The space parameter defines the space.");
//		ParameterMetaInfo objectid = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//			Object.class, "objectid", null, "The objectid parameter for identifying the space object.");
//		ParameterMetaInfo taskidmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT, 
//			Object.class, "taskid", null, "The taskid parameter for identifying the task.");
//
//		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{spacemi, objectid, taskidmi}); 
//	}
}
