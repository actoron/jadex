package jadex.tools.comanalyzer;

import jadex.bridge.IComponentInstance;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.IToolAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * The adapter resides inside the agent, communicating with the tool and (on
 * demand) listenes for specific systemevents triggered in the agent.
 * 
 * TODO: combine with IntrospectorAdapter to have one generic tool adapter for
 * all tools receiving events from agents (e.g.
 * jadex.tools.common.ToolEventAdapter)
 */
public class ComanalyzerAdapter implements IToolAdapter//, ISystemEventListener, Serializable
{
	// -------- constants --------

	/** The comanalyzer tool type. */
	public static final String TOOL_COMANALYZER = "comanalyzer";

	//-------- attibutes -------
	
	/** The agent. */
	protected IComponentInstance agent;
	
	/** The sniff state. */
//	protected boolean issniffing;
	
	/** The observing tools. */
	protected List tools;

	//-------- constructors --------

	/**
	 *  Create a new comanalyzer adapter.
	 */
	public void init(IComponentInstance agent)
	{
		this.agent = agent;
		this.tools = new ArrayList();
	}
	
	//-------- methods --------
	
//	/**
//	 *  Set the sniff state.
//	 *  @param sniffstate The sniffstate.
//	 */
//	public void setSniffing(boolean sniffstate)
//	{
//		this.issniffing = sniffstate;
//	}
//	
//	/**
//	 *  Get the sniff state.
//	 *  @return The sniffstate.
//	 */
//	public boolean isSniffing()
//	{
//		return issniffing;
//	}
	
	/**
	 *  Add a tool.
	 *  @param tool The tool.
	 */
	public void addTool(ComanalyzerPlugin tool)
	{
		tools.add(tool);
	}
	
	/**
	 *  Add a tool.
	 *  @param tool The tool.
	 */
	public void removeTool(ComanalyzerPlugin tool)
	{
		tools.remove(tool);
	}
	
	//-------- IToolAdapter interface --------

	/**
	 *  Called when the agent sent a message.
	 */
	public void	messageSent(IMessageAdapter msg)
	{
//		System.out.println("Message sent: "+msg);

		for(int i=0; i<tools.size(); i++)
		{
			ComanalyzerPlugin coma = (ComanalyzerPlugin)tools.get(i);
			coma.addMessage(msg);
		}
	}
	
	/**
	 *  Called when the agent receives a message.
	 *  May be called from external (i.e. non-agent) threads.
	 *  The methods return value indicates if the message is
	 *  handled by the tool ("tool message") and
	 *    should not be propagated to the agent itself.
	 *  @return True, when the message was handled by the tool and
	 *    should not be propagated to the agent itself.
	 */
	public boolean	messageReceived(IMessageAdapter msg)
	{
//		System.out.println("Message received: "+msg);
		
		for(int i=0; i<tools.size(); i++)
		{
			ComanalyzerPlugin coma = (ComanalyzerPlugin)tools.get(i);
			coma.addMessage(msg);
		}
		
		return false;
	}

	/**
	 *  Called when the agent is about to execute a step
	 *  ("agenda action").
	 *  Always called on the agent thread.
	 *  The methods return value indicates if the agent
	 *  is allowed to execute a step. If some tool
	 *  prevents the execution of steps, the agent will be
	 *  blocked until it is released by the tool.
	 *  
	 *  Messages are still received by tools and a blocking
	 *  tool should call wakeup() on the agent, once the
	 *  agent may continue to run.
	 *  
	 *  @return True, when the agent is allowed to execute
	 *    i.e. not blocked.
	 */
	public boolean executeAction()
	{
				
		return true;
	}
	
	/**
	 * Handle a request from a tool agent.
	 * 
	 * @param sender The tool agent that issued the request.
	 * @param request The the request or query.
	 * @param reply A callback interface to the platform allowing the tool
	 * adapter to send messages to the tool agents.
	 * /
	public void handleToolRequest(AgentIdentifier sender, AgentAction action, IToolReply reply)
	{
		// System.out.println("Comanalyzer Tool request: "+action+ " for " +  agent + " from " + reply);

		try
		{
			if(action instanceof SniffOn)
			{
				// System.out.println("SiffOn received from " + sender + " to " +  agent);

				String[] ltypes = ((SniffOn)action).getEventTypes();
				Set stypes = SCollection.createHashSet();
				for(int i = 0; i < ltypes.length; i++)
					stypes.addAll(ISystemEventTypes.Subtypes.getSubtypes(ltypes[i]));
				// System.out.println("new event types: "+stypes);

				this.tools.put(sender, stypes);
				this.repliers.put(sender, reply);

				updateListener();

				reply.sendInform(new Done(action), false);
			}
			else if(action instanceof SniffOff)
			{

				// System.out.println("SiffOff received from " + sender + " to"+ agent);

				this.tools.removeKey(sender);
				this.repliers.remove(sender);

				updateListener();

				reply.sendInform(new Done(action), false);

				// Hack!!! used to remove outstanding reply waits.
				reply.cleanup();
			}

		}
		catch(MessageFailureException e)
		{
			// Remove agent on message failure.
			this.tools.removeKey(sender);
			this.repliers.remove(sender);

			updateListener();
		}
	}*/

	/**
	 * The tool type supported by this adapter.
	 * /
	public Class getMessageClass()
	{
		return ToolRequest.class;
	}*/

	// -------- Interface ISystemEventListener --------

