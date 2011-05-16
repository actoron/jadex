package jadex.simulation.analysis.common.component.workflow.defaultView;

import jadex.bpmn.model.MActivity;
import jadex.simulation.analysis.common.component.workflow.tasks.general.ATaskView;
import jadex.simulation.analysis.common.component.workflow.tasks.general.IATask;
import jadex.simulation.analysis.common.component.workflow.tasks.general.IATaskView;
import jadex.simulation.analysis.common.component.workflow.tasks.general.TaskProperties;
import jadex.simulation.analysis.common.dataObjects.factories.ADataViewFactory;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameter;
import jadex.simulation.analysis.common.events.task.ATaskEvent;
import jadex.simulation.analysis.common.events.task.IATaskListener;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ATaskCollectionView extends JPanel implements IATaskListener
{
	protected Object mutex = new Object();

	protected Map<String, IATaskView> displayedTasks;
	protected JList list;
	protected JSplitPane generalComp;
	protected JComponent specialComp;
	protected JPanel dummy = new JPanel();

	protected TaskProperties taskProperties;
	protected TaskProperties generalTaskProperties;

	private Integer lastTaskNumber = 0;

	public ATaskCollectionView(final MActivity activity)
	{
		super(new GridBagLayout());
		synchronized (mutex)
		{
			displayedTasks = Collections.synchronizedMap(new HashMap<String, IATaskView>());

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
			{
				synchronized (mutex)
					{

						// component = new JPanel(new GridBagLayout());
						specialComp = dummy;
						specialComp.setPreferredSize(new Dimension(750, 750));

						generalComp = new JSplitPane();
						final Insets insets = new Insets(1, 1, 1, 1);
						// list
						DefaultListModel listModel = new DefaultListModel();

						list = new JList(listModel);
						list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						list.setLayoutOrientation(JList.VERTICAL);
						list.setVisibleRowCount(-1);
						list.setSelectedIndex(0);
						list.setPreferredSize(new Dimension(250, 150));
						list.setFixedCellWidth(150);
						list.setSize(new Dimension(150, 150));
						list.addListSelectionListener(new ListSelectionListener()
						{

							@Override
							public void valueChanged(ListSelectionEvent e)
							{
								synchronized (mutex)
								{
									IATaskView view = displayedTasks.get((String) list.getSelectedValue());
									update(view);
								}

							}
						});

						for (String task : displayedTasks.keySet())
						{
							listModel.addElement(task);
						}

						JScrollPane listScroller = new JScrollPane(list);
						listScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
						listScroller.setPreferredSize(new Dimension(250, 200));
						listScroller.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Instanziierte Aktivitäten"));
						JPanel leftPanel = new JPanel(new GridBagLayout());
						leftPanel.add(listScroller,
								new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
						leftPanel.setPreferredSize(new Dimension(150, 200));
						((JSplitPane) generalComp).setLeftComponent(leftPanel);

						generalTaskProperties = new TaskProperties();
						generalTaskProperties.getTextField("Activitätsname").setText(activity.getName());
						generalTaskProperties.getTextField("Activitätsklasse").setText(activity.getClazz().getName().toString());
						generalTaskProperties.getTextField("Viewerklasse").setText("Noch nicht bekannt");

						taskProperties = generalTaskProperties;
						((JSplitPane) generalComp).setRightComponent(taskProperties);

						// ((JSplitPane) generalComp).setDividerLocation(200);
						generalComp.setPreferredSize(new Dimension(750, 200));

						generalComp.revalidate();
						generalComp.repaint();
						add(generalComp, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
						add(specialComp, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					}
				}
			});
		}
	}

	public Map<String, IATaskView> getInstanciatedTasks()
	{
		return displayedTasks;
	}

	public Object getMutex()
	{
		return mutex;
	}

	public void addTask(final IATask task, final IATaskView view)
	{
		synchronized (mutex)
		{
			final String name = "Ausführung " + task.getTaskNumber();
			displayedTasks.put(name, view);
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					synchronized (mutex)
					{
						revalidate();
						repaint();
					}
				}
			});

		}

	}

	public void taskEventOccur(ATaskEvent event)
	{
		final IATask task = (IATask) event.getSource();
		if (event.getCommand().equals(AConstants.TASK_LÄUFT))
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					String name = "Ausführung " + task.getTaskNumber();
					if (!((DefaultListModel) list.getModel()).contains(name))
					{
						((DefaultListModel) list.getModel()).addElement(name);
					}
					list.setSelectedValue(name, true);
					
					update(displayedTasks.get(name));
				}
			});
		}
	}

	public Integer getTaskNumber()
	{
		lastTaskNumber++;
		return lastTaskNumber;
	}
	
	private void update(IATaskView view)
	{
		taskProperties = view.getTaskProperties();
		((JSplitPane) generalComp).setRightComponent(taskProperties);
		remove(specialComp);
//		System.out.println(specialComp);
		specialComp = view.getComponent();
		add(specialComp, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));

		revalidate();
		repaint();
		
	}

	public static void main(String[] args)
	{
		MActivity activity = new MActivity();
		activity.setName("Test Activity");
		activity.setClazz(ATaskView.class);
		ATaskCollectionView view = new ATaskCollectionView(activity);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(750, 950);
		frame.add(view);
		frame.setVisible(true);
	}

}
