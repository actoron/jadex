package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;

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
	//-------- constants --------
	
	/** The future result parameter name. */
	public static final String	THREAD_PARAMETER_SERVICE_RESULT	= "$$service_result";
	
	/** The user result parameter name. */
	// Todo: remove. use explicit model.
	public static final String	EVENT_PARAMETER_SERVICE_RESULT	= "service_result";
	
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
		Future<Void> ret = new Future<Void>();
		
		MActivity	act	= (MActivity)events.get(method);
//		ThreadContext	tc	= instance.getThreadContext();
		ProcessThread	thread	= new ProcessThread(act, instance.getTopLevelThread(), instance);
		instance.getTopLevelThread().addThread(thread);
//		tc.addThread(thread);

		String[] params	= act.getPropertyNames();
		for(int i=0; i<params.length; i++)
		{
			thread.setParameterValue(params[i], args[i]);
		}
		thread.setParameterValue(THREAD_PARAMETER_SERVICE_RESULT, ret);
		
		instance.step(act, instance, thread, null);
		
		return ret;
	}
}
