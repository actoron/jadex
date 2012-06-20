package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.model.gpmn.IGpmnModel;

import com.mxgraph.swing.mxGraphComponent;

/**
 *  Container with the current visual and business model.
 *
 */
public interface IModelContainer
{
	/**
	 *  Returns the current visual graph component.
	 *  @return The graph.
	 */
	public mxGraphComponent getGraphComponent();
	
	/**
	 *  Returns the current visual graph.
	 *  @return The graph.
	 */
	public GpmnGraph getGraph();
	
	/**
	 *  Returns the GPMN model.
	 *  @return GPMN model.
	 */
	public IGpmnModel getGpmnModel();
	
	/**
	 *  Sets the current visual graph.
	 *  @param graph The graph.
	 */
	public void setGraph(GpmnGraph graph);
	
	/**
	 *  Sets the visual graph component.
	 *  @param component The component.
	 */
	public void setGraphComponent(mxGraphComponent component);
	
	/**
	 *  Sets the GPMN model.
	 *  @param model The model.
	 */
	public void setGpmnModel(IGpmnModel model);
}
