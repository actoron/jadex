package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.IBDIClassGenerator;
import jadex.bdiv3.PojoBDIAgent;
import jadex.bdiv3.actions.ExecutePlanStepAction;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.PlanContextCondition;
import jadex.bdiv3.annotation.RawEvent;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MCondition;
import jadex.bdiv3.model.MConfiguration;
import jadex.bdiv3.model.MDeliberation;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MTrigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.ICapability;
import jadex.bdiv3.runtime.IGoal.GoalLifecycleState;
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
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.component.ComponentSuspendable;
import jadex.bridge.service.search.LocalServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.IPlatformComponentFactory;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.commons.FieldInfo;
import jadex.commons.IResultCommand;
import jadex.commons.IValueFetcher;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.SimpleParameterGuesser;
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
import jadex.rules.eca.annotations.Event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
	public BDIAgentInterpreter(IComponentDescription desc, IPlatformComponentFactory factory, 
		final BDIModel model, Class<?> agentclass, final Map<String, Object> args, final String config, 
		final IExternalAccess parent, RequiredServiceBinding[] bindings, boolean copy, boolean realtime, boolean persist,
		IPersistInfo persistinfo,
		final IIntermediateResultListener<Tuple2<String, Object>> listener, final Future<Void> inited, LocalServiceRegistry registry)
	{
		super(desc, factory, model, agentclass, args, config, parent, bindings, copy, realtime, persist, persistinfo, listener, inited, registry);
		this.bdimodel = model;
		this.capa = new RCapability(bdimodel.getCapability());
	}
	
	/**
	 *  Create the agent.
	 */
	protected MicroAgent createAgent(Class<?> agentclass, MicroModel model, IPersistInfo pinfo) throws Exception
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
		// Add hidden agent field also to contained inner classes (goals, plans)
		// Does not work as would have to be inserted in each object of that type :-(
