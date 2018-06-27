package jadex.bdi.planlib.protocols;

import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bridge.fipa.SFipa;
import jadex.commons.SUtil;
import jadex.commons.TimeoutException;


/**
 *  The fipa request and query plan performs the initiator side
 *  of the fipa-request/query protocol.
 *  The parameters have to be specified in the goal.
 *  The result of the request is stored in the goal.
 */
public class RPQPInitiatorPlan extends AbstractInitiatorPlan
{
	//-------- constants --------

	/** The state indicating a timeout in this plan. */
	protected static final String STATUS_TIMEOUT = "timeout";

	//-------- attributes --------

	/** The request (must be saved because of conversation tracking). */
	protected IMessageEvent request;

	//-------- methods --------

	/**
	 *  Perform the request.
	 */
	public void body()
	{
		super.body();	// Hack???
		
		//getLogger().info(getScope().getName() + ": Request initiator action called: " + this);
		getLogger().info("Request/Query initiator action called: " + this+" "+getComponentName());

		// Prepare message event.
		request = createMessageEvent(getShortProtocolName()+"_request");
		request.getParameter(SFipa.CONTENT).setValue(getParameter("action").getValue());

		request.getParameterSet(SFipa.RECEIVERS).addValue(getParameter("receiver").getValue());
		if(getParameter("language").getValue()!=null)
			request.getParameter(SFipa.LANGUAGE).setValue(getParameter("language").getValue());
		if(getParameter("ontology").getValue()!=null)
			request.getParameter(SFipa.ONTOLOGY).setValue(getParameter("ontology").getValue());

		// Send message and wait for answer.
		try
		{
			getWaitqueue().addReply(request);
			IMessageEvent	event	= sendMessageAndWait(request, getTimeout());
			event = handleFirstAnswer(event);
			handleSecondAnswer(event);
		}
		catch(TimeoutException e)
		{
			requestFinished(false, STATUS_TIMEOUT);
		}
		finally
		{
			getWaitqueue().removeReply(request);
		}
	}

	//-------- helper methods --------

	/**
	 *  Process the first answer.
	 */
	protected IMessageEvent handleFirstAnswer(IMessageEvent answer)
	{
		IMessageEvent ret = null;
		getLogger().info("First answer: " + answer +" "+this);

		// Initiator side of FIPA Request protocol.
		if(answer.getType().equals(getShortProtocolName()+"_not_understood")
			|| answer.getType().equals(getShortProtocolName()+"_refuse")
			|| answer.getType().equals(getShortProtocolName()+"_failure"))
		{
			Object content = answer.getParameter(SFipa.CONTENT).getValue();
			getLogger().info(getScope().getAgentName() + ": Received"+answer.getType() + content);
			requestFinished(false, new Object[]{answer.getType(), content});
		}
		else if(answer.getType().equals(getShortProtocolName()+"_agree"))
		{
			getLogger().info(getScope().getAgentName() + ": Received agree.");

			try
			{
				ret = waitForReply(request, getTimeout());
			}
			catch(TimeoutException e)
			{
				requestFinished(false, STATUS_TIMEOUT);
			}
		}
		else if(answer.getType().equals(getShortProtocolName()+"_inform"))
		{
			ret = answer;
		}
		else
		{
			assert false: "State should not be reached";
		}
		return ret;
	}

	/**
	 *  Process the second answer.
	 */
	protected void handleSecondAnswer(IMessageEvent answer)
	{
		getLogger().info("Second answer: " + answer +" "+this);

		if(answer.getType().equals(getShortProtocolName()+"_failure"))
		{
			Object content = answer.getParameter(SFipa.CONTENT).getValue();
			getLogger().info(getScope().getAgentName() + ": Received failure: " + content);
			requestFinished(false, new Object[]{answer.getType(), content});
		}
		else if(answer.getType().equals(getShortProtocolName()+"_inform"))
		{
			Object content = answer.getParameter(SFipa.CONTENT).getValue();
			getLogger().info(getScope().getAgentName() + ": Protocol succeeded.");
			requestFinished(true, content);
		}
		else
		{
			assert false: "State should not be reached";
		}
	}

	/**
	 *  Method, that is being called, when the request has finished.
	 *  Default implementation sets status and result on goal.
	 *  @param success	The final status of the df search.
	 *  @param result	The result object.
	 */
	protected void requestFinished(boolean success, Object result)
	{
		getLogger().info(getShortProtocolName()+" finished with: "+success+" "+ SUtil.arrayToString(result)+" "+this+" "+getComponentName());

		getParameter("result").setValue(result);

		if(!success)
			fail();
	}
	
	//-------- AbstractInitiatorPlan template methods --------
	
	/**
	 *  Get the initial message.
	 */
	protected IMessageEvent getInitialMessage()
	{
		return request;
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
