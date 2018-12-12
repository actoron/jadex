package jadex.tools.debugger;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.commons.SReflect;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.tools.debugger.common.ObjectInspectorDebuggerPanel;

/**
 *  Show details of a debugged agent.
 */
public class DebuggerMainPanel extends JSplitPane
{
	//-------- constants --------
	
	/** The factory properties key for debugger panels
	 * (value is a comma separated list of fully
	 * qualified class names implementing IDebuggerPanel). */
	public static final String	KEY_DEBUGGER_PANELS	= "debugger.panels";

	/** The model properties key for breakpoints (should contain a java.util.Collection object). */
//	public static final String	KEY_DEBUGGER_BREAKPOINTS	= "debugger.breakpoints";

	//-------- attributes --------
	
	/** The control center. */
	protected IControlCenter	jcc;
	
	/** The component description. */
	protected IComponentDescription	desc;
	
	/** The step button. */
	protected JButton	step;

	/** The step button. */
	protected JButton	run;

	/** The pause buuton. */
	protected JButton	pause;
	
	/** The tabs. */
	protected List<IDebuggerPanel> debuggerpanels;
	
	/** The cms listener. */
	protected ISubscriptionIntermediateFuture<CMSStatusEvent> listener;
	
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
		this.debuggerpanels = new ArrayList<IDebuggerPanel>();
		this.setOneTouchExpandable(true);
		setDividerLocation(0.3);
				
		// The right panel (step & custom tabs)
		JPanel	rightpanel	= new JPanel();
		setRightComponent(rightpanel);
		rightpanel.setLayout(new GridBagLayout());
		
		final JTabbedPane tabs = new JTabbedPane();	
		
		jcc.getPlatformAccess().getExternalAccessAsync(desc.getName())
			.addResultListener(new SwingResultListener<IExternalAccess>(new IResultListener<IExternalAccess>()
		{			
			public void resultAvailable(final IExternalAccess exta)
			{
				// The left panel (breakpoints)
				final BreakpointPanel[] leftpanel = new BreakpointPanel[1];
				exta.getModelAsync().addResultListener(new SwingResultListener<IModelInfo>(new IResultListener<IModelInfo>()
				{			
					public void resultAvailable(final IModelInfo model)
					{
						String[] bps = model.getBreakpoints();
						if(bps!=null && bps.length>0)
						{
							leftpanel[0] = new BreakpointPanel(bps, desc, jcc.getPlatformAccess(), jcc.getCMSHandler());
							DebuggerMainPanel.this.setLeftComponent(leftpanel[0]);
							DebuggerMainPanel.this.setDividerLocation(0);	// 150? Hack???
						}
						else
						{
							JPanel nobreakpoints = new JPanel();
							nobreakpoints.add(new JLabel("no breakpoints"));
							DebuggerMainPanel.this.setLeftComponent(nobreakpoints);
							DebuggerMainPanel.this.setDividerLocation(0);
						}
						
						// Sub panels of right panel.
						SComponentFactory.getProperty(exta, DebuggerMainPanel.this.desc.getType(), KEY_DEBUGGER_PANELS)
							.addResultListener(new SwingDefaultResultListener<Object>(DebuggerMainPanel.this)
						{
							public void customResultAvailable(Object result)
							{
								final String	panels	= (String)result;
								if(panels!=null)
								{
									AbstractJCCPlugin.getClassLoader(desc.getName(), jcc).addResultListener(new SwingDefaultResultListener<ClassLoader>(DebuggerMainPanel.this)
									{
										public void customResultAvailable(ClassLoader cl)
										{
		//											final ClassLoader	cl	= (ClassLoader)result;
											StringTokenizer	stok	= new StringTokenizer(panels, ", \t\n\r\f");
											while(stok.hasMoreTokens())
											{
												String classname	= stok.nextToken();
												try
												{
		//													System.out.println("loading panel: "+classname+" "+cl);
													Class<?> clazz	= SReflect.classForName(classname, cl);
													IDebuggerPanel	panel	= (IDebuggerPanel)clazz.newInstance();
													panel.init(DebuggerMainPanel.this.jcc, leftpanel[0], DebuggerMainPanel.this.desc.getName(), exta);
													debuggerpanels.add(panel);
													tabs.addTab(panel.getTitle(), panel.getIcon(), panel.getComponent(), panel.getTooltipText());
												}
												catch(Exception e)
												{
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
									debuggerpanels.add(panel);
									tabs.addTab(panel.getTitle(), panel.getIcon(), panel.getComponent(), panel.getTooltipText());
								}
							}
						});
					}
				
					public void exceptionOccurred(Exception exception)
					{
						DebuggerMainPanel.this.jcc.displayError("Error initializing debugger panels.", null, exception);
					}
				}));
			}
			
			public void exceptionOccurred(Exception exception)
			{
					DebuggerMainPanel.this.jcc.displayError("Error initializing debugger panels.", null, exception);
			}
		}));
		
		pause = new JButton("Pause");
		pause.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				pause.setEnabled(false);
				DebuggerMainPanel.this.jcc.getPlatformAccess().getExternalAccess(DebuggerMainPanel.this.desc.getName()).suspendComponent();
			}
		});
		
		step = new JButton("Step");
		step.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				step.setEnabled(false);
				run.setEnabled(false);
				DebuggerMainPanel.this.jcc.getPlatformAccess().getDescription(DebuggerMainPanel.this.desc.getName())
					.addResultListener(new IResultListener<IComponentDescription>()
				{
					public void resultAvailable(IComponentDescription result)
					{
						updatePanel(result);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						error();
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
				pause.setEnabled(true);
				IFuture<Void> ret = DebuggerMainPanel.this.jcc.getPlatformAccess().getExternalAccess(DebuggerMainPanel.this.desc.getName()).resumeComponent(); 
				ret.addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						DebuggerMainPanel.this.jcc.getPlatformAccess().getDescription(DebuggerMainPanel.this.desc.getName())
							.addResultListener(new IResultListener<IComponentDescription>()
						{
							public void resultAvailable(IComponentDescription result)
							{
								updatePanel(result);
							}
							
							public void exceptionOccurred(Exception exception)
							{
								error();
							}
						});
					}
					
					public void exceptionOccurred(Exception exception)
					{
						error();
					}
				});
			}
		});
		
		Dimension msize = pause.getMinimumSize();
		Dimension psize = pause.getPreferredSize();
		run.setMinimumSize(msize);
		run.setPreferredSize(psize);
		step.setMinimumSize(msize);
		step.setPreferredSize(psize);
		
