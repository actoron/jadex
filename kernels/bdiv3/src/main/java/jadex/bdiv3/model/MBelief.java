package jadex.bdiv3.model;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.features.impl.BDILifecycleAgentFeature;
import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.MParameter.EvaluationMode;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bdiv3x.runtime.CapabilityWrapper;
import jadex.bdiv3x.runtime.IBelief;
import jadex.bdiv3x.runtime.IBeliefSet;
import jadex.bridge.ClassInfo;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bytecode.vmhacks.VmHacks;
import jadex.commons.FieldInfo;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.javaparser.SJavaParser;
import jadex.rules.eca.EventType;


/**
 *  Belief model.
 */
public class MBelief extends MElement
{
	/** The autoclosable class, if present. */
	public static final Class<?>	AUTOCLOSABLE	= SReflect.classForName0("java.lang.AutoCloseable", BDILifecycleAgentFeature.class.getClassLoader());

	/** The field target. */
	protected FieldInfo ftarget;

	/** The method targets. */
	protected MethodInfo mgetter;
	protected MethodInfo msetter;
	
	/** The collection implementation class. */
	protected String impl;
	
//	/** The dynamic flag. */
//	protected boolean dynamic;
	
	/** The update rate. */
	protected UnparsedExpression updaterate;
	
	/** Flag if is multi. */
	protected Boolean multi;
	
	/** The events this belief depends on. */
	protected Set<String> beliefevents;
	
	/** The raw events. */
	protected Collection<EventType> rawevents;
	
	/** The aggregated/inited events. */
	protected List<EventType> events;

	//-------- additional xml properties --------
	
	/** The default fact. */
	protected UnparsedExpression fact;
	
	/** The default facts (multi). */
	protected List<UnparsedExpression> facts;
	
	/** The type (if explicitly specified). */
	protected ClassInfo clazz;
	
	/** The exported / argument flag. */
	protected boolean exported;
	
	/** The result flag. */
	protected boolean result;
	
	/** The reference. */
	protected String ref;

	/** The direction. */
	protected EvaluationMode evaluationmode = EvaluationMode.STATIC;
	
	/**
	 *	Bean Constructor. 
	 */
	public MBelief()
	{
	}
	
	/**
	 *  Create a new belief.
	 */
	public MBelief(FieldInfo target, String impl, boolean dynamic, UnparsedExpression updaterate, Set<String> beliefevents, Collection<EventType> rawevents)
	{
		super(target!=null? target.getName(): null);
		this.ftarget = target;
		this.impl = impl;
		if(dynamic)
			this.evaluationmode = updaterate!=null ? MParameter.EvaluationMode.POLLING : MParameter.EvaluationMode.PULL;
		this.updaterate	= updaterate;
		this.beliefevents = beliefevents;
		this.rawevents = rawevents;

		adaptEvaluationMode();
		
//		// Set to push if user specified event dependencies (not if only deduced from an expression)
//		if(this.beliefevents!=null && this.beliefevents.size()>0 || this.rawevents!=null && this.rawevents.size()>0)
//			this.evaluationmode = MParameter.EvaluationMode.PUSH;
		
//		System.out.println("bel: "+(target!=null?target.getName():"")+" "+dynamic);
	}
	
	/**
	 *  Create a new belief.
	 */
	public MBelief(FieldInfo target, String impl, boolean dynamic, long updaterate, Set<String> beliefevents, Collection<EventType> rawevents)
	{
		super(target!=null? target.getName(): null);
		this.ftarget = target;
		this.impl = impl;
		if(dynamic)
			this.evaluationmode = updaterate>0 ? MParameter.EvaluationMode.POLLING : MParameter.EvaluationMode.PULL;
//		this.dynamic = dynamic;
		this.updaterate	= updaterate>0? new UnparsedExpression(null, ""+updaterate): null;
		this.beliefevents = beliefevents;
		this.rawevents = rawevents;

		adaptEvaluationMode();
		
//		// Set to push if user specified event dependencies (not if only deduced from an expression)
//		if(this.beliefevents!=null && this.beliefevents.size()>0 || this.rawevents!=null && this.rawevents.size()>0)
//			this.evaluationmode = MParameter.EvaluationMode.PUSH;
		
//		System.out.println("bel: "+(target!=null?target.getName():"")+" "+dynamic);
	}
	
