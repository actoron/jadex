package jadex.bdi.runtime.interpreter;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bridge.DefaultMessageAdapter;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.message.IContentCodec;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.message.MessageType;
import jadex.bridge.service.types.message.MessageType.ParameterSpecification;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.FunctionCall;
import jadex.rules.rulesystem.rules.IConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.IPriorityEvaluator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.LiteralReturnValueConstraint;
import jadex.rules.rulesystem.rules.NotCondition;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.OrConstraint;
import jadex.rules.rulesystem.rules.PredicateConstraint;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.TestCondition;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.functions.MethodCallFunction;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 *  Rules for handling message events.
 *  The following rules are present:
 *  - message arrived: When there is a new message in the inbox then
 *    finding matching message event (and original), create message event and
 *    add it to the capability's message events.
 *  - message to send: When there is a message in the outbox then
 *    transform message to map (encode content) and call sendMessage
 *    on message adapter.
 */
public class MessageEventRules
{
	//-------- constants --------
	
	/** The maximum number of outstanding messages. */
	public static final String MESSAGEEVENTS_MAX = "storedmessages.size";
		
	/**
     * The Class object representing the class corresponding to
     * the this Class. Need due to JavaFlow Bug:
     * http://issues.apache.org/jira/browse/SANDBOX-111
     */
	public static final Class TYPE = MessageEventRules.class;
	
	//-------- methods --------

	/**
	 *  Instantiate an message event.
	 *  @param state	The state
	 *  @param rcapa	The capability.
	 *  @param mevent	The event model.
	 *  @param cevent	The event configuration (if any).
	 *  @return The event instance.
	 */
	public static Object instantiateMessageEvent(IOAVState state, Object rcapa, Object mevent, Object cevent, Map bindings, OAVBDIFetcher fetcher, OAVBDIFetcher configfetcher)
	{
		Object revent = state.createObject(OAVBDIRuntimeModel.messageevent_type);
		state.setAttributeValue(revent, OAVBDIRuntimeModel.element_has_model, mevent);
		
		// todo: adapter?
		if(fetcher==null)
			fetcher = new OAVBDIFetcher(state, rcapa);
		AgentRules.initParameters(state, revent, cevent, fetcher, configfetcher, null, bindings, rcapa);
		
		return revent;
	}

	/**
	 *  Adopt an message event.
	 *  Adds the event to the state (eventbase).
	 *  @param state	The state
	 *  @param rcap	The capability.
	 *  @param rgoal	The goal.
	 * /
	public static void	adoptMessageEvent(IOAVState state, Object rcap, Object rmessageevent, Object rplan)
	{
		state.addAttributeValue(rcap, OAVBDIRuntimeModel.capability_has_messageevents, rmessageevent);
		if(rplan!=null)
			state.removeAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_uservariables, rmessageevent);
		// Hack!!! Only needed for external access!
		BDIInterpreter.getInterpreter(state).getAgentAdapter().wakeup();
	}*/
	
	/**
	 *  Send a message after some delay.
	 *  @param me The message event.
	 *  @return The filter to wait for an answer.
	 */
	public static IFuture	sendMessage(IOAVState state, Object rcapa, Object rmessageevent, byte[] codecids)
	{
		Future ret = new Future();
		
		state.setAttributeValue(rmessageevent, OAVBDIRuntimeModel.messageevent_has_sendfuture, ret);
		if(codecids!=null)
			state.setAttributeValue(rmessageevent, OAVBDIRuntimeModel.messageevent_has_codecids, codecids);
		state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_outbox, rmessageevent);
		
		// Hack!!! Only needed for external access!
		BDIInterpreter.getInterpreter(state).getAgentAdapter().wakeup();
		
		return ret;
	}
	
	/**
	 *  Message matching rule.
	 *  Dispatch a received message.
	 */
	protected static Rule createMessageMatchingRule()
	{
		Variable ragent = new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable mcapa = new Variable("?mcapa", OAVBDIMetaModel.capability_type);
		
		Variable rawmsg = new Variable("?rawmsg", OAVBDIRuntimeModel.java_imessageadapter_type);
		Variable mevent = new Variable("?mevent", OAVBDIMetaModel.messageevent_type);
		Variable params = new Variable("$?params", OAVBDIMetaModel.parameter_type, true, false);
		Variable paramsets = new Variable("$?paramsets", OAVBDIMetaModel.parameterset_type, true, false);
		Variable paramname = new Variable("?paramname", OAVJavaType.java_string_type);
		Variable paramsetname = new Variable("?paramsetname", OAVJavaType.java_string_type);
		Variable matchexp = new Variable("?matchexp", OAVBDIMetaModel.expression_type);

		ObjectCondition messagecon = new ObjectCondition(OAVBDIRuntimeModel.java_imessageadapter_type);
		messagecon.addConstraint(new BoundConstraint(null, rawmsg));
		
		ObjectCondition meventcon = new ObjectCondition(OAVBDIMetaModel.messageevent_type);
		meventcon.addConstraint(new BoundConstraint(null, mevent));
		meventcon.addConstraint(new OrConstraint(new IConstraint[]{
			new LiteralConstraint(OAVBDIMetaModel.messageevent_has_direction, OAVBDIMetaModel.MESSAGE_DIRECTION_RECEIVE),
			new LiteralConstraint(OAVBDIMetaModel.messageevent_has_direction, OAVBDIMetaModel.MESSAGE_DIRECTION_SEND_RECEIVE)
		}));
		meventcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.parameterelement_has_parameters, params));
		meventcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.messageevent_has_match, matchexp));
		
		ObjectCondition ragentcon = new ObjectCondition(OAVBDIRuntimeModel.agent_type);
		ragentcon.addConstraint(new BoundConstraint(null, ragent));
		ragentcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.agent_has_inbox, rawmsg, IOperator.CONTAINS));
		
		ObjectCondition mcapacon = new ObjectCondition(OAVBDIMetaModel.capability_type);
		mcapacon.addConstraint(new BoundConstraint(null, mcapa));
		mcapacon.addConstraint(new BoundConstraint(OAVBDIMetaModel.capability_has_messageevents, mevent, IOperator.CONTAINS));

		ObjectCondition rcapacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapacon.addConstraint(new BoundConstraint(null, rcapa));
		rcapacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mcapa));

		Method method0 = null;
		try{method0 = MessageEventRules.class.getMethod("unequalParameterValues", new Class[]{IOAVState.class, String.class, Object.class, IMessageAdapter.class, Object.class});}
		catch(Exception e){e.printStackTrace();}
		
		// There must not be any fixed parameter which has a different value in the rawmsg 
		ObjectCondition paramcon = new ObjectCondition(OAVBDIMetaModel.parameter_type);
		paramcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.parameter_has_direction, OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED));
		paramcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.modelelement_has_name, paramname));
		paramcon.addConstraint(new BoundConstraint(null, params, IOperator.CONTAINS));
		paramcon.addConstraint(new PredicateConstraint(new FunctionCall(new MethodCallFunction(method0), 
			new Object[]{null, Variable.STATE, paramname, mevent, rawmsg, rcapa})));

		Method method1 = null;
		try{method1 = MessageEventRules.class.getMethod("unequalParameterSetValues", new Class[]{IOAVState.class, String.class, Object.class, IMessageAdapter.class, Object.class});}
		catch(Exception e){e.printStackTrace();}

		// There must not be any fixed parameterset which has a different value in the rawmsg 
		ObjectCondition paramsetcon = new ObjectCondition(OAVBDIMetaModel.parameterset_type);
		paramsetcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.parameterset_has_direction, OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED));
		paramsetcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.modelelement_has_name, paramsetname));
		paramsetcon.addConstraint(new BoundConstraint(null, paramsets, IOperator.CONTAINS));
		paramsetcon.addConstraint(new PredicateConstraint(new FunctionCall(new MethodCallFunction(method1), 
			new Object[]{null, Variable.STATE, paramsetname, mevent, rawmsg, rcapa})));
		
		Method method2 = null;
		try{method2 = MessageEventRules.class.getMethod("evaluateMatchExpression", 
			new Class[]{IOAVState.class, Object.class, IMessageAdapter.class, Object.class});}
		catch(Exception e){e.printStackTrace();}

		TestCondition matchcon = new TestCondition(new PredicateConstraint(new FunctionCall(new MethodCallFunction(method2), 
			new Object[]{null, Variable.STATE, rcapa, rawmsg, matchexp})));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object ragent = assignments.getVariableValue("?ragent");
				Object rcapa = assignments.getVariableValue("?rcapa");
				IMessageAdapter rawmsg = (IMessageAdapter)assignments.getVariableValue("?rawmsg");
				Object mevent = assignments.getVariableValue("?mevent");
				
