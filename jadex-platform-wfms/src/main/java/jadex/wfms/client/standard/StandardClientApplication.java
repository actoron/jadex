package jadex.wfms.client.standard;

import jadex.base.gui.SwingDefaultResultListener;
import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;
import jadex.wfms.client.ClientInfo;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.ProcessResource;
import jadex.wfms.gui.images.SImage;
import jadex.wfms.guicomponents.CenteringLayout;
import jadex.wfms.guicomponents.ComponentLoginPanel;
import jadex.wfms.service.IExternalWfmsService;
import jadex.wfms.service.listeners.ActivityEvent;
import jadex.wfms.service.listeners.IActivityListener;
import jadex.wfms.service.listeners.ILogListener;
import jadex.wfms.service.listeners.IProcessRepositoryListener;
import jadex.wfms.service.listeners.IWorkitemListener;
import jadex.wfms.service.listeners.ProcessRepositoryEvent;
import jadex.wfms.service.listeners.WorkitemEvent;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

public class StandardClientApplication implements IWfmsClient
{
	protected static final String WORKITEM_LIST_TAB_NAME = "Workitem List";
	
	protected static final String PROCESS_MODEL_TAB_NAME = "Process Models";
	
	protected static final String ADMIN_ACTIVITIES_TAB_NAME = "Activities";
	
	protected static final String MONITORING_TAB_NAME = "Monitoring";
	
	protected static final String DISCONNECT_TOOLTIP = "Disconnect";
	
	protected static final JPanel EMPTY_PANEL = new JPanel();
	
	protected static final String CONNECT_ICON_NAME = "Connection";
	protected static final String CONNECT_OFF_ICON_PATH = SImage.IMAGE_PATH.concat("connection_off_small.png");
	protected static final String CONNECT_ON_ICON_PATH = SImage.IMAGE_PATH.concat("connection_on_small.png");
	
	protected IExternalAccess ea;
	
	protected JPanel mainPanel;
	
	protected StatusBar statusBar;
	
	//protected JSplitPane mainSplitPane;
	
	protected JTabbedPane toolPane;
	
	/** Component displaying the workitem list and work area. */
	protected WorkitemListComponent wlComponent;
	
	/** Component displaying the process models */
	protected ProcessModelTreeComponent pmComponent;
	
	/** Component displaying administrative tools */
	protected AdminActivitiesComponent aaComponent;
	
	/** The WfMS access. */
	protected IExternalWfmsService wfms;
	
	/** Capabilities of the user account. */
	protected Set capabilities;
	
	/** Component for displaying monitoring-related information. */
	protected MonitoringComponent mocomponent;
	
	public StandardClientApplication(IExternalAccess access)
	{
		this.ea = access;
		
		EventQueue.invokeLater(new Runnable()
		{
			
			public void run()
			{
				/*try
				{
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}*/
				if (mainPanel == null)
					mainPanel = new JPanel();
				/*mainSplitPane = new JSplitPane();
				mainSplitPane.setOneTouchExpandable(true);
				mainPanel.setLayout(new GridBagLayout());
				GridBagConstraints g = new GridBagConstraints();
				g.weightx = 1.0;
				g.weighty = 1.0;
				g.fill = GridBagConstraints.BOTH;
				g.anchor = GridBagConstraints.CENTER;
				mainPanel.add(mainSplitPane, g);*/
				
				mainPanel.setLayout(new GridBagLayout());
				
				toolPane = new JTabbedPane();
				GridBagConstraints g = new GridBagConstraints();
				g.gridy = 0;
				g.weightx = 1.0;
				g.weighty = 1.0;
				g.fill = GridBagConstraints.BOTH;
				g.anchor = GridBagConstraints.CENTER;
				mainPanel.add(toolPane, g);
				
				statusBar = new StatusBar();
				statusBar.addIcon(CONNECT_ICON_NAME, CONNECT_OFF_ICON_PATH, null);
				statusBar.setText("Ready.");
				statusBar.setPreferredSize(new Dimension(100, 24));
				
				g = new GridBagConstraints();
				g.gridy = 1;
				g.weightx = 1.0;
				g.fill = GridBagConstraints.HORIZONTAL;
				g.anchor = GridBagConstraints.PAGE_END;
				mainPanel.add(statusBar, g);
				
				wlComponent = new WorkitemListComponent(StandardClientApplication.this);
				pmComponent = new ProcessModelTreeComponent();
				aaComponent = new AdminActivitiesComponent();
				mocomponent = new MonitoringComponent();
				
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
	
	public JPanel getView()
	{
		if (mainPanel == null)
			mainPanel = new JPanel();
		return mainPanel;
	}
	
	public IComponentIdentifier getComponentIdentifier()
	{
		return ea.getComponentIdentifier();
	}
	
	public IExternalWfmsService getWfms()
	{
		return wfms;
	}
	
	private void cleanUp()
	{
		pmComponent.clear();
		wlComponent.clear();
		toolPane.removeAll();
		flushActions();
		
		//mainSplitPane.setLeftComponent(new JTabbedPane());
		
		/*if (mainSplitPane.getRightComponent() instanceof JTabbedPane)
			mainSplitPane.setRightComponent(new JTabbedPane());
		else
			mainSplitPane.setRightComponent(EMPTY_PANEL);
		
		mainSplitPane.setDividerLocation(0.45);*/
		showConnectDialog();
	}
	
	private void showConnectDialog()
	{
		//mainFrame.setEnabled(false);
		
		final ComponentLoginPanel loginpanel = new ComponentLoginPanel(ea.getServiceProvider());
		final JPanel centerpanel = CenteringLayout.createCenteringPanel(loginpanel);
		loginpanel.setPreferredSize(new Dimension(500, 273));
		mainPanel.remove(toolPane);
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
					mainPanel.remove(centerpanel);
					GridBagConstraints g = new GridBagConstraints();
					g.weightx = 1.0;
					g.weighty = 1.0;
					g.fill = GridBagConstraints.BOTH;
					g.anchor = GridBagConstraints.CENTER;
					mainPanel.add(toolPane, g);
					mainPanel.repaint();
					IExternalWfmsService selectedwfms = loginpanel.getWfms();
					connect(selectedwfms, loginpanel.getUserName(), loginpanel.getPassword());
				}
			}
		});
		
		mainPanel.add(centerpanel, g);
	}
	
