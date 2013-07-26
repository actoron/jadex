package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.actions.FindApplicableCandidatesAction;
import jadex.bdiv3.model.MServiceCall;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.ExceptionDelegationResultListener;
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
	protected BDIAgentInterpreter agent;
	
	/** The annotated service interface. */
	protected Class<?> iface;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service wrapper invocation handler.
	 *  @param agent The internal access of the agent.
	 */
	public BDIServiceInvocationHandler(BDIAgentInterpreter agent, Class<?> iface)
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
		String mn = method.toString();
		MServiceCall msc = agent.getBDIModel().getCapability().getService(mn);
		final RServiceCall sc = new RServiceCall(msc, new InvocationInfo(args));
		sc.addListener(new ExceptionDelegationResultListener<Void, Object>(ret)
		{
			public void customResultAvailable(Void result)
			{
				Object res = sc.getInvocationInfo().getResult();
				ret.setResult(res);
			}
		});
		FindApplicableCandidatesAction fac = new FindApplicableCandidatesAction(sc);
		agent.scheduleStep(fac);
		
		return ret;
	}
}
