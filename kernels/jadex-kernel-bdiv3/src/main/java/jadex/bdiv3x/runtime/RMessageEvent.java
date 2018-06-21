package jadex.bdiv3x.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bdiv3.model.MConfigParameterElement;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.BeanIntrospectorFactory;
import jadex.commons.transformation.traverser.BeanProperty;
import jadex.commons.transformation.traverser.IBeanIntrospector;

/**
 *  The runtime message event.
 */
public class RMessageEvent<T> extends RProcessableElement implements IMessageEvent<T> 
{
	//-------- attributes --------
	
	/** The message. */
	protected T msg;
	
	/** The original message, if this message is a reply. */
	RMessageEvent<T>	original;
	
	//-------- constructors --------
	
	/**
	 *  Create a new runtime element.
	 */
	public RMessageEvent(MMessageEvent modelelement, IInternalAccess agent, MConfigParameterElement config)
	{
		super(modelelement, null, agent, null, config);
		// Create initial pojo value.
		try
		{
			@SuppressWarnings("unchecked")
			T t = (T)modelelement.getType().getType(agent.getClassLoader()).newInstance();
			this.msg	= t;
		}
		catch(Exception e)
		{
			SUtil.throwUnchecked(e);
		}
		
		// Must be done after msg has been assigned :-(
		// 1 -> Create parameters from model
		super.initParameters(null, config);
		
		// In case of messages there can be parameters only in the config, not in the model due to underlying message type definition
		// 2 -> Create parameters from config
		IBeanIntrospector	bi	= BeanIntrospectorFactory.getInstance().getBeanIntrospector();
		Map<String, BeanProperty>	props	= bi.getBeanProperties(msg.getClass(), true, false);
		if(config!=null && config.getParameters()!=null)
		{
			for(Map.Entry<String, List<UnparsedExpression>> entry: config.getParameters().entrySet())
			{
				if(!hasParameter(entry.getKey()) && !hasParameterSet(entry.getKey()))
				{
					if(SReflect.isIterableClass(props.get(entry.getKey()).getType()))
					{
						addParameterSet(createParameterSet(null, entry.getKey(), getAgent(), config.getParameters(entry.getKey())));
					}
					else
					{
						addParameter(createParameter(null, entry.getKey(), getAgent(), config.getParameter(entry.getKey())));
					}
				}
			}
		}
		
		// Finally add remaining properties from pojo as parameters.
		// 3 -> Create parameters from pojo
		for(Map.Entry<String, BeanProperty> entry: props.entrySet())
		{
			if(!hasParameter(entry.getKey()) && !hasParameterSet(entry.getKey()))
			{
				if(SReflect.isIterableClass(entry.getValue().getType()))
				{
					addParameterSet(createParameterSet(null, entry.getKey(), getAgent(), (Object)null));					
				}
				else
				{
					addParameter(createParameter(null, entry.getKey(), getAgent(), (Object)null));
				}
			}
		}
	}
	
	/**
	 *  Create a new runtime element.
	 *  
	 *  Constructor Without parameter init for received messages.
	 */
	public RMessageEvent(MMessageEvent modelelement, T msg, IInternalAccess agent, RMessageEvent<T> original)
	{
		super(modelelement, null, agent, null, null);
		this.msg = msg;
		this.original	= original;
		
		Map<String, Object>	def	= null;
		if(msg!=null)
		{
			def	= new HashMap<String, Object>();
			IBeanIntrospector	bi	= BeanIntrospectorFactory.getInstance().getBeanIntrospector();
			Map<String, BeanProperty>	props	= bi.getBeanProperties(msg.getClass(), true, false);
			for(Map.Entry<String, BeanProperty> entry: props.entrySet())
			{
				Object	val	= entry.getValue().getPropertyValue(msg);
				if(val!=null)
				{
					def.put(entry.getKey(), val);
				}
			}
		}
		
		// Tricky, must do init for default values if NOT present in the msg
		// Must be done after msg has been assigned :-(
		super.initParameters(def, null);
		
		if(msg!=null)
		{
			// Finally add remaining properties from pojo as parameters.
			IBeanIntrospector	bi	= BeanIntrospectorFactory.getInstance().getBeanIntrospector();
			Map<String, BeanProperty>	props	= bi.getBeanProperties(msg.getClass(), true, false);
			for(Map.Entry<String, BeanProperty> entry: props.entrySet())
			{
				if(!hasParameter(entry.getKey()) && !hasParameterSet(entry.getKey()))
				{
					if(SReflect.isIterableClass(entry.getValue().getType()))
					{
						addParameterSet(createParameterSet(null, entry.getKey(), getAgent(), def.get(entry.getKey())));					
					}
					else
					{
						addParameter(createParameter(null, entry.getKey(), getAgent(), def.get(entry.getKey())));
					}
				}
			}
		}
	}
	
