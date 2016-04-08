package jadex.bdiv3.features.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.bdiv3.IBDIClassGenerator;
import jadex.bdiv3.annotation.PlanContextCondition;
import jadex.bdiv3.annotation.RawEvent;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.features.impl.BDIAgentFeature.GoalsExistCondition;
import jadex.bdiv3.features.impl.BDIAgentFeature.LifecycleStateCondition;
import jadex.bdiv3.features.impl.BDIAgentFeature.NotInShutdownCondition;
import jadex.bdiv3.features.impl.BDIAgentFeature.PlansExistCondition;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.IBDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MCondition;
import jadex.bdiv3.model.MConfigBeliefElement;
import jadex.bdiv3.model.MConfigParameterElement;
import jadex.bdiv3.model.MConfiguration;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MInternalEvent;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.model.MParameter.EvaluationMode;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MTrigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.EasyDeliberationStrategy;
import jadex.bdiv3.runtime.IDeliberationStrategy;
import jadex.bdiv3.runtime.impl.APL;
import jadex.bdiv3.runtime.impl.APL.MPlanInfo;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3.runtime.impl.RCapability;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bdiv3.runtime.impl.RParameterElement.RParameter;
import jadex.bdiv3.runtime.impl.RParameterElement.RParameterSet;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.bdiv3x.runtime.CapabilityWrapper;
import jadex.bdiv3x.runtime.IInternalEvent;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.RInternalEvent;
import jadex.bdiv3x.runtime.RMessageEvent;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.ILifecycleComponentFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.commons.ICommand;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;
import jadex.micro.features.impl.MicroLifecycleComponentFeature;
import jadex.rules.eca.ChangeInfo;
import jadex.rules.eca.EventType;
import jadex.rules.eca.IAction;
import jadex.rules.eca.ICondition;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.MethodCondition;
import jadex.rules.eca.Rule;
import jadex.rules.eca.RuleSystem;
import jadex.rules.eca.annotations.CombinedCondition;

/**
 *  Feature that ensures the agent created(), body() and killed() are called on the pojo. 
 */
public class BDILifecycleAgentFeature extends MicroLifecycleComponentFeature implements IInternalBDILifecycleFeature
{
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(ILifecycleComponentFeature.class, BDILifecycleAgentFeature.class,
		new Class<?>[]{IRequiredServicesFeature.class, IProvidedServicesFeature.class, ISubcomponentsFeature.class}, null, false);
	
	/** Is the agent inited and allowed to execute rules? */
	protected boolean inited;
	
	/** Is the agent in shutdown?. */
	protected boolean shutdown;
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public BDILifecycleAgentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public IFuture<Void> body()
	{
		IInternalBDIAgentFeature bdif = component.getComponentFeature(IInternalBDIAgentFeature.class);
		createStartBehavior().startBehavior(bdif.getBDIModel(), bdif.getRuleSystem(), bdif.getCapability());
		return super.body();
	}
	
	/**
	 *  Create the start behavior.
	 */
	protected StartBehavior createStartBehavior()
	{
		return new StartBehavior(component);
	}
	
	/**
	 *  Create the end behavior.
	 */
	protected EndBehavior createEndBehavior()
	{
		return new EndBehavior(component);
	}

