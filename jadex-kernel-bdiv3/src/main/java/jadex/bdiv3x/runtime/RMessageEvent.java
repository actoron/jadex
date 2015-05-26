package jadex.bdiv3x.runtime;

import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.runtime.impl.RElement;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.message.MessageType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * 
 */
public class RMessageEvent extends RProcessableElement implements IMessageEvent 
{
	//-------- attributes --------
	
	/** The message. */
	protected Map<String, Object> msg;
	
	/** The message type. */
	protected MessageType mt;
	
	//-------- constructors --------
	
	/**
	 *  Create a new runtime element.
	 */
	public RMessageEvent(MMessageEvent modelelement, Map<String, Object> msg, MessageType mt, IInternalAccess agent)
	{
		super(modelelement, null, agent);
		this.msg = msg;
		this.mt = mt;
	}
	
	/**
	 *  Create the parameters from model spec.
	 */
	public void initParameters()
	{
		// do nothing
		// todo: do we want to create all parameters of the message template
		// or only when getParameters() is called?!
	}
	
	//-------- methods --------
	
	/**
	 *  Get all parameters.
	 *  @return All parameters.
	 */
	public IParameter[] getParameters()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Get all parameter sets.
	 *  @return All parameter sets.
	 */
	public IParameterSet[] getParameterSets()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Get the parameter element.
	 *  @param name The name.
	 *  @return The param.
	 */
	public IParameter getParameter(String name)
	{
		if(mt.getParameter(name)==null)
			throw new RuntimeException("Unknown parameter: "+name);
		
		MParameter mp = getMMessageEvent().getParameter(name);
		return new RParameter(mp, name, getAgent());
	}

	/**
	 *  Get the parameter set element.
 	 *  @param name The name.
	 *  @return The param set.
	 */
	public IParameterSet getParameterSet(String name)
	{
		if(mt.getParameterSet(name)==null)
			throw new RuntimeException("Unknown parameter set: "+name);
		
		MParameter mp = getMMessageEvent().getParameter(name);
		return new RParameterSet(mp, name, getAgent());
	}

	/**
	 *  Has the element a parameter element.
	 *  @param name The name.
	 *  @return True, if it has the parameter.
	 */
	public boolean hasParameter(String name)
	{
		return mt.getParameter(name)!=null;
	}
	
	/**
	 *  Has the element a parameter set element.
	 *  @param name The name.
	 *  @return True, if it has the parameter set.
	 */
	public boolean hasParameterSet(String name)
	{
		return mt.getParameterSet(name)!=null;
	}

	/**
	 *  Get the native (platform specific) message object.
	 *  @return The native message.
	 */
	public Object getMessage()
	{
		return msg;
	}

	/**
	 *  Get the message type.
	 *  @return The message type.
	 */
	public MessageType getMessageType()
	{
		return mt;
	}
	
//	/**
//	 *  Get the element type (i.e. the name declared in the ADF).
//	 *  @return The element type.
//	 */
//	public String getType()
//	{
//		return getModelElement().getName();
//	}
	
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
	public class RParameter extends RElement implements IParameter
	{
		/** The name. */
		protected String name;

		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParameter(MElement modelelement, String name, IInternalAccess agent)
		{
			super(modelelement, agent);
			this.name = name;
		}

		/**
		 *  Get the name.
		 *  @return The name
		 */
		public String getName()
		{
			return name;
		}
		
		/**
		 *  Set a value of a parameter.
		 *  @param value The new value.
		 */
		public void setValue(Object value)
		{
			msg.put(name, value);
		}

		/**
		 *  Get the value of a parameter.
		 *  @return The value.
		 */
		public Object	getValue()
		{
			return msg.get(name);
		}
	}
	
	/**
	 * 
	 */
	public class RParameterSet extends RElement implements IParameterSet
	{
		/** The name. */
		protected String name;
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParameterSet(MElement modelelement, String name, IInternalAccess agent)
		{
			super(modelelement, agent);
			this.name = name;
		}

		/**
		 *  Get the name.
		 *  @return The name
		 */
		public String getName()
		{
			return name;
		}
		
		/**
		 *  Add a value to a parameter set.
		 *  @param value The new value.
		 */
		public void addValue(Object value)
		{
			Collection<Object> values = (Collection<Object>)msg.get(name);
			if(values==null)
				values = new ArrayList<Object>();
			values.add(value);
		}

		/**
		 *  Remove a value to a parameter set.
		 *  @param value The new value.
		 */
		public void removeValue(Object value)
		{
			Collection<Object> values = (Collection<Object>)msg.get(name);
			if(values!=null)
				values.remove(value);
		}

		/**
		 *  Add values to a parameter set.
		 */
		public void addValues(Object[] values)
		{
			if(values!=null)
			{
				for(Object value: values)
				{
					addValue(value);
				}
			}
		}

		/**
		 *  Remove all values from a parameter set.
		 */
		public void removeValues()
		{
			Collection<Object> values = (Collection<Object>)msg.get(name);
			if(values!=null)
				values.clear();
		}

		/**
		 *  Get a value equal to the given object.
		 *  @param oldval The old value.
		 */
//		public Object	getValue(Object oldval);

		/**
		 *  Test if a value is contained in a parameter.
		 *  @param value The value to test.
		 *  @return True, if value is contained.
		 */
		public boolean containsValue(Object value)
		{
			Collection<Object> values = (Collection<Object>)msg.get(name);
			return values==null? false: values.contains(value);
		}

		/**
		 *  Get the values of a parameterset.
		 *  @return The values.
		 */
		public Object[]	getValues()
		{
			Collection<Object> values = (Collection<Object>)msg.get(name);
			return values==null? new Object[0]: values.toArray();
		}

		/**
		 *  Get the number of values currently
		 *  contained in this set.
		 *  @return The values count.
		 */
		public int size()
		{
			Collection<Object> values = (Collection<Object>)msg.get(name);
			return values==null? 0: values.size();
		}
	}
}
