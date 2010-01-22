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
	
	private static final String ADMIN_ACTIVITIES_TAB_NAME = "Activities";
	
	private static final JPanel EMPTY_PANEL = new JPanel();
	
	private static final String CONNECT_ICON_NAME = "Connection";
	private static final String CONNECT_OFF_ICON_PATH = StandardClientApplication.class.getPackage().getName().replaceAll("\\.", "/").concat("/images/connection_off_small.png");
	private static final String CONNECT_ON_ICON_PATH = StandardClientApplication.class.getPackage().getName().replaceAll("\\.", "/").concat("/images/connection_on_small.png");
	
	private IBDIExternalAccess agent;
	
	private JFrame mainFrame;
	
	private StatusBar statusBar;
	
	private JSplitPane mainSplitPane;
	
	private JTabbedPane toolPane;
	
	private WorkitemListComponent wlComponent;
	
	private ProcessModelComponent pmComponent;
	
	private AdminActivitiesComponent aaComponent;
	
	private boolean connected;
	
	private Set capabilities;
	
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
				
				toolPane = new JTabbedPane();
				mainSplitPane.setLeftComponent(toolPane);
				
				mainFrame.pack();
				mainFrame.setSize(800, 550);
				mainFrame.setLocation(SGUI.calculateMiddlePosition(mainFrame));
				mainFrame.setVisible(true);
				mainSplitPane.setDividerLocation(0.45);
				
				wlComponent = new WorkitemListComponent();
				pmComponent = new ProcessModelComponent();
				aaComponent = new AdminActivitiesComponent();
				
				showConnectDialog();
			}
		});
		
	}
	
	private void cleanUp()
	{
		pmComponent.clear();
		wlComponent.clear();
		toolPane.removeAll();
		flushActions();
		
		mainSplitPane.setLeftComponent(new JTabbedPane());
		
		if (mainSplitPane.getRightComponent() instanceof JTabbedPane)
			mainSplitPane.setRightComponent(new JTabbedPane());
		else
			mainSplitPane.setRightComponent(EMPTY_PANEL);
		
		mainSplitPane.setDividerLocation(0.45);
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
				e.printStackTrace();
				cleanUp();
			}
		}
	}
	
	private void connect(String userName, Object authToken)
	{
		IGoal connect = agent.createGoal("clientcap.connect");
		connect.getParameter("user_name").setValue(userName);
		connect.getParameter("auth_token").setValue(authToken);
		agent.dispatchTopLevelGoalAndWait(connect);
		
		capabilities = (Set) connect.getParameter("capabilities").getValue();
		
		if (capabilities.containsAll(SCapReqs.ACTIVITY_HANDLING))
			setupActivityHandling();
		
		if (capabilities.containsAll(SCapReqs.WORKITEM_LIST))
		{
			toolPane.add(WORKITEM_LIST_TAB_NAME, wlComponent);
			setupWorkitemListComponent();
		}
		
		if (capabilities.containsAll(SCapReqs.PROCESS_LIST))
		{
			toolPane.add(PROCESS_MODEL_TAB_NAME, pmComponent);
			setupProcessModelComponent();
		}
		
		if (capabilities.containsAll(SCapReqs.ADMIN_ACTIVITIES))
		{
			toolPane.add(ADMIN_ACTIVITIES_TAB_NAME, aaComponent);
			setupAdminActivitiesComponent();
		}
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
	
	private void setupWorkitemListComponent()
	{
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
		
		Action wiAdded = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				IWorkitem wi = (IWorkitem) e.getSource();
				wlComponent.addWorkitem(wi);
			}
		};
		agent.getBeliefbase().getBelief("clientcap.add_workitem_controller").setFact(wiAdded);
		
		Action wiRemoved = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				IWorkitem wi = (IWorkitem) e.getSource();
				wlComponent.removeWorkitem(wi);
			}
		};
		agent.getBeliefbase().getBelief("clientcap.remove_workitem_controller").setFact(wiRemoved);
		
		IGoal subscribe = agent.createGoal("clientcap.start_workitem_subscription");
		agent.dispatchTopLevelGoalAndWait(subscribe);
	}
	
	private void setupProcessModelComponent()
	{
		Action pmAdded = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				String modelName = (String) e.getSource();
				pmComponent.addProcessModelName(modelName);
			}
		};
		agent.getBeliefbase().getBelief("clientcap.add_process_model_controller").setFact(pmAdded);
		
		Action pmRemoved = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				String modelName = (String) e.getSource();
				pmComponent.removeProcessModelName(modelName);
			}
		};
		agent.getBeliefbase().getBelief("clientcap.remove_process_model_controller").setFact(pmRemoved);
		
		IGoal subscribe = agent.createGoal("clientcap.start_model_repository_subscription");
		agent.dispatchTopLevelGoalAndWait(subscribe);
		
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
		
		pmComponent.setAddProcessAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				String path = (String)JOptionPane.showInputDialog(mainFrame,
																  "Enter new process path:",
	                    										  "Add Process",
	                    										  JOptionPane.PLAIN_MESSAGE,
	                    										  null, null, null);
				IGoal addGoal = agent.createGoal("clientcap.add_process");
				addGoal.getParameter("process_path").setValue(path);
				try
				{
					agent.dispatchTopLevelGoalAndWait(addGoal);
				}
				catch (GoalFailureException e1)
				{
					JOptionPane.showMessageDialog(mainFrame, "Adding process failed.");
				}
			}
		});
		
		pmComponent.setRemoveProcessAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				String name = pmComponent.getSelectedModelName();
				if (name != null)
				{
					IGoal removeGoal = agent.createGoal("clientcap.remove_process");
					removeGoal.getParameter("process_name").setValue(name);
					
					try
					{
						agent.dispatchTopLevelGoalAndWait(removeGoal);
					}
					catch (GoalFailureException e1)
					{
						JOptionPane.showMessageDialog(mainFrame, "Removing process failed.");
					}
				}
			}
		});
	}
	
	private void setupAdminActivitiesComponent()
	{
		aaComponent.setTerminateAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				IClientActivity activity = aaComponent.getSelectedActivity();
				if (activity != null)
				{
					IGoal terminate = agent.createGoal("clientcap.terminate_activity");
					terminate.getParameter("activity").setValue(activity);
					try
					{
						agent.dispatchTopLevelGoalAndWait(terminate);
					}
					catch (GoalFailureException e1)
					{
						JOptionPane.showMessageDialog(mainFrame, "Activity termination failed.");
					}
				}
			}
		});
		
		Action uacAdded = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				aaComponent.addUserActivity(e.getActionCommand(), (IClientActivity) e.getSource());
			}
		};
		agent.getBeliefbase().getBelief("clientcap.add_user_activity_controller").setFact(uacAdded);
		
		Action uacRemoved = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				aaComponent.removeUserActivity(e.getActionCommand(), (IClientActivity) e.getSource());
			}
		};
		agent.getBeliefbase().getBelief("clientcap.remove_user_activity_controller").setFact(uacRemoved);
		
		IGoal subscribe = agent.createGoal("clientcap.start_user_activities_subscription");
		agent.dispatchTopLevelGoalAndWait(subscribe);
	}
	
	private void setupActivityHandling()
	{
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
		agent.getBeliefbase().getBelief("clientcap.add_activity_controller").setFact(acAdded);
		
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
		agent.getBeliefbase().getBelief("clientcap.remove_activity_controller").setFact(acRemoved);
		
		//TODO: put somewhere else
		
		
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
		agent.getBeliefbase().getBelief("clientcap.lost_connection_controller").setFact(lcAction);
		
		IGoal subscribe = agent.createGoal("clientcap.start_activity_subscription");
		agent.dispatchTopLevelGoalAndWait(subscribe);
	}
	
	public void flushActions()
	{
		Action emptyAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
			}
		};
		wlComponent.setBeginActivityAction(emptyAction);
		pmComponent.setStartAction(emptyAction);
		aaComponent.setTerminateAction(emptyAction);
		
		agent.getBeliefbase().getBelief("clientcap.add_workitem_controller").setFact(null);
		agent.getBeliefbase().getBelief("clientcap.remove_workitem_controller").setFact(null);
		agent.getBeliefbase().getBelief("clientcap.add_user_activity_controller").setFact(null);
		agent.getBeliefbase().getBelief("clientcap.remove_user_activity_controller").setFact(null);
		agent.getBeliefbase().getBelief("clientcap.add_process_model_controller").setFact(null);
		agent.getBeliefbase().getBelief("clientcap.remove_process_model_controller").setFact(null);
	}
}