	/**
	 *  Adapt the evaluation mode according to the 
	 */
	protected void adaptEvaluationMode()
	{
		// Set to push if user specified event dependencies (not if only deduced from an expression)
		if(this.beliefevents!=null && this.beliefevents.size()>0 || this.rawevents!=null && this.rawevents.size()>0)
			this.evaluationmode = MParameter.EvaluationMode.PUSH;
	}
	
	
	/**
	 *  Create a new belief.
	 */
	public MBelief(FieldInfo target, String impl, boolean dynamic, long updaterate, String[] beliefevents, Collection<EventType> rawevents)
	{
		this(target, impl, dynamic, updaterate, beliefevents!=null ? new LinkedHashSet<String>(Arrays.asList(beliefevents)) : null, rawevents);
	}
	
	/**
	 *  Create a new belief.
	 */
	public MBelief(MethodInfo target, String impl, boolean dynamic, UnparsedExpression updaterate, Set<String> beliefevents, Collection<EventType> rawevents)
	{
		this((FieldInfo)null, impl, dynamic, updaterate, beliefevents, rawevents);
		
		if(target.getName().startsWith("get"))
		{
			this.mgetter = target;
			name = target.getName().substring(3);			
		}
		else if(target.getName().startsWith("is"))
		{
			this.mgetter = target;
			name = target.getName().substring(2);			
		}
		else// if(target.getName().startsWith("set"))
		{
			this.msetter = target;
			name = target.getName().substring(3);			
		}
		
		name = name.substring(0, 1).toLowerCase()+name.substring(1);
	}
	
	/**
	 *  Create a new belief.
	 */
	public MBelief(MethodInfo target, String impl, boolean dynamic, long updaterate, Set<String> beliefevents, Collection<EventType> rawevents)
	{
		this(target, impl, dynamic, updaterate>0? new UnparsedExpression(null, ""+updaterate): null, beliefevents, rawevents);
	}
	
	/**
	 *  Create a new belief.
	 */
	public MBelief(MethodInfo target, String impl, boolean dynamic, long updaterate, String[] beliefevents, Collection<EventType> rawevents)
	{
		this(target, impl, dynamic, updaterate, beliefevents!=null ? new LinkedHashSet<String>(Arrays.asList(beliefevents)) : null, rawevents);
	}
	

//	/**
//	 *  Get the target.
//	 *  @return The target.
//	 */
//	public FieldInfo getFieldTarget()
//	{
//		return ftarget;
//	}
//
//	/**
//	 *  Set the target.
//	 *  @param target The target to set.
//	 */
//	public void setFieldTarget(FieldInfo target)
//	{
//		this.ftarget = target;
//	}
//	
//	/**
//	 *  Get the target.
//	 *  @return The target.
//	 */
//	public MethodInfo[] getMethodTarget()
//	{
//		return mtarget;
//	}
//
//	/**
//	 *  Set the target.
//	 *  @param target The target to set.
//	 */
//	public void setMethodTarget(MethodInfo[] target)
//	{
//		this.mtarget = target;
//	}
	
	/**
	 *  Get the impl.
	 *  @return The impl.
	 */
	public String getImplClassName()
	{
		return impl;
	}

	/**
	 *  Set the impl.
	 *  @param impl The impl to set.
	 */
	public void setImplClassName(String impl)
	{
		this.impl = impl;
	}
	
	/**
	 *  Get the dynamic.
	 *  @return The dynamic.
	 */
	public boolean isDynamic()
	{
		return !evaluationmode.equals(MParameter.EvaluationMode.STATIC);
	}

//	/**
//	 *  Set the dynamic.
//	 *  @param dynamic The dynamic to set.
//	 */
//	public void setDynamic(boolean dynamic)
//	{
//		this.dynamic = dynamic;
//	}
	
