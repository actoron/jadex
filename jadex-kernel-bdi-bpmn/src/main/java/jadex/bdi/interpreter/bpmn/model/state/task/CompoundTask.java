package jadex.bdi.interpreter.bpmn.model.state.task;

import jadex.bdi.interpreter.bpmn.model.ParsedStateMachine;
import jadex.bdi.interpreter.bpmn.model.state.AbstractState;
import jadex.bdi.interpreter.bpmn.parser.BpmnParser;
import jadex.bdi.interpreter.bpmn.parser.BpmnParserException;
import jadex.bdi.interpreter.bpmn.parser.impl.daimler.BpmnPlanParser;

import java.net.URL;

import org.xml.sax.Attributes;


public class CompoundTask extends AbstractState {

	// ---- attributes -----
	
	private String subStructureReference;
	private ParsedStateMachine parsedSubStateMachine;

	// ---- constructor ----
	
	public CompoundTask()
	{
	}
	
	// ---- methods ----
	
	public ParsedStateMachine getTheParsedSubStateMachine() {
		return parsedSubStateMachine;
	}

	public String getTheSubStructureReference() {
		return subStructureReference;
	}
	
	// ---- self parsing element overrides - remove later ----
	
	//
	// The following methods contain some more than stupid HACKS mostly copied
	// from daimler classes to provide backward compatibility with old net files
	// using the daimler parser.
	//
	// PLEASE DON'T HIT ME FOR THIS :-)
	//
	
	// TODO: remove parsing here, this is in the responsibility of the parser!
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals("attribute"))
		{
			String sAttributeName = attributes.getValue("name");
			
			// c. altschaffel: 
			// Seems that "Link" contains the name of the sub structure net file. This is parsed here and the resulting
			// StateMachine is accessed by the buildStateMachine() method of BpmnHandlerBase. I don't understand why c.wiech
			// would remove this for the daimler parser yet. 
			
			// c.wiech: this might be removed in the future because I can't see any sense of this information
			if (sAttributeName.equals("Link"))
			{
				subStructureReference = attributes.getValue("value");
				parseSubStructure(subStructureReference);
			}
		}
	}
	
	
	public void endElement(String uri, String localName, String qName) 
	{
		// nothing to do here
	}
	
	private void parseSubStructure(String subStructureReference) 
	{
			try 
			{
				String moduleFile = subStructureReference;
				if (moduleFile.startsWith("/")) 
				{
					moduleFile = moduleFile.substring(1);
				}
				URL url = Thread.currentThread().getContextClassLoader().getResource(moduleFile);
				BpmnParser myParser = BpmnParser.getInstance(url);
				if (!(myParser instanceof BpmnPlanParser)) 
				{
					return;
				}
				parsedSubStateMachine = (ParsedStateMachine) ((BpmnPlanParser) myParser).parseFile();
			} 
			catch (BpmnParserException err) 
			{
				System.err.println(">>>>>>>>>>>>>>>>ERROR PARSING SUBTASKS");
				err.printStackTrace();
			}
	}
	

}
