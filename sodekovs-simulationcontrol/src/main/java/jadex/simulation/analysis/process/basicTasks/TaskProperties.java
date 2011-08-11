package jadex.simulation.analysis.process.basicTasks;

import java.awt.Dimension;
import java.util.UUID;

import jadex.commons.gui.PropertiesPanel;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.util.AConstants;

public class TaskProperties extends PropertiesPanel
{
	public TaskProperties()
	{
		super(" Task Eigenschaften ");
		createTextField("Activitätsname");
		createTextField("Activitätsklasse");
		
		createTextField("Status", AConstants.TASK_NIE_GESTARTET);
		setPreferredSize(new Dimension(400,200));
	}
}
