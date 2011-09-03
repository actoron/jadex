package jadex.simulation.analysis.common.superClasses.tasks;

import jadex.simulation.analysis.common.superClasses.service.view.session.subprocess.ASubProcessView;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * View for a SubProcess Task
 * @author 5Haubeck
 *
 */
public class ASubProcessTaskView extends ATaskView implements IATaskView
{
	public ASubProcessTaskView(IATask taskObject)
	{
		super(taskObject);
	}
	
	/**
	 * Adds the {@link ASubProcessView} to the ATaskView
	 * @param {@link ASubProcessView} to display in task
	 */
	public void setSubProcess(ASubProcessView component)
	{
		this.component.add(component, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
	}
}
