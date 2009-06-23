package jadex.bdi.interpreter.bpmn.model.state;

import jadex.bdi.interpreter.bpmn.model.IBpmnEndState;
import jadex.bdi.runtime.IBpmnPlanContext;

import org.xml.sax.Attributes;


public class EndLinkState extends AbstractState implements IBpmnEndState
{
	
	// ---- attributes ----
	
	/** 
	 * indicates if this end state is only a end state 
	 * of a compound sub process. False per default.
	 */
	protected boolean subProcessEndState;

	// ---- constructors ----
	
	/**
	 * Create a new {@link EndLinkState}.
	 * Per default this is not a compound sub task
	 */
	public EndLinkState()
	{
		subProcessEndState = false;
//		setFinished(true);
		setFinalState(true);
	}

	// ---- overrides ----
	
	public boolean isFinalState() 
	{
		return !subProcessEndState;
	}
	
//	public String getNextStateId() {
//		// return the next state if this is only a compound sub end state
//		return subProcessEndState ? super.getNextStateId() : null;
//	}

	// ---- IBpmnEndState methods ----

	public boolean isEndOfSubProcess() 
	{
		return subProcessEndState;
	}

	public void setEndOfSubProcess(boolean b) 
	{
		subProcessEndState = b;
	}
	
	// ---- self parsing element overrides ----
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) 
	{
		// no special attributes for EndLinkState
	}

	public void endElement(String uri, String localName, String qName)
	{
		// nothing to do here for EndLinkState
	}
}
