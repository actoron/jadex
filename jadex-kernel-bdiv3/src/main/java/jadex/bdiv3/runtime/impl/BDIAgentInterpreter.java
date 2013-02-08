package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.IBDIClassGenerator;
import jadex.bdiv3.PojoBDIAgent;
import jadex.bdiv3.annotation.GoalContextCondition;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.GoalRecurCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MConfiguration;
import jadex.bdiv3.model.MDeliberation;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MTrigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.wrappers.ListWrapper;
import jadex.bdiv3.runtime.wrappers.MapWrapper;
import jadex.bdiv3.runtime.wrappers.SetWrapper;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.IComponentAdapterFactory;
import jadex.commons.FieldInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentInterpreter;
import jadex.micro.MicroModel;
import jadex.micro.annotation.Agent;
import jadex.rules.eca.EventType;
import jadex.rules.eca.IAction;
import jadex.rules.eca.ICondition;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.MethodCondition;
import jadex.rules.eca.Rule;
import jadex.rules.eca.RuleSystem;
import jadex.rules.eca.annotations.CombinedCondition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  The bdi agent interpreter.
 *  Its steps consist of two parts
 *  - execution the activated rules
 *  - executing the enqueued steps
 */
public class BDIAgentInterpreter extends MicroAgentInterpreter
{
	//-------- attributes --------
	
	/** The bdi model. */
	protected BDIModel bdimodel;
	
	/** The rule system. */
	protected RuleSystem rulesystem;
	
	/** The bdi state. */
	protected RCapability capa;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent.
	 */
	public BDIAgentInterpreter(IComponentDescription desc, IComponentAdapterFactory factory, 
		final BDIModel model, Class<?> agentclass, final Map<String, Object> args, final String config, 
		final IExternalAccess parent, RequiredServiceBinding[] bindings, boolean copy, boolean realtime,
		final IIntermediateResultListener<Tuple2<String, Object>> listener, final Future<Void> inited)
	{
		super(desc, factory, model, agentclass, args, config, parent, bindings, copy, realtime, listener, inited);
		this.bdimodel = model;
		this.capa = new RCapability(bdimodel.getCapability());
	}
	
	/**
	 *  Create the agent.
	 */
	protected MicroAgent createAgent(Class<?> agentclass, MicroModel model) throws Exception
	{
		MicroAgent ret;
		
		final Object agent = agentclass.newInstance();
		if(agent instanceof MicroAgent)
		{
			ret = (MicroAgent)agent;
			ret.init(BDIAgentInterpreter.this);
		}
		else // if pojoagent
		{
			PojoBDIAgent pa = new PojoBDIAgent();
			pa.init(this, agent);
			ret = pa;

			FieldInfo[] fields = model.getAgentInjections();
			for(int i=0; i<fields.length; i++)
			{
				try
				{
					Field f = fields[i].getField(getClassLoader());
					f.setAccessible(true);
					f.set(agent, ret);
				}
				catch(Exception e)
				{
					getLogger().warning("Agent injection failed: "+e);
				}
			}
		}
		
		// Additionally inject agent to hidden agent field
		if(!(agent instanceof MicroAgent))
		{
			Class<?> agcl = agent.getClass();
			while(agcl.isAnnotationPresent(Agent.class))
			{
				try
				{
					Field field = agcl.getDeclaredField("__agent");
					field.setAccessible(true);
					field.set(agent, ret);
					agcl = agcl.getSuperclass();
				}
				catch(Exception e)
				{
					getLogger().warning("Hidden agent injection failed: "+e);
					break;
				}
			}
		}
		
		// Init rule system
		this.rulesystem = new RuleSystem(agent);
		
		return ret;
	}
	
