package jadex.simulation.analysis.common.component.workflow.tasks.general;

import jadex.simulation.analysis.common.events.ATaskEvent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class ATaskView implements IATaskView
{
	protected JComponent component;
	protected IATaskObservable displayedTask;
	protected Object mutex = new Object();
	
	public ATaskView(IATaskObservable task)
	{
		displayedTask = task;
		task.addTaskListener(this);
		component = new JPanel(new GridBagLayout());
		JComponent freePanel = new JPanel();
		component.add(freePanel, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
	}	
	
	@Override
	public JComponent getComponent()
	{
		return component;
	}

	@Override
	public void taskEventOccur(ATaskEvent event)
	{
		//omit
	}

	@Override
	public IATaskObservable getDisplayedObject()
	{
		return displayedTask;
	}

	@Override
	public Object getMutex()
	{
		return mutex;
	}

	@Override
	public void setDisplayedObject(IATaskObservable taskObject)
	{
		synchronized (mutex)
		{
			this.displayedTask = taskObject;
		}
		
	}

}
