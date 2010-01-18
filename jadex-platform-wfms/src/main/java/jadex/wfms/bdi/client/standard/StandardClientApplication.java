package jadex.wfms.bdi.client.standard;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IExternalAccess;
import jadex.commons.SGUI;
import jadex.wfms.bdi.client.standard.parametergui.ActivityComponent;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

public class StandardClientApplication
{
	private static final String WINDOW_TITLE = "Workflow Client Application";
	
	private static final String WORKITEM_LIST_TAB_NAME = "Workitem List";
	
	private static final String PROCESS_MODEL_TAB_NAME = "Process Models";
	
	private static final JPanel EMPTY_PANEL = new JPanel();
	
	private static final String CONNECT_ICON_NAME = "Connection";
	private static final String CONNECT_OFF_ICON_PATH = StandardClientApplication.class.getPackage().getName().replaceAll("\\.", "/").concat("/images/connection_off_small.png");
	private static final String CONNECT_ON_ICON_PATH = StandardClientApplication.class.getPackage().getName().replaceAll("\\.", "/").concat("/images/connection_on_small.png");
	
	private IBDIExternalAccess agent;
	
	private JFrame mainFrame;
	
	private StatusBar statusBar;
	
	private JSplitPane mainSplitPane;
	
	private WorkitemListComponent wlComponent;
	
	private ProcessModelComponent pmComponent;
	
	private boolean connected;
	
