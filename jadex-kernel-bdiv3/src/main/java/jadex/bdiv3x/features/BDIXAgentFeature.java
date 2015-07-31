package jadex.bdiv3x.features;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import jadex.bdiv3.actions.ExecutePlanStepAction;
import jadex.bdiv3.annotation.RawEvent;
import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.IBDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IBeliefListener;
import jadex.bdiv3.runtime.IGoal.GoalLifecycleState;
import jadex.bdiv3.runtime.impl.BeliefInfo;
import jadex.bdiv3.runtime.impl.BodyAborted;
import jadex.bdiv3.runtime.impl.CapabilityPojoWrapper;
import jadex.bdiv3.runtime.impl.GoalInfo;
import jadex.bdiv3.runtime.impl.InvocationInfo;
import jadex.bdiv3.runtime.impl.PlanInfo;
import jadex.bdiv3.runtime.impl.RCapability;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bdiv3.runtime.impl.RPlan.PlanLifecycleState;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.bdiv3.runtime.wrappers.ListWrapper;
import jadex.bdiv3.runtime.wrappers.MapWrapper;
import jadex.bdiv3.runtime.wrappers.SetWrapper;
import jadex.bdiv3x.runtime.CapabilityWrapper;
import jadex.bdiv3x.runtime.IBeliefbase;
import jadex.bdiv3x.runtime.IEventbase;
import jadex.bdiv3x.runtime.IExpressionbase;
import jadex.bdiv3x.runtime.IGoalbase;
import jadex.bdiv3x.runtime.IPlanbase;
import jadex.bdiv3x.runtime.RBeliefbase;
import jadex.bdiv3x.runtime.REventbase;
import jadex.bdiv3x.runtime.RExpressionbase;
import jadex.bdiv3x.runtime.RGoalbase;
import jadex.bdiv3x.runtime.RPlanbase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.ILifecycleComponentFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.commons.IFilter;
import jadex.commons.IResultCommand;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.SimpleParameterGuesser;
import jadex.commons.Tuple2;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.rules.eca.ChangeInfo;
import jadex.rules.eca.EventType;
import jadex.rules.eca.IAction;
import jadex.rules.eca.ICondition;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.Rule;
import jadex.rules.eca.RuleSystem;
import jadex.rules.eca.annotations.Event;

/**
 *  BDI agent feature version for XML agents.
 */
public class BDIXAgentFeature extends AbstractComponentFeature implements IBDIXAgentFeature, IInternalBDIAgentFeature
{
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(IBDIXAgentFeature.class, BDIXAgentFeature.class,  
//		new Class[]{IMicroLifecycleFeature.class}, null);
		null, new Class[]{ILifecycleComponentFeature.class, IProvidedServicesFeature.class}, new Class<?>[]{IInternalBDIAgentFeature.class});
	
	/** The bdi model. */
	protected IBDIModel bdimodel;
	
	/** The rule system. */
	protected RuleSystem rulesystem;
	
	/** The bdi state. */
	protected RCapability capa;
	
	/** The event adders. */
//	protected Map<EventType, IResultCommand<IFuture<Void>, PropertyChangeEvent>> eventadders 
//		= new HashMap<EventType, IResultCommand<IFuture<Void>,PropertyChangeEvent>>();
	
//	/** Is the agent inited and allowed to execute rules? */
//	protected boolean	inited;
	
	//-------- constructors --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public BDIXAgentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
		
		this.bdimodel = (IBDIModel)getComponent().getModel();
		this.capa = new RCapability(bdimodel.getCapability(), component);
		this.rulesystem = new RuleSystem(null, true)
		{
			public IFuture<Void> addEvent(IEvent event) 
			{
				// Implement atomic by changing the rule execution mode
				RPlan rplan = ExecutePlanStepAction.RPLANS.get();
				
				boolean	queue	= isQueueEvents();
				if(rplan!=null && !queue && rplan.isAtomic())
					setQueueEvents(true);
				
				IFuture<Void> ret = super.addEvent(event);
				
				if(queue!=isQueueEvents())
					setQueueEvents(queue);
				
				return ret;
			}
		};
	}

	/**
	 *  Initialize the feature.
	 *  Empty implementation that can be overridden.
	 */
	public IFuture<Void> init()
	{
		RBeliefbase bb = new RBeliefbase(getComponent());
		getCapability().setBeliefbase(bb);
		bb.init();
		
		RExpressionbase eb = new RExpressionbase(getComponent());
		getCapability().setExpressionbase(eb);
		
		RGoalbase gb = new RGoalbase(getComponent());
		getCapability().setGoalbase(gb);
		
		RPlanbase pb = new RPlanbase(getComponent());
		getCapability().setPlanbase(pb);

		REventbase evb = new REventbase(getComponent());
		getCapability().setEventbase(evb);

		// cannot do this in constructor because it needs access to this feature in expressions
	
//		injectAgent(getComponent(), pojo, bdimodel, null);
//		invokeInitCalls(pojo);
//		initCapabilities(pojo, bdimodel.getSubcapabilities() , 0);
//		startBehavior();
		return IFuture.DONE;
	}
	
	
