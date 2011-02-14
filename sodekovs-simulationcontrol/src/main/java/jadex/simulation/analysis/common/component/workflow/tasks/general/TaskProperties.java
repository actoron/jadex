package jadex.simulation.analysis.common.component.workflow.tasks.general;

import java.awt.Dimension;

import jadex.commons.gui.PropertiesPanel;
import jadex.simulation.analysis.common.util.AConstants;

public class TaskProperties extends PropertiesPanel
{
	public TaskProperties()
	{
		super(" Task Eigenschaften ");
		createTextField("Activitätsname");
		createTextField("Activitätsklasse");
		createTextField("Viewerklasse");
		
		createTextField("Status", AConstants.TASK_NIE_GESTARTET);
		setPreferredSize(new Dimension(400,200));
	}
}