//				String agentname = BDIInterpreter.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName();
//				System.out.println("Agent has received msg: "+agentname+" "+rawmsg+" "+mevent);
					
				Object revent = createReceivedMessageEvent(state, mevent, rawmsg, rcapa, null);
				state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_messageevents, revent);
				state.setAttributeValue(revent, OAVBDIRuntimeModel.processableelement_has_state,
					OAVBDIRuntimeModel.PROCESSABLEELEMENT_UNPROCESSED);
				state.removeAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_inbox, rawmsg);
			}
		};
		
		Rule message_matching = new Rule("message_matching", new AndCondition(new ICondition[]{messagecon, meventcon, 
			ragentcon, mcapacon, rcapacon, new NotCondition(paramcon), new NotCondition(paramsetcon), matchcon}), action, pe_mm);
		return message_matching;
	}
	
	static IPriorityEvaluator pe_mm = new IPriorityEvaluator()
	{
		public int getPriority(IOAVState state, IVariableAssignments assignments)
		{
			Object mevent = assignments.getVariableValue("?mevent");
			return calculateDegree(state, mevent);
		}
	};
	
	/**
	 *  Message conversation matching rule.
	 *  Dispatch a received message.
	 */
	protected static Rule createMessageConversationMatchingRule()
	{
		Variable ragent = new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		Variable rcapa = new Variable("?rcapa", OAVBDIRuntimeModel.capability_type);
		Variable mcapa = new Variable("?mcapa", OAVBDIMetaModel.capability_type);
		
		Variable rawmsg = new Variable("?rawmsg", OAVBDIRuntimeModel.java_imessageadapter_type);
		Variable mevent = new Variable("?mevent", OAVBDIMetaModel.messageevent_type);
		Variable params = new Variable("$?params", OAVBDIMetaModel.parameter_type, true, false);
		Variable paramsets = new Variable("$?paramsets", OAVBDIMetaModel.parameterset_type, true, false);
		Variable paramname = new Variable("?paramname", OAVJavaType.java_string_type);
		Variable paramsetname = new Variable("?paramsetname", OAVJavaType.java_string_type);
		Variable matchexp = new Variable("?matchexp", OAVBDIMetaModel.expression_type);
//		Variable inreplymsg = new Variable("?inreplymsg", OAVBDIRuntimeModel.messageevent_type);

		Method method3 = null;
		try{method3 = MessageEventRules.class.getMethod("getInReplyMessageEvent", 
			new Class[]{IOAVState.class, IMessageAdapter.class, Object.class});}
		catch(Exception e){e.printStackTrace();}
		
		ObjectCondition messagecon = new ObjectCondition(OAVBDIRuntimeModel.java_imessageadapter_type);
		messagecon.addConstraint(new BoundConstraint(null, rawmsg));
//		messagecon.addConstraint(new BoundConstraint(new MethodCall(null, method3), inreplymsg));
		// Problem: function call values are not cached in rete :-(
		messagecon.addConstraint(new LiteralReturnValueConstraint(null, new FunctionCall(new MethodCallFunction(method3), 
			new Object[]{null, Variable.STATE, rawmsg, rcapa}), IOperator.NOTEQUAL));
		
		ObjectCondition meventcon = new ObjectCondition(OAVBDIMetaModel.messageevent_type);
		meventcon.addConstraint(new BoundConstraint(null, mevent));
		meventcon.addConstraint(new OrConstraint(new IConstraint[]{
			new LiteralConstraint(OAVBDIMetaModel.messageevent_has_direction, OAVBDIMetaModel.MESSAGE_DIRECTION_RECEIVE),
			new LiteralConstraint(OAVBDIMetaModel.messageevent_has_direction, OAVBDIMetaModel.MESSAGE_DIRECTION_SEND_RECEIVE)
		}));
		meventcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.parameterelement_has_parameters, params));
		meventcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.messageevent_has_match, matchexp));
		
		ObjectCondition ragentcon = new ObjectCondition(OAVBDIRuntimeModel.agent_type);
		ragentcon.addConstraint(new BoundConstraint(null, ragent));
		ragentcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.agent_has_inbox, rawmsg, IOperator.CONTAINS));
		
		ObjectCondition mcapacon = new ObjectCondition(OAVBDIMetaModel.capability_type);
		mcapacon.addConstraint(new BoundConstraint(null, mcapa));
		mcapacon.addConstraint(new BoundConstraint(OAVBDIMetaModel.capability_has_messageevents, mevent, IOperator.CONTAINS));

		ObjectCondition rcapacon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		rcapacon.addConstraint(new BoundConstraint(null, rcapa));
		rcapacon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.element_has_model, mcapa));

		Method method0 = null;
		
		try{method0 = MessageEventRules.class.getMethod("unequalParameterValues", new Class[]{IOAVState.class, String.class, Object.class, IMessageAdapter.class, Object.class});}
		catch(Exception e){e.printStackTrace();}
		
		// There must not be any fixed parameter which has a different value in the rawmsg 
		ObjectCondition paramcon = new ObjectCondition(OAVBDIMetaModel.parameter_type);
		paramcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.parameter_has_direction, OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED));
		paramcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.modelelement_has_name, paramname));
		paramcon.addConstraint(new BoundConstraint(null, params, IOperator.CONTAINS));
		paramcon.addConstraint(new PredicateConstraint(new FunctionCall(new MethodCallFunction(method0), 
			new Object[]{null, Variable.STATE, paramname, mevent, rawmsg, rcapa})));

		Method method1 = null;
		try{method1 = MessageEventRules.class.getMethod("unequalParameterSetValues", new Class[]{IOAVState.class, String.class, Object.class, IMessageAdapter.class, Object.class});}
		catch(Exception e){e.printStackTrace();}

		// There must not be any fixed parameterset which has a different value in the rawmsg 
		ObjectCondition paramsetcon = new ObjectCondition(OAVBDIMetaModel.parameterset_type);
		paramsetcon.addConstraint(new LiteralConstraint(OAVBDIMetaModel.parameterset_has_direction, OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED));
		paramsetcon.addConstraint(new BoundConstraint(OAVBDIMetaModel.modelelement_has_name, paramsetname));
		paramsetcon.addConstraint(new BoundConstraint(null, paramsets, IOperator.CONTAINS));
		paramsetcon.addConstraint(new PredicateConstraint(new FunctionCall(new MethodCallFunction(method1), 
			new Object[]{null, Variable.STATE, paramsetname, mevent, rawmsg, rcapa})));
		
		Method method2 = null;
		try{method2 = MessageEventRules.class.getMethod("evaluateMatchExpression", 
			new Class[]{IOAVState.class, Object.class, IMessageAdapter.class, Object.class});}
		catch(Exception e){e.printStackTrace();}

		TestCondition matchcon = new TestCondition(new PredicateConstraint(new FunctionCall(new MethodCallFunction(method2), 
			new Object[]{null, Variable.STATE, rcapa, rawmsg, matchexp})));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object ragent = assignments.getVariableValue("?ragent");
				Object rcapa = assignments.getVariableValue("?rcapa");
				IMessageAdapter rawmsg = (IMessageAdapter)assignments.getVariableValue("?rawmsg");
				Object mevent = assignments.getVariableValue("?mevent");
				
				// todo: fetch value from rete, currently not possible because rete does not cache function call results.
				Object original = getInReplyMessageEvent(state, rawmsg, rcapa);
				
