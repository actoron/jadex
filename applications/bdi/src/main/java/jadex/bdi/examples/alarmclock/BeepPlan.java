package jadex.bdi.examples.alarmclock;

import java.awt.Component;
import java.awt.Toolkit;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jadex.bdiv3x.runtime.Plan;
import jadex.commons.SUtil;
import jadex.commons.gui.GuiCreator;

/**
 *  Play a beep.
 */
public class BeepPlan extends Plan
{
	//-------- methods --------

	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		Alarm	alarm	= (Alarm)getParameter("alarm").getValue();
		final Component	parent	= ((GuiCreator)getBeliefbase().getBelief("gui").getFact()).getGui(); // Hack!
		final String	message	= alarm.getMessage()!=null ? SUtil.wrapText(alarm.getMessage()) : null;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Toolkit.getDefaultToolkit().beep();
				JOptionPane.showMessageDialog(parent, message, "Alarm is due", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}
}
