package jadex.bdi.interpreter.bpmn.parser;

/**
 * BPMN parse exception, throws if a parse error occurs.
 *
 *
 * @author claas altschaffel
 * Partial based on class provided by Daimler
 */
public class BpmnParserException extends Exception
{
	public BpmnParserException()
	{
		super();
	}
	
	public BpmnParserException(String message)
	{
		super(message);
	}
	
	public BpmnParserException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public BpmnParserException(Throwable cause)
	{
		super(cause);
	}
}
