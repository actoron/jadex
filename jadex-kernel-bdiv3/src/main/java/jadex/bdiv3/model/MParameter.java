package jadex.bdiv3.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3x.runtime.CapabilityWrapper;
import jadex.bridge.ClassInfo;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.FieldInfo;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.javaparser.SJavaParser;
import jadex.rules.eca.EventType;


/**
 *  Parameter model.
 */
public class MParameter extends MElement
{
	public static Map<String, Direction> dirs = new HashMap<String, Direction>();

	/** The message direction. */
	public enum Direction
	{
		IN("in"),
		OUT("out"),
		INOUT("inout"),
		FIXED("fixed");
		
		protected String str;
		
		/**
		 *  Create a new direction
		 */
		Direction(String str)
		{
			this.str = str;
			dirs.put(str, this);
		} 
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String getString()
		{
			return str;
		}
		
		/**
		 * 
		 */
		public static Direction getDirection(String name)
		{
			return dirs.get(name);
		}
	}
	
	public static Map<String, EvaluationMode> evas = new HashMap<String, EvaluationMode>();

	/** The message direction. */
	public enum EvaluationMode
	{
		STATIC("static"), 
		PUSH("push"), // change when other beliefs change
		PULL("pull"), // recalculate on access
		POLLING("polling");
		
		protected String str;
		
		/**
		 *  Create a new direction
		 */
		EvaluationMode(String str)
		{
			this.str = str;
			evas.put(str, this);
		} 
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String getString()
		{
			return str;
		}
		
		/**
		 * 
		 */
		public static EvaluationMode getEvaluationMode(String name)
		{
			return evas.get(name);
		}
	}
	
	//-------- attributes --------
	
	/** The field target. */
	protected FieldInfo ftarget;

	/** The method targets. */
	protected MethodInfo mgetter;
	protected MethodInfo msetter;
	
	/** Flag if is multi. */
	protected Boolean multi;
	
	//-------- additional xml properties --------
	
	/** The direction. */
	protected Direction direction = Direction.IN; // default is 'in'
	
	/** The type (if explicitly specified). */
	protected ClassInfo clazz;
	
	/** The default value. */
	protected UnparsedExpression value;
	
	/** The default values (multi). */
	protected List<UnparsedExpression> values;
	
	/** The optional flag. */
	protected boolean optional; 
	
	/** The binding options. */
	protected UnparsedExpression bindingoptions;
	
	/** The direction. */
	protected EvaluationMode evaluationmode = EvaluationMode.STATIC;
	
	/** The update rate. */
	protected UnparsedExpression updaterate;
	
	// Currently unused -> todo support in XML / Annotations.
//	/** The events this belief depends on. */
//	protected Set<String> beliefevents;
//	
//	/** The raw events. */
//	protected Set<EventType> rawevents;
	
	/** The ECA events that may denote changes in the parameter value(s). */
	protected List<EventType> events;
	
	/** The service mappings. */
	protected List<String> servicemappings;
	
	/**
	 *	Bean Constructor. 
	 */
	public MParameter()
	{
	}
	
