package jadex.tools.debugger;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *  Show details of a debugged agent.
 */
public class DebuggerPanel extends JPanel
{
	//-------- attributes --------
	
	/** The component adapter. */
	protected IComponentAdapter	adapter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new debugger panel.
	 *  @param container	The service container.
	 *  @param comp	The identifier of the component to be debugged.
	 */
	public DebuggerPanel(IServiceContainer container, IComponentIdentifier comp)
	{
//		// The step action
//		setLayout(new GridBagLayout());
//		final JButton step = new JButton("Step");
//		step.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				steppable.doStep();
//			}
//		});
//		step.setEnabled(steppable.isStepmode());		
//		
//		final JCheckBox	stepmode = new JCheckBox("Step Mode", steppable.isStepmode());
//		stepmode.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				steppable.setStepmode(stepmode.isSelected());
//				step.setEnabled(steppable.isStepmode());		
//			}
//		});
//		
//		steppable.addBreakpointCommand(new ICommand()
//		{
//			public void execute(Object args)
//			{
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						stepmode.setSelected(steppable.isStepmode());
//						step.setEnabled(steppable.isStepmode());		
//					}
//				});
//			}
//		});
//		
//		int row	= 0;
//		int	col	= 0;
//		add(stepmode, new GridBagConstraints(col++, row, 1, 1,
//			1,0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));
//		add(step, new GridBagConstraints(col, row++, GridBagConstraints.REMAINDER, 1,
//			0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));

		
		((IComponentExecutionService)container.getService(IComponentExecutionService.class))
			.getComponentAdapter(comp, new IResultListener()
		{
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
			
			public void resultAvailable(Object result)
			{
				DebuggerPanel.this.adapter	= (IComponentAdapter)result;
				
//				final JPanel	panel	= new ExecutionControlPanel(...);
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						setLayout(new BorderLayout());
//						add("Center", panel);
						
						doLayout();
						repaint();
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
}