	/**
	 *  Get the exported flag.
	 *  @return The exported flag.
	 */
	public boolean isExported()
	{
		return exported;
	}

	/**
	 *  Set the exported flag.
	 *  @param exported The exported to set.
	 */
	public void setExported(boolean exported)
	{
		this.exported = exported;
	}
	
	/**
	 *  Get the result flag.
	 *  @return The result flag.
	 */
	public boolean isResult()
	{
		return result;
	}

	/**
	 *  Set the result flag.
	 *  @param result The result to set.
	 */
	public void setResult(boolean result)
	{
		this.result = result;
	}
	
	/**
	 *  Get the reference. For belief references.
	 *  @return The reference.
	 */
	public String getRef()
	{
		return ref;
	}

	/**
	 *  Set the reference.
	 *  @param reference The reference to set.
	 */
	public void setRef(String reference)
	{
		this.ref = internalName(reference);

	}
	
//	/**
//	 *  Get the updaterate.
//	 *  @return The updaterate.
//	 */
//	public long getUpdaterate()
//	{
//		return updaterate;
//	}
//
//	/**
//	 *  Set the updaterate.
//	 *  @param updaterate The updaterate to set.
//	 */
//	public void setUpdaterate(long updaterate)
//	{
//		this.updaterate = updaterate;
//	}
	
	/**
	 *  Get the updaterate.
	 *  @return The updaterate
	 */
	public UnparsedExpression getUpdateRate()
	{
		return updaterate;
	}

	/**
	 *  The updaterate to set.
	 *  @param updaterate The updaterate to set
	 */
	public void setUpdateRate(UnparsedExpression updaterate)
	{
		this.updaterate = updaterate;
	}
	
	/**
	 *  Get/Evaluate the updaterate value.
	 *  @param agent The agent.
	 *  @return The update rate.
	 */
	public long getUpdaterateValue(IInternalAccess agent)
	{
		long ret = -1;
		if(updaterate!=null)
			ret = ((Number)SJavaParser.parseExpression(updaterate, agent.getModel().getAllImports(), 
				agent.getClassLoader()).getValue(CapabilityWrapper.getFetcher(agent, updaterate.getLanguage()))).longValue();
		return ret;
	}
	
	/**
	 *  Set the mgetter.
	 *  @param mgetter The mgetter to set.
	 */
	public void setGetter(MethodInfo mgetter)
	{
		this.mgetter = mgetter;
	}

	/**
	 *  Set the msetter.
	 *  @param msetter The msetter to set.
	 */
	public void setSetter(MethodInfo msetter)
	{
		this.msetter = msetter;
	}
	
	/**
	 *  Get the clazz.
	 *  @return The clazz
	 */
	public ClassInfo getClazz()
	{
		return clazz;
	}

	/**
	 *  The clazz to set.
	 *  @param clazz The clazz to set
	 */
	public void setClazz(ClassInfo clazz)
	{
		this.clazz = clazz;
	}
	
	/**
	 *  Test if this belief refers to a field.
	 *  @return True if is a field belief.
	 */
	public boolean isFieldBelief()
	{
		return ftarget!=null;
	}
	
	/**
	 *  Get the multi.
	 *  @return The multi.
	 */
	public boolean isMulti(ClassLoader cl)
	{
		if(multi==null)
		{
			Class<?> ftype = null;
			if(ftarget!=null)
			{
				Field f = ftarget.getField(cl);
				ftype = f.getType();
			}
			else if(mgetter!=null)
			{
				ftype = mgetter.getMethod(cl).getReturnType();
			}
			
			if(ftype!=null)
			{
				if(ftype.isArray() || SReflect.isSupertype(List.class, ftype) 
					|| SReflect.isSupertype(Set.class, ftype)
					|| SReflect.isSupertype(Map.class, ftype))
				{
					multi = Boolean.TRUE;
				}
				else
				{
					multi = Boolean.FALSE;
				}
			}
			else
			{
				// default for xml
				multi = Boolean.FALSE;
			}
		}
		return multi;
	}
	