	/**
	 *  Create the parameters from model spec.
	 */
	@Override
	public void initParameters(Map<String, Object> vals, MConfigParameterElement config)
	{
		// do nothing in super constructor init 
	}
//	
	/**
	 *  Get the name of the element in the fetcher (e.g. $goal).
	 *  @return The element name in the fetcher name.
	 */
	public String getFetcherName()
	{
		return "$event";
	}
	
	/**
	 * 
	 */
	@Override
	public IParameter createParameter(MParameter modelelement, String name, IInternalAccess agent, UnparsedExpression inival)
	{
		return new RParam(modelelement, name, agent, inival, getModelElement().getName());
	}
	
	/**
	 * 
	 */
	@Override
	public IParameterSet createParameterSet(MParameter modelelement, String name, IInternalAccess agent, List<UnparsedExpression> inivals)
	{
		return new RParamSet(modelelement, name, agent, inivals, getModelElement().getName());
	}
	
	/**
	 * 
	 */
	@Override
	public IParameter createParameter(MParameter modelelement, String name, IInternalAccess agent, Object value)
	{
		return new RParam(modelelement, name, agent, value, getModelElement().getName());
	}
	
	/**
	 * 
	 */
	@Override
	public IParameterSet createParameterSet(MParameter modelelement, String name, IInternalAccess agent, Object values)
	{
		return new RParamSet(modelelement, name, agent, values, getModelElement().getName());
	}

	//-------- methods --------
	
	/**
	 *  Has the element a parameter element.
	 *  @param name The name.
	 *  @return True, if it has the parameter.
	 */
	@Override
	public boolean hasParameter(String name)
	{
		return super.hasParameter(name) || super.hasParameter(SUtil.camelToSnakeCase(name));
	}

	/**
	 *  Has the element a parameter set element.
	 *  @param name The name.
	 *  @return True, if it has the parameter set.
	 */
	@Override
	public boolean hasParameterSet(String name)
	{
		return super.hasParameterSet(name) || super.hasParameterSet(SUtil.camelToSnakeCase(name));
	}
	
//	/**
//	 *  Get all parameters.
//	 *  @return All parameters.
//	 */
//	public IParameter[] getParameters()
//	{
//		List<IParameter> ret = new ArrayList<IParameter>();
//		for(String name: mt.getParameterNames())
//		{
//			ret.add(getParameter(name));
//		}
//		return ret.toArray(new IParameter[ret.size()]);
//	}
//
//	/**
//	 *  Get all parameter sets.
//	 *  @return All parameter sets.
//	 */
//	public IParameterSet[] getParameterSets()
//	{
//		List<IParameterSet> ret = new ArrayList<IParameterSet>();
//		for(String name: mt.getParameterSetNames())
//		{
//			ret.add(getParameterSet(name));
//		}
//		return ret.toArray(new IParameterSet[ret.size()]);
//	}

	/**
	 *  Get the parameter element.
	 *  @param name The name.
	 *  @return The param.
	 */
	public IParameter getParameter(String name)
	{
		IParameter param;
		if(super.hasParameter(SUtil.camelToSnakeCase(name)))
		{
			param	= super.getParameter(SUtil.camelToSnakeCase(name));
		}
		else if(super.hasParameter(SUtil.snakeToCamelCase(name)))
		{
			param	= super.getParameter(SUtil.snakeToCamelCase(name));
		}
		else
		{
			// Throws exception when not found.
			param	= super.getParameter(name);
		}
		return param;
	}

	/**
	 *  Get the parameter set element.
 	 *  @param name The name.
	 *  @return The param set.
	 */
	public IParameterSet getParameterSet(String name)
	{
		IParameterSet param;
		if(super.hasParameterSet(SUtil.camelToSnakeCase(name)))
		{
			param	= super.getParameterSet(SUtil.camelToSnakeCase(name));
		}
		else if(super.hasParameterSet(SUtil.snakeToCamelCase(name)))
		{
			param	= super.getParameterSet(SUtil.snakeToCamelCase(name));
		}
		else
		{
			// Throws exception when not found.
			param	= super.getParameterSet(name);
		}
		return param;
	}

	/**
	 *  Get the native (platform specific) message object.
	 *  @return The native message.
	 */
	public T getMessage()
	{
		return msg;
	}
	
	/**
	 *  Get the original message event (if this is a reply).
	 */
	public RMessageEvent<T> getOriginal()
	{
		return original;
	}
	
	/**
	 * 
	 */
	public MMessageEvent getMMessageEvent()
	{
		return (MMessageEvent)getModelElement();
	}
	
	/**
	 * 
	 */
	public class RParam extends RParameter
	{
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParam(MParameter modelelement, String name, IInternalAccess agent, String pename)
		{
			super(modelelement, name, agent, pename);
		}
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParam(MParameter modelelement, String name, IInternalAccess agent, UnparsedExpression inival, String pename)
		{
			super(modelelement, name, agent, inival, pename);
		}
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParam(MParameter modelelement, String name, IInternalAccess agent, Object value, String pename)
		{
			super(modelelement, name, agent, value, pename);
		}
		