	/**
	 *  Create a new parameter.
	 */
	public MParameter(FieldInfo ftarget)
	{
		super(ftarget!=null? ftarget.getName(): null);
		this.ftarget = ftarget;
//		System.out.println("bel: "+(target!=null?target.getName():"")+" "+dynamic);
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
	 *  Test if this belief refers to a field.
	 *  @return True if is a field belief.
	 */
	public boolean isFieldParameter()
	{
		return ftarget!=null;
	}
	
//	/**
//	 *  Get the value of the belief.
//	 */
//	public Object getValue(IInternalAccess agent)
//	{
//		String	capaname	= getName().indexOf(MElement.CAPABILITY_SEPARATOR)==-1
//			? null : getName().substring(0, getName().lastIndexOf(MElement.CAPABILITY_SEPARATOR));
//		return getValue(((BDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class)).getCapabilityObject(capaname), agent.getClassLoader());
//	}
//
	/**
	 *  Get the value of the belief.
	 *  @param object The rparameterelement (such as goal).
	 *  @param cl The classloader.
	 */
	public Object getValue(Object object, ClassLoader cl)
	{
		Object ret = null;
		if(ftarget!=null)
		{
			try
			{
				Field f = ftarget.getField(cl);
				f.setAccessible(true);
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
	
//	/**
//	 *  Set the value of the belief.
//	 */
//	public boolean setValue(BDIAgentInterpreter bai, Object value)
//	{
//		String	capaname	= getName().indexOf(BDIAgentInterpreter.CAPABILITY_SEPARATOR)==-1
//			? null : getName().substring(0, getName().lastIndexOf(BDIAgentInterpreter.CAPABILITY_SEPARATOR));
//		return setValue(bai.getCapabilityObject(capaname), value, bai.getClassLoader());
//	}

	/**
	 *  Set the value of the parameter.
	 *  @param object The rparameterelement (such as goal).
	 *  @param value The value.
	 *  @param cl The classloader.
	 */
	public boolean setValue(Object object, Object value, ClassLoader cl)
	{
		boolean field	= false;
		if(ftarget!=null)
		{
			field	= true;
			try
			{
				Field f = ftarget.getField(cl);
				f.setAccessible(true);
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
		else if(value!=null)
		{
			// todo: imports?
			ret = SJavaParser.parseExpression(value, null, cl).getStaticType();
		}
		else if(values!=null && values.size()>0)
		{
			// todo: imports?
			ret = SJavaParser.parseExpression(values.get(0), null, cl).getStaticType();
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
		}
		return multi;
	}

	/**
	 *  The multi to set.
	 *  @param multi The multi to set
	 */
	public void setMulti(boolean multi)
	{
//		System.out.println("setmulti: "+multi+" "+getName());
		this.multi = multi? Boolean.TRUE: Boolean.FALSE;
	}
	
	/**
	 *  Test if parameter is of array type.
	 */
	public boolean isArray()
	{
		boolean ret = false;
		if(isFieldParameter() && ftarget.getClassName()!=null)
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
	 *  Get the direction.
	 *  @return The direction
	 */
	public Direction getDirection()
	{
		return direction;
	}

	/**
	 *  The direction to set.
	 *  @param direction The direction to set
	 */
	public void setDirection(Direction direction)
	{
		this.direction = direction;
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
	 *  Get the value.
	 *  @return The value
	 */
	public UnparsedExpression getDefaultValue()
	{
		// The default value must not null, when a basic type is declared.
		// Hence a new default value is created.
		if(value==null && values==null && getClazz()!=null && clazz!=null)
		{
			if(clazz.getTypeName()=="boolean")
				value = new UnparsedExpression(null, "false");
			else if(clazz.getTypeName()=="byte")
				value = new UnparsedExpression(null, "0");
			else if(clazz.getTypeName()=="char")
				value = new UnparsedExpression(null, "0");
			else if(clazz.getTypeName()=="short")
				value = new UnparsedExpression(null, "0");
			else if(clazz.getTypeName()=="double")
				value = new UnparsedExpression(null, "0");
			else if(clazz.getTypeName()=="float")
				value = new UnparsedExpression(null, "0");
			else if(clazz.getTypeName()=="long")
				value = new UnparsedExpression(null, "0");
			else if(clazz.getTypeName()=="int")
				value = new UnparsedExpression(null, "0");
			
			if(value!=null)
			{
				// Make sure parsed expression is set...
				SJavaParser.parseExpression(value, null, null);
			}
		}
		
		return value;
	}

	/**
	 *  The value to set.
	 *  @param value The value to set
	 */
	public void setDefaultValue(UnparsedExpression value)
	{
		this.value = value;
	}
	
	/**
	 *  Get the value.
	 *  @return The value
	 */
	public List<UnparsedExpression> getDefaultValues()
	{
		return values;
	}

	/**
	 *  The value to set.
	 *  @param value The value to set
	 */
	public void setDefaultValues(List<UnparsedExpression> values)
	{
		this.values = values;
	}
	
	/**
	 *  The value to set.
	 *  @param value The value to set
	 */
	public void addDefaultValues(UnparsedExpression fact)
	{
		if(values==null)
			values = new ArrayList<UnparsedExpression>();
		values.add(fact);
	}
	
	/**
	 *  Get the optional flag.
	 *  @return The otpional flag
	 */
	public boolean isOptional()
	{
		return optional;
	}

	/**
	 *  Set the optional flag.
	 *  @param optional The value to set
	 */
	public void setOptional(boolean optional)
	{
		this.optional = optional;
	}

	/**
	 *  Get the bindingOptions.
	 *  @return The bindingOptions
	 */
	public UnparsedExpression getBindingOptions()
	{
		return bindingoptions;
	}

	/**
	 *  The binding options to set.
	 *  @param bindingoptions The bindingOptions to set
	 */
	public void setBindingOptions(UnparsedExpression bindingoptions)
	{
		this.bindingoptions = bindingoptions;
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
			ret = ((Number)SJavaParser.parseExpression(updaterate, agent.getModel().getAllImports(), agent.getClassLoader())
				.getValue(CapabilityWrapper.getFetcher(agent, updaterate.getLanguage()))).longValue();
		return ret;
	}
	
	// Currently unused -> todo support in XML / Annotations.
//	/**
//	 *  Get the rawevents.
//	 *  @return The rawevents.
//	 */
//	public Set<EventType> getRawEvents()
//	{
//		return rawevents;
//	}
//
//	/**
//	 *  Set the rawevents.
//	 *  @param rawevents The rawevents to set.
//	 */
//	public void setRawEvents(Set<EventType> rawevents)
//	{
//		this.rawevents = rawevents;
//	}
//	
//	/**
//	 *  Get the events.
//	 *  @return The events.
//	 */
//	public Set<String> getBeliefEvents()
//	{
//		return beliefevents;
//	}
//
//	/**
//	 *  Set the events.
//	 *  @param events The events to set.
//	 */
//	public void setBeliefEvents(Set<String> events)
//	{
//		this.beliefevents	= events;
//	}
	
	/**
	 *  Get the events.
	 *  @return The events.
	 */
	public List<EventType> getEvents()
	{
		return events;
	}
	
	/**
	 *  Init the event, when loaded from xml.
	 */
	public void	initEvents(MParameterElement owner)
	{
		if(events==null)
			events = new ArrayList<EventType>();
		
		// Currently unused -> todo support in XML / Annotations.
//		Collection<String> evs = getBeliefEvents();
//		if(evs!=null && !evs.isEmpty())
//		{
//			for(String ev: evs)
//			{
//				BDIAgentFeature.addBeliefEvents(agent, allevents, ev);
//			}
//		}
		
		// Currently unused -> todo support in XML / Annotations.
//		Collection<EventType> rawevents = getRawEvents();
//		if(rawevents!=null)
//			allevents.addAll(rawevents);
		
		// Hack!!! what about initial values?
		if(getDefaultValue()!=null)
		{
			BDIAgentFeature.addExpressionEvents(getDefaultValue(), events, owner);
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

	/**
	 *  Get the service mappings.
	 */
	public List<String>	getServiceMappings()
	{
		return servicemappings;
	}
	
	/**
	 *  Set the service mappings.
	 */
	public void	setServiceMappings(List<String> servicemappings)
	{
		this.servicemappings	= servicemappings;
	}
	
	/**
	 *  Add a service mapping.
	 */
	public void	addServiceMapping(String mapping)
	{
		if(servicemappings==null)
		{
			servicemappings	= new ArrayList<String>();
		}
		servicemappings.add(mapping);
	}
}