	private void connect(final IExternalWfmsService wfms, final String username, final Object authToken)
	{
		ClientInfo info = new ClientInfo(username);
		wfms.authenticate(ea.getComponentIdentifier(), info).addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				if (Boolean.TRUE.equals(result))
				{
					wfms.getCapabilities(ea.getComponentIdentifier()).addResultListener(new SwingDefaultResultListener()
					{
						public void customResultAvailable(Object result)
						{
							StandardClientApplication.this.wfms = wfms;
							capabilities = (Set) result;
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
								toolPane.add(MONITORING_TAB_NAME, mocomponent);
								setupMonitoringComponent();
							}
							
							
							statusBar.replaceIcon(CONNECT_ICON_NAME, CONNECT_ON_ICON_PATH, DISCONNECT_TOOLTIP);
							statusBar.setText("Connected.");
						}
						
						public void customExceptionOccurred(Exception exception)
						{
							//TODO: Do something. Cleanup ok?
							cleanUp();
						}
					});
				}
			}
			
			public void customExceptionOccurred(Exception exception)
			{
				//TODO: Do something. Cleanup ok?
				cleanUp();
			}
		});
	}
	
	public void disconnect()
	{
		if (wfms != null)
		{
			wfms.deauthenticate(ea.getComponentIdentifier()).addResultListener(new SwingDefaultResultListener()
			{
				public void customResultAvailable(Object result)
				{
					statusBar.replaceIcon(CONNECT_ICON_NAME, CONNECT_OFF_ICON_PATH, null);
					StandardClientApplication.this.wfms = null;
					cleanUp();
				}
			});
		}
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
					wfms.beginActivity(ea.getComponentIdentifier(), wi).addResultListener(new SwingDefaultResultListener()
					{
						public void customResultAvailable(Object result)
						{
						}
						
						public void customExceptionOccurred(Exception exception)
						{
							JOptionPane.showMessageDialog(mainPanel, "Start of activity failed.");
						}
					});
				}
			}
		});
		
		wfms.addWorkitemListener(ea.getComponentIdentifier(), new IWorkitemListener()
		{
			public IFuture workitemRemoved(WorkitemEvent event)
			{
				wlComponent.removeWorkitem(event.getWorkitem());
				return IFuture.DONE;
			}
			
			public IFuture workitemAdded(WorkitemEvent event)
			{
				wlComponent.addWorkitem(event.getWorkitem());
				return IFuture.DONE;
			}
		});
	}
	
	private void setupProcessModelComponent()
	{
		wfms.addProcessRepositoryListener(ea.getComponentIdentifier(), new IProcessRepositoryListener()
		{
			public IFuture processModelRemoved(ProcessRepositoryEvent event)
			{
				pmComponent.removeProcessModelName(event.getModelName());
				return IFuture.DONE;
			}
			
			public IFuture processModelAdded(ProcessRepositoryEvent event)
			{
				pmComponent.addProcessModelName(event.getModelName());
				return IFuture.DONE;
			}
		});
		
		pmComponent.setStartAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				final String processname = pmComponent.getSelectedModelName();
				if (processname != null)
				{
					wfms.startProcess(ea.getComponentIdentifier(), processname).addResultListener(new SwingDefaultResultListener()
					{
						public void customResultAvailable(Object result)
						{
						}
						
						public void customExceptionOccurred(Exception exception)
						{
							JOptionPane.showMessageDialog(mainPanel, "Process Start failed.");
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
				int result = fileChooser.showOpenDialog(mainPanel);
				if (result == JFileChooser.APPROVE_OPTION)
				{
					//TODO: Move file operations?
					String path = fileChooser.getSelectedFile().getAbsolutePath();
					if (!path.toLowerCase().endsWith(".jar"))
						throw new RuntimeException("File not found: " + path);
					File resourceFile = new File(path);
					ProcessResource pr = null;
					try
					{
						MappedByteBuffer map = (new FileInputStream(resourceFile)).getChannel().map(FileChannel.MapMode.READ_ONLY, 0, resourceFile.length());
						byte[] resource = new byte[map.capacity()];
						map.get(resource, 0, map.capacity());
						pr = new ProcessResource(resourceFile.getName(), resource);
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
						throw new RuntimeException("Error sending file: " + path);
					}
					wfms.addProcessResource(ea.getComponentIdentifier(), pr);
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
					wfms.removeProcessResource(ea.getComponentIdentifier(), name).addResultListener(new SwingDefaultResultListener()
					{
						public void customResultAvailable(Object result)
						{
						}
						
						public void customExceptionOccurred(Exception exception)
						{
							JOptionPane.showMessageDialog(mainPanel, "Removing process resource failed.");
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
					wfms.terminateActivity(ea.getComponentIdentifier(), activity).addResultListener(new SwingDefaultResultListener()
					{
						public void customResultAvailable(Object result)
						{							
						}
						
						public void customExceptionOccurred(Exception exception)
						{
							JOptionPane.showMessageDialog(mainPanel, "Activity termination failed.");
						}
					});
				}
			}
		});
		
		wfms.addActivitiesListener(ea.getComponentIdentifier(), new IActivityListener()
		{
			public IFuture activityRemoved(ActivityEvent event)
			{
				aaComponent.removeUserActivity(event.getUserName(), event.getActivity());
				return IFuture.DONE;
			}
			
			public IFuture activityAdded(ActivityEvent event)
			{
				aaComponent.addUserActivity(event.getUserName(), event.getActivity());
				return IFuture.DONE;
			}
		});
	}
	
	protected void setupMonitoringComponent()
	{
		wfms.addLogListener(ea.getComponentIdentifier(), new ILogListener()
		{
			public IFuture logMessage(final IComponentChangeEvent event)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						mocomponent.addLogEvent(event);
					}
				});
				return IFuture.DONE;
			}
		}, true);
	}
	
	private void setupActivityHandling()
	{
		wfms.addActivityListener(ea.getComponentIdentifier(), new IActivityListener()
		{
			public IFuture activityRemoved(final ActivityEvent event)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						IClientActivity activity = event.getActivity();
						wlComponent.removeActivity(activity);
					}
				});
				
				return IFuture.DONE;
			}
			
			public IFuture activityAdded(final ActivityEvent event)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						wlComponent.addActivity(event.getActivity());
					}
				});
				return IFuture.DONE;
			}
		});
		
		
		//TODO: Move connection loss handling somewhere else
		/*Action lcAction = new AbstractAction()
		{
			public void actionPerformed(final ActionEvent e)
			{
				wfms = null;
				statusBar.replaceIcon(CONNECT_ICON_NAME, CONNECT_OFF_ICON_PATH);
				cleanUp();
			}
		};
		agent.scheduleStep(new SetBeliefStep("clientcap.lost_connection_controller", lcAction));*/
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
		
		/*agent.scheduleStep(new SetBeliefStep(new HashMap()
		{{
			put("clientcap.add_workitem_controller", null);
			put("clientcap.remove_workitem_controller", null);
			put("clientcap.add_user_activity_controller", null);
			put("clientcap.remove_user_activity_controller", null);
			put("clientcap.add_process_model_controller", null);
			put("clientcap.remove_process_model_controller", null);
			put("clientcap.log_controller", null);
		}}));*/
		
		/*agent.getBeliefbase().setBeliefFact("clientcap.add_workitem_controller", null);
		agent.getBeliefbase().setBeliefFact("clientcap.remove_workitem_controller", null);
		agent.getBeliefbase().setBeliefFact("clientcap.add_user_activity_controller", null);
		agent.getBeliefbase().setBeliefFact("clientcap.remove_user_activity_controller", null);
		agent.getBeliefbase().setBeliefFact("clientcap.add_process_model_controller", null);
		agent.getBeliefbase().setBeliefFact("clientcap.remove_process_model_controller", null);*/
	}
}

