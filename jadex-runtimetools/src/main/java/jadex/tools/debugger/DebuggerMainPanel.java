package jadex.tools.debugger;

import jadex.base.SComponentFactory;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.ICMSComponentListener;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.SwingDefaultResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.tools.debugger.common.ObjectInspectorDebuggerPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 *  Show details of a debugged agent.
 */
public class DebuggerMainPanel extends JSplitPane
{
	//-------- constants --------
	
	/** The factory properties key for debugger panels
	 * (value is a comma separated list of fully
	 * qualified class names implementing IDebuggerPanel). */
	public static String	KEY_DEBUGGER_PANELS	= "debugger.panels";

	/** The model properties key for breakpoints (should contain a java.util.Collection object). */
	public static String	KEY_DEBUGGER_BREAKPOINTS	= "debugger.breakpoints";

	//-------- attributes --------
	
	/** The control center. */
	protected IControlCenter	jcc;
	
	/** The component description. */
	protected IComponentDescription	desc;
	
	/** The step button. */
	protected JButton	step;

	/** The step button. */
	protected JButton	run;

	/** The stepmode checkbox. */
	protected JCheckBox	stepmode;
	
	//-------- constructors --------
	
	/**
	 *  Create a new debugger panel.
	 *  @param container	The service container.
	 *  @param comp	The identifier of the component to be debugged.
	 */
	public DebuggerMainPanel(final IControlCenter jcc, final IComponentDescription desc)
	{
		super(JSplitPane.HORIZONTAL_SPLIT, new JPanel(), new JPanel());
		this.jcc	= jcc;
		this.desc	= desc;
		this.setOneTouchExpandable(true);
		setDividerLocation(150);
		
		SServiceProvider.getServiceUpwards(jcc.getExternalAccess().getServiceProvider(), IComponentManagementService.class).addResultListener(new SwingDefaultResultListener(DebuggerMainPanel.this)
		{
			public void customResultAvailable(Object result)
			{
				IComponentManagementService	ces	= (IComponentManagementService)result;
				// The right panel (step & custom tabs)
				JPanel	rightpanel	= new JPanel();
				setRightComponent(rightpanel);
				rightpanel.setLayout(new GridBagLayout());
				
				final JTabbedPane	tabs	= new JTabbedPane();		
				IFuture ret = ces.getExternalAccess(desc.getName());
				ret.addResultListener(new IResultListener()
				{			
					public void resultAvailable(final Object result)
					{
						// The left panel (breakpoints)
						final IExternalAccess exta = (IExternalAccess)result;
						final BreakpointPanel[] leftpanel = new BreakpointPanel[1];
						final Map props = ((IExternalAccess)result).getModel().getProperties();
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								if(props!=null && props.containsKey(KEY_DEBUGGER_BREAKPOINTS))
								{
									Collection	breakpoints	= (Collection)props.get(KEY_DEBUGGER_BREAKPOINTS);
									leftpanel[0] = new BreakpointPanel(breakpoints, desc, jcc.getExternalAccess().getServiceProvider());
									DebuggerMainPanel.this.setLeftComponent(leftpanel[0]);
									DebuggerMainPanel.this.setDividerLocation(150);	// Hack???
								}
								else
								{
									JPanel nobreakpoints = new JPanel();
									nobreakpoints.add(new JLabel("no breakpoints"));
									DebuggerMainPanel.this.setLeftComponent(nobreakpoints);
									DebuggerMainPanel.this.setDividerLocation(0);
								}
								
								// Sub panels of right panel.
								SComponentFactory.getProperties(DebuggerMainPanel.this.jcc.getExternalAccess().getServiceProvider(), DebuggerMainPanel.this.desc.getType())
									.addResultListener(new SwingDefaultResultListener(DebuggerMainPanel.this)
								{
									public void customResultAvailable(Object result)
									{
										final Map props2 = (Map)result;
										if(props2!=null && props2.containsKey(KEY_DEBUGGER_PANELS))
										{
											AbstractJCCPlugin.getClassLoader(desc.getName(), jcc).addResultListener(new SwingDefaultResultListener(DebuggerMainPanel.this)
											{
												public void customResultAvailable(Object result)
												{
													final ClassLoader	cl	= (ClassLoader)result;
													String	panels	= (String)props2.get(KEY_DEBUGGER_PANELS);
													StringTokenizer	stok	= new StringTokenizer(panels, ", \t\n\r\f");
													while(stok.hasMoreTokens())
													{
														String classname	= stok.nextToken();
														try
														{
															Class clazz	= SReflect.classForName(classname, cl);
															IDebuggerPanel	panel	= (IDebuggerPanel)clazz.newInstance();
															panel.init(DebuggerMainPanel.this.jcc, leftpanel[0], DebuggerMainPanel.this.desc.getName(), exta);
															tabs.addTab(panel.getTitle(), panel.getIcon(), panel.getComponent(), panel.getTooltipText());
														}
														catch(Exception e)
														{
															e.printStackTrace();
															DebuggerMainPanel.this.jcc.displayError("Error initializing debugger panel.", "Debugger panel class: "+classname, e);
														}
													}
												}
											});
										}
										else
										{
											ObjectInspectorDebuggerPanel panel = new ObjectInspectorDebuggerPanel();
											panel.init(DebuggerMainPanel.this.jcc, leftpanel[0], DebuggerMainPanel.this.desc.getName(), exta);
											tabs.addTab(panel.getTitle(), panel.getIcon(), panel.getComponent(), panel.getTooltipText());
										}
									}
								});
							}
						});
					}
					public void exceptionOccurred(Exception exception)
					{
						DebuggerMainPanel.this.jcc.displayError("Error initializing debugger panels.", null, exception);
					}
				});
				
				
				step = new JButton("Step");
				step.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						step.setEnabled(false);
						run.setEnabled(false);
						SServiceProvider.getServiceUpwards(DebuggerMainPanel.this.jcc.getExternalAccess().getServiceProvider(), IComponentManagementService.class)
							.addResultListener(new SwingDefaultResultListener(DebuggerMainPanel.this)
						{
							public void customResultAvailable(Object result)
							{
								IComponentManagementService	ces	= (IComponentManagementService)result;
								IFuture ret = ces.stepComponent(DebuggerMainPanel.this.desc.getName());
								ret.addResultListener(new IResultListener()
								{
									public void resultAvailable(Object result)
									{
										updatePanel((IComponentDescription)result);
									}
									
									public void exceptionOccurred(Exception exception)
									{
										// Hack!!! keep tool reactive in case of error!?
										step.setEnabled(true);
										run.setEnabled(true);
									}
								});
							}
						});
					}
				});
				
				run = new JButton("Run");
				run.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						step.setEnabled(false);
						run.setEnabled(false);
						stepmode.setSelected(false);
						SServiceProvider.getServiceUpwards(DebuggerMainPanel.this.jcc.getExternalAccess().getServiceProvider(), IComponentManagementService.class)
							.addResultListener(new SwingDefaultResultListener(DebuggerMainPanel.this)
						{
							public void customResultAvailable(Object result)
							{
								final IComponentManagementService	ces	= (IComponentManagementService)result;
								IFuture ret = ces.stepComponent(DebuggerMainPanel.this.desc.getName());
								ret.addResultListener(new IResultListener()
								{
									public void resultAvailable(Object result)
									{
										IFuture ret = ces.resumeComponent(DebuggerMainPanel.this.desc.getName()); 
										ret.addResultListener(new IResultListener()
										{
											public void resultAvailable(Object result)
											{
												updatePanel((IComponentDescription)result);
											}
											
											public void exceptionOccurred(Exception exception)
											{
												step.setEnabled(true);
												run.setEnabled(true);
												stepmode.setSelected(true);
											}
										});
									}
									
									public void exceptionOccurred(Exception exception)
									{
										step.setEnabled(true);
										run.setEnabled(true);
										stepmode.setSelected(true);
									}
								});
							}
						});
					}
				});
				
				stepmode = new JCheckBox("Step Mode");
				stepmode.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						SServiceProvider.getServiceUpwards(DebuggerMainPanel.this.jcc.getExternalAccess().getServiceProvider(), IComponentManagementService.class)
							.addResultListener(new SwingDefaultResultListener(DebuggerMainPanel.this)
						{
							public void customResultAvailable(Object result)
							{
								IComponentManagementService	ces	= (IComponentManagementService)result;
								if(stepmode.isSelected())
								{
									ces.suspendComponent(DebuggerMainPanel.this.desc.getName());
								}
								else
								{
									ces.resumeComponent(DebuggerMainPanel.this.desc.getName());
								}
							}
						});
					}
				});
						
				int row	= 0;
				int	col	= 0;
				rightpanel.add(tabs, new GridBagConstraints(col++, row, GridBagConstraints.REMAINDER, 1,
						1,1, GridBagConstraints.LINE_END, GridBagConstraints.BOTH, new Insets(1,1,1,1), 0,0));
				row++;
				col	= 0;
				rightpanel.add(stepmode, new GridBagConstraints(col++, row, 1, 1,
					1,0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));
				rightpanel.add(step, new GridBagConstraints(col++, row, 1, 1,
						0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));
				rightpanel.add(run, new GridBagConstraints(col, row++, GridBagConstraints.REMAINDER, 1,
						0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));

				
				updatePanel((IComponentDescription)desc);

				ces.addComponentListener(desc.getName(), new ICMSComponentListener()
				{			
					public IFuture componentChanged(IComponentDescription desc)
					{
						updatePanel(desc);
						return new Future(null);
					}
					public IFuture componentRemoved(IComponentDescription desc, Map results)
					{
						return new Future(null);
					}			
					public IFuture componentAdded(IComponentDescription desc)
					{
						return new Future(null);
					}
				});		
			}
		});
	}

	/**
	 *  Dispose the panel, i.e. remove any associated resources (listeners etc.).
	 */
	public void dispose()
	{
	}

	protected void updatePanel(final IComponentDescription desc)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				stepmode.setSelected(IComponentDescription.STATE_SUSPENDED.equals(desc.getState()));
				step.setEnabled(IComponentDescription.STATE_SUSPENDED.equals(desc.getState()));
//					&& IComponentDescription.PROCESSINGSTATE_READY.equals(desc.getProcessingState()));
				run.setEnabled(IComponentDescription.STATE_SUSPENDED.equals(desc.getState()));
			}
		});
	}
}
