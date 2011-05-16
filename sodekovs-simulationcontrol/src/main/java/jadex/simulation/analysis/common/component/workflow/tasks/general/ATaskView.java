package jadex.simulation.analysis.common.component.workflow.tasks.general;

import jadex.simulation.analysis.common.component.workflow.factories.ATaskViewFactory;
import jadex.simulation.analysis.common.events.task.ATaskEvent;
import jadex.simulation.analysis.common.util.AConstants;

import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ATaskView implements IATaskView
{
	protected JComponent component;
	protected TaskProperties properties;

	protected IATask displayedTask;
	protected Object mutex = new Object();
	protected JComponent dummy = new JPanel();

	public ATaskView()
	{
		component = new JPanel(new GridBagLayout());
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				properties = new TaskProperties();
				component = dummy;
			}
		});
	}

	@Override
	public JComponent getComponent()
	{
		return component;
	}

	@Override
	public TaskProperties getTaskProperties()
	{
		return properties;
	}

	@Override
	public void taskEventOccur(final ATaskEvent event)
	{
	
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if (event.getCommand().equals(AConstants.TASK_LÄUFT))
				{
					properties.getTextField("Status").setText(AConstants.TASK_LÄUFT);
				} else if (event.getCommand().equals(AConstants.TASK_BEENDET))
				{
					properties.getTextField("Status").setText(AConstants.TASK_BEENDET);
				}
				properties.revalidate();
				properties.repaint();
			}
		});
	}

	@Override
	public IATask getDisplayedObject()
	{
		return displayedTask;
	}

	@Override
	public Object getMutex()
	{
		return mutex;
	}

	@Override
	public void setDisplayedObject(final IATask taskObject)
	{
		synchronized (mutex)
		{
			displayedTask = taskObject;
			taskObject.addTaskListener(this);
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					properties.getTextField("Activitätsname").setText(taskObject.getActivity().getName());
					properties.getTextField("Activitätsklasse").setText(taskObject.getActivity().getClazz().getName().toString());
					properties.getTextField("Viewerklasse").setText(ATaskViewFactory.getViewerClass(taskObject).getName().toString());

					properties.revalidate();
					properties.repaint();
				}
			});

		}

	}
}
