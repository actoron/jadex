package jadex.simulation.analysis.common.superClasses.tasks;

import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.util.AConstants;

import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Basic View for a analysis Task
 * @author 5Haubeck
 *
 */
public class ATaskView implements IATaskView
{
	protected JComponent component;
	protected TaskProperties properties;
	protected ISuspendable susThread = new ThreadSuspendable(this);
	protected JInternalFrame parent;

	protected IATask displayedTask;
	protected Object mutex = new Object();

	public ATaskView(final IATask taskObject)
	{
		displayedTask = taskObject;
		taskObject.addListener(this);
		component = new JPanel(new GridBagLayout());
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				properties = new TaskProperties();
				if (taskObject.getActivity() != null)
				{
					properties.getTextField("Activitätsname").setText(taskObject.getActivity().getName());
					properties.getTextField("Activitätsklasse").setText(taskObject.getActivity().getClazz().getTypeName().toString());

					properties.revalidate();
					properties.repaint();
				}
				
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
	public void update(final IAEvent event)
	{
	
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if (event.getCommand().equals(AConstants.TASK_LAEUFT))
				{
					properties.getTextField("Status").setText(AConstants.TASK_LAEUFT);
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
	public IATask getDisplayedTask()
	{
		return displayedTask;
	}

	@Override
	public Object getMutex()
	{
		return mutex;
	}
	

	public void setParent(JInternalFrame frame)
	{
		synchronized (mutex)
		{
			parent = frame;		
		}
	}
}
