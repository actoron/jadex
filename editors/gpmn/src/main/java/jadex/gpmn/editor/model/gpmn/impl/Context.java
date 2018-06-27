package jadex.gpmn.editor.model.gpmn.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.gpmn.editor.model.gpmn.IContext;
import jadex.gpmn.editor.model.gpmn.IParameter;

public class Context implements IContext
{
	/** The context parameters name map */
	public Map<String, Integer> parameternames = new HashMap<String, Integer>();
	
	/** The parameter list */
	public List<IParameter> parameters = new ArrayList<IParameter>();
	
	/**
	 *  Adds a new parameter.
	 */
	public void addParameter()
	{
		IParameter param = new Parameter();
		addParameter(param);
	}
	
	/**
	 *  Adds a parameter.
	 *  
	 *  @param parameter The parameter.
	 */
	public void addParameter(IParameter parameter)
	{
		int index = parameters.size();
		parameters.add(parameter);
		String basename = parameter.getName();
		String name = basename;
		int counter = 0;
		while (parameternames.containsKey(name))
		{
			name = basename + String.valueOf(counter++);
		}
		parameter.setName(name);
		parameternames.put(name, index);
	}
	
	/**
	 *  Removes a parameter.
	 *  
	 *  @param name The name of the parameter.
	 */
	public void removeParameter(String name)
	{
		int index = parameternames.remove(name);
		parameters.remove(index);
	}
	
	/**
	 *  Removes a parameter.
	 *  
	 *  @param index The parameter index.
	 */
	public void removeParameter(int index)
	{
		IParameter param = parameters.remove(index);
		parameternames.remove(param.getName());
	}
	
	/**
	 *  Removes parameters.
	 *  
	 *  @param indexes The parameter indexes.
	 */
	public void removeParameters(int[] indexes)
	{
		IParameter[] removals = new IParameter[indexes.length];
		for (int i = 0; i < indexes.length; ++i)
		{
			removals[i] = parameters.get(indexes[i]);
			parameternames.remove(removals[i].getName());
		}
		parameters.removeAll(Arrays.asList(removals));
	}
	
	/**
	 *  Renames a parameter.
	 *  
	 *  @param oldname The old name.
	 *  @param newname The new name.
	 */
	public void renameParameter(String oldname, String newname)
	{
		if (newname.length() == 0)
		{
			newname = IParameter.DEFAULT_NAME;
		}
		int index = parameternames.remove(oldname);
		String basename = newname;
		int counter = 0;
		while (parameternames.containsKey(newname))
		{
			newname = basename + String.valueOf(counter++);
		}
		parameters.get(index).setName(newname);
		parameternames.put(newname, index);
	}
	
	/**
	 *  Gets the parameters.
	 *
	 *  @return The parameters.
	 */
	public List<IParameter> getParameters()
	{
		return parameters;
	}

	/**
	 *  Sets the parameters.
	 *
	 *  @param parameters The parameters.
	 */
	public void setParameters(List<IParameter> parameters)
	{
		this.parameters.clear();
		this.parameternames.clear();
		for (IParameter parameter : parameters)
		{
			addParameter(parameter);
		}
	}
	
	
}