	/**
	 *  Cleanup the agent.
	 */
	public IFuture<Void> shutdown()
	{
		setShutdown(true);
		
		final Future<Void>	ret	= new Future<Void>();
		final IInternalBDIAgentFeature bdif = component.getComponentFeature(IInternalBDIAgentFeature.class);

		createEndBehavior().startEndBehavior(bdif.getBDIModel(), bdif.getRuleSystem(), bdif.getCapability())
			.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				BDILifecycleAgentFeature.super.shutdown().addResultListener(new DelegationResultListener<Void>(ret));
			}

			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				BDILifecycleAgentFeature.super.shutdown().addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		return ret;
	}
	
	/**
	 *  Execute a goal method.
	 */
	protected static IFuture<Boolean> executeGoalMethod(Method m, RProcessableElement goal, IEvent event, IInternalAccess component)
	{
		return invokeBooleanMethod(goal.getPojoElement(), m, goal.getModelElement(), event, null, component);
	}
	
	/**
	 *  Assemble fitting parameters from context and invoke a boolean method. 
	 */
	protected static IFuture<Boolean> invokeBooleanMethod(Object pojo, Method m, MElement modelelement, IEvent event, RPlan rplan, IInternalAccess component)
	{
		final Future<Boolean> ret = new Future<Boolean>();
		try
		{
			m.setAccessible(true);
			
			Object[] vals = BDIAgentFeature.getInjectionValues(m.getParameterTypes(), m.getParameterAnnotations(),
				modelelement, event!=null ? new ChangeEvent(event) : null, rplan, null, component);
			if(vals==null)
				System.out.println("Invalid parameter assignment");
			Object app = m.invoke(pojo, vals);
			if(app instanceof Boolean)
			{
				ret.setResult((Boolean)app);
			}
			else if(app instanceof IFuture)
			{
				((IFuture<Boolean>)app).addResultListener(new DelegationResultListener<Boolean>(ret));
			}
		}
		catch(Exception e)
		{
			System.err.println("method: "+m);
			e.printStackTrace();
			ret.setException(e);
		}
		return ret;
	}
	
	/**
	 *  Get the inited.
	 *  @return The inited.
	 */
	public boolean isInited()
	{
		return inited;
	}

	/**
	 *  The inited to set.
	 *  @param inited The inited to set
	 */
	public void setInited(boolean inited)
	{
		this.inited = inited;
	}
	

	/**
	 *  Get the shutdown. 
	 *  @return The shutdown
	 */
	public boolean isShutdown()
	{
		return shutdown;
	}

	/**
	 *  Set the shutdown.
	 *  @param shutdown The shutdown to set
	 */
	public void setShutdown(boolean shutdown)
	{
		this.shutdown = shutdown;
	}
	
	// for xml

	/**
	 *  Evaluate the condition.
	 *  @return
	 */
	public static boolean evaluateCondition(IInternalAccess agent, MCondition cond, MElement owner, Map<String, Object> vals)
	{
		boolean ret = false;
		
		UnparsedExpression uexp = cond.getExpression();
		try
		{
			Object res = SJavaParser.getParsedValue(uexp, agent.getModel().getAllImports(), CapabilityWrapper.getFetcher(agent, uexp.getLanguage(), vals), agent.getClassLoader());
			if(res instanceof Boolean)
			{
				ret = ((Boolean)res).booleanValue();
			}
			else
			{
				agent.getLogger().warning("Condition does not evaluate to boolean: "+uexp.getValue());
			}
		}
		catch(Exception e)
		{
			agent.getLogger().warning("Condition evaluation produced exception: "+uexp.getValue()+", "+e);
		}
		
		return ret;
	}
	
	/**
	 *  Condition that tests if an expression evalutes to true.
	 */
	public static class EvaluateExpressionCondition implements ICondition
	{
		protected MCondition cond;
		protected MElement owner;
		protected IInternalAccess agent;
		protected Map<String, Object> vals;
		
		public EvaluateExpressionCondition(IInternalAccess agent, MCondition cond, MElement owner, Map<String, Object> vals)
		{
			this.agent = agent;
			this.cond = cond;
			this.owner = owner;
			this.vals = vals;
		}
		
		public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
		{
//			vals.put("$event", event);
			boolean res = evaluateCondition(agent, cond, owner, vals);
			return new Future<Tuple2<Boolean,Object>>(res? ICondition.TRUE: ICondition.FALSE);
		}
	}
	
	/**
	 *  Extracted start behavior. 
	 */
	public static class LifecycleBehavior
	{
		/** The agent. */
		protected IInternalAccess component;
		
		/**
		 *  Create a new start behavior.
		 */
		public LifecycleBehavior(IInternalAccess component)
		{
			this.component = component;
		}
		
		/**
		 *  Get the capability object (only for pojo).
		 */
		public Object getCapabilityObject(String name)
		{
			IBDIAgentFeature bdif = component.getComponentFeature(IBDIAgentFeature.class);
			return ((BDIAgentFeature)bdif).getCapabilityObject(name);
		}
		
		/**
		 *  Dispatch a top level goal.
		 */
		public IFuture<Object> dispatchTopLevelGoal(Object goal)
		{
			IBDIAgentFeature bdif = component.getComponentFeature(IBDIAgentFeature.class);
			return bdif.dispatchTopLevelGoal(goal);
		}
		
		/**
		 *  Dispatch a message event.
		 */
		public IFuture<Void> sendMessageEvent(IMessageEvent message)
		{
			IMessageFeature mf = component.getComponentFeature(IMessageFeature.class);
			return mf.sendMessage((Map<String, Object>)message.getMessage(), message.getMessageType());
		}
		
		/**
		 *  Dispatch an internal event.
		 */
		public IFuture<Void> dispatchInternalEvent(IInternalEvent event)
		{
			// Pojo bdi does not support internal events
			throw new UnsupportedOperationException();
		}
		
		/**
		 * 
		 */
		protected IFuture<Void> dispatchConfigPlans(final IInternalAccess component, List<MConfigParameterElement> cplans, IBDIModel bdimodel)
		{
			Future<Void> ret = new Future<Void>();
			if(cplans!=null && cplans.size()>0)
			{
				FutureBarrier<Object> barrier = new FutureBarrier<Object>();
				for(MConfigParameterElement cplan: cplans)
				{
					MPlan mplan = bdimodel.getCapability().getPlan(cplan.getRef());
					// todo: allow Java plan constructor calls
	//				Object val = SJavaParser.parseExpression(uexp, model.getModelInfo().getAllImports(), getClassLoader());
					
					// todo: bindings in config elems
					
					List<Map<String, Object>> bindings = APL.calculateBindingElements(component, mplan);
					
					if(bindings!=null)
					{
						for(Map<String, Object> binding: bindings)
						{
							RPlan rplan = RPlan.createRPlan(mplan, mplan, null, component, null, cplan);
							barrier.addFuture(RPlan.executePlan(rplan, component));
						}
					}
					// No binding: generate one candidate.
					else
					{
						RPlan rplan = RPlan.createRPlan(mplan, mplan, null, component, null, cplan);
						barrier.addFuture(RPlan.executePlan(rplan, component));
					}
				}
				
				barrier.waitForIgnoreFailures(new ICommand<Exception>()
				{
					@Override
					public void execute(Exception e)
					{
						component.getLogger().severe("Failure during config plan execution: "+SUtil.getExceptionStacktrace(e));
					}
				}).addResultListener(new DelegationResultListener<Void>(ret));
			}
			else
			{
				ret.setResult(null);
			}
			return ret;
		}
		
		/**
		 * 
		 */
		protected IFuture<Void> dispatchConfigGoals(final IInternalAccess component, List<MConfigParameterElement> cgoals, IBDIModel bdimodel)
		{
			Future<Void> ret = new Future<Void>();
			if(cgoals!=null && cgoals.size()>0)
			{
				FutureBarrier<Object> barrier = new FutureBarrier<Object>();
				
				for(MConfigParameterElement cgoal: cgoals)
				{
					MGoal mgoal = null;
					Class<?> gcl = null;
					Object goal = null;
					
					// try to fetch via name
					mgoal = bdimodel.getCapability().getGoal(cgoal.getRef());
					if(mgoal==null && cgoal.getRef().indexOf(".")==-1)
					{
						// try with package
						mgoal = bdimodel.getCapability().getGoal(component.getModel().getPackage()+"."+cgoal.getRef());
					}
					
					if(mgoal!=null)
					{
						gcl = mgoal.getTargetClass(component.getClassLoader());
					}
					// if not found, try expression
					else
					{
						Object o = SJavaParser.parseExpression(cgoal.getRef(), component.getModel().getAllImports(), component.getClassLoader())
							.getValue(CapabilityWrapper.getFetcher(component, cgoal.getCapabilityName()));
						if(o instanceof Class)
						{
							gcl = (Class<?>)o;
						}
						else
						{
							goal = o;
							gcl = o.getClass();
						}
						mgoal = bdimodel.getCapability().getGoal(gcl.getName());
					}
		
//					// Create goal if expression available
//					if(uexp.getName()!=null && uexp.getValue().length()>0)
//					{
//						Object o = SJavaParser.parseExpression(uexp, component.getModel().getAllImports(), component.getClassLoader()).getValue(component.getFetcher());
//						if(o instanceof Class)
//						{
//							gcl = (Class<?>)o;
//						}
//						else
//						{
//							goal = o;
//							gcl = o.getClass();
//						}
//					}
//					
//					if(gcl==null && uexp.getClazz()!=null)
//					{
//						gcl = uexp.getClazz().getType(component.getClassLoader(), component.getModel().getAllImports());
//					}
//					if(gcl==null)
//					{
//						// try to fetch via name
//						mgoal = bdimodel.getCapability().getGoal(uexp.getName());
//						if(mgoal==null && uexp.getName().indexOf(".")==-1)
//						{
//							// try with package
//							mgoal = bdimodel.getCapability().getGoal(component.getModel().getPackage()+"."+uexp.getName());
//						}
//						if(mgoal!=null)
//						{
//							gcl = mgoal.getTargetClass(component.getClassLoader());
//						}
//					}						
//					if(mgoal==null)
//					{
//						mgoal = bdimodel.getCapability().getGoal(gcl.getName());
//					}
					
					// Create goal instance
					if(goal==null && gcl!=null)
					{
						try
						{
							Object agent = component.getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
							Class<?> agcl = agent.getClass();
							Constructor<?>[] cons = gcl.getDeclaredConstructors();
							for(Constructor<?> c: cons)
							{
								Class<?>[] params = c.getParameterTypes();
								if(params.length==0)
								{
									// perfect found empty con
									goal = gcl.newInstance();
									break;
								}
								else if(params.length==1 && params[0].equals(agcl))
								{
									// found (first level) inner class constructor
									goal = c.newInstance(new Object[]{agent});
									break;
								}
							}
						}
						catch(RuntimeException e)
						{
							throw e;
						}
						catch(Exception e)
						{
							throw new RuntimeException(e);
						}
					}
					
					if(mgoal==null || (goal==null && gcl!=null))
					{
						throw new RuntimeException("Could not create goal: "+cgoal);
					}
					
					List<Map<String, Object>> bindings = APL.calculateBindingElements(component, mgoal);
					
					if(goal==null)
					{
						// XML only
						if(bindings!=null)
						{
							for(Map<String, Object> binding: bindings)
							{
								RGoal rgoal = new RGoal(component, mgoal, null, null, binding, cgoal);
								barrier.addFuture(dispatchTopLevelGoal(rgoal));//.addResultListener(goallis);
							}
						}
						// No binding: generate one candidate.
						else
						{
							RGoal rgoal = new RGoal(component, mgoal, goal, null, null, cgoal);
							barrier.addFuture(dispatchTopLevelGoal(rgoal));//.addResultListener(goallis);
						}
					}
					else
					{
						// Pojo only
						barrier.addFuture(dispatchTopLevelGoal(goal));//.addResultListener(goallis);								
					}
				}
				
				// wait for all goals being finished
				barrier.waitForIgnoreFailures(new ICommand<Exception>()
				{
					@Override
					public void execute(Exception e)
					{
						component.getLogger().severe("Failure during config goal processing: "+SUtil.getExceptionStacktrace(e));
					}
				}).addResultListener(new DelegationResultListener<Void>(ret));
			}
			else
			{
				ret.setResult(null);
			}
			
			return ret;
		}
		
		/**
		 * 
		 */
		protected IFuture<Void> dispatchConfigEvents(IInternalAccess component, List<MConfigParameterElement> cevents, IBDIModel bdimodel)
		{
			Future<Void> ret = new Future<Void>();
			FutureBarrier<Void> barrier = new FutureBarrier<Void>();
			
			IInternalBDIAgentFeature bdif = component.getComponentFeature(IInternalBDIAgentFeature.class);
			MCapability mcapa = (MCapability)bdif.getCapability().getModelElement();
			
			// Send initial messages
			// Throw initial internal events
			for(MConfigParameterElement cpe: SUtil.safeList(cevents))
			{
				MInternalEvent mievent = mcapa.getInternalEvent(cpe.getRef());
				if(mievent!=null)
				{
					RInternalEvent rievent = new RInternalEvent(mievent, component, cpe);
					dispatchInternalEvent(rievent);
				}
				else
				{
					MMessageEvent mmevent = mcapa.getResolvedMessageEvent(null, cpe.getRef());
					RMessageEvent rmevent = new RMessageEvent(mmevent, component, cpe);
					barrier.addFuture(sendMessageEvent(rmevent));
				}
			}
			
			barrier.waitFor().addResultListener(new DelegationResultListener<Void>(ret));
			return ret;
		}
	}
	
	/**
	 *  Extracted start behavior. 
	 */
	public static class StartBehavior extends LifecycleBehavior
	{
		/**
		 *  Create a new start behavior.
		 */
		public StartBehavior(IInternalAccess component)
		{
			super(component);
		}
		
		/**
		 *  Start the component behavior.
		 */
		public void startBehavior(final IBDIModel bdimodel, final RuleSystem rulesystem, final RCapability rcapa)
		{
//			super.startBehavior();
			
//			final Object agent = microagent instanceof PojoBDIAgent? ((PojoBDIAgent)microagent).getPojoAgent(): microagent;
					
//			final IBDIAgentFeature bdif = component.getComponentFeature(IBDIAgentFeature.class);
//			final IInternalBDIAgentFeature ibdif = (IInternalBDIAgentFeature)bdif; 
//			final IBDIModel bdimodel = ibdif.getBDIModel();
			
			final IResultListener<Object> goallis = new IResultListener<Object>()
			{
				public void resultAvailable(Object result)
				{
					component.getLogger().info("Goal succeeded: "+result);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					component.getLogger().info("Goal failed: "+exception);
				}
			};
			
			// Init bdi configuration
			String confname = component.getConfiguration();
			if(confname!=null)
			{
				MConfiguration mconf = bdimodel.getCapability().getConfiguration(confname);
				
				if(mconf!=null)
				{
					// only for pojo agents / xml is inited in beliefbase init
					if(bdimodel instanceof BDIModel)
					{
						// Set initial belief values
						List<MConfigBeliefElement> ibels = mconf.getInitialBeliefs();
						if(ibels!=null)
						{
							for(MConfigBeliefElement ibel: ibels)
							{
								try
								{
									UnparsedExpression	fact	= ibel.getFacts().get(0);	// pojo initial beliefs are @NameValue, thus exactly one fact.
									MBelief mbel = bdimodel.getCapability().getBelief(ibel.getName());
									Object val = SJavaParser.parseExpression(fact, component.getModel().getAllImports(), component.getClassLoader()).getValue(CapabilityWrapper.getFetcher(component, fact.getLanguage()));
									mbel.setValue(component, val);
								}
								catch(RuntimeException e)
								{
									throw e;
								}
								catch(Exception e)
								{
									throw new RuntimeException(e);
								}
							}
						}
					}
					
					// Create initial plans (create plans before other elements as they might want to react to these)
					List<MConfigParameterElement> iplans = mconf.getInitialPlans();
					dispatchConfigPlans(component, iplans, bdimodel);
					
					// Create initial goals
					List<MConfigParameterElement> igoals = mconf.getInitialGoals();
					dispatchConfigGoals(component, igoals, bdimodel);
					
					// Create initial events
					List<MConfigParameterElement> ievents = mconf.getInitialEvents();
					dispatchConfigEvents(component, ievents, bdimodel);
				}
			}
			
			// Observe dynamic beliefs
			List<MBelief> beliefs = bdimodel.getCapability().getBeliefs();
			
			for(final MBelief mbel: beliefs)
			{
				List<EventType> events = mbel.getEvents();
				
//				Object cap = null;
//				if(component.getComponentFeature0(IPojoComponentFeature.class)!=null)
//				{
//					Object agent = component.getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
//					Object ocapa = agent;
//					int	i	= mbel.getName().indexOf(MElement.CAPABILITY_SEPARATOR);
//					if(i!=-1)
//					{
//						ocapa	= ((BDIAgentFeature)bdif).getCapabilityObject(mbel.getName().substring(0, mbel.getName().lastIndexOf(MElement.CAPABILITY_SEPARATOR)));
//					}
//					cap	= ocapa;
//				}
//				final Object fcapa = cap;
				
				String name = null;
				Object capa = null;
				if(component.getComponentFeature0(IPojoComponentFeature.class)!=null)
				{
					int	i	= mbel.getName().indexOf(MElement.CAPABILITY_SEPARATOR);
					if(i!=-1)
					{
						capa	= getCapabilityObject(mbel.getName().substring(0, mbel.getName().lastIndexOf(MElement.CAPABILITY_SEPARATOR)));
						name	= mbel.getName().substring(mbel.getName().lastIndexOf(MElement.CAPABILITY_SEPARATOR)+1); 
					}
					else
					{
						Object agent = component.getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
						capa	= agent;
						name	= mbel.getName();
					}
				}
				final String fname = name;
				final Object fcapa = capa;
				
				// Automatic reevaluation if belief depends on other beliefs
				if(!events.isEmpty() || mbel.getEvaluationMode().equals(EvaluationMode.PUSH))
				{
					Rule<Void> rule = new Rule<Void>(mbel.getName()+"_belief_update", 
						ICondition.TRUE_CONDITION, new IAction<Void>()
					{
						Object oldval = null;
						
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
//							System.out.println("belief update: "+event);
							// Invoke dynamic update method if field belief
							if(mbel.isFieldBelief())
							{
								try
								{
									Method um = fcapa.getClass().getMethod(IBDIClassGenerator.DYNAMIC_BELIEF_UPDATEMETHOD_PREFIX+SUtil.firstToUpperCase(mbel.getName()), new Class[0]);
									um.invoke(fcapa, new Object[0]);
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
							}
							// Otherwise just call getValue and throw event
							else if(fcapa!=null) // if is pojo 
							{
								Object value = mbel.getValue(component);
								// todo: save old value?!
								BDIAgentFeature.createChangeEvent(value, oldval, null, component, mbel.getName());
								oldval = value;
							}
							else // xml belief push mode
							{
								// reevaluate the belief on change events
								Object value = SJavaParser.parseExpression(mbel.getDefaultFact(), 
									component.getModel().getAllImports(), component.getClassLoader()).getValue(CapabilityWrapper.getFetcher(component, mbel.getDefaultFact().getLanguage()));
								// save the value
								mbel.setValue(component, value);
//								oldval = value;	// not needed for xml
							}
							return IFuture.DONE;
						}
					});
					rule.setEvents(events);
					rulesystem.getRulebase().addRule(rule);
				}
				
				if(mbel.getUpdaterateValue(component)>0)
				{
					final IClockService cs = SServiceProvider.getLocalService(component, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM);
//					cs.createTimer(mbel.getUpdaterate(), new ITimedObject()
					ITimedObject to = new ITimedObject()
					{
						ITimedObject	self	= this;
						Object oldval = null;
						
						public void timeEventOccurred(long currenttime)
						{
							try
							{
								component.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
								{
									public IFuture<Void> execute(IInternalAccess ia)
									{
										try
										{
											// Invoke dynamic update method if field belief
											if(mbel.isFieldBelief())
											{
												Method um = fcapa.getClass().getMethod(IBDIClassGenerator.DYNAMIC_BELIEF_UPDATEMETHOD_PREFIX+SUtil.firstToUpperCase(fname), new Class[0]);
												um.invoke(fcapa, new Object[0]);
											}
											// Otherwise just call getValue and throw event
											else if(fcapa!=null)
											{
												Object value = mbel.getValue(fcapa, component.getClassLoader());
												BDIAgentFeature.createChangeEvent(value, oldval, null, component, mbel.getName());
												oldval = value;
											}
											else // xml belief updaterate
											{
												// reevaluate the belief on change events
												Object value = SJavaParser.parseExpression(mbel.getDefaultFact(), 
													component.getModel().getAllImports(), component.getClassLoader()).getValue(CapabilityWrapper.getFetcher(component, mbel.getDefaultFact().getLanguage()));
												// save the value 
												// change event is automatically thrown
												mbel.setValue(component, value);
												oldval = value;
											}
										}
										catch(Exception e)
										{
											e.printStackTrace();
										}
										
										cs.createTimer(mbel.getUpdaterateValue(component), self);
										return IFuture.DONE;
									}
								});
							}
							catch(ComponentTerminatedException cte)
							{
								
							}
						}
					
//						public void exceptionOccurred(Exception exception)
//						{
//							component.getLogger().severe("Cannot update belief "+mbel.getName()+": "+exception);
//						}
					};
					// Evaluate at time 0, updaterate*1, updaterate*2, ...
					to.timeEventOccurred(cs.getTime());
				}
			}
			
			// Observe dynamic parameters of goals
			// todo: other parameter elements?!
			List<MGoal> mgoals = bdimodel.getCapability().getGoals();
			
			for(final MGoal mgoal: mgoals)
			{
				List<MParameter> mparams = mgoal.getParameters();
				
				if(mparams!=null)
				{
					for(final MParameter mparam: mparams)
					{
						if(mparam.getEvaluationMode().equals(EvaluationMode.PUSH))
						{
							List<EventType> events = mparam.getEvents();
						
							// Automatic reevaluation if belief depends on other beliefs
							if(!events.isEmpty())
							{
								Rule<Void> rule = new Rule<Void>(mgoal.getName()+"_"+mparam.getName()+"_parameter_update", 
									ICondition.TRUE_CONDITION, new IAction<Void>()
								{
									// todo: oldval
			//						Object oldval = null;
									
									public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
									{
//										System.out.println("parameter update: "+event);
										
										RCapability capa = BDIAgentFeature.getCapability(component);
										for(RGoal goal: SUtil.safeCollection(capa.getGoals(mgoal)))
										{
											if(!mparam.isMulti(component.getClassLoader()))
											{
												((RParameter)goal.getParameter(mparam.getName())).updateDynamicValue();
											}
											else
											{
												((RParameterSet)goal.getParameterSet(mparam.getName())).updateDynamicValues();
											}
										}
										
										return IFuture.DONE;
									}
								});
								
								rule.setEvents(events);
								rulesystem.getRulebase().addRule(rule);
							}
							
							if(mparam.getUpdaterateValue(component)>0)
							{
								final IClockService cs = SServiceProvider.getLocalService(component, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM);
								ITimedObject to = new ITimedObject()
								{
									ITimedObject self = this;
			//						Object oldval = null;
									
									public void timeEventOccurred(long currenttime)
									{
										try
										{
											component.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
											{
												public IFuture<Void> execute(IInternalAccess ia)
												{
													try
													{
														System.out.println("parameter updaterate: "+mparam.getUpdaterateValue(component));
														
														RCapability capa = BDIAgentFeature.getCapability(component);
														for(RGoal goal: SUtil.safeCollection(capa.getGoals(mgoal)))
														{
															if(!mparam.isMulti(component.getClassLoader()))
															{
																((RParameter)goal.getParameter(mparam.getName())).updateDynamicValue();
															}
															else
															{
																((RParameterSet)goal.getParameterSet(mparam.getName())).updateDynamicValues();
															}
														}
													}
													catch(Exception e)
													{
														e.printStackTrace();
													}
													
													cs.createTimer(mparam.getUpdaterateValue(component), self);
													return IFuture.DONE;
												}
											});
										}
										catch(ComponentTerminatedException cte)
										{
										}
									}
								};
								// Evaluate at time 0, updaterate*1, updaterate*2, ...
								to.timeEventOccurred(cs.getTime());
							}
						}
					}
				}
			}
			
			// Observe goal types
			List<MGoal> goals = bdimodel.getCapability().getGoals();
			for(final MGoal mgoal: goals)
			{
//				todo: explicit bdi creation rule
//				rulesystem.observeObject(goals.get(i).getTargetClass(getClassLoader()));
			
//				boolean fin = false;
				
				final Class<?> gcl = mgoal.getTargetClass(component.getClassLoader());
//				boolean declarative = false;
//				boolean maintain = false;
				
				List<MCondition> conds = mgoal.getConditions(MGoal.CONDITION_CREATION);
				if(conds!=null)
				{
					for(MCondition cond: conds)
					{
						if(cond.getConstructorTarget()!=null)
						{
							final Constructor<?> c = cond.getConstructorTarget().getConstructor(component.getClassLoader());
							
							Rule<Void> rule = new Rule<Void>(mgoal.getName()+"_goal_create", 
								new NotInShutdownCondition(component), new IAction<Void>()
							{
								public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
								{
		//							System.out.println("create: "+context);
									
									Object pojogoal = null;
									try
									{
										boolean ok = true;
										Class<?>[] ptypes = c.getParameterTypes();
										Object[] pvals = new Object[ptypes.length];
										
										Annotation[][] anns = c.getParameterAnnotations();
										int skip = ptypes.length - anns.length;
										
										for(int i=0; i<ptypes.length; i++)
										{
											Object agent = component.getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
											Object	o	= event.getContent();
											if(o!=null && SReflect.isSupertype(ptypes[i], o.getClass()))
											{
												pvals[i] = o;
											}
											else if(o instanceof ChangeInfo<?> && ((ChangeInfo)o).getValue()!=null && SReflect.isSupertype(ptypes[i], ((ChangeInfo)o).getValue().getClass()))
											{
												pvals[i] = ((ChangeInfo)o).getValue();
											}
											else if(SReflect.isSupertype(agent.getClass(), ptypes[i]))
											{
												pvals[i] = agent;
											}
											
											// ignore implicit parameters of inner class constructor
											if(pvals[i]==null && i>=skip)
											{
												for(int j=0; anns!=null && j<anns[i-skip].length; j++)
												{
													if(anns[i-skip][j] instanceof CheckNotNull)
													{
														ok = false;
														break;
													}
												}
											}
										}
										
										if(ok)
										{
											pojogoal = c.newInstance(pvals);
										}
									}
									catch(RuntimeException e)
									{
										throw e;
									}
									catch(Exception e)
									{
										throw new RuntimeException(e);
									}
									
									if(pojogoal!=null && !rcapa.containsGoal(pojogoal))
									{
										final Object fpojogoal = pojogoal;
										dispatchTopLevelGoal(pojogoal).addResultListener(goallis);
									}
//									else
//									{
//										System.out.println("new goal not adopted, already contained: "+pojogoal);
//									}
								
									return IFuture.DONE;
								}
							});
							rule.setEvents(cond.getEvents());
							rulesystem.getRulebase().addRule(rule);
						}
						else if(cond.getMethodTarget()!=null)
						{
							final Method m = cond.getMethodTarget().getMethod(component.getClassLoader());
							
							Rule<Void> rule = new Rule<Void>(mgoal.getName()+"_goal_create", 
								new CombinedCondition(new ICondition[]{new NotInShutdownCondition(component), new MethodCondition(null, m)
							{
								protected Object invokeMethod(IEvent event) throws Exception
								{
									m.setAccessible(true);
									Object[] pvals = BDIAgentFeature.getInjectionValues(m.getParameterTypes(), m.getParameterAnnotations(),
										mgoal, new ChangeEvent(event), null, null, component);
									return pvals!=null? m.invoke(null, pvals): null;
								}
							}}), new IAction<Void>()
							{
								public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
								{
			//						System.out.println("create: "+context);
									
									if(condresult!=null)
									{
										if(SReflect.isIterable(condresult))
										{
											for(Iterator<Object> it = SReflect.getIterator(condresult); it.hasNext(); )
											{
												Object pojogoal = it.next();
												dispatchTopLevelGoal(pojogoal).addResultListener(goallis);
											}
										}
										else
										{
											dispatchTopLevelGoal(condresult).addResultListener(goallis);
										}
									}
									else
									{
										Constructor<?>[] cons = gcl.getConstructors();
										Object pojogoal = null;
										boolean ok = false;
										for(Constructor<?> c: cons)
										{
											try
											{
												Object[] vals = BDIAgentFeature.getInjectionValues(c.getParameterTypes(), c.getParameterAnnotations(),
													mgoal, new ChangeEvent(event), null, null, component);
												if(vals!=null)
												{
													pojogoal = c.newInstance(vals);
													dispatchTopLevelGoal(pojogoal).addResultListener(goallis);
													break;
												}
												else
												{
													ok = true;
												}
											}
											catch(Exception e)
											{
											}
										}
										if(pojogoal==null && !ok)
											throw new RuntimeException("Unknown how to create goal: "+gcl);
									}
									return IFuture.DONE;
								}
							});
							rule.setEvents(cond.getEvents());
							rulesystem.getRulebase().addRule(rule);
						}
						else
						{
							Rule<Void> rule = new Rule<Void>(mgoal.getName()+"_goal_create", 
								new CombinedCondition(new ICondition[]{new NotInShutdownCondition(component), new EvaluateExpressionCondition(component, cond, mgoal, null)}), new IAction<Void>()
							{
								public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
								{
			//						System.out.println("create: "+create);
									
									List<Map<String, Object>> bindings = APL.calculateBindingElements(component, mgoal);
									
									if(bindings!=null)
									{
										for(Map<String, Object> binding: bindings)
										{
											RGoal rgoal = new RGoal(component, mgoal, null, null, binding, null);
											dispatchTopLevelGoal(rgoal).addResultListener(goallis);
										}
									}
									// No binding: generate one candidate.
									else
									{
										RGoal rgoal = new RGoal(component, mgoal, null, null, null, null);
										dispatchTopLevelGoal(rgoal).addResultListener(goallis);
									}
									
									return IFuture.DONE;
								}
							});
							
							rule.setEvents(cond.getEvents());
							rulesystem.getRulebase().addRule(rule);
						}
					}
				}
				
				conds = mgoal.getConditions(MGoal.CONDITION_DROP);
				if(conds!=null)
				{
					for(final MCondition cond: conds)
					{
						final Method m = cond.getMethodTarget()==null? null: cond.getMethodTarget().getMethod(component.getClassLoader());
						
						Rule<?> rule = new Rule<Void>(mgoal.getName()+"_goal_drop", 
							new GoalsExistCondition(mgoal, rcapa), new IAction<Void>()
						{
							public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
							{
								for(final RGoal goal: rcapa.getGoals(mgoal))
								{
									if(!RGoal.GoalLifecycleState.DROPPING.equals(goal.getLifecycleState())
										 && !RGoal.GoalLifecycleState.DROPPED.equals(goal.getLifecycleState()))
									{
										if(m!=null)
										{
											executeGoalMethod(m, goal, event, component)
												.addResultListener(new IResultListener<Boolean>()
											{
												public void resultAvailable(Boolean result)
												{
													if(result.booleanValue())
													{
		//												System.out.println("Goal dropping triggered: "+goal);
						//								rgoal.setLifecycleState(BDIAgent.this, rgoal.GOALLIFECYCLESTATE_DROPPING);
														if(!goal.isFinished())
														{
															goal.setException(new GoalFailureException("drop condition: "+m.getName()));
//															{
//																public void printStackTrace() 
//																{
//																	super.printStackTrace();
//																}
//															});
															goal.setProcessingState(component, RGoal.GoalProcessingState.FAILED);
														}
													}
												}
												
												public void exceptionOccurred(Exception exception)
												{
												}
											});
										}
										else
										{
											if(evaluateCondition(component, cond, mgoal, SUtil.createHashMap(new String[]{"$goal"}, new Object[]{goal})))
											{
												if(!goal.isFinished())
												{
													goal.setException(new GoalFailureException("drop condition: "+goal));
													goal.setProcessingState(component, RGoal.GoalProcessingState.FAILED);
												}
											}
										}
									}
								}
								
								return IFuture.DONE;
							}
						});
						List<EventType> events = new ArrayList<EventType>(cond.getEvents());
						events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED, mgoal.getName()}));
						rule.setEvents(events);
						rulesystem.getRulebase().addRule(rule);