		/**
		 *  Set a value of a parameter.
		 *  @param value The new value.
		 */
		public void setValue(Object value)
		{
			BeanProperty bp = findBeanProperty(getName());
			publisher.entryChanged(bp.getPropertyValue(msg), value, -1);
			bp.setPropertyValue(msg, value);
		}

		/**
		 *  Get the value of a parameter.
		 *  @return The value.
		 */
		public Object	getValue()
		{
			BeanProperty bp = findBeanProperty(getName());
			return bp.getPropertyValue(msg);
		}
	}
	
	/**
	 * 
	 */
	public class RParamSet extends RParameterSet
	{
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParamSet(MParameter modelelement, String name, IInternalAccess agent, String pename)
		{
			super(modelelement, name, agent, pename);
		}
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParamSet(MParameter modelelement, String name, IInternalAccess agent, List<UnparsedExpression> inivals, String pename)
		{
			super(modelelement, name, agent, inivals, pename);
		}
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParamSet(MParameter modelelement, String name, IInternalAccess agent, Object values, String pename)
		{
			super(modelelement, name, agent, values, pename);
		}
		
		/**
		 *  Get the class of a value.
		 */
		@Override
		protected Class<?> getClazz()
		{
			Class<?> ret = getModelElement()!=null? super.getClazz(): null;
			if(ret==null)
			{
				BeanProperty bp = findBeanProperty(getName());
				ret	= SReflect.getIterableComponentType(bp.getGenericType()!=null ? bp.getGenericType() : bp.getType());
			}
			return ret;
		}
		
		/**
		 *  The values to set.
		 *  @param values The values to set
		 */
		protected void internalSetValues(List<Object> values)
		{
			BeanProperty bp = findBeanProperty(getName());
			Object	value	= SReflect.createComposite(bp.getGenericType()!=null ? bp.getGenericType() : bp.getType(), values);
			bp.setPropertyValue(msg, value);
		}
		
		/**
		 *  Adapt to message type for implicit parameters.
		 */
		@Override
		public Object[] getValues()
		{
			Object[]	ret;
			if(getModelElement()==null)
			{
				BeanProperty bp = findBeanProperty(getName());
				ret	= super.getValues(SReflect.getIterableComponentType(bp.getGenericType()!=null ? bp.getGenericType() : bp.getType()));
			}
			else
			{
				ret	= super.getValues();
			}
			
			return ret;
		}
		
		/**
		 * 
		 */
		protected List<Object> internalGetValues()
		{
			BeanProperty bp = findBeanProperty(getName());
			Object vals = bp.getPropertyValue(msg);
			List<Object>	ret	= new ArrayList<Object>();
			if(vals!=null)
			{
				for(Object o: SReflect.getIterable(vals))
				{
					ret.add(o);
				}
			}
			return ret;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void internalAddValue(Object value)
		{
			BeanProperty bp = findBeanProperty(getName());
			Object vals = bp.getPropertyValue(msg);
			if(vals instanceof Collection)
			{
				// TODO: doesn't work if copy is given (should try to find addXXX method???)
				((Collection<Object>)vals).add(value);
			}
			else if(vals==null)
			{
				vals	= SReflect.createComposite(bp.getGenericType()!=null ? bp.getGenericType() : bp.getType(), Collections.singleton(value));		
				bp.setPropertyValue(msg, vals);
			}
			else
			{
				throw new UnsupportedOperationException("Composite type not supported: "+vals);
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void internalRemoveValue(Object value)
		{
			BeanProperty bp = findBeanProperty(getName());
			Object vals = bp.getPropertyValue(msg);
			if(vals instanceof Collection)
			{
				((Collection<Object>)vals).remove(value);
			}
			else
			{
				// TODO
				throw new UnsupportedOperationException("Composite type not supported: "+vals);
			}
		}
		
		@Override
		protected void internalRemoveValues()
		{
			BeanProperty bp = findBeanProperty(getName());
			bp.setPropertyValue(msg, null);
		}
	}
	
	/** 
	 *  Get the string represntation.
	 */
	public String toString()
	{
//		return "RGoal(lifecyclestate=" + lifecyclestate + ", processingstate="
//			+ processingstate + ", state=" + state + ", id=" + id + ")";
		return "RMessageEvent: "+msg;
	}

	protected BeanProperty findBeanProperty(String name)
	{
		IBeanIntrospector	bi	= BeanIntrospectorFactory.getInstance().getBeanIntrospector();
		BeanProperty	bp	= bi.getBeanProperties(msg.getClass(), true, false).get(name);

		// If not found -> try converting underscore to camel case.
		if(bp==null)
		{
			bp	= bi.getBeanProperties(msg.getClass(), true, false).get(SUtil.snakeToCamelCase(name));
		}
		
		return bp;
	}
}
