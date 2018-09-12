package jadex.platform.service.processengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.base.Starter;
import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.IInternalProcessEngineService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.SFuture;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.CMSStatusEvent.CMSCreatedEvent;
import jadex.bridge.service.types.cms.CMSStatusEvent.CMSIntermediateResultEvent;
import jadex.bridge.service.types.cms.CMSStatusEvent.CMSTerminatedEvent;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cron.CronJob;
import jadex.bridge.service.types.cron.ICronService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.Boolean3;
import jadex.commons.IFilter;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.collection.IdentityHashSet;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;
import jadex.commons.transformation.annotations.Classname;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Autostart;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.platform.service.cron.CronAgent;
import jadex.platform.service.cron.TimePatternFilter;
import jadex.platform.service.cron.jobs.CronCreateCommand;
import jadex.platform.service.processengine.EventMapper.ModelDetails;

/**
 *  Agent that implements the bpmn monitoring starter interface.
 */
@Agent(autoprovide=Boolean3.TRUE, autostart=@Autostart(value=Boolean3.FALSE))
@Service
@RequiredServices(
{
	@RequiredService(name="libs", type=ILibraryService.class),
	@RequiredService(name="crons", type=ICronService.class),
})
@ComponentTypes(
{
	@ComponentType(name="cronagent", clazz=CronAgent.class)
})
@Configurations(@Configuration(name="default", components=@Component(type="cronagent")))