//							rule.setEvents(cond.getEvents());
//							rulesystem.getRulebase().addRule(rule);
					}
				}
				
				conds = mgoal.getConditions(MGoal.CONDITION_CONTEXT);
				if(conds!=null)
				{
					for(final MCondition cond: conds)
					{
						final Method m = cond.getMethodTarget()==null? null: cond.getMethodTarget().getMethod(component.getClassLoader());
						
						Rule<?> rule = new Rule<Void>(mgoal.getName()+"_goal_suspend", 
							new GoalsExistCondition(mgoal, rcapa), new IAction<Void>()
						{
							public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
							{
								for(final RGoal goal: rcapa.getGoals(mgoal))
								{
									if(!RGoal.GoalLifecycleState.SUSPENDED.equals(goal.getLifecycleState())
									  && !RGoal.GoalLifecycleState.DROPPING.equals(goal.getLifecycleState())
									  && !RGoal.GoalLifecycleState.DROPPED.equals(goal.getLifecycleState()))
									{	
										if(m!=null)
										{
											executeGoalMethod(m, goal, event, component)
												.addResultListener(new IResultListener<Boolean>()
											{
												public void resultAvailable(Boolean result)
												{
													if(!result.booleanValue())
													{
		//												if(goal.getMGoal().getName().indexOf("AchieveCleanup")!=-1)
		//													System.out.println("Goal suspended: "+goal);
														goal.setLifecycleState(component, RGoal.GoalLifecycleState.SUSPENDED);
														goal.setState(RProcessableElement.State.INITIAL);
													}
												}
												
												public void exceptionOccurred(Exception exception)
												{
												}
											});
										}
										else
										{
											if(!evaluateCondition(component, cond, mgoal, SUtil.createHashMap(new String[]{"$goal"}, new Object[]{goal})))
											{
												goal.setLifecycleState(component, RGoal.GoalLifecycleState.SUSPENDED);
												goal.setState(RProcessableElement.State.INITIAL);
											}
										}
									}
								}
								return IFuture.DONE;
							}
						});
						List<EventType> events = new ArrayList<EventType>(cond.getEvents());
						events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED, mgoal.getName()}));
						rule.setEvents(events);
						rulesystem.getRulebase().addRule(rule);
						