//				stepmode = new JCheckBox("Step Mode");
//				stepmode.addActionListener(new ActionListener()
//				{
//					public void actionPerformed(ActionEvent e)
//					{
//						SServiceProvider.getServiceUpwards(DebuggerMainPanel.this.jcc.getPlatformAccess().getServiceProvider(), IComponentManagementService.class)
//							.addResultListener(new SwingDefaultResultListener(DebuggerMainPanel.this)
//						{
//							public void customResultAvailable(Object result)
//							{
//								IComponentManagementService	ces	= (IComponentManagementService)result;
//								if(stepmode.isSelected())
//								{
//									ces.suspendComponent(DebuggerMainPanel.this.desc.getName());
//								}
//								else
//								{
//									ces.resumeComponent(DebuggerMainPanel.this.desc.getName());
//								}
//							}
//						});
//					}
//				});
				
		int row	= 0;
		int	col	= 0;
		rightpanel.add(tabs, new GridBagConstraints(col++, row, GridBagConstraints.REMAINDER, 1,
			1,1, GridBagConstraints.LINE_END, GridBagConstraints.BOTH, new Insets(1,1,1,1), 0,0));
		row++;
		col	= 0;
		rightpanel.add(pause, new GridBagConstraints(col++, row, 1, 1,
			1,0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));
		rightpanel.add(step, new GridBagConstraints(col++, row, 1, 1,
			0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));
		rightpanel.add(run, new GridBagConstraints(col, row++, GridBagConstraints.REMAINDER, 1,
			0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));

		
		updatePanel((IComponentDescription)desc);

		listener	= jcc.getCMSHandler().addCMSListener(desc.getName().getRoot());
		listener.addResultListener(new IIntermediateResultListener<CMSStatusEvent>()
		{
			@Override
			public void exceptionOccurred(Exception exception)
			{
			}

			@Override
			public void resultAvailable(Collection<CMSStatusEvent> result)
			{
			}

			@Override
			public void intermediateResultAvailable(CMSStatusEvent event)
			{
				if(event.getComponentDescription().getName().equals(DebuggerMainPanel.this.desc.getName()))
					updatePanel(event.getComponentDescription());
			}

			@Override
			public void finished()
			{
			}
		});
	}

	/**
	 *  Dispose the panel, i.e. remove any associated resources (listeners etc.).
	 */
	public void dispose()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				for(int i=0; i<debuggerpanels.size(); i++)
				{
					IDebuggerPanel panel = (IDebuggerPanel)debuggerpanels.get(i);
					panel.dispose();
				}
				if(listener!=null)
				{
					listener.terminate();
				}
			}
		});
	}

	protected void updatePanel(final IComponentDescription desc)
	{
		if(desc==null)
			System.out.println("asfhsfhakfhk");
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
//				System.out.println("update: "+desc);
				pause.setEnabled(!IComponentDescription.STATE_SUSPENDED.equals(desc.getState()));
				step.setEnabled(IComponentDescription.STATE_SUSPENDED.equals(desc.getState()));
//					&& IComponentDescription.PROCESSINGSTATE_READY.equals(desc.getProcessingState()));
				run.setEnabled(IComponentDescription.STATE_SUSPENDED.equals(desc.getState()));
			}
		});
	}
	
	/**
	 * 
	 */
	protected void error()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				step.setEnabled(true);
				run.setEnabled(true);
				pause.setEnabled(false);
			}
		});
	}
	
	/**
	 *  Get the step info.
	 */
	public String getStepInfo()
	{
		String ret = null;
		if(debuggerpanels!=null)
		{
			for(IDebuggerPanel pan: debuggerpanels)
			{
				ret = pan.getStepInfo();
				if(ret!=null)
				{
					break;
				}
			}
		}
		return ret;
	}
}
