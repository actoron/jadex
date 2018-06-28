package jadex.bdi.planlib.protocols;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;

/**
 *  The state of the execution of an interaction.
 */
public class InteractionState
{
	//-------- constants --------
	
	/** Interaction state initial (not yet started). */
	public static final String INTERACTION_INITIAL = "initial";
	
	/** Interaction state running. */
	public static final String INTERACTION_RUNNING = "running";
	
	/** Interaction state cancelled. */
	public static final String INTERACTION_CANCELLED = "cancelled";
	
	/** Interaction state finished. */
	public static final String INTERACTION_FINISHED = "finished";
		
	/** Constant identifying successful cancellation of interaction. */
	public static final String	CANCELLATION_SUCCEEDED	= "cancellation-succeeded";
	
	/** Constant identifying failed cancellation of interaction (failure explicitly stated by receiver side). */
	public static final String	CANCELLATION_FAILED	= "cancellation-failed";
	
	/** Constant identifying unknown state of cancellation of interaction (no response from receiver side within timeout). */
	public static final String	CANCELLATION_UNKNOWN	= "cancellation-unknown";
	
	//-------- attributes --------
	
	/** The interaction state. */
	protected String interaction_state;
	
	/** The failure (if any). */
	protected Object failure;
	
	/** The received cancel responses (if any). */
	protected Map	cancel_responses;
	
	/** The cancel response contents (if any). */
	protected Map	cancel_response_contents;
	
	//-------- constructors --------
	
	/**
	 *  Create a new interaction state
	 *  in default initial state "running".
	 */
	public InteractionState()
	{
		this.interaction_state	= INTERACTION_INITIAL;
	}
	
	//-------- methods --------

	/**
	 *  Get the failure description.
	 *  @return The failure description (if any).
	 */
	public Object getFailure()
	{
		return failure;
	}

	/**
	 *  Set the failure description.
	 *  @param failure The failure description to set.
	 */
	public void setFailure(Object failure)
	{
		this.failure = failure;
	}

	/**
	 *  Get the interaction state.
	 *  @return The interaction state.
	 */
	public String getInteractionState()
	{
		return interaction_state;
	}

	/**
	 *  Set the interaction state.
	 *  @param interaction state The interaction state to set.
	 */
	public void setInteractionState(String interaction_state)
	{
		this.interaction_state = interaction_state;
	}

	//-------- cancel-related meta-information --------
	
	/**
	 *  Add a cancel response.
	 */
	public void	addCancelResponse(IComponentIdentifier responder, String response, Object content)
	{
		if(cancel_responses==null)
		{
			assert cancel_response_contents==null;
			cancel_responses	= new HashMap();
			cancel_response_contents	= new HashMap();
		}
		
		cancel_responses.put(responder, response);
		cancel_response_contents.put(responder, content);
	}
	
	/**
	 *  Get all agents with a specific cancel response.
	 */
	public IComponentIdentifier[]	getCancelResponders(String response)
	{
		IComponentIdentifier[]	ret;
		if(cancel_responses!=null)
		{
			List	list	= new ArrayList();
			for(Iterator it=cancel_responses.keySet().iterator(); it.hasNext(); )
			{
				Object	agent	= it.next();
				if(response.equals(cancel_responses.get(agent)))
				{
					list.add(agent);
				}
			}
			ret	= (IComponentIdentifier[])list.toArray(new IComponentIdentifier[list.size()]);
		}
		else
		{
			ret	= new IComponentIdentifier[0];
		}
		return ret;
	}

	/**
	 *  Get the cancel response of an agent.
	 */
	public String	getCancelResponse(IComponentIdentifier responder)
	{
		String	ret	= null;
		if(cancel_responses!=null)
		{
			ret	= (String)cancel_responses.get(responder);
		}
		return ret;
	}


	/**
	 *  Get the details of a cancel response of a given agent.
	 *  This will be any information that the agent sent as content
	 *  of the cancel response.
	 */
	public Object	getCancelResponseContent(IComponentIdentifier responder)
	{
		Object	ret	= null;
		if(cancel_response_contents!=null)
		{
			ret	= cancel_response_contents.get(responder);
		}
		return ret;
	}
}
