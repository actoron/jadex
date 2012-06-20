package jadex.gpmn.editor.model.gpmn.impl;

import jadex.gpmn.editor.model.gpmn.IContext;
import jadex.gpmn.editor.model.gpmn.IParameter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Context implements IContext
{
	/** The context parameters */
	public Map<String, IParameter> parameters = new HashMap<String, IParameter>();
	
	/**
	 *  Adds a parameter.
	 *  
	 *  @param parameter The parameter.
	 */
	public void addParameter(IParameter parameter)
	{
		parameters.put(parameter.getName(), parameter);
	}
	
	/**
	 *  Removes a parameter.
	 *  
	 *  @param name The name of the parameter.
	 */
	public void removeParameter(String name)
	{
		parameters.remove(name);
	}
	
	/**
	 *  Renames a parameter.
	 *  
	 *  @param oldname The old name.
	 *  @param newname The new name.
	 */
	public void renameParameter(String oldname, String newname)
	{
		IParameter parameter = parameters.remove(oldname);
		parameter.setName(newname);
		addParameter(parameter);
	}
	
	/**
	 *  Gets the parameters.
	 *
	 *  @return The parameters.
	 */
	public Collection<IParameter> getParameters()
	{
		return parameters.values();
	}

	/**
	 *  Sets the parameters.
	 *
	 *  @param parameters The parameters.
	 */
	public void setParameters(Collection<IParameter> parameters)
	{
		this.parameters = new HashMap<String, IParameter>();
		for (IParameter parameter : parameters)
		{
			this.parameters.put(parameter.getName(), parameter);
		}
	}
	
	
}