//		Class<?>[] inners = agent.getClass().getDeclaredClasses();
//		if(inners!=null)
//		{
//			for(Class<?> icl: inners)
//			{
//				try
//				{
//					Field field = icl.getDeclaredField("__agent");
//					field.setAccessible(true);
//					field.set(icl, pa);
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//		}
	}
	
	/**
	 *  Get a capability pojo object.
	 */
	public Object	getCapabilityObject(String name)
	{
		Object	ret	= ((PojoBDIAgent)microagent).getPojoAgent();
		if(name!=null)
		{
			StringTokenizer	stok	= new StringTokenizer(name, MElement.CAPABILITY_SEPARATOR);
			while(stok.hasMoreTokens())
			{
				name	= stok.nextToken();
				
				boolean found = false;
				Class<?> cl = ret.getClass();
				while(!found && !Object.class.equals(cl))
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
	 *  @param capa	The capability name or null for agent.
	 */
	protected Object adaptToCapability(Object obj, String capa)
	{
		if(obj instanceof ChangeEvent && capa!=null)
		{
			ChangeEvent	ce	= (ChangeEvent)obj;
			String	source	= (String)ce.getSource();
			if(source!=null)
			{
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
							int	idx2	= target.lastIndexOf(MElement.CAPABILITY_SEPARATOR);
							String	capa2	= target.substring(0, idx2);
							if(capa.equals(capa2))
							{
								source	= target.substring(capa.length()+1);
								break;
							}
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
	 *  Create a service implementation from description.
	 */
	protected Object createServiceImplementation(ProvidedServiceInfo info, IModelInfo model)
	{
		// Support special case that BDI should implement provided service with plans.
		Object ret = null;
		ProvidedServiceImplementation impl = info.getImplementation();
		if(impl!=null && impl.getClazz()!=null && impl.getClazz().getType(getClassLoader()).equals(BDIAgent.class))
		{
			Class<?> iface = info.getType().getType(getClassLoader());
			ret = Proxy.newProxyInstance(getClassLoader(), new Class[]{iface}, 
				new BDIServiceInvocationHandler(this, iface));
		}
		else
		{
			ret = super.createServiceImplementation(info, model);
		}
		return ret;
	}
	
	/**
	 *  Init a service.
	 */
	protected IFuture<Void> initService(ProvidedServiceInfo info, IModelInfo model, IResultCommand<Object, Class<?>> componentfetcher)
	{
		Future<Void>	ret	= new Future<Void>();
		
		int i	= info.getName()!=null ? info.getName().indexOf(MElement.CAPABILITY_SEPARATOR) : -1;
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
					globalname	= globalname==null ? f.getName() : globalname+MElement.CAPABILITY_SEPARATOR+f.getName();
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
	
//	/**
//	 *  Add extra init code after components.
//	 */
//	public IFuture<Void> initComponents(final IModelInfo model, String config)
//	{
//		final Future<Void>	ret	= new Future<Void>();
//		super.initComponents(model, config).addResultListener(new DelegationResultListener<Void>(ret)
//		{
//			public void customResultAvailable(Void result)
//			{
//				Object agent = microagent instanceof IPojoMicroAgent? ((IPojoMicroAgent)microagent).getPojoAgent(): microagent;
////				wrapCollections(bdimodel.getCapability(), agent);
//				ret.setResult(null);
//			}
//		});
//		return ret;
//	}
	
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
				Object val = mbel.getValue(this);
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
					mbel.setValue(this, new ListWrapper((List<?>)val, this, ChangeEvent.FACTADDED+"."+bname, ChangeEvent.FACTREMOVED+"."+bname, ChangeEvent.FACTCHANGED+"."+bname, mbel));
				}
				else if(val instanceof Set)
				{
					String bname = mbel.getName();
					mbel.setValue(this, new SetWrapper((Set<?>)val, this, ChangeEvent.FACTADDED+"."+bname, ChangeEvent.FACTREMOVED+"."+bname, ChangeEvent.FACTCHANGED+"."+bname, mbel));
				}
				else if(val instanceof Map)
				{
					String bname = mbel.getName();
					mbel.setValue(this, new MapWrapper((Map<?,?>)val, this, ChangeEvent.FACTADDED+"."+bname, ChangeEvent.FACTREMOVED+"."+bname, ChangeEvent.FACTCHANGED+"."+bname, mbel));
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
		final RGoal rgoal = new RGoal(getInternalAccess(), mgoal, goal, (RPlan)null);
		rgoal.addListener(new ExceptionDelegationResultListener<Void, E>(ret)
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
	 *  Drop a pojo goal.
	 */
	public void dropGoal(Object goal)
	{
		for(RGoal rgoal: getCapability().getGoals(goal.getClass()))
		{
			if(goal.equals(rgoal.getPojoElement()))
			{
				rgoal.drop();
			}
		}
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
							mbel.setValue(this, val);
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
							Object o = SJavaParser.parseExpression(uexp, getModel().getAllImports(), getClassLoader()).getValue(getFetcher());
							if(o instanceof Class)
							{
								gcl = (Class<?>)o;
							}
							else
							{
								goal = o;
								gcl = o.getClass();
							}
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
						{
							throw new RuntimeException("Could not create initial goal: "+uexp);
						}
						
						RGoal rgoal = new RGoal(getInternalAccess(), mgoal, goal, (RPlan)null);
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
						RPlan.executePlan(rplan, getInternalAccess(), null);
					}
				}
			}
		}
		
		// Observe dynamic beliefs
		List<MBelief> beliefs = bdimodel.getCapability().getBeliefs();
		
		for(final MBelief mbel: beliefs)
		{
			List<EventType> events = new ArrayList<EventType>();
			
			Collection<String> evs = mbel.getEvents();
			Object	cap = null;
			if(evs!=null && !evs.isEmpty())
			{
				Object	ocapa	= agent;
				int	i	= mbel.getName().indexOf(MElement.CAPABILITY_SEPARATOR);
				if(i!=-1)
				{
					ocapa	= getCapabilityObject(mbel.getName().substring(0, mbel.getName().lastIndexOf(MElement.CAPABILITY_SEPARATOR)));
				}
				cap	= ocapa;

				for(String ev: evs)
				{
					addBeliefEvents(getInternalAccess(), events, ev);
				}
			}
			
			Collection<EventType> rawevents = mbel.getRawEvents();
			if(rawevents!=null)
			{
				Collection<EventType> revs = mbel.getRawEvents();
				if(revs!=null)
					events.addAll(revs);
			}
		
			if(!events.isEmpty())
			{
				final Object fcapa = cap;
				Rule<Void> rule = new Rule<Void>(mbel.getName()+"_belief_update", 
					ICondition.TRUE_CONDITION, new IAction<Void>()
				{
					Object oldval = null;
					
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
					{
//						System.out.println("belief update: "+event);
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
						else
						{
							Object value = mbel.getValue(BDIAgentInterpreter.this);
							// todo: save old value?!
							BDIAgent.createChangeEvent(value, oldval, null, (BDIAgent)microagent, mbel.getName());
							oldval = value;
						}
						return IFuture.DONE;
					}
				});
				rule.setEvents(events);
				getRuleSystem().getRulebase().addRule(rule);
			}
			
			if(mbel.getUpdaterate()>0)
			{
				int	i	= mbel.getName().indexOf(MElement.CAPABILITY_SEPARATOR);
				final String	name;
				final Object	capa;
				if(i!=-1)
				{
					capa	= getCapabilityObject(mbel.getName().substring(0, mbel.getName().lastIndexOf(MElement.CAPABILITY_SEPARATOR)));
					name	= mbel.getName().substring(mbel.getName().lastIndexOf(MElement.CAPABILITY_SEPARATOR)+1); 
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
							Object oldval = null;
							
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
												// Invoke dynamic update method if field belief
												if(mbel.isFieldBelief())
												{
													Method um = capa.getClass().getMethod(IBDIClassGenerator.DYNAMIC_BELIEF_UPDATEMETHOD_PREFIX+SUtil.firstToUpperCase(name), new Class[0]);
													um.invoke(capa, new Object[0]);
												}
												// Otherwise just call getValue and throw event
												else
												{
													Object value = mbel.getValue(capa, getClassLoader());
													BDIAgent.createChangeEvent(value, oldval, null, (BDIAgent)microagent, mbel.getName());
													oldval = value;
												}
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
									boolean ok = true;
									Class<?>[] ptypes = c.getParameterTypes();
									Object[] pvals = new Object[ptypes.length];
									
									Annotation[][] anns = c.getParameterAnnotations();
									int skip = ptypes.length - anns.length;
									
									for(int i=0; i<ptypes.length; i++)
									{
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
								
								if(pojogoal!=null && !getCapability().containsGoal(pojogoal))
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
//								else
//								{
//									System.out.println("new goal not adopted, already contained: "+pojogoal);
//								}
								
								return IFuture.DONE;
							}
						});
						rule.setEvents(cond.getEvents());
						getRuleSystem().getRulebase().addRule(rule);
					}
					else if(cond.getMethodTarget()!=null)
					{
						final Method m = cond.getMethodTarget().getMethod(getClassLoader());
						
						Rule<Void> rule = new Rule<Void>(mgoal.getName()+"_goal_create", 
							new MethodCondition(null, m)
						{
							protected Object invokeMethod(IEvent event) throws Exception
							{
								m.setAccessible(true);
								Object[] pvals = getInjectionValues(m.getParameterTypes(), m.getParameterAnnotations(),
									mgoal, new ChangeEvent(event), null, null);
								return pvals!=null? m.invoke(null, pvals): null;
							}
														
//							public Tuple2<Boolean, Object> prepareResult(Object res)
//							{
//								Tuple2<Boolean, Object> ret = null;
//								if(res instanceof Boolean)
//								{
//									ret = new Tuple2<Boolean, Object>((Boolean)res, null);
//								}
//								else if(res!=null)
//								{
//									ret = new Tuple2<Boolean, Object>(Boolean.TRUE, res);
//								}
//								else
//								{
//									ret = new Tuple2<Boolean, Object>(Boolean.FALSE, null);
//								}
//								return ret;
//							}
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
//									Object pojogoal = null;
//									if(event.getContent()!=null)
//									{
//										try
//										{
//											Class<?> evcl = event.getContent().getClass();
//											Constructor<?> c = gcl.getConstructor(new Class[]{evcl});
//											pojogoal = c.newInstance(new Object[]{event.getContent()});
//											dispatchTopLevelGoal(pojogoal);
//										}
//										catch(Exception e)
//										{
//											e.printStackTrace();
//										}
//									}
//									else
//									{
										Constructor<?>[] cons = gcl.getConstructors();
										Object pojogoal = null;
										boolean ok = false;
										for(Constructor<?> c: cons)
										{
											try
											{
												Object[] vals = getInjectionValues(c.getParameterTypes(), c.getParameterAnnotations(),
													mgoal, new ChangeEvent(event), null, null);
												if(vals!=null)
												{
													pojogoal = c.newInstance(vals);
													dispatchTopLevelGoal(pojogoal);
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
//								}
								
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
							for(final RGoal goal: getCapability().getGoals(mgoal))
							{
								if(!RGoal.GoalLifecycleState.DROPPING.equals(goal.getLifecycleState())
									 && !RGoal.GoalLifecycleState.DROPPED.equals(goal.getLifecycleState()))
								{
									executeGoalMethod(m, goal, event)
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
													goal.setProcessingState(getInternalAccess(), RGoal.GoalProcessingState.FAILED);
												}
											}
										}
										
										public void exceptionOccurred(Exception exception)
										{
										}
									});
								}
							}
							
							return IFuture.DONE;
						}
					});
					List<EventType> events = new ArrayList<EventType>(cond.getEvents());
					events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED, mgoal.getName()}));
					rule.setEvents(events);
					getRuleSystem().getRulebase().addRule(rule);
