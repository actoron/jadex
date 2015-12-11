package jadex.bpmn.runtime.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.Tuple2;
import jadex.commons.collection.IndexMap;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;

/**
 *  Task for creating a component.
 */
@Task(description="This task can be used to create new component instances.",
parameters={
	@TaskParameter(name="name", clazz=String.class, direction=TaskParameter.DIRECTION_IN,
	description="The name of the new component instance (optional)."),
	@TaskParameter(name="model", clazz=String.class, direction=TaskParameter.DIRECTION_IN,
	description="The model location of the component."),
	@TaskParameter(name="configuration", clazz=String.class, direction=TaskParameter.DIRECTION_IN,
	description="The configuration of the new component instance (optional)."),
	@TaskParameter(name="subcomponent", clazz=Boolean.class, direction=TaskParameter.DIRECTION_IN,
	description="Flag indicating if the new component instance should be a subcomponent of the workflow (default=false)."),
	@TaskParameter(name="wait", clazz=Boolean.class, direction=TaskParameter.DIRECTION_IN,
	description="Flag indicating if the task should wait for the subcomponent to terminate (default=false, true if result mappings are available)."),
	@TaskParameter(name="suspend", clazz=Boolean.class, direction=TaskParameter.DIRECTION_IN,
	description="Flag indicating if the new component instance is started in suspended mode (default=false)."),
	@TaskParameter(name="master", clazz=Boolean.class, direction=TaskParameter.DIRECTION_IN,
	description="Flag indicating if the new component instance is started as a master (default=false)."),
	@TaskParameter(name="daemon", clazz=Boolean.class, direction=TaskParameter.DIRECTION_IN,
	description="Flag indicating if the new component instance is started as daemon (default=false)."),
	@TaskParameter(name="autoshutdown", clazz=Boolean.class, direction=TaskParameter.DIRECTION_IN,
	description="Flag indicating if the platform should shutdown when the component terminates (default=false)."),
	@TaskParameter(name="monitoring", clazz=Boolean.class, direction=TaskParameter.DIRECTION_IN,
	description="Flag indicating if monitoring should be enabled for the new component (default=false)."),
	@TaskParameter(name="synchronous", clazz=Boolean.class, direction=TaskParameter.DIRECTION_IN,
	description="Flag indicating if the new component instance is started in synchronous mode (default=false)."),
	@TaskParameter(name="persistable", clazz=Boolean.class, direction=TaskParameter.DIRECTION_IN,
	description="Flag indicating if the new component instance is persistable (default=false)."),
	@TaskParameter(name="killlistener", clazz=IResultListener.class, direction=TaskParameter.DIRECTION_IN,
	description="An optional kill listener."),
	@TaskParameter(name="value", clazz=Object.class, direction=TaskParameter.DIRECTION_IN,
	description="The value that is written to the context variable.")}
)
public class CreateComponentTask implements ITask
{
	/** Future called when the component is created to allow compensation (i.e. killing the component). */
	protected Future<IComponentIdentifier> creationfuture = new Future<IComponentIdentifier>();
	
	static Set<String> reserved;
	static
	{
		reserved = new HashSet<String>();
		reserved.add("name");
		reserved.add("model");
		reserved.add("configuration");
		reserved.add("suspend");
		reserved.add("subcomponent");
		reserved.add("killlistener");
		reserved.add("resultmapping");
		reserved.add("wait");
		reserved.add("master");
		reserved.add("monitoring");
		reserved.add("synchronous");
		reserved.add("arguments");
	}
	
