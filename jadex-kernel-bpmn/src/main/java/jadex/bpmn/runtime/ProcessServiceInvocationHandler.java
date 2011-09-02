package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *  Invocation handler for mapping service requests to
 *  start events of BPMN processes.
 */
@Service	// Hack!!! Let BasicServiceInvocationHandler know that this is a service implementation.
public class ProcessServiceInvocationHandler implements InvocationHandler
{
	//-------- attributes --------
	
	/** The process instance. */
	protected BpmnInterpreter	instance;
	
	/** The method / event mapping. */
	protected Map	events;
	
	//-------- constructors --------
	
	/**
	 *  Create a new process service invocation handler.
	 */
	public ProcessServiceInvocationHandler(BpmnInterpreter instance, Map events)
	{
		this.instance	= instance;
		this.events	= events;
	}
	
	//-------- InvocationHandler interface --------
	
	/**
	 *  Called when a method is invoked on a proxy.
	 */
	public Object invoke(Object proxy, Method method, Object[] args)	throws Throwable
	{
		MActivity	act	= (MActivity)events.get(method);
		ThreadContext	tc	= instance.getThreadContext();
		ProcessThread	thread	= new ProcessThread(""+instance.idcnt++, act, tc, instance);
		tc.addThread(thread);

		String[]	params	= act.getPropertyNames();
		for(int i=0; i<params.length; i++)
		{
			thread.setParameterValue(params[i], args[i]);
		}
		
		instance.step(act, instance, thread, null);
		
		return IFuture.DONE;
	}
}
