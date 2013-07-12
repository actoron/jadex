package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.IBDIClassGenerator;
import jadex.bdiv3.PojoBDIAgent;
import jadex.bdiv3.actions.ExecutePlanStepAction;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.PlanContextCondition;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MCondition;
import jadex.bdiv3.model.MConfiguration;
import jadex.bdiv3.model.MDeliberation;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MTrigger;
import jadex.bdiv3.model.MethodInfo;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.ICapability;
import jadex.bdiv3.runtime.impl.RGoal.GoalLifecycleState;
import jadex.bdiv3.runtime.impl.RPlan.PlanLifecycleState;
import jadex.bdiv3.runtime.impl.RPlan.PlanProcessingState;
import jadex.bdiv3.runtime.impl.RPlan.ResumeCommand;
import jadex.bdiv3.runtime.wrappers.ListWrapper;
import jadex.bdiv3.runtime.wrappers.MapWrapper;
import jadex.bdiv3.runtime.wrappers.SetWrapper;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.ComponentSuspendable;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.IComponentAdapterFactory;
import jadex.commons.FieldInfo;
import jadex.commons.IMethodParameterGuesser;
import jadex.commons.IResultCommand;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.SimpleMethodParameterGuesser;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;
import jadex.micro.IPojoMicroAgent;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *  The bdi agent interpreter.
 *  Its steps consist of two parts
 *  - execution the activated rules
 *  - executing the enqueued steps
 */
public class BDIAgentInterpreter extends MicroAgentInterpreter
{
	//-------- constants --------
	
	/** The capability separator. */
	public static String	CAPABILITY_SEPARATOR	= "/";
	
	//-------- attributes --------
	
	/** The bdi model. */
	protected BDIModel bdimodel;
	
	/** The rule system. */
	protected RuleSystem rulesystem;
	
	/** The bdi state. */
	protected RCapability capa;
	
	/** Is the agent inited and allowed to execute rules? */
	protected boolean	inited;
	
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

