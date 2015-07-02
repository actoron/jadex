package jadex.bdiv3.model;

import jadex.bridge.modelinfo.UnparsedExpression;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  Initial / end goals and plans.
 */
public class MConfigParameterElement	extends MElement
{
	//-------- attributes --------
	
	/** The parameters. */
	protected Map<String, List<UnparsedExpression>> parameters;

	//-------- methods --------
	
	/**
	 *  Get the parameters.
	 */
	public Map<String, List<UnparsedExpression>> getParameters()
	{
		return parameters;
	}
	
	/**
	 *  Get the parameters by name.
	 */
	public List<UnparsedExpression> getParameters(String name)
	{
		return parameters!=null ? parameters.get(name) : null;
	}
	
	/**
	 *  Get a parameter by name.
	 */
	public UnparsedExpression getParameter(String name)
	{
		List<UnparsedExpression>	ret	= parameters!=null ? parameters.get(name) : null;
		if(ret!=null && ret.size()!=1)
		{
			throw new RuntimeException("Not a single value for parameter: "+name+", "+ret);
		}
		return ret!=null ? ret.get(0) : null;
	}
	
	/**
	 *  Test if goal has a parameter.
	 */
	public boolean hasParameter(String name)
	{
		return parameters!=null && parameters.containsKey(name);
	}

	/**
	 *  Add a parameter.
	 *  @param parameter The parameter.
	 */
	public void addParameter(UnparsedExpression parameter)
	{
		if(parameters==null)
		{
			parameters = new LinkedHashMap<String, List<UnparsedExpression>>();
		}
		List<UnparsedExpression>	params	=  parameters.get(parameter.getName());
		if(params==null)
		{
			params	= new ArrayList<UnparsedExpression>();
			parameters.put(parameter.getName(), params);
		}
		params.add(parameter);
	}
}