public class ProcessEngineAgent implements IProcessEngineService, IInternalProcessEngineService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;

	/** The remove commands. */
	protected Map<Tuple2<String, IResourceIdentifier>, List<Runnable>> remcoms;
	
	/** The managed process instances. */
	protected Map<IComponentIdentifier, ProcessInfo> processes;
	
	/** The event mapper. */
	protected EventMapper eventmapper;
	
	/** The event waitqueue. */
	protected Map<String, Set<Object>> waitqueue;
	
	/** The event types to be put in waitqueue. */
	protected Map<String, Set<Tuple2<String, IResourceIdentifier>>>	waitqueuetypes;
	
	/** The set of currently creating process instances. */ 
	protected Set<Future<Void>> creating;
	
	//-------- methods --------
	
	/**
	 *  Init method.
	 */
	@AgentCreated
	public IFuture<Void> init()
	{
		this.remcoms = new HashMap<Tuple2<String,IResourceIdentifier>, List<Runnable>>();
		this.processes = new HashMap<IComponentIdentifier, ProcessEngineAgent.ProcessInfo>();
		this.waitqueue	= new HashMap<String, Set<Object>>();
		this.waitqueuetypes	= new HashMap<String, Set<Tuple2<String,IResourceIdentifier>>>();
		this.eventmapper = new EventMapper(agent);
		this.creating = new HashSet<Future<Void>>();
		return IFuture.DONE;
	}
	
	/**
	 *  Add a bpmn model that is monitored for start events.
	 *  @param model The bpmn model
	 *  @param rid The resource identifier (null for all platform jar resources).
	 */
	public ISubscriptionIntermediateFuture<ProcessEngineEvent> addBpmnModel(final String model, final IResourceIdentifier urid)//, final ICorrelationFilterFactory corfac)
	{
//		final SubscriptionIntermediateFuture<MonitoringStarterEvent> ret = new SubscriptionIntermediateFuture<MonitoringStarterEvent>();
		final SubscriptionIntermediateFuture<ProcessEngineEvent> ret = (SubscriptionIntermediateFuture<ProcessEngineEvent>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
			
		final Tuple2<String, IResourceIdentifier> key = new Tuple2<String, IResourceIdentifier>(model, urid);

		// find classloader for rid
		agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new DefaultResultListener<ILibraryService>()
		{
			public void resultAvailable(ILibraryService libs)
			{
				final IResourceIdentifier rid = urid!=null? urid: libs.getRootResourceIdentifier();
				libs.getClassLoader(rid).addResultListener(new DefaultResultListener<ClassLoader>()
				{
					public void resultAvailable(ClassLoader cl)
					{
						try
						{
							// load the bpmn model
							BpmnModelLoader loader = new BpmnModelLoader();
							final MBpmnModel amodel = loader.loadBpmnModel(model, null, cl, new Object[]{rid, agent.getId().getRoot()});
							
							// Find all instance wait activities
							// register waitqueue events
							List<MActivity> wevsa = amodel.getWaitingEvents();
							List<MActivity> epvsa = amodel.getEventSubProcessStartEvents();
							final Set<String> wevs = new HashSet<String>();
							final Set<String> epvs = new HashSet<String>();
							
							for(MActivity mevent: wevsa)
							{
								extractDomainEventTypes(mevent, key, wevs);
							}
							
							for(MActivity mevent: epvsa)
							{
								extractDomainEventTypes(mevent, key, epvs);
							}
							
							addRemoveCommand(new Runnable()
							{
								public void run()
								{
//									Set<String> all = new HashSet<String>();
//									all.addAll(wevs);
//									all.addAll(epvs);
									
									for(String type: wevs)
									{
										Set<Tuple2<String, IResourceIdentifier>>	set	= waitqueuetypes.get(type);
										if(set!=null)
										{
											set.remove(key);
											if(set.isEmpty())
											{
												waitqueuetypes.remove(type);
											}
										}
									}
									
//									for(MActivity mevent: amodel.getWaitingEvents())
//									{
//										if(mevent.getPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_EVENTTYPES)!=null)
//										{
//											String[]	types	= (String[])mevent.getParsedPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_EVENTTYPES);
//											for(String type: types)
//											{
//												Set<Tuple2<String, IResourceIdentifier>>	set	= waitqueuetypes.get(type);
//												if(set!=null)
//												{
//													set.remove(key);
//													if(set.isEmpty())
//													{
//														waitqueuetypes.remove(type);
//													}
//												}
//											}
//										}
//									}
								}
							}, key);
						
							// Search timer, rule start events in model 
							List<MActivity> startevents = new ArrayList<MActivity>();
							startevents.addAll(amodel.getTypeMatchedStartEvents());
							startevents.addAll(amodel.getEventSubProcessStartEvents());
							List<Tuple2<String, String>> timers = new ArrayList<Tuple2<String,String>>();
							final List<EventInfo> infos = new ArrayList<EventInfo>();
														
							for(int i=0; startevents!=null && i<startevents.size(); i++)
							{
								MActivity mact = (MActivity)startevents.get(i);
								
								if(MBpmnModel.EVENT_START_TIMER.equals(mact.getActivityType()))
								{
									Object val = mact.getParsedPropertyValue("duration");
									timers.add(new Tuple2<String, String>(""+val, mact.getId()));
								}
								else if(MBpmnModel.EVENT_START_RULE.equals(mact.getActivityType()))
								{
									// new variant with new models bpmn2
									if(mact.hasPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_EVENTTYPES))
									{
										String[] etypes = (String[])mact.getParsedPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_EVENTTYPES);
										if(etypes!=null && etypes.length>0)
										{
											EventInfo info = new EventInfo();
											List<String> events = SUtil.arrayToList(etypes);
											if(mact.hasPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_CONDITION))
											{
												UnparsedExpression cond = (UnparsedExpression)mact.getPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_CONDITION);
												if(cond!=null)
												{
													info.setCondition(cond);
												}
											}
											info.setEventNames(events);
											info.setActivityId(mact.getId());
											infos.add(info);
										}
									}
								}
							}
							
							// if has found some timer start
							CronJob<CMSStatusEvent> cj = null;
							if(!timers.isEmpty())
							{
								for(Tuple2<String, String> tup: timers)
								{
									// add cron job automatically adds a remove command for the removal
									cj = new CronJob<CMSStatusEvent>(tup.getFirstEntity(), new TimePatternFilter(tup.getFirstEntity()),
										createCronCreateCommand(rid, model, tup.getSecondEntity()));//, dellis));
								}
							}
							
							// add observed flag if no jobs
							if(cj==null && infos.isEmpty())
							{
								remcoms.put(key, null);
							}
							
							// cron job process instance management
							final Tuple2<IFuture<Void>, ISubscriptionIntermediateFuture<CMSStatusEvent>> f = addCronJob(cj, key);//, createlis);
							IFuture<Void> f1 = f.getFirstEntity();
							f1.addResultListener(new ExceptionDelegationResultListener<Void, Collection<ProcessEngineEvent>>(ret)
							{
								public void customResultAvailable(Void result)
								{
									ISubscriptionIntermediateFuture<CMSStatusEvent> f2 = f.getSecondEntity();
									f2.addResultListener(new ConversionListener(key, ret));
									
									if(infos!=null)
									{
										for(EventInfo info: infos)
										{
											IFilter<Object> filter = null;
											if(info.getCondition()!=null)
											{
												final IParsedExpression exp = SJavaParser.parseExpression(info.getCondition(), null, null); // todo: classloader?
												
												filter = new IFilter<Object>()
												{
													public boolean filter(Object obj)
													{
														SimpleValueFetcher fetcher = new SimpleValueFetcher();
														fetcher.setValue("$event", obj);
														Object ret = exp.getValue(fetcher);
														return ret instanceof Boolean? ((Boolean)ret).booleanValue(): false;
													}
												};
											}
											
											eventmapper.addModelMapping(info.getEventNames().toArray(new String[0]), filter, model, rid, 
												info.getActivityId(), ret, wevs, epvs);
										}
										addRemoveCommand(new Runnable()
										{
											public void run()
											{
												eventmapper.removeModelMappings(model);
											}
										}, key);
										ret.addIntermediateResult(new ProcessEngineEvent(ProcessEngineEvent.PROCESSMODEL_ADDED, null, null));
									}
								}
							});
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
	
	/**
	 * 
	 */
	protected void extractDomainEventTypes(MActivity mevent, Tuple2<String, IResourceIdentifier> model, Set<String> evs)
	{
		if(mevent.getPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_EVENTTYPES)!=null)
		{
			String[] etypes = (String[])mevent.getParsedPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_EVENTTYPES);
			if(etypes!=null && etypes.length>0)
			{
				for(String etype: etypes)
				{
					evs.add(etype);
					
					Set<Tuple2<String, IResourceIdentifier>>	set	= waitqueuetypes.get(etype);
					if(set==null)
					{
						set	= new HashSet<Tuple2<String,IResourceIdentifier>>();
						waitqueuetypes.put(etype, set);
					}
					set.add(model);
				}
			}
		}
	}
	
	/**
	 *  Remove a bpmn model.
	 *  @param model The bpmn model
	 *  @param rid The resource identifier (null for all platform jar resources).
	 */
	public IFuture<Void> removeBpmnModel(final String model, final IResourceIdentifier rid)
	{
		final Future<Void> ret = new Future<Void>();

		final Tuple2<String, IResourceIdentifier> key = new Tuple2<String, IResourceIdentifier>(model, rid);
	
		if(!remcoms.containsKey(key))
		{
			ret.setException(new RuntimeException("Not monitored: "+model+" "+rid));
		}
		else
		{
			List<Runnable> coms = remcoms.remove(key);
			if(coms!=null)
			{
				for(Runnable run: coms)
				{
					run.run();
				}
			}
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Get the currently monitored processes.
	 *  @return The currently observed bpmn models.
	 */
	public IIntermediateFuture<Tuple2<String, IResourceIdentifier>> getBpmnModels()
	{
		return new IntermediateFuture<Tuple2<String,IResourceIdentifier>>(
			new ArrayList<Tuple2<String,IResourceIdentifier>>(remcoms.keySet()));
	}
	
	/**
	 *  Process an event and get the consequence events.
	 */
	public IFuture<Void> processEvent(final Object event, String type)
	{
		return internalProcessEvent(event, type, true);
	}
	
	/**
	 *  Process an event and get the consequence events.
	 *  Called internally when event should not be added to waitqueue.
	 */
	protected IFuture<Void> internalProcessEvent(final Object event, final String atype, final boolean add)
	{
		final Future<Void> ret = new Future<Void>();
		
		// Auto-determine event type if is null
		final String type = EventMapper.getEventType(event, atype);
		
		// First check if an instance match occurred
		eventmapper.processInstanceEvent(event, type)
			.addResultListener(new ExceptionDelegationResultListener<Boolean, Void>(ret)
		{
			public void customResultAvailable(Boolean result)
			{
				if(result.booleanValue())
				{
					ret.setResult(null);
				}
				else
				{
					final IResultListener<Void> lis = new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
							// If not check if a model has a corresponding start event
							final ModelDetails det = eventmapper.processModelEvent(event, type);
							if(det!=null)
							{
								createProcessInstance(event, det).addResultListener(new DelegationResultListener<Void>(ret));
							}
							else if(waitqueuetypes.containsKey(type))
							{
								dispatchToWaitqueue(event, type);
								ret.setResult(null);
							}
							else
							{
								System.out.println("No process to handle event: "+type+", "+event);
								ret.setException(new RuntimeException("No process to handle event: "+type+", "+event));
							}					
						}
						
						public void exceptionOccurred(Exception exception)
						{
						}
					};
					
					// If no instance match was found, check if instances are currently created
					if(!creating.isEmpty() && eventmapper.isEventInstanceWaitRelevant(type))
					{
//						System.out.println("defer: "+event);
						defer().addResultListener(new DelegationResultListener<Void>(ret)
						{
							public void customResultAvailable(Void result)
							{
//								System.out.println("after defer: "+event);
								// Check again, if an instance match occurred
								eventmapper.processInstanceEvent(event, type)
									.addResultListener(new ExceptionDelegationResultListener<Boolean, Void>(ret)
								{
									public void customResultAvailable(Boolean result)
									{
										if(result.booleanValue())
										{
											ret.setResult(null);
										}
										else
										{
											lis.resultAvailable(null);
										}
									}
								});
							}
						});
					}
					else
					{
						lis.resultAvailable(null);
					}
				}
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> createProcessInstance(final Object event, final ModelDetails det)
	{
//		System.out.println("create instance for: "+event);
		final Future<Void> ret = new Future<Void>();

		creating.add(ret);
		
		Tuple2<String, IResourceIdentifier> model = new Tuple2<String, IResourceIdentifier>(det.getModel(), det.getRid());
		
		CreationInfo info = new CreationInfo(agent.getId(), det.getRid());
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(MBpmnModel.TRIGGER, new Tuple3<String, String, Object>(MBpmnModel.EVENT_START_RULE, det.getEventId(), event));
		info.setArguments(args);
		info.setFilename(det.getModel());
		
		ISubscriptionIntermediateFuture<CMSStatusEvent> fut = agent.createComponentWithResults(info);
		fut.addResultListener(new ConversionListener(new Tuple2<String, IResourceIdentifier>(det.getModel(), det.getRid()), det.getFuture())); // Add converion listener for addmodel() future 
		
		fut.addResultListener(new IIntermediateResultListener<CMSStatusEvent>()
		{
			public void intermediateResultAvailable(CMSStatusEvent result)
			{
				if(result instanceof CMSCreatedEvent)
				{
//							System.out.println("created: "+result);
					cont();
				}
			}
			
			public void resultAvailable(Collection<CMSStatusEvent> result)
			{
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				cont();
			}
			
			public void finished()
			{
			}
			
			protected void cont()
			{
				if(creating.remove(ret))
				{
					ret.setResult(null);
//							System.out.println("creating fini");
				}
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected void dispatchToWaitqueue(final Object event, String type)
	{
//		System.out.println("dispatch to waitqueue: "+event+" "+type+" "+waitqueue);
		Set<Object>	wq	= waitqueue.get(type);
		if(wq==null)
		{
			wq	= new IdentityHashSet<Object>();
			waitqueue.put(type, wq);
		}
		wq.add(event);
		
		// Todo: use event time-to-live from registered models?
		final Set<Object>	fwq	= wq;
		final String	ftype	= type;
		
		long to = Starter.getDefaultTimeout(agent.getId());
		if(to>0)
		{
			agent.getFeature(IExecutionFeature.class).waitForDelay(to, new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					fwq.remove(event);
					if(fwq.isEmpty())
					{
						waitqueue.remove(ftype);
					}
					return IFuture.DONE;
				}
			});
		}
	}
	
	/**
	 *  Dispatch events from the waitqueue.
	 *  @param type The event type. Events of that type will be redispatched. 
	 */
	protected void dispatchFromWaitqueue(final String type, boolean add)
	{
		if(waitqueue.containsKey(type))
		{
			for(final Object event: waitqueue.get(type).toArray())
			{
				internalProcessEvent(event, type, add).addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						// Handled -> remove from waitqueue.
						Set<Object>	wq	= waitqueue.get(type);
						if(wq!=null)
						{
							wq.remove(event);
							if(wq.isEmpty())
							{
								waitqueue.remove(wq);
							}
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						// Not handled -> ignore
					}
				});
			}
		}
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> defer()
	{
//		System.out.println("Defer event processing");
		final Future<Void> ret = new Future<Void>();
		
		CounterResultListener<Void> lis = new CounterResultListener<Void>(creating.size(), new DelegationResultListener<Void>(ret));
		
		for(Future<Void> fut: creating)
		{
			fut.addResultListener(lis);
		}
		
		return ret;
	}
	
	//-------- IInternalProcessEngine interface --------
	
	/**
	 *  Register an event description to be notified, when the event happens.
	 *  @return An id to be used for deregistration.
	 */
	public IFuture<String>	addEventMatcher(String[] events, UnparsedExpression uexp, String[] imports, Map<String, Object> vals, boolean remove, IResultCommand<IFuture<Void>, Object> cmd)
	{
		String	id	= eventmapper.addInstanceMapping(uexp, events, vals, imports, remove, cmd);
		
		for(final String type: events)
		{
			dispatchFromWaitqueue(type, false);
		}
		
		return new Future<String>(id);
	}
	
	/**
	 *  Register an event description to be notified, when the event happens.
	 */
	public IFuture<Void>	removeEventMatcher(String id)
	{	
		eventmapper.removeInstanceMappings(id);
		return IFuture.DONE;
	}

	//-------- --------
	
	/**
	 *  Add a cron job to the cron service.
	 *  
	 *  Also adds a remove command that is triggered when someone calls removeBPMN.
	 */
	protected Tuple2<IFuture<Void>, ISubscriptionIntermediateFuture<CMSStatusEvent>> addCronJob(final CronJob<CMSStatusEvent> cj, final Tuple2<String, IResourceIdentifier> key)//
//		final IIntermediateResultListener<IComponentIdentifier> lis)
	{
		final Future<Void> ret1 = new Future<Void>();
		final SubscriptionIntermediateDelegationFuture<CMSStatusEvent> ret2 = new SubscriptionIntermediateDelegationFuture<CMSStatusEvent>();
		final Tuple2<IFuture<Void>, ISubscriptionIntermediateFuture<CMSStatusEvent>> ret = new Tuple2<IFuture<Void>, ISubscriptionIntermediateFuture<CMSStatusEvent>>(ret1, ret2);
				
		if(cj==null)
		{
			ret1.setResult(null);
		}
		else
		{
			IFuture<ICronService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("crons");
			fut.addResultListener(new ExceptionDelegationResultListener<ICronService, Collection<CMSStatusEvent>>(ret2)
			{
				public void customResultAvailable(final ICronService crons)
				{
					Runnable com = new Runnable()
					{
						public void run()
						{
							crons.removeJob(cj.getId()).addResultListener(new DefaultResultListener<Void>()
							{
								public void resultAvailable(Void result)
								{
								}
							});
						}
					};
					addRemoveCommand(com, key);
					// Cron servive provides subscription future which notifies results of cron job executions
					// listener that notifies when new instances are created
	//				crons.addJob(cj).addResultListener();
					ISubscriptionIntermediateFuture<CMSStatusEvent> fut = crons.addJob(cj);
					TerminableIntermediateDelegationResultListener<CMSStatusEvent> lis = new TerminableIntermediateDelegationResultListener<CMSStatusEvent>(ret2, fut);
					fut.addResultListener(lis);
					ret1.setResult(null);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected void addRemoveCommand(Runnable com, Tuple2<String, IResourceIdentifier> key)
	{
		List<Runnable> coms = remcoms.get(key);
		if(coms==null)
		{
			coms = new ArrayList<Runnable>();
			remcoms.put(key, coms);
		}
		coms.add(com);
	}
	
	/**
	 *  Create a new cron create component command.
	 */
	protected static CronCreateCommand createCronCreateCommand(IResourceIdentifier rid, String model, final String mactid)//, IResultListener<Collection<Tuple2<String, Object>>> killis)
	{
		CronCreateCommand ret = null;
		
		// add implicit triggering event 
		CreationInfo ci = new CreationInfo(rid);
		ret = new CronCreateCommand(null, model, ci)//, killis)
		{
			@Classname("CronCreateCommand")
			public ISubscriptionIntermediateFuture<CMSStatusEvent> execute(Tuple2<IInternalAccess, Long> args)
			{
				Map<String, Object> vs = getCommand().getInfo().getArguments();
				if(vs==null)
				{
					vs = new HashMap<String, Object>();
					getCommand().getInfo().setArguments(vs);
				}
				// One could put in the activity name but how to determine then if multiple timers match?
				vs.put(MBpmnModel.TRIGGER, new Tuple3<String, String, Object>(MBpmnModel.EVENT_START_TIMER, mactid, args.getSecondEntity()));
				return super.execute(args);
			}
		};
		
		return ret;
	}
	
	/**
	 *  Process info struct.
	 */
	public static class ProcessInfo
	{
		protected Tuple2<String, IResourceIdentifier> model;
		
		/** The process instance cid. */
		protected IComponentIdentifier cid;

		/**
		 *  Create a new ProcessInfo.
		 */
		public ProcessInfo(Tuple2<String, IResourceIdentifier> model, IComponentIdentifier cid)
		{
			this.model = model;
			this.cid = cid;
		}

		/**
		 *  Get the cid.
		 *  return The cid.
		 */
		public IComponentIdentifier getCid()
		{
			return cid;
		}

		/**
		 *  Set the cid. 
		 *  @param cid The cid to set.
		 */
		public void setCid(IComponentIdentifier cid)
		{
			this.cid = cid;
		}

		/**
		 *  Get the model.
		 *  return The model.
		 */
		public Tuple2<String, IResourceIdentifier> getModel()
		{
			return model;
		}

		/**
		 *  Set the model. 
		 *  @param model The model to set.
		 */
		public void setModel(Tuple2<String, IResourceIdentifier> model)
		{
			this.model = model;
		}
	}
	
	/**
	 *  Listener that converts cms events to engine events.
	 */
	class ConversionListener extends IntermediateDefaultResultListener<CMSStatusEvent>
	{
		/** The delegate future. */
		protected SubscriptionIntermediateFuture<ProcessEngineEvent> delegate;
		
		/** The component identifier. */
		protected IComponentIdentifier cid;
		
		/** The model. */
		protected Tuple2<String, IResourceIdentifier> model;
		
		/**
		 *  Create a new ConversionListener.
		 */
		public ConversionListener(Tuple2<String, IResourceIdentifier> model, SubscriptionIntermediateFuture<ProcessEngineEvent> delegate)
		{
			this.model = model;
			this.delegate = delegate;
		}
		
		public void intermediateResultAvailable(CMSStatusEvent result) 
		{
			if(result instanceof CMSCreatedEvent)
			{
				cid = ((CMSCreatedEvent)result).getComponentIdentifier();
				processes.put(cid, new ProcessInfo(model, cid));
				delegate.addIntermediateResultIfUndone(new ProcessEngineEvent(ProcessEngineEvent.INSTANCE_CREATED, cid, null));
			}
			else if(result instanceof CMSTerminatedEvent)
			{
				// send instance terminated event 
				delegate.addIntermediateResultIfUndone(new ProcessEngineEvent(ProcessEngineEvent.INSTANCE_TERMINATED, 
					cid, ((CMSTerminatedEvent)result).getResults()));
				processes.remove(cid);
			}
			else if(result instanceof CMSIntermediateResultEvent)
			{
				CMSIntermediateResultEvent ev = (CMSIntermediateResultEvent)result;
				delegate.addIntermediateResultIfUndone(new ProcessEngineEvent(ProcessEngineEvent.INSTANCE_RESULT_RECEIVED, 
					cid, new Tuple2<String, Object>(ev.getName(), ev.getValue())));
			}
		}
		
		public void resultAvailable(Collection<CMSStatusEvent> result)
		{
			for(CMSStatusEvent ev: result)
			{
				intermediateResultAvailable(ev);
			}
			delegate.setFinishedIfUndone();
		}
		
		public void finished() 
		{
			// Do not finish the future of the addMonitoring() only when one instance is terminated
//			delegate.setFinishedIfUndone();
		}
		
		public void exceptionOccurred(Exception exception) 
		{
			delegate.setExceptionIfUndone(exception);
		}
	}
	
	/**
	 * 
	 */
	protected static class EventInfo
	{
		/** The event names. */
		protected List<String> eventNames;
		
		/** The activity id. */
		protected String activityid;
		
		/** The condition expression. */
		protected UnparsedExpression condition;

		/**
		 *  Get the eventNames.
		 *  return The eventNames.
		 */
		public List<String> getEventNames()
		{
			return eventNames;
		}

		/**
		 *  Set the eventNames. 
		 *  @param eventNames The eventNames to set.
		 */
		public void setEventNames(List<String> eventNames)
		{
			this.eventNames = eventNames;
		}

		/**
		 *  Get the activityId.
		 *  return The activityId.
		 */
		public String getActivityId()
		{
			return activityid;
		}

		/**
		 *  Set the activityId. 
		 *  @param activityId The activityId to set.
		 */
		public void setActivityId(String activityId)
		{
			this.activityid = activityId;
		}

		/**
		 *  Get the condition.
		 *  return The condition.
		 */
		public UnparsedExpression getCondition()
		{
			return condition;
		}

		/**
		 *  Set the condition. 
		 *  @param condition The condition to set.
		 */
		public void setCondition(UnparsedExpression condition)
		{
			this.condition = condition;
		}
	}
}
