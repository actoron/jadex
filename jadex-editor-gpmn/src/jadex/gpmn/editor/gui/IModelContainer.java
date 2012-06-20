package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.model.gpmn.IGpmnModel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.mxgraph.swing.mxGraphComponent;

/**
 *  Container with the current visual and business model.
 *
 */
public interface IModelContainer
{
	/** Select Edit Mode */
	public static final String SELECT_MODE			= "Select";
	
	/** Control Point Mode */
	public static final String CONTROL_POINT_MODE	= "Control_Points";
	
	/** Achieve Goal Edit Mode */
	public static final String ACHIEVE_GOAL_MODE	= "Goal_Achieve";
	
	/** Perform Goal Edit Mode */
	public static final String PERFORM_GOAL_MODE	= "Goal_Perform";
	
	/** Maintain Goal Edit Mode */
	public static final String MAINTAIN_GOAL_MODE	= "Goal_Maintain";
	
	/** Query Goal Edit Mode */
	public static final String QUERY_GOAL_MODE		= "Goal_Query";
	
	/** BPMN Plan Edit Mode */
	public static final String BPMN_PLAN_MODE		= "Plan_BPMN";
	
	/** Activation Plan Edit Mode */
	public static final String ACTIVATION_PLAN_MODE = "Plan_Activation";
	
	/** Suppression Edge Edit Mode */
	public static final String SUPPRESSION_EDGE_MODE = "Edge_Suppression";
	
	/** Node Creation Modes */
	public static final Set<String> NODE_CREATION_MODES = new HashSet<String>(Arrays.asList(new String[] 
	{
		ACHIEVE_GOAL_MODE,
		PERFORM_GOAL_MODE,
		MAINTAIN_GOAL_MODE,
		QUERY_GOAL_MODE,
		BPMN_PLAN_MODE,
		ACTIVATION_PLAN_MODE
	}));
	
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
	 *  Sets the GPMN model.
	 *  @param model The model.
	 */
	public void setGpmnModel(IGpmnModel model);
}
