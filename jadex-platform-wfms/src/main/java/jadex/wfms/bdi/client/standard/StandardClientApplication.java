package jadex.wfms.bdi.client.standard;

import jadex.base.gui.SwingDefaultResultListener;
import jadex.bdi.planlib.iasteps.DispatchGoalStep;
import jadex.bdi.planlib.iasteps.SetBeliefStep;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.TerminationAdapter;
import jadex.commons.gui.SGUI;
import jadex.wfms.bdi.client.standard.parametergui.ActivityComponent;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.gui.images.SImage;
import jadex.wfms.guicomponents.CenteringLayout;
import jadex.wfms.guicomponents.LoginPanel;
import jadex.wfms.guicomponents.SGuiHelper;
import jadex.wfms.service.IExternalWfmsService;
import jadex.xml.annotation.XMLClassname;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
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
	private static final String CONNECT_OFF_ICON_PATH = SImage.IMAGE_PATH.concat("connection_off_small.png");
	private static final String CONNECT_ON_ICON_PATH = SImage.IMAGE_PATH.concat("connection_on_small.png");
	
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
		
		agent.scheduleStep(new IComponentStep()
		{
			@XMLClassname("dispose") 
			public Object execute(IInternalAccess ia)
			{
				ia.addComponentListener(new TerminationAdapter()
				{
					public void componentTerminated()
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
				
				return null;
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
						agent.scheduleStep(new IComponentStep()
						{
							@XMLClassname("kill") 
							public Object execute(IInternalAccess ia)
							{
								ia.killComponent();
								return null;
							}
						});
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
						if (e.getActionCommand().equals(CONNECT_ON_ICON_PATH))
							disconnect();
					}
				});
				
				cleanUp();
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
		showConnectDialog();
	}
	
	private void showConnectDialog()
	{
		//mainFrame.setEnabled(false);
		
		final LoginPanel loginpanel = new LoginPanel(agent);
		final JPanel centerpanel = CenteringLayout.createCenteringPanel(loginpanel);
		loginpanel.setPreferredSize(new Dimension(500, 273));
		mainFrame.getContentPane().remove(mainSplitPane);
		GridBagConstraints g = new GridBagConstraints();
		g.weightx = 1.0;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.BOTH;
		g.anchor = GridBagConstraints.CENTER;
		
		loginpanel.setLoginAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (loginpanel.isConnect())
				{
					mainFrame.getContentPane().remove(centerpanel);
					GridBagConstraints g = new GridBagConstraints();
					g.weightx = 1.0;
					g.weighty = 1.0;
					g.fill = GridBagConstraints.BOTH;
					g.anchor = GridBagConstraints.CENTER;
					mainFrame.add(mainSplitPane, g);
					mainFrame.repaint();
					try
					{
						connect(loginpanel.getWfms(), loginpanel.getUserName(), loginpanel.getPassword());
						connected = true;
						statusBar.replaceIcon(CONNECT_ICON_NAME, CONNECT_ON_ICON_PATH);
						statusBar.setText("Connected.");
					}
					catch (GoalFailureException e1)
					{
						e1.printStackTrace();
						cleanUp();
					}
				}
			}
		});
		
		mainFrame.getContentPane().add(centerpanel, g);
	}
	
	private void connect(final IExternalWfmsService wfms, final String userName, final Object authToken)
	{
		agent.scheduleStep(new DispatchGoalStep("clientcap.connect", new HashMap() {{
			   put("wfms", wfms);
			   put("user_name", userName);
			   put("auth_token", authToken);
			}})).addResultListener(new SwingDefaultResultListener()
			{
				
				public void customResultAvailable(Object result)
				{
					Map parameters = (Map) result;
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
		agent.scheduleStep(new DispatchGoalStep("clientcap.disconnect")).addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				statusBar.replaceIcon(CONNECT_ICON_NAME, CONNECT_OFF_ICON_PATH);
				connected = false;
				cleanUp();
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
				
				agent.scheduleStep(new DispatchGoalStep("clientcap.finish_activity", "activity", activity)).addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object result)
					{
					}
					
					public void customExceptionOccurred(Exception exception)
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
		agent.scheduleStep(new DispatchGoalStep("clientcap.cancel_activity", "activity", activity)).addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
			}
			
			public void customExceptionOccurred(Exception exception)
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
					agent.scheduleStep(new DispatchGoalStep("clientcap.begin_activity", "workitem", wi)).addResultListener(new SwingDefaultResultListener()
					{
						public void customResultAvailable(Object result)
						{
						}
						
						public void customExceptionOccurred(Exception exception)
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
		
		agent.scheduleStep(new SetBeliefStep("clientcap.add_workitem_controller", wiAdded));
		
		Action wiRemoved = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				IWorkitem wi = (IWorkitem) e.getSource();
				wlComponent.removeWorkitem(wi);
			}
		};
		agent.scheduleStep(new SetBeliefStep("clientcap.remove_workitem_controller", wiRemoved));
		
		agent.scheduleStep(new DispatchGoalStep("clientcap.start_workitem_subscription"));
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
		agent.scheduleStep(new SetBeliefStep("clientcap.add_process_model_controller", pmAdded));
		
		Action pmRemoved = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				String modelName = (String) e.getSource();
				pmComponent.removeProcessModelName(modelName);
			}
		};
		agent.scheduleStep(new SetBeliefStep("clientcap.remove_process_model_controller", pmRemoved));
		
		agent.scheduleStep(new DispatchGoalStep("clientcap.start_model_repository_subscription"));
		
		pmComponent.setStartAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				final String processName = pmComponent.getSelectedModelName();
				if (processName != null)
				{
					agent.scheduleStep(new DispatchGoalStep("clientcap.start_process", "process_name", processName)).addResultListener(new SwingDefaultResultListener()
					{
						public void customResultAvailable(Object result)
						{
						}
						
						public void customExceptionOccurred(Exception exception)
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
					
					agent.scheduleStep(new DispatchGoalStep("clientcap.add_model_resource", "resource_path", path));
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
					agent.scheduleStep(new DispatchGoalStep("clientcap.remove_process", "process_name", name)).addResultListener(new SwingDefaultResultListener()
					{
						public void customResultAvailable(Object result)
						{
						}
						
						public void customExceptionOccurred(Exception exception)
						{
							JOptionPane.showMessageDialog(mainFrame, "Removing process failed.");
						}
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
					agent.scheduleStep(new DispatchGoalStep("clientcap.terminate_activity", "activity", activity)).addResultListener(new SwingDefaultResultListener()
					{
						public void customResultAvailable(Object result)
						{							
						}
						
						public void customExceptionOccurred(Exception exception)
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
		agent.scheduleStep(new SetBeliefStep("clientcap.add_user_activity_controller", uacAdded));
		
		Action uacRemoved = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				aaComponent.removeUserActivity(e.getActionCommand(), (IClientActivity) e.getSource());
			}
		};
		agent.scheduleStep(new SetBeliefStep("clientcap.remove_user_activity_controller", uacRemoved));
		
		agent.scheduleStep(new DispatchGoalStep("clientcap.start_user_activities_subscription"));
		
		setupMonitoringComponent();
	}
	
	protected void setupMonitoringComponent()
	{
		Action lEvent = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Client-side input: " + e.getSource());
			}
		};
		agent.scheduleStep(new SetBeliefStep("clientcap.log_controller", lEvent));
		
		agent.scheduleStep(new DispatchGoalStep("clientcap.start_log_event_subscription"));
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
		agent.scheduleStep(new SetBeliefStep("clientcap.add_activity_controller", acAdded));
		
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
		agent.scheduleStep(new SetBeliefStep("clientcap.remove_activity_controller", acRemoved));
		
		//TODO: put somewhere else
		
		
		Action lcAction = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				connected = false;
				statusBar.replaceIcon(CONNECT_ICON_NAME, CONNECT_OFF_ICON_PATH);
				cleanUp();
			}
		};
		agent.scheduleStep(new SetBeliefStep("clientcap.lost_connection_controller", lcAction));
		
		agent.scheduleStep(new DispatchGoalStep("clientcap.start_activity_subscription"));
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
		
		agent.scheduleStep(new SetBeliefStep(new HashMap()
		{{
			put("clientcap.add_workitem_controller", null);
			put("clientcap.remove_workitem_controller", null);
			put("clientcap.add_user_activity_controller", null);
			put("clientcap.remove_user_activity_controller", null);
			put("clientcap.add_process_model_controller", null);
			put("clientcap.remove_process_model_controller", null);
			put("clientcap.log_controller", null);
		}}));
		
		/*agent.getBeliefbase().setBeliefFact("clientcap.add_workitem_controller", null);
		agent.getBeliefbase().setBeliefFact("clientcap.remove_workitem_controller", null);
		agent.getBeliefbase().setBeliefFact("clientcap.add_user_activity_controller", null);
		agent.getBeliefbase().setBeliefFact("clientcap.remove_user_activity_controller", null);
		agent.getBeliefbase().setBeliefFact("clientcap.add_process_model_controller", null);
		agent.getBeliefbase().setBeliefFact("clientcap.remove_process_model_controller", null);*/
	}
}

