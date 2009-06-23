package jadex.bdi.interpreter.bpmn.model.state;

import jadex.bdi.interpreter.bpmn.model.IBpmnEndState;
import jadex.bdi.runtime.IBpmnPlanContext;

import org.xml.sax.Attributes;


public class IntermediateErrorState extends EndLinkState implements IBpmnEndState
{

	// ---- constructors ----

	public IntermediateErrorState()
	{
		setEndOfSubProcess(false);
//		setFinished(true);
	}

	// ---- overrides ----
	
	public IBpmnPlanContext execute(IBpmnPlanContext body) 
	{
		// nothing to do here for IntermediateError
		super.execute(body);
		System.err.println("Ending in Intermediate error " + getId());
		return body;
	}

	// ---- self parsing element overrides ----
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) 
	{
		// no special attributes for IntermediateErrorState
	}

	public void endElement(String uri, String localName, String qName)
	{
		// nothing to do here for IntermediateErrorState
	}
	
}
