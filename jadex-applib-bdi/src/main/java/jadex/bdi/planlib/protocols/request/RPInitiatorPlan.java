package jadex.bdi.planlib.protocols.request;

import jadex.bdi.planlib.protocols.AbstractInitiatorPlan;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bridge.fipa.SFipa;
import jadex.commons.SUtil;
import jadex.commons.concurrent.TimeoutException;


/**
 *  The fipa request plan performs the initiator side
 *  of the fipa-request protocol.
 *  The parameters have to be specified in the goal.
 *  The result of the request is stored in the goal.
 */
public class RPInitiatorPlan extends AbstractInitiatorPlan
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
		getLogger().info("Request initiator action called: " + this+" "+getComponentName());

		// Prepare message event.
		request = createMessageEvent("rp_request");
		request.getParameter(SFipa.CONTENT).setValue(getParameter("action").getValue());

		request.getParameterSet("receivers").addValue(getParameter("receiver").getValue());
		if(getParameter("language").getValue()!=null)
			request.getParameter("language").setValue(getParameter("language").getValue());
		if(getParameter("ontology").getValue()!=null)
			request.getParameter("ontology").setValue(getParameter("ontology").getValue());
		if(getParameter("conversation_id").getValue()!=null)
			request.getParameter("conversation_id").setValue(getParameter("conversation_id").getValue());

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
		if(answer.getType().equals("rp_not_understood")
			|| answer.getType().equals("rp_refuse")
			|| answer.getType().equals("rp_failure"))
		{
			Object content = answer.getParameter(SFipa.CONTENT).getValue();
			getLogger().info(getScope().getAgentName() + ": Received"+answer.getType() + content);
			requestFinished(false, new Object[]{answer.getType(), content});
		}
		else if(answer.getType().equals("rp_agree"))
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
		else if(answer.getType().equals("rp_inform"))
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

		if(answer.getType().equals("rp_failure"))
		{
			Object content = answer.getParameter(SFipa.CONTENT).getValue();
			getLogger().info(getScope().getAgentName() + ": Received failure: " + content);
			requestFinished(false, new Object[]{answer.getType(), content});
		}
		else if(answer.getType().equals("rp_inform"))
		{
			Object content = answer.getParameter(SFipa.CONTENT).getValue();
			getLogger().info(getScope().getAgentName() + ": Request succeeded.");
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
		getLogger().info("Request finished with: "+success+" "+ SUtil.arrayToString(result)+" "+this+" "+getComponentName());

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
}
