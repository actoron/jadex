package jadex.simulation.analysis.common.superClasses.service.view.session.subprocess;

import jadex.bpmn.model.MActivity;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.events.IAListener;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;
import jadex.simulation.analysis.common.superClasses.tasks.IATask;
import jadex.simulation.analysis.common.superClasses.tasks.IATaskView;
import jadex.simulation.analysis.common.superClasses.tasks.TaskProperties;
import jadex.simulation.analysis.common.util.AConstants;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ATaskCollectionView extends JPanel implements IAListener
{
	protected Object mutex = new Object();

	protected Map<String, IATaskView> displayedTasks;
	final protected JList list = new JList(new DefaultListModel());
	protected JSplitPane generalComp;
	// protected JComponent specialComp;
	protected JPanel dummy = new JPanel();

	protected TaskProperties taskProperties;
	protected TaskProperties generalTaskProperties;

	private Integer lastTaskNumber = 0;

	public ATaskCollectionView(final ASubProcessView parent, final MActivity activity)
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

						// specialComp = dummy;
						// specialComp.setPreferredSize(new Dimension(750, 750));

						generalComp = new JSplitPane();
						final Insets insets = new Insets(1, 1, 1, 1);

						// list
						list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						list.setLayoutOrientation(JList.VERTICAL);
//						list.setEnabled(false);
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
							((DefaultListModel) list.getModel()).addElement(task);
						}

						JScrollPane listScroller = new JScrollPane(list);
						listScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
						listScroller.setPreferredSize(new Dimension(250, 150));
						listScroller.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Ausfï¿½hrungen"));
						JPanel leftPanel = new JPanel(new GridBagLayout());
						leftPanel.add(listScroller,
								new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

						JButton showButton = new JButton("Anzeigen");
						leftPanel.add(showButton,
								new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
						showButton.addActionListener(new ActionListener()
						{

							@Override
							public void actionPerformed(ActionEvent e)
							{
								Object select = list.getSelectedValue();
								if (select != null)
								{
									IATaskView taskView = displayedTasks.get(select);
									

									ATaskInternalFrame frame = new ATaskInternalFrame("Task: " + taskView.getDisplayedTask().getActivity().getName() + " (" + select + ")", true, true, true, false);
									taskView.setParent(frame);
									frame.setVisible(true);
									frame.add(taskView.getComponent());
									frame.setSize(new Dimension(500, 500));
//									frame.setLocation(100, 100);
									parent.add(frame);
									try
									{
										frame.setMaximum(true);
										frame.setSelected(true);
									}
									catch (PropertyVetoException e1)
									{
										// omit
									}
								}
							}
						});

						leftPanel.setPreferredSize(new Dimension(150, 200));
						((JSplitPane) generalComp).setLeftComponent(leftPanel);

						generalTaskProperties = new TaskProperties();
						generalTaskProperties.getTextField("Activitï¿½tsname").setText(activity.getName());
//						System.out.println(activity);
//						System.out.println(activity.getClazz());
						generalTaskProperties.getTextField("Activitï¿½tsklasse").setText(activity.getClazz().getName().toString());
						// generalTaskProperties.getTextField("Viewerklasse").setText("Noch nicht bekannt");

						taskProperties = generalTaskProperties;
						((JSplitPane) generalComp).setRightComponent(taskProperties);

						// ((JSplitPane) generalComp).setDividerLocation(200);
						generalComp.setPreferredSize(new Dimension(750, 200));

						generalComp.revalidate();
						generalComp.repaint();
						add(generalComp, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
						// add(specialComp, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0,
						// 0));
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
			final String name = "Ausfï¿½hrung " + task.getTaskNumber();
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

	public void update(ATaskEvent event)
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

		revalidate();
		repaint();

	}

	@Override
	public void update(IAEvent event)
	{
		//omit
		
	}


}
