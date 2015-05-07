package jadex.bdiv3x.runtime;

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.model.MMessageEvent.Direction;
import jadex.bdiv3.model.MParameter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.IFilter;
import jadex.commons.collection.SCollection;
import jadex.commons.future.Future;
import jadex.javaparser.SJavaParser;
import jadex.micro.AbstractMessageHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 *  Dummy class for loading v2 examples using v3x.
 */
public abstract class Plan
{
	/** The internal access. */
	protected IInternalAccess agent;
	
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public abstract void body();

	/**
	 *  The passed method is called on plan success.
	 */
	public void	passed()
	{
	}

	/**
	 *  The failed method is called on plan failure/abort.
	 */
	public void	failed()
	{
	}

	/**
	 *  The plan was aborted (because of conditional goal
	 *  success or termination from outside).
	 */
	public void aborted()
	{
	}
	
	/**
	 *  Wait for a some time.
	 *  @param duration The duration.
	 */
	public void	waitFor(int timeout)
	{
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(timeout).get();
	}
	
	/**
	 *  Wait for a message event.
	 *  @param type The message event type.
	 */
	public IMessageEvent waitForMessageEvent(String type)
	{
		return waitForMessageEvent(type, -1);
	}

	/**
	 *  Wait for a message event.
	 *  @param type The message event type.
	 *  @param timeout The timeout.
	 */
	public IMessageEvent waitForMessageEvent(String type, long timeout)
	{
		final Future<IMessageEvent> ret = new Future<IMessageEvent>();
		
		final MMessageEvent[] res = new MMessageEvent[1];
		
		IMessageFeature mf = agent.getComponentFeature(IMessageFeature.class);
		mf.addMessageHandler(new AbstractMessageHandler(new IFilter<IMessageAdapter>()
		{
			public boolean filter(IMessageAdapter message)
			{
				boolean ret = false;
				
				IInternalBDIAgentFeature bdif = (IInternalBDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class);
//				List<MMessageEvent> mevents = bdif.getBDIModel().getCapability().getMessageEvents();
//				for(MMessageEvent mevent: mevents)
//				{
//					if(mevent.getDirection())
//				}
				
				// Find the event to which the message is a reply (if any).
//				IRMessageEvent original = null;
//				List	capas = agent.getAllCapabilities();
//				for(int i=0; i<capas.size(); i++)
//				{
//					IRMessageEvent	rep	= ((RCapability)capas.get(i)).getEventbase().getInReplyMessageEvent(message);
//					if(rep!=null && original!=null)
//					{
//						agent.getLogger().severe("Cannot match reply message (multiple capabilities "+rep.getScope().getName()+", "+original.getScope().getName()+") for: "+message);
//						return;	// Hack!!! Ignore message?
//					}
//					else if(rep!=null)
//					{
//						original	= rep;
//						// Todo: break if production mode.
//					}
//				}

				// Find all matching event models for received message.
				List<MMessageEvent>	events	= SCollection.createArrayList();
				List<MMessageEvent>	matched	= SCollection.createArrayList();
				int	degree	= 0;

				degree = matchMessageEvents(message.getParameterMap(), bdif.getBDIModel().getCapability().getMessageEvents(), matched, events, degree);
				
				// For messages without conversation all capabilities are considered.
//				if(original==null)
//				{
//					// Search through event bases to find matching events.
//					// Only original message events are considered to respect encapsualtion of a capability.
//					//Object	content	= extractMessageContent(msg);
//					for(int i=0; i<capas.size(); i++)
//					{
//						RCapability capa = (RCapability)capas.get(i);
//						IMEventbase eb = (IMEventbase)capa.getEventbase().getModelElement();
//						degree = matchMessageEvents(message, eb.getMessageEvents(), matched, events, degree);
//					}
//				}
//
//				// For messages of ongoing conversations only the source capability is considered.
//				else
//				{
//					//System.out.println("Found reply :-) "+original);
//					RCapability capa = original.getScope();
//					IMEventbase eb = (IMEventbase)capa.getEventbase().getModelElement();
//
//					degree = matchMessageEvents(message, eb.getMessageEvents(), matched, events, degree);
//					degree = matchMessageEventReferences(message, eb.getMessageEventReferences(), matched, events, degree);
//				}

				if(events.size()==0)
				{
					agent.getLogger().severe(agent.getComponentIdentifier()+" cannot process message, no message event matches: "+message.getMessage());
				}
				else
				{
					if(events.size()>1)
					{
						// Multiple matches of highest degree.
						agent.getLogger().severe(agent.getComponentIdentifier()+" cannot decide which event matches message, " +
							"using first: "+message.getMessage()+", "+events);
					}
					else if(matched.size()>1)
					{
						// Multiple matches but different degrees.
						agent.getLogger().info(agent.getComponentIdentifier()+" multiple events matching message, using " +
							"message event with highest specialization degree: "+message+" ("+degree+"), "+events.get(0)+", "+matched);
					}

//					IMReferenceableElement	mevent	= (IMReferenceableElement)events.get(0);
//					RCapability	scope	= agent.lookupCapability(mevent.getScope());
//					scope.getEventbase().dispatchIncomingMessageEvent(mevent, message, original);
				
					res[0] = events.get(0);
					ret = true;
				}
				
				return ret;
			}
		}, timeout, true, true)
		{
			public void handleMessage(final Map<String, Object> msg, final MessageType type)
			{
//				System.out.println("received reply: "+msg);
				ret.setResult(new RMessageEvent(res[0], msg, type));
			}
			
			public void timeoutOccurred()
			{
				ret.setException(new TimeoutException());
			}
		});
		
		return ret.get();
	}
	
