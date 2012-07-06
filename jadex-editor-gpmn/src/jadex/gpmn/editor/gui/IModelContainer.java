package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.model.gpmn.IGpmnModel;

import java.io.File;

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
	
	/** 
	 *  Sets the dirty model state.
	 *  
	 *  @param dirty The dirty state.
	 */
	public void setDirty(boolean dirty);
	
	/**
	 *  Tests if the state is dirty.
	 *  
	 *  @return True, if dirty.
	 */
	public boolean isDirty();
	
	/**
	 *  Gets the project root.
	 *  
	 *  @return The project root.
	 */
	public File getProjectRoot();
	
	/**
	 *  Sets the project root.
	 *  
	 *  @param root The project root.
	 */
	public void setProjectRoot(File root);
	
	/**
	 *  Gets the model file.
	 *  
	 *  @return The model file.
	 */
	public File getFile();
	
	/**
	 *  Sets the model file.
	 *  
	 *  @param file The model file.
	 */
	public void setFile(File file);
	
	/**
	 *  Returns all available Java classes in the project.
	 *  
	 *  @return Array of class names, null if unknown.
	 */
	public String[] getProjectClasses();
}
