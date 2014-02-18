package jadex.bpmn.persist;

import java.util.Map;

/**
 *  Class containing persistence information about
 *  a BPMN process instance.
 *
 */
public class BpmnPersistInfo
{
	/** The context variables. */
	protected Map<String, Object> variables;
	
	/** The thread id counter. */
	protected int idcnt;
}
