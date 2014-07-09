package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MSubProcess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.annotation.Service;
import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.javaparser.SJavaParser;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
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
		
		events = new HashMap<String, MActivity>();
				
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
					
					UnparsedExpression uexp = mact.getPropertyValue("method");
					String method = (String)SJavaParser.parseExpression(uexp, model.getModelInfo().getAllImports(), instance.getClassLoader()).getValue(null);
//					String method = mact.getPropertyValue("method");
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
		
		MActivity act = events.get(SReflect.getMethodSignature(method));
		if(act==null)
			act = events.get(method.toString());
		ProcessThread	thread	= new ProcessThread(act, instance.getTopLevelThread(), instance);
		instance.getTopLevelThread().addThread(thread);

//		List<MParameter> params	= act.getParameters(new String[]{MParameter.DIRECTION_IN, MParameter.DIRECTION_INOUT});
//		String[] params	= act.getPropertyNames();
//		if(params!=null && args!=null)
//		{
//			for(int i=0; i<params.size() && i<args.length; i++)
//			{
//				thread.setOrCreateParameterValue(params.get(i).getName(), args[i]);
//			}
//		}
		thread.setOrCreateParameterValue("$callargs", args);
		thread.setOrCreateParameterValue(THREAD_PARAMETER_SERVICE_RESULT, ret);
		
		instance.step(act, instance, thread, null);
		
		return ret;
	}
}