//				String agentname = BDIInterpreter.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName();
//				System.out.println("Agent has received conversation msg: "+agentname+" "+rawmsg+" "+mevent);
					
				Object revent = createReceivedMessageEvent(state, mevent, rawmsg, rcapa, original);
				state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_messageevents, revent);
				state.setAttributeValue(revent, OAVBDIRuntimeModel.processableelement_has_state,
					OAVBDIRuntimeModel.PROCESSABLEELEMENT_UNPROCESSED);
				state.removeAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_inbox, rawmsg);
			}
		};
		
		Rule message_conversation_matching = new Rule("message_matching_conversation",
			new AndCondition(new ICondition[]{meventcon, mcapacon, rcapacon, messagecon, ragentcon,
				new NotCondition(paramcon), new NotCondition(paramsetcon), matchcon}), action, pe_mmc);
		return message_conversation_matching;
	}
	
	static IPriorityEvaluator pe_mmc = new IPriorityEvaluator()
	{
		public int getPriority(IOAVState state, IVariableAssignments assignments)
		{
			// Ensure that these matches are always better than non-conversation matches.
			IMessageAdapter rawmsg = (IMessageAdapter)assignments.getVariableValue("?rawmsg");
			int paramcnt = rawmsg.getMessageType().getParameterNames().length 
				+ rawmsg.getMessageType().getParameterSetNames().length + 1; 
			Object mevent = assignments.getVariableValue("?mevent");
			return calculateDegree(state, mevent) + paramcnt;	
		}
	};
	
	/**
	 *  Message matching rule for the case that no match can be found.
	 *  Dispatch a received message.
	 */
	protected static Rule createMessageNoMatchRule()
	{
		Variable ragent = new Variable("?ragent", OAVBDIRuntimeModel.agent_type);
		Variable rawmsg = new Variable("?rawmsg", OAVBDIRuntimeModel.java_imessageadapter_type);

		ObjectCondition messagecon = new ObjectCondition(OAVBDIRuntimeModel.java_imessageadapter_type);
		messagecon.addConstraint(new BoundConstraint(null, rawmsg));
		
		ObjectCondition agentcon = new ObjectCondition(OAVBDIRuntimeModel.agent_type);
		agentcon.addConstraint(new BoundConstraint(null, ragent));
		agentcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.agent_has_inbox, rawmsg, IOperator.CONTAINS));

		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object ragent = assignments.getVariableValue("?ragent");
				Object rawmsg = assignments.getVariableValue("?rawmsg");

				String agentname = BDIInterpreter.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName();
				BDIInterpreter.getInterpreter(state).getLogger(ragent).severe("Agent has received msg and has found no template: "+agentname+" "+rawmsg);
			
				state.removeAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_inbox, rawmsg);
			}
		};
		
