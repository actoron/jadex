package jadex.bdi.testcases.events;

import java.util.List;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.bdi.testcases.AbstractMultipleAgentsPlan;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.SFipa;

/**
 *  Receive a message sent to itself and forward it to another agent
 *  by exchanging the receiver in the message.
 */
public class MessageForwardPlan extends AbstractMultipleAgentsPlan
{

	/**
	 * The body method is called on the
	 * instantiated plan instance from the scheduler.
	 */
	public void body()
	{
		if(((Boolean)getBeliefbase().getBelief("testcap.off").getFact()).booleanValue())
		{
//			System.out.println("Received forwarded message succesfully!");
//			killAgent();
			return;
		}

		List<IComponentIdentifier> agents = createAgents("/jadex/bdi/testcases/events/MessageForward.agent.xml", "receiver", new Map[1]);
		
		TestReport tr = new TestReport("forward_message", "Forward a received message.");
		if(assureTest(tr))
		{		
			try
			{
				IMessageEvent me = (IMessageEvent)getReason();
				me.getParameterSet(SFipa.RECEIVERS).removeValues();
				me.getParameterSet(SFipa.RECEIVERS).addValue(agents.get(0));
				sendMessage(me).get();
				tr.setSucceeded(true);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				tr.setReason(e.getMessage());
			}
		}

		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
