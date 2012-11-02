package jadex.platform.service.cron.bpmn;

import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cron.CronJob;
import jadex.bridge.service.types.cron.ICronService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.IParsedExpression;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.platform.service.cron.TimePatternFilter;
import jadex.platform.service.cron.jobs.CreateCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredServices(
{
	@RequiredService(name="libs", type=ILibraryService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="crons", type=ICronService.class, 
		binding=@Binding(create=true, creationtype="cronagent"))
})
@ComponentTypes(@ComponentType(name="cronagent", filename="jadex/platform/service/cron/CronAgent.class"))
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IStartService.class, implementation=@Implementation(expression="$pojoagent")))
public class StartAgent implements IStartService
{
	
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		IStartService sts = (IStartService)agent.getServiceContainer().getProvidedService(IStartService.class);
		sts.addBpmnModel("jadex/bpmn/testcases/TimerEventStart.bpmn", null).addResultListener(new DefaultResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				System.out.println("monitoring TimerEventStart.bpmn");
			}
		});
	}
	
	/**
	 *  Add a bpmn model that is monitored for start events.
	 */
	public IFuture<Void> addBpmnModel(final String model, final IResourceIdentifier urid)
	{
		final Future<Void> ret = new Future<Void>();
			
		IFuture<ILibraryService> fut = agent.getServiceContainer().getRequiredService("libs");
		fut.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Void>(ret)
		{
			public void customResultAvailable(final ILibraryService libs)
			{
				// job that creates a hello world agent every minute
				final IResourceIdentifier rid = urid!=null? urid: libs.getRootResourceIdentifier();
		
				createCronJob(agent.getExternalAccess(), model, rid)
					.addResultListener(new ExceptionDelegationResultListener<CronJob, Void>(ret)
				{
					public void customResultAvailable(final CronJob cj)
					{
						IFuture<ICronService> fut = agent.getServiceContainer().getRequiredService("crons");
						fut.addResultListener(new ExceptionDelegationResultListener<ICronService, Void>(ret)
						{
							public void customResultAvailable(final ICronService crons)
							{
								crons.addJob(cj).addResultListener(new DelegationResultListener<Void>(ret));
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Remove a bpmn model.
	 */
	public IFuture<Void> removeBpmnModel()
	{
		return new Future<Void>(new UnsupportedOperationException());
	}
	
	/**
	 * 
	 */
	public static IFuture<CronJob> createCronJob(final IExternalAccess exta, final String model, final IResourceIdentifier rid)
	{
		final Future<CronJob> ret = new Future<CronJob>();
		
		SServiceProvider.getService(exta.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DefaultResultListener<ILibraryService>()
		{
			public void resultAvailable(ILibraryService libser)
			{
				libser.getClassLoader(rid).addResultListener(
					new DefaultResultListener<ClassLoader>()
				{
					public void resultAvailable(ClassLoader cl)
					{
						try
						{
							// load the bpmn model
							BpmnModelLoader loader = new BpmnModelLoader();
							MBpmnModel amodel = loader.loadBpmnModel(model, null, cl, new Object[]{rid, exta.getComponentIdentifier().getRoot()});
						
							// Search timer start events in model 
							List startevents = amodel.getStartActivities(null, null);
							for(int i=0; startevents!=null && i<startevents.size(); i++)
							{
								MActivity mact = (MActivity)startevents.get(i);
								
								StringBuffer buf = new StringBuffer();
								if(MBpmnModel.EVENT_START_TIMER.equals(mact.getActivityType()))
								{
									Object val	= mact.getParsedPropertyValue("duration");
									if(buf.length()>0)
										buf.append("|");
									buf.append(val);
								}
								
								// if has found some timer start
								if(buf.length()>0)
								{
									// add implicit triggering event 
									CreationInfo ci = new CreationInfo(rid);
									CronJob cj = new CronJob(new TimePatternFilter(buf.toString()),
										new CreateCommand(null, model, ci, null)
									{
										public void execute(Tuple2<IInternalAccess, Long> args)
										{
											Map<String, Object> vs = getInfo().getArguments();
											if(vs==null)
											{
												vs = new HashMap<String, Object>();
												getInfo().setArguments(vs);
											}
											vs.put(MBpmnModel.TRIGGER, new Tuple2<String, Object>(MBpmnModel.EVENT_START_TIMER, args.getSecondEntity()));
											super.execute(args);
										}
									});
									ret.setResult(cj);
								}
								else
								{
									ret.setException(new RuntimeException("Bpmn needs no start monitoring."));
								}
							}
						}
						catch(Exception e)
						{
							ret.setException(e);
						}
					}
				});
			}
		});
		
		return ret;
	}
	

}