//					rule.setEvents(cond.getEvents());
//					getRuleSystem().getRulebase().addRule(rule);
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
							for(final RGoal goal: getCapability().getGoals(mgoal))
							{
								if(!RGoal.GoalLifecycleState.SUSPENDED.equals(goal.getLifecycleState())
								  && !RGoal.GoalLifecycleState.DROPPING.equals(goal.getLifecycleState())
								  && !RGoal.GoalLifecycleState.DROPPED.equals(goal.getLifecycleState()))
								{	
									executeGoalMethod(m, goal, event)
										.addResultListener(new IResultListener<Boolean>()
									{
										public void resultAvailable(Boolean result)
										{
											if(!result.booleanValue())
											{
//												if(goal.getMGoal().getName().indexOf("AchieveCleanup")!=-1)
//													System.out.println("Goal suspended: "+goal);
												goal.setLifecycleState(getInternalAccess(), RGoal.GoalLifecycleState.SUSPENDED);
												goal.setState(RProcessableElement.State.INITIAL);
											}
										}
										
										public void exceptionOccurred(Exception exception)
										{
										}
									});
								}
							}
							return IFuture.DONE;
						}
					});
					List<EventType> events = new ArrayList<EventType>(cond.getEvents());
					events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED, mgoal.getName()}));
					rule.setEvents(events);
					getRuleSystem().getRulebase().addRule(rule);
					
//					rule.setEvents(cond.getEvents());
//					getRuleSystem().getRulebase().addRule(rule);
					
					rule = new Rule<Void>(mgoal.getName()+"_goal_option", 
						new GoalsExistCondition(mgoal, capa), new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
							for(final RGoal goal: getCapability().getGoals(mgoal))
							{
								if(RGoal.GoalLifecycleState.SUSPENDED.equals(goal.getLifecycleState()))
								{	
									executeGoalMethod(m, goal, event)
										.addResultListener(new IResultListener<Boolean>()
									{
										public void resultAvailable(Boolean result)
										{
											if(result.booleanValue())
											{
//												if(goal.getMGoal().getName().indexOf("AchieveCleanup")!=-1)
//													System.out.println("Goal made option: "+goal);
												goal.setLifecycleState(getInternalAccess(), RGoal.GoalLifecycleState.OPTION);
//												setState(ia, PROCESSABLEELEMENT_INITIAL);
											}
										}
										
										public void exceptionOccurred(Exception exception)
										{
										}
									});
								}
							}
							
							return IFuture.DONE;
						}
					});
					rule.setEvents(events);
					getRuleSystem().getRulebase().addRule(rule);
					
