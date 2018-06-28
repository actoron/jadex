package jadex.gpmn.editor.model.gpmn.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.gpmn.editor.model.gpmn.IEdge;
import jadex.gpmn.editor.model.gpmn.IElement;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;

public abstract class AbstractElement implements IElement
{
	/** The model the element belongs to. */
	protected IGpmnModel model;
	
	/** Name of the element. */
	protected String name = null;
	
	/** The source edges of the element. */
	protected Set<IEdge> sourceedges;
	
	/** The target edges of the element. */
	protected Set<IEdge> targetedges;

	/**
	 * Creates a new element.
	 */
	protected AbstractElement(IGpmnModel model)
	{
		this.model = model;
		sourceedges = new HashSet<IEdge>();
		targetedges = new HashSet<IEdge>();
	}
	
	/**
	 *  Gets the model.
	 *  
	 *  @return The model.
	 */
	public IGpmnModel getModel()
	{
		return model;
	}
	
	/**
	 *  Gets the name.
	 *
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Sets the name.
	 *
	 *  @param name The name.
	 */
	public void setName(String name)
	{
		if (this.name != null)
			((GpmnModel) getModel()).getNameRepository().deleteUniqueName(this);
		this.name = ((GpmnModel) getModel()).getNameRepository().createUniqueName(name, this);
	}
	
	/**
	 *  Gets the source edges.
	 *  
	 *  @return The edges.
	 */
	public Set<IEdge> getSourceEdges()
	{
		return sourceedges;
	}
	
	/**
	 *  Gets the target edges.
	 *  
	 *  @param type Type of the target edges.
	 *  @return The edges.
	 */
	public Set<IEdge> getTargetEdges()
	{
		return targetedges;
	}
	
	/**
	 *  Adds a new source edge to the element.
	 *  
	 *  @param edge The new edge.
	 */
	protected void addSourceEdge(AbstractEdge edge)
	{
		sourceedges.add(edge);
	}
	
	/**
	 *  Adds a new target edge to the element.
	 *  
	 *  @param edge The new edge.
	 */
	protected void addTargetEdge(AbstractEdge edge)
	{
		targetedges.add(edge);
	}
	
	/**
	 *  Removes a source edge from the element.
	 *  
	 *  @param edge The edge.
	 */
	protected void removeSourceEdge(AbstractEdge edge)
	{
		sourceedges.remove(edge);
	}
	
	/**
	 *  Removes a target edge from the element.
	 *  
	 *  @param edge The edge.
	 */
	protected void removeTargetEdge(AbstractEdge edge)
	{
		targetedges.remove(edge);
	}
	
	protected static Set<AbstractEdge> getEdgeSet(Class type, Map<Class, Set<AbstractEdge>> edges)
	{
		Set <AbstractEdge> ret = edges.get(type);
		if (ret == null)
		{
			ret = new HashSet<AbstractEdge>();
			edges.put(type, ret);
		}
		
		return ret;
	}
}