	/**
	 *  Execute the task.
	 */
	public IFuture<Void> execute(final ITaskContext context, final IInternalAccess instance)
	{
		final Future<Void> ret = new Future<Void>();
		
		SServiceProvider.getService(instance, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(
			instance.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				String name = (String)context.getParameterValue("name");
				String model = (String)context.getParameterValue("model");
				String config = (String)context.getParameterValue("configuration");
				boolean suspend = context.getParameterValue("suspend")!=null? ((Boolean)context.getParameterValue("suspend")).booleanValue(): false;
				boolean sub = context.getParameterValue("subcomponent")!=null? ((Boolean)context.getParameterValue("subcomponent")).booleanValue(): false;
				final IResultListener killlistener = (IResultListener)context.getParameterValue("killlistener");
				final String[] resultmapping = (String[])context.getParameterValue("resultmapping");
				final boolean wait = context.getParameterValue("wait")!=null? ((Boolean)context.getParameterValue("wait")).booleanValue(): resultmapping!=null;
				Boolean master = context.getParameterValue("master")!=null? (Boolean)context.getParameterValue("master"): null;
				Boolean daemon = context.getParameterValue("daemon")!=null? (Boolean)context.getParameterValue("daemon"): null;
				Boolean autoshutdown = context.getParameterValue("autoshutdown")!=null? (Boolean)context.getParameterValue("autoshutdown"): null;
				Boolean synchronous = context.getParameterValue("synchronous")!=null? (Boolean)context.getParameterValue("synchronous"): null;
				Boolean persistable = context.getParameterValue("persistable")!=null? (Boolean)context.getParameterValue("persistable"): null;
				Boolean monitoring = context.getParameterValue("monitoring")!=null? (Boolean)context.getParameterValue("monitoring"): null;
				RequiredServiceBinding[] bindings = context.getParameterValue("bindings")!=null? (RequiredServiceBinding[])context.getParameterValue("bindings"): null;
				
				Map<String, Object> args = (Map<String, Object>)context.getParameterValue("arguments");
				if(args==null)
				{
					args = new HashMap<String, Object>();
					IndexMap<String, MParameter> params = context.getActivity().getParameters();
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
				
				IIntermediateResultListener<Tuple2<String, Object>> lis = new IIntermediateResultListener<Tuple2<String, Object>>()
				{
					protected Map<String, Object> results;
					
					public void intermediateResultAvailable(Tuple2<String, Object> result)
					{
						addResult(result.getFirstEntity(), result.getSecondEntity());
						
						if(resultmapping!=null)
						{
							for(int i=0; i<resultmapping.length/2; i++)
							{
								if(resultmapping[i].equals(result.getFirstEntity()))
								{
									context.setOrCreateParameterValue(resultmapping[i+1], result.getSecondEntity());
									break;
								}
							}
						}
						
						if(killlistener instanceof IIntermediateResultListener)
							((IIntermediateResultListener<Tuple2<String, Object>>)killlistener).intermediateResultAvailable(result);
					}
					
					public void finished()
					{
						if(killlistener instanceof IIntermediateResultListener)
						{
							((IIntermediateResultListener)killlistener).finished();
						}
						else if(killlistener!=null)
						{
							killlistener.resultAvailable(getResultCollection());
						}
						
						if(wait)
							ret.setResult(null);
					}
					
					public void resultAvailable(Collection<Tuple2<String, Object>> result)
					{
						if(result!=null)
						{
							Map<String, Object> results = Argument.convertArguments(result);
							for(int i=0; i<resultmapping.length/2; i++)
							{
								Object value = results.get(resultmapping[i]);
								context.setOrCreateParameterValue(resultmapping[i+1], value);
								
//								System.out.println("Mapped result value: "+value+" "+resultmapping[i]+" "+resultmapping[i+1]);
							}
						}
						if(killlistener!=null)
							killlistener.resultAvailable(result);
//						listener.resultAvailable(CreateComponentTask.this, null);
						
						if(wait)
							ret.setResult(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						if(killlistener!=null)
							killlistener.exceptionOccurred(exception);
//						listener.exceptionOccurred(CreateComponentTask.this, exception);
						ret.setException(exception);
					}
					
					protected void addResult(String name, Object value)
					{
						if(results==null)
						{
							results = new HashMap<String, Object>();
						}
						results.put(name, value);
					}
					
					public synchronized Collection<Tuple2<String, Object>> getResultCollection()
					{
						Collection<Tuple2<String, Object>> ret = null;
						if(results!=null)
						{
							ret = new ArrayList<Tuple2<String, Object>>();
							for(Iterator<String> it=results.keySet().iterator(); it.hasNext(); )
							{
								String key = it.next();
								ret.add(new Tuple2<String, Object>(key, results.get(key)));
							}
						}
						return ret;
					}
				};
				
				// todo: rid
				// todo: monitoring
				PublishEventLevel elm = monitoring!=null && monitoring.booleanValue() ? PublishEventLevel.COARSE: PublishEventLevel.OFF;
				cms.createComponent(name, model,
					new CreationInfo(config, args, sub? instance.getComponentIdentifier() : null, 
						suspend, master, daemon, autoshutdown, synchronous, persistable, elm ,
						instance.getModel().getAllImports(), bindings,
						instance.getModel().getResourceIdentifier()), lis)
					.addResultListener(instance.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(creationfuture)));
				
				creationfuture.addResultListener(instance.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
				{
					public void customResultAvailable(IComponentIdentifier cid)
					{
						// If should not wait notify that was created
						if(!wait)
							ret.setResult(null);
					}
				}));

//				if(!wait)
//				{
//					ret.setResult(null);
////					listener.resultAvailable(this, null);
//				}
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture<Void> cancel(final IInternalAccess instance)
	{
		final Future<Void> ret = new Future<Void>();
		creationfuture.addResultListener(instance.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<IComponentIdentifier>()
		{
			public void resultAvailable(final IComponentIdentifier cid)
			{
				SServiceProvider.getService(instance, IComponentManagementService.class, 
					RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(instance.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<IComponentManagementService>()
				{
					public void resultAvailable(IComponentManagementService cms)
					{
						IFuture<Map<String, Object>> fut = cms.destroyComponent(cid);
						fut.addResultListener(new ExceptionDelegationResultListener<Map<String,Object>, Void>(ret)
						{
							public void customResultAvailable(Map<String, Object> result)
							{
								ret.setResult(null);
							}
						});
					}
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
						ret.setResult(null);
					}
					
				}));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(null);
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
