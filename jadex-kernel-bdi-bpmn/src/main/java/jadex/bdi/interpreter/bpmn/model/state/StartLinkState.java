package jadex.bdi.interpreter.bpmn.model.state;

import org.xml.sax.Attributes;



public class StartLinkState extends AbstractState
{
	// ---- attributes ----
	
//	// this should be checked and removed soon! 
//	// Doesn't make sense to store this information 
//	// TODO: remove!
	private String mgoalReference;
	
	// ---- constructors ----
	
	public StartLinkState()
	{
//		setFinished(true);
	}

	// ---- self parsing element overrides ----
	
	public String getGoalReference() {
		return mgoalReference;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) 
	{
		if (qName.equals("attribute"))
		{
			String sAttributeName = attributes.getValue("name");
			//this might be removed in the future because I can't see any sense of this information
			if (sAttributeName.equals("Link"))
			{
				mgoalReference = attributes.getValue("value");
			}
		}
	}

	public void endElement(String uri, String localName, String qName)
	{
		// nothing to do here for StartLink
	}

	
}