//		IPriorityEvaluator pe = new IPriorityEvaluator()
//		{
//			public int getPriority(IOAVState state, IVariableAssignments assignments)
//			{
//				return -1;
//			}
//		};
		
		Rule message_no_match = new Rule("message_no_match", new AndCondition(new ICondition[]{messagecon, agentcon}), action);//, pe);
		return message_no_match;
	}
	
	/**
	 *  Test if two parameter values are equal.
	 *  @param state The state.
	 *  @param paramname The parameter name.
	 *  @param mevent The message event.
	 *  @param rawmsg The message adapter.
	 *  @param rcapa The capability.
	 *  @return True, if values are not equal.
	 */
	public static boolean unequalParameterValues(IOAVState state, String paramname, Object mevent, IMessageAdapter rawmsg, Object rcapa)
	{
		Object param = state.getAttributeValue(mevent, OAVBDIMetaModel.parameterelement_has_parameters, paramname);
		Object mexp = state.getAttributeValue(param, OAVBDIMetaModel.parameter_has_value);
		OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rcapa);
		Object defvalue = AgentRules.evaluateExpression(state, mexp, fetcher);
		Object msgvalue = getValue(rawmsg, paramname, rcapa);
		return !SUtil.equals(defvalue, msgvalue);
	}
	
	/**
	 *  Test if two parameter values are equal.
	 *  @param state The state.
	 *  @param paramname The parameter name.
	 *  @param mevent The message event.
	 *  @param rawmsg The message adapter.
	 *  @param rcapa The capability.
	 *  @return True, if values are not equal.
	 */
	public static boolean unequalParameterSetValues(IOAVState state, String paramsetname, Object mevent, IMessageAdapter rawmsg, Object rcapa)
	{
		Object paramset = state.getAttributeValue(mevent, OAVBDIMetaModel.parameterelement_has_parametersets, paramsetname);
		OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rcapa);
	
		Collection mexps = state.getAttributeValues(paramset, OAVBDIMetaModel.parameterset_has_values);
		Object mexp = state.getAttributeValues(paramset, OAVBDIMetaModel.parameterset_has_valuesexpression);

		// Create and save the default values that must be contained in the native message to match.
		List vals = new ArrayList();
		if(mexps!=null && mexps.size()>0)
		{
			for(Iterator it=mexps.iterator(); it.hasNext(); )
			{
				vals.add(AgentRules.evaluateExpression(state, it.next(), fetcher));
			}
		}
		else if(mexp!=null)
		{
			Iterator it = SReflect.getIterator(AgentRules.evaluateExpression(state, mexp, fetcher));
			while(it.hasNext())
				vals.add(it.next());
		}

		// Create the message values and store them in a set for quick contains tests.
		Object msgvalue = getValue(rawmsg, paramsetname, rcapa);
		Set msgvals = new HashSet();
		Iterator it = SReflect.getIterator(msgvalue);
		while(it.hasNext())
			msgvals.add(it.next());
		
		// Match each required value of the list.
		return !msgvals.containsAll(vals);
		//System.out.println("matched "+msgevent.getName()+"."+params[i].getName()+": "+pvalue+", "+mvalue+", "+match);
	}
	
	/**
	 *  Evaluate a match expression of a message event template.
	 *  @param state The state.
	 *  @param rcapa The capability.
	 *  @param rawmsg The message adapter.
	 *  @param matchexp The match expression.
	 *  @return True, if match expression is valid.
	 */
	public static boolean evaluateMatchExpression(IOAVState state, Object rcapa, IMessageAdapter rawmsg, Object matchexp)
	{
		boolean ret = true;
		if(matchexp!=null)
		{
			MessageEventFetcher fetcher = new MessageEventFetcher(state, rcapa, rawmsg);
			ret = ((Boolean)AgentRules.evaluateExpression(state, matchexp, fetcher)).booleanValue();
		}
		return ret;
	}
	
	/**
	 *  Message arrived rule.
	 *  Dispatch a received message.
	 */
	protected static Rule createMessageArrivedRule()
	{
		ObjectCondition messagecon = new ObjectCondition(OAVBDIRuntimeModel.java_imessageadapter_type);
		messagecon.addConstraint(new BoundConstraint(null, new Variable("?msg", OAVBDIRuntimeModel.java_imessageadapter_type)));
		ObjectCondition ragentcon = new ObjectCondition(OAVBDIRuntimeModel.agent_type);
		ragentcon.addConstraint(new BoundConstraint(null, new Variable("?ragent", OAVBDIRuntimeModel.agent_type)));
		ragentcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.agent_has_inbox, 
			new Variable("?msg", OAVBDIRuntimeModel.java_imessageadapter_type), IOperator.CONTAINS));

		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object ragent = assignments.getVariableValue("?ragent");
				Logger logger = BDIInterpreter.getInterpreter(state).getAgentAdapter().getLogger();
				IMessageAdapter message = (IMessageAdapter)assignments.getVariableValue("?msg");
				String agentname = BDIInterpreter.getInterpreter(state).getAgentAdapter().getComponentIdentifier().getLocalName();
//				System.out.println("Agent has received msg: "+agentname+" "+message);
				
				// Find the event to which the message is a reply (if any).
				Object original = null;
				
				// Fetch all capabilities.
				List	capas = AgentRules.getAllSubcapabilities(state, ragent);
				
				for(int i=0; i<capas.size(); i++)
				{
					Object rep = getInReplyMessageEvent(state, message, capas.get(i));
					if(rep!=null && original!=null)
					{
//						System.out.println("Reply message problem: "+rep+" "+original);
						logger.severe("Cannot match reply message (multiple capabilities "+rep+", "+original+") for: "+message);
						return;	// Hack!!! Ignore message?
					}
					else if(rep!=null)
					{
						original = rep;
						// Todo: break if production mode.
					}
				}

				// Find all matching event models for received message.
				List	events	= SCollection.createArrayList();
				List	matched	= SCollection.createArrayList();
				int	degree	= 0;

				// For messages without conversation all capabilities are considered.
				if(original==null)
				{
					// Search through event bases to find matching events.
					// Only original message events are considered to respect encapsualtion of a capability.
					//Object	content	= extractMessageContent(msg);
					for(int i=0; i<capas.size(); i++)
					{
						Object capa = capas.get(i);
						Object mcapa = state.getAttributeValue(capa, OAVBDIRuntimeModel.element_has_model);
						Collection mevents = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_messageevents);
						if(mevents!=null)
							degree = matchMessageEvents(state, message, mevents, capa, matched, events, degree);
					}
				}

				// For messages of ongoing conversations only the source capability is considered.
//				// todo
				else
				{
//					System.out.println("Found reply :-) todo matching in capa "+original);
					
					// todo: support original.getScope()
					Object capa = capas.get(0);
					Object mcapa = state.getAttributeValue(capa, OAVBDIRuntimeModel.element_has_model);
					Collection mevents = state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_messageevents);
					if(mevents!=null)
						degree = matchMessageEvents(state, message, mevents, capa, matched, events, degree);
					
//					RCapability capa = original.getScope();
//					IMEventbase eb = (IMEventbase)capa.getEventbase().getModelElement();
//
//					degree = matchMessageEvents(message, eb.getMessageEvents(), capa, matched, events, degree);
//					degree = matchMessageEventReferences(message, eb.getMessageEventReferences(), capa, matched, events, degree);
				}

				if(events.size()==0)
				{
//					System.out.println(agentname+" cannot process message, no message event matches: "+message.getMessage());
					logger.warning(agentname+" cannot process message, no message event matches: "+message.getMessage());
				}
				else
				{
					if(events.size()>1)
					{
						// Multiple matches of highest degree.
//						System.out.println(agentname+" cannot decide which event matches message, " +
//							"using first: "+message.getMessage()+", "+events);
						logger.severe(agentname+" cannot decide which event matches message, " +
							"using first: "+message.getMessage()+", "+events);
					}
					else if(matched.size()>1)
					{
						// Multiple matches but different degrees.
//						System.out.println(agentname+" multiple events matching message, using " +
//							"message event with highest specialization degree: "+message+" ("+degree+"), "+events.get(0)+", "+matched);
						logger.info(agentname+" multiple events matching message, using " +
							"message event with highest specialization degree: "+message+" ("+degree+"), "+events.get(0)+", "+matched);
					}

					Object mevent = ((Object[])events.get(0))[0];
					Object rcapa = ((Object[])events.get(0))[1];
					
//					System.out.println("Matched: "+mevent+" "+message);
					
					Object revent = createReceivedMessageEvent(state, mevent, message, rcapa, original);
					state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_messageevents, revent);
					state.setAttributeValue(revent, OAVBDIRuntimeModel.processableelement_has_state,
						OAVBDIRuntimeModel.PROCESSABLEELEMENT_UNPROCESSED);
					
//					BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
//					OAVTreeModel.createOAVFrame("Agent State", ip.getState(), 
//						BDIInterpreter.getInterpreter(state).getAgent()).setVisible(true);
//					RetePanel.createReteFrame("Agent Rules", 
//						((RetePatternMatcherFunctionality)ip.getRuleSystem().getMatcherFunctionality()).getReteNode(), 
//						((RetePatternMatcherState)ip.getRuleSystem().getMatcherState()).getReteMemory(), new Object());
					