//							rule.setEvents(cond.getEvents());
//							rulesystem.getRulebase().addRule(rule);
						
						rule = new Rule<Void>(mgoal.getName()+"_goal_option", 
							new GoalsExistCondition(mgoal, rcapa), new IAction<Void>()
						{
							public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
							{
								for(final RGoal goal: rcapa.getGoals(mgoal))
								{
									if(RGoal.GoalLifecycleState.SUSPENDED.equals(goal.getLifecycleState()))
									{	
										if(m!=null)
										{
											executeGoalMethod(m, goal, event, component)
												.addResultListener(new IResultListener<Boolean>()
											{
												public void resultAvailable(Boolean result)
												{
													if(result.booleanValue())
													{
		//												if(goal.getMGoal().getName().indexOf("AchieveCleanup")!=-1)
		//												System.out.println("Goal made option: "+goal);
														goal.setLifecycleState(component, RGoal.GoalLifecycleState.OPTION);
		//												setState(ia, PROCESSABLEELEMENT_INITIAL);
													}
												}
												
												public void exceptionOccurred(Exception exception)
												{
												}
											});
										}
										else
										{
											if(evaluateCondition(component, cond, mgoal, SUtil.createHashMap(new String[]{"$goal"}, new Object[]{goal})))
											{
												goal.setLifecycleState(component, RGoal.GoalLifecycleState.OPTION);
											}
										}
									}
								}
								
								return IFuture.DONE;
							}
						});
						rule.setEvents(events);
						rulesystem.getRulebase().addRule(rule);
						
