package jadex.tools.debugger;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *  Show details of a debugged agent.
 */
public class DebuggerPanel extends JPanel
{
	//-------- attributes --------
	
	/** The service container. */
	protected IServiceContainer	container;
	
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
	public DebuggerPanel(IServiceContainer container, IComponentIdentifier comp)
	{
		this.container	= container;
		this.comp	= comp;
		
		// The step action
		setLayout(new GridBagLayout());
		this.step = new JButton("Step");
		step.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				IComponentExecutionService	ces	= (IComponentExecutionService)
					DebuggerPanel.this.container.getService(IComponentExecutionService.class);
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
					DebuggerPanel.this.container.getService(IComponentExecutionService.class);
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
		add(stepmode, new GridBagConstraints(col++, row, 1, 1,
			1,0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));
		add(step, new GridBagConstraints(col, row++, GridBagConstraints.REMAINDER, 1,
			0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));

		
		// Todo add listener to component state changes.
		updatePanel();
	}

	/**
	 *  Dispose the panel, i.e. remove any associated resources (listeners etc.).
	 */
	public void dispose()
	{
	}

	protected void updatePanel()
	{
		((IComponentExecutionService)container.getService(IComponentExecutionService.class))
			.getComponentDescription(comp, new IResultListener()
		{
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
			
			public void resultAvailable(final Object result)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						boolean	isstep	= IComponentDescription.STATE_SUSPENDED.equals(((IComponentDescription)result).getState());
						stepmode.setSelected(isstep);
						step.setEnabled(isstep);		
					}
				});
			}
		});
	}
}
