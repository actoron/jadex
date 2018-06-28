package jadex.bdiv3.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  Base class for elements with parameters.
 */
public class MParameterElement extends MElement
{
	/** The parameters. */
	protected List<MParameter> parameters;

	/**
	 *	Bean Constructor. 
	 */
	public MParameterElement()
	{
	}
	
	/**
	 *  Create a new element.
	 */
	public MParameterElement(String name)
	{
		super(name);
	}	
	
	/**
	 *  Get the parameters.
	 *  @return The parameters.
	 */
	public List<MParameter> getParameters()
	{
		return parameters;
	}
	
	/**
	 *  Get a parameter by name.
	 */
	public MParameter getParameter(String name)
	{
		MParameter ret = null;
		if(parameters!=null && name!=null)
		{
			for(MParameter param: parameters)
			{
				if(param.getName().equals(name))
				{
					ret = param;
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Test if goal has a parameter.
	 */
	public boolean hasParameter(String name)
	{
		return getParameter(name)!=null;
	}

	/**
	 *  Set the parameters.
	 *  @param parameters The parameters to set.
	 */
	public void setParameters(List<MParameter> parameters)
	{
		this.parameters = parameters;
	}
	
	/**
	 *  Add a parameter.
	 *  @param parameter The parameter.
	 */
	public void addParameter(MParameter parameter)
	{
		if(parameters==null)
			parameters = new ArrayList<MParameter>();
		this.parameters.add(parameter);
	}
	
	/**
	 *  Test if a param is contained.
	 *  @param name The name.
	 *  @return The correctly spelled parameter name or null.
	 */
	public String hasParameterIgnoreCase(String name)
	{
		String ret = null;
		name = name.toLowerCase();
		
		if(parameters!=null && name!=null)
		{
			for(MParameter param: parameters)
			{
				if(name.equals(param.getName().toLowerCase()))
				{
					ret = param.getName();
					break;
				}
			}
		}
		
		return ret;
	}
}
