package jadex.platform.service.bpmnstarter;

import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cron.CronJob;
import jadex.bridge.service.types.cron.ICronService;
import jadex.bridge.service.types.ecarules.IRuleService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
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
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.platform.service.cron.TimePatternFilter;
import jadex.platform.service.cron.jobs.CronCreateCommand;
import jadex.rules.eca.CommandAction;
import jadex.rules.eca.CommandAction.CommandData;
import jadex.rules.eca.ExpressionCondition;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.Rule;
import jadex.rules.eca.RuleEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Agent that implements the bpmn monitoring starter interface.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IMonitoringStarterService.class, implementation=@Implementation(expression="$pojoagent")))
@RequiredServices(
{
	@RequiredService(name="libs", type=ILibraryService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="crons", type=ICronService.class, 
		binding=@Binding(create=true, creationinfo=@jadex.micro.annotation.CreationInfo(type="cronagent"))),
	@RequiredService(name="rules", type=IRuleService.class, 
		binding=@Binding(create=true, creationinfo=@jadex.micro.annotation.CreationInfo(type="ruleagent")))
})
@ComponentTypes(
{
	@ComponentType(name="cronagent", filename="jadex/platform/service/cron/CronAgent.class"),
	@ComponentType(name="ruleagent", filename="jadex/platform/service/ecarules/RuleAgent.class")
})
public class MonitoringStarterAgent implements IMonitoringStarterService
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;

	/** The remove commands. */
	protected Map<Tuple2<String, IResourceIdentifier>, List<Runnable>> remcoms;
	
	//-------- methods --------
	
	/**
	 * 
	 */
	@AgentCreated
	public IFuture<Void> init()
	{
		this.remcoms = new HashMap<Tuple2<String,IResourceIdentifier>, List<Runnable>>();
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
	public ISubscriptionIntermediateFuture<MonitoringStarterEvent> addBpmnModel(final String model, final IResourceIdentifier urid)
	{
		final SubscriptionIntermediateFuture<MonitoringStarterEvent> ret = new SubscriptionIntermediateFuture<MonitoringStarterEvent>();
//		final Future<Void> ret = new Future<Void>();
			
		final Tuple2<String, IResourceIdentifier> key = new Tuple2<String, IResourceIdentifier>(model, urid);

		SServiceProvider.getService(agent.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DefaultResultListener<ILibraryService>()
		{
			public void resultAvailable(ILibraryService libs)
			{
				final IResourceIdentifier rid = urid!=null? urid: libs.getRootResourceIdentifier();
				libs.getClassLoader(rid).addResultListener(
					new DefaultResultListener<ClassLoader>()
				{
					public void resultAvailable(ClassLoader cl)
					{
						try
						{
							// load the bpmn model
							BpmnModelLoader loader = new BpmnModelLoader();
							MBpmnModel amodel = loader.loadBpmnModel(model, null, cl, new Object[]{rid, agent.getComponentIdentifier().getRoot()});
						
							// Search timer, rule start events in model 
							List<MActivity> startevents = amodel.getStartActivities(null, null);
							StringBuffer timing = new StringBuffer();
							final List<IRule<IComponentIdentifier>> rules = new ArrayList<IRule<IComponentIdentifier>>();
							
							final IIntermediateResultListener<IComponentIdentifier> createlis = new IntermediateDefaultResultListener<IComponentIdentifier>()
							{
								public void intermediateResultAvailable(IComponentIdentifier cid)
								{
									// send instance created event
									ret.addIntermediateResultIfUndone(new MonitoringStarterEvent(MonitoringStarterEvent.INSTANCE_CREATED, cid, null));
								}
							};
							
							final IResultListener<Collection<Tuple2<String, Object>>> dellis = new IResultListener<Collection<Tuple2<String, Object>>>()
							{
								public void resultAvailable(Collection<Tuple2<String,Object>> results) 
								{
									// send instance terminated event 
									Map<String, Object> res = Argument.convertArguments(results);
									ret.addIntermediateResultIfUndone(new MonitoringStarterEvent(MonitoringStarterEvent.INSTANCE_TERMINATED, 
										(IComponentIdentifier)res.get(IComponentIdentifier.RESULTCID), res));
								}
								
								public void exceptionOccurred(Exception exception)
								{
								}
							};
							
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
									String val = (String)mact.getParsedPropertyValue("notifier");
									if(val!=null)
									{
										Rule<IComponentIdentifier> rule = new Rule<IComponentIdentifier>(key.toString()+" "+i+" "+mact.getId());
										String type = val;
										String cond = null;
										int idx = val.indexOf(";");
										if(idx!=-1)
										{
											type = val.substring(0, idx);
											cond = val.substring(idx+1);
										}
										
										// todo multiple events
										List<String> events = new ArrayList<String>();
										events.add(type);
										rule.setEventNames(events);
										if(cond!=null)
										{
											UnparsedExpression up = new UnparsedExpression(null, Boolean.class, cond, null);
											rule.setCondition(new ExpressionCondition(up, null)); // todo: fetcher?
										}
										rule.setAction(new CommandAction<IComponentIdentifier>(createRuleCreateCommand(rid, model, dellis)));
										rules.add(rule);
									}
								}
							}
							
							// if has found some timer start
							CronJob<IComponentIdentifier> cj = null;
							if(timing.length()>0)
							{
								// kill listener for cron jobs
								cj = new CronJob<IComponentIdentifier>(timing.toString(), new TimePatternFilter(timing.toString()),
									createCronCreateCommand(rid, model, dellis));
							}
							
							// add observed flag if no jobs
							if(cj==null && rules.size()==0)
							{
								remcoms.put(key, null);
							}
							
							addCronJob(cj, key, createlis).addResultListener(new ExceptionDelegationResultListener<Void, Collection<MonitoringStarterEvent>>(ret)
							{
								public void customResultAvailable(Void result)
								{
									// add rule listener if at least one rule
									addRuleListener(rules, key, ret).addResultListener(new ExceptionDelegationResultListener<Void, Collection<MonitoringStarterEvent>>(ret)
									{
										public void customResultAvailable(Void result)
										{
											addRuleJobs(rules.iterator(), key).addResultListener(new ExceptionDelegationResultListener<Void, Collection<MonitoringStarterEvent>>(ret)
											{
												public void customResultAvailable(Void result)
												{
													// first event to state that monitoring is complete
													ret.addIntermediateResult(new MonitoringStarterEvent(MonitoringStarterEvent.ADDED, null, null));
												}
											});
										}
									});
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
	protected IFuture<Void> addRuleListener(List<IRule<IComponentIdentifier>> rules, final Tuple2<String, IResourceIdentifier> key,
		final SubscriptionIntermediateFuture<MonitoringStarterEvent> res)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(rules.size()>0)
		{
			IFuture<IRuleService> fut = agent.getServiceContainer().getRequiredService("rules");
			fut.addResultListener(new ExceptionDelegationResultListener<IRuleService, Void>(ret)
			{
				public void customResultAvailable(final IRuleService rules)
				{
					final ISubscriptionIntermediateFuture<RuleEvent> fut = rules.subscribeToEngine();
					addRemoveCommand(new Runnable()
					{
						public void run()
						{
							fut.terminate();
						}
					}, key);
					fut.addResultListener(new IntermediateDefaultResultListener<RuleEvent>()
					{
						public void intermediateResultAvailable(RuleEvent re) 
						{
							// first event is subscribe() finished
							if(re!=null)
							{
								// send instance created event
								res.addIntermediateResultIfUndone(new MonitoringStarterEvent(MonitoringStarterEvent.INSTANCE_CREATED, (IComponentIdentifier)re.getResult(), null));
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
							res.setExceptionIfUndone(exception);
						}
					});
					ret.setResult(null);
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
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
	 *  Add a cron job to the cron service.
	 */
	protected IFuture<Void> addCronJob(final CronJob<IComponentIdentifier> cj, final Tuple2<String, IResourceIdentifier> key,
		final IIntermediateResultListener<IComponentIdentifier> lis)
	{
		if(cj==null)
			return IFuture.DONE;
			
		final Future<Void> ret = new Future<Void>();
		IFuture<ICronService> fut = agent.getServiceContainer().getRequiredService("crons");
		fut.addResultListener(new ExceptionDelegationResultListener<ICronService, Void>(ret)
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
				crons.addJob(cj).addResultListener(lis);
			}
		});
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
	 * 
	 */
	protected IFuture<Void> addRuleJobs(final Iterator<IRule<IComponentIdentifier>> it, final Tuple2<String, IResourceIdentifier> key)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(it.hasNext())
		{
			addRuleJob(it.next(), key).addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					addRuleJobs(it, key).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Add a rule to the rule engine.
	 */
	protected IFuture<Void> addRuleJob(final IRule<?> rule, final Tuple2<String, IResourceIdentifier> key)
	{
		final Future<Void> ret = new Future<Void>();
		IFuture<IRuleService> fut = agent.getServiceContainer().getRequiredService("rules");
		final String rulename = rule.getName();
		fut.addResultListener(new ExceptionDelegationResultListener<IRuleService, Void>(ret)
		{
			public void customResultAvailable(final IRuleService rules)
			{
				Runnable com = new Runnable()
				{
					public void run()
					{
						rules.removeRule(rulename).addResultListener(new DefaultResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
//								System.out.println("removed rule: "+rule);
							}
						});
					}
				};
				addRemoveCommand(com, key);
				rules.addRule(rule).addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		return ret;
	}
	
	/**
	 *  Create a new cron create component command.
	 */
	protected static CronCreateCommand createCronCreateCommand(IResourceIdentifier rid, String model, IResultListener<Collection<Tuple2<String, Object>>> killis)
	{
		CronCreateCommand ret = null;
		
		// add implicit triggering event 
		CreationInfo ci = new CreationInfo(rid);
		ret = new CronCreateCommand(null, model, ci, killis)
		{
			@Classname("CronCreateCommand")
			public IFuture<IComponentIdentifier> execute(Tuple2<IInternalAccess, Long> args)
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
	
	/**
	 *  Create a new rule create component command.
	 */
	protected static RuleCreateCommand createRuleCreateCommand(IResourceIdentifier rid, String model, IResultListener<Collection<Tuple2<String, Object>>> killis)
	{
		RuleCreateCommand ret = null;
		
		// add implicit triggering event 
		CreationInfo ci = new CreationInfo(rid);
		
		ret = new RuleCreateCommand(null, model, ci, killis)
		{
			@Classname("RuleCreateCommand")
			public IFuture<IComponentIdentifier> execute(CommandData args)
			{
				Map<String, Object> vs = getCommand().getInfo().getArguments();
				if(vs==null)
				{
					vs = new HashMap<String, Object>();
					getCommand().getInfo().setArguments(vs);
				}
				vs.put(MBpmnModel.TRIGGER, new Tuple3<String, String, Object>(MBpmnModel.EVENT_START_RULE, args.getRule().getName(), args.getEvent()));
				return super.execute(args);
			}
		};
		
		return ret;
	}

}