//							rule.setEvents(cond.getEvents());
//							rulesystem.getRulebase().addRule(rule);
					}
				}
				
				conds = mgoal.getConditions(MGoal.CONDITION_TARGET);
				if(conds!=null)
				{
					for(final MCondition cond: conds)
					{
						final Method m = cond.getMethodTarget()==null? null: cond.getMethodTarget().getMethod(component.getClassLoader());
											
						Rule<?> rule = new Rule<Void>(mgoal.getName()+"_goal_target", 
							new CombinedCondition(new ICondition[]{
								new GoalsExistCondition(mgoal, rcapa)
			//							, new LifecycleStateCondition(SUtil.createHashSet(new String[]
			//							{
			//								RGoal.GOALLIFECYCLESTATE_ACTIVE,
			//								RGoal.GOALLIFECYCLESTATE_ADOPTED,
			//								RGoal.GOALLIFECYCLESTATE_OPTION,
			//								RGoal.GOALLIFECYCLESTATE_SUSPENDED
			//							}))
							}),
							new IAction<Void>()
						{
							public IFuture<Void> execute(final IEvent event, final IRule<Void> rule, final Object context, Object condresult)
							{
//								if(mgoal.getName().indexOf("cleanup")!=-1)
//									System.out.println("target test");
								
								for(final RGoal goal: rcapa.getGoals(mgoal))
								{
									if(m!=null)
									{
										executeGoalMethod(m, goal, event, component)
											.addResultListener(new IResultListener<Boolean>()
										{
											public void resultAvailable(Boolean result)
											{
												if(result.booleanValue())
												{
													if(!goal.isFinished())
													{
														goal.targetConditionTriggered(component, event, rule, context);
													}
												}
											}
											
											public void exceptionOccurred(Exception exception)
											{
											}
										});
									}
									else
									{
										if(!goal.isFinished() && evaluateCondition(component, cond, mgoal, SUtil.createHashMap(new String[]{"$goal"}, new Object[]{goal})))
										{
											goal.targetConditionTriggered(component, event, rule, context);
										}
									}
								}
							
								return IFuture.DONE;
							}
						});
						List<EventType> events = cond.getEvents()==null || cond.getEvents().size()==0? new ArrayList<EventType>(): new ArrayList<EventType>(cond.getEvents());
						events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED, mgoal.getName()}));
						rule.setEvents(events);
						rulesystem.getRulebase().addRule(rule);
					}
				}
				
				conds = mgoal.getConditions(MGoal.CONDITION_RECUR);
				if(conds!=null)
				{
					for(final MCondition cond: conds)
					{
						final Method m = cond.getMethodTarget()==null? null: cond.getMethodTarget().getMethod(component.getClassLoader());
											
						Rule<?> rule = new Rule<Void>(mgoal.getName()+"_goal_recur",
							new GoalsExistCondition(mgoal, rcapa), new IAction<Void>()
		//						new CombinedCondition(new ICondition[]{
		//							new LifecycleStateCondition(GOALLIFECYCLESTATE_ACTIVE),
		//							new ProcessingStateCondition(GOALPROCESSINGSTATE_PAUSED),
		//							new MethodCondition(getPojoElement(), m),
		//						}), new IAction<Void>()
						{
							public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
							{
								for(final RGoal goal: rcapa.getGoals(mgoal))
								{
									if(RGoal.GoalLifecycleState.ACTIVE.equals(goal.getLifecycleState())
										&& RGoal.GoalProcessingState.PAUSED.equals(goal.getProcessingState()))
									{	
										if(m!=null)
										{
											executeGoalMethod(m, goal, event, component)
												.addResultListener(new IResultListener<Boolean>()
											{
												public void resultAvailable(Boolean result)
												{
													if(result.booleanValue())
													{
														goal.setTriedPlans(null);
														goal.setApplicablePlanList(null);
														goal.setProcessingState(component, RGoal.GoalProcessingState.INPROCESS);
													}
												}
												
												public void exceptionOccurred(Exception exception)
												{
												}
											});
										}
										else
										{
											if(evaluateCondition(component, cond, mgoal, SUtil.createHashMap(new String[]{"$goal"}, new Object[]{goal})))
											{
												goal.setTriedPlans(null);
												goal.setApplicablePlanList(null);
												goal.setProcessingState(component, RGoal.GoalProcessingState.INPROCESS);
											}
										}
									}
								}
								return IFuture.DONE;
							}
						});
						rule.setEvents(cond.getEvents());
						rulesystem.getRulebase().addRule(rule);
					}
				}
				
				conds = mgoal.getConditions(MGoal.CONDITION_MAINTAIN);
				if(conds!=null)
				{
					for(final MCondition cond: conds)
					{
						final Method m = cond.getMethodTarget()==null? null: cond.getMethodTarget().getMethod(component.getClassLoader());
						
						Rule<?> rule = new Rule<Void>(mgoal.getName()+"_goal_maintain", 
							new GoalsExistCondition(mgoal, rcapa), new IAction<Void>()
		//						new CombinedCondition(new ICondition[]{
		//							new LifecycleStateCondition(GOALLIFECYCLESTATE_ACTIVE),
		//							new ProcessingStateCondition(GOALPROCESSINGSTATE_IDLE),
		//							new MethodCondition(getPojoElement(), mcond, true),
		//						}), new IAction<Void>()
						{
							public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
							{
								for(final RGoal goal: rcapa.getGoals(mgoal))
								{
									if(RGoal.GoalLifecycleState.ACTIVE.equals(goal.getLifecycleState())
										&& RGoal.GoalProcessingState.IDLE.equals(goal.getProcessingState()))
									{	
										if(m!=null)
										{
											executeGoalMethod(m, goal, event, component)
												.addResultListener(new IResultListener<Boolean>()
											{
												public void resultAvailable(Boolean result)
												{
													if(!result.booleanValue())
													{
		//												System.out.println("Goal maintain triggered: "+goal);
		//												System.out.println("state was: "+goal.getProcessingState());
														goal.setProcessingState(component, RGoal.GoalProcessingState.INPROCESS);
													}
												}
												
												public void exceptionOccurred(Exception exception)
												{
												}
											});
										}
										else // xml expression
										{
											if(!evaluateCondition(component, cond, mgoal, SUtil.createHashMap(new String[]{"$goal"}, new Object[]{goal})))
											{
												goal.setProcessingState(component, RGoal.GoalProcessingState.INPROCESS);
											}
										}
									}
								}
								return IFuture.DONE;
							}
						});
						List<EventType> events = new ArrayList<EventType>(cond.getEvents());
						events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED, mgoal.getName()}));
						rule.setEvents(events);
						rulesystem.getRulebase().addRule(rule);
						
						// if has no own target condition
						if(mgoal.getConditions(MGoal.CONDITION_TARGET)==null)
						{
							// if not has own target condition use the maintain cond
							rule = new Rule<Void>(mgoal.getName()+"_goal_target", 
								new GoalsExistCondition(mgoal, rcapa), new IAction<Void>()
		//							new MethodCondition(getPojoElement(), mcond), new IAction<Void>()
							{
								public IFuture<Void> execute(final IEvent event, final IRule<Void> rule, final Object context, Object condresult)
								{
									for(final RGoal goal: rcapa.getGoals(mgoal))
									{
										if(m!=null)
										{
											executeGoalMethod(m, goal, event, component)
												.addResultListener(new IResultListener<Boolean>()
											{
												public void resultAvailable(Boolean result)
												{
													if(result.booleanValue())
													{
														goal.targetConditionTriggered(component, event, rule, context);
													}
												}
												
												public void exceptionOccurred(Exception exception)
												{
												}
											});
										}
										else // xml expression
										{
											if(evaluateCondition(component, cond, mgoal, SUtil.createHashMap(new String[]{"$goal"}, new Object[]{goal})))
											{
												goal.targetConditionTriggered(component, event, rule, context);
											}
										}
									}
									
									return IFuture.DONE;
								}
							});
							rule.setEvents(cond.getEvents());
							rulesystem.getRulebase().addRule(rule);
						}
					}
				}
			}
			
			// Observe plan types
			List<MPlan> mplans = bdimodel.getCapability().getPlans();
			for(int i=0; i<mplans.size(); i++)
			{
				final MPlan mplan = mplans.get(i);
				
				IAction<Void> createplan = new IAction<Void>()
				{
					public IFuture<Void> execute(final IEvent event, IRule<Void> rule, Object context, Object condresult)
					{
						// Create all binding plans
						List<MPlanInfo> cands = APL.createMPlanCandidates(component, mplan);

						final CollectionResultListener<MPlanInfo> lis = new CollectionResultListener<MPlanInfo>(cands.size(), 
							new IResultListener<Collection<MPlanInfo>>()
						{
							public void resultAvailable(Collection<MPlanInfo> result)
							{
								for(MPlanInfo mplaninfo: result)
								{
									RPlan rplan = RPlan.createRPlan(mplan, mplan, new ChangeEvent(event), component, mplaninfo.getBinding(), null);
									RPlan.executePlan(rplan, component);
								}
							}
							
							public void exceptionOccurred(Exception exception)
							{
							}
						});
						
						for(final MPlanInfo mplan: cands)
						{
							// check precondition
							APL.checkMPlan(component, mplan, null).addResultListener(new IResultListener<Boolean>()
							{
								public void resultAvailable(Boolean result)
								{
									if(result.booleanValue())
									{
										lis.resultAvailable(mplan);
									}
									else
									{
										lis.exceptionOccurred(null);
									}
								}
								
								public void exceptionOccurred(Exception exception)
								{
									lis.exceptionOccurred(exception);
								}
							});
						}
						
						return IFuture.DONE;
					}
				};
				
				MTrigger trigger = mplan.getTrigger();
				
				if(trigger!=null)
				{
					List<String> fas = trigger.getFactAddeds();
					if(fas!=null && fas.size()>0)
					{
						// todo: hmm turn off these too? new NotInShutdownCondition(component)
						Rule<Void> rule = new Rule<Void>("create_plan_factadded_"+mplan.getName(), ICondition.TRUE_CONDITION, createplan);
						for(String fa: fas)
						{
							rule.addEvent(new EventType(new String[]{ChangeEvent.FACTADDED, fa}));
						}
						rulesystem.getRulebase().addRule(rule);
					}
		
					List<String> frs = trigger.getFactRemoveds();
					if(frs!=null && frs.size()>0)
					{
						Rule<Void> rule = new Rule<Void>("create_plan_factremoved_"+mplan.getName(), ICondition.TRUE_CONDITION, createplan);
						for(String fr: frs)
						{
							rule.addEvent(new EventType(new String[]{ChangeEvent.FACTREMOVED, fr}));
						}
						rulesystem.getRulebase().addRule(rule);
					}
					
					List<String> fcs = trigger.getFactChangeds();
					if(fcs!=null && fcs.size()>0)
					{
						Rule<Void> rule = new Rule<Void>("create_plan_factchanged_"+mplan.getName(), ICondition.TRUE_CONDITION, createplan);
						for(String fc: fcs)
						{
							rule.addEvent(new EventType(new String[]{ChangeEvent.FACTCHANGED, fc}));
							rule.addEvent(new EventType(new String[]{ChangeEvent.BELIEFCHANGED, fc}));
						}
						rulesystem.getRulebase().addRule(rule);
					}
					
					List<MGoal> gfs = trigger.getGoalFinisheds();
					if(gfs!=null && gfs.size()>0)
					{
						Rule<Void> rule = new Rule<Void>("create_plan_goalfinished_"+mplan.getName(), ICondition.TRUE_CONDITION, createplan);
						for(MGoal gf: gfs)
						{
							rule.addEvent(new EventType(new String[]{ChangeEvent.GOALDROPPED, gf.getName()}));
						}
						rulesystem.getRulebase().addRule(rule);
					}
					
					final MCondition mcond = trigger.getCondition();
					if(mcond!=null)
					{
						Rule<Void> rule = new Rule<Void>("create_plan_condition_"+mplan.getName(), new CombinedCondition(new ICondition[]{new NotInShutdownCondition(component), new ICondition()
						{
							public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
							{
								UnparsedExpression uexp = mcond.getExpression();
								Boolean ret = (Boolean)SJavaParser.parseExpression(uexp, component.getModel().getAllImports(), component.getClassLoader()).getValue(CapabilityWrapper.getFetcher(component, uexp.getLanguage()));
								return new Future<Tuple2<Boolean, Object>>(ret!=null && ret.booleanValue()? TRUE: FALSE);
							}
						}}), createplan);
						rule.setEvents(mcond.getEvents());
						rulesystem.getRulebase().addRule(rule);
					}
				}
				
				// context condition
								
				final MethodInfo mi = mplan.getBody().getContextConditionMethod(component.getClassLoader());
				if(mi!=null)
				{
					PlanContextCondition pcc = mi.getMethod(component.getClassLoader()).getAnnotation(PlanContextCondition.class);
					String[] evs = pcc.beliefs();
					RawEvent[] rawevs = pcc.rawevents();
					List<EventType> events = new ArrayList<EventType>();
					for(String ev: evs)
					{
						BDIAgentFeature.addBeliefEvents(component, events, ev);
					}
					for(RawEvent rawev: rawevs)
					{
						events.add(BDIAgentFeature.createEventType(rawev));
					}
				
					IAction<Void> abortplans = new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
							Collection<RPlan> coll = rcapa.getPlans(mplan);
							
							for(final RPlan plan: coll)
							{
								invokeBooleanMethod(plan.getBody().getBody(), mi.getMethod(component.getClassLoader()), plan.getModelElement(), event, plan, component)
									.addResultListener(new IResultListener<Boolean>()
								{
									public void resultAvailable(Boolean result)
									{
										if(!result.booleanValue())
										{
											plan.abort();
										}
									}
									
									public void exceptionOccurred(Exception exception)
									{
									}
								});
							}
							return IFuture.DONE;
						}
					};
					
					Rule<Void> rule = new Rule<Void>("plan_context_abort_"+mplan.getName(), 
						new PlansExistCondition(mplan, rcapa), abortplans);
					rule.setEvents(events);
					rulesystem.getRulebase().addRule(rule);
				}
				else if(mplan.getContextCondition()!=null)
				{
					final MCondition mcond = mplan.getContextCondition();
					
					IAction<Void> abortplans = new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
							Collection<RPlan> coll = rcapa.getPlans(mplan);
							
							for(final RPlan plan: coll)
							{
								if(!evaluateCondition(component, mcond, plan.getModelElement(), SUtil.createHashMap(new String[]{"$plan"}, new Object[]{plan})))
								{
									plan.abort();
								}
							}
							return IFuture.DONE;
						}
					};
					
					Rule<Void> rule = new Rule<Void>("plan_context_condition_"+mplan.getName(), new PlansExistCondition(mplan, rcapa), abortplans);
					rule.setEvents(mcond.getEvents());
					rulesystem.getRulebase().addRule(rule);
				}
			}
			
			// add/rem goal inhibitor rules
			if(!goals.isEmpty())
			{
				boolean	usedelib	= false;
				for(int i=0; !usedelib && i<goals.size(); i++)
				{
					usedelib	= goals.get(i).getDeliberation()!=null;
				}
				
				final IDeliberationStrategy delstr = new EasyDeliberationStrategy();
				delstr.init(component);
				RCapability capa = BDIAgentFeature.getCapability(component);
				capa.setDeliberationStrategy(delstr);

				if(usedelib)
				{
					List<EventType> events = new ArrayList<EventType>();
					events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED, EventType.MATCHALL}));
					Rule<Void> rule = new Rule<Void>("goal_addinitialinhibitors", 
						ICondition.TRUE_CONDITION, new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
							// create the complete inhibitorset for a newly adopted goal
							RGoal goal = (RGoal)event.getContent();
							return delstr.goalIsAdopted(goal);
						}
					});
					rule.setEvents(events);
					rulesystem.getRulebase().addRule(rule);
					
					events = new ArrayList<EventType>();
					events.add(new EventType(new String[]{ChangeEvent.GOALDROPPED, EventType.MATCHALL}));
					rule = new Rule<Void>("goal_removegoalfromdelib", 
						ICondition.TRUE_CONDITION, new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
							// Remove a goal completely from 
							RGoal goal = (RGoal)event.getContent();
							return delstr.goalIsDropped(goal);
						}
					});
					rule.setEvents(events);
					rulesystem.getRulebase().addRule(rule);
					
					events = BDIAgentFeature.getGoalEvents(null);
					rule = new Rule<Void>("goal_addinhibitor", 
						new ICondition()
						{
							public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
							{
								// return true when other goal is active and inprocess
								boolean ret = false;
								EventType type = event.getType();
								RGoal goal = (RGoal)event.getContent();
								ret = ChangeEvent.GOALACTIVE.equals(type.getType(0)) && RGoal.GoalProcessingState.INPROCESS.equals(goal.getProcessingState())
									|| (ChangeEvent.GOALINPROCESS.equals(type.getType(0)) && RGoal.GoalLifecycleState.ACTIVE.equals(goal.getLifecycleState()));
//									return ret? ICondition.TRUE: ICondition.FALSE;
								return new Future<Tuple2<Boolean,Object>>(ret? ICondition.TRUE: ICondition.FALSE);
							}
						}, new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
							RGoal goal = (RGoal)event.getContent();
							return delstr.goalIsActive(goal);
						}
					});
					rule.setEvents(events);
					rulesystem.getRulebase().addRule(rule);
					
					rule = new Rule<Void>("goal_removeinhibitor", 
						new ICondition()
						{
							public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
							{
//								if(getComponentIdentifier().getName().indexOf("Ambu")!=-1)
//									System.out.println("remin");
								
								// return true when other goal is active and inprocess
								boolean ret = false;
								EventType type = event.getType();
								if(event.getContent() instanceof RGoal)
								{
									RGoal goal = (RGoal)event.getContent();
									ret = ChangeEvent.GOALSUSPENDED.equals(type.getType(0)) 
										|| ChangeEvent.GOALOPTION.equals(type.getType(0))
//										|| ChangeEvent.GOALDROPPED.equals(type.getType(0)) 
										|| !RGoal.GoalProcessingState.INPROCESS.equals(goal.getProcessingState());
								}
//									return ret? ICondition.TRUE: ICondition.FALSE;
								return new Future<Tuple2<Boolean,Object>>(ret? ICondition.TRUE: ICondition.FALSE);
							}
						}, new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
							// Remove inhibitions of this goal 
							RGoal goal = (RGoal)event.getContent();
							return delstr.goalIsNotActive(goal);
						}
					});
					rule.setEvents(events);
					rulesystem.getRulebase().addRule(rule);
				}
				
				Rule<Void> rule = new Rule<Void>("goal_activate", 
					new LifecycleStateCondition(RGoal.GoalLifecycleState.OPTION),
					new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
					{
						RGoal goal = (RGoal)event.getContent();
						return delstr.goalIsOption(goal);
					}
				});
