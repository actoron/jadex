package jadex.bpmn.examples.puzzle;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bpmnbdi.BpmnPlanBodyInstance;

/**
 *  Write our print used memory for benchmark agent.
 *  Does nothing for Sokrates agent.
 */
public class BenchmarkMemoryTask extends AbstractTask
{
	/**
	 * 	Execute the task.
	 */
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		BpmnPlanBodyInstance	inst	= (BpmnPlanBodyInstance)instance;
		if(inst.getBeliefbase().containsBelief("endmem"))
		{
			boolean	print	= ((Boolean)context.getParameterValue("print")).booleanValue();
			Long	endmem	= (Long) inst.getBeliefbase().getBelief("endmem").getFact();
			
			if(print && endmem!=null)
			{
				long	startmem	= ((Number)context.getParameterValue("startmem")).longValue();
				System.out.println("Needed: "+(((endmem.longValue()-startmem)*10/1024)/1024)/10.0+" Mb.");
			}
			else if(!print && endmem==null)
			{
				endmem	= new Long(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
				inst.getBeliefbase().getBelief("endmem").setFact(endmem);
			}
		}
	}
}