//					rule.setEvents(cond.getEvents());
//					getRuleSystem().getRulebase().addRule(rule);
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
						public IFuture<Void> execute(final IEvent event, final IRule<Void> rule, final Object context, Object condresult)
						{
							for(final RGoal goal: getCapability().getGoals(mgoal))
							{
								executeGoalMethod(m, goal, event)
									.addResultListener(new IResultListener<Boolean>()
								{
									public void resultAvailable(Boolean result)
									{
										if(result.booleanValue())
										{
											goal.targetConditionTriggered(getInternalAccess(), event, rule, context);
										}
									}
									
									public void exceptionOccurred(Exception exception)
									{
									}
								});
							}
						
							return IFuture.DONE;
						}
					});
					List<EventType> events = new ArrayList<EventType>(cond.getEvents());
					events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED, mgoal.getName()}));
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
							for(final RGoal goal: getCapability().getGoals(mgoal))
							{
								if(RGoal.GoalLifecycleState.ACTIVE.equals(goal.getLifecycleState())
									&& RGoal.GoalProcessingState.PAUSED.equals(goal.getProcessingState()))
								{	
									executeGoalMethod(m, goal, event)
										.addResultListener(new IResultListener<Boolean>()
									{
										public void resultAvailable(Boolean result)
										{
											if(result.booleanValue())
											{
												goal.setTriedPlans(null);
												goal.setApplicablePlanList(null);
												goal.setProcessingState(getInternalAccess(), RGoal.GoalProcessingState.INPROCESS);
											}
										}
										
										public void exceptionOccurred(Exception exception)
										{
										}
									});
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
							for(final RGoal goal: getCapability().getGoals(mgoal))
							{
								if(RGoal.GoalLifecycleState.ACTIVE.equals(goal.getLifecycleState())
									&& RGoal.GoalProcessingState.IDLE.equals(goal.getProcessingState()))
								{	
									executeGoalMethod(m, goal, event)
										.addResultListener(new IResultListener<Boolean>()
									{
										public void resultAvailable(Boolean result)
										{
											if(!result.booleanValue())
											{
//												System.out.println("Goal maintain triggered: "+goal);
//												System.out.println("state was: "+goal.getProcessingState());
												goal.setProcessingState(getInternalAccess(), RGoal.GoalProcessingState.INPROCESS);
											}
										}
										
										public void exceptionOccurred(Exception exception)
										{
										}
									});
								}
							}
							return IFuture.DONE;
						}
					});
					List<EventType> events = new ArrayList<EventType>(cond.getEvents());
					events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED, mgoal.getName()}));
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
							public IFuture<Void> execute(final IEvent event, final IRule<Void> rule, final Object context, Object condresult)
							{
								for(final RGoal goal: getCapability().getGoals(mgoal))
								{
									executeGoalMethod(m, goal, event)
										.addResultListener(new IResultListener<Boolean>()
									{
										public void resultAvailable(Boolean result)
										{
											if(result.booleanValue())
											{
												goal.targetConditionTriggered(getInternalAccess(), event, rule, context);
											}
										}
										
										public void exceptionOccurred(Exception exception)
										{
										}
									});
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
					RPlan.executePlan(rplan, getInternalAccess(), null);
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
				
				List<MGoal> gfs = trigger.getGoalFinisheds();
				if(gfs!=null && gfs.size()>0)
				{
					Rule<Void> rule = new Rule<Void>("create_plan_goalfinished_"+mplan.getName(), new ICondition()
					{
						public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
						{
							return new Future<Tuple2<Boolean, Object>>(TRUE);
						}
					}, createplan);
					for(MGoal gf: gfs)
					{
						rule.addEvent(new EventType(new String[]{ChangeEvent.GOALDROPPED, gf.getName()}));
					}
					rulesystem.getRulebase().addRule(rule);
				}
			}
			
			// context condition
			
			final MethodInfo mi = mplan.getBody().getContextConditionMethod(getClassLoader());
			if(mi!=null)
			{
				PlanContextCondition pcc = mi.getMethod(getClassLoader()).getAnnotation(PlanContextCondition.class);
				String[] evs = pcc.beliefs();
				RawEvent[] rawevs = pcc.rawevents();
				List<EventType> events = new ArrayList<EventType>();
				for(String ev: evs)
				{
					addBeliefEvents(getInternalAccess(), events, ev);
				}
				for(RawEvent rawev: rawevs)
				{
					events.add(createEventType(rawev));
				}
				
				IAction<Void> abortplans = new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
					{
						Collection<RPlan> coll = capa.getPlans(mplan);
						
						for(final RPlan plan: coll)
						{
							invokeBooleanMethod(plan.getBody().getBody(agent), mi.getMethod(getClassLoader()), plan.getModelElement(), event, plan)
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
				events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED, EventType.MATCHALL}));
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
							if(!other.isInhibitedBy(goal) && other.inhibits(goal, getInternalAccess()))
							{
								goal.addInhibitor(other, getInternalAccess());
							}
						}
						
						return IFuture.DONE;
					}
				});
				rule.setEvents(events);
				getRuleSystem().getRulebase().addRule(rule);
				
				events = getGoalEvents(null);
				rule = new Rule<Void>("goal_addinhibitor", 
					new ICondition()
					{
						public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
						{
	//						if(((RGoal)event.getContent()).getId().indexOf("Battery")!=-1)
	//							System.out.println("maintain");
//							if(getComponentIdentifier().getName().indexOf("Ambu")!=-1)
//								System.out.println("addin");
							
							// return true when other goal is active and inprocess
							boolean ret = false;
							EventType type = event.getType();
							RGoal goal = (RGoal)event.getContent();
							ret = ChangeEvent.GOALACTIVE.equals(type.getType(0)) && RGoal.GoalProcessingState.INPROCESS.equals(goal.getProcessingState())
								|| (ChangeEvent.GOALINPROCESS.equals(type.getType(0)) && RGoal.GoalLifecycleState.ACTIVE.equals(goal.getLifecycleState()));
//							return ret? ICondition.TRUE: ICondition.FALSE;
							return new Future<Tuple2<Boolean,Object>>(ret? ICondition.TRUE: ICondition.FALSE);
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
							if(inhs!=null)
							{
								for(MGoal inh: inhs)
								{
									Collection<RGoal> goals = getCapability().getGoals(inh);
									for(RGoal other: goals)
									{
	//									if(!other.isInhibitedBy(goal) && goal.inhibits(other, getInternalAccess()))
										if(!goal.isInhibitedBy(other) && goal.inhibits(other, getInternalAccess()))
										{
											other.addInhibitor(goal, getInternalAccess());
										}
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
						public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
						{
//							if(getComponentIdentifier().getName().indexOf("Ambu")!=-1)
//								System.out.println("remin");
							
							// return true when other goal is active and inprocess
							boolean ret = false;
							EventType type = event.getType();
							if(event.getContent() instanceof RGoal)
							{
								RGoal goal = (RGoal)event.getContent();
								ret = ChangeEvent.GOALSUSPENDED.equals(type.getType(0)) || ChangeEvent.GOALOPTION.equals(type.getType(0))
									|| !RGoal.GoalProcessingState.INPROCESS.equals(goal.getProcessingState());
							}
//							return ret? ICondition.TRUE: ICondition.FALSE;
							return new Future<Tuple2<Boolean,Object>>(ret? ICondition.TRUE: ICondition.FALSE);
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
							if(inhs!=null)
							{
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
							
							// Remove inhibitor from goals of same type if cardinality is used
							if(delib.isCardinalityOne())
							{
								Collection<RGoal> goals = getCapability().getGoals(goal.getMGoal());
								if(goals!=null)
								{
									for(RGoal other: goals)
									{
										if(goal.equals(other))
											continue;
										
										if(other.isInhibitedBy(goal))
											other.removeInhibitor(goal, getInternalAccess());
									}
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
				rule.addEvent(new EventType(new String[]{ChangeEvent.GOALINHIBITED, EventType.MATCHALL}));
				getRuleSystem().getRulebase().addRule(rule);
			}
			
			Rule<Void> rule = new Rule<Void>("goal_activate", 
				new CombinedCondition(new ICondition[]{
					new LifecycleStateCondition(RGoal.GoalLifecycleState.OPTION),
					new ICondition()
					{
						public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
						{
							RGoal goal = (RGoal)event.getContent();
//							return !goal.isInhibited()? ICondition.TRUE: ICondition.FALSE;
							return new Future<Tuple2<Boolean,Object>>(!goal.isInhibited()? ICondition.TRUE: ICondition.FALSE);
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
			rule.addEvent(new EventType(new String[]{ChangeEvent.GOALNOTINHIBITED, EventType.MATCHALL}));
			rule.addEvent(new EventType(new String[]{ChangeEvent.GOALOPTION, EventType.MATCHALL}));
//			rule.setEvents(SUtil.createArrayList(new String[]{ChangeEvent.GOALNOTINHIBITED, ChangeEvent.GOALOPTION}));
			getRuleSystem().getRulebase().addRule(rule);
		}
		
		// perform init write fields (after injection of bdiagent)
		BDIAgent.performInitWrites((BDIAgent)microagent);
		
		// Start rule system
		inited	= true;
//		if(getComponentIdentifier().getName().indexOf("Cleaner")!=-1)// && getComponentIdentifier().getName().indexOf("Burner")==-1)
//			getCapability().dumpPlansPeriodically(getInternalAccess());
//		if(getComponentIdentifier().getName().indexOf("Ambulance")!=-1)
//		{
//			getCapability().dumpGoalsPeriodically(getInternalAccess());
//			getCapability().dumpPlansPeriodically(getInternalAccess());
//		}
		
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		
//		throw new RuntimeException();
	}
	
	/**
	 *  Called before blocking the component thread.
	 */
	public void	beforeBlock()
	{
		RPlan	rplan	= ExecutePlanStepAction.RPLANS.get();
		testBodyAborted(rplan);
		ComponentSuspendable sus = ComponentSuspendable.COMSUPS.get();
		if(rplan!=null && sus!=null && !RPlan.PlanProcessingState.WAITING.equals(rplan.getProcessingState()))
		{
			final ResumeCommand<Void> rescom = rplan.new ResumeCommand<Void>(sus, false);
			rplan.setProcessingState(PlanProcessingState.WAITING);
			rplan.resumecommand = rescom;
		}
	}
	
	/**
	 *  Called after unblocking the component thread.
	 */
	public void	afterBlock()
	{
		RPlan rplan = ExecutePlanStepAction.RPLANS.get();
		testBodyAborted(rplan);
		if(rplan!=null)
		{
			rplan.setProcessingState(PlanProcessingState.RUNNING);
			if(rplan.resumecommand!=null)
			{
				// performs only cleanup without setting future
				rplan.resumecommand.execute(Boolean.FALSE);
				rplan.resumecommand = null;
			}
		}
	}
	
	/**
	 *  Check if plan is already aborted.
	 */
	protected void testBodyAborted(RPlan rplan)
	{
		// Throw error to exit body method of aborted plan.
		if(rplan!=null && rplan.aborted && rplan.getLifecycleState()==PlanLifecycleState.BODY)
		{
//			System.out.println("aborting after block: "+rplan);
			throw new BodyAborted();
		}
	}
	
	/**
	 *  Execute a goal method.
	 */
	protected IFuture<Boolean> executeGoalMethod(Method m, RProcessableElement goal, IEvent event)
	{
		return invokeBooleanMethod(goal.getPojoElement(), m, goal.getModelElement(), event, null);
	}
	
	/**
	 *  Get parameter values for injection into method and constructor calls.
	 */
	public Object[] getInjectionValues(Class<?>[] ptypes, Annotation[][] anns, MElement melement, ChangeEvent event, RPlan rplan, RProcessableElement rpe)
	{
		return getInjectionValues(ptypes, anns, melement, event, rplan, rpe, null);
	}
	
	// todo: support parameter names via annotation in guesser (guesser with meta information)
	/**
	 *  Get parameter values for injection into method and constructor calls.
	 *  @return A valid assigment or null if no assignment could be found.
	 */
	protected Object[]	getInjectionValues(Class<?>[] ptypes, Annotation[][] anns, MElement melement, ChangeEvent event, RPlan rplan, RProcessableElement rpe, Collection<Object> vs)
	{
		Collection<Object> vals = new LinkedHashSet<Object>();
		if(vs!=null)
			vals.addAll(vs);
		
		// Find capability based on model element (or use agent).
		String	capaname	= null;
		if(melement!=null)
		{
			int idx = melement.getName().lastIndexOf(MElement.CAPABILITY_SEPARATOR);
			if(idx!=-1)
			{
				capaname = melement.getName().substring(0, idx);
			}
		}
		Object capa = capaname!=null ? getCapabilityObject(capaname)
			: getAgent() instanceof PojoBDIAgent? ((PojoBDIAgent)getAgent()).getPojoAgent(): getAgent();
		vals.add(capa);
		vals.add(new CapabilityWrapper((BDIAgent)getAgent(), capa, capaname));
		vals.add(getAgent());
		vals.add(getExternalAccess());

		// Add plan values if any.
		if(rplan!=null)
		{
			Object reason = rplan.getReason();
			if(reason instanceof RProcessableElement && rpe==null)
			{
				rpe	= (RProcessableElement)reason;
			}
			vals.add(reason);
			vals.add(rplan);
			
			if(rplan.getException()!=null)
			{
				vals.add(rplan.getException());
			}
			
			Object delem = rplan.getDispatchedElement();
			if(delem instanceof ChangeEvent && event==null)
			{
				event = (ChangeEvent)delem;
			}
		}
		// Todo: cond result!?
		
		// Add event values
		if(event!=null)
		{
			vals.add(event);
			vals.add(event.getValue());
			if(event.getValue() instanceof ChangeInfo)
			{
				vals.add(new ChangeInfoEntryMapper((ChangeInfo<?>)event.getValue()));
				vals.add(((ChangeInfo<?>)event.getValue()).getValue());
			}
		}

		// Add processable element values if any (for plan and APL).
		if(rpe!=null)
		{
			vals.add(rpe);
			if(rpe.getPojoElement()!=null)
			{
				vals.add(rpe.getPojoElement());
				if(rpe instanceof RGoal)
				{
					Object pojo = rpe.getPojoElement();
					MGoal mgoal = (MGoal)rpe.getModelElement();
					List<MParameter> params = mgoal.getParameters();
					for(MParameter param: params)
					{
						Object val = param.getValue(pojo, getClassLoader());
						vals.add(val);
					}
				}
			}
			if(rpe.getPojoElement() instanceof InvocationInfo)
			{
				vals.add(((InvocationInfo)rpe.getPojoElement()).getParams());
			}
		}
		
		// Fill in values from annotated events or using parameter guesser.
		boolean[] notnulls = new boolean[ptypes.length];
		
		Object[]	ret	= new Object[ptypes.length];
		SimpleParameterGuesser	g	= new SimpleParameterGuesser(vals);
		for(int i=0; i<ptypes.length; i++)
		{
			boolean	done	= false;
			for(int j=0; !done && anns!=null && j<anns[i].length; j++)
			{
				if(anns[i][j] instanceof Event)
				{
					done	= true;
					String	source	= ((Event)anns[i][j]).value();
					if(capaname!=null)
					{
						source	= capaname + MElement.CAPABILITY_SEPARATOR + source;
					}
					if(getBDIModel().getBeliefMappings().containsKey(source))
					{
						source	= getBDIModel().getBeliefMappings().get(source);
					}
					
					if(event!=null && event.getSource()!=null && event.getSource().equals(source))
					{
						boolean set = false;
						if(SReflect.isSupertype(ptypes[i], ChangeEvent.class))
						{
							ret[i]	= event;
							set = true;
						}
						else
						{
							if(SReflect.getWrappedType(ptypes[i]).isInstance(event.getValue()))
							{
								ret[i]	= event.getValue();
								set = true;
							}
							else if(event.getValue() instanceof ChangeInfo)
							{
								final ChangeInfo<?> ci = (ChangeInfo<?>)event.getValue();
								if(ptypes[i].equals(ChangeInfo.class))
								{
									ret[i] = ci;
									set = true;
								}
								else if(SReflect.getWrappedType(ptypes[i]).isInstance(ci.getValue()))
								{
									ret[i] = ci.getValue();
									set = true;
								}
								else if(ptypes[i].equals(Map.Entry.class))
								{
									ret[i] = new ChangeInfoEntryMapper(ci);
									set = true;
								}
							}
						}
						if(!set)
						{
							throw new IllegalArgumentException("Unexpected type for event injection: "+event+", "+ptypes[i]);
						}
						
//						else if(SReflect.isSupertype(ptypes[i], ChangeEvent.class))
//						{
//							ret[i]	= event;
//						}
//						else
//						{
//							throw new IllegalArgumentException("Unexpected type for event injection: "+event+", "+ptypes[i]);
//						}
					}
					else
					{
						MBelief	mbel	= getBDIModel().getCapability().getBelief(source);
						ret[i]	= mbel.getValue(this);

					}
				}
				else if(anns[i][j] instanceof CheckNotNull)
				{
					notnulls[i] = true;
				}
			}
			
			if(!done)
			{
				ret[i]	= g.guessParameter(ptypes[i], false);
			}
		}
		

		// Adapt values (e.g. change events) to capability.
		if(capaname!=null)
		{
			for(int i=0; i<ret.length; i++)
			{
				ret[i]	= adaptToCapability(ret[i], capaname);
			}
		}
		
		for(int i=0; i<ptypes.length; i++)
		{
			if(notnulls[i] && ret[i]==null)
			{
				ret = null;
				break;
			}
		}
		
		return ret;
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
//		boolean aborted = false;
//		if(rulesystem!=null)
//			aborted = rulesystem.processAllEvents(15);
//		if(aborted)
//			getCapability().dumpGoals();
		
		if(inited && rulesystem!=null)
			rulesystem.processAllEvents();
		
//		if(steps!=null && steps.size()>0)
//		{
//			System.out.println(getComponentIdentifier()+" steps: "+steps.size()+" "+steps.get(0).getStep().getClass());
//		}
		boolean ret = super.executeStep();
		
//		System.out.println(getComponentIdentifier()+" after step");

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
	 *  Get the inited.
	 *  @return The inited.
	 */
	public boolean isInited()
	{
		return inited;
	}
	
//	/**
//	 *  Create a result listener which is executed as an component step.
//	 *  @param The original listener to be called.
//	 *  @return The listener.
//	 */
//	public <T> IResultListener<T> createResultListener(IResultListener<T> listener)
//	{
//		// Must override method to ensure that plan steps are executed with planstepactions
//		if(ExecutePlanStepAction.RPLANS.get()!=null && !(listener instanceof BDIComponentResultListener))
//		{
//			return new BDIComponentResultListener(listener, this);
//		}
//		else
//		{
//			return super.createResultListener(listener);
//		}
//	}
	
	/**
	 * 
	 */
	protected IFuture<Boolean> invokeBooleanMethod(Object pojo, Method m, MElement modelelement, IEvent event, RPlan rplan)
	{
		final Future<Boolean> ret = new Future<Boolean>();
		try
		{
			m.setAccessible(true);
			
			Object[] vals = getInjectionValues(m.getParameterTypes(), m.getParameterAnnotations(),
				modelelement, event!=null ? new ChangeEvent(event) : null, rplan, null);
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
	public static List<EventType> getGoalEvents(MGoal mgoal)
	{
		List<EventType> events = new ArrayList<EventType>();
		if(mgoal==null)
		{
			events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED, EventType.MATCHALL}));
			events.add(new EventType(new String[]{ChangeEvent.GOALDROPPED, EventType.MATCHALL}));
			
			events.add(new EventType(new String[]{ChangeEvent.GOALOPTION, EventType.MATCHALL}));
			events.add(new EventType(new String[]{ChangeEvent.GOALACTIVE, EventType.MATCHALL}));
			events.add(new EventType(new String[]{ChangeEvent.GOALSUSPENDED, EventType.MATCHALL}));
			
			events.add(new EventType(new String[]{ChangeEvent.GOALINPROCESS, EventType.MATCHALL}));
			events.add(new EventType(new String[]{ChangeEvent.GOALNOTINPROCESS, EventType.MATCHALL}));
		}
		else
		{
			String name = mgoal.getName();
			events.add(new EventType(new String[]{ChangeEvent.GOALADOPTED, name}));
			events.add(new EventType(new String[]{ChangeEvent.GOALDROPPED, name}));
			
			events.add(new EventType(new String[]{ChangeEvent.GOALOPTION, name}));
			events.add(new EventType(new String[]{ChangeEvent.GOALACTIVE, name}));
			events.add(new EventType(new String[]{ChangeEvent.GOALSUSPENDED, name}));
			
			events.add(new EventType(new String[]{ChangeEvent.GOALINPROCESS, name}));
			events.add(new EventType(new String[]{ChangeEvent.GOALNOTINPROCESS, name}));
		}
		
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
					if(type.length()==0)
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
	 *  Map a change info as Map:Entry.
	 */
	public static class ChangeInfoEntryMapper implements Map.Entry
	{
		protected ChangeInfo<?>	ci;

		public ChangeInfoEntryMapper(ChangeInfo<?> ci)
		{
			this.ci = ci;
		}

		public Object getKey()
		{
			return ci.getInfo();
		}

		public Object getValue()
		{
			return ci.getValue();
		}

		public Object setValue(Object value)
		{
			throw new UnsupportedOperationException();
		}

		public boolean equals(Object obj)
		{
			boolean	ret	= false;
			
			if(obj instanceof Map.Entry)
			{
				Map.Entry<?,?>	e1	= this;
				Map.Entry<?,?>	e2	= (Map.Entry<?,?>)obj;
				ret	= (e1.getKey()==null ? e2.getKey()==null : e1.getKey().equals(e2.getKey()))
					&& (e1.getValue()==null ? e2.getValue()==null : e1.getValue().equals(e2.getValue()));
			}
			
			return ret;
		}

		public int hashCode()
		{
			return (getKey()==null ? 0 : getKey().hashCode())
				^ (getValue()==null ? 0 : getValue().hashCode());
		}
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
		public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
		{
			RGoal goal = (RGoal)event.getContent();
			boolean ret = states.contains(goal.getLifecycleState());
			if(!allowed)
				ret = !ret;
//			return ret? ICondition.TRUE: ICondition.FALSE;
			return new Future<Tuple2<Boolean,Object>>(ret? ICondition.TRUE: ICondition.FALSE);
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
		public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
		{
			boolean res = !capa.getGoals(mgoal).isEmpty();
//			return res? ICondition.TRUE: ICondition.FALSE;
			return new Future<Tuple2<Boolean,Object>>(res? ICondition.TRUE: ICondition.FALSE);
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
		public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
		{
			return new Future<Tuple2<Boolean,Object>>(!capa.getPlans(mplan).isEmpty()? ICondition.TRUE: ICondition.FALSE);
		}
	}
	
	/**
	 *  Get the current state as events.
	 */
	public List<IMonitoringEvent> getCurrentStateEvents()
	{
		List<IMonitoringEvent> ret = new ArrayList<IMonitoringEvent>();
		
		// Already gets merged beliefs (including subcapas).
		List<MBelief> mbels = getBDIModel().getCapability().getBeliefs();
		
		if(mbels!=null)
		{
			for(MBelief mbel: mbels)
			{
				BeliefInfo info = BeliefInfo.createBeliefInfo(this, mbel, getClassLoader());
				MonitoringEvent ev = new MonitoringEvent(getComponentIdentifier(), getComponentDescription().getCreationTime(), IMonitoringEvent.EVENT_TYPE_CREATION+"."+IMonitoringEvent.SOURCE_CATEGORY_FACT, System.currentTimeMillis(), PublishEventLevel.FINE);
				ev.setSourceDescription(mbel.toString());
				ev.setProperty("details", info);
				ret.add(ev);
			}
		}
		
		// Goals of this capability.
		Collection<RGoal> goals = getCapability().getGoals();
		if(goals!=null)
		{
			for(RGoal goal: goals)
			{
				GoalInfo info = GoalInfo.createGoalInfo(goal);
				MonitoringEvent ev = new MonitoringEvent(getComponentIdentifier(), getComponentDescription().getCreationTime(), IMonitoringEvent.EVENT_TYPE_CREATION+"."+IMonitoringEvent.SOURCE_CATEGORY_GOAL, System.currentTimeMillis(), PublishEventLevel.FINE);
				ev.setSourceDescription(goal.toString());
				ev.setProperty("details", info);
				ret.add(ev);
			}
		}
		
		// Plans of this capability.
		Collection<RPlan> plans	= getCapability().getPlans();
		if(plans!=null)
		{
			for(RPlan plan: plans)
			{
				PlanInfo info = PlanInfo.createPlanInfo(plan);
				MonitoringEvent ev = new MonitoringEvent(getComponentIdentifier(), getComponentDescription().getCreationTime(), IMonitoringEvent.EVENT_TYPE_CREATION+"."+IMonitoringEvent.SOURCE_CATEGORY_PLAN, System.currentTimeMillis(), PublishEventLevel.FINE);
				ev.setSourceDescription(plan.toString());
				ev.setProperty("details", info);
				ret.add(ev);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the capability part of a complex element name.
	 */
	public static String getCapabilityPart(String name)
	{
		String ret = null;
		int	idx = name.lastIndexOf(MElement.CAPABILITY_SEPARATOR);
		if(idx!=-1)
		{
			ret = name.substring(0, idx);
		}
		return ret;
	}
	
	/**
	 *  Get the name part of a complex element name.
	 */
	public static String getNamePart(String name)
	{
		String ret = name;
		int	idx = name.lastIndexOf("$");
		if(idx==-1)
		{
			idx = name.lastIndexOf(".");
		}
		if(idx==-1)
		{	
			idx = name.lastIndexOf(MElement.CAPABILITY_SEPARATOR);
		}
		if(idx!=-1)
		{	
			ret = name.substring(idx+1);
		}
		return ret;
	}
	
	/**
	 *  Get beautified element name.
	 */
	public static String getBeautifiedName(String name)
	{
		String capa = getCapabilityPart(name);
		String pname = getNamePart(name);
		return capa!=null? capa.replace(MElement.CAPABILITY_SEPARATOR, ".")+"."+pname: pname;
	}
	
	/**
	 * 
	 */
	public static EventType createEventType(RawEvent rawev)
	{
		String[] p = new String[2];
		p[0] = rawev.value();
		p[1] = Object.class.equals(rawev.secondc())? rawev.second(): rawev.secondc().getName();
//		System.out.println("eveve: "+p[0]+" "+p[1]);
		return new EventType(p);
	}
}

