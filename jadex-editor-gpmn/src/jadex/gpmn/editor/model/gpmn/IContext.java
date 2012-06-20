package jadex.gpmn.editor.model.gpmn;

import java.util.Collection;

public interface IContext
{
	/**
	 *  Adds a parameter.
	 *  
	 *  @param parameter The parameter.
	 */
	public void addParameter(IParameter parameter);
	/**
	 *  Removes a parameter.
	 *  
	 *  @param name The name of the parameter.
	 */
	public void removeParameter(String name);
	
	/**
	 *  Renames a parameter.
	 *  
	 *  @param oldname The old name.
	 *  @param newname The new name.
	 */
	public void renameParameter(String oldname, String newname);
	
	/**
	 *  Gets the parameters.
	 *
	 *  @return The parameters.
	 */
	public Collection<IParameter> getParameters();

	/**
	 *  Sets the parameters.
	 *
	 *  @param parameters The parameters.
	 */
	public void setParameters(Collection<IParameter> parameters);
}
