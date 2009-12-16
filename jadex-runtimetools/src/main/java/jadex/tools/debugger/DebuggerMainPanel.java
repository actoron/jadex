package jadex.tools.debugger;

import jadex.adapter.base.SComponentFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentListener;
import jadex.bridge.IExternalAccess;
import jadex.commons.SReflect;
import jadex.commons.concurrent.IResultListener;
import jadex.service.library.ILibraryService;
import jadex.tools.common.plugin.IControlCenter;

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
	
	/** The stepmode checkbox. */
	protected JCheckBox	stepmode;
	
	//-------- constructors --------
	
	/**
	 *  Create a new debugger panel.
	 *  @param container	The service container.
	 *  @param comp	The identifier of the component to be debugged.
	 */
	public DebuggerMainPanel(IControlCenter jcc, IComponentDescription desc)
	{
		this.jcc	= jcc;
		this.desc	= desc;
		this.setOneTouchExpandable(true);
		
		IComponentExecutionService	ces	= ((IComponentExecutionService)
			jcc.getServiceContainer().getService(IComponentExecutionService.class));
		
		// The right panel (step & custom tabs)
		JPanel	rightpanel	= new JPanel();
		this.setRightComponent(rightpanel);
		rightpanel.setLayout(new GridBagLayout());
		
		final JTabbedPane	tabs	= new JTabbedPane();		
		ces.getExternalAccess(desc.getName(), new IResultListener()
		{			
			public void resultAvailable(Object source, final Object result)
			{
				// The left panel (breakpoints)
				SwingUtilities.invokeLater(new Runnable()
				{
					
					public void run()
					{
						Map	props	= ((IExternalAccess)result).getModel().getProperties();
						if(props!=null && props.containsKey(KEY_DEBUGGER_BREAKPOINTS))
						{
							Collection	breakpoints	= (Collection)props.get(KEY_DEBUGGER_BREAKPOINTS);
							BreakpointPanel	leftpanel	= new BreakpointPanel(breakpoints);
							DebuggerMainPanel.this.setLeftComponent(leftpanel);
							DebuggerMainPanel.this.setDividerLocation(150);	// Hack???
						}
					}
				});
				
				// Sub panels of right panel.
				final Map	props	= SComponentFactory.getProperties(DebuggerMainPanel.this.jcc.getServiceContainer(), DebuggerMainPanel.this.desc.getType());
				if(props!=null && props.containsKey(KEY_DEBUGGER_PANELS))
				{
					final ILibraryService	libservice	= (ILibraryService)DebuggerMainPanel.this.jcc.getServiceContainer().getService(ILibraryService.class);
					String	panels	= (String)props.get(KEY_DEBUGGER_PANELS);
					StringTokenizer	stok	= new StringTokenizer(panels, ", \t\n\r\f");
					while(stok.hasMoreTokens())
					{
						final String classname	= stok.nextToken();
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								try
								{
									Class	clazz	= SReflect.classForName(classname, libservice.getClassLoader());
									IDebuggerPanel	panel	= (IDebuggerPanel)clazz.newInstance();
									panel.init(DebuggerMainPanel.this.jcc, DebuggerMainPanel.this.desc.getName(), result);
									tabs.addTab(panel.getTitle(), panel.getIcon(), panel.getComponent(), panel.getTooltipText());
								}
								catch(Exception e)
								{
									DebuggerMainPanel.this.jcc.displayError("Error initializing debugger panel.", "Debugger panel class: "+classname, e);
								}
							}							
						});
					}
				}
			}
			public void exceptionOccurred(Object source, Exception exception)
			{
				DebuggerMainPanel.this.jcc.displayError("Error initializing debugger panels.", null, exception);
			}
		});
		
		this.step = new JButton("Step");
		step.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				step.setEnabled(false);
				IComponentExecutionService	ces	= (IComponentExecutionService)
					DebuggerMainPanel.this.jcc.getServiceContainer().getService(IComponentExecutionService.class);
				ces.stepComponent(DebuggerMainPanel.this.desc.getName(), new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						updatePanel((IComponentDescription)result);
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						// Hack!!! keep tool reactive in case of error!?
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								step.setEnabled(true);
							}
						});
					}
				});
			}
		});
		
		this.stepmode = new JCheckBox("Step Mode");
		stepmode.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				IComponentExecutionService	ces	= (IComponentExecutionService)
					DebuggerMainPanel.this.jcc.getServiceContainer().getService(IComponentExecutionService.class);
				if(stepmode.isSelected())
				{
					ces.suspendComponent(DebuggerMainPanel.this.desc.getName(), null);
				}
				else
				{
					ces.resumeComponent(DebuggerMainPanel.this.desc.getName(), null);
				}
				step.setEnabled(stepmode.isSelected());		
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
		rightpanel.add(step, new GridBagConstraints(col, row++, GridBagConstraints.REMAINDER, 1,
			0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));

		
		updatePanel((IComponentDescription)desc);

		ces.addComponentListener(desc.getName(), new IComponentListener()
		{			
			public void componentChanged(IComponentDescription desc)
			{
				updatePanel(desc);
			}
			public void componentRemoved(IComponentDescription desc, Map results)
			{
			}			
			public void componentAdded(IComponentDescription desc)
			{
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
				stepmode.setSelected(IComponentDescription.STATE_SUSPENDED.equals((desc).getState())
					|| IComponentDescription.STATE_WAITING.equals((desc).getState()));
				step.setEnabled(IComponentDescription.STATE_SUSPENDED.equals((desc).getState()));		
			}
		});
	}
}
