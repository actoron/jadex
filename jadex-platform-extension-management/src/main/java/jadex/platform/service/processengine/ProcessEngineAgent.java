package jadex.platform.service.processengine;

import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.IInternalProcessEngineService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.SFuture;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSCreatedEvent;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSStatusEvent;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSTerminatedEvent;
import jadex.bridge.service.types.cron.CronJob;
import jadex.bridge.service.types.cron.ICronService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
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
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.platform.service.cron.CronAgent;
import jadex.platform.service.cron.TimePatternFilter;
import jadex.platform.service.cron.jobs.CronCreateCommand;
import jadex.rules.eca.EventType;
import jadex.rules.eca.ExpressionCondition;
import jadex.rules.eca.ICondition;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.Rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Agent that implements the bpmn monitoring starter interface.
 */
@Agent(autoprovide=true)
@Service
@RequiredServices(
{
	@RequiredService(name="libs", type=ILibraryService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="crons", type=ICronService.class, 
		binding=@Binding(create=true, creationinfo=@jadex.micro.annotation.CreationInfo(type="cronagent"))),
//	@RequiredService(name="rules", type=IRuleService.class, 
//		binding=@Binding(create=true, creationinfo=@jadex.micro.annotation.CreationInfo(type="ruleagent")))
})
@ComponentTypes(
{
	@ComponentType(name="cronagent", clazz=CronAgent.class)//,
//	@ComponentType(name="ruleagent", clazz=RuleAgent.class)
})
public class ProcessEngineAgent implements IProcessEngineService, IInternalProcessEngineService
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;

	/** The remove commands. */
	protected Map<Tuple2<String, IResourceIdentifier>, List<Runnable>> remcoms;
	
	/** The managed process instances. */
	protected Map<IComponentIdentifier, ProcessInfo> processes;
	
	/** The event mapper. */
	protected EventMapper eventmapper;
	
	//-------- methods --------
	
	/**
	 * 
	 */
	@AgentCreated
	public IFuture<Void> init()
	{
		this.remcoms = new HashMap<Tuple2<String,IResourceIdentifier>, List<Runnable>>();
		this.processes = new HashMap<IComponentIdentifier, ProcessEngineAgent.ProcessInfo>();
		this.eventmapper = new EventMapper();
		return IFuture.DONE;
	}
	
