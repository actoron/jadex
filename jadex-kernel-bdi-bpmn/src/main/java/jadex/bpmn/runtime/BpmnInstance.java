package jadex.bpmn.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bpmn.model.MBpmnDiagram;

/**
 * 
 */
public class BpmnInstance
{
	/** The model. */
	protected MBpmnDiagram model;

	/** The current states. */
	protected Set currentstates;
	
	/** The instance state. */
	protected Map state;
	
	/**
	 * 
	 */
	public BpmnInstance(MBpmnDiagram model)
	{
		this.model = model;
		this.state = new HashMap();
		this.currentstates = new LinkedHashSet();
	
	}
	
	/**
	 * 
	 */
	public void executeStep()
	{
		// What about diagrams with multiple pools/lanes etc?
		if(currentstates.isEmpty())
		{
//			Collection start = getStartStates();
			
		}
	}
	
	/**
	 * 
	 */
	public void getStartStates()
	{
		
	}
}