	/**
	 *  Start the component behavior.
	 */
	public void startBehavior()
	{
		super.startBehavior();
		
		final Object agent = microagent instanceof PojoBDIAgent? ((PojoBDIAgent)microagent).getPojoAgent(): microagent;
				
		// Init bdi configuration
		String confname = getConfiguration();
		if(confname!=null)
		{
			MConfiguration mconf = bdimodel.getCapability().getConfiguration(confname);
			
			// Set initial belief values
			List<UnparsedExpression> ibels = mconf.getInitialBeliefs();
			if(ibels!=null)
			{
				for(UnparsedExpression uexp: ibels)
				{
					try
					{
						MBelief mbel = bdimodel.getCapability().getBelief(uexp.getName());
						Object val = SJavaParser.parseExpression(uexp, getModel().getAllImports(), getClassLoader());
						mbel.getTarget().getField(getClassLoader()).set(agent, val);
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
			
			// Create initial goals
			List<UnparsedExpression> igoals = mconf.getInitialGoals();
			if(igoals!=null)
			{
				for(UnparsedExpression uexp: igoals)
				{
					MGoal mgoal = null;
					Object goal = null;
					Class<?> gcl = null;
					
					// Create goal if expression available
					if(uexp.getValue()!=null && uexp.getValue().length()>0)
					{
						goal = SJavaParser.parseExpression(uexp, getModel().getAllImports(), getClassLoader());
						gcl = goal.getClass();
					}
					
					if(gcl==null && uexp.getClazz()!=null)
					{
						gcl = uexp.getClazz().getType(getClassLoader(), getModel().getAllImports());
					}
					if(gcl==null)
					{
						// try to fetch via name
						mgoal = bdimodel.getCapability().getGoal(uexp.getName());
						if(mgoal==null && uexp.getName().indexOf(".")==-1)
						{
							// try with package
							mgoal = bdimodel.getCapability().getGoal(getModel().getPackage()+"."+uexp.getName());
						}
						if(mgoal!=null)
						{
							gcl = mgoal.getTargetClass(getClassLoader());
						}
					}
					if(mgoal==null)
					{
						mgoal = bdimodel.getCapability().getGoal(gcl.getName());
					}
					if(goal==null)
					{
						try
						{
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
					
					if(mgoal==null || goal==null)
						throw new RuntimeException("Could not create initial goal: ");
					
					RGoal rgoal = new RGoal(mgoal, goal, null);
					RGoal.adoptGoal(rgoal, getInternalAccess());
				}
			}
			
			// Create initial plans
			List<UnparsedExpression> iplans = mconf.getInitialPlans();
			if(iplans!=null)
			{
				for(UnparsedExpression uexp: iplans)
				{
					MPlan mplan = bdimodel.getCapability().getPlan(uexp.getName());
					// todo: allow Java plan constructor calls
//						Object val = SJavaParser.parseExpression(uexp, model.getModelInfo().getAllImports(), getClassLoader());
				
					RPlan rplan = RPlan.createRPlan(mplan, mplan, null, getInternalAccess());
					RPlan.adoptPlan(rplan, getInternalAccess());
				}
			}
		}
				
		// Inject belief collections.
		List<MBelief> mbels = bdimodel.getCapability().getBeliefs();
		for(MBelief mbel: mbels)
		{
			try
			{
				Field f = mbel.getTarget().getField(getClassLoader());
				f.setAccessible(true);
				Object val = f.get(agent);
				if(val==null)
				{
					String impl = mbel.getImplClassName();
					if(impl!=null)
					{
						Class<?> implcl = SReflect.findClass(impl, null, getClassLoader());
						val = implcl.newInstance();
					}
					else
					{
						Class<?> cl = f.getType();
						if(SReflect.isSupertype(List.class, cl))
						{
							val = new ArrayList();
						}
						else if(SReflect.isSupertype(Set.class, cl))
						{
							val = new HashSet();
						}
						else if(SReflect.isSupertype(Map.class, cl))
						{
							val = new HashMap();
						}
					}
				}
				if(val instanceof List)
				{
					String bname = mbel.getName();
					f.set(agent, new ListWrapper((List<?>)val, rulesystem, ChangeEvent.FACTADDED+"."+bname, ChangeEvent.FACTREMOVED+"."+bname, ChangeEvent.FACTCHANGED+"."+bname));
				}
				else if(val instanceof Set)
				{
					String bname = mbel.getName();
					f.set(agent, new SetWrapper((Set<?>)val, rulesystem, ChangeEvent.FACTADDED+"."+bname, ChangeEvent.FACTREMOVED+"."+bname, ChangeEvent.FACTCHANGED+"."+bname));
				}
				else if(val instanceof Map)
				{
					String bname = mbel.getName();
					f.set(agent, new MapWrapper((Map<?,?>)val, rulesystem, ChangeEvent.FACTADDED+"."+bname, ChangeEvent.FACTREMOVED+"."+bname, ChangeEvent.FACTCHANGED+"."+bname));
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

		// Observe dynamic beliefs
		List<MBelief> beliefs = bdimodel.getCapability().getBeliefs();
		
		for(final MBelief mbel: beliefs)
		{
			Collection<String> evs = mbel.getEvents();
			if(evs!=null && !evs.isEmpty())
			{
				List<EventType> events = new ArrayList<EventType>();
				for(String ev: evs)
				{
					addBeliefEvents(getInternalAccess(), events, ev);
				}
				
				Rule<Void> rule = new Rule<Void>(mbel.getName()+"_belief_update", 
					ICondition.TRUE_CONDITION, new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
					{
//						System.out.println("belief update: "+event);
						try
						{
							Method um = agent.getClass().getMethod(IBDIClassGenerator.DYNAMIC_BELIEF_UPDATEMETHOD_PREFIX+SUtil.firstToUpperCase(mbel.getName()), new Class[0]);
							um.invoke(agent, new Object[0]);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						return IFuture.DONE;
					}
				});
				rule.setEvents(events);
				getRuleSystem().getRulebase().addRule(rule);
			}
		}
		
		// Observe goal types
		List<MGoal> goals = bdimodel.getCapability().getGoals();
		for(final MGoal mgoal: goals)
		{
//			 todo: explicit bdi creation rule
//			rulesystem.observeObject(goals.get(i).getTargetClass(getClassLoader()));
		
//			boolean fin = false;
			
			final Class<?> gcl = mgoal.getTargetClass(getClassLoader());
			boolean declarative = false;
			boolean maintain = false;
			
			Constructor<?>[] cons = gcl.getConstructors();
			for(final Constructor<?> c: cons)
			{
				if(c.isAnnotationPresent(GoalCreationCondition.class))
				{
					String[] evs = c.getAnnotation(GoalCreationCondition.class).events();
					List<EventType> events = readAnnotationEvents(getInternalAccess(), c.getParameterAnnotations());
					for(String ev: evs)
					{
						addBeliefEvents(getInternalAccess(), events, ev);
					}
				
					Rule<Void> rule = new Rule<Void>(mgoal.getName()+"_goal_create", 
						ICondition.TRUE_CONDITION, new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
						{
//							System.out.println("create: "+context);
							
							Object pojogoal = null;
							try
							{
								Class<?>[] ptypes = c.getParameterTypes();
								Object[] pvals = new Object[ptypes.length];
								
								for(int i=0; i<ptypes.length; i++)
								{
									if(event.getContent()!=null && SReflect.isSupertype(event.getContent().getClass(), ptypes[i]))
									{
										pvals[i] = event.getContent();
									}
									else if(SReflect.isSupertype(agent.getClass(), ptypes[i]))
									{
										pvals[i] = agent;
									}
								}
								
								pojogoal = c.newInstance(pvals);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
							
							if(!getCapability().containsGoal(pojogoal))
							{
								final Object fpojogoal = pojogoal;
								((BDIAgent)microagent).dispatchTopLevelGoal(pojogoal)
									.addResultListener(new IResultListener<Object>()
								{
									public void resultAvailable(Object result)
									{
										getLogger().info("Goal succeeded: "+result);
									}
									
									public void exceptionOccurred(Exception exception)
									{
										getLogger().info("Goal failed: "+fpojogoal+" "+exception);
									}
								});
							}
							else
							{
								System.out.println("new goal not adopted, already contained: "+pojogoal);
							}
							
							return IFuture.DONE;
						}
					});
					rule.setEvents(events);
					getRuleSystem().getRulebase().addRule(rule);
				}
			}	
			
			Method mcond = null;
			Method[] ms = gcl.getDeclaredMethods();
			for(final Method m: ms)
			{
				if(m.isAnnotationPresent(GoalCreationCondition.class))
				{
					List<EventType> events = readAnnotationEvents(getInternalAccess(), m.getParameterAnnotations());
					Rule<Void> rule = new Rule<Void>(mgoal.getName()+"_goal_create", 
						new MethodCondition(null, m), new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
						{
//							System.out.println("create: "+context);
							
							Object pojogoal = null;
							if(event.getContent()!=null)
							{
								try
								{
									Class<?> evcl = event.getContent().getClass();
									Constructor<?> c = gcl.getConstructor(new Class[]{evcl});
									pojogoal = c.newInstance(new Object[]{event.getContent()});
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
							}
							else
							{
								// todo:
//								Constructor<?>[] cons = gcl.getConstructors();
//								for(Constructor c: cons)
//								{
//								}
								throw new RuntimeException("Unknown how to create goal: "+gcl);
							}
							
							((BDIAgent)microagent).dispatchTopLevelGoal(pojogoal);
							
							return IFuture.DONE;
						}
					});
					rule.setEvents(events);
					getRuleSystem().getRulebase().addRule(rule);
				}
				
				if(m.isAnnotationPresent(GoalMaintainCondition.class))
					mcond = m; // do later
				
				if(m.isAnnotationPresent(GoalTargetCondition.class))
				{			
					String[] evs = m.getAnnotation(GoalTargetCondition.class).events();
					List<EventType> events = readAnnotationEvents(getInternalAccess(), m.getParameterAnnotations());
					for(String ev: evs)
					{
						addBeliefEvents(getInternalAccess(), events, ev);
					}
					events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED})); // check also initially 
					
					Rule<?> rule = new Rule<Void>(mgoal.getName()+"_goal_target", 
						new CombinedCondition(new ICondition[]{
							new GoalsExistCondition(mgoal, capa)
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
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
						{
							for(RGoal goal: getCapability().getGoals(mgoal))
							{
								if(executeGoalMethod(m, goal, event))
									goal.targetConditionTriggered(getInternalAccess(), event, rule, context);
							}
						
							return IFuture.DONE;
						}
					});
					rule.setEvents(events);
					getRuleSystem().getRulebase().addRule(rule);
					declarative = true;
				}
				
				if(m.isAnnotationPresent(GoalDropCondition.class))
				{			
					String[] evs = m.getAnnotation(GoalDropCondition.class).events();
					List<EventType> events = readAnnotationEvents(getInternalAccess(), m.getParameterAnnotations());
					for(String ev: evs)
					{
						addBeliefEvents(getInternalAccess(), events, ev);
					}
					Rule<?> rule = new Rule<Void>(mgoal.getName()+"_goal_drop", 
						new GoalsExistCondition(mgoal, capa), new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
						{
							for(RGoal goal: getCapability().getGoals(mgoal))
							{
								if(executeGoalMethod(m, goal, event))
								{
//									System.out.println("Goal dropping triggered: "+goal);
//									rgoal.setLifecycleState(BDIAgent.this, rgoal.GOALLIFECYCLESTATE_DROPPING);
									if(!goal.isFinished())
									{
										goal.setException(new GoalFailureException("drop condition: "+m.getName()));
										goal.setProcessingState(getInternalAccess(), RGoal.GOALPROCESSINGSTATE_FAILED);
									}
								}
							}
							
							return IFuture.DONE;
						}
					});
					rule.setEvents(events);
					getRuleSystem().getRulebase().addRule(rule);
				}
				
				if(m.isAnnotationPresent(GoalContextCondition.class))
				{			
					String[] evs = m.getAnnotation(GoalContextCondition.class).events();
					List<EventType> events = readAnnotationEvents(getInternalAccess(), m.getParameterAnnotations());
					for(String ev: evs)
					{
						addBeliefEvents(getInternalAccess(), events, ev);
					}
					events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED})); // check state for initial goals
					
					Rule<?> rule = new Rule<Void>(mgoal.getName()+"_goal_suspend", 
						new GoalsExistCondition(mgoal, capa), new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
						{
							for(RGoal goal: getCapability().getGoals(mgoal))
							{
								if(!RGoal.GOALLIFECYCLESTATE_SUSPENDED.equals(goal.getLifecycleState()))
								{	
									if(!executeGoalMethod(m, goal, event))
									{
										if(goal.getMGoal().getName().indexOf("AchieveCleanup")!=-1)
											System.out.println("Goal suspended: "+goal);
										goal.setLifecycleState(getInternalAccess(), RGoal.GOALLIFECYCLESTATE_SUSPENDED);
										goal.setState(RGoal.PROCESSABLEELEMENT_INITIAL);
									}
								}
							}
							return IFuture.DONE;
						}
					});
					rule.setEvents(events);
					getRuleSystem().getRulebase().addRule(rule);
					
					rule = new Rule<Void>(mgoal.getName()+"_goal_option", 
						new GoalsExistCondition(mgoal, capa), new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
						{
							for(RGoal goal: getCapability().getGoals(mgoal))
							{
								if(RGoal.GOALLIFECYCLESTATE_SUSPENDED.equals(goal.getLifecycleState()))
								{	
									if(executeGoalMethod(m, goal, event))
									{
//										if(goal.getMGoal().getName().indexOf("AchieveCleanup")!=-1)
//											System.out.println("Goal made option: "+goal);
										goal.setLifecycleState(getInternalAccess(), RGoal.GOALLIFECYCLESTATE_OPTION);
//										setState(ia, PROCESSABLEELEMENT_INITIAL);
									}
								}
							}
							
							return IFuture.DONE;
						}
					});
					rule.setEvents(events);
					getRuleSystem().getRulebase().addRule(rule);
				}
				
				if(m.isAnnotationPresent(GoalRecurCondition.class))
				{			
					String[] evs = m.getAnnotation(GoalRecurCondition.class).events();
					List<EventType> events = readAnnotationEvents(getInternalAccess(), m.getParameterAnnotations());
					for(String ev: evs)
					{
						addBeliefEvents(getInternalAccess(), events, ev);
					}
					Rule<?> rule = new Rule<Void>(mgoal.getName()+"_goal_recur",
						new GoalsExistCondition(mgoal, capa), new IAction<Void>()
//						new CombinedCondition(new ICondition[]{
//							new LifecycleStateCondition(GOALLIFECYCLESTATE_ACTIVE),
//							new ProcessingStateCondition(GOALPROCESSINGSTATE_PAUSED),
//							new MethodCondition(getPojoElement(), m),
//						}), new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
						{
							for(RGoal goal: getCapability().getGoals(mgoal))
							{
								if(RGoal.GOALLIFECYCLESTATE_ACTIVE.equals(goal.getLifecycleState())
									&& RGoal.GOALPROCESSINGSTATE_PAUSED.equals(goal.getProcessingState()))
								{	
									if(executeGoalMethod(m, goal, event))
									{
										goal.setTriedPlans(null);
										goal.setApplicablePlanList(null);
										goal.setProcessingState(getInternalAccess(), RGoal.GOALPROCESSINGSTATE_INPROCESS);
									}
								}
							}
							return IFuture.DONE;
						}
					});
					rule.setEvents(events);
					getRuleSystem().getRulebase().addRule(rule);
					declarative = true;
				}
			}
			
			if(mcond!=null)
			{		
				final Method m = mcond;
				
				String[] evs = mcond.getAnnotation(GoalMaintainCondition.class).events();
				List<EventType> events = readAnnotationEvents(getInternalAccess(), mcond.getParameterAnnotations());
				for(String ev: evs)
				{
					addBeliefEvents(getInternalAccess(), events, ev);
				}
				
				Rule<?> rule = new Rule<Void>(mgoal.getName()+"_goal_maintain", 
					new GoalsExistCondition(mgoal, capa), new IAction<Void>()
//					new CombinedCondition(new ICondition[]{
//						new LifecycleStateCondition(GOALLIFECYCLESTATE_ACTIVE),
//						new ProcessingStateCondition(GOALPROCESSINGSTATE_IDLE),
//						new MethodCondition(getPojoElement(), mcond, true),
//					}), new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
					{
						for(RGoal goal: getCapability().getGoals(mgoal))
						{
							if(RGoal.GOALLIFECYCLESTATE_ACTIVE.equals(goal.getLifecycleState())
								&& RGoal.GOALPROCESSINGSTATE_IDLE.equals(goal.getProcessingState()))
							{	
								if(!executeGoalMethod(m, goal, event))
								{
//									System.out.println("Goal maintain triggered: "+goal);
//									System.out.println("state was: "+getProcessingState());
									goal.setProcessingState(getInternalAccess(), RGoal.GOALPROCESSINGSTATE_INPROCESS);
								}
							}
						}
						return IFuture.DONE;
					}
				});
				rule.setEvents(events);
				getRuleSystem().getRulebase().addRule(rule);
				
				// if has no own target condition
				if(!declarative)
				{
					// if not has own target condition use the maintain cond
					rule = new Rule<Void>(mgoal.getName()+"_goal_target", 
						new GoalsExistCondition(mgoal, capa), new IAction<Void>()
//						new MethodCondition(getPojoElement(), mcond), new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
						{
							for(RGoal goal: getCapability().getGoals(mgoal))
							{
								if(executeGoalMethod(m, goal, event))
									goal.targetConditionTriggered(getInternalAccess(), event, rule, context);
							}
							
							return IFuture.DONE;
						}
					});
					rule.setEvents(events);
					getRuleSystem().getRulebase().addRule(rule);
				}

