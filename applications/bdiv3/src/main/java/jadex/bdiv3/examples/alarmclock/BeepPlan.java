package jadex.bdiv3.examples.alarmclock;

import java.awt.Component;
import java.awt.Toolkit;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.alarmclock.AlarmclockBDI.NotifyGoal;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.SUtil;

/**
 *  Play a beep.
 */
@Plan
public class BeepPlan
{
	//-------- attributes --------
	
	@PlanCapability
	protected AlarmclockBDI	scope;
	
	@PlanAPI
	protected IPlan	plan;
	
	@PlanReason
	protected NotifyGoal	goal;
	
	//-------- methods --------

	/**
	 * The body method is called on the
	 * instantiated plan instance from the scheduler.
	 */
	@PlanBody
	public void body()
	{
		Alarm	alarm	= goal.getAlarm();
		final Component	parent	= scope.getGui(); // Hack!
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