//				rule.addEvent(new EventType(new String[]{ChangeEvent.GOALNOTINHIBITED, EventType.MATCHALL}));
				rule.addEvent(new EventType(new String[]{ChangeEvent.GOALOPTION, EventType.MATCHALL}));
//				rule.setEvents(SUtil.createArrayList(new String[]{ChangeEvent.GOALNOTINHIBITED, ChangeEvent.GOALOPTION}));
				rulesystem.getRulebase().addRule(rule);
			}
			
			
			// Init must be set to true before init writes to ensure that new events
			// are executed and not processed as init writes
			IInternalBDILifecycleFeature bdil = (IInternalBDILifecycleFeature)component.getComponentFeature(ILifecycleComponentFeature.class);
			bdil.setInited(true);
			
			// After init rule execution mode to direct
			rulesystem.setQueueEvents(false);
			
//			System.out.println("inited: "+component.getComponentIdentifier());
			
			// perform init write fields (after injection of bdiagent)
			BDIAgentFeature.performInitWrites(component);
			
			// Start rule system
//				if(getComponentIdentifier().getName().indexOf("Cleaner")!=-1)// && getComponentIdentifier().getName().indexOf("Burner")==-1)
//					getCapability().dumpPlansPeriodically(getInternalAccess());
//				if(getComponentIdentifier().getName().indexOf("Ambulance")!=-1)
//				{
//					getCapability().dumpGoalsPeriodically(getInternalAccess());
//					getCapability().dumpPlansPeriodically(getInternalAccess());
//				}
			
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//				}
			
