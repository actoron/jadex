package jadex.tools.convcenter;

import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;


/**
 *  A plan to receive and display messages.
 */
public class FipaReceivePlan extends Plan
{
	//-------- attributes --------

	/** The panel to show the messages. */
	protected FipaConversationPanel	panel;

	//-------- constructors --------

	/**
	 *  Create a new FipaReceivePlan.
	 *  
	 */
	public FipaReceivePlan(FipaConversationPanel panel)
	{
		this.panel	= panel;
	}

	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Hack! todo: avoid unwrap()
//		IMessageEvent	planmsg	= (IMessageEvent)getInitialEvent();
//		IRMessageEvent	rmsg	= (IRMessageEvent)((ElementWrapper)planmsg).unwrap();
//		IMessageEvent	extmsg	= new MessageEventWrapper(rmsg);
//		panel.addMessage(extmsg);
		panel.addMessage((IMessageEvent)getReason());
	}
}
