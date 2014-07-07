package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MSubProcess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.javaparser.SJavaParser;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	protected BpmnInterpreter instance;
	
	/** The method / event mapping. */
	protected Map<String, MActivity> events;
	
	//-------- constructors --------
	
	/**
	 *  Create a new process service invocation handler.
	 */
	public ProcessServiceInvocationHandler(BpmnInterpreter instance, Map<String, MActivity> events)
	{
		this.instance	= instance;
		this.events	= events;
	}
	
	/**
	 *  Create a new process service invocation handler.
	 */
	public ProcessServiceInvocationHandler(BpmnInterpreter instance, String actid)
	{
		this.instance	= instance;
		
		MBpmnModel model = instance.getModelElement();
		
		MSubProcess proc = (MSubProcess)model.getActivityById(actid);
		final Map<MSubProcess, List<MActivity>> evtsubstarts = model.getEventSubProcessStartEventMapping();
		
		List<MActivity> macts = evtsubstarts.get(proc);
		
		Map<String, MActivity> events = new HashMap<String, MActivity>();
				
		Class<?> iface = null;
		for(MActivity mact: macts)
		{
			if(MBpmnModel.EVENT_START_MESSAGE.equals(mact.getActivityType()))
			{
				if(mact.hasPropertyValue("iface"))
				{
					if(iface==null)
					{
						UnparsedExpression uexp = mact.getPropertyValue("iface");
						iface = (Class<?>)SJavaParser.parseExpression(uexp, model.getModelInfo().getAllImports(), instance.getClassLoader()).getValue(null);
					}
					
					String method = mact.getPropertyValueString("method");
					events.put(method, mact);
				}
			}
		}
	}
	
	//-------- InvocationHandler interface --------
	
	/**
	 *  Called when a method is invoked on a proxy.
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		Future<Void> ret = new Future<Void>();
		
		MActivity act = events.get(method.toString());
		ProcessThread	thread	= new ProcessThread(act, instance.getTopLevelThread(), instance);
		instance.getTopLevelThread().addThread(thread);

		String[] params	= act.getPropertyNames();
		for(int i=0; i<params.length; i++)
		{
			thread.setOrCreateParameterValue(params[i], args[i]);
		}
		thread.setOrCreateParameterValue(THREAD_PARAMETER_SERVICE_RESULT, ret);
		
		instance.step(act, instance, thread, null);
		
		return ret;
	}
}
