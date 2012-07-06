package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.gui.propertypanels.BasePropertyPanel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

public interface IViewAccess
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
	
	/** Ref Plan Edit Mode */
	public static final String REF_PLAN_MODE		= "Plan_Ref";
	
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
		REF_PLAN_MODE,
		ACTIVATION_PLAN_MODE
	}));
	
	/**
	 *  Returns the tool group.
	 *  
	 *  @return The tool group.
	 */
	public ButtonGroup getToolGroup();
	
	/**
	 *  Sets the property panel.
	 *  
	 *  @param panel The panel.
	 */
	public void setPropertPanel(BasePropertyPanel panel);
	
	/**
	 *  Returns the select tool.
	 *  
	 *  @return The select tool.
	 */
	public JToggleButton getSelectTool();
	
	/** 
	 * Returns the current edit mode
	 * 
	 * @return the edit mode.
	 */
	public String getEditMode();
}