//					scope.getEventbase().dispatchIncomingMessageEvent(mevent, message, original);
				}
				
				state.removeAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_inbox, message);
			}
		};
		
		Rule agent_message_arrived = new Rule("agent_message_arrived", new AndCondition(new ICondition[]{messagecon, ragentcon}), action);
		return agent_message_arrived;
	}
	
	/**
	 *  Send message rule.
	 *  Take message event, create a message map, and hand over to adapter.
	 */
	protected static Rule createSendMessageRule()
	{
		ObjectCondition messagecon = new ObjectCondition(OAVBDIRuntimeModel.messageevent_type);
		messagecon.addConstraint(new BoundConstraint(null, new Variable("?me", OAVBDIRuntimeModel.messageevent_type)));
		ObjectCondition agentcon = new ObjectCondition(OAVBDIRuntimeModel.capability_type);
		agentcon.addConstraint(new BoundConstraint(null, new Variable("?rcapa", OAVBDIRuntimeModel.capability_type)));
		agentcon.addConstraint(new BoundConstraint(OAVBDIRuntimeModel.capability_has_outbox, 
			new Variable("?me", OAVBDIRuntimeModel.messageevent_type), IOperator.CONTAINS));
		
		IAction	action	= new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				final BDIInterpreter interpreter = BDIInterpreter.getInterpreter(state);
				Object rcapa = assignments.getVariableValue("?rcapa");
				Object rme = assignments.getVariableValue("?me");
				final Future ret = (Future)state.getAttributeValue(rme, OAVBDIRuntimeModel.messageevent_has_sendfuture);
				final byte[] codecids = (byte[])state.getAttributeValue(rme, OAVBDIRuntimeModel.messageevent_has_codecids);
						
				// Transform message event to map.
				Object mme = state.getAttributeValue(rme, OAVBDIRuntimeModel.element_has_model);
				MessageType mtype = getMessageEventType(state, mme);
//				MessageType type = Configuration.getConfiguration().getMessageType(mtype);
				
				// todo: check if used parameters are available in type specification
//				MessageType.ParameterSpecification[] params = type.getParameters();
//				MessageType.ParameterSpecification[] paramsets = type.getParameterSets();
				
//				registerMessageEvent(state, rme, rcapa);
				
				// Prepare msg as map
				Map message = SCollection.createHashMap();		
				
				// Copy all parameter values to the map.	
				Collection coll = state.getAttributeValues(rme, 
					OAVBDIRuntimeModel.parameterelement_has_parameters);
				if(coll!=null)
				{
					for(Iterator it=coll.iterator(); it.hasNext(); )
					{
						Object param = it.next();
						String name = (String)state.getAttributeValue(param, OAVBDIRuntimeModel.parameter_has_name);
						Object value = state.getAttributeValue(param, OAVBDIRuntimeModel.parameter_has_value);
					
						if(value!=null)
							message.put(name, value);
					}
				}
				
				// Copy all parameterset values to the map.	
				coll = state.getAttributeValues(rme, 
					OAVBDIRuntimeModel.parameterelement_has_parametersets);
				if(coll!=null)
				{
					for(Iterator it=coll.iterator(); it.hasNext(); )
					{
						Object paramset = it.next();
						String name = (String)state.getAttributeValue(paramset, OAVBDIRuntimeModel.parameterset_has_name);
						Collection values = state.getAttributeValues(paramset, OAVBDIRuntimeModel.parameterset_has_values);
											
						if(values!=null)
						{
							// Must not expose internal state data structures because they could be changed outside
							// Example: message receivers list is enhanced before sending.
							message.put(name, new ArrayList(values));
						}
					}
				}

				// Conversion via agent specific codecs
				IContentCodec[]	codecs	= getContentCodecs(rcapa, state);	// todo: cache codecs.
				for(Iterator it=message.keySet().iterator(); it.hasNext(); )
				{
					String	name	= (String)it.next();
					Object	value	= message.get(name);
					IContentCodec	codec	= mtype.findContentCodec(codecs, message, name);
					if(codec!=null)
					{
						// todo: null? how to get the codec info? and the context & Jadex version, is it needed or is default ok?
						message.put(name, codec.encode(value, state.getTypeModel().getClassLoader(), null, null));
					}
				}
				
				// Check receivers
				// Done on platform level.
//				String rec = mtype.getReceiverIdentifier();
//				Collection recs = (Collection)message.get(rec);
//				if(recs==null || recs.isEmpty())
//				{
//					throw new MessageFailureException(rme, mtype, null, "No receivers specified");
//				}
//				else
//				{
//					for(Iterator it=recs.iterator(); it.hasNext(); )
//					{
//						if(it.next()==null)
//							throw new MessageFailureException(rme, "A receiver nulls");
//					}
//				}
				
				final IMessageAdapter msg = new DefaultMessageAdapter(message, mtype);
				
				SServiceProvider.getService(interpreter.getServiceProvider(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(interpreter.createResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							IFuture	sent = ((IMessageService)result).sendMessage(msg.getParameterMap(), msg.getMessageType(), 
								interpreter.getAgentAdapter().getComponentIdentifier(), interpreter.getModel().getResourceIdentifier(), null, codecids);
//								interpreter.getAgentAdapter().getComponentIdentifier(), interpreter.getState().getTypeModel().getClassLoader(), codecids);
							
							// ret may be null for initial events.
							if(ret!=null)
								sent.addResultListener(new DelegationResultListener(ret));
						}
						
						public void exceptionOccurred(Exception exception)
						{
							// ret may be null for initial events.
							if(ret!=null)
								ret.setException(exception);
						}
					}));

//				interpreter.getComponentAdapter().sendMessage(message, mtype);
				
				state.removeAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_outbox, rme);
				
//				IToolAdapter[] tas = interpreter.getToolAdapters();
//				for(int i=0; i<tas.length; i++)
//					tas[i].messageSent(msg);
				