	/**
	 * An event occured in the model or in the subcapabilities.
	 * @param events The event.
	 * /
	public void systemEventsOccurred(SystemEvent[] events)
	{

		// Update listener registration, when capabilities get added/removed.
		for(int e = 0; e < events.length; e++)
		{
			if(events[e].getType().equals(SystemEvent.CAPABILITY_ADDED))
			{
				((RCapability)events[e].getSource()).addSystemEventListener(this, filter, false, false);
			}
			else if(events[e].getType().equals(SystemEvent.CAPABILITY_REMOVED))
			{
				((RCapability)events[e].getSource()).removeSystemEventListener(this);
			}
		}

		for(int i = 0; i < tools.size(); i++)
		{
			Collection types = (Collection)tools.get(i);
			// Filter out events relevant for tool.
			ArrayList matched = SCollection.createArrayList();
			for(int e = 0; e < events.length; e++)
			{
				if(types.contains(events[e].getType()))
				{
					matched.add(events[e]);
				}
			}

			// Inform tool, when some event matched.
			if(matched.size() > 0)
			{
				try
				{
					externalizeSystemEvents(matched);
					SniffState state = new SniffState(TOOL_COMANALYZER);
					state.setSystemEvents((SystemEvent[])matched.toArray(new SystemEvent[matched.size()]));
					IToolReply reply = (IToolReply)repliers.get(tools.getKey(i));
					reply.sendInform(state, true);
				}
				catch(TimeoutException e)
				{
					// Tool not reachable -> remove.
					System.out.println("Comanalyser failure: " + e);
					repliers.remove(tools.getKey(i));
					tools.remove(i);
					updateListener();
				}
				catch(MessageFailureException e)
				{
					// Tool not reachable -> remove.
					System.out.println("Comanalyser failure: " + e);
					repliers.remove(tools.getKey(i));
					tools.remove(i);
					updateListener();
				}
			}
		}
	}*/

	// -------- helper methods --------

	/**
	 * Register, modify or deregister the listener based on currently active
	 * tools.
	 * /
	protected void updateListener()
	{
		Set eventtypes = new HashSet();
		for(int i = 0; i < tools.size(); i++)
			eventtypes.addAll((Set)tools.get(i));

		// Register new listener
		if(filter == null && !eventtypes.isEmpty())
		{
			filter = new SystemEventFilter((String[])eventtypes.toArray(new String[eventtypes.size()]));
			List caps = agent.getAllCapabilities();
			for(int i = 0; i < caps.size(); i++)
			{
				((RCapability)caps.get(i)).addSystemEventListener(this, filter, false, false);
			}
		}

		// Modify existing listener.
		else if(filter != null && !eventtypes.isEmpty())
		{
			filter.setEventTypes((String[])eventtypes.toArray(new String[eventtypes.size()]));
		}

		// Deregister listener.
		else if(filter != null && eventtypes.isEmpty())
		{
			this.filter = null;
			List caps = agent.getAllCapabilities();
			for(int i = 0; i < caps.size(); i++)
			{
				((RCapability)caps.get(i)).removeSystemEventListener(this);
			}
		}
	}*/

	/**
	 * Externalize the given system events. The message event is the source of
	 * the system event.
	 * 
	 * @param events A List of events to externalize
	 * /
	protected void externalizeSystemEvents(List events)
	{
		for(int i = 0; i < events.size(); i++)
		{
			SystemEvent event = (SystemEvent)events.get(i);
			event = (SystemEvent)event.clone();
			events.set(i, event);
			IRMessageEvent me = (IRMessageEvent)event.getSource();
			event.setSource(encodeMessage(me));
		}
	}*/

	/**
	 * Convert the given messsage into a string, by putting the message
	 * parameters into a map and converting them into xml with nuggets.
	 * 
	 * @param message The message event to encode.
	 * /
	protected String encodeMessage(IRMessageEvent message)
	{
		Map map = new HashMap();

		// System.out.println("encoding " + message.getId() + " in " +
		// agent.getAgentIdentifier());

		// parameters with information about the event event
		map.put(Message.ID, message.getId());
		map.put(Message.DATE, new Date());
		map.put(Message.EVENT_NAME, message.getName());
		map.put(Message.EVENT_TYPE, message.getType());
		map.put(Message.EVENT_DIRECTION, ((IMMessageEvent)message.getModelElement()).getDirection());

		// extract parameters
		IRParameter[] params = message.getParameters();
		for(int i = 0; i < params.length; i++)
		{
			map.put(params[i].getName(), params[i].getValue());
		}
		IRParameterSet[] paramsets = message.getParameterSets();
		for(int i = 0; i < paramsets.length; i++)
		{
			map.put(paramsets[i].getName(), paramsets[i].getValues());
		}

		// if reply get the in_reply messageevent
		if(message.isReply())
			map.put(Message.REPLY_ID, message.getInReplyMessageEvent().getId());

		// Add sender if empty. (Its only empty, when message is to be sent)
		// Should be set when event is created!!!
		if(map.get(Message.SENDER) == null)
			map.put(Message.SENDER, agent.getAgentIdentifier());
		// Add content class if empty. (s.o.)
		if(map.get(Message.CONTENT) != null && map.get(Message.CONTENT_CLASS) == null)
			map.put(Message.CONTENT_CLASS, map.get(Message.CONTENT).getClass());

		// set receiver and direction
		if(map.get(Message.SENDER).equals(agent.getAgentIdentifier()))
		{
			// this msg is to be sent, receiver is set in plugin
			map.put(Message.DIRECTION, Message.MESSAGE_SENT);
		}
		else
		{
			// this msg has been received, set receiver
			map.put(Message.RECEIVER, agent.getAgentIdentifier());
			map.put(Message.DIRECTION, Message.MESSAGE_RECEIVED);
		}

		String msg = Nuggets.objectToXML(map);
		return msg;
	}*/

}
