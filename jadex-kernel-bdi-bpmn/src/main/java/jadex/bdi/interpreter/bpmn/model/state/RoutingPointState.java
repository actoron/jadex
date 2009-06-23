package jadex.bdi.interpreter.bpmn.model.state;

import org.xml.sax.Attributes;

/**
 * I don't know why this class was introduced by daimler .... check ability to remove!
 * 
 * @author claas
 *
 */
public class RoutingPointState extends AbstractState
{

	// ---- constructors ----

	public RoutingPointState()
	{
//		setFinished(true);
	}

	// ---- self parsing element overrides ----
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) 
	{
		// nothing to do here
	}

	public void endElement(String uri, String localName, String qName) 
	{
		// nothing to do here
	}

}