//				System.out.println("Send message: "+rme);
			}
		};
		
		Rule agent_send_message = new Rule("agent_send_message", 
			new AndCondition(new ICondition[]{messagecon, agentcon}), action);
		return agent_send_message;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Match message events with a message adapter.
	 */
	protected static int matchMessageEvents(IOAVState state, IMessageAdapter message, Collection mevents, 
		Object rcapa, List matched, List events, int degree)
	{
		for(Iterator it=mevents.iterator(); it.hasNext();)
		{
			Object mevent = it.next();
			String dir = (String)state.getAttributeValue(mevent, OAVBDIMetaModel.messageevent_has_direction);

			try
			{
				if((dir.equals(OAVBDIMetaModel.MESSAGE_DIRECTION_RECEIVE)
					|| dir.equals(OAVBDIMetaModel.MESSAGE_DIRECTION_SEND_RECEIVE))
					&& match(state, message, mevent, rcapa))
				{
					matched.add(mevent);
					//int tmp = ((Integer)state.getAttributeValue(mevent, OAVBDIMetaModel.messageevent_has_degree)).intValue();
					int tmp = calculateDegree(state, mevent);
					if(tmp>degree)
					{
						degree	= tmp;
						events.clear();
						events.add(new Object[]{mevent, rcapa});
					}
					else if(tmp==degree)
					{
						events.add(new Object[]{mevent, rcapa});
					}
				}
			}
			catch(RuntimeException e)
			{
				e.printStackTrace();
//				StringWriter	sw	= new StringWriter();
//				e.printStackTrace(new PrintWriter(sw));
//				agent.getLogger().severe(sw.toString());
			}
		}
		return degree;
	}
	
	/**
	 *  Match a message with a message event.
	 *  @param mevent The message event.
	 *  @return True, if message matches the message event.
	 */
	public static boolean match(final IOAVState state, final IMessageAdapter message, Object mevent, final Object scope)
	{
		MessageType mt = message.getMessageType(); 
		
		if(mt==null)
			throw new RuntimeException("Message has no message type: "+message);
		
		MessageType mt2 = getMessageEventType(state, mevent);
		boolean	match	= mt.equals(mt2);
		final IValueFetcher fetcher = new OAVBDIFetcher(state, scope);
		
		// Match against parameters specified in the event type.
		Collection params = state.getAttributeValues(mevent, OAVBDIMetaModel.parameterelement_has_parameters);
		if(params!=null)
		{
			for(Iterator it=params.iterator(); it.hasNext(); )
			{
				Object param = it.next();
				String dir = (String)state.getAttributeValue(param, OAVBDIMetaModel.parameter_has_direction);
				Object mexp = state.getAttributeValue(param, OAVBDIMetaModel.parameter_has_value);
				
				if(dir.equals(OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED) && mexp!=null)
				{
					String name = (String)state.getAttributeValue(param, OAVBDIMetaModel.modelelement_has_name);
					Object defvalue = AgentRules.evaluateExpression(state, mexp, fetcher);
					Object msgvalue = getValue(message, name, scope);
					match = SUtil.equals(defvalue, msgvalue);
		
					//System.out.println("matched "+msgevent.getName()+"."+params[i].getName()+": "+pvalue+", "+mvalue+", "+match);
				}
			}
		}
	
		// Match against parameter sets specified in the event type.
		// todo: this implements a default strategy for param sets by checking if all values
		// todo: of the message event are also contained in the native message
		// todo: this allows further values being contained in the native message
		Collection paramsets = state.getAttributeValues(mevent, OAVBDIMetaModel.parameterelement_has_parametersets);
		if(paramsets!=null)
		{
			for(Iterator it=paramsets.iterator(); it.hasNext(); )
			{
				Object paramset = it.next();
				String dir = (String)state.getAttributeValue(paramset, OAVBDIMetaModel.parameterset_has_direction);
			
				Collection mexps = state.getAttributeValues(paramset, OAVBDIMetaModel.parameterset_has_values);
				Object mexp = state.getAttributeValues(paramset, OAVBDIMetaModel.parameterset_has_valuesexpression);
					
				if(dir.equals(OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED))
				{
					// Create and save the default values that must be contained in the native message to match.
					List vals = new ArrayList();
					if(mexps!=null && mexps.size()>0)
					{
						for(Iterator it2=mexps.iterator(); it2.hasNext(); )
						{
							vals.add(AgentRules.evaluateExpression(state, it2.next(), fetcher));
						}
					}
					else if(mexp!=null)
					{
						Iterator it2 = SReflect.getIterator(AgentRules.evaluateExpression(state, it.next(), fetcher));
						while(it2.hasNext())
							vals.add(it2.next());
					}
		
					// Create the message values and store them in a set for quick contains tests.
					String name = (String)state.getAttributeValue(paramset, OAVBDIMetaModel.modelelement_has_name);
					Object msgvalue = getValue(message, name, scope);
					Set msgvals = new HashSet();
					Iterator	it2	= SReflect.getIterator(msgvalue);
					while(it2.hasNext())
						msgvals.add(it2.next());
					// Match each required value of the list.
					match = msgvals.containsAll(vals);
					//System.out.println("matched "+msgevent.getName()+"."+params[i].getName()+": "+pvalue+", "+mvalue+", "+match);
				}
			}
		}
		
		// Match against match expression.
		Object matchexp = state.getAttributeValue(mevent, OAVBDIMetaModel.messageevent_has_match);
		if(match && matchexp!=null)
		{
			//System.out.println("Matchexp: "+msgevent.getMatchExpression()+" "+msgevent.getName());
			final MessageType mtype = getMessageEventType(state, mevent);
			
			IValueFetcher fetcher2 = new IValueFetcher()
			{
				public Object fetchValue(String name)
				{
					boolean found = false;
					Object ret = null;
					
					if(name==null)
						throw new RuntimeException("Name must not be null.");
					
					if(name.startsWith("$"))
					{
						String tmp = name.substring(1);
						ParameterSpecification ps = mtype.getParameter(tmp);
						if(ps!=null)
						{
							ret = getValue(message, tmp, scope);
							found = true;
						}
					}
					
					if(!found)
						ret = fetcher.fetchValue(name);
					
					// needed?
					// Hack! converts "-" to "_" because variable names must not contain "-" in Java
					// String paramname = "$"+ SUtil.replace(tparams[i].getName(), "-", "_");
					//exparams.put("$messagemap", exparams.getLocalMap());
					
					return ret;
				}
				
				public Object fetchValue(String name, Object object)
				{
					return fetcher.fetchValue(name, object);
				}
			};
			
			try
			{
				//System.out.println(exparams);
				match = ((Boolean)AgentRules.evaluateExpression(state, matchexp, fetcher2)).booleanValue();
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
	 *  Get the value for a parameter, or the values
	 *  for a parameter set from a native message.
	 *  Parameter set values can be provided as array, collection,
	 *  iterator or enumeration.
	 */
	public static Object getValue(IMessageAdapter message, String name, Object scope)
	{
		// codecs are handled on platform level.
		Object val = message.getValue(name);
//		System.out.println(name+" "+val);
		return val;
	}
	
	/**
	 *  Fill parameters of a message event from a native one.
	 *  Uses the abstract method getParameterOrSetValue
	 *  to retrieve the values from the native message.
	 */
	public static Object createReceivedMessageEvent(IOAVState state,
		Object mevent, IMessageAdapter msg, Object scope, Object original)
	{
		Object ret = state.createObject(OAVBDIRuntimeModel.messageevent_type);
		state.setAttributeValue(ret, OAVBDIRuntimeModel.element_has_model, mevent);
		
		// Make native message object accessible for plans.
		state.setAttributeValue(ret, OAVBDIRuntimeModel.messageevent_has_nativemessage, msg);
		// Save message id
//		state.setAttributeValue(ret, OAVBDIRuntimeModel.messageevent_has_id, msg.getId());
		
		// Fill all parameter values from the map (if available).
		MessageType mt = msg.getMessageType();
		String[] paramnames = mt.getParameterNames();
		for(int i=0; i<paramnames.length; i++)
		{
			Object	msgvalue = getValue(msg, paramnames[i], scope);
			if(msgvalue!=null)
			{
				BeliefRules.createParameter(state, paramnames[i], msgvalue, mt.getParameter(paramnames[i]).getClazz(), ret, null, scope);
				
//				Object mparam = OAVBDIMetaModel.getElement(state, mevent, 
//					OAVBDIMetaModel.parameterelement_has_parameters, paramnames[i], null);
//				if(mparam!=null)
//					state.setAttributeValue(param, OAVBDIRuntimeModel.element_has_model, mparam);
			}
		}

		// Fill all parameter set values from the map (if available).
		String[] paramsetnames = mt.getParameterSetNames();
		for(int i=0; i<paramsetnames.length; i++)
		{
			Object	msgvalue = getValue(msg, paramsetnames[i], scope);
			if(msgvalue!=null)
			{
				List	vals	= new ArrayList();
				Iterator it = SReflect.getIterator(msgvalue);
				while(it.hasNext())
					vals.add(it.next());
				
				BeliefRules.createParameterSet(state, paramsetnames[i], vals, 
					mt.getParameterSet(paramsetnames[i]).getClazz(), ret, null, scope);
			}
		}
		
		// Store original message.
		if(original!=null)
			state.setAttributeValue(ret, OAVBDIRuntimeModel.messageevent_has_original, original);
		
		return ret;
	}
	
	/**
	 *  Calculate the degree of a message.
	 *  todo: don't call it on every match, but save the values somewhere
	 */
	protected static int calculateDegree(IOAVState state, Object mevent)
	{
		// Start with one to be able to set the priority of no-match to 0.
		int ret = 1;//0;
		
		Collection params = state.getAttributeValues(mevent, OAVBDIMetaModel.parameterelement_has_parameters);
		if(params!=null)
		{
			for(Iterator it=params.iterator(); it.hasNext(); )
			{
				Object param = it.next();
				String dir = (String)state.getAttributeValue(param, OAVBDIMetaModel.parameter_has_direction);
				if(OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED.equals(dir))
					ret++;
			}
		}
	
		Collection paramsets = state.getAttributeValues(mevent, OAVBDIMetaModel.parameterelement_has_parametersets);
		if(paramsets!=null)
		{
			for(Iterator it=paramsets.iterator(); it.hasNext(); )
			{
				Object paramset = it.next();
				String dir = (String)state.getAttributeValue(paramset, OAVBDIMetaModel.parameterset_has_direction);
				if(OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED.equals(dir))
					ret++;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Register a conversation or reply_with to be able
	 *  to send back answers to the source capability.
	 *  @param msgevent The message event.
	 *  todo: indexing for msgevents for speed.
	 */
	public static void registerMessageEvent(IOAVState state,  Object rmevent, Object rcapa)
	{
		// todo: is not the global value :-(
		Collection coll = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_sentmessageevents);
		int smz = getStoredMessagesSize(BDIInterpreter.getInterpreter(state));
		if(smz!=0 && coll!=null && coll.size()>smz)
		{
			BDIInterpreter.getInterpreter(state).getLogger(rcapa).severe("Agent does not save conversation due " +
				"to too many outstanding messages. Increase buffer in properties - storedmessages.size");
		}
		else
		{
			state.addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_sentmessageevents, rmevent);
			coll = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_sentmessageevents);
//			System.out.println("+++"+BDIInterpreter.getInterpreter(state).getAgentAdapter()
//				.getComponentIdentifier()+" has open conversations: "+coll.size()+" "+coll);
//			Thread.dumpStack();
		}
	}
	
	/**
	 *  Get the maximum execution time.
	 *  0 indicates no maximum execution time.
	 *  @return The max execution time.
	 */
	protected static int getStoredMessagesSize(BDIInterpreter interpreter)
	{
		Map	props	= interpreter.getProperties();
		Number num	= props!=null ? (Number)props.get(MESSAGEEVENTS_MAX) : null;
		return num!=null? num.intValue(): 0;
	}
	
	/**
	 *  Remove a registered message event.
	 *  @param msgevent The message event.
	 */
	public static void deregisterMessageEvent(IOAVState state,  Object rmevent, Object rscope)
	{
		state.removeAttributeValue(rscope, OAVBDIRuntimeModel.capability_has_sentmessageevents, rmevent);

//		Collection coll = state.getAttributeValues(rscope, OAVBDIRuntimeModel.capability_has_sentmessageevents);
//		if(!coll.remove(rmevent))
//			throw new RuntimeException("Registration of message event not found: "+rmevent+" "+rscope);
			
//		System.out.println("+++"+BDIInterpreter.getInterpreter(state).getAgentAdapter()
//			.getComponentIdentifier()+" has open conversations: "+coll.size()+" "+coll);
	}
	
	/**
	 *  Test if a message has been registered.
	 */
	public static boolean containsRegisteredMessageEvents(IOAVState state, Object rcapa, Object rmevent)
	{
		boolean ret = false;
		
		List capas = AgentRules.getAllSubcapabilities(state, rcapa);
		for(int i=0; i<capas.size(); i++)
		{
			Collection coll = state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_sentmessageevents);
			if(coll!=null && coll.contains(rmevent))
			{
				ret = true;
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Find a message event that the given native message is a reply to.
	 *  @param message	The (native) message.
	 */
	public static Object getInReplyMessageEvent(IOAVState state, IMessageAdapter rawmessage, Object rscope)
	{
		Collection coll = state.getAttributeValues(rscope, OAVBDIRuntimeModel.capability_has_sentmessageevents);
		if(coll==null)
			return null;
		
//		System.out.println("+++"+BDIInterpreter.getInterpreter(state).getAgentAdapter()
//			.getComponentIdentifier()+" has open conversations: "+coll.size()+" "+coll);	
		
		// Prefer the newest messages for finding replies.
		// todo: conversations should be better supported
		Object ret	= null;
		MessageType mt = rawmessage.getMessageType();
		
		Object[] smes = coll.toArray();
		for(int i=smes.length-1; ret==null && i>-1; i--)
		{
			boolean	fullmatch = true; // Does the message match all convid parameters?
			boolean	contains = false; // Does the message contains at least one (non-null) convid parameter?
			Object msmes = state.getAttributeValue(smes[i], OAVBDIRuntimeModel.element_has_model);
			MessageType mt2 = getMessageEventType(state, msmes);
			
			if(mt.equals(mt2))
			{
				ParameterSpecification[] params = mt.getConversationIdentifiers();
				
				for(int j=0; fullmatch && j<params.length; j++)
				{
					if(!params[j].isSet())
					{
						Object sourceparam = state.getAttributeValue(smes[i], 
							OAVBDIRuntimeModel.parameterelement_has_parameters, params[j].getSource());
						Object sourceval = sourceparam==null? null: state.getAttributeValue(sourceparam, OAVBDIRuntimeModel.parameter_has_value);
						Object destval = getValue(rawmessage, params[j].getName(), rscope);
						fullmatch = SUtil.equals(sourceval, destval);
						contains = contains || sourceval!=null;
					}
					else
					{
						// todo: support that source could be a parameter set
						
						Object sourceparam = state.getAttributeValue(smes[j], 
							OAVBDIRuntimeModel.parameterelement_has_parameters, params[j].getSource());
						Object sourceval = sourceparam==null? null: state.getAttributeValue(sourceparam, OAVBDIRuntimeModel.parameter_has_value);
						Object destvals = getValue(rawmessage, params[j].getName(), rscope);
						
						if(destvals!=null)
						{
							Iterator it = SReflect.getIterator(destvals);
							
							if(!it.hasNext() && sourceval!=null)
								fullmatch = false;
							
							while(it.hasNext())
							{
								Object destval = it.next();
								fullmatch = SUtil.equals(sourceval, destval);
								contains = contains || sourceval!=null;
							}
						}
						else if(sourceval!=null)
						{
							fullmatch = false;
						}
					}
				}
			}
			
			if(contains && fullmatch)
			{
				ret	= smes[i];
			}
		}
		
		return ret;
	}

	/**
	 *  Create a reply to this message event.
	 *  @param msgeventtype	The message event type.
	 *  @return The reply event.
	 */
	public static Object initializeReply(IOAVState state, Object rcapa, Object revent, Object rreply)
	{
		Object mevent = state.getAttributeValue(revent, OAVBDIRuntimeModel.element_has_model);		
		Object mreply = state.getAttributeValue(rreply, OAVBDIRuntimeModel.element_has_model);
		MessageType mtreply = getMessageEventType(state, mreply);
		MessageType mtevent = getMessageEventType(state, mevent);

		// Check if events are of same type.
		if(!SUtil.equals(mtreply, mtevent))
		{
			throw new RuntimeException("Cannot create reply of incompatible message type: "+
				mtevent+" "+mtreply);
		}

		// todo
//		event.setInReplyMessageEvent(this);

		// Copy parameter(set) values as specified in event type.
//		MessageType mt = Configuration.getConfiguration().getMessageType(mtreplyname);
		
		MessageType.ParameterSpecification[] params	= mtreply.getParameters();
		for(int i=0; i<params.length; i++)
		{
			String sourcename = params[i].getSource();
			if(sourcename!=null)
			{
				Object mdestparam = state.getAttributeValue(mreply, OAVBDIMetaModel.parameterelement_has_parameters, 
					params[i].getName());
				String dir = null;
				if(mdestparam!=null)
					dir = (String)state.getAttributeValue(mdestparam, OAVBDIMetaModel.parameter_has_direction);
				if(!OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED.equals(dir))
				{
					Object sourceval = null;
					Object sourceparam = state.getAttributeValue(revent, 
						OAVBDIRuntimeModel.parameterelement_has_parameters, sourcename);
						
					if(sourceparam!=null)
						sourceval = state.getAttributeValue(sourceparam, OAVBDIRuntimeModel.parameter_has_value);
					// todo: model value copy
					
//					if(sourceval!=null)	// Override null values also (e.g. language==null of received event overrides language==jadex_xml defined in model).
					{
//						System.out.println("Copied: "+sourceval);
						Object destparam = state.getAttributeValue(rreply, OAVBDIRuntimeModel.parameterelement_has_parameters, params[i].getName());
						if(destparam==null)
						{
							Class clazz = mdestparam!=null? (Class)state.getAttributeValue(mdestparam, OAVBDIMetaModel.typedelement_has_class): params[i].getClazz();
							destparam = BeliefRules.createParameter(state, params[i].getName(), null, clazz, rreply, mdestparam, rcapa);
						}
						BeliefRules.setParameterValue(state, destparam, sourceval);
					}
				}
			}
		}
		
		MessageType.ParameterSpecification[] paramsets	= mtreply.getParameterSets();
		for(int i=0; i<paramsets.length; i++)
		{
			String sourcename = paramsets[i].getSource();
			if(sourcename!=null)
			{
				Object mdestparamset = state.getAttributeValue(mreply, OAVBDIMetaModel.parameterelement_has_parametersets, 
					paramsets[i].getName());
				String dir = null;
				if(mdestparamset!=null)
					dir = (String)state.getAttributeValue(mdestparamset, OAVBDIMetaModel.parameterset_has_direction);
				if(!OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED.equals(dir))
				{
					//Collection sourcevals = null;
					Object sourceval = null;
					Object sourceparam = state.getAttributeValue(revent, 
						OAVBDIRuntimeModel.parameterelement_has_parameters, sourcename);
					if(sourceparam!=null)
						sourceval = state.getAttributeValue(sourceparam, OAVBDIRuntimeModel.parameter_has_value);
					// todo: model value copy
					
					if(sourceval!=null)
					{
						Object destparamset = state.getAttributeValue(rreply, OAVBDIRuntimeModel.parameterelement_has_parametersets, paramsets[i].getName());
						if(destparamset==null)
						{
							Class clazz = mdestparamset!=null? (Class)state.getAttributeValue(mdestparamset, OAVBDIMetaModel.typedelement_has_class): paramsets[i].getClazz();
							destparamset = BeliefRules.createParameterSet(state, paramsets[i].getName(), null, clazz, rreply, mdestparamset, rcapa);
						}
						else
						{
							// Clear existing values
							Collection coll = state.getAttributeValues(destparamset, OAVBDIRuntimeModel.parameterset_has_values);
							if(coll!=null)
							{
								for(Iterator it=coll.iterator(); it.hasNext(); )
									BeliefRules.removeParameterSetValue(state, destparamset, it.next());
							}
						}
						//for(Iterator it=sourcevals.iterator(); it.hasNext(); )
							BeliefRules.addParameterSetValue(state, destparamset, sourceval);
					}
				}
			}
		}
		
		return rreply;
	}
	
	/**
	 *  Get a matching content codec.
	 *  @param props The properties.
	 *  @return The content codec.
	 */
	public static IContentCodec[] getContentCodecs(Object rcapa, IOAVState state)
	{
		List ret	= null;
		Map	rprops = BDIInterpreter.getInterpreter(state).getProperties(rcapa);
		if(rprops!=null)
		{
			for(Iterator it=rprops.keySet().iterator(); ret==null && it.hasNext();)
			{
				String	name	= (String) it.next();
				if(name.startsWith("contentcodec."))
				{
					if(ret==null)
						ret	= new ArrayList();
					ret.add(rprops.get(name));
				}
			}
		}

		return ret!=null? (IContentCodec[])ret.toArray(new IContentCodec[ret.size()]): null;
	}

	/**
	 *  Get the message event type of a message event.
	 */
	public static MessageType getMessageEventType(IOAVState state, Object mme)
	{
		String	mtype	= (String)state.getAttributeValue(mme, OAVBDIMetaModel.messageevent_has_type);
		BDIInterpreter	bdii	= BDIInterpreter.getInterpreter(state);
		MessageType ret	= bdii.getMessageService().getMessageType(mtype);
		return ret;
	}
}