//	/**
//	 *  Init beliefbase after services have been created.
//	 */
//	public IFuture<Void> body()
//	{
//		RBeliefbase bb = new RBeliefbase(getComponent());
//		getCapability().setBeliefbase(bb);
//		bb.init();
//		
//		RExpressionBase eb = new RExpressionBase(getComponent());
//		getCapability().setExpressionbase(eb);
//		
//		RGoalbase gb = new RGoalbase(getComponent());
//		getCapability().setGoalbase(gb);
//
//		// cannot do this in constructor because it needs access to this feature in expressions
//	
////		injectAgent(getComponent(), pojo, bdimodel, null);
////		invokeInitCalls(pojo);
////		initCapabilities(pojo, bdimodel.getSubcapabilities() , 0);
////		startBehavior();
//		return IFuture.DONE;
//	}
	
	//-------- internal method used for rewriting field access -------- 
	
	/**
	 *  Add an entry to the init calls.
	 *  
	 *  @param obj object instance that owns the field __initargs
	 *  @param clazz Class definition of the obj object
	 *  @param argtypes Signature of the init method
	 *  @param args Actual argument values for the init method
	 */
	public static void	addInitArgs(Object obj, Class<?> clazz, Class<?>[] argtypes, Object[] args)
	{
		try
		{
			Field f	= clazz.getDeclaredField("__initargs");
//				System.out.println(f+", "+SUtil.arrayToString(args));
			f.setAccessible(true);
			List<Tuple2<Class<?>[], Object[]>> initcalls	= (List<Tuple2<Class<?>[], Object[]>>)f.get(obj);
			if(initcalls==null)
			{
				initcalls	= new ArrayList<Tuple2<Class<?>[], Object[]>>();
				f.set(obj, initcalls);
			}
			initcalls.add(new Tuple2<Class<?>[], Object[]>(argtypes, args));
		}
		catch(Exception e)
		{
			throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
		}
	}
	
	/**
	 *  Caution: this method is used from byte engineered code, change signature with caution
	 * 
	 *  Create a belief changed event.
	 *  @param val The new value.
	 *  @param agent The agent.
	 *  @param belname The belief name.
	 */
	public static void createChangeEvent(Object val, Object oldval, Object info, final IInternalAccess agent, final String belname)
