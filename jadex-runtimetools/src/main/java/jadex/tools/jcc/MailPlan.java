package jadex.tools.jcc;

import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;



/**
 * A plan to receive and display messages.
 */
public class MailPlan extends Plan
{
	/**
	 * The plan body.
	 */
	public void body()
	{
		final ControlCenter ctrl = (ControlCenter)getBeliefbase().getBelief("jcc").getFact();
		if(ctrl != null)
		{
			ctrl.processMessage((IMessageEvent)getDispatchedElement());
		}
	}
}
