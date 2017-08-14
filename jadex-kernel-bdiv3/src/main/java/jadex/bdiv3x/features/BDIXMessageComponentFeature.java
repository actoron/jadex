package jadex.bdiv3x.features;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import jadex.bdiv3.actions.FindApplicableCandidatesAction;
import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.model.MMessageEvent.Direction;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3x.runtime.CapabilityWrapper;
import jadex.bdiv3x.runtime.RMessageEvent;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.component.impl.MessageComponentFeature;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.transformation.binaryserializer.BeanIntrospectorFactory;
import jadex.commons.transformation.traverser.BeanProperty;
import jadex.commons.transformation.traverser.IBeanIntrospector;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;

/**
 *  Extension to allow message injection in agent methods.
 */
public class BDIXMessageComponentFeature extends MessageComponentFeature	implements IInternalBDIXMessageFeature
{
	//-------- constants --------
	
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(IMessageFeature.class, BDIXMessageComponentFeature.class, IInternalBDIXMessageFeature.class);
	
	//-------- attributes --------
	
	/** Sent message tracking (msg->cnt). */
	protected Map<RMessageEvent, Integer> sent_mevents;
	
	/** The maximum number of outstanding messages. */
	protected long mevents_max;
	
	//-------- constructors --------
	
	/**
	 *  Create the feature.
	 */
	public BDIXMessageComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
		