//		public static void createChangeEvent(Object val, final BDIAgent agent, MBelief mbel)
	{
//			System.out.println("createEv: "+val+" "+agent+" "+belname);
//		BDIAgentInterpreter ip = (BDIAgentInterpreter)agent.getInterpreter();
		
		MBelief mbel = ((IInternalBDIAgentFeature)agent.getComponentFeature(IBDIXAgentFeature.class)).getBDIModel().getCapability().getBelief(belname);
		
		RuleSystem rs = ((IInternalBDIAgentFeature)agent.getComponentFeature(IBDIXAgentFeature.class)).getRuleSystem();
		rs.addEvent(new jadex.rules.eca.Event(ChangeEvent.BELIEFCHANGED+"."+belname, new ChangeInfo<Object>(val, oldval, info)));
		
		publishToolBeliefEvent(agent, mbel);
	}
	
	/**
	 * 
	 */
	public static void publishToolBeliefEvent(IInternalAccess ia, MBelief mbel)//, String evtype)
	{
		if(mbel!=null && ia.getComponentFeature0(IMonitoringComponentFeature.class)!=null && 
			ia.getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOSUBSCRIBERS, PublishEventLevel.FINE))
		{
			long time = System.currentTimeMillis();//getClockService().getTime();
			MonitoringEvent mev = new MonitoringEvent();
			mev.setSourceIdentifier(ia.getComponentIdentifier());
			mev.setTime(time);
			
			BeliefInfo info = BeliefInfo.createBeliefInfo(ia, mbel, ia.getClassLoader());
//				mev.setType(evtype+"."+IMonitoringEvent.SOURCE_CATEGORY_FACT);
			mev.setType(IMonitoringEvent.EVENT_TYPE_MODIFICATION+"."+IMonitoringEvent.SOURCE_CATEGORY_FACT);
//				mev.setProperty("sourcename", element.toString());
			mev.setProperty("sourcetype", info.getType());
			mev.setProperty("details", info);
			mev.setLevel(PublishEventLevel.FINE);
			
			ia.getComponentFeature(IMonitoringComponentFeature.class).publishEvent(mev, PublishTarget.TOSUBSCRIBERS);
		}
	}
	
	/**
	 * 
	 */
	protected static String getBeliefName(Object obj, String fieldname)
	{
		String	gn	= null;
		try
		{
			Field	gnf	= obj.getClass().getField("__globalname");
			gnf.setAccessible(true);
			gn	= (String)gnf.get(obj);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		String belname	= gn!=null ? gn + MElement.CAPABILITY_SEPARATOR + fieldname : fieldname;
		return belname;
	}
	
	//-------- methods for goal/plan parameter rewrites --------
	
//	/**
//	 *  Method that is called automatically when a parameter 
//	 *  is written as field access.
//	 */
//	public static void writeParameterField(Object val, String fieldname, Object obj, IInternalAccess agent)
//	{
////			System.out.println("write: "+val+" "+fieldname+" "+obj+" "+agent);
//		
//		// This is the case in inner classes
//		if(agent==null)
//		{
//			try
//			{
//				Tuple2<Field, Object> res = findFieldWithOuterClass(obj, "__agent");
////						System.out.println("res: "+res);
//				agent = (IInternalAccess)res.getFirstEntity().get(res.getSecondEntity());
//			}
//			catch(RuntimeException e)
//			{
//				throw e;
//			}
//			catch(Exception e)
//			{
//				throw new RuntimeException(e);
//			}
//		}
//
////		BDIAgentInterpreter ip = (BDIAgentInterpreter)agent.getInterpreter();
//		String elemname = obj.getClass().getName();
//		MGoal mgoal = ((IInternalBDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class)).getBDIModel().getCapability().getGoal(elemname);
//		
////			String paramname = elemname+"."+fieldname; // ?
//
//		if(mgoal!=null)
//		{
//			MParameter mparam = mgoal.getParameter(fieldname);
//			if(mparam!=null)
//			{
//				// Wrap collections of multi beliefs (if not already a wrapper)
//				if(mparam.isMulti(agent.getClassLoader()))
//				{
//					EventType addev = new EventType(new String[]{ChangeEvent.VALUEADDED, elemname, fieldname});
//					EventType remev = new EventType(new String[]{ChangeEvent.VALUEREMOVED, elemname, fieldname});
//					EventType chev = new EventType(new String[]{ChangeEvent.VALUECHANGED, elemname, fieldname});
//					if(val instanceof List && !(val instanceof ListWrapper))
//					{
//						val = new ListWrapper((List<?>)val, agent, addev, remev, chev, null);
//					}
//					else if(val instanceof Set && !(val instanceof SetWrapper))
//					{
//						val = new SetWrapper((Set<?>)val, agent, addev, remev, chev, null);
//					}
//					else if(val instanceof Map && !(val instanceof MapWrapper))
//					{
//						val = new MapWrapper((Map<?,?>)val, agent, addev, remev, chev, null);
//					}
//				}
//			}
//		}
//		
//		// agent is not null any more due to deferred exe of init expressions but rules are
//		// available only after startBehavior
//		if(((IBDILifecycleAgentFeature)agent.getComponentFeature(ILifecycleComponentFeature.class)).isInited())
//		{
//			EventType chev1 = new EventType(new String[]{ChangeEvent.PARAMETERCHANGED, elemname, fieldname});
//			EventType chev2 = new EventType(new String[]{ChangeEvent.VALUECHANGED, elemname, fieldname});
//			((BDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class)).writeField(val, null, fieldname, obj, chev1, chev2);
//		}
////				else
////				{
////					// In init set field immediately but throw events later, when agent is available.
////					
////					try
////					{
////						setFieldValue(obj, fieldname, val);
////					}
////					catch(Exception e)
////					{
////						e.printStackTrace();
////						throw new RuntimeException(e);
////					}
////					synchronized(initwrites)
////					{
////						List<Object[]> inits = initwrites.get(agent);
////						if(inits==null)
////						{
////							inits = new ArrayList<Object[]>();
////							initwrites.put(agent, inits);
////						}
////						inits.add(new Object[]{val, belname});
////					}
////				}
//	}
	
//	/**
//	 *  Method that is called automatically when a belief 
//	 *  is written as array access.
//	 */
//	// todo: allow init writes in constructor also for arrays
//	public static void writeArrayParameterField(Object array, final int index, Object val, Object agentobj, String fieldname)
//	{
//		// This is the case in inner classes
//		IInternalAccess agent = null;
//		if(agentobj instanceof IInternalAccess)
//		{
//			agent = (IInternalAccess)agentobj;
//		}
//		else
//		{
//			try
//			{
//				Tuple2<Field, Object> res = findFieldWithOuterClass(agentobj, "__agent");
////					System.out.println("res: "+res);
//				agent = (IInternalAccess)res.getFirstEntity().get(res.getSecondEntity());
//			}
//			catch(RuntimeException e)
//			{
//				throw e;
//			}
//			catch(Exception e)
//			{
//				throw new RuntimeException(e);
//			}
//		}
//		
////		final BDIAgentInterpreter ip = (BDIAgentInterpreter)agent.getInterpreter();
//		
//		assert agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
//
//		// Test if array store is really a belief store instruction by
//		// looking up the current belief value and comparing it with the
//		// array that is written
//		
//		boolean isparamwrite = false;
//		
//		MGoal mgoal = ((MCapability)((IInternalBDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class)).getCapability().getModelElement()).getGoal(agentobj.getClass().getName());
//		if(mgoal!=null)
//		{
//			MParameter mparam = mgoal.getParameter(fieldname);
//			if(mparam!=null)
//			{
//				Object curval = mparam.getValue(agentobj, agent.getClassLoader());
//				isparamwrite = curval==array;
//			}
//		}
//		RuleSystem rs = ((IInternalBDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class)).getRuleSystem();
////			System.out.println("write array index: "+val+" "+index+" "+array+" "+agent+" "+fieldname);
//		
//		Object oldval = null;
//		if(isparamwrite)
//		{
//			oldval = Array.get(array, index);
//			rs.unobserveObject(oldval);	
//		}
//		
//		Class<?> ct = array.getClass().getComponentType();
//		if(boolean.class.equals(ct))
//		{
//			val = ((Integer)val)==1? Boolean.TRUE: Boolean.FALSE;
//		}
//		else if(byte.class.equals(ct))
//		{
////				val = new Byte(((Integer)val).byteValue());
//			val = Byte.valueOf(((Integer)val).byteValue());
//		}
//		Array.set(array, index, val);
//		
//		if(isparamwrite)
//		{
//			
//			if(!SUtil.equals(val, oldval))
//			{
//				jadex.rules.eca.Event ev = new jadex.rules.eca.Event(new EventType(new String[]{ChangeEvent.VALUECHANGED, mgoal.getName(), fieldname}), new ChangeInfo<Object>(val, oldval, Integer.valueOf(index)));
//				rs.addEvent(ev);
//				// execute rulesystem immediately to ensure that variable values are not changed afterwards
//				rs.processAllEvents(); 
//			}
//		}
//	}
	
	/**
	 * 
	 */
	protected boolean isComponentThread()
	{
		return getComponent().getComponentFeature(IExecutionFeature.class).isComponentThread();
	}
	
//	/**
//	 *  Get the inited.
//	 *  @return The inited.
//	 */
//	public boolean isInited()
//	{
//		return inited;
//	}
	
//			this.bdimodel = model;
//			this.capa = new RCapability(bdimodel.getCapability());
		
//	/**
//	 *  Create the agent.
//	 */
//	protected MicroAgent createAgent(Class<?> agentclass, MicroModel model, IPersistInfo pinfo) throws Exception
//	{
//		ASMBDIClassGenerator.checkEnhanced(agentclass);
//		
//		MicroAgent ret;
//		final Object agent = agentclass.newInstance();
//		if(agent instanceof MicroAgent)
//		{
//			ret = (MicroAgent)agent;
//			ret.init(BDIAgentInterpreter.this);
//		}
//		else // if pojoagent
//		{
//			PojoBDIAgent pa = new PojoBDIAgent();
//			pa.init(this, agent);
//			ret = pa;
//
//			injectAgent(pa, agent, model, null);
//		}
//		
//		// Init rule system
//		this.rulesystem = new RuleSystem(agent);
//		
//		return ret;
//	}
		
//	/**
//	 *  Inject the agent into annotated fields.
//	 */
//	protected void	injectAgent(IInternalAccess pa, Object agent, MicroModel model, String globalname)
//	{
//		FieldInfo[] fields = model.getAgentInjections();
//		for(int i=0; i<fields.length; i++)
//		{
//			try
//			{
//				Field f = fields[i].getField(getComponent().getClassLoader());
//				if(SReflect.isSupertype(f.getType(), ICapability.class))
//				{
//					f.setAccessible(true);
//					f.set(agent, new CapabilityWrapper(pa, agent, globalname));						
//				}
//				else
//				{
//					f.setAccessible(true);
//					f.set(agent, pa);
//				}
//			}
//			catch(Exception e)
//			{
//				pa.getLogger().warning("Agent injection failed: "+e);
//			}
//		}
//	
//		// Additionally inject hidden agent fields
//		Class<?> agcl = agent.getClass();
//		while(agcl.isAnnotationPresent(Agent.class)
//			|| agcl.isAnnotationPresent(Capability.class))
//		{
//			try
//			{
//				Field field = agcl.getDeclaredField("__agent");
//				field.setAccessible(true);
//				field.set(agent, pa);
//				
//				field = agcl.getDeclaredField("__globalname");
//				field.setAccessible(true);
//				field.set(agent, globalname);
//				agcl = agcl.getSuperclass();
//
//			}
//			catch(Exception e)
//			{
//				pa.getLogger().warning("Hidden agent injection failed: "+e);
//				break;
//			}
//		}
//		// Add hidden agent field also to contained inner classes (goals, plans)
//		// Does not work as would have to be inserted in each object of that type :-(
////			Class<?>[] inners = agent.getClass().getDeclaredClasses();
////			if(inners!=null)
////			{
////				for(Class<?> icl: inners)
////				{
////					try
////					{
////						Field field = icl.getDeclaredField("__agent");
////						field.setAccessible(true);
////						field.set(icl, pa);
////					}
////					catch(Exception e)
////					{
////						e.printStackTrace();
////					}
////				}
////			}
//	}
		
//	/**
//	 *  Get a capability pojo object.
//	 */
//	public Object	getCapabilityObject(String name)
//	{
////		Object	ret	= ((PojoBDIAgent)microagent).getPojoAgent();
//		Object ret = getComponent().getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
//		if(name!=null)
//		{
//			StringTokenizer	stok	= new StringTokenizer(name, MElement.CAPABILITY_SEPARATOR);
//			while(stok.hasMoreTokens())
//			{
//				name	= stok.nextToken();
//				
//				boolean found = false;
//				Class<?> cl = ret.getClass();
//				while(!found && !Object.class.equals(cl))
//				{
//					try
//					{
//						Field	f	= cl.getDeclaredField(name);
//						f.setAccessible(true);
//						ret	= f.get(ret);
//						found = true;
//						break;
//					}
//					catch(Exception e)
//					{
//						cl	= cl.getSuperclass();
//					}
//				}
//				if(!found)
//					throw new RuntimeException("Could not fetch capability object: "+name);
//			}
//		}
//		return ret;
//	}
		
	/**
	 * 	Adapt element for use in inner capabilities.
	 *  @param obj	The object to adapt (e.g. a change event)
	 *  @param capa	The capability name or null for agent.
	 */
	protected static Object adaptToCapability(Object obj, String capa, IBDIModel bdimodel)
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
					Map<String, String>	map	= bdimodel.getCapability().getBeliefReferences();
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
		
//	/**
//	 *  Init the capability pojo objects.
//	 */
//	protected IFuture<Void>	initCapabilities(final Object agent, final Tuple2<FieldInfo, BDIModel>[] caps, final int i)
//	{
//		final Future<Void>	ret	= new Future<Void>();
//		
//		if(i<caps.length)
//		{
//			try
//			{
//				Field	f	= caps[i].getFirstEntity().getField(getComponent().getClassLoader());
//				f.setAccessible(true);
//				final Object capa = f.get(agent);
//				
//				String globalname;
//				try
//				{
//					Field	g	= agent.getClass().getDeclaredField("__globalname");
//					g.setAccessible(true);
//					globalname	= (String)g.get(agent);
//					globalname	= globalname==null ? f.getName() : globalname+MElement.CAPABILITY_SEPARATOR+f.getName();
//				}
//				catch(Exception e)
//				{
//					throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
//				}
//				
////				injectAgent(getComponent(), capa, caps[i].getSecondEntity(), globalname);
//				
//				// Todo: capability features?
////				MicroInjectionComponentFeature.injectServices(capa, caps[i].getSecondEntity(), getComponent())
////					.addResultListener(new DelegationResultListener<Void>(ret)
////				{
////					public void customResultAvailable(Void result)
////					{
////						injectParent(capa, caps[i].getSecondEntity())
////							.addResultListener(new DelegationResultListener<Void>(ret)
////						{
////							public void customResultAvailable(Void result)
////							{
//								initCapabilities(agent, caps, i+1)
//									.addResultListener(new DelegationResultListener<Void>(ret));
////							}
////						});
////					}
////				});				
//			}
//			catch(Exception e)
//			{
//				ret.setException(e);
//			}
//		}
//		else
//		{
//			ret.setResult(null);
//		}
//		
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
				Object val = mbel.getValue(getComponent());
				if(val==null)
				{
					String impl = mbel.getImplClassName();
					if(impl!=null)
					{
						Class<?> implcl = SReflect.findClass(impl, null, getComponent().getClassLoader());
						val = implcl.newInstance();
					}
					else
					{
						Class<?> cl = mbel.getType(getComponent().getClassLoader());//f.getType();
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
					mbel.setValue(getComponent(), new ListWrapper((List<?>)val, getComponent(), ChangeEvent.FACTADDED+"."+bname, ChangeEvent.FACTREMOVED+"."+bname, ChangeEvent.FACTCHANGED+"."+bname, mbel));
				}
				else if(val instanceof Set)
				{
					String bname = mbel.getName();
					mbel.setValue(getComponent(), new SetWrapper((Set<?>)val, getComponent(), ChangeEvent.FACTADDED+"."+bname, ChangeEvent.FACTREMOVED+"."+bname, ChangeEvent.FACTCHANGED+"."+bname, mbel));
				}
				else if(val instanceof Map)
				{
					String bname = mbel.getName();
					mbel.setValue(getComponent(), new MapWrapper((Map<?,?>)val, getComponent(), ChangeEvent.FACTADDED+"."+bname, ChangeEvent.FACTREMOVED+"."+bname, ChangeEvent.FACTCHANGED+"."+bname, mbel));
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
	 *  Add a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public void addBeliefListener(String name, final IBeliefListener listener)
	{
		String fname = bdimodel.getCapability().getBeliefReferences().containsKey(name) ? bdimodel.getCapability().getBeliefReferences().get(name) : name;
		
		List<EventType> events = new ArrayList<EventType>();
		addBeliefEvents(getComponent(), events, fname);

		final boolean multi = ((MCapability)getCapability().getModelElement())
			.getBelief(fname).isMulti(((ModelInfo)bdimodel).getClassLoader());
		
		String rulename = fname+"_belief_listener_"+System.identityHashCode(listener);
		Rule<Void> rule = new Rule<Void>(rulename, 
			ICondition.TRUE_CONDITION, new IAction<Void>()
		{
			public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
			{
				if(!multi)
				{
					listener.beliefChanged((ChangeInfo)event.getContent());
				}
				else
				{
					if(ChangeEvent.FACTADDED.equals(event.getType().getType(0)))
					{
						listener.factAdded((ChangeInfo)event.getContent());
					}
					else if(ChangeEvent.FACTREMOVED.equals(event.getType().getType(0)))
					{
						listener.factRemoved((ChangeInfo)event.getContent());
					}
					else if(ChangeEvent.FACTCHANGED.equals(event.getType().getType(0)))
					{
//						Object[] vals = (Object[])event.getContent();
						listener.factChanged((ChangeInfo)event.getContent());
					}
					else if(ChangeEvent.BELIEFCHANGED.equals(event.getType().getType(0)))
					{
						listener.beliefChanged((ChangeInfo)event.getContent());
					}
				}
				return IFuture.DONE;
			}
		});
		rule.setEvents(events);
		getRuleSystem().getRulebase().addRule(rule);
	}
	
	/**
	 *  Remove a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public void removeBeliefListener(String name, IBeliefListener listener)
	{
		name	= bdimodel.getCapability().getBeliefReferences().containsKey(name) ? bdimodel.getCapability().getBeliefReferences().get(name) : name;
		String rulename = name+"_belief_listener_"+System.identityHashCode(listener);
		getRuleSystem().getRulebase().removeRule(rulename);
	}
		
//	/**
//	 *  Called before blocking the component thread.
//	 */
//	public void	beforeBlock()
//	{
//		RPlan	rplan	= ExecutePlanStepAction.RPLANS.get();
//		testBodyAborted(rplan);
//		ComponentSuspendable sus = ComponentSuspendable.COMSUPS.get();
//		if(rplan!=null && sus!=null && !RPlan.PlanProcessingState.WAITING.equals(rplan.getProcessingState()))
//		{
//			final ResumeCommand<Void> rescom = rplan.new ResumeCommand<Void>(sus, false);
//			rplan.setProcessingState(PlanProcessingState.WAITING);
//			rplan.resumecommand = rescom;
//		}
//	}
//		
//	/**
//	 *  Called after unblocking the component thread.
//	 */
//	public void	afterBlock()
//	{
//		RPlan rplan = ExecutePlanStepAction.RPLANS.get();
//		testBodyAborted(rplan);
//		if(rplan!=null)
//		{
//			rplan.setProcessingState(PlanProcessingState.RUNNING);
//			if(rplan.resumecommand!=null)
//			{
//				// performs only cleanup without setting future
//				rplan.resumecommand.execute(Boolean.FALSE);
//				rplan.resumecommand = null;
//			}
//		}
//	}
		
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
	 *  Get parameter values for injection into method and constructor calls.
	 */
	public static Object[] getInjectionValues(Class<?>[] ptypes, Annotation[][] anns, MElement melement, ChangeEvent event, RPlan rplan, RProcessableElement rpe, IInternalAccess component)
	{
		return getInjectionValues(ptypes, anns, melement, event, rplan, rpe, null, component);
	}
		
	// todo: support parameter names via annotation in guesser (guesser with meta information)
	/**
	 *  Get parameter values for injection into method and constructor calls.
	 *  @return A valid assigment or null if no assignment could be found.
	 */
	public static Object[]	getInjectionValues(Class<?>[] ptypes, Annotation[][] anns, MElement melement, ChangeEvent event, RPlan rplan, RProcessableElement rpe, Collection<Object> vs, IInternalAccess component)
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
		IInternalBDIAgentFeature bdif = (IInternalBDIAgentFeature)component.getComponentFeature(IBDIXAgentFeature.class);
		Object capa = component.getComponentFeature(IPojoComponentFeature.class).getPojoAgent(); // todo
//		Object capa = capaname!=null ? bdif.getCapabilityObject(capaname): component.getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
//			: getAgent() instanceof PojoBDIAgent? ((PojoBDIAgent)getAgent()).getPojoAgent(): getAgent();
		
		vals.add(capa);
		vals.add(new CapabilityPojoWrapper(component, capa, capaname));
		vals.add(component);
		vals.add(component.getExternalAccess());

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
						Object val = param.getValue(pojo, component.getClassLoader());
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
					if(bdif.getBDIModel().getCapability().getBeliefReferences().containsKey(source))
					{
						source	= bdif.getBDIModel().getCapability().getBeliefReferences().get(source);
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
						
//							else if(SReflect.isSupertype(ptypes[i], ChangeEvent.class))
//							{
//								ret[i]	= event;
//							}
//							else
//							{
//								throw new IllegalArgumentException("Unexpected type for event injection: "+event+", "+ptypes[i]);
//							}
					}
					else
					{
						MBelief	mbel	= bdif.getBDIModel().getCapability().getBelief(source);
						ret[i]	= mbel.getValue(component);

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
				ret[i]	= adaptToCapability(ret[i], capaname, bdif.getBDIModel());
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
		
//	/**
//	 *  Can be called on the agent thread only.
//	 * 
//	 *  Main method to perform agent execution.
//	 *  Whenever this method is called, the agent performs
//	 *  one of its scheduled actions.
//	 *  The platform can provide different execution models for agents
//	 *  (e.g. thread based, or synchronous).
//	 *  To avoid idle waiting, the return value can be checked.
//	 *  The platform guarantees that executeAction() will not be called in parallel. 
//	 *  @return True, when there are more actions waiting to be executed. 
//	 */
//	public boolean executeStep()
//	{
//		assert isComponentThread();
//		
//		// Evaluate condition before executing step.
////			boolean aborted = false;
////			if(rulesystem!=null)
////				aborted = rulesystem.processAllEvents(15);
////			if(aborted)
////				getCapability().dumpGoals();
//		
//		if(inited && rulesystem!=null)
//			rulesystem.processAllEvents();
//		
////			if(steps!=null && steps.size()>0)
////			{
////				System.out.println(getComponentIdentifier()+" steps: "+steps.size()+" "+steps.get(0).getStep().getClass());
////			}
//		boolean ret = super.executeStep();
//		
////			System.out.println(getComponentIdentifier()+" after step");
//
//		return ret || (inited && rulesystem!=null && rulesystem.isEventAvailable());
//	}
		
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
	public IBDIModel getBDIModel()
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
		
//		/**
//		 *  Method that tries to guess the parameters for the method call.
//		 */
//		public Object[] guessMethodParameters(Object pojo, Class<?>[] ptypes, Set<Object> values)
//		{
//			if(ptypes==null || values==null)
//				return null;
//			
//			Object[] params = new Object[ptypes.length];
//			
//			for(int i=0; i<ptypes.length; i++)
//			{
//				for(Object val: values)
//				{
//					if(SReflect.isSupertype(val.getClass(), ptypes[i]))
//					{
//						params[i] = val;
//						break;
//					}
//				}
//			}
//					
//			return params;
//		}
		
//	/**
//	 *  Get the inited.
//	 *  @return The inited.
//	 */
//	public boolean isInited()
//	{
//		return inited;
//	}
		
//		/**
//		 *  Create a result listener which is executed as an component step.
//		 *  @param The original listener to be called.
//		 *  @return The listener.
//		 */
//		public <T> IResultListener<T> createResultListener(IResultListener<T> listener)
//		{
//			// Must override method to ensure that plan steps are executed with planstepactions
//			if(ExecutePlanStepAction.RPLANS.get()!=null && !(listener instanceof BDIComponentResultListener))
//			{
//				return new BDIComponentResultListener(listener, this);
//			}
//			else
//			{
//				return super.createResultListener(listener);
//			}
//		}
			
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
		
//		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
		MBelief mbel = ((MCapability)((IInternalBDIAgentFeature)ia.getComponentFeature(IBDIXAgentFeature.class)).getCapability().getModelElement()).getBelief(belname);
		if(mbel!=null && mbel.isMulti(null))
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
//				return ret? ICondition.TRUE: ICondition.FALSE;
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
//				return res? ICondition.TRUE: ICondition.FALSE;
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
				BeliefInfo info = BeliefInfo.createBeliefInfo(getComponent(), mbel, getComponent().getClassLoader());
				MonitoringEvent ev = new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), IMonitoringEvent.EVENT_TYPE_CREATION+"."+IMonitoringEvent.SOURCE_CATEGORY_FACT, System.currentTimeMillis(), PublishEventLevel.FINE);
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
				MonitoringEvent ev = new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), IMonitoringEvent.EVENT_TYPE_CREATION+"."+IMonitoringEvent.SOURCE_CATEGORY_GOAL, System.currentTimeMillis(), PublishEventLevel.FINE);
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
				MonitoringEvent ev = new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), IMonitoringEvent.EVENT_TYPE_CREATION+"."+IMonitoringEvent.SOURCE_CATEGORY_PLAN, System.currentTimeMillis(), PublishEventLevel.FINE);
				ev.setSourceDescription(plan.toString());
				ev.setProperty("details", info);
				ret.add(ev);
			}
		}
		
		return ret;
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
	
	
	/**
	 *  Get the feature from the agent.
	 */
	public static IBDIXAgentFeature	getBDIAgentFeature(IInternalAccess agent)
	{
		return agent.getComponentFeature(IBDIXAgentFeature.class);
	}
	
	/**
	 *  Get the value fetcher.
	 */
	public IValueFetcher getValueFetcher()
	{
		// Todo: obsolete because of capability specific fetchers?
		return new IValueFetcher()
		{
			public Object fetchValue(String name)
			{
				if("$beliefbase".equals(name))
				{
					return getCapability().getBeliefbase();
				}
				else if("$goalbase".equals(name))
				{
					return getCapability().getGoalbase();
				}
				else if("$planbase".equals(name))
				{
					return getCapability().getPlanbase();
				}
				else if("$eventbase".equals(name))
				{
					return getCapability().getEventbase();
				}
				else if("$expressionbase".equals(name))
				{
					return getCapability().getExpressionbase();
				}
				else if("$scope".equals(name))
				{
					return new CapabilityWrapper(getComponent(), null);
				}
				else
				{
					throw new RuntimeException("Value not found: "+name);
				}
			}
		};
	}
	