//	/**
//	 *  The agent body.
//	 */
//	@AgentBody
//	public void body()
//	{		
//		final long dur = 10000;
////		final String model = "jadex/bpmn/examples/execute/TimerEventStart.bpmn";
//		final String model = "jadex/bpmn/examples/execute/ConditionEventStart.bpmn";
//		
//		final IMonitoringStarterService sts = (IMonitoringStarterService)agent.getServiceContainer().getProvidedService(IMonitoringStarterService.class);
//		sts.addBpmnModel(model, null).addResultListener(new DefaultResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
//				System.out.println("monitoring "+dur/1000+"s "+model);
//				
//				IFuture<IRuleService> fut = agent.getServiceContainer().getRequiredService("rules");
//				fut.addResultListener(new DefaultResultListener<IRuleService>()
//				{
//					public void resultAvailable(final IRuleService rules)
//					{
//						rules.addEvent(new Event("new_file", "some event"));
//					}
//				});
//				
//				agent.waitFor(dur, new IComponentStep<Void>()
//				{
//					public IFuture<Void> execute(IInternalAccess ia)
//					{
//						sts.removeBpmnModel(model, null).addResultListener(new DefaultResultListener<Void>()
//						{
//							public void resultAvailable(Void result)
//							{
//								System.out.println("monitoring ended");
//							}
//						});
//						return IFuture.DONE;
//					}
//				});
//			}
//		});
//	}
	
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
		SServiceProvider.getService(agent.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
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
							MBpmnModel amodel = loader.loadBpmnModel(model, null, cl, new Object[]{rid, agent.getComponentIdentifier().getRoot()});
						
							// Search timer, rule start events in model 
							List<MActivity> startevents = new ArrayList<MActivity>();
							startevents.addAll(amodel.getTypeMatchedStartEvents());
							startevents.addAll(amodel.getEventSubProcessStartEvents());
							StringBuffer timing = new StringBuffer();
							final List<IRule<Collection<CMSStatusEvent>>> rules = new ArrayList<IRule<Collection<CMSStatusEvent>>>();
							
							for(int i=0; startevents!=null && i<startevents.size(); i++)
							{
								MActivity mact = (MActivity)startevents.get(i);
								
								if(MBpmnModel.EVENT_START_TIMER.equals(mact.getActivityType()))
								{
									Object val = mact.getParsedPropertyValue("duration");
									if(timing.length()>0)
										timing.append("|");
									timing.append(val);
								}
								else if(MBpmnModel.EVENT_START_RULE.equals(mact.getActivityType()))
								{
									// new variant with new models bpmn2
									if(mact.hasPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_EVENTTYPES))
									{
										String[] etypes = (String[])mact.getParsedPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_EVENTTYPES);
										if(etypes!=null && etypes.length>0)
										{
											Rule<Collection<CMSStatusEvent>> rule = new Rule<Collection<CMSStatusEvent>>(key.toString()+" "+i+" "+mact.getId());
											
											List<String> events = SUtil.arrayToList(etypes);
											if(mact.hasPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_CONDITION))
											{
												UnparsedExpression cond = (UnparsedExpression)mact.getPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_CONDITION);
												if(cond!=null)
												{
													rule.setCondition(new ExpressionCondition(cond, null)); // todo: fetcher?
												}
											}
											if(rule.getCondition()==null)
											{
												rule.setCondition(ICondition.TRUE_CONDITION);
											}
//											rule.setAction(new CommandAction<Collection<CMSStatusEvent>>(createRuleCreateCommand(rid, model)));//, dellis)));
											rule.setEventNames(events);
											rules.add(rule);
										}
									}
								}
							}
							
							// if has found some timer start
							CronJob<CMSStatusEvent> cj = null;
							if(timing.length()>0)
							{
								// add cron job automatically adds a remove command for the removal
								cj = new CronJob<CMSStatusEvent>(timing.toString(), new TimePatternFilter(timing.toString()),
									createCronCreateCommand(rid, model));//, dellis));
							}
							
							// add observed flag if no jobs
							if(cj==null && rules.size()==0)
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
									f2.addResultListener(new IntermediateDefaultResultListener<CMSStatusEvent>()
									{
										protected IComponentIdentifier cid;
										public void intermediateResultAvailable(CMSStatusEvent result) 
										{
											if(result instanceof CMSCreatedEvent)
											{
												cid = ((CMSCreatedEvent)result).getComponentIdentifier();
												// Without correlator when not started with rule event
												// todo: support a task that sets the evaluator manually
												processes.put(cid, new ProcessInfo(null, cid));
												ret.addIntermediateResultIfUndone(new ProcessEngineEvent(ProcessEngineEvent.INSTANCE_CREATED, cid, null));
											}
											else if(result instanceof CMSTerminatedEvent)
											{
												// send instance terminated event 
//												Map<String, Object> res = Argument.convertArguments(results);
//												ret.addIntermediateResultIfUndone(new MonitoringStarterEvent(MonitoringStarterEvent.INSTANCE_TERMINATED, 
//													(IComponentIdentifier)res.get(IComponentIdentifier.RESULTCID), res));
												ret.addIntermediateResultIfUndone(new ProcessEngineEvent(ProcessEngineEvent.INSTANCE_TERMINATED, 
													cid, ((CMSTerminatedEvent)result).getResults()));
												processes.remove(cid);
											}
										}
										
										public void finished() 
										{
											ret.setFinishedIfUndone();
										}
										
										public void exceptionOccurred(Exception exception) 
										{
											ret.setExceptionIfUndone(exception);
										}
									});
									
									if(rules!=null)
									{
										for(IRule<Collection<CMSStatusEvent>> rule: rules)
										{
											ExpressionCondition ec = (ExpressionCondition)rule.getCondition();
											final IParsedExpression exp = SJavaParser.parseExpression(ec.getExpression(), null, null); // todo: classloader?
											
											IFilter<Object> filter = new IFilter<Object>()
											{
												public boolean filter(Object obj)
												{
													SimpleValueFetcher fetcher = new SimpleValueFetcher();
													fetcher.setValue("$event", obj);
													Object ret = exp.getValue(fetcher);
													return ret instanceof Boolean? ((Boolean)ret).booleanValue(): false;
												}
											};
											List<EventType> ets = rule.getEvents();
											String[] events = new String[ets.size()];
											for(int i=0; i<ets.size(); i++)
											{
												events[i] = ets.get(i).getTypename();
											}
											eventmapper.addModelMapping(events, filter, model, rid);
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
									
//									// add rule listener if at least one rule
//									addRuleListener(rules, key, ret, corfac).addResultListener(new ExceptionDelegationResultListener<Void, Collection<ProcessEngineEvent>>(ret)
//									{
//										public void customResultAvailable(Void result)
//										{
//											addRuleJobs(rules.iterator(), key).addResultListener(new ExceptionDelegationResultListener<Void, Collection<ProcessEngineEvent>>(ret)
//											{
//												public void customResultAvailable(Void result)
//												{
//													// first event to state that monitoring is complete
//													ret.addIntermediateResult(new ProcessEngineEvent(ProcessEngineEvent.PROCESSMODEL_ADDED, null, null));
//												}
//											});
//										}
//									});
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
	
//	/**
//	 *  Add a rule listener on the engine that processes the received events.
//	 */
//	protected IFuture<Void> addRuleListener(List<IRule<Collection<CMSStatusEvent>>> rules, final Tuple2<String, IResourceIdentifier> key,
//		final SubscriptionIntermediateFuture<ProcessEngineEvent> res, final ICorrelationFilterFactory corfac)
//	{
//		final Future<Void> ret = new Future<Void>();
//		
//		if(rules.size()>0)
//		{
//			IFuture<IRuleService> fut = agent.getServiceContainer().getRequiredService("rules");
//			fut.addResultListener(new ExceptionDelegationResultListener<IRuleService, Void>(ret)
//			{
//				public void customResultAvailable(final IRuleService rules)
//				{
//					final ISubscriptionIntermediateFuture<RuleEvent> fut = rules.subscribeToEngine();
//					addRemoveCommand(new Runnable()
//					{
//						public void run()
//						{
//							fut.terminate();
//						}
//					}, key);
//					
//					fut.addResultListener(new IntermediateDefaultResultListener<RuleEvent>()
//					{
//						public void intermediateResultAvailable(RuleEvent re) 
//						{
//							// first event is subscribe() finished
//							if(re!=null)
//							{
//								// send event and create correlator for new instance
//								if(re.getResult() instanceof CMSCreatedEvent)
//								{
//									CMSCreatedEvent ev = (CMSCreatedEvent)re.getResult();
//									IEvent event = (IEvent)ev.getProperty("startevent");
//									IComponentIdentifier cid = ev.getComponentIdentifier();
//									processes.put(cid, new ProcessInfo(corfac==null? null: corfac.createCorrelationFilter(event), cid));
//								
//									// send instance created event
//									res.addIntermediateResultIfUndone(new ProcessEngineEvent(ProcessEngineEvent.INSTANCE_CREATED, (IComponentIdentifier)re.getResult(), null));
//								}
//								else if(re.getResult() instanceof CMSTerminatedEvent)
//								{
//									CMSTerminatedEvent ev = (CMSTerminatedEvent)re.getResult();
//									processes.remove(ev.getComponentIdentifier());
//								}
//							}
//						}
//						
//						public void exceptionOccurred(Exception exception)
//						{
//							res.setExceptionIfUndone(exception);
//						}
//					});
//					ret.setResult(null);
//				}
//			});
//		}
//		else
//		{
//			ret.setResult(null);
//		}
//		
//		return ret;
//	}
	
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
	public IFuture<Void> processEvent(final Object event, final String type)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(eventmapper.processInstanceEvent(event, type))
		{
			ret.setResult(null);
		}
		else
		{
			Tuple2<IResourceIdentifier, String> tup = eventmapper.processModelEvent(event, type);
			if(tup!=null)
			{
				final IResourceIdentifier rid = tup.getFirstEntity();
				final String model = tup.getSecondEntity();
				
				SServiceProvider.getService(agent.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(agent.createResultListener(new IResultListener<IComponentManagementService>()
				{
					public void resultAvailable(IComponentManagementService cms)
					{
						CreationInfo info = new CreationInfo(agent.getComponentIdentifier(), rid);
						Map<String, Object> vs = new HashMap<String, Object>();
//						vs.put(MBpmnModel.TRIGGER, new Tuple3<String, String, Object>(MBpmnModel.EVENT_START_RULE, args.getRule().getName(), event));
						vs.put(MBpmnModel.TRIGGER, new Tuple3<String, String, Object>(MBpmnModel.EVENT_START_RULE, null, event));

						ISubscriptionIntermediateFuture<CMSStatusEvent> fut = cms.createComponent(info, null, model);
						ret.setResult(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.setException(exception);
					}
				}));
			}
			else
			{
				ret.setResult(null);
			}
		}
		
		return ret;
	}
	
	//-------- IInternalProcessEngine interface --------
	
	/**
	 *  Register an event description to be notified, when the event happens.
	 *  @return An id to be used for deregistration.
	 */
	public IFuture<String>	addEventMatcher(String[] events, UnparsedExpression uexp, String[] imports, Map<String, Object> vals, ICommand<Object> cmd)
	{
		return new Future<String>(eventmapper.addInstanceMapping(uexp, events, vals, imports, cmd));
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
			IFuture<ICronService> fut = agent.getServiceContainer().getRequiredService("crons");
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
	
//	/**
//	 *  Add rule jobs to the engine.
//	 */
//	protected IFuture<Void> addRuleJobs(final Iterator<IRule<Collection<CMSStatusEvent>>> it, final Tuple2<String, IResourceIdentifier> key)
//	{
//		final Future<Void> ret = new Future<Void>();
//		
//		if(it.hasNext())
//		{
//			addRuleJob(it.next(), key).addResultListener(new DelegationResultListener<Void>(ret)
//			{
//				public void customResultAvailable(Void result)
//				{
//					addRuleJobs(it, key).addResultListener(new DelegationResultListener<Void>(ret));
//				}
//			});
//		}
//		else
//		{
//			ret.setResult(null);
//		}
//		
//		return ret;
//	}
	
//	/**
//	 *  Add a rule to the rule engine.
//	 */
//	protected IFuture<Void> addRuleJob(final IRule<?> rule, final Tuple2<String, IResourceIdentifier> key)
//	{
//		final Future<Void> ret = new Future<Void>();
//		IFuture<IRuleService> fut = agent.getServiceContainer().getRequiredService("rules");
//		final String rulename = rule.getName();
//		fut.addResultListener(new ExceptionDelegationResultListener<IRuleService, Void>(ret)
//		{
//			public void customResultAvailable(final IRuleService rules)
//			{
//				Runnable com = new Runnable()
//				{
//					public void run()
//					{
//						rules.removeRule(rulename).addResultListener(new DefaultResultListener<Void>()
//						{
//							public void resultAvailable(Void result)
//							{
////								System.out.println("removed rule: "+rule);
//							}
//						});
//					}
//				};
//				addRemoveCommand(com, key);
//				rules.addRule(rule).addResultListener(new DelegationResultListener<Void>(ret));
//			}
//		});
//		return ret;
//	}
	
	/**
	 *  Create a new cron create component command.
	 */
	protected static CronCreateCommand createCronCreateCommand(IResourceIdentifier rid, String model)//, IResultListener<Collection<Tuple2<String, Object>>> killis)
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
				vs.put(MBpmnModel.TRIGGER, new Tuple3<String, String, Object>(MBpmnModel.EVENT_START_TIMER, null, args.getSecondEntity()));
				return super.execute(args);
			}
		};
		
		return ret;
	}
	
//	/**
//	 *  Create a new rule create component command.
//	 */
//	protected static RuleCreateCommand createRuleCreateCommand(IResourceIdentifier rid, String model)//, IResultListener<Collection<Tuple2<String, Object>>> killis)
//	{
//		RuleCreateCommand ret = null;
//		
//		// add implicit triggering event 
//		CreationInfo ci = new CreationInfo(rid);
//		
//		ret = new RuleCreateCommand(null, model, ci)//, killis)
//		{
//			@Classname("RuleCreateCommand")
//			public IIntermediateFuture<CMSStatusEvent> execute(final CommandData args)
//			{
//				IntermediateFuture<CMSStatusEvent> ret = new IntermediateFuture<CMSStatusEvent>();
//				Map<String, Object> vs = getCommand().getInfo().getArguments();
//				if(vs==null)
//				{
//					vs = new HashMap<String, Object>();
//					getCommand().getInfo().setArguments(vs);
//				}
////				vs.put(MBpmnModel.TRIGGER, new Tuple3<String, String, Object>(MBpmnModel.EVENT_START_RULE, args.getRule().getName(), args.getEvent()));
//				vs.put(MBpmnModel.TRIGGER, new Tuple3<String, String, Object>(MBpmnModel.EVENT_START_RULE, args.getRule().getName(), args.getEvent().getContent()));
//				IIntermediateFuture<CMSStatusEvent> fut = super.execute(args);
//				fut.addResultListener(new IntermediateDelegationResultListener<CMSStatusEvent>(ret)
//				{
//					public void customIntermediateResultAvailable(CMSStatusEvent event)
//					{
//						System.out.println("event is: "+event);
//						if(event instanceof CMSCreatedEvent)
//						{
//							event.setProperty("startevent", args.getEvent());
//						}
//						super.customIntermediateResultAvailable(event);
//					}
//				});
//				return ret;
//			}
//		};
//		
//		return ret;
//	}

	/**
	 *  Process info struct.
	 */
	public static class ProcessInfo
	{
		/** The correlator (if any). */
		protected IFilter<IEvent> correlator;
		
		/** The process instance cid. */
		protected IComponentIdentifier cid;

		/**
		 *  Create a new ProcessInfo.
		 */
		public ProcessInfo(IFilter<IEvent> correlator, IComponentIdentifier cid)
		{
			this.correlator = correlator;
			this.cid = cid;
		}

		/**
		 *  Get the correlator.
		 *  return The correlator.
		 */
		public IFilter<IEvent> getCorrelator()
		{
			return correlator;
		}

		/**
		 *  Set the correlator. 
		 *  @param correlator The correlator to set.
		 */
		public void setCorrelator(IFilter<IEvent> correlator)
		{
			this.correlator = correlator;
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
	}
}
