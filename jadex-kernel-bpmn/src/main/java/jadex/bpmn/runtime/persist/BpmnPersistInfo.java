package jadex.bpmn.runtime.persist;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.kernelbase.AbstractPersistInfo;

/**
 *  Class containing persistence information about
 *  a Bpmn process instance.
 */
public class BpmnPersistInfo extends AbstractPersistInfo
{
	/** The context variables. */
	protected ThreadInfo topthread;
	
	/**
	 *  Create a new BpmnPersistInfo.
	 */
	public BpmnPersistInfo(BpmnInterpreter interpreter)
	{
		super(interpreter);
		topthread = new ThreadInfo(interpreter.getTopLevelThread());
	}
}