				declarative = true;
				maintain = true;
			}
			
			// todo: do not write from instance level!
			mgoal.setDeclarative(declarative);
			mgoal.setMaintain(maintain);
		}
		
		// Observe plan types
		List<MPlan> mplans = bdimodel.getCapability().getPlans();
		for(int i=0; i<mplans.size(); i++)
		{
			final MPlan mplan = mplans.get(i);
			
			IAction<Void> createplan = new IAction<Void>()
			{
				public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
				{
					RPlan rplan = RPlan.createRPlan(mplan, mplan, new ChangeEvent(event), getInternalAccess());
					RPlan.adoptPlan(rplan, getInternalAccess());
					return IFuture.DONE;
				}
			};
			
			MTrigger trigger = mplan.getTrigger();
			
			if(trigger!=null)
			{
				List<String> fas = trigger.getFactAddeds();
				if(fas!=null && fas.size()>0)
				{
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
			
			if(usedelib)
			{
				List<EventType> events = new ArrayList<EventType>();
				events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED}));
				Rule<Void> rule = new Rule<Void>("goal_addinitialinhibitors", 
					ICondition.TRUE_CONDITION, new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
					{
						// create the complete inhibitorset for a newly adopted goal
						
						RGoal goal = (RGoal)event.getContent();
						for(RGoal other: getCapability().getGoals())
						{
	//						if(other.getLifecycleState().equals(RGoal.GOALLIFECYCLESTATE_ACTIVE) 
	//							&& other.getProcessingState().equals(RGoal.GOALPROCESSINGSTATE_INPROCESS)
							if(other.inhibits(goal, getInternalAccess()))
							{
								goal.addInhibitor(other, getInternalAccess());
							}
						}
						
	//					if(goal.inhibitors!=null && goal.inhibitors.size()>0)
	//						System.out.println("initial inhibitors of: "+goal+" "+goal.inhibitors);
						return IFuture.DONE;
					}
				});
				rule.setEvents(events);
				getRuleSystem().getRulebase().addRule(rule);
				
				events = getGoalEvents();
				rule = new Rule<Void>("goal_addinhibitor", 
					new ICondition()
					{
						public boolean evaluate(IEvent event)
						{
	//						if(((RGoal)event.getContent()).getId().indexOf("Battery")!=-1)
	//							System.out.println("maintain");
							
							// return true when other goal is active and inprocess
							boolean ret = false;
							EventType type = event.getType();
							RGoal goal = (RGoal)event.getContent();
							ret = ChangeEvent.GOALACTIVE.equals(type.getType(0)) && RGoal.GOALPROCESSINGSTATE_INPROCESS.equals(goal.getProcessingState())
								|| (ChangeEvent.GOALINPROCESS.equals(type.getType(0)) && RGoal.GOALLIFECYCLESTATE_ACTIVE.equals(goal.getLifecycleState()));
							return ret;
						}
					}, new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
					{
						RGoal goal = (RGoal)event.getContent();
//						if(goal.getId().indexOf("PerformPatrol")!=-1)
//							System.out.println("addinh: "+goal);
						MDeliberation delib = goal.getMGoal().getDeliberation();
						if(delib!=null)
						{
							Set<MGoal> inhs = delib.getInhibitions();
							for(MGoal inh: inhs)
							{
								Collection<RGoal> goals = getCapability().getGoals(inh);
								for(RGoal other: goals)
								{
									if(!other.isInhibitedBy(goal) && goal.inhibits(other, getInternalAccess()))
									{
										other.addInhibitor(goal, getInternalAccess());
									}
								}
							}
						}
						return IFuture.DONE;
					}
				});
				rule.setEvents(events);
				getRuleSystem().getRulebase().addRule(rule);
				
				rule = new Rule<Void>("goal_removeinhibitor", 
					new ICondition()
					{
						public boolean evaluate(IEvent event)
						{
							// return true when other goal is active and inprocess
							boolean ret = false;
							EventType type = event.getType();
							if(event.getContent() instanceof RGoal)
							{
								RGoal goal = (RGoal)event.getContent();
								ret = ChangeEvent.GOALSUSPENDED.equals(type.getType(0)) || ChangeEvent.GOALOPTION.equals(type.getType(0))
									|| !RGoal.GOALPROCESSINGSTATE_INPROCESS.equals(goal.getProcessingState());
							}
							return ret;
						}
					}, new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
					{
						// Remove inhibitions of this goal 
						RGoal goal = (RGoal)event.getContent();
						MDeliberation delib = goal.getMGoal().getDeliberation();
						if(delib!=null)
						{
							Set<MGoal> inhs = delib.getInhibitions();
							for(MGoal inh: inhs)
							{
	//							if(goal.getId().indexOf("AchieveCleanup")!=-1)
	//								System.out.println("reminh: "+goal);
								Collection<RGoal> goals = getCapability().getGoals(inh);
								for(RGoal other: goals)
								{
									if(goal.equals(other))
										continue;
									
									if(other.isInhibitedBy(goal))
										other.removeInhibitor(goal, getInternalAccess());
								}
							}
						}
						return IFuture.DONE;
					}
				});
				rule.setEvents(events);
				getRuleSystem().getRulebase().addRule(rule);
				
				
				rule = new Rule<Void>("goal_inhibit", 
					new LifecycleStateCondition(RGoal.GOALLIFECYCLESTATE_ACTIVE), new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
					{
						RGoal goal = (RGoal)event.getContent();
	//					System.out.println("optionizing: "+goal+" "+goal.inhibitors);
						goal.setLifecycleState(getInternalAccess(), RGoal.GOALLIFECYCLESTATE_OPTION);
						return IFuture.DONE;
					}
				});
				rule.addEvent(new EventType(new String[]{ChangeEvent.GOALINHIBITED}));
				getRuleSystem().getRulebase().addRule(rule);
			}
			
			Rule<Void> rule = new Rule<Void>("goal_activate", 
				new CombinedCondition(new ICondition[]{
					new LifecycleStateCondition(RGoal.GOALLIFECYCLESTATE_OPTION),
					new ICondition()
					{
						public boolean evaluate(IEvent event)
						{
							RGoal goal = (RGoal)event.getContent();
							return !goal.isInhibited();
						}
					}
				}), new IAction<Void>()
			{
				public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
				{
					RGoal goal = (RGoal)event.getContent();
//					if(goal.getMGoal().getName().indexOf("AchieveCleanup")!=-1)
//						System.out.println("reactivating: "+goal);
					goal.setLifecycleState(getInternalAccess(), RGoal.GOALLIFECYCLESTATE_ACTIVE);
					return IFuture.DONE;
				}
			});
			rule.addEvent(new EventType(new String[]{ChangeEvent.GOALNOTINHIBITED}));
			rule.addEvent(new EventType(new String[]{ChangeEvent.GOALOPTION}));
