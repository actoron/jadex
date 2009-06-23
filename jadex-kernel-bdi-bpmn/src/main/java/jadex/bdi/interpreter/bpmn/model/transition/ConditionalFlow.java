package jadex.bdi.interpreter.bpmn.model.transition;

import org.xml.sax.Attributes;

import com.daimler.util.StringUtils;

public class ConditionalFlow extends AbstractStateTransition
{
	// ------- attributes -------
	
	protected String condition;
	
	// ------- constructors -------
	
	public ConditionalFlow()
	{
		super();
	}
	
	// -------- getter / setter --------
	
	public String getCondition() {
		return condition;
	}
	
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	/** Wrapper for setCondition(String) - provide backwards compatibility to 1.1*/
	public void setConditionExpression(String condition) {
		setCondition(condition);
	}
	
	// ---- self parsing element overrides ----
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		//parse the condition for this transition
		if (qName.equals("attribute")) 
		{
			String attributeName = attributes.getValue("name");
			if (attributeName.equals("ConditionExpression"))
			{
				this.setCondition(StringUtils.removeUmlauts(attributes.getValue("value")));
			}
		}
		
	}

	public void endElement(String uri, String localName, String qName) {
		//nothing to do here for conditional flow
	}

	


}
