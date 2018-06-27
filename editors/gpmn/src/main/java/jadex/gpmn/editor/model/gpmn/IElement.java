package jadex.gpmn.editor.model.gpmn;

import java.util.Set;

/**
 * Interface for a model element.
 *
 */
public interface IElement
{
	/**
	 *  Gets the model.
	 *  
	 *  @return The model.
	 */
	public IGpmnModel getModel();
	
	/**
	 *  Gets the name.
	 *
	 *  @return The name.
	 */
	public String getName();
	
	/**
	 *  Sets the name.
	 *
	 *  @param name The name.
	 */
	public void setName(String name);
	
	/**
	 *  Gets the source edges.
	 *  
	 *  @return The edges.
	 */
	public Set<IEdge> getSourceEdges();
	
	/**
	 *  Gets the target edges.
	 *  
	 *  @param type Type of the target edges.
	 *  @return The edges.
	 */
	public Set<IEdge> getTargetEdges();
}