//				throw new RuntimeException();
		}
	}
	
	/**
	 *  Extracted start behavior. 
	 */
	public static class EndBehavior extends LifecycleBehavior
	{
		/**
		 *  Create a new start behavior.
		 */
		public EndBehavior(IInternalAccess component)
		{
			super(component);
		}
		
		/**
		 *  Start the end behavior.
		 *  
		 *  todo: problem with events
		 *  it is unclear how to wait for processing end of internal/end events 
		 *  solution: do not allow posting end events?!
		 */
		public IFuture<Void> startEndBehavior(final IBDIModel bdimodel, final RuleSystem rulesystem, final RCapability rcapa)
		{
			final Future<Void>	ret	= new Future<Void>();
			final IInternalBDIAgentFeature bdif = component.getComponentFeature(IInternalBDIAgentFeature.class);
			
			// Barrier to wait for all body processing.
			FutureBarrier<Void>	bodyend	= new FutureBarrier<Void>();
			
			// Abort running goals.
			Collection<RGoal> goals = bdif.getCapability().getGoals();
//			System.out.println(component.getComponentIdentifier()+" dropping body goals: "+goals);
			for(RGoal goal: goals)
			{
				bodyend.addFuture(goal.drop());
			}
			
			// Abort running plans.
			Collection<RPlan> plans = bdif.getCapability().getPlans();
//			System.out.println(component.getComponentIdentifier()+" dropping body plans: "+plans);
			for(final RPlan plan: plans)
			{
				bodyend.addFuture(plan.abort());
			}
			
			bodyend.waitFor().addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					String confname = component.getConfiguration();
					if(confname!=null)
					{
						MConfiguration mconf = bdimodel.getCapability().getConfiguration(confname);
						
						if(mconf!=null)
						{
							final CounterResultListener<Void> lis = new CounterResultListener<Void>(3, new DelegationResultListener<Void>(ret));
								
							// Create end plans
							final List<MConfigParameterElement> iplans = mconf.getEndPlans();
							dispatchConfigPlans(component, iplans, bdimodel).addResultListener(lis);
							
							// Create end goals
							final List<MConfigParameterElement> igoals = mconf.getEndGoals();
							dispatchConfigGoals(component, igoals, bdimodel).addResultListener(lis);
							
							// Create end events
							final List<MConfigParameterElement> ievents = mconf.getEndEvents();
							dispatchConfigEvents(component, ievents, bdimodel).addResultListener(lis);
						}
						else
						{
							ret.setResult(null);
						}
					}
					else
					{
						ret.setResult(null);
					}
				}
			});
			
			return ret;
		}
	}

}


