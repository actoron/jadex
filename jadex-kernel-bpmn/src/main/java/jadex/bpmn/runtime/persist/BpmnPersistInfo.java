package jadex.bpmn.runtime.persist;

import jadex.bpmn.runtime.BpmnInterpreter;

/**
 *  Class containing persistence information about
 *  a BPMN process instance.
 */
public class BpmnPersistInfo
{
	/** The context variables. */
	protected ThreadInfo topthread;
	
//	/** The thread id counter. */
//	protected int idcnt;
	
	/**
	 *  Create a new BpmnPersistInfo.
	 */
	public BpmnPersistInfo(BpmnInterpreter interpreter)
	{
		topthread = new ThreadInfo(interpreter.getTopLevelThread());
	}
}
