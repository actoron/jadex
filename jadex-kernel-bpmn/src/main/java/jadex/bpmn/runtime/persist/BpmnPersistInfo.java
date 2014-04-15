package jadex.bpmn.runtime.persist;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.commons.transformation.traverser.Traverser;

import java.util.Map;

/**
 *  Class containing persistence information about
 *  a BPMN process instance.
 */
public class BpmnPersistInfo
{
	/** The context variables. */
	protected Map<String, Object> variables;
	
	/** The thread context info. */
	protected ThreadInfo topthread;
	
//	/** The thread id counter. */
//	protected int idcnt;
	
	/**
	 *  Create a new BpmnPersistInfo.
	 */
	public BpmnPersistInfo(BpmnInterpreter interpreter)
	{
		variables = (Map<String, Object>)Traverser.traverseObject(interpreter.getVariables(), null, true, null);
		topthread = new ThreadInfo(interpreter.getTopLevelThread());
	}
}
