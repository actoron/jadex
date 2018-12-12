package jadex.bpmn.tools.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import jadex.bpmn.model.MBpmnModel;
import jadex.bridge.IInternalAccess;
import jadex.commons.ICommand;
import jadex.commons.ISteppable;

/**
 * 
 */
public class ExecutionControlPanel extends JPanel
{
	//-------- constructors --------
	
	/**
	 *  Create a new control panel.
	 */
	public ExecutionControlPanel(final ISteppable steppable)
	{
		// The step action
		setLayout(new GridBagLayout());
		final JButton step = new JButton("Step");
		step.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				steppable.doStep();
			}
		});
		step.setEnabled(steppable.isStepmode());		
		
		final JCheckBox	stepmode = new JCheckBox("Step Mode", steppable.isStepmode());
		stepmode.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				steppable.setStepmode(stepmode.isSelected());
				step.setEnabled(steppable.isStepmode());		
			}
		});
		
		steppable.addBreakpointCommand(new ICommand()
		{
			public void execute(Object args)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						stepmode.setSelected(steppable.isStepmode());
						step.setEnabled(steppable.isStepmode());		
					}
				});
			}
		});
		
		int row	= 0;
		int	col	= 0;
		add(stepmode, new GridBagConstraints(col++, row, 1, 1,
			1,0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));
		add(step, new GridBagConstraints(col, row, GridBagConstraints.REMAINDER, 1,
			0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));
		
	}
	
	/**
	 *  Dispose the panel and remove any listeners.
	 */
	public void	dispose()
	{
//		ap.dispose();
//		rulebasepanel.dispose();
//		system.getAgenda().removeAgendaListener(agendalistener);
	}
	
	/**
	 *  Create a frame for a bpmn structure.
	 *  @param title	The title for the frame.
	 *  @param rs	The rule system.
	 *  @return	The frame.
	 */
	public static JFrame createBpmnFrame(String title, IInternalAccess instance, ISteppable steppable)
	{
		JFrame f = new JFrame(title);
		f.getContentPane().setLayout(new BorderLayout());
		ProcessViewPanel vp = new ProcessViewPanel(instance.getExternalAccess(), null);
		ExecutionControlPanel ep = new ExecutionControlPanel(steppable);
		ActivityPanel ap = new ActivityPanel((MBpmnModel)instance.getModel().getRawModel(), steppable);
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		sp.add(ap);
		sp.add(vp);
		f.add(sp, BorderLayout.CENTER);
		f.add(ep, BorderLayout.SOUTH);
//		f.pack();
        f.setSize(600, 400);
        f.setVisible(true);
        sp.setDividerLocation(0.3);
        
		return f;
	}
}
