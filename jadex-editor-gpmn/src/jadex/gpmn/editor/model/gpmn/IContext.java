package jadex.gpmn.editor.model.gpmn;

import java.util.List;

public interface IContext
{
	/**
	 *  Adds a new parameter.
	 */
	public void addParameter();
	
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
	 *  Removes a parameter.
	 *  
	 *  @param index The parameter index.
	 */
	public void removeParameter(int index);
	
	/**
	 *  Removes parameters.
	 *  
	 *  @param indexes The parameter indexes.
	 */
	public void removeParameters(int[] indexes);
	
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
	public List<IParameter> getParameters();

	/**
	 *  Sets the parameters.
	 *
	 *  @param parameters The parameters.
	 */
	public void setParameters(List<IParameter> parameters);
}
