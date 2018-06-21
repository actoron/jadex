package jadex.gpmn.editor.model.gpmn;

import com.mxgraph.model.mxIGraphModel;

public interface IGpmnModel
{
	/**
	 *  Gets the description.
	 *  
	 *  @return The description.
	 */
	public String getDescription();
	
	/**
	 *  Sets the name.
	 *  
	 *  @param description The description.
	 */
	public void setDescription(String description);
	
	/**
	 *  Gets the package.
	 *  
	 *  @return The package.
	 */
	public String getPackage();
	
	/**
	 *  Sets the package.
	 *  
	 *  @param pkg The package.
	 */
	public void setPackage(String pkg);
	
	/**
	 *  Gets the context.
	 *
	 *  @return The context.
	 */
	public IContext getContext();

	/**
	 *  Sets the context.
	 *
	 *  @param context The context.
	 */
	public void setContext(IContext context);

	/**
	 *  Creates a node in the model.
	 *  
	 *  @param nodetype The node type.
	 *  @return The node.
	 */
	public INode createNode(Class nodetype);
	
	/**
	 *  Copies a node in the model.
	 *  
	 *  @param node The node.
	 *  @return The node copy.
	 */
	public INode copyNode(IElement node);
	
	/**
	 *  Creates an edge in the model.
	 *  
	 *  @param source Source of the edge.
	 *  @param target Target of the edge.
	 *  @param edgetype The edge type.
	 *  @return The edge.
	 */
	public IEdge createEdge(IElement source, IElement target, Class edgetype);
	
	/**
	 *  Removes a node from the model.
	 *  
	 *  @param node The node.
	 */
	public void removeNode(INode node);
	
	/**
	 *  Removes an edge from the model.
	 *  
	 *  @param edge The edge.
	 */
	public void removeEdge(IEdge edge);
	
	/**
	 *  Returns the codec for loading and saving models.
	 *  
	 *  @param type The type of the codec.
	 *  @return The codec.
	 */
	public IModelCodec getModelCodec(String type);
	
	/**
	 *  Generates a visual model for a GPMN model.
	 *  
	 *  @return A visual model.
	 */
	public mxIGraphModel generateGraphModel();
	
	/**
	 *  Clears the model.
	 */
	public void clear();
}
