package jadex.bdiv3.tutorial.b5;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.actions.FindApplicableCandidatesAction;
import jadex.bdiv3.model.MServiceCall;
import jadex.bdiv3.runtime.impl.BDIAgentInterpreter;
import jadex.bdiv3.runtime.impl.InvocationInfo;
import jadex.bdiv3.runtime.impl.RServiceCall;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 
 */
@Service // Used here only to pass allow proxy to be used as service (check is delegated to handler)
public class BDIServiceInvocationHandler implements InvocationHandler
{
	//-------- attributes --------
	
	/** The agent. */
	protected BDIAgent agent;
	
	/** The annotated service interface. */
	protected Class<?> iface;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service wrapper invocation handler.
	 *  @param agent The internal access of the agent.
	 */
	public BDIServiceInvocationHandler(BDIAgent agent, Class<?> iface)
	{
		if(agent==null)
			throw new IllegalArgumentException("Agent must not null.");
		if(iface==null)
			throw new IllegalArgumentException("Rest interface must not be null.");
		this.agent = agent;
		this.iface = iface;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when a wrapper method is invoked.
	 *  Uses the cms to create a new invocation agent and lets this
	 *  agent call the web service. The result is transferred back
	 *  into the result future of the caller.
	 */
	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
	{
		final Future<Object> ret = new Future<Object>();
		
		// Find fitting MServiceCall
		BDIAgentInterpreter ip = (BDIAgentInterpreter)agent.getInterpreter();
		String mn = method.toString();
		MServiceCall msc = ip.getBDIModel().getCapability().getService(mn);
		RServiceCall sc = new RServiceCall(msc, new InvocationInfo(args));
		FindApplicableCandidatesAction fac = new FindApplicableCandidatesAction(sc);
		agent.scheduleStep(fac);
		
//		ip.getCapability().addGoal(goal);
//		goal.setLifecycleState(ia, RGoal.GoalLifecycleState.ADOPTED);
		
//		if(mgoal==null)
//			throw new RuntimeException("Unknown goal type: "+goal);
//		final RGoal rgoal = new RGoal(getInternalAccess(), mgoal, goal, null);
//		rgoal.addGoalListener(new ExceptionDelegationResultListener<Void, E>(ret)
//		{
//			public void customResultAvailable(Void result)
//			{
//				Object res = RGoal.getGoalResult(goal, mgoal, bdimodel.getClassloader());
//				ret.setResult((E)res);
//			}
//		});
//
////		System.out.println("adopt goal");
//		RGoal.adoptGoal(rgoal, getInternalAccess());
		
		return ret;
	}
}
