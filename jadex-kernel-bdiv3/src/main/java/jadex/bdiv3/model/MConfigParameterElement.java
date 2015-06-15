package jadex.bdiv3.model;

import jadex.bridge.modelinfo.UnparsedExpression;

import java.util.ArrayList;
import java.util.List;

/**
 *  Initial / end goals and plans.
 */
public class MConfigParameterElement	extends MElement
{
	//-------- attributes --------
	
	/** The referenced element name. */
	protected String ref;

	/** The parameters. */
	protected List<UnparsedExpression> parameters;

	//-------- methods --------
	
	/**
	 *  Get the parameters.
	 *  @return The parameters.
	 */
	public List<UnparsedExpression> getParameters()
	{
		return parameters;
	}
	
	/**
	 *  Get a parameter by name.
	 */
	public UnparsedExpression getParameter(String name)
	{
		UnparsedExpression ret = null;
		if(parameters!=null && name!=null)
		{
			for(UnparsedExpression param: parameters)
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
	public void setParameters(List<UnparsedExpression> parameters)
	{
		this.parameters = parameters;
	}
	
	/**
	 *  Add a parameter.
	 *  @param parameter The parameter.
	 */
	public void addParameter(UnparsedExpression parameter)
	{
		if(parameters==null)
			parameters = new ArrayList<UnparsedExpression>();
		this.parameters.add(parameter);
	}

}