	/**
	 *  Kill this agent.
	 */
	public void	killAgent()
	{
		agent.killComponent();
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return agent.getLogger();
	}
	
	/**
	 *  Get the beliefbase.
	 *  @return The beliefbase.
	 */
	public IBeliefbase getBeliefbase()
	{
		return null;
	}
	
	/**
	 *  Match message events with a message adapter.
	 */
	protected int matchMessageEvents(Map<String, Object> message, List<MMessageEvent> mevents, List<MMessageEvent> matched, List<MMessageEvent> events, int degree)
	{
		for(MMessageEvent mevent: mevents)
		{
			Direction dir = mevent.getDirection();

			try
			{
				if((dir.equals(Direction.RECEIVE)
					|| dir.equals(Direction.SENDRECEIVE))
					&& match(mevent, message))
				{
					matched.add(mevent);
					if(mevent.getSpecializationDegree()>degree)
					{
						degree	= mevent.getSpecializationDegree();
						events.clear();
						events.add(mevent);
					}
					else if(mevent.getSpecializationDegree()==degree)
					{
						events.add(mevent);
					}
				}
			}
			catch(RuntimeException e)
			{
				StringWriter	sw	= new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				agent.getLogger().severe(sw.toString());
			}
		}
		return degree;
	}
		
	/**
	 *  Match a message with a message event.
	 *  @param msgevent The message event.
	 *  @return True, if message matches the message event.
	 */
	public boolean match(MMessageEvent msgevent, Map<String, Object> msg)//, MessageType mt)
	{
		boolean	match	= true;

//		RCapability scope = agent.lookupCapability(msgevent.getScope());
//		RCapability capa = ((IInternalBDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class)).getCapability();

		// Match against parameters specified in the event type.
		for(MParameter param: msgevent.getParameters())
		{
			if(param.getDirection().equals(jadex.bdiv3.model.MParameter.Direction.FIXED) && param.getValue()!=null)
			{
				Object pvalue = msg.get(param.getName());
				Object mvalue = SJavaParser.parseExpression(param.getValue(), agent.getModel().getAllImports(), agent.getClassLoader()).getValue(agent.getFetcher());
//				Object pvalue = RExpression.evaluateExpression(params[i].getDefaultValue(), scope.getExpressionParameters());
//				Object mvalue = getValue(params[i].getName(), scope);
				match	= pvalue==null && mvalue==null || pvalue!=null && mvalue!=null && pvalue.equals(mvalue);

				//System.out.println("matched "+msgevent.getName()+"."+params[i].getName()+": "+pvalue+", "+mvalue+", "+match);
			}
		}

		// todo:
		// Match against parameter sets specified in the event type.
		// todo: this implements a default strategy for param sets by checking if all values
		// todo: of the message event are also contained in the native message
		// todo: this allows further values being contained in the native message
//			IMParameterSet[]	paramsets	= msgevent.getParameterSets();
//			for(int i=0; match && i<paramsets.length; i++)
//			{
//				if(paramsets[i].getDirection().equals(IMParameterSet.DIRECTION_FIXED))
//				{
//					// Create and save the default values that must be contained in the native message to match.
//					List vals = new ArrayList();
//					if(paramsets[i].getDefaultValues().length>0)
//					{
//						IMExpression[] dvs = paramsets[i].getDefaultValues();
//						for(int j=0; j<dvs.length; j++)
//							vals.add(RExpression.evaluateExpression(dvs[i], null)); // Hack!
//					}
//					else if(paramsets[i].getDefaultValuesExpression()!=null)
//					{
//						Iterator it = SReflect.getIterator(RExpression.evaluateExpression(paramsets[i].getDefaultValuesExpression(), null)); // Hack!
//						while(it.hasNext())
//							vals.add(it.next());
//					}
//
//					// Create the message values and store them in a set for quick contains tests.
//					Object mvalue = getValue(paramsets[i].getName(), scope);
//					Set mvals = new HashSet();
//					Iterator	it	= SReflect.getIterator(mvalue);
//					while(it.hasNext())
//						mvals.add(it.next());
//					// Match each required value of the list.
//					match = mvals.containsAll(vals);
//					//System.out.println("matched "+msgevent.getName()+"."+params[i].getName()+": "+pvalue+", "+mvalue+", "+match);
//				}
//			}

		// todo:
		// Match against match expression.
//		UnparsedExpression matchexp = msgevent.getMatchExpression();
//		if(match && matchexp!=null)
//		{
//			NestedMap exparams = SCollection.createNestedMap(scope.getExpressionParameters());
//			for(int i=0; i<params.length; i++)
//			{
//				try
//				{
//					Object	mvalue	= getValue(params[i].getName(), scope);
//					// Hack! converts "-" to "_" because variable names must not contain "-" in Java
//					String paramname = "$"+ SUtil.replace(params[i].getName(), "-", "_");
//					exparams.put(paramname, mvalue);
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//			for(int i=0; i<paramsets.length; i++)
//			{
//				try
//				{
//					Object mvalue = getValue(paramsets[i].getName(), scope);
//					// Hack! converts "-" to "_" because variable names must not contain "-" in Java
//					String paramsetname = "$"+SUtil.replace(paramsets[i].getName(), "-", "_");
//					exparams.put(paramsetname, mvalue);
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//
//			try
//			{
//				exparams.put("$messagemap", exparams.getLocalMap());
//				match = ((Boolean)RExpression.evaluateExpression(matchexp, exparams)).booleanValue();
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//				match = false;
//			}
//		}

		return match;
	}
}
