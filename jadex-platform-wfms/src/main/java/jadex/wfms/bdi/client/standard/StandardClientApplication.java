package jadex.wfms.bdi.client.standard;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IEAGoal;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IExternalAccess;
import jadex.commons.SGUI;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.wfms.SwingGoalDispatchResultListener;
import jadex.wfms.bdi.client.standard.parametergui.ActivityComponent;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.guicomponents.LoginDialog;
import jadex.wfms.guicomponents.SGuiHelper;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;

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
	
	private ProcessModelTreeComponent pmComponent;
	
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
				pmComponent = new ProcessModelTreeComponent();
				aaComponent = new AdminActivitiesComponent();
				
				statusBar.setIconAction(CONNECT_ICON_NAME, new AbstractAction()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (e.getActionCommand().equals(CONNECT_OFF_ICON_PATH))
							showConnectDialog();
						else
							disconnect();
					}
				});
			}
		});
		
	}
	
	private void cleanUp()
	{
		pmComponent.clear();
		wlComponent.clear();
		toolPane.removeAll();
		flushActions();
		
		//mainSplitPane.setLeftComponent(new JTabbedPane());
		
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
	
	private void connect(final String userName, final Object authToken)
	{
		agent.createGoal("clientcap.connect").addResultListener(new SwingGoalDispatchResultListener(agent)
		{
			public void configureGoal(IEAGoal goal)
			{
				goal.setParameterValue("user_name", userName);
				goal.setParameterValue("auth_token", authToken);
			}
			
			public void goalResultsAvailable(Map parameters)
			{
				capabilities = (Set) parameters.get("capabilities");
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
		});
	}
	
	private void disconnect()
	{
		agent.createGoal("clientcap.disconnect").addResultListener(new SwingGoalDispatchResultListener(agent)
		{
			public void goalResultsAvailable(Map parameters)
			{
				cleanUp();
				statusBar.replaceIcon(CONNECT_ICON_NAME, CONNECT_OFF_ICON_PATH);
				connected = false;
			}
		});
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
				
				final IClientActivity activity = ac.getActivity();
				activity.setMultipleParameterValues(ac.getParameterValues());
				agent.createGoal("clientcap.finish_activity").addResultListener(new SwingGoalDispatchResultListener(agent)
				{
					public void configureGoal(IEAGoal goal)
					{
						goal.setParameterValue("activity", activity);
					}
					
					public void goalExceptionOccurred(
							Exception exception)
					{
						JOptionPane.showMessageDialog(mainFrame, "Failed finishing activity.");
					}
				});
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
	
	private void cancelActivity(final IClientActivity activity)
	{
		agent.createGoal("clientcap.cancel_activity").addResultListener(new SwingGoalDispatchResultListener(agent)
		{
			public void configureGoal(IEAGoal goal)
			{
				goal.setParameterValue("activity", activity);
			}
			
			public void goalExceptionOccurred(Exception exception)
			{
				JOptionPane.showMessageDialog(mainFrame, "Activity cancelation failed.");
			}
		});
	}
	
	private void setupWorkitemListComponent()
	{
		wlComponent.setBeginActivityAction(new AbstractAction()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				final IWorkitem wi = wlComponent.getSelectedWorkitem();
				if (wi != null)
				{
					agent.createGoal("clientcap.begin_activity").addResultListener(new SwingGoalDispatchResultListener(agent)
					{
						public void configureGoal(IEAGoal goal)
						{
							goal.setParameterValue("workitem", wi);
						}
						
						public void goalExceptionOccurred(
								Exception exception)
						{
							JOptionPane.showMessageDialog(mainFrame, "Start of activity failed.");
						}
					});
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
		agent.getBeliefbase().setBeliefFact("clientcap.add_workitem_controller", wiAdded);
		
		Action wiRemoved = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				IWorkitem wi = (IWorkitem) e.getSource();
				wlComponent.removeWorkitem(wi);
			}
		};
		agent.getBeliefbase().setBeliefFact("clientcap.remove_workitem_controller", wiRemoved);
		
		agent.createGoal("clientcap.start_workitem_subscription").addResultListener(new SwingGoalDispatchResultListener(agent));
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
		agent.getBeliefbase().setBeliefFact("clientcap.add_process_model_controller", pmAdded);
		
		Action pmRemoved = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				String modelName = (String) e.getSource();
				pmComponent.removeProcessModelName(modelName);
			}
		};
		agent.getBeliefbase().setBeliefFact("clientcap.remove_process_model_controller", pmRemoved);
		
		agent.createGoal("clientcap.start_model_repository_subscription").addResultListener(new SwingGoalDispatchResultListener(agent));
		
		pmComponent.setStartAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				final String processName = pmComponent.getSelectedModelName();
				if (processName != null)
				{
					agent.createGoal("clientcap.start_process").addResultListener(new SwingGoalDispatchResultListener(agent) 
					{
						public void configureGoal(IEAGoal goal)
						{
							goal.setParameterValue("process_name", processName);
						}
						
						public void goalExceptionOccurred(
								Exception exception)
						{
							JOptionPane.showMessageDialog(mainFrame, "Process Start failed.");
						}
					});
				}
			}
		});
		
		pmComponent.setAddProcessAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				/*IGoal modelGoal = agent.createGoal("clientcap.request_loadable_model_paths");
				agent.dispatchTopLevelGoalAndWait(modelGoal);
				Set modelSet = (Set) modelGoal.getParameter("loadable_model_paths").getValue();
				AddProcessModelDialog dialog = new AddProcessModelDialog(mainFrame, modelSet);
				dialog.setLocation(SGUI.calculateMiddlePosition(dialog));
				dialog.setVisible(true);
				
				String path = dialog.getProcessPath();
				if (path == null)
					return;
				
				IGoal addGoal = agent.createGoal("clientcap.add_process");
				addGoal.getParameter("process_path").setValue(path);
				try
				{
					agent.dispatchTopLevelGoalAndWait(addGoal);
				}
				catch (GoalFailureException e1)
				{
					JOptionPane.showMessageDialog(mainFrame, "Adding process failed.");
				}*/
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileFilter()
				{
					public String getDescription()
					{
						return "*.jar";
					}
					
					public boolean accept(File f)
					{
						return f.getName().toLowerCase().endsWith(".jar");
					}
				});
				int result = fileChooser.showOpenDialog(mainFrame);
				if (result == JFileChooser.APPROVE_OPTION)
				{
					final String path = fileChooser.getSelectedFile().getAbsolutePath();
					agent.createGoal("clientcap.add_model_resource").addResultListener(new SwingGoalDispatchResultListener(agent)
					{
						public void configureGoal(IEAGoal goal)
						{
							goal.setParameterValue("resource_path", path);
						}
					});
				}
			}
		});
		
		pmComponent.setRemoveProcessAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				final String name = pmComponent.getSelectedModelName();
				if (name != null)
				{
					agent.createGoal("clientcap.remove_process").addResultListener(new SwingGoalDispatchResultListener(agent)
					{
						public void configureGoal(IEAGoal goal)
						{
							goal.setParameterValue("process_name", name);
						};
						
						public void goalExceptionOccurred(Exception exception)
						{
							JOptionPane.showMessageDialog(mainFrame, "Removing process failed.");
						};
					});
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
				final IClientActivity activity = aaComponent.getSelectedActivity();
				if (activity != null)
				{
					agent.createGoal("clientcap.terminate_activity").addResultListener(new SwingGoalDispatchResultListener(agent)
					{
						public void configureGoal(IEAGoal goal)
						{
							goal.setParameterValue("activity", activity);
						}
						
						public void goalExceptionOccurred(
								Exception exception)
						{
							JOptionPane.showMessageDialog(mainFrame, "Activity termination failed.");
						}
					});
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
		agent.getBeliefbase().setBeliefFact("clientcap.add_user_activity_controller", uacAdded);
		
		Action uacRemoved = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				aaComponent.removeUserActivity(e.getActionCommand(), (IClientActivity) e.getSource());
			}
		};
		agent.getBeliefbase().setBeliefFact("clientcap.remove_user_activity_controller", uacRemoved);
		
		agent.createGoal("clientcap.start_user_activities_subscription").addResultListener(new SwingGoalDispatchResultListener(agent));
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
					tabPane.add(ac, SGuiHelper.beautifyName(ac.getActivity().getName()));
					tabPane.setSelectedComponent(ac);
				}
			}
		};
		agent.getBeliefbase().setBeliefFact("clientcap.add_activity_controller", acAdded);
		
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
		agent.getBeliefbase().setBeliefFact("clientcap.remove_activity_controller", acRemoved);
		
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
		agent.getBeliefbase().setBeliefFact("clientcap.lost_connection_controller", lcAction);
		
		agent.createGoal("clientcap.start_activity_subscription").addResultListener(new SwingGoalDispatchResultListener(agent));
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
		
		agent.getBeliefbase().setBeliefFact("clientcap.add_workitem_controller", null);
		agent.getBeliefbase().setBeliefFact("clientcap.remove_workitem_controller", null);
		agent.getBeliefbase().setBeliefFact("clientcap.add_user_activity_controller", null);
		agent.getBeliefbase().setBeliefFact("clientcap.remove_user_activity_controller", null);
		agent.getBeliefbase().setBeliefFact("clientcap.add_process_model_controller", null);
		agent.getBeliefbase().setBeliefFact("clientcap.remove_process_model_controller", null);
	}
}