			injectAgent(pa, agent, model, null);
		}
		
		// Init rule system
		this.rulesystem = new RuleSystem(agent);
		
		return ret;
	}
	
	/**
	 *  Inject the agent into annotated fields.
	 */
	protected void	injectAgent(BDIAgent pa, Object agent, MicroModel model, String globalname)
	{
		FieldInfo[] fields = model.getAgentInjections();
		for(int i=0; i<fields.length; i++)
		{
			try
			{
				Field f = fields[i].getField(getClassLoader());
				if(SReflect.isSupertype(f.getType(), ICapability.class))
				{
					f.setAccessible(true);
					f.set(agent, new CapabilityWrapper(pa, agent, globalname));						
				}
				else
				{
					f.setAccessible(true);
					f.set(agent, pa);
				}
			}
			catch(Exception e)
			{
				getLogger().warning("Agent injection failed: "+e);
			}
		}
	
		// Additionally inject hidden agent fields
		Class<?> agcl = agent.getClass();
		while(agcl.isAnnotationPresent(Agent.class)
			|| agcl.isAnnotationPresent(Capability.class))
		{
			try
			{
				Field field = agcl.getDeclaredField("__agent");
				field.setAccessible(true);
				field.set(agent, pa);
				
				field = agcl.getDeclaredField("__globalname");
				field.setAccessible(true);
				field.set(agent, globalname);
				agcl = agcl.getSuperclass();

			}
			catch(Exception e)
			{
				getLogger().warning("Hidden agent injection failed: "+e);
				break;
			}
		}
	}
	
	/**
	 *  Get a capability pojo object.
	 */
	public Object	getCapabilityObject(String name)
	{
		Object	ret	= ((PojoBDIAgent)microagent).getPojoAgent();
		if(name!=null)
		{
			StringTokenizer	stok	= new StringTokenizer(name, CAPABILITY_SEPARATOR);
			while(stok.hasMoreTokens())
			{
				name	= stok.nextToken();
				
				boolean found = false;
				Class<?> cl = ret.getClass();
				while(!found & !Object.class.equals(cl))
				{
					try
					{
						Field	f	= cl.getDeclaredField(name);
						f.setAccessible(true);
						ret	= f.get(ret);
						found = true;
						break;
					}
					catch(Exception e)
					{
						cl	= cl.getSuperclass();
					}
				}
				if(!found)
					throw new RuntimeException("Could not fetch capability object: "+name);
			}
		}
		return ret;
	}
	
	/**
	 * 	Adapt element for use in inner capabilities.
	 *  @param obj	The object to adapt (e.g. a change event)
	 *  @param melement	The element to adapt the object for (e.g. a plan)
	 */
	protected Object adaptToCapability(Object obj, MElement melement)
	{
		if(obj instanceof ChangeEvent && melement.getName().indexOf(BDIAgentInterpreter.CAPABILITY_SEPARATOR)!=-1)
		{
			ChangeEvent	ce	= (ChangeEvent)obj;
			int	idx	= melement.getName().lastIndexOf(BDIAgentInterpreter.CAPABILITY_SEPARATOR);
			String	capa	= melement.getName().substring(0, idx);
			String	source	= (String)ce.getSource();
			// For concrete belief just strip capability prefix.
			if(source.startsWith(capa))
			{
				source	= source.substring(capa.length()+1);
			}
			// For abstract belief find corresponding mapping.
			else
			{
				Map<String, String>	map	= getBDIModel().getBeliefMappings();
				for(String target: map.keySet())
				{
					if(source.equals(map.get(target)))
					{
						int	idx2	= target.lastIndexOf(BDIAgentInterpreter.CAPABILITY_SEPARATOR);
						String	capa2	= target.substring(0, idx2);
						if(capa.equals(capa2))
						{
							source	= target.substring(capa.length()+1);
							break;
						}
					}
				}
			}
			
			ChangeEvent	ce2	= new ChangeEvent();
			ce2.setType(ce.getType());
			ce2.setSource(source);
			ce2.setValue(ce.getValue());
			obj	= ce2;
		}
		return obj;
	}
	
	/**
	 *  Get the component fetcher.
	 */
	protected IResultCommand<Object, Class<?>>	getComponentFetcher()
	{
		return new IResultCommand<Object, Class<?>>()
		{
			public Object execute(Class<?> type)
			{
				Object ret	= null;
				if(SReflect.isSupertype(type, microagent.getClass()))
				{
					ret	= microagent;
				}
				else if(microagent instanceof IPojoMicroAgent
					&& SReflect.isSupertype(type, ((IPojoMicroAgent)microagent).getPojoAgent().getClass()))
				{
					ret	= ((IPojoMicroAgent)microagent).getPojoAgent();
				}
				return ret;
			}
		};
	}
	

	/**
	 *  Init a service.
	 */
	protected IFuture<Void> initService(ProvidedServiceInfo info, IModelInfo model, IResultCommand<Object, Class<?>> componentfetcher)
	{
		Future<Void>	ret	= new Future<Void>();
		
		int i	= info.getName()!=null ? info.getName().indexOf(CAPABILITY_SEPARATOR) : -1;
		Object	ocapa	= ((PojoBDIAgent)microagent).getPojoAgent();
		String	capa	= null;
		final IValueFetcher	oldfetcher	= getFetcher();
		if(i!=-1)
		{
			capa	= info.getName().substring(0, i); 
			SimpleValueFetcher fetcher = new SimpleValueFetcher(oldfetcher);
			if(microagent instanceof IPojoMicroAgent)
			{
				ocapa	= getCapabilityObject(capa);
				fetcher.setValue("$pojocapa", ocapa);
			}
			this.fetcher = fetcher;
			final Object	oocapa	= ocapa;
			final String	scapa	= capa;
			componentfetcher	= componentfetcher!=null ? componentfetcher :
				new IResultCommand<Object, Class<?>>()
			{
				public Object execute(Class<?> type)
				{
					Object ret	= null;
					if(SReflect.isSupertype(type, microagent.getClass()))
					{
						ret	= microagent;
					}
					else if(SReflect.isSupertype(type, oocapa.getClass()))
					{
						ret	= oocapa;
					}
					else if(SReflect.isSupertype(type, ICapability.class))
					{
						ret	= new CapabilityWrapper((BDIAgent)microagent, oocapa, scapa);
					}
					return ret;
				}
			};
		}
		super.initService(info, model, componentfetcher).addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				BDIAgentInterpreter.this.fetcher	= oldfetcher;
				super.customResultAvailable(result);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Add init code after parent injection.
	 */
	protected IFuture<Void> injectParent(final Object agent, final MicroModel model)
	{
		final Future<Void>	ret	= new Future<Void>();
		super.injectParent(agent, model).addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				// Find classes with generated init methods.
				List<Class<?>>	inits	= new ArrayList<Class<?>>();
				inits.add(agent.getClass());
				for(int i=0; i<inits.size(); i++)
				{
					Class<?>	clazz	= inits.get(i);
					if(clazz.getSuperclass().isAnnotationPresent(Agent.class)
						|| clazz.getSuperclass().isAnnotationPresent(Capability.class))
					{
						inits.add(clazz.getSuperclass());
					}
				}
				
				// Call init methods of superclasses first.
				for(int i=inits.size()-1; i>=0; i--)
				{
					Class<?>	clazz	= inits.get(i);
					List<Tuple2<Class<?>[], Object[]>>	initcalls	= BDIAgent.getInitCalls(agent, clazz);
					for(Tuple2<Class<?>[], Object[]> initcall: initcalls)
					{					
						try
						{
							String name	= IBDIClassGenerator.INIT_EXPRESSIONS_METHOD_PREFIX+"_"+clazz.getName().replace("/", "_").replace(".", "_");
							Method um = agent.getClass().getMethod(name, initcall.getFirstEntity());
//							System.out.println("Init: "+um);
							um.invoke(agent, initcall.getSecondEntity());
						}
						catch(InvocationTargetException e)
						{
							e.getTargetException().printStackTrace();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				
				initCapabilities(agent, ((BDIModel)model).getSubcapabilities(), 0).addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		return ret;
	}
	
	/**
	 *  Init the capability pojo objects.
	 */
	protected IFuture<Void>	initCapabilities(final Object agent, final Tuple2<FieldInfo, BDIModel>[] caps, final int i)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		if(i<caps.length)
		{
			try
			{
				Field	f	= caps[i].getFirstEntity().getField(getClassLoader());
				f.setAccessible(true);
				final Object	capa	= f.get(agent);
				
				String globalname;
				try
				{
					Field	g	= agent.getClass().getDeclaredField("__globalname");
					g.setAccessible(true);
					globalname	= (String)g.get(agent);
					globalname	= globalname==null ? f.getName() : globalname+CAPABILITY_SEPARATOR+f.getName();
				}
				catch(Exception e)
				{
					throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
				}
				
				injectAgent((BDIAgent)microagent, capa, caps[i].getSecondEntity(), globalname);
				
				injectServices(capa, caps[i].getSecondEntity())
					.addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						injectParent(capa, caps[i].getSecondEntity())
							.addResultListener(new DelegationResultListener<Void>(ret)
						{
							public void customResultAvailable(Void result)
							{
								initCapabilities(agent, caps, i+1)
									.addResultListener(new DelegationResultListener<Void>(ret));
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
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Add extra init code after components.
	 */
	public IFuture<Void> initComponents(final IModelInfo model, String config)
	{
		final Future<Void>	ret	= new Future<Void>();
		super.initComponents(model, config).addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				Object agent = microagent instanceof IPojoMicroAgent? ((IPojoMicroAgent)microagent).getPojoAgent(): microagent;
				wrapCollections(bdimodel.getCapability(), agent);
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 * 
	 */
	protected void wrapCollections(MCapability mcapa, Object agent)
	{
		// Inject belief collections.
		List<MBelief> mbels = mcapa.getBeliefs();
		for(MBelief mbel: mbels)
		{
			try
			{
				Object capa = agent;
				int	i	= mbel.getName().indexOf(CAPABILITY_SEPARATOR);
				if(i!=-1)
				{
					capa	= getCapabilityObject(mbel.getName().substring(0, mbel.getName().lastIndexOf(CAPABILITY_SEPARATOR)));
				}
				Object val = mbel.getValue(capa, getClassLoader());
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
						Class<?> cl = mbel.getType(getClassLoader());//f.getType();
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
//					f.set(agent, new ListWrapper((List<?>)val, rulesystem, ChangeEvent.FACTADDED+"."+bname, ChangeEvent.FACTREMOVED+"."+bname, ChangeEvent.FACTCHANGED+"."+bname));
					mbel.setValue(capa, new ListWrapper((List<?>)val, this, ChangeEvent.FACTADDED+"."+bname, ChangeEvent.FACTREMOVED+"."+bname, ChangeEvent.FACTCHANGED+"."+bname), getClassLoader());
				}
				else if(val instanceof Set)
				{
					String bname = mbel.getName();
//					f.set(agent, new SetWrapper((Set<?>)val, rulesystem, ChangeEvent.FACTADDED+"."+bname, ChangeEvent.FACTREMOVED+"."+bname, ChangeEvent.FACTCHANGED+"."+bname));
					mbel.setValue(capa, new SetWrapper((Set<?>)val, this, ChangeEvent.FACTADDED+"."+bname, ChangeEvent.FACTREMOVED+"."+bname, ChangeEvent.FACTCHANGED+"."+bname), getClassLoader());
				}
				else if(val instanceof Map)
				{
					String bname = mbel.getName();
//					f.set(agent, new MapWrapper((Map<?,?>)val, rulesystem, ChangeEvent.FACTADDED+"."+bname, ChangeEvent.FACTREMOVED+"."+bname, ChangeEvent.FACTCHANGED+"."+bname));
					mbel.setValue(capa, new MapWrapper((Map<?,?>)val, this, ChangeEvent.FACTADDED+"."+bname, ChangeEvent.FACTREMOVED+"."+bname, ChangeEvent.FACTCHANGED+"."+bname), getClassLoader());
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
	}
	
	/**
	 *  Dispatch a goal wait for its result.
	 */
	public <T, E> IFuture<E> dispatchTopLevelGoal(final T goal)
	{
		final Future<E> ret = new Future<E>();
		
		final MGoal mgoal = ((MCapability)capa.getModelElement()).getGoal(goal.getClass().getName());
		if(mgoal==null)
			throw new RuntimeException("Unknown goal type: "+goal);
		final RGoal rgoal = new RGoal(getInternalAccess(), mgoal, goal, null);
		rgoal.addGoalListener(new ExceptionDelegationResultListener<Void, E>(ret)
		{
			public void customResultAvailable(Void result)
			{
				Object res = RGoal.getGoalResult(goal, mgoal, bdimodel.getClassloader());
				ret.setResult((E)res);
			}
		});

//		System.out.println("adopt goal");
		RGoal.adoptGoal(rgoal, getInternalAccess());
		
		return ret;
	}
	
	/**
	 *  Start the component behavior.
	 */
	public void startBehavior()
	{
		super.startBehavior();
		
//		try
//		{
		
		final Object agent = microagent instanceof PojoBDIAgent? ((PojoBDIAgent)microagent).getPojoAgent(): microagent;
				
		// Init bdi configuration
		String confname = getConfiguration();
		if(confname!=null)
		{
			MConfiguration mconf = bdimodel.getCapability().getConfiguration(confname);
			
			if(mconf!=null)
			{
				// Set initial belief values
				List<UnparsedExpression> ibels = mconf.getInitialBeliefs();
				if(ibels!=null)
				{
					for(UnparsedExpression uexp: ibels)
					{
						try
						{
							MBelief mbel = bdimodel.getCapability().getBelief(uexp.getName());
							Object val = SJavaParser.parseExpression(uexp, getModel().getAllImports(), getClassLoader()).getValue(null);
	//						Field f = mbel.getTarget().getField(getClassLoader());
	//						f.setAccessible(true);
	//						f.set(agent, val);
							mbel.setValue(agent, val, getClassLoader());
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
						
						RGoal rgoal = new RGoal(getInternalAccess(), mgoal, goal, null);
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
						RPlan.executePlan(rplan, getInternalAccess());
					}
				}
			}
		}
		
		// Observe dynamic beliefs
		List<MBelief> beliefs = bdimodel.getCapability().getBeliefs();
		
		for(final MBelief mbel: beliefs)
		{
			Collection<String> evs = mbel.getEvents();
			if(evs!=null && !evs.isEmpty())
			{
				Object	ocapa	= agent;
				int	i	= mbel.getName().indexOf(CAPABILITY_SEPARATOR);
				if(i!=-1)
				{
					ocapa	= getCapabilityObject(mbel.getName().substring(0, mbel.getName().lastIndexOf(CAPABILITY_SEPARATOR)));
				}
				final Object	capa	= ocapa;

				List<EventType> events = new ArrayList<EventType>();
				for(String ev: evs)
				{
					addBeliefEvents(getInternalAccess(), events, ev);
				}
				
				Rule<Void> rule = new Rule<Void>(mbel.getName()+"_belief_update", 
					ICondition.TRUE_CONDITION, new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
					{
//						System.out.println("belief update: "+event);
						try
						{
							Method um = capa.getClass().getMethod(IBDIClassGenerator.DYNAMIC_BELIEF_UPDATEMETHOD_PREFIX+SUtil.firstToUpperCase(mbel.getName()), new Class[0]);
							um.invoke(capa, new Object[0]);
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
			
			if(mbel.getUpdaterate()>0)
			{
				int	i	= mbel.getName().indexOf(CAPABILITY_SEPARATOR);
				final String	name;
				final Object	capa;
				if(i!=-1)
				{
					capa	= getCapabilityObject(mbel.getName().substring(0, mbel.getName().lastIndexOf(CAPABILITY_SEPARATOR)));
					name	= mbel.getName().substring(mbel.getName().lastIndexOf(CAPABILITY_SEPARATOR)+1); 
				}
				else
				{
					capa	= agent;
					name	= mbel.getName();
				}

				SServiceProvider.getService(getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new IResultListener<IClockService>()
				{
					public void resultAvailable(final IClockService cs)
					{
						cs.createTimer(mbel.getUpdaterate(), new ITimedObject()
						{
							ITimedObject	self	= this;
							public void timeEventOccurred(long currenttime)
							{
								try
								{
									scheduleStep(new IComponentStep<Void>()
									{
										public IFuture<Void> execute(IInternalAccess ia)
										{
											try
											{
												Method um = capa.getClass().getMethod(IBDIClassGenerator.DYNAMIC_BELIEF_UPDATEMETHOD_PREFIX+SUtil.firstToUpperCase(name), new Class[0]);
												um.invoke(capa, new Object[0]);
											}
											catch(Exception e)
											{
												e.printStackTrace();
											}
											
											cs.createTimer(mbel.getUpdaterate(), self);
											return IFuture.DONE;
										}
									});
								}
								catch(ComponentTerminatedException cte)
								{
									
								}
							}
						});
					}
					
					public void exceptionOccurred(Exception exception)
					{
						getLogger().severe("Cannot update belief "+mbel.getName()+": "+exception);
					}
				});
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
//			boolean declarative = false;
//			boolean maintain = false;
			
			List<MCondition> conds = mgoal.getConditions(MGoal.CONDITION_CREATION);
			if(conds!=null)
			{
				for(MCondition cond: conds)
				{
					if(cond.getConstructorTarget()!=null)
					{
						final Constructor<?> c = cond.getConstructorTarget().getConstructor(getClassLoader());
						
						Rule<Void> rule = new Rule<Void>(mgoal.getName()+"_goal_create", 
							ICondition.TRUE_CONDITION, new IAction<Void>()
						{
							public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
							{
	//							System.out.println("create: "+context);
								
								Object pojogoal = null;
								try
								{
									Class<?>[] ptypes = c.getParameterTypes();
									Object[] pvals = new Object[ptypes.length];
									
									for(int i=0; i<ptypes.length; i++)
									{
										if(event.getContent()!=null && SReflect.isSupertype(ptypes[i], event.getContent().getClass()))
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
									dispatchTopLevelGoal(pojogoal)
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
						rule.setEvents(cond.getEvents());
						getRuleSystem().getRulebase().addRule(rule);
					}
					else if(cond.getMethodTarget()!=null)
					{
						final Method m = cond.getMethodTarget().getMethod(getClassLoader());
						
						List<Object> vals = new ArrayList<Object>();
						if(agent!=null)
							vals.add(agent);
						vals.add(getExternalAccess());
						
						SimpleMethodParameterGuesser g = new SimpleMethodParameterGuesser(m.getParameterTypes(), vals);
						
						Rule<Void> rule = new Rule<Void>(mgoal.getName()+"_goal_create", 
							new MethodCondition(null, m, g)
						{
							public List<Object> getExtraValues(IEvent event)
							{
								List<Object> vals = super.getExtraValues(event);
								String type = event.getType().getType(1);
								int idx = type.lastIndexOf(CAPABILITY_SEPARATOR);
								if(idx!=-1)
								{
									String capaname = type.substring(0, idx);
									Object capa = getCapabilityObject(capaname);
									vals.add(new CapabilityWrapper((BDIAgent)getAgent(), capa, type));
								}
								else
								{
									vals.add(new CapabilityWrapper((BDIAgent)getAgent(), capa, null));
								}
									
								return vals;
							}
							
							public Tuple2<Boolean, Object> prepareResult(Object res)
							{
								Tuple2<Boolean, Object> ret = null;
								if(res!=null)
								{
									ret = new Tuple2<Boolean, Object>(Boolean.TRUE, res);
								}
								else
								{
									ret = new Tuple2<Boolean, Object>(Boolean.FALSE, null);
								}
								return ret;
							}
						}, new IAction<Void>()
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
											dispatchTopLevelGoal(pojogoal);
										}
									}
									else
									{
										dispatchTopLevelGoal(condresult);
									}
								}
								else
								{
									Object pojogoal = null;
									if(event.getContent()!=null)
									{
										try
										{
											Class<?> evcl = event.getContent().getClass();
											Constructor<?> c = gcl.getConstructor(new Class[]{evcl});
											pojogoal = c.newInstance(new Object[]{event.getContent()});
											dispatchTopLevelGoal(pojogoal);
										}
										catch(Exception e)
										{
											e.printStackTrace();
										}
									}
									else
									{
										// todo:
			//							Constructor<?>[] cons = gcl.getConstructors();
			//							for(Constructor c: cons)
			//							{
			//							}
										throw new RuntimeException("Unknown how to create goal: "+gcl);
									}
								}
								
								return IFuture.DONE;
							}
						});
						rule.setEvents(cond.getEvents());
						getRuleSystem().getRulebase().addRule(rule);
					}
				}
			}
			
			conds = mgoal.getConditions(MGoal.CONDITION_DROP);
			if(conds!=null)
			{
				for(MCondition cond: conds)
				{
					final Method m = cond.getMethodTarget().getMethod(getClassLoader());
					
					Rule<?> rule = new Rule<Void>(mgoal.getName()+"_goal_drop", 
						new GoalsExistCondition(mgoal, capa), new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
							for(RGoal goal: getCapability().getGoals(mgoal))
							{
								if(executeGoalMethod(m, goal, event))
								{
	//								System.out.println("Goal dropping triggered: "+goal);
	//								rgoal.setLifecycleState(BDIAgent.this, rgoal.GOALLIFECYCLESTATE_DROPPING);
									if(!goal.isFinished())
									{
										goal.setException(new GoalFailureException("drop condition: "+m.getName()));
										goal.setProcessingState(getInternalAccess(), RGoal.GoalProcessingState.FAILED);
									}
								}
							}
							
							return IFuture.DONE;
						}
					});
					rule.setEvents(cond.getEvents());
					getRuleSystem().getRulebase().addRule(rule);
				}
			}
			
			conds = mgoal.getConditions(MGoal.CONDITION_CONTEXT);
			if(conds!=null)
			{
				for(final MCondition cond: conds)
				{
					final Method m = cond.getMethodTarget().getMethod(getClassLoader());
					
					Rule<?> rule = new Rule<Void>(mgoal.getName()+"_goal_suspend", 
						new GoalsExistCondition(mgoal, capa), new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
							for(RGoal goal: getCapability().getGoals(mgoal))
							{
								if(!RGoal.GoalLifecycleState.SUSPENDED.equals(goal.getLifecycleState()))
								{	
									if(!executeGoalMethod(m, goal, event))
									{
	//									if(goal.getMGoal().getName().indexOf("AchieveCleanup")!=-1)
	//										System.out.println("Goal suspended: "+goal);
										goal.setLifecycleState(getInternalAccess(), RGoal.GoalLifecycleState.SUSPENDED);
										goal.setState(RGoal.State.INITIAL);
									}
								}
							}
							return IFuture.DONE;
						}
					});
					rule.setEvents(cond.getEvents());
					getRuleSystem().getRulebase().addRule(rule);
					
					rule = new Rule<Void>(mgoal.getName()+"_goal_option", 
						new GoalsExistCondition(mgoal, capa), new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
							for(RGoal goal: getCapability().getGoals(mgoal))
							{
								if(RGoal.GoalLifecycleState.SUSPENDED.equals(goal.getLifecycleState()))
								{	
									if(executeGoalMethod(m, goal, event))
									{
	//									if(goal.getMGoal().getName().indexOf("AchieveCleanup")!=-1)
	//										System.out.println("Goal made option: "+goal);
										goal.setLifecycleState(getInternalAccess(), RGoal.GoalLifecycleState.OPTION);
	//									setState(ia, PROCESSABLEELEMENT_INITIAL);
									}
								}
							}
							
							return IFuture.DONE;
						}
					});
					rule.setEvents(cond.getEvents());
					getRuleSystem().getRulebase().addRule(rule);
				}
			}
			
			conds = mgoal.getConditions(MGoal.CONDITION_TARGET);
			if(conds!=null)
			{
				for(final MCondition cond: conds)
				{
					final Method m = cond.getMethodTarget().getMethod(getClassLoader());
					
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
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
							for(RGoal goal: getCapability().getGoals(mgoal))
							{
								if(executeGoalMethod(m, goal, event))
									goal.targetConditionTriggered(getInternalAccess(), event, rule, context);
							}
						
							return IFuture.DONE;
						}
					});
					List<EventType> events = new ArrayList<EventType>(cond.getEvents());
					events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED}));
					rule.setEvents(events);
					getRuleSystem().getRulebase().addRule(rule);
				}
			}
			
			conds = mgoal.getConditions(MGoal.CONDITION_RECUR);
			if(conds!=null)
			{
				for(final MCondition cond: conds)
				{
					final Method m = cond.getMethodTarget().getMethod(getClassLoader());
					
					Rule<?> rule = new Rule<Void>(mgoal.getName()+"_goal_recur",
						new GoalsExistCondition(mgoal, capa), new IAction<Void>()
	//						new CombinedCondition(new ICondition[]{
	//							new LifecycleStateCondition(GOALLIFECYCLESTATE_ACTIVE),
	//							new ProcessingStateCondition(GOALPROCESSINGSTATE_PAUSED),
	//							new MethodCondition(getPojoElement(), m),
	//						}), new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
							for(RGoal goal: getCapability().getGoals(mgoal))
							{
								if(RGoal.GoalLifecycleState.ACTIVE.equals(goal.getLifecycleState())
									&& RGoal.GoalProcessingState.PAUSED.equals(goal.getProcessingState()))
								{	
									if(executeGoalMethod(m, goal, event))
									{
										goal.setTriedPlans(null);
										goal.setApplicablePlanList(null);
										goal.setProcessingState(getInternalAccess(), RGoal.GoalProcessingState.INPROCESS);
									}
								}
							}
							return IFuture.DONE;
						}
					});
					rule.setEvents(cond.getEvents());
					getRuleSystem().getRulebase().addRule(rule);
				}
			}
			
			conds = mgoal.getConditions(MGoal.CONDITION_MAINTAIN);
			if(conds!=null)
			{
				for(final MCondition cond: conds)
				{
					final Method m = cond.getMethodTarget().getMethod(getClassLoader());
					
					Rule<?> rule = new Rule<Void>(mgoal.getName()+"_goal_maintain", 
						new GoalsExistCondition(mgoal, capa), new IAction<Void>()
	//						new CombinedCondition(new ICondition[]{
	//							new LifecycleStateCondition(GOALLIFECYCLESTATE_ACTIVE),
	//							new ProcessingStateCondition(GOALPROCESSINGSTATE_IDLE),
	//							new MethodCondition(getPojoElement(), mcond, true),
	//						}), new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
							for(RGoal goal: getCapability().getGoals(mgoal))
							{
								if(RGoal.GoalLifecycleState.ACTIVE.equals(goal.getLifecycleState())
									&& RGoal.GoalProcessingState.IDLE.equals(goal.getProcessingState()))
								{	
									if(!executeGoalMethod(m, goal, event))
									{
//										System.out.println("Goal maintain triggered: "+goal);
//										System.out.println("state was: "+goal.getProcessingState());
										goal.setProcessingState(getInternalAccess(), RGoal.GoalProcessingState.INPROCESS);
									}
								}
							}
							return IFuture.DONE;
						}
					});
					List<EventType> events = new ArrayList<EventType>(cond.getEvents());
					events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED}));
					rule.setEvents(events);
					getRuleSystem().getRulebase().addRule(rule);
					
					// if has no own target condition
					if(mgoal.getConditions(MGoal.CONDITION_TARGET)==null)
					{
						// if not has own target condition use the maintain cond
						rule = new Rule<Void>(mgoal.getName()+"_goal_target", 
							new GoalsExistCondition(mgoal, capa), new IAction<Void>()
	//							new MethodCondition(getPojoElement(), mcond), new IAction<Void>()
						{
							public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
							{
								for(RGoal goal: getCapability().getGoals(mgoal))
								{
									if(executeGoalMethod(m, goal, event))
										goal.targetConditionTriggered(getInternalAccess(), event, rule, context);
								}
								
								return IFuture.DONE;
							}
						});
						rule.setEvents(cond.getEvents());
						getRuleSystem().getRulebase().addRule(rule);
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
				public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
				{
					RPlan rplan = RPlan.createRPlan(mplan, mplan, new ChangeEvent(event), getInternalAccess());
					RPlan.executePlan(rplan, getInternalAccess());
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
			
			// context condition
			
			final MethodInfo mi = mplan.getBody().getContextConditionMethod(getClassLoader());
			if(mi!=null)
			{
				PlanContextCondition pcc = mi.getMethod(getClassLoader()).getAnnotation(PlanContextCondition.class);
				String[] evs = pcc.events();
				List<EventType> events = new ArrayList<EventType>();
				for(String ev: evs)
				{
					addBeliefEvents(getInternalAccess(), events, ev);
				}
				
				IAction<Void> abortplans = new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
					{
						Collection<RPlan> coll = capa.getPlans(mplan);
						
						for(final RPlan plan: coll)
						{
							List<Object> vals = new ArrayList<Object>();
							vals.add(agent);
							vals.add(plan);
							invokeBooleanMethod(plan.getBody().getBody(agent), mi.getMethod(getClassLoader()), vals)
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
					new PlansExistCondition(mplan, capa), abortplans);
				rule.setEvents(events);
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
			
			if(usedelib)
			{
				List<EventType> events = new ArrayList<EventType>();
				events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED}));
				Rule<Void> rule = new Rule<Void>("goal_addinitialinhibitors", 
					ICondition.TRUE_CONDITION, new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
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
						public Tuple2<Boolean, Object> evaluate(IEvent event)
						{
	//						if(((RGoal)event.getContent()).getId().indexOf("Battery")!=-1)
	//							System.out.println("maintain");
							
							// return true when other goal is active and inprocess
							boolean ret = false;
							EventType type = event.getType();
							RGoal goal = (RGoal)event.getContent();
							ret = ChangeEvent.GOALACTIVE.equals(type.getType(0)) && RGoal.GoalProcessingState.INPROCESS.equals(goal.getProcessingState())
								|| (ChangeEvent.GOALINPROCESS.equals(type.getType(0)) && RGoal.GoalLifecycleState.ACTIVE.equals(goal.getLifecycleState()));
							return ret? ICondition.TRUE: ICondition.FALSE;
						}
					}, new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
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
						public Tuple2<Boolean, Object> evaluate(IEvent event)
						{
							// return true when other goal is active and inprocess
							boolean ret = false;
							EventType type = event.getType();
							if(event.getContent() instanceof RGoal)
							{
								RGoal goal = (RGoal)event.getContent();
								ret = ChangeEvent.GOALSUSPENDED.equals(type.getType(0)) || ChangeEvent.GOALOPTION.equals(type.getType(0))
									|| !RGoal.GoalProcessingState.INPROCESS.equals(goal.getProcessingState());
							}
							return ret? ICondition.TRUE: ICondition.FALSE;
						}
					}, new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
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
					new LifecycleStateCondition(RGoal.GoalLifecycleState.ACTIVE), new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
					{
						RGoal goal = (RGoal)event.getContent();
	//					System.out.println("optionizing: "+goal+" "+goal.inhibitors);
						goal.setLifecycleState(getInternalAccess(), RGoal.GoalLifecycleState.OPTION);
						return IFuture.DONE;
					}
				});
				rule.addEvent(new EventType(new String[]{ChangeEvent.GOALINHIBITED}));
				getRuleSystem().getRulebase().addRule(rule);
			}
			
			Rule<Void> rule = new Rule<Void>("goal_activate", 
				new CombinedCondition(new ICondition[]{
					new LifecycleStateCondition(RGoal.GoalLifecycleState.OPTION),
					new ICondition()
					{
						public Tuple2<Boolean, Object> evaluate(IEvent event)
						{
							RGoal goal = (RGoal)event.getContent();
							return !goal.isInhibited()? ICondition.TRUE: ICondition.FALSE;
						}
					}
				}), new IAction<Void>()
			{
				public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
				{
					RGoal goal = (RGoal)event.getContent();
//					if(goal.getMGoal().getName().indexOf("AchieveCleanup")!=-1)
//						System.out.println("reactivating: "+goal);
					goal.setLifecycleState(getInternalAccess(), RGoal.GoalLifecycleState.ACTIVE);
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
		
		// Start rule system
		inited	= true;
	
//		getCapability().dumpGoalsPeriodically(getInternalAccess());
//		getCapability().dumpPlansPeriodically(getInternalAccess());
		
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
	}
	
	/**
	 *  Called before blocking the component thread.
	 */
	public void	beforeBlock()
	{
		testBodyAborted();
		
		RPlan	rplan	= ExecutePlanStepAction.RPLANS.get();
		ComponentSuspendable sus = ComponentSuspendable.COMSUPS.get();
		if(rplan!=null && sus!=null && !RPlan.PlanProcessingState.WAITING.equals(rplan.getProcessingState()))
		{
			Future fut = sus.getFuture();
			final ResumeCommand<Void> rescom = rplan.new ResumeCommand<Void>(fut);
			rplan.setResumeCommand(rescom);
		}
	}
	
	/**
	 *  Called after unblocking the component thread.
	 */
	public void	afterBlock()
	{
		testBodyAborted();
		RPlan rplan = ExecutePlanStepAction.RPLANS.get();
		if(rplan!=null)
		{
			rplan.setProcessingState(PlanProcessingState.RUNNING);
			rplan.resumecommand = null;
		}
	}
	
	/**
	 * 
	 */
	protected void testBodyAborted()
	{
		// Throw error to exit body method of aborted plan.
		RPlan	rplan	= ExecutePlanStepAction.RPLANS.get();
		if(rplan!=null && rplan.aborted && rplan.getLifecycleState()==PlanLifecycleState.BODY)
		{
//			System.out.println("aborting after block: "+rplan);
			throw new BodyAborted();
		}
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
			result = m.invoke(goal.getPojoElement(), m.getParameterTypes().length==0? new Object[0]: new Object[]{event.getContent()});
			return ((Boolean)result).booleanValue();
		}
		catch(Exception e)
		{
			System.err.println("method: "+m);
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
		
		if(inited && rulesystem!=null)
			rulesystem.processAllEvents();
		
//		if(steps!=null && steps.size()>0)
//		{
//			System.out.println("steps: "+steps.size()+" "+((Object[])steps.get(0))[0]);
//		}
		boolean ret = super.executeStep();

		return ret || (inited && rulesystem!=null && rulesystem.isEventAvailable());
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
	
//	/**
//	 *  Method that tries to guess the parameters for the method call.
//	 */
//	public Object[] guessMethodParameters(Object pojo, Class<?>[] ptypes, Set<Object> values)
//	{
//		if(ptypes==null || values==null)
//			return null;
//		
//		Object[] params = new Object[ptypes.length];
//		
//		for(int i=0; i<ptypes.length; i++)
//		{
//			for(Object val: values)
//			{
//				if(SReflect.isSupertype(val.getClass(), ptypes[i]))
//				{
//					params[i] = val;
//					break;
//				}
//			}
//		}
//				
//		return params;
//	}
	
	/**
	 * 
	 */
	protected IFuture<Boolean> invokeBooleanMethod(Object pojo, Method m, Collection<Object> vals)
	{
		final Future<Boolean> ret = new Future<Boolean>();
		try
		{
			m.setAccessible(true);
			IMethodParameterGuesser g = new SimpleMethodParameterGuesser(m.getParameterTypes(), vals);
			Object app = m.invoke(pojo, g.guessParameters());
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
			ret.setException(e);
		}
		return ret;
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
					jadex.rules.eca.annotations.Event ev = (jadex.rules.eca.annotations.Event)an;
					String name = ev.value();
					String type = ev.type();
					if(type.isEmpty())
					{
						addBeliefEvents(ia, events, name);
					}
					else
					{
						events.add(new EventType(new String[]{type, name}));
					}
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
		protected Set<GoalLifecycleState> states;
		
		/** The flag if state is allowed or disallowed. */
		protected boolean allowed;
		
		/**
		 *  Create a new condition.
		 */
		public LifecycleStateCondition(GoalLifecycleState state)
		{
			this(SUtil.createHashSet(new GoalLifecycleState[]{state}));
		}
		
		/**
		 *  Create a new condition.
		 */
		public LifecycleStateCondition(Set<GoalLifecycleState> states)
		{
			this(states, true);
		}
		
		/**
		 *  Create a new condition.
		 */
		public LifecycleStateCondition(GoalLifecycleState state, boolean allowed)
		{
			this(SUtil.createHashSet(new GoalLifecycleState[]{state}), allowed);
		}
		
		/**
		 *  Create a new condition.
		 */
		public LifecycleStateCondition(Set<GoalLifecycleState> states, boolean allowed)
		{
			this.states = states;
			this.allowed = allowed;
		}
		
		/**
		 *  Evaluate the condition.
		 */
		public Tuple2<Boolean, Object> evaluate(IEvent event)
		{
			RGoal goal = (RGoal)event.getContent();
			boolean ret = states.contains(goal.getLifecycleState());
			if(!allowed)
				ret = !ret;
			return ret? ICondition.TRUE: ICondition.FALSE;
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
		public Tuple2<Boolean, Object> evaluate(IEvent event)
		{
			boolean res = !capa.getGoals(mgoal).isEmpty();
			return res? ICondition.TRUE: ICondition.FALSE;
		}
	}
	
	/**
	 *  Condition that tests if goal instances of an mplan exist.
	 */
	public static class PlansExistCondition implements ICondition
	{
		protected MPlan mplan;
		
		protected RCapability capa;
		
		public PlansExistCondition(MPlan mplan, RCapability capa)
		{
			this.mplan = mplan;
			this.capa = capa;
		}
		
		/**
		 * 
		 */
		public Tuple2<Boolean, Object> evaluate(IEvent event)
		{
			return !capa.getPlans(mplan).isEmpty()? ICondition.TRUE: ICondition.FALSE;
		}
	}
}

