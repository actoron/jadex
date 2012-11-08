package jadex.simulation.analysis.common.superClasses.service.view.session.subprocess;

import jadex.base.gui.componenttree.ProvidedServiceInfoProperties;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.events.IAListener;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;
import jadex.simulation.analysis.common.superClasses.service.view.session.IASessionView;
import jadex.simulation.analysis.common.superClasses.service.view.session.SessionProperties;
import jadex.simulation.analysis.common.superClasses.tasks.IATask;
import jadex.simulation.analysis.common.superClasses.tasks.IATaskView;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.common.util.workflowGraph.GraphPanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class ASubProcessView extends JDesktopPane implements IASessionView, IAListener
{
	protected Object mutex = new Object();
	protected Map<String, ATaskCollectionView> tasks = new HashMap<String, ATaskCollectionView>();

	protected JSplitPane basicPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	protected JPanel bottom = new JPanel(new GridBagLayout());
	protected JPanel top = new JPanel(new GridBagLayout());
	protected SessionProperties prop = null;
	protected ProvidedServiceInfoProperties serviceProp = null;
	protected GraphPanel graphPanel = null;

	final protected ASubProcessView me = this;

	public ASubProcessView()
	{
		super();
		
	}
	
	public void init(final IExternalAccess instance, final String id, final IAParameterEnsemble configuration)
	{
		synchronized (mutex)
		{
			final GraphPanel graph = new GraphPanel(me,instance);
			graphPanel = graph;

			instance.scheduleStep(new IComponentStep<Void>()
			{
				@Override
				public IFuture<Void> execute(IInternalAccess ia)
				{
					Map<String, MActivity> allActivities = ((BpmnInterpreter) ia).getModelElement().getAllActivities();
					for (final MActivity activity : allActivities.values())
					{
						if (activity.getActivityType().equals(MBpmnModel.TASK))
						{
							ATaskCollectionView taskColl = new ATaskCollectionView(me,activity);
							tasks.put(activity.getName(), taskColl);
						}
					};
					return IFuture.DONE;
				}
			});

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					prop = new SessionProperties(id, configuration);
					Insets insets = new Insets(1, 1, 1, 1);

					bottom.add(prop, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					// GraphPanel graph = new GraphPanel(instance);
					// graph.initWorkflow(instance);
					top.add(graph, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
					top.setPreferredSize(new Dimension(900, 350));
					bottom.setPreferredSize(new Dimension(900, 250));
					basicPanel.setTopComponent(top);
					basicPanel.setBottomComponent(bottom);
					basicPanel.setDividerLocation(0.3);
					basicPanel.setResizeWeight(0.5);

				    
					ATaskInternalFrame frame = new ATaskInternalFrame("Workflow", false, false, false, false);
					JComponent c = (BasicInternalFrameTitlePane)((BasicInternalFrameUI) frame.getUI()).getNorthPane();  
					c.setPreferredSize(new Dimension(c.getPreferredSize().width, 20));
					frame.setBorder(null);
					frame.setVisible(true);
					  
					frame.add(basicPanel);
					add(frame);
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
			});
		}
	}
	
	public ATaskCollectionView getTaskView(String name)
	{
		return tasks.get(name);
	}
	
	public JPanel getPropertyPanel()
	{
		return bottom;
	}

	public void registerTask(final IATask task, final IATaskView view)
	{
		synchronized (mutex)
		{
			task.addListener(this);
			task.setTaskNumber(tasks.get(task.getActivity().getName()).getTaskNumber());
			tasks.get(task.getActivity().getName()).addTask(task, view);
		}
	}

	@Override
	public void update(IAEvent event)
	{
		synchronized (mutex)
		{
			final IATask task = (IATask) ((ATaskEvent)event).getSource();
			if (event.getCommand().equals(AConstants.TASK_LÄUFT))
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						graphPanel.setTaskStatus(task.getActivity(), AConstants.TASK_LÄUFT);					}
				});
			}
			else if (event.getCommand().equals(AConstants.TASK_BEENDET))
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						graphPanel.setTaskStatus(task.getActivity(), AConstants.TASK_BEENDET);
					}
				});
			}
			else if (event.getCommand().equals(AConstants.TASK_USER))
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						graphPanel.setTaskStatus(task.getActivity(), AConstants.TASK_USER);
					}
				});
			}
			else if (event.getCommand().equals(AConstants.TASK_ABBRUCH))
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						graphPanel.setTaskStatus(task.getActivity(), AConstants.TASK_ABBRUCH);
					}
				});
			}
			tasks.get(task.getActivity().getName()).update(event);
		}
	}

	@Override
	public SessionProperties getSessionProperties()
	{
		return prop;
	}

	@Override
	public JComponent getComponent()
	{
		return me;
	}

}
