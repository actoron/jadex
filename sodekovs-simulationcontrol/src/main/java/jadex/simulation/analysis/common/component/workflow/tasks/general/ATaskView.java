package jadex.simulation.analysis.common.component.workflow.tasks.general;

import jadex.simulation.analysis.common.component.workflow.Factory.ATaskViewFactory;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameter;
import jadex.simulation.analysis.common.events.task.ATaskEvent;
import jadex.simulation.analysis.common.util.AConstants;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