//			rule.setEvents(SUtil.createArrayList(new String[]{ChangeEvent.GOALNOTINHIBITED, ChangeEvent.GOALOPTION}));
			getRuleSystem().getRulebase().addRule(rule);
		}
		
		// perform init write fields (after injection of bdiagent)
		BDIAgent.performInitWrites((BDIAgent)microagent);
	
//		getCapability().dumpGoalsPeriodically(getInternalAccess());
//		getCapability().dumpPlansPeriodically(getInternalAccess());
	}
	
	/**
	 *  Execute a goal method.
	 */
	protected boolean executeGoalMethod(Method m, RGoal goal, IEvent event)
	{
		try
		{
			m.setAccessible(true);
			Object result = null;
			if(m.getParameterTypes().length==0)
				result = m.invoke(goal.getPojoElement(), m.getParameterTypes().length==0? new Object[0]: new Object[]{event.getContent()});
			return ((Boolean)result).booleanValue();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 *  Can be called on the agent thread only.
	 * 
	 *  Main method to perform agent execution.
	 *  Whenever this method is called, the agent performs
	 *  one of its scheduled actions.
	 *  The platform can provide different execution models for agents
	 *  (e.g. thread based, or synchronous).
	 *  To avoid idle waiting, the return value can be checked.
	 *  The platform guarantees that executeAction() will not be called in parallel. 
	 *  @return True, when there are more actions waiting to be executed. 
	 */
	public boolean executeStep()
	{
		assert isComponentThread();
		
		// Evaluate condition before executing step.
		boolean aborted = false;
//		if(rulesystem!=null)
//			aborted = rulesystem.processAllEvents(15);
//		if(aborted)
//			getCapability().dumpGoals();
		
		if(rulesystem!=null)
			rulesystem.processAllEvents();
		
//		if(steps!=null && steps.size()>0)
//		{
//			System.out.println("steps: "+steps.size()+" "+((Object[])steps.get(0))[0]);
//		}
		boolean ret = super.executeStep();

		return ret || (rulesystem!=null && rulesystem.isEventAvailable());
	}
	
	/**
	 *  Get the rulesystem.
	 *  @return The rulesystem.
	 */
	public RuleSystem getRuleSystem()
	{
		return rulesystem;
	}
	
	/**
	 *  Get the bdimodel.
	 *  @return the bdimodel.
	 */
	public BDIModel getBDIModel()
	{
		return bdimodel;
	}
	
	/**
	 *  Get the state.
	 *  @return the state.
	 */
	public RCapability getCapability()
	{
		return capa;
	}
	
	/**
	 *  Create belief events from a belief name.
	 *  For normal beliefs 
	 *  beliefchanged.belname and factchanged.belname 
	 *  and for multi beliefs additionally
	 *  factadded.belname and factremoved 
	 *  are created.
	 */
	public static void addBeliefEvents(IInternalAccess ia, List<EventType> events, String belname)
	{
		events.add(new EventType(new String[]{ChangeEvent.BELIEFCHANGED, belname})); // the whole value was changed
		events.add(new EventType(new String[]{ChangeEvent.FACTCHANGED, belname})); // property change of a value
		
		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
		MBelief mbel = ((MCapability)ip.getCapability().getModelElement()).getBelief(belname);
		if(mbel!=null && mbel.isMulti(ia.getClassLoader()))
		{
			events.add(new EventType(new String[]{ChangeEvent.FACTADDED, belname}));
			events.add(new EventType(new String[]{ChangeEvent.FACTREMOVED, belname}));
		}
	}
	
	/**
	 *  Create goal events for a goal name. creates
	 *  goaladopted, goaldropped
	 *  goaloption, goalactive, goalsuspended
	 *  goalinprocess, goalnotinprocess
	 *  events.
	 */
	public static List<EventType> getGoalEvents()
	{
		List<EventType> events = new ArrayList<EventType>();
		events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED}));
		events.add(new EventType(new String[]{ChangeEvent.GOALDROPPED}));
		
		events.add(new EventType(new String[]{ChangeEvent.GOALOPTION}));
		events.add(new EventType(new String[]{ChangeEvent.GOALACTIVE}));
		events.add(new EventType(new String[]{ChangeEvent.GOALSUSPENDED}));
		
		events.add(new EventType(new String[]{ChangeEvent.GOALINPROCESS}));
		events.add(new EventType(new String[]{ChangeEvent.GOALNOTINPROCESS}));
		return events;
	}
	
	/**
	 *  Read the annotation events from method annotations.
	 */
	public static List<EventType> readAnnotationEvents(IInternalAccess ia, Annotation[][] annos)
	{
		List<EventType> events = new ArrayList<EventType>();
		for(Annotation[] ana: annos)
		{
			for(Annotation an: ana)
			{
				if(an instanceof jadex.rules.eca.annotations.Event)
				{
					String belname = ((jadex.rules.eca.annotations.Event)an).value();
					addBeliefEvents(ia, events, belname);
				}
			}
		}
		return events;
	}
	
	/**
	 *  Condition for checking the lifecycle state of a goal.
	 */
	public static class LifecycleStateCondition implements ICondition
	{
		/** The allowed states. */
		protected Set<String> states;
		
		/** The flag if state is allowed or disallowed. */
		protected boolean allowed;
		
		/**
		 *  Create a new condition.
		 */
		public LifecycleStateCondition(String state)
		{
			this(SUtil.createHashSet(new String[]{state}));
		}
		
		/**
		 *  Create a new condition.
		 */
		public LifecycleStateCondition(Set<String> states)
		{
			this(states, true);
		}
		
		/**
		 *  Create a new condition.
		 */
		public LifecycleStateCondition(String state, boolean allowed)
		{
			this(SUtil.createHashSet(new String[]{state}), allowed);
		}
		
		/**
		 *  Create a new condition.
		 */
		public LifecycleStateCondition(Set<String> states, boolean allowed)
		{
			this.states = states;
			this.allowed = allowed;
		}
		
		/**
		 *  Evaluate the condition.
		 */
		public boolean evaluate(IEvent event)
		{
			RGoal goal = (RGoal)event.getContent();
			boolean ret = states.contains(goal.getLifecycleState());
			if(!allowed)
				ret = !ret;
			return ret;
		}
	}
	
	/**
	 *  Condition that tests if goal instances of an mgoal exist.
	 */
	public static class GoalsExistCondition implements ICondition
	{
		protected MGoal mgoal;
		
		protected RCapability capa;
		
		public GoalsExistCondition(MGoal mgoal, RCapability capa)
		{
			this.mgoal = mgoal;
			this.capa = capa;
		}
		
		/**
		 * 
		 */
		public boolean evaluate(IEvent event)
		{
			return !capa.getGoals(mgoal).isEmpty();
		}
	}
}