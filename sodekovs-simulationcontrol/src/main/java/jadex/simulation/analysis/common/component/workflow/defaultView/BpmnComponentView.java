package jadex.simulation.analysis.common.component.workflow.defaultView;

import jadex.base.gui.componenttree.ComponentProperties;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.runtime.ExternalAccess;
import jadex.bpmn.runtime.IActivityListener;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.simulation.analysis.common.component.workflow.tasks.general.IATask;
import jadex.simulation.analysis.common.component.workflow.tasks.general.IATaskView;
import jadex.simulation.analysis.common.events.task.ATaskEvent;
import jadex.simulation.analysis.common.events.task.IATaskListener;
import jadex.simulation.analysis.common.util.AConstants;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class BpmnComponentView extends JTabbedPane implements IATaskListener
{
	protected ExternalAccess instance;
	protected IComponentDescription desc;

	protected ThreadSuspendable susThread = new ThreadSuspendable(this);
	protected Object mutex = new Object();

	protected JPanel generalcomp;
	protected Map<String, ATaskCollectionView> tasks = new HashMap<String, ATaskCollectionView>();

	protected Set<String> activeActivities;
	private JList list;
	private JPanel listPanel;
	protected ComponentProperties compProp;
	private boolean init = false;

	public BpmnComponentView(IExternalAccess access)
	{
		super();
		synchronized (mutex)
		{
			this.instance = ((ExternalAccess) access);
			// instance.getInterpreter().addActivityListener(this);
			synchronized (mutex)
			{
				compProp = new ComponentProperties();
				
				DefaultListModel listModel = new DefaultListModel();
				list = new JList(listModel);
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						generalcomp = new JPanel(new GridBagLayout());
						Insets insets = new Insets(2, 2, 2, 2);

						compProp.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Jadex 'bpmn active component' Eigenschaften "));
						generalcomp.add(compProp, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

						// active activity list
	
						list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						list.setLayoutOrientation(JList.VERTICAL);
						list.setVisibleRowCount(-1);
						list.setEnabled(false);
						list.setSelectedIndex(0);
						// list.setPreferredSize(new Dimension(250, 250));

						JScrollPane listScroller = new JScrollPane(list);
						// listScroller.setPreferredSize(new Dimension(250, 275));

						listPanel = new JPanel(new GridBagLayout());
						// listPanel.setPreferredSize(new Dimension(800, 300));
						listPanel.add(listScroller,
									new GridBagConstraints(0, 0, 1, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
						listPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Aktive Aktivitäten:"));
						// generalcomp.add(listPanel, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

						generalcomp.add(listPanel,
									new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
						addTab("Allgemein", null, generalcomp);
						setSelectedComponent(generalcomp);

						validate();
						updateUI();
					}
				});
			}

		}
	}

	public void init()
	{
		synchronized (mutex)
		{
			if (!init)
			{
				IFuture cmsFut = SServiceProvider.getService(instance.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
				IComponentManagementService cms = (IComponentManagementService) cmsFut.get(susThread);
				IFuture descFut = cms.getComponentDescription(instance.getComponentIdentifier());
				desc = (IComponentDescription) descFut.get(susThread);

				Map<String, MActivity> allActivities = instance.getInterpreter().getModelElement().getAllActivities();
				for (final MActivity activity : allActivities.values())
				{
					if (activity.getActivityType().equals(MBpmnModel.TASK))
					{
						final ATaskCollectionView taskColl = new ATaskCollectionView(activity);
						tasks.put(activity.getName(), taskColl);
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								addTab(activity.getName(), null, taskColl);
							}
						});
					}
				}
				;
				compProp.setDescription(desc);
				init = true;

			}
		}
	}

	public void registerTask(final IATask task, final IATaskView view)
	{
		synchronized (mutex)
		{
			if (!init)
			{
				init();
			}
			task.addTaskListener(this);
			task.setTaskNumber(tasks.get(task.getActivity().getName()).getTaskNumber());
			tasks.get(task.getActivity().getName()).addTask(task, view);
		}
	}

	@Override
	public void taskEventOccur(ATaskEvent event)
	{
		final IATask task = (IATask) event.getSource();
		if (event.getCommand().equals(AConstants.TASK_LÄUFT))
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					((DefaultListModel) list.getModel()).addElement(task.getActivity().getName() + " (" + task.getTaskNumber() + ")");
				}
			});
		}
		else if (event.getCommand().equals(AConstants.TASK_BEENDET))
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					((DefaultListModel) list.getModel()).removeElement(task.getActivity().getName() + " (" + task.getTaskNumber() + ")");
				}
			});
		}
		tasks.get(task.getActivity().getName()).taskEventOccur(event);
	}
}
