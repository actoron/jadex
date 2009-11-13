package jadex.tools.debugger;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.commons.SGUI;
import jadex.commons.concurrent.IResultListener;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.rules.tools.stateviewer.OAVPanel;
import jadex.tools.common.GuiProperties;
import jadex.tools.common.plugin.IControlCenter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;

/**
 *  Show details of a debugged agent.
 */
public class DebuggerPanel extends JSplitPane
{
	//-------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"contents", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/bug_small.png")
	});

	//-------- attributes --------
	
	/** The control center. */
	protected IControlCenter	jcc;
	
	/** The component identifier. */
	protected IComponentIdentifier	comp;
	
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
	public DebuggerPanel(IControlCenter jcc, IComponentIdentifier comp)
	{
		this.jcc	= jcc;
		this.comp	= comp;
		this.setOneTouchExpandable(true);
		
		IComponentExecutionService	ces	= ((IComponentExecutionService)
			jcc.getServiceContainer().getService(IComponentExecutionService.class));
		
		// The right panel (step & custom tabs)
		JPanel	rightpanel	= new JPanel();
		this.setRightComponent(rightpanel);
		rightpanel.setLayout(new GridBagLayout());
		
		final JTabbedPane	tabs	= new JTabbedPane();
		
		// Add OAV Viewer as default introspector (hack???).
		ces.getExternalAccess(comp, new IResultListener()
		{			
			public void resultAvailable(Object result)
			{
				IOAVState	dummystate	= OAVStateFactory.createOAVState(OAVJavaType.java_type_model);
				dummystate.addJavaRootObject(result);
				final OAVPanel	oavpanel	= new OAVPanel(dummystate);
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						tabs.addTab("Object", icons.getIcon("contents"), oavpanel, "Show the object contents");
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				DebuggerPanel.this.jcc.displayError("Error showing object contents", null, exception);
			}
		});
		
		this.step = new JButton("Step");
		step.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				IComponentExecutionService	ces	= (IComponentExecutionService)
					DebuggerPanel.this.jcc.getServiceContainer().getService(IComponentExecutionService.class);
				ces.stepComponent(DebuggerPanel.this.comp, new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								step.setEnabled(true);
							}
						});
					}
					
					public void exceptionOccurred(Exception exception)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								step.setEnabled(true);
							}
						});
					}
				});
				step.setEnabled(false);
			}
		});
		
		this.stepmode = new JCheckBox("Step Mode");
		stepmode.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				IComponentExecutionService	ces	= (IComponentExecutionService)
					DebuggerPanel.this.jcc.getServiceContainer().getService(IComponentExecutionService.class);
				if(stepmode.isSelected())
				{
					ces.suspendComponent(DebuggerPanel.this.comp, null);
				}
				else
				{
					ces.resumeComponent(DebuggerPanel.this.comp, null);
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

		
		ces.getComponentDescription(comp, new IResultListener()
		{
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
			
			public void resultAvailable(Object result)
			{
				updatePanel((IComponentDescription)result);
			}
		});

		ces.addComponentListener(comp, new IComponentListener()
		{			
			public void componentChanged(IComponentDescription desc)
			{
				updatePanel(desc);
			}
			public void componentRemoved(IComponentDescription desc)
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