	/**
	 *  The multi to set.
	 *  @param multi The multi to set
	 */
	public void setMulti(boolean multi)
	{
		this.multi = multi? Boolean.TRUE: Boolean.FALSE;
	}

	/**
	 *  Get the value of the belief.
	 */
	public Object getValue(IInternalAccess agent)
	{
		Object ret = null;
		
		IInternalBDIAgentFeature bdif = agent.getFeature(IInternalBDIAgentFeature.class);
		
		if(bdif instanceof BDIAgentFeature)
		{
			String	capaname	= getName().indexOf(MElement.CAPABILITY_SEPARATOR)==-1
				? null : getName().substring(0, getName().lastIndexOf(MElement.CAPABILITY_SEPARATOR));
			ret = getValue(((BDIAgentFeature)bdif).getCapabilityObject(capaname), agent.getClassLoader());
		}
		else if(bdif instanceof jadex.bdiv3x.features.BDIXAgentFeature)
		{
			if(multi)
			{
				IBeliefSet rbelset = bdif.getCapability().getBeliefbase().getBeliefSet(getName());
				ret = rbelset.getFacts();
			}
			else
			{
				IBelief rbel = bdif.getCapability().getBeliefbase().getBelief(getName());
				ret = rbel.getFact();
			}
		}	
		
		return ret;
	}

