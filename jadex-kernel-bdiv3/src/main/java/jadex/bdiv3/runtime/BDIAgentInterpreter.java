package jadex.bdiv3.runtime;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.PojoBDIAgent;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MConfiguration;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MTrigger;
import jadex.bdiv3.runtime.wrappers.ListWrapper;
import jadex.bdiv3.runtime.wrappers.MapWrapper;
import jadex.bdiv3.runtime.wrappers.SetWrapper;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.IComponentAdapterFactory;
import jadex.commons.FieldInfo;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentInterpreter;
import jadex.micro.MicroModel;
import jadex.rules.eca.IAction;
import jadex.rules.eca.ICondition;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.MethodCondition;
import jadex.rules.eca.Rule;
import jadex.rules.eca.RuleSystem;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class BDIAgentInterpreter extends MicroAgentInterpreter
{
	/** The bdi model. */
	protected BDIModel bdimodel;
	
	/** The rule system. */
	protected RuleSystem rulesystem;
	
	/** The bdi state. */
	protected RCapability capa;
	
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
//				if(fields[i].isAnnotationPresent(Agent.class))
//				{
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
//				}
			}
		}
		
		// Additionally inject agent to hidden agent field
		if(!(agent instanceof MicroAgent))
		{
			try
			{
				// todo: cannot use fields as they are from the 'not enhanced' class
				Field field = agent.getClass().getDeclaredField("__agent");
				field.setAccessible(true);
				field.set(agent, ret);
			}
			catch(Exception e)
			{
				getLogger().warning("Hidden agent injection failed: "+e);
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
//							Object val = SJavaParser.parseExpression(uexp, model.getModelInfo().getAllImports(), getClassLoader());
				
					RPlan rplan = RPlan.createRPlan(mplan, null, getInternalAccess());
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

		// Observe goal types
		List<MGoal> goals = bdimodel.getCapability().getGoals();
		for(int i=0; i<goals.size(); i++)
		{
//			 todo: explicit bdi creation rule
//			rulesystem.observeObject(goals.get(i).getTargetClass(getClassLoader()));
		
			boolean fin = false;
			
			MGoal mgoal = goals.get(i);
			final Class<?> gcl = mgoal.getTargetClass(getClassLoader());
			
			Constructor<?>[] cons = gcl.getConstructors();
			for(final Constructor<?> c: cons)
			{
				if(c.isAnnotationPresent(GoalCreationCondition.class))
				{
					String[] evs = c.getAnnotation(GoalCreationCondition.class).events();
					List<String> events = RGoal.readAnnotationEvents(getInternalAccess(), c.getParameterAnnotations());
					for(String ev: evs)
					{
						RGoal.addBeliefEvents(getInternalAccess(), events, ev);
					}
				
					Rule<Void> rule = new Rule<Void>(mgoal.getName()+"_goal_create", 
						ICondition.TRUE_CONDITION, new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
						{
							System.out.println("create: "+context);
							
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
							
							return IFuture.DONE;
						}
					});
					rule.setEvents(events);
					getRuleSystem().getRulebase().addRule(rule);
				}
			}	
			
			Method[] ms = gcl.getDeclaredMethods();
			for(Method m: ms)
			{
				if(m.isAnnotationPresent(GoalCreationCondition.class))
				{
					List<String> events = RGoal.readAnnotationEvents(getInternalAccess(), m.getParameterAnnotations());
					Rule<Void> rule = new Rule<Void>(mgoal.getName()+"_goal_create", 
						new MethodCondition(null, m), new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
						{
							System.out.println("create: "+context);
							
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
			}
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
					int idx = event.getType().indexOf(".");
					String evtype = event.getType().substring(0, idx);
					String belname = event.getType().substring(idx+1);
					RPlan rplan = RPlan.createRPlan(mplan, new ChangeEvent(evtype, belname, event.getContent()), getInternalAccess());
					RPlan.adoptPlan(rplan, getInternalAccess());
					return IFuture.DONE;
				}
			};
			
			MTrigger trigger = mplan.getTrigger();
			
			List<String> fas = trigger.getFactAddeds();
			if(fas!=null && fas.size()>0)
			{
				Rule<Void> rule = new Rule<Void>("create_plan_factadded_"+mplan.getName(), ICondition.TRUE_CONDITION, createplan);
				for(String fa: fas)
				{
					rule.addEvent(ChangeEvent.FACTADDED+"."+fa);
				}
				rulesystem.getRulebase().addRule(rule);
			}

			List<String> frs = trigger.getFactRemoveds();
			if(frs!=null && frs.size()>0)
			{
				Rule<Void> rule = new Rule<Void>("create_plan_factremoved_"+mplan.getName(), ICondition.TRUE_CONDITION, createplan);
				for(String fr: frs)
				{
					rule.addEvent(ChangeEvent.FACTREMOVED+"."+fr);
				}
				rulesystem.getRulebase().addRule(rule);
			}
			
			List<String> fcs = trigger.getFactChangeds();
			if(fcs!=null && fcs.size()>0)
			{
				Rule<Void> rule = new Rule<Void>("create_plan_factchanged_"+mplan.getName(), ICondition.TRUE_CONDITION, createplan);
				for(String fc: fcs)
				{
					rule.addEvent(ChangeEvent.FACTCHANGED+"."+fc);
					rule.addEvent(ChangeEvent.BELIEFCHANGED+"."+fc);
				}
				rulesystem.getRulebase().addRule(rule);
			}
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
		// Evaluate condition before executing step.
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
}