	public StandardClientApplication(IExternalAccess appAgent)
	{
		connected = false;
		this.agent = (IBDIExternalAccess) appAgent;
		
		agent.addAgentListener(new IAgentListener()
		{
			public void agentTerminating(AgentEvent ae)
			{
			}
			
			public void agentTerminated(AgentEvent ae)
			{
				EventQueue.invokeLater(new Runnable()
				{
					
					public void run()
					{
						mainFrame.dispose();
					}
				});
			}
		});
		
		EventQueue.invokeLater(new Runnable()
		{
			
			public void run()
			{
				mainFrame = new JFrame(WINDOW_TITLE);
				mainFrame.addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						if (connected)
							disconnect();
						agent.killAgent();
						mainFrame.dispose();
					}
				});
				
				mainSplitPane = new JSplitPane();
				mainSplitPane.setOneTouchExpandable(true);
				mainFrame.getContentPane().setLayout(new GridBagLayout());
				GridBagConstraints g = new GridBagConstraints();
				g.weightx = 1.0;
				g.weighty = 1.0;
				g.fill = GridBagConstraints.BOTH;
				g.anchor = GridBagConstraints.CENTER;
				mainFrame.getContentPane().add(mainSplitPane, g);
				
				statusBar = new StatusBar();
				statusBar.addIcon(CONNECT_ICON_NAME, CONNECT_OFF_ICON_PATH);
				statusBar.setText("Ready.");
				statusBar.setPreferredSize(new Dimension(100, 24));
				
				g = new GridBagConstraints();
				g.gridy = 1;
				g.weightx = 1.0;
				g.fill = GridBagConstraints.HORIZONTAL;
				mainFrame.getContentPane().add(statusBar, g);
				
				mainSplitPane.setRightComponent(new JTabbedPane());
				
				JTabbedPane leftTabPane = new JTabbedPane();
				mainSplitPane.setLeftComponent(leftTabPane);
				
				wlComponent = new WorkitemListComponent();
				leftTabPane.add(WORKITEM_LIST_TAB_NAME, wlComponent);
				
				pmComponent = new ProcessModelComponent();
				leftTabPane.add(PROCESS_MODEL_TAB_NAME, pmComponent);
				
				mainFrame.pack();
				mainFrame.setSize(800, 550);
				mainFrame.setLocation(SGUI.calculateMiddlePosition(mainFrame));
				mainFrame.setVisible(true);
				mainSplitPane.setDividerLocation(0.4);
				
				setAgentActions();
				
				showConnectDialog();
				
				initializeWorkitemList();
				initializeActivities();
				updateProcessModels();
				setActions();
			}
		});
		
	}
	
	private void setActions()
	{
		pmComponent.setStartAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				String processName = pmComponent.getSelectedModelName();
				if (processName != null)
				{
					IGoal startProcess = agent.createGoal("clientcap.start_process");
					startProcess.getParameter("process_name").setValue(processName);
					try
					{
						agent.dispatchTopLevelGoalAndWait(startProcess);
					}
					catch (GoalFailureException e1)
					{
						JOptionPane.showMessageDialog(mainFrame, "Process Start failed.");
					}
				}
			}
		});
		
		wlComponent.setBeginActivityAction(new AbstractAction()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				IWorkitem wi = wlComponent.getSelectedWorkitem();
				if (wi != null)
				{
					IGoal beginActivity = agent.createGoal("clientcap.begin_activity");
					beginActivity.getParameter("workitem").setValue(wi);
					try
					{
						agent.dispatchTopLevelGoalAndWait(beginActivity);
					}
					catch (GoalFailureException e1)
					{
						JOptionPane.showMessageDialog(mainFrame, "Start of activity failed.");
					}
				}
			}
		});
	}
	
	private void setAgentActions()
	{
		Action wiAdded = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				IWorkitem wi = (IWorkitem) e.getSource();
				wlComponent.addWorkitem(wi);
			}
		};
		agent.getBeliefbase().getBelief("add_workitem_controller").setFact(wiAdded);
		
		Action wiRemoved = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				IWorkitem wi = (IWorkitem) e.getSource();
				wlComponent.removeWorkitem(wi);
			}
		};
		agent.getBeliefbase().getBelief("remove_workitem_controller").setFact(wiRemoved);
		
		Action acAdded = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				if (mainSplitPane.getRightComponent() instanceof ActivityComponent)
				{
					ActivityComponent currentAc = (ActivityComponent) mainSplitPane.getRightComponent();
					cancelActivity(currentAc);
					mainSplitPane.setRightComponent(EMPTY_PANEL);
				}
				
				IClientActivity activity = (IClientActivity) e.getSource();
				ActivityComponent ac = createActivityComponent(activity);
				
				if (mainSplitPane.getRightComponent().equals(EMPTY_PANEL))
				{
					mainSplitPane.setRightComponent(ac);
				}
				else if (mainSplitPane.getRightComponent() instanceof JTabbedPane)
				{
					JTabbedPane tabPane = (JTabbedPane) mainSplitPane.getRightComponent();
					tabPane.add(ac, ac.getActivity().getName());
					tabPane.setSelectedComponent(ac);
				}
			}
		};
		agent.getBeliefbase().getBelief("add_activity_controller").setFact(acAdded);
		
		Action acRemoved = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				IClientActivity activity = (IClientActivity) e.getSource();
				
				if (mainSplitPane.getRightComponent() instanceof ActivityComponent)
				{
					ActivityComponent ac = (ActivityComponent) mainSplitPane.getRightComponent();
					if (activity.equals(ac.getActivity()))
						mainSplitPane.setRightComponent(EMPTY_PANEL);
				}
				else if (mainSplitPane.getRightComponent() instanceof JTabbedPane)
				{
					JTabbedPane tabPane = (JTabbedPane) mainSplitPane.getRightComponent();
					for (int i = 0; i < tabPane.getTabCount(); ++i)
					{
						ActivityComponent ac = (ActivityComponent) tabPane.getComponent(i);
						if (ac.getActivity().equals(activity))
						{
							tabPane.remove(i);
							return;
						}
					}
				}
			}
		};
		agent.getBeliefbase().getBelief("remove_activity_controller").setFact(acRemoved);
		
		Action lcAction = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				connected = false;
				statusBar.replaceIcon(CONNECT_ICON_NAME, CONNECT_OFF_ICON_PATH);
				cleanUp();
				showConnectDialog();
			}
		};
		agent.getBeliefbase().getBelief("lost_connection_controller").setFact(lcAction);
	}
	
	private void cleanUp()
	{
		pmComponent.clear();
		wlComponent.clear();
		
		if (mainSplitPane.getRightComponent() instanceof JTabbedPane)
			mainSplitPane.setRightComponent(new JTabbedPane());
		else
			mainSplitPane.setRightComponent(EMPTY_PANEL);
	}
	
	private void showConnectDialog()
	{
		while (!connected)
		{
			LoginDialog loginDialog = new LoginDialog(mainFrame);
			loginDialog.setLocation(SGUI.calculateMiddlePosition(loginDialog));
			loginDialog.setVisible(true);
			try
			{
				connect(loginDialog.getUserName(), loginDialog.getPassword());
				connected = true;
				statusBar.replaceIcon(CONNECT_ICON_NAME, CONNECT_ON_ICON_PATH);
				statusBar.setText("Connected.");
			}
			catch (GoalFailureException e)
			{
			}
		}
	}
	
	private void connect(String userName, Object authToken)
	{
		IGoal connect = agent.createGoal("clientcap.connect");
		connect.getParameter("user_name").setValue(userName);
		connect.getParameter("auth_token").setValue(authToken);
		agent.dispatchTopLevelGoalAndWait(connect);
	}
	
	private void disconnect()
	{
		try
		{
			IGoal disconnect = agent.createGoal("clientcap.disconnect");
			agent.dispatchTopLevelGoalAndWait(disconnect);
		}
		catch (GoalFailureException e)
		{
		}
	}
	
	private void initializeWorkitemList()
	{
		IGoal subscribe = agent.createGoal("clientcap.start_workitem_subscription");
		agent.dispatchTopLevelGoalAndWait(subscribe);
		
		IGoal requestListGoal = agent.createGoal("clientcap.request_workitem_list");
		agent.dispatchTopLevelGoalAndWait(requestListGoal);
		Set workitemList = (Set) requestListGoal.getParameter("workitem_list").getValue();
		wlComponent.setWorkitems(workitemList);
	}
	
	private void initializeActivities()
	{
		IGoal requestListGoal = agent.createGoal("clientcap.request_activity_list");
		agent.dispatchTopLevelGoalAndWait(requestListGoal);
		Set activityList = (Set) requestListGoal.getParameter("activity_list").getValue();
		
		for (Iterator it = activityList.iterator(); it.hasNext(); )
		{
			IClientActivity act = (IClientActivity) it.next();
			if (mainSplitPane.getRightComponent() instanceof JTabbedPane)
			{
				JTabbedPane tabPane = (JTabbedPane) mainSplitPane.getRightComponent();
				ActivityComponent ac = createActivityComponent(act);
				tabPane.add(ac, ac.getActivity().getName());
				tabPane.setSelectedComponent(ac);
			}
			else if (mainSplitPane.getRightComponent() instanceof ActivityComponent)
			{
				cancelActivity(act);
			}
			else
			{
				ActivityComponent ac = createActivityComponent(act);
				mainSplitPane.setRightComponent(ac);
			}
		}
	}
	
	private void updateProcessModels()
	{
		IGoal modelNameGoal = agent.createGoal("clientcap.request_model_names");
		agent.dispatchTopLevelGoalAndWait(modelNameGoal);
		Set modelNames = (Set) modelNameGoal.getParameter("model_names").getValue();
		pmComponent.setProcessModelNames(modelNames);
	}
	
	private ActivityComponent createActivityComponent(IClientActivity activity)
	{
		final ActivityComponent ac = new ActivityComponent(activity);
		ac.setCancelAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				cancelActivity(ac);
			}
		});
		
		ac.setSuspendAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				suspendActivity(ac);
			}
		});
		
		ac.setFinishAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (!ac.isReadyForFinish())
					return;
				
				IGoal finishGoal = agent.createGoal("clientcap.finish_activity");
				IClientActivity activity = ac.getActivity();
				
				activity.setMultipleParameterValues(ac.getParameterValues());
				
				finishGoal.getParameter("activity").setValue(activity);
				
				try
				{
					agent.dispatchTopLevelGoalAndWait(finishGoal);
				}
				catch (GoalFailureException e1)
				{
					JOptionPane.showMessageDialog(mainFrame, "Failed finishing activity.");
					return;
				}
			}
		});
		
		return ac;
	}
	
	private void suspendActivity(ActivityComponent ac)
	{
		IClientActivity activity = ac.getActivity();
		Map parameterValues = ac.getParameterValues();
		activity.setMultipleParameterValues(parameterValues);
		
		cancelActivity(ac);
	}
	
	private void cancelActivity(ActivityComponent ac)
	{
		cancelActivity(ac.getActivity());
	}
	
	private void cancelActivity(IClientActivity activity)
	{
		IGoal cancelGoal = agent.createGoal("clientcap.cancel_activity");
		cancelGoal.getParameter("activity").setValue(activity);
		
		try
		{
			agent.dispatchTopLevelGoalAndWait(cancelGoal);
		}
		catch (GoalFailureException e)
		{
			JOptionPane.showMessageDialog(mainFrame, "Activity cancelation failed.");
		}
	}
}