	/**
	 *  Get the value of the belief.
	 */
	public Object getValue(Object object, ClassLoader cl)
	{
		Object ret = null;
		if(ftarget!=null)
		{
			try
			{
				Field f = ftarget.getField(cl);
				VmHacks.get().setAccessible(f, true);
				ret = f.get(object);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				Method m = mgetter.getMethod(cl);
				ret = m.invoke(object, new Object[0]);
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
		return ret;
	}
	
	/**
	 *  Set the value of the belief.
	 *  @return True, if field was set.
	 */
	public boolean setValue(IInternalAccess agent, final Object value)
	{
		boolean ret = false;
		
		
		// Pojo
		if(agent.getFeature0(IBDIAgentFeature.class)!=null)
		{
			String	capaname	= getName().indexOf(MElement.CAPABILITY_SEPARATOR)==-1
				? null : getName().substring(0, getName().lastIndexOf(MElement.CAPABILITY_SEPARATOR));
			return setValue(((BDIAgentFeature)agent.getFeature(IBDIAgentFeature.class)).getCapabilityObject(capaname), value, agent.getClassLoader());
		}
		
		// Xml
		else 
		{
			IInternalBDIAgentFeature	bdif	= (IInternalBDIAgentFeature)agent.getFeature(IBDIXAgentFeature.class);
			if(multi)
			{
				IBeliefSet rbelset = bdif.getCapability().getBeliefbase().getBeliefSet(getName());
				rbelset.removeFacts();
				if(value!=null)
				{
					for(Object val: SReflect.getIterable(value))
					{
						rbelset.addFact(val);
					}
				}
			}
			else
			{
				IBelief rbel = bdif.getCapability().getBeliefbase().getBelief(getName());
				rbel.setFact(value);
			}
		}	
		
		return ret;
	}

	/**
	 *  Set the value of the belief.
	 */
	public boolean setValue(Object object, Object value, ClassLoader cl)
	{
		boolean field	= false;
		if(ftarget!=null)
		{
			field = true;
			try
			{
				Field f = ftarget.getField(cl);
				VmHacks.get().setAccessible(f, true);
				f.set(object, value);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				Method m = msetter.getMethod(cl);
				m.invoke(object, new Object[]{value});
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return field;
	}
	
	/**
	 *  Cleanup the value on agent shutdown, i.e., call close() on closeable fact objects.
	 */
	public void	cleanup(IInternalAccess component)
	{
		if(isMulti(component.getClassLoader()))
		{
			try
			{
				Object	fact	= getValue(component);
				if(fact!=null && SReflect.isIterableClass(fact.getClass()))
				{
					for(Object item: SReflect.getIterable(fact))
					{
						if(fact instanceof Closeable)
						{
							((Closeable)fact).close();
						}
						else if(fact!=null && AUTOCLOSABLE!=null && SReflect.isSupertype(AUTOCLOSABLE, fact.getClass()))
						{
							AUTOCLOSABLE.getMethod("close", new Class<?>[0]).invoke(fact, new Object[0]);
						}
						else
						{
							break;
						}
					}
				}
			}
			catch(Exception e)
			{
				component.getLogger().warning("Exception during autoclose of belief set "+getName()+": "+SUtil.getExceptionStacktrace(e));
			}
		}
		else
		{
			try
			{
				Object	fact	= getValue(component);
				if(fact instanceof Closeable)
				{
					((Closeable)fact).close();
				}
				else if(fact!=null && AUTOCLOSABLE!=null && SReflect.isSupertype(AUTOCLOSABLE, fact.getClass()))
				{
					AUTOCLOSABLE.getMethod("close", new Class<?>[0]).invoke(fact, new Object[0]);
				}
			}
			catch(Exception e)
			{
				component.getLogger().warning("Exception during autoclose of belief "+getName()+": "+SUtil.getExceptionStacktrace(e));
			}
		}
	}
	
	/**
	 *  Get the class of the belief.
	 */
	public Class<?> getType(ClassLoader cl)
	{
		Class<?> ret = null;
		if(ftarget!=null)
		{
			try
			{
				Field f = ftarget.getField(cl);
//				f.setAccessible(true);
				ret = f.getType();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else if(mgetter!=null)
		{
			try
			{
				Method m = mgetter.getMethod(cl);
				ret = m.getReturnType();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else if(clazz!=null)
		{
			ret = clazz.getType(cl);
		}
		return ret;
	}

	/**
	 *  Get the field (for field-backed beliefs).
	 */
	public FieldInfo getField()
	{
		return ftarget;
	}

	/**
	 *  Get the getter method (for method-backed beliefs).
	 */
	public MethodInfo getGetter()
	{
		return mgetter;
	}

	/**
	 *  Get the setter method (for method-backed beliefs).
	 */
	public MethodInfo getSetter()
	{
		return msetter;
	}
	
	/**
	 *  Test if belief is of array type.
	 */
	public boolean isArrayBelief()//ClassLoader cl)
	{
		boolean ret = false;
		if(isFieldBelief() && ftarget.getClassName()!=null)
		{
//			ret = ftarget.getField(cl).getType().isArray();
			ret = ftarget.getTypeName().charAt(0)=='['; 
		}
		else if(mgetter!=null && mgetter.getReturnTypeInfo()!=null)
		{
//			ret = mgetter.getMethod(cl).getReturnType().isArray();
			ret = mgetter.getReturnTypeInfo().getTypeName().charAt(0)=='['; 
		}
		return ret;
	}

	/**
	 *  Get the value.
	 *  @return The value
	 */
	public UnparsedExpression getDefaultFact()
	{
		// The default value must not null, when a basic type is declared.
		// Hence a new default value is created.
		if(fact==null && facts==null && clazz!=null && !isMulti(null))
		{
			if(clazz.getTypeName()=="boolean")
				fact = new UnparsedExpression(null, "false");
			else if(clazz.getTypeName()=="byte")
				fact = new UnparsedExpression(null, "0");
			else if(clazz.getTypeName()=="char")
				fact = new UnparsedExpression(null, "0");
			else if(clazz.getTypeName()=="short")
				fact = new UnparsedExpression(null, "0");
			else if(clazz.getTypeName()=="double")
				fact = new UnparsedExpression(null, "0");
			else if(clazz.getTypeName()=="float")
				fact = new UnparsedExpression(null, "0");
			else if(clazz.getTypeName()=="long")
				fact = new UnparsedExpression(null, "0");
			else if(clazz.getTypeName()=="int")
				fact = new UnparsedExpression(null, "0");
		}
		
		return fact;
	}

	/**
	 *  The value to set.
	 *  @param value The value to set
	 */
	public void setDefaultFact(UnparsedExpression fact)
	{
		this.fact = fact;
	}
	
	
	/**
	 *  Get the value.
	 *  @return The value
	 */
	public List<UnparsedExpression> getDefaultFacts()
	{
		return facts;
	}

	/**
	 *  Set the value.
	 *  @return The value
	 */
	public void	setDefaultFacts(List<UnparsedExpression> facts)
	{
		this.facts	= facts;
	}

	/**
	 *  The value to set.
	 *  @param value The value to set
	 */
	public void addDefaultFacts(UnparsedExpression fact)
	{
		if(facts==null)
			facts = new ArrayList<UnparsedExpression>();
		facts.add(fact);
	}
	
	/**
	 *  Get the evaluationmode.
	 *  @return The evaluationmode
	 */
	public EvaluationMode getEvaluationMode()
	{
		return evaluationmode;
	}

	/**
	 *  The evaluationmode to set.
	 *  @param evaluationmode The evaluationmode to set
	 */
	public void setEvaluationMode(EvaluationMode evaluationmode)
	{
		this.evaluationmode = evaluationmode;
	}
	
	/**
	 *  Get the rawevents.
	 *  @return The rawevents.
	 */
	public Collection<EventType> getRawEvents()
	{
		return rawevents;
	}

	/**
	 *  Set the rawevents.
	 *  @param rawevents The rawevents to set.
	 */
	public void setRawEvents(Set<EventType> rawevents)
	{
		this.rawevents = rawevents;
		
		adaptEvaluationMode();
	}
	
	/**
	 *  Get the events.
	 *  @return The events.
	 */
	public Set<String> getBeliefEvents()
	{
		if(beliefevents!=null)
		{
			return beliefevents;
		}
		else
		{
			return Collections.emptySet();
		}
	}

	/**
	 *  Set the events.
	 *  @param events The events to set.
	 */
	public void setBeliefEvents(Set<String> events)
	{
		this.beliefevents	= events;
		
		adaptEvaluationMode();
	}
	
	/**
	 *  Get the events.
	 *  @return The events.
	 */
	public List<EventType> getEvents()
	{
		return SUtil.notNull(events);
	}
	
	/**
	 *  Init the event, when loaded from xml.
	 */
	public void	initEvents(IBDIModel model, ClassLoader cl)
	{
		Collection<String> evs = getBeliefEvents();
		if(evs!=null && !evs.isEmpty())
		{
			if(events==null)
			{
				events = new ArrayList<EventType>();
			}

			for(String ev: evs)
			{
				BDIAgentFeature.addBeliefEvents(model.getCapability(), events, ev, cl);
			}
		}
		
		Collection<EventType> rawevents = getRawEvents();
		if(rawevents!=null)
		{
			if(events==null)
			{
				events = new ArrayList<EventType>();
			}

			events.addAll(rawevents);
		}
		
		// Hack!!! what about initial values?
		if(getDefaultFact()!=null)
		{
			if(events==null)
			{
				events = new ArrayList<EventType>();
			}

			SJavaParser.parseExpression(getDefaultFact(), model.getModelInfo().getAllImports(), cl);
			BDIAgentFeature.addExpressionEvents(getDefaultFact(), events, null);
		}
	}
	
	/**
	 *  The events to set.
	 *  @param events The events to set
	 */
	public void setEvents(List<EventType> events)
	{
		this.events = events;
	}
	
	/**
	 *  Add an event.
	 *  @param event The event.
	 */
	public void addEvent(EventType event)
	{
		if(events==null)
			events = new ArrayList<EventType>();
		if(!events.contains(event))
			events.add(event);
	}
}
