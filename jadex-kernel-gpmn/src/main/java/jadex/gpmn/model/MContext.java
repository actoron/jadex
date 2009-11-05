package jadex.gpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MContext extends MArtifact
{
	//-------- attributes --------
	
	/** The parameters. */
	protected List parameters;
	
	//-------- methods --------
	
	/**
	 *  Get the parameters.
	 *  @return The parameters.
	 */
	public List getParameters()
	{
		return parameters;
	}

	/**
	 *  Add an parameter.
	 *  @param param The parameter.
	 */
	public void addParameter(MParameter param)
	{
		if(parameters==null)
			parameters = new ArrayList();
		parameters.add(param);
	}
	
	/**
	 *  Remove a parameter.
	 *  @param param The parameter.
	 */
	public void removeParameter(MParameter param)
	{
		if(parameters!=null)
			parameters.remove(param);
	}
}
