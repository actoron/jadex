package jadex.tools.gpmn.diagram.tools;

import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gmf.runtime.diagram.ui.tools.UnspecifiedTypeConnectionTool;
import org.eclipse.jface.resource.ImageDescriptor;

public class CustomPaletteTools
{
	public static final String ACHIEVE_GOAL_TOOL = "AchieveGoalTool";
	
	public static final String PERFORM_GOAL_TOOL = "PerformGoalTool";
	
	public static final String MAINTAIN_GOAL_TOOL = "MaintainGoalTool";
	
	public static final String ACTIVATION_EDGE_TOOL = "ActivationEdgeTool";
	
	public static final Map<String, ToolEntry> TOOL_ENTRIES = new HashMap<String, ToolEntry>();
	static
	{
		TOOL_ENTRIES.put(ACHIEVE_GOAL_TOOL,
				new CustomToolEntry("Achieve Goal",
				"Create new Achieve Goal",
				GpmnElementTypes.getImageDescriptor(GpmnElementTypes.Goal_2004),
				GpmnElementTypes.getImageDescriptor(GpmnElementTypes.Goal_2004),
				AchieveGoalCreationTool.class));
		
		TOOL_ENTRIES.put(PERFORM_GOAL_TOOL,
				new CustomToolEntry("Perform Goal",
				"Create new Perform Goal",
				GpmnElementTypes.getImageDescriptor(GpmnElementTypes.Goal_2004),
				GpmnElementTypes.getImageDescriptor(GpmnElementTypes.Goal_2004),
				PerformGoalCreationTool.class));
		
		TOOL_ENTRIES.put(MAINTAIN_GOAL_TOOL,
				new CustomToolEntry("Maintain Goal",
				"Create new Maintain Goal",
				GpmnElementTypes.getImageDescriptor(GpmnElementTypes.Goal_2004),
				GpmnElementTypes.getImageDescriptor(GpmnElementTypes.Goal_2004),
				MaintainGoalCreationTool.class));
		
		TOOL_ENTRIES.put(ACTIVATION_EDGE_TOOL,
				new CustomToolEntry("Activation Edge",
				"Create new Activation Edge",
				GpmnElementTypes.getImageDescriptor(GpmnElementTypes.ActivationEdge_4001),
				GpmnElementTypes.getImageDescriptor(GpmnElementTypes.ActivationEdge_4001),
				ActivationEdgeCreationTool.class));
	}
	
	private static class CustomToolEntry extends ToolEntry
	{
		private Class toolClass;

		private CustomToolEntry(String title, String description, ImageDescriptor iconSmall, ImageDescriptor iconLarge, Class toolClass)
		{
			super(title, description, iconSmall, iconLarge);
			this.toolClass = toolClass;
		}
		
		public Tool createTool()
		{
			try
			{
				return (Tool) toolClass.newInstance();
			}
			catch (InstantiationException e)
			{
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			return null;
		}
	}
}
