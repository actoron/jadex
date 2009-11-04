package jadex.bpmn.runtime;

import jadex.javaparser.IValueFetcher;

/**
 *  Fetch values from the context variables of a BPMN process instance
 *  or its superordinated fetcher.
 */
public class BpmnInstanceFetcher implements IValueFetcher
{
	//-------- attributes --------
	
	/** The BPMN process instance. */
	protected BpmnInterpreter	interpreter;
	
	/** The superordinated value fetcher (if any). */
	protected IValueFetcher	fetcher;

	//-------- constructors --------
	
	/**
	 *  Create a BPMN instance value fetcher.
	 */
	public BpmnInstanceFetcher(BpmnInterpreter interpreter, IValueFetcher fetcher)
	{
		this.interpreter	= interpreter;
		this.fetcher	= fetcher;
	}
	
	//-------- methods --------

	/**
	 *  Get the superordinated value fetcher.
	 *  @return The fetcher, if any.
	 */
	public IValueFetcher getValueFetcher()
	{
		return fetcher;
	}
	
	//-------- IValueFetcher interface --------
	
	/**
	 *  Fetch a value via its name.
	 *  @param name The name.
	 *  @return The value.
	 */
	public Object fetchValue(String name)
	{
		Object	ret;
		if(interpreter.hasContextVariable(name))
		{
			ret	= interpreter.getContextVariable(name);
		}
		else if(fetcher!=null)
		{
			ret	= fetcher.fetchValue(name);
		}
		else
		{
			throw new RuntimeException("Parameter not found: "+name);
		}
		return ret;
	}
	
	/**
	 *  Fetch a value via its name from an object.
	 *  @param name The name.
	 *  @param object The object.
	 *  @return The value.
	 */
	public Object fetchValue(String name, Object object)
	{
		if(fetcher!=null)
			return fetcher.fetchValue(name, object);
		else
			throw new UnsupportedOperationException();
	}
}
