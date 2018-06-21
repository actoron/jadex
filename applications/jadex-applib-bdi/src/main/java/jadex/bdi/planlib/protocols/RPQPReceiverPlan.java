package jadex.bdi.planlib.protocols;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.SFipa;

/**
 *  Receive a request and answer it.
 */
public class RPQPReceiverPlan extends AbstractReceiverPlan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void body()
	{
		super.body();	// Hack???
		
		IMessageEvent me = (IMessageEvent)getParameter("message").getValue();
		getLogger().info("Receiver got request: "+me);
		IMessageEvent reply;

		try
		{
			Boolean res = decideRequest(me.getParameter(SFipa.CONTENT).getValue(), 
				(IComponentIdentifier)me.getParameter(SFipa.SENDER).getValue());

			if(res!=null)
			{
				if(res.booleanValue())
				{
					getLogger().info("Receiver sent agree.");
					reply = getEventbase().createReply(me, getShortProtocolName()+"_agree");
					sendMessage(reply);
				}
				else
				{
					getLogger().info("Receiver sent refuse.");
					reply = getEventbase().createReply(me, getShortProtocolName()+"_refuse");
					sendMessage(reply);
					return;
				}
			}
		}
		catch(GoalFailureException e)
		//catch(Exception e)
		{
			getLogger().info("No agree/refuse sent.");
			//e.printStackTrace();
		}

		try
		{
			Object res = executeRequest(me.getParameter(SFipa.CONTENT).getValue(), 
				(IComponentIdentifier)me.getParameter(SFipa.SENDER).getValue());
			reply = getEventbase().createReply(me, getShortProtocolName()+"_inform");
			reply.getParameter(SFipa.CONTENT).setValue(res);
			getLogger().info("Receiver sent inform.");
			sendMessage(reply);
			getParameter("result").setValue(res);
		}
		//catch(Exception e)
		catch(GoalFailureException e)
		{
			getLogger().info("Receiver sent failure: "+e);
			reply = getEventbase().createReply(me, getShortProtocolName()+"_failure");
			sendMessage(reply);
		}
	}

	/**
	 *  Decide about the request.
	 *  @param request The request.
	 *  @param initiator The requesting agent.
	 *  @return True, if should send agree. False for sending refuse. Exception/null for sending nothing.
	 */
	public Boolean decideRequest(Object request, IComponentIdentifier initiator)
	{
		IGoal decide_request = createGoal(getShortProtocolName()+"_decide_request");
		decide_request.getParameter("action").setValue(request);
		decide_request.getParameter("initiator").setValue(initiator);
		dispatchSubgoalAndWait(decide_request);
		return (Boolean)decide_request.getParameter("accept").getValue();
	}

	/**
	 *  Execute the request.
	 *  @param request The request.
	 *  @param initiator The requesting agent.
	 *  @return The result.
	 */
	public Object executeRequest(Object request, IComponentIdentifier initiator)
	{
		IGoal execute_request = createGoal(getShortProtocolName()+"_execute_request");
		execute_request.getParameter("action").setValue(request);
		execute_request.getParameter("initiator").setValue(initiator);
		dispatchSubgoalAndWait(execute_request);
		return execute_request.getParameter("result").getValue();
	}
	
	/**
	 *  Get protocol abbrev name.
	 *  @return The protocol abbrev name.
	 */
	protected String getShortProtocolName()
	{
		String ret;
		if(SFipa.PROTOCOL_REQUEST.equals(getParameter("protocol").getValue()))
			ret = "rp";
		else
			ret = "qp";
		return ret;
	}
}
