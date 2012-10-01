package jadex.wfms.client.standard;

import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ExceptionResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.wfms.client.ClientInfo;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.ProcessResource;
import jadex.wfms.gui.images.SImage;
import jadex.wfms.guicomponents.CenteringLayout;
import jadex.wfms.guicomponents.ComponentLoginPanel;
import jadex.wfms.service.IExternalWfmsService;
import jadex.wfms.service.ProcessResourceInfo;
import jadex.wfms.service.listeners.ActivityEvent;
import jadex.wfms.service.listeners.IActivityListener;
import jadex.wfms.service.listeners.ILogListener;
import jadex.wfms.service.listeners.IProcessRepositoryListener;
import jadex.wfms.service.listeners.IWorkitemListener;
import jadex.wfms.service.listeners.ProcessRepositoryEvent;
import jadex.wfms.service.listeners.WorkitemEvent;

import java.awt.Dimension;
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
	protected ProcessModelComponent pmComponent;
	
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
		
		SwingUtilities.invokeLater(new Runnable()
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
				pmComponent = new ProcessModelComponent();
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
	
	/** Returns the component access. */
	public IExternalAccess getExternalAccess()
	{
		return ea;
	}
	
	public IExternalWfmsService getWfms()
	{
		return wfms;
	}
	
	public void setWfms(IExternalWfmsService wfms)
	{
		this.wfms = wfms;
	}
	
	private void cleanUp()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			
			public void run()
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
		});
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
		ea.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ClientInfo info = new ClientInfo(username);
				System.out.print("Authenticating " + info.getUserName() + "...");
				wfms.authenticate(info).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						System.out.println("done");
						System.out.println("Result: " + result);
						wfms.getCapabilities().addResultListener(new IResultListener()
						{
							public void resultAvailable(final Object result)
							{
								SwingUtilities.invokeLater(new Runnable()
								{
									public void run()
									{
										capabilities = (Set) result;
										setWfms(wfms);
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
								});
							}
							
							public void exceptionOccurred(Exception exception)
							{
								//TODO: Do something. Cleanup ok?
								cleanUp();
							}
						});
					}
					
					public void exceptionOccurred(Exception exception)
					{
						//TODO: Do something. Catch security exception here Cleanup okay?
						exception.printStackTrace();
						cleanUp();
					}
				});
				return IFuture.DONE;
			}
		});
		
	}
	
	public void disconnect()
	{
		if (getWfms() != null)
		{
			ea.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					getWfms().deauthenticate().addResultListener(new SwingDefaultResultListener()
					{
						public void customResultAvailable(Object result)
						{
							statusBar.replaceIcon(CONNECT_ICON_NAME, CONNECT_OFF_ICON_PATH, null);
							setWfms(null);
							cleanUp();
						}
					});
					return IFuture.DONE;
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
					ea.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							getWfms().beginActivity(wi).addResultListener(new SwingResultListener<Void>(new ExceptionResultListener<Void>()
							{
								public void exceptionOccurred(Exception exception)
								{
									JOptionPane.showMessageDialog(mainPanel, "Start of activity failed.");
								}
							}));
							return IFuture.DONE;
						}
					});
				}
			}
		});
		
		ea.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				getWfms().addWorkitemListener(new IWorkitemListener()
				{
					public IFuture workitemRemoved(final WorkitemEvent event)
					{
						final Future<Void> ret = new Future<Void>();
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								wlComponent.removeWorkitem(event.getWorkitem());
								ret.setResult(null);
							}
						});
						return ret;
					}
					
					public IFuture workitemAdded(final WorkitemEvent event)
					{
						final Future<Void> ret = new Future<Void>();
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								wlComponent.addWorkitem(event.getWorkitem());
								ret.setResult(null);
							}
						});
						return ret;
					}
				});
				
				return IFuture.DONE;
			}
		});
		
	}
	
	private void setupProcessModelComponent()
	{
		ea.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				getWfms().addProcessRepositoryListener(new IProcessRepositoryListener()
				{
					public IFuture processModelRemoved(final ProcessRepositoryEvent event)
					{
						final Future<Void> ret = new Future<Void>();
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								pmComponent.removeProcessModel(event.getProcessInformation());
								ret.setResult(null);
							}
						});
						return ret;
					}
					
					public IFuture processModelAdded(final ProcessRepositoryEvent event)
					{
						final Future<Void> ret = new Future<Void>();
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								pmComponent.addProcessModel(event.getProcessInformation());
								ret.setResult(null);
							}
						});
						return ret;
					}
				});
				return IFuture.DONE;
			}
		});
		
		
		pmComponent.setStartAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				final ProcessResourceInfo info = pmComponent.getSelectedModel();
				if (info != null)
				{
					ea.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							getWfms().startProcess(info).addResultListener(new SwingResultListener(new ExceptionResultListener()
							{
								public void exceptionOccurred(Exception exception)
								{
									JOptionPane.showMessageDialog(mainPanel, "Process Start failed.");
								}
							}));
							return IFuture.DONE;
						}
					});
				}
			}
		});
		
		pmComponent.setAddProcessAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
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
						JOptionPane.showMessageDialog(mainPanel, "Error reading file: " + path);
					}
					if (pr != null)
					{
						final ProcessResource fpr = pr;
						ea.scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								getWfms().addProcessResource(fpr);
								return IFuture.DONE;
							}
						});
					}
				}
			}
		});
		
		pmComponent.setRemoveProcessAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				final ProcessResourceInfo info = pmComponent.getSelectedModel();
				if (info != null)
				{
					ea.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							getWfms().removeProcessResource(info).addResultListener(new SwingResultListener<Void>(new ExceptionResultListener<Void>()
							{
								public void exceptionOccurred(Exception exception)
								{
									JOptionPane.showMessageDialog(mainPanel, "Removing process resource failed.");
								}
							}));
							return IFuture.DONE;
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
					ea.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							getWfms().terminateActivity(activity).addResultListener(new SwingResultListener(new ExceptionResultListener<Void>()
							{
								public void exceptionOccurred(Exception exception)
								{
									JOptionPane.showMessageDialog(mainPanel, "Activity termination failed.");
								}
							}));
							return IFuture.DONE;
						}
					});
				}
			}
		});
		
		ea.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				getWfms().addActivitiesListener(new IActivityListener()
				{
					public IFuture activityRemoved(final ActivityEvent event)
					{
						final Future<Void> ret = new Future<Void>();
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								aaComponent.removeUserActivity(event.getUserName(), event.getActivity());
								ret.setResult(null);
							}
						});
						return ret;
					}
					
					public IFuture activityAdded(final ActivityEvent event)
					{
						final Future<Void> ret = new Future<Void>();
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								aaComponent.addUserActivity(event.getUserName(), event.getActivity());
								ret.setResult(null);
							}
						});
						return ret;
					}
				});
				
				return Future.DONE;
			}
		});
		
	}
	
	protected void setupMonitoringComponent()
	{
		ea.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				getWfms().addLogListener(new ILogListener()
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
				
				return IFuture.DONE;
			}
		});
		
	}
	
	private void setupActivityHandling()
	{
		ea.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				getWfms().addActivityListener(new IActivityListener()
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
				return IFuture.DONE;
			}
		});
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
	}
}