//	/**
//	 *  Get a capability pojo object.
//	 *  @return The capability pojo.
//	 */
//	public Object	getCapabilityObject(String name)
//	{
//		throw new UnsupportedOperationException();
//	}
	
	/**
	 *  Get the event type.
	 *  @return The event adder.
	 */
	public Map<EventType, IResultCommand<IFuture<Void>, PropertyChangeEvent>> getEventAdders()
	{
		throw new UnsupportedOperationException();
//		return eventadders;
	}
	
	// ICapability interface
	
	/**
	 *  Get the scope.
	 *  Method with IExternalAccess return value included
	 *  for compatibility with IInternalAccess. 
	 *  @return The scope.
	 */
	public IExternalAccess getExternalAccess()
	{
		return getComponent().getExternalAccess();
	}

	/**
	 *  Get the belief base.
	 *  @return The belief base.
	 */
	public IBeliefbase getBeliefbase()
	{
		return capa.getBeliefbase();
	}
	
	/**
	 *  Get the event base.
	 *  @return The event base.
	 */
	public IEventbase getEventbase()
	{
		return capa.getEventbase();
	}

	/**
	 *  Get the goal base.
	 *  @return The goal base.
	 */
	public IGoalbase getGoalbase()
	{
		return capa.getGoalbase();
	}

	/**
	 *  Get the plan base.
	 *  @return The plan base.
	 */
	public IPlanbase getPlanbase()
	{
		return capa.getPlanbase();
	}

	/**
	 * Get the expression base.
	 * @return The expression base.
	 */
	public IExpressionbase getExpressionbase()
	{
		return capa.getExpressionbase();
	}

	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return getComponent().getLogger();
	}

	/**
	 * Get the agent model.
	 * @return The agent model.
	 */
	public IModelInfo getAgentModel()
	{
		return getComponent().getModel();
	}

	/**
	 * Get the capability model.
	 * @return The capability model.
	 */
	public IModelInfo getModel()
	{
		return getComponent().getModel();
	}

	/**
	 * Get the agent name.
	 * @return The agent name.
	 */
	public String getAgentName()
	{
		return getComponent().getComponentIdentifier().getLocalName();
	}

	/**
	 * Get the configuration name.
	 * @return The configuration name.
	 */
	public String getConfigurationName()
	{
		return getComponent().getConfiguration();
	}

	/**
	 * Get the agent identifier.
	 * @return The agent identifier.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return getComponent().getComponentIdentifier();
	}

	/**
	 * Get the component description.
	 * @return The component description.
	 */
	public IComponentDescription	getComponentDescription()
	{
		return getComponent().getComponentDescription();
	}

	/**
	 *  Get the current time.
	 *  The time unit depends on the currently running clock implementation.
	 *  For the default system clock, the time value adheres to the time
	 *  representation as used by {@link System#currentTimeMillis()}, i.e.,
	 *  the value of milliseconds passed since 0:00 'o clock, January 1st, 1970, UTC.
	 *  For custom simulation clocks, arbitrary representations can be used.
	 *  @return The current time.
	 */
	public long getTime()
	{
		return SServiceProvider.getLocalService(getComponent(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).getTime();
	}

	/**
	 *  Get the classloader.
	 *  @return The classloader.
	 */
	public ClassLoader getClassLoader()
	{
		return getComponent().getClassLoader();
	}
	
	/**
	 *  Kill the agent.
	 */
	public IFuture<Map<String, Object>> killAgent()
	{
		return getComponent().killComponent();
	}
	
	/**
	 *  Subscribe to monitoring events.
	 *  @param filter An optional filter.
	 */
//	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(IFilter<IMonitoringEvent> filter, boolean initial, PublishEventLevel elm)
	{
		return getComponent().getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(filter, initial, elm);
	}	
}
