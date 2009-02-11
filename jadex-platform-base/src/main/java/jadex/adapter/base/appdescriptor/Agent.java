package jadex.adapter.base.appdescriptor;

import java.util.ArrayList;
import java.util.List;

public class Agent
{
	//-------- attributes --------
	
	/** The list of contained parameters. */
	protected List parameters;
	
	//-------- constructors --------
	
	/**
	 * 
	 */
	public Agent()
	{
		this.parameters = new ArrayList();
	}
	
	//-------- methods --------
	
	/**
	 * 
	 */
	public void addParameters(Parameter param)
	{
		this.parameters.add(param);
	}
	
	/**
	 * 
	 */
	public void addParameters(ParameterSet paramset)
	{
		this.parameters.add(paramset);
	}
}
