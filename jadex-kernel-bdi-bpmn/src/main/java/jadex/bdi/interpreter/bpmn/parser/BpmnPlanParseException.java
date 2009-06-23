package jadex.bdi.interpreter.bpmn.parser;


/**
 * Exception for the BPMN plan parser, thrown if an element is not parsable
 *
 * @author cwiech8, claas altschaffel
 * Partial based on class provided by Daimler
 * 
 * <p>
 * This file is property of DaimlerCrysler.
 * </p>
 */
public class BpmnPlanParseException extends BpmnParserException
{
	public BpmnPlanParseException()
	{
		super();
	}
	
	public BpmnPlanParseException(String message)
	{
		super(message);
	}
}