		this.sent_mevents = new WeakHashMap<RMessageEvent, Integer>();
	}
	
	//-------- IInternalMessageFeature interface --------
	
	/**
	 *  Test if there are matching message events in XML description.
	 */
	@Override
	protected void processUnhandledMessage(IMsgSecurityInfos secinf, IMsgHeader header, Object body)
	{
		MMessageEvent mevent = null;
			
//		System.out.println("rec msg: "+body+", "+header);
			
		IInternalBDIAgentFeature bdif = (IInternalBDIAgentFeature)getComponent().getComponentFeature(IBDIXAgentFeature.class);

		// Find all matching event models for received message.
		List<MMessageEvent>	events	= SCollection.createArrayList();
		List<MMessageEvent>	matched	= SCollection.createArrayList();
		int	degree	= 0;
			
		// Find original message event to know in which scope the
		//   new message event type should be searched
		// For messages without conversation all capabilities are considered.
		// For messages of ongoing conversations only the source capability is considered.
		RMessageEvent	original	= null; //getInReplyMessageEvent(message);
		
		// Extract bean properties.
		Map<String, Object>	vals	= new LinkedHashMap<String, Object>();
		if(body!=null)
		{
			IBeanIntrospector	bi	= BeanIntrospectorFactory.getInstance().getBeanIntrospector();
			Map<String, BeanProperty>	bprops	= bi.getBeanProperties(body.getClass(), true, false);
			for(Map.Entry<String, BeanProperty> bprop: bprops.entrySet())
			{
				vals.put(bprop.getKey(), bprop.getValue().getPropertyValue(body));
			}
		}

		
		degree = matchMessageEvents(vals, bdif.getBDIModel().getCapability().getMessageEvents(), matched, events, degree,
			original!=null, original==null ? null : original.getModelElement().getCapabilityName());

		if(events.size()==0)
		{
			getComponent().getLogger().severe(getComponent().getComponentIdentifier()+" cannot process message, no message event matches: "+body+", "+header);
		}
		else
		{
			if(events.size()>1)
			{
				// Multiple matches of highest degree.
				getComponent().getLogger().severe(getComponent().getComponentIdentifier()+" cannot decide which event matches message, " +
					"using first: "+body+", "+header+", "+events);
			}
			else if(matched.size()>1)
			{
				// Multiple matches but different degrees.
				getComponent().getLogger().info(getComponent().getComponentIdentifier()+" multiple events matching message, using " +
					"message event with highest specialization degree: "+body+", "+header+" ("+degree+"), "+events.get(0)+", "+matched);
			}
				
			mevent = events.get(0);
		}
			
		if(mevent!=null)
		{
			RMessageEvent revent = new RMessageEvent(mevent, body, getComponent());
			FindApplicableCandidatesAction fac = new FindApplicableCandidatesAction(revent);
			getComponent().getComponentFeature(IExecutionFeature.class).scheduleStep(fac);
		}
	}
	
	/**
	 *  Match message events with a message adapter.
	 */
	protected int matchMessageEvents(Map<String, Object> message, List<MMessageEvent> mevents, List<MMessageEvent> matched, List<MMessageEvent> events, int degree, boolean checkscope, String scope)
	{
		for(MMessageEvent mevent: mevents)
		{
			if(!checkscope || SUtil.equals(mevent.getCapabilityName(), scope))
			{
				Direction dir = mevent.getDirection();
				if(dir==null)
					System.out.println("null");
	
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
					getComponent().getLogger().severe(sw.toString());
				}
			}
		}
		return degree;
	}
		
	/**
	 *  Match a message with a message event.
	 *  @param msgevent The message event.
	 *  @return True, if message matches the message event.
	 */
	protected boolean match(MMessageEvent msgevent, Map<String, Object> msg)
	{
		boolean	match	= true;

		// Match against parameters specified in the event type.
		for(MParameter param: msgevent.getParameters())
		{
			if(param.getDirection().equals(MParameter.Direction.FIXED) && param.getDefaultValue()!=null)
			{
				Object pvalue = msg.get(param.getName());
				Object mvalue = SJavaParser.parseExpression(param.getDefaultValue(), getComponent().getModel().getAllImports(), 
					getComponent().getClassLoader()).getValue(CapabilityWrapper.getFetcher(getComponent(), param.getDefaultValue().getLanguage()));
				if(!SUtil.equals(pvalue, mvalue))
				{
					match	= false;
					break;
				}

//				System.out.println("matched "+msgevent.getName()+"."+param.getName()+": "+pvalue+", "+mvalue+", "+match);
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

		// Match against match expression.
		UnparsedExpression matchexp = msgevent.getMatchExpression();
		if(match && matchexp!=null)
		{
			Map<String, Object> exparams = new HashMap<String, Object>();
			
//			List<String> names = new ArrayList<String>();
//			for(String name: mt.getParameterNames())
//				names.add(name);
//			for(String name: mt.getParameterSetNames())
//				names.add(name);
//			
//			for(String name: mt.getParameterNames())
//			{
//				try
//				{
//					Object pvalue = msg.get(name);
//					// Hack! converts "-" to "_" because variable names must not contain "-" in Java
//					String paramname = "$"+ SUtil.replace(name, "-", "_");
//					exparams.put(paramname, pvalue);
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
			
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

			try
			{
				exparams.put("$messagemap", msg);
				for(String prop: msg.keySet())
				{
					exparams.put("$"+prop, msg.get(prop));
					// Convert CamelCase to snake_case for FIPA backwards compatibility
			        String snake_case	= prop.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
			        exparams.put("$"+snake_case, msg.get(prop));
//			        System.out.println("added: $"+prop+"/"+snake_case+" -> "+msg.get(prop));
				}
				IParsedExpression exp = SJavaParser.parseExpression(matchexp, getComponent().getModel().getAllImports(), getComponent().getClassLoader());
				match = ((Boolean)exp.getValue(CapabilityWrapper.getFetcher(getComponent(), matchexp.getLanguage(), exparams))).booleanValue();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				match = false;
			}
		}

		return match;
	}
	
	/**
	 *  Register a conversation or reply-with to be able
	 *  to send back answers to the source capability.
	 *  @param msgevent The message event.
	 */
	public void registerMessageEvent(RMessageEvent msgevent)
	{
		if(mevents_max!=0 && sent_mevents.size()>mevents_max)
		{
			getComponent().getLogger().severe("Agent does not save conversation due " +
				"to too many outstanding messages. Increase buffer in runtime.xml - storedmessages.size");
		}
		else
		{
			sent_mevents.put(msgevent, sent_mevents.containsKey(msgevent) ? sent_mevents.get(msgevent)+1 : 1);
		}
	}
	
	/**
	 *  Deregister a conversation or reply-with.
	 *  @param msgevent The message event.
	 */
	public void deregisterMessageEvent(RMessageEvent msgevent)
	{
		if(sent_mevents.containsKey(msgevent))
		{
			int	cnt = sent_mevents.get(msgevent)-1;
			if(cnt>0)
			{
				sent_mevents.put(msgevent, cnt);
			}
			else
			{
				sent_mevents.remove(msgevent);
			}
		}
	}
	
	/**
	 *  Find a message event that the given native message is a reply to.
	 *  @param message The (native) message.
	 */
	public RMessageEvent getInReplyMessageEvent(IMessageAdapter message)
	{
//		System.out.println("+++"+getComponent().getComponentIdentifier()+" has open conversations: "+sent_mevents.size()+" "+sent_mevents);
		
		RMessageEvent	ret	= null;
		// Prefer the newest messages for finding replies.
		// todo: conversations should be better supported
		RMessageEvent[] smes = (RMessageEvent[])sent_mevents.keySet().toArray(new RMessageEvent[0]);
		//todo: indexing for msgevents for speed.

		for(int i=smes.length-1; i>-1; i--)
		{
			boolean	match = true; // Does the message match all convid parameters?
			boolean	matched	= false; // Does the message match at least one (non-null) convid parameter?
//			MessageType	type = ((MMessageEvent)smes[i].getOriginalElement().getModelElement()).getMessageType();
//			MessageType	type = ((MMessageEvent)smes[i].getModelElement()).getType();
//
//			MessageType.ParameterSpecification[] params = type.getParameters();
//			for(int j=0; match && j<params.length; j++)
//			{
//				if(params[j].isConversationIdentifier())
//				{
//					Object sourceval = smes[i].getParameter(params[j].getSource()).getValue();
//					Object destval = message.getValue(params[j].getName());
//					match = SUtil.equals(sourceval, destval);
//					matched = matched || sourceval!=null;
//				}
//			}
//
//			MessageType.ParameterSpecification[] paramsets = type.getParameterSets();
//			for(int j=0; match && j<paramsets.length; j++)
//			{
//				if(paramsets[j].isConversationIdentifier())
//				{
//					Object sourceval = smes[i].getParameter(paramsets[j].getSource()).getValue();
//					Iterator it2 = SReflect.getIterator(message.getValue(paramsets[j].getName()));
//					while(it2.hasNext())
//					{
//						Object destval = it2.next();
//						match = SUtil.equals(sourceval, destval);
//						matched	= matched || sourceval!=null;
//					}
//				}
//			}
			
			if(matched && match)
			{
				// Break tie by using shorter (full) capability name -> prefers outer capa before inner (e.g. cnp_cap vs. cnp_cap/cm_cap).
				if(ret==null || smes[i].getModelElement().getCapabilityName()==null
					|| smes[i].getModelElement().getCapabilityName().length()<(ret.getModelElement().getCapabilityName()==null ? 0 : ret.getModelElement().getCapabilityName().length()))
				{
					ret	= smes[i];
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Test is a message is a reply of another message.
	 *  @param message The (native) message.
	 */
	public static boolean isReply(RMessageEvent msg, RMessageEvent reply)
	{
		//System.out.println("+++"+getScope().getAgent().getName()+" has open conversations: "+sent_mevents.size()+" "+sent_mevents);
		
		// Prefer the newest messages for finding replies.
		// todo: conversations should be better supported
		boolean	match = true; // Does the message match all convid parameters?
		boolean	matched	= false; // Does the message match at least one (non-null) convid parameter?
//		MessageType	type = ((MMessageEvent)smes[i].getOriginalElement().getModelElement()).getMessageType();
//		MessageType	type = ((MMessageEvent)msg.getModelElement()).getType();
//
//		MessageType.ParameterSpecification[] params = type.getParameters();
//		for(int j=0; match && j<params.length; j++)
//		{
//			if(params[j].isConversationIdentifier())
//			{
//				Object sourceval = msg.getParameter(params[j].getSource()).getValue();
//				Object destval = reply.getParameter(params[j].getName()).getValue();
//				match = SUtil.equals(sourceval, destval);
//				matched = matched || sourceval!=null;
//			}
//		}
//
//		MessageType.ParameterSpecification[] paramsets = type.getParameterSets();
//		for(int j=0; match && j<paramsets.length; j++)
//		{
//			if(paramsets[j].isConversationIdentifier())
//			{
//				Object sourceval = msg.getParameter(paramsets[j].getSource()).getValue();
//				Iterator it2 = SReflect.getIterator(reply.getParameter(paramsets[j].getName()).getValue());
//				while(it2.hasNext())
//				{
//					Object destval = it2.next();
//					match = SUtil.equals(sourceval, destval);
//					matched	= matched || sourceval!=null;
//				}
//			}
//		}
		
		return matched && match;
	}
}
