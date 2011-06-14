package jadex.bpmn.runtime.task;

import jadex.bpmn.model.MParameter;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *  Task for creating a component.
 */
public class CreateComponentTask implements ITask
{
	static Set reserved;
	static
	{
		reserved = new HashSet();
		reserved.add("name");
		reserved.add("model");
		reserved.add("configuration");
		reserved.add("suspend");
		reserved.add("subcomponent");
		reserved.add("killlistener");
		reserved.add("resultmapping");
		reserved.add("wait");
		reserved.add("master");
		reserved.add("arguments");
	}
	
	/**
	 *  Execute the task.
	 */
	public IFuture execute(final ITaskContext context, final BpmnInterpreter instance)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(instance.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(instance.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				IComponentManagementService	cms	= (IComponentManagementService)result;
				String name = (String)context.getParameterValue("name");
				String model = (String)context.getParameterValue("model");
				String config = (String)context.getParameterValue("configuration");
				boolean suspend = context.getParameterValue("suspend")!=null? ((Boolean)context.getParameterValue("suspend")).booleanValue(): false;
				boolean sub = context.getParameterValue("subcomponent")!=null? ((Boolean)context.getParameterValue("subcomponent")).booleanValue(): false;
				final IResultListener killlistener = (IResultListener)context.getParameterValue("killlistener");
				final String[] resultmapping = (String[])context.getParameterValue("resultmapping");
				boolean wait = context.getParameterValue("wait")!=null? ((Boolean)context.getParameterValue("wait")).booleanValue(): resultmapping!=null;
				Boolean master = context.getParameterValue("master")!=null? (Boolean)context.getParameterValue("master"): null;
				Boolean daemon = context.getParameterValue("daemon")!=null? (Boolean)context.getParameterValue("daemon"): null;
				Boolean autoshutdown = context.getParameterValue("autoshutdown")!=null? (Boolean)context.getParameterValue("autoshutdown"): null;
				RequiredServiceBinding[] bindings = context.getParameterValue("bindings")!=null? (RequiredServiceBinding[])context.getParameterValue("bindings"): null;
				
				Map args = (Map)context.getParameterValue("arguments");
				if(args==null)
				{
					args = new HashMap();
					Map params = context.getActivity().getParameters();
					if(params!=null)
					{
						for(Iterator it=params.values().iterator(); it.hasNext(); )
						{
							MParameter param = (MParameter)it.next();
							if(!reserved.contains(param.getName()))
								args.put(param.getName(), context.getParameterValue(param.getName()));
						}
					}
				}
//				System.out.println("args: "+args);
				IResultListener lis = killlistener;
				if(wait)
				{
					lis = new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							if(result!=null)
							{
								Map results = (Map)result;
								for(int i=0; i<resultmapping.length/2; i++)
								{
									Object value = results.get(resultmapping[i]);
									context.setParameterValue(resultmapping[i+1], value);
									
//									System.out.println("Mapped result value: "+value+" "+resultmapping[i]+" "+resultmapping[i+1]);
								}
							}
							if(killlistener!=null)
								killlistener.resultAvailable(result);
//							listener.resultAvailable(CreateComponentTask.this, null);
							ret.setResult(null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							if(killlistener!=null)
								killlistener.exceptionOccurred(exception);
//							listener.exceptionOccurred(CreateComponentTask.this, exception);
							ret.setException(exception);
						}
					};
				}
				
				cms.createComponent(name, model,
					new CreationInfo(config, args, sub ? instance.getComponentAdapter().getComponentIdentifier() : null, 
						suspend, master, daemon, autoshutdown, instance.getModelElement().getModelInfo().getAllImports(), bindings), lis);

				if(!wait)
				{
					ret.setResult(null);
//					listener.resultAvailable(this, null);
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
//				listener.exceptionOccurred(source, exception);
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "The create component task can be used for creating a new component instance. " +
			"This allows a process to start other processes as well as other kinds of components like agents";
		
		ParameterMetaInfo namemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "name", null, "The name parameter identifies the name of new component instance.");
		ParameterMetaInfo modelmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "model", null, "The model parameter contains the filename of the component to start.");
		ParameterMetaInfo confmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "configuration", null, "The configuration parameter defines the configuration the component should be started in.");
		ParameterMetaInfo suspendmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "suspend", null, "The suspend parameter can be used to create the component in suspended mode.");
		ParameterMetaInfo subcommi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "subcomponent", null, "The subcomponent parameter decides if the new component is considered as subcomponent.");
		ParameterMetaInfo killimi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			IResultListener.class, "killlistener", null, "The killlistener parameter can be used to be notified when the component terminates.");
		ParameterMetaInfo resultmapmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String[].class, "resultmapping", null, "The resultmapping parameter defines the mapping of result to context parameters. " +
				"The string array structure is 0: first result name, 1: first context parameter name, 2: second result name, etc.");
		ParameterMetaInfo waitmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "wait", null, "The wait parameter specifies is the activity should wait for the completeion of the started component." +
				"This is e.g. necessary if the return values should be used.");
		ParameterMetaInfo mastermi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "master", null, "The master parameter decides if the component is considered as master for its parent. The parent" +
				"can implement special logic when a master dies, e.g. an application terminates itself.");
		ParameterMetaInfo argumentsmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Map.class, "arguments", null, "The arguments parameter allows passing an argument map of name value pairs.");

		
		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{namemi, modelmi, confmi, suspendmi, 
			subcommi, killimi, resultmapmi, waitmi, mastermi, argumentsmi}); 
	}
}
