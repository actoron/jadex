package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.model.gpmn.IGpmnModel;

import com.mxgraph.swing.mxGraphComponent;

public class ModelContainer implements IModelContainer
{
	/** The graph component. */
	protected mxGraphComponent graphcomponent;
	
	/** The current model. */
	protected IGpmnModel model;
	
	/**
	 *  Creates a new container.
	 */
	public ModelContainer(mxGraphComponent graphcomponent, IGpmnModel model)
	{
		this.graphcomponent = graphcomponent;
		this.model = model;
	}
	
	/**
	 *  Returns the current visual graph component.
	 *  @return The graph.
	 */
	public mxGraphComponent getGraphComponent()
	{
		return graphcomponent;
	}
	
	/**
	 *  Returns the current visual graph.
	 *  @return The graph.
	 */
	public GpmnGraph getGraph()
	{
		return (GpmnGraph) graphcomponent.getGraph();
	}
	
	/**
	 *  Returns the GPMN intermediate model.
	 *  @return GPMN model.
	 */
	public IGpmnModel getGpmnModel()
	{
		return model;
	}
	
	/**
	 *  Sets the current visual graph.
	 *  @param graph The graph.
	 */
	public void setGraph(GpmnGraph graph)
	{
		graphcomponent.setGraph(graph);
	}
	
	/**
	 *  Sets the visual graph component.
	 *  @param component The component.
	 */
	public void setGraphComponent(mxGraphComponent component)
	{
		graphcomponent = component;
	}
	
	/**
	 *  Sets the GPMN model.
	 *  @param model The model.
	 */
	public void setGpmnModel(IGpmnModel model)
	{
		this.model = model;
	}
}
