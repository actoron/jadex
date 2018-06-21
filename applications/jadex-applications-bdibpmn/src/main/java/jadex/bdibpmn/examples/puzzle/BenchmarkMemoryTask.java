package jadex.bdibpmn.examples.puzzle;

import jadex.bdibpmn.BpmnPlanBodyInstance;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.IInternalAccess;

/**
 *  Write our print used memory for benchmark agent.
 *  Does nothing for Sokrates agent.
 */
public class BenchmarkMemoryTask extends AbstractTask
{
	/**
	 * 	Execute the task.
	 */
	public void doExecute(ITaskContext context, IInternalAccess instance)
	{
		BpmnPlanBodyInstance	inst	= (BpmnPlanBodyInstance)instance;
		if(inst.getBeliefbase().containsBelief("endmem"))
		{
			boolean	print	= ((Boolean)context.getParameterValue("print")).booleanValue();
			Long	endmem	= (Long) inst.getBeliefbase().getBelief("endmem").getFact();
			
			if(print && endmem!=null)
			{
				long	startmem	= ((Number)context.getParameterValue("startmem")).longValue();
				long	memused	= ((endmem.longValue()-startmem)*10/1024)/1024;
				System.out.println("Needed: "+memused/10.0+" Mb.");
			}
			else if(!print && endmem==null)
			{
				endmem	= Long.valueOf(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
				inst.getBeliefbase().getBelief("endmem").setFact(endmem);
			}
		}
	}
	
	/**
	 *  Get the meta information about the task.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "Write our print used memory for benchmark agent.";
		
		ParameterMetaInfo pmi1 = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "print", null, "Flag to turn on printing.");
		ParameterMetaInfo pmi2 = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Long.class, "startmem", null, "The initially used memory.");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{pmi1, pmi2}); 
	}
}
