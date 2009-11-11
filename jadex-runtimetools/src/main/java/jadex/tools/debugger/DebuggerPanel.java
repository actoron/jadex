package jadex.tools.debugger;

import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.ICommand;
import jadex.commons.ISteppable;
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
	
	/** The component adapter. */
	protected ISteppable	adapter;
	
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
		// The step action
		setLayout(new GridBagLayout());
		this.step = new JButton("Step");
		step.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(adapter!=null)
					adapter.doStep();
			}
		});
		
		this.stepmode = new JCheckBox("Step Mode");
		stepmode.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				adapter.setStepmode(stepmode.isSelected());
				step.setEnabled(adapter.isStepmode());		
			}
		});
				
		int row	= 0;
		int	col	= 0;
		add(stepmode, new GridBagConstraints(col++, row, 1, 1,
			1,0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));
		add(step, new GridBagConstraints(col, row++, GridBagConstraints.REMAINDER, 1,
			0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));

		
		((IComponentExecutionService)container.getService(IComponentExecutionService.class))
			.getComponentAdapter(comp, new IResultListener()
		{
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
			
			public void resultAvailable(Object result)
			{
				DebuggerPanel.this.adapter	= (ISteppable)result;
				
				adapter.addBreakpointCommand(new ICommand()
				{
					public void execute(Object args)
					{
						updatePanel();
					}
				});

				updatePanel();
			}
		});
	}

	/**
	 *  Dispose the panel, i.e. remove any associated resources (listeners etc.).
	 */
	public void dispose()
	{
	}

	protected void updatePanel()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				stepmode.setSelected(adapter.isStepmode());
				step.setEnabled(adapter.isStepmode());		
			}
		});
	}
}
