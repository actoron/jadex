package jadex.bdiv3x.runtime;

import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.runtime.impl.RElement;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.bridge.service.types.message.MessageType;

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
	
	/** The finished flag. */
	boolean finished;
	
	//-------- constructors --------
	
	/**
	 *  Create a new runtime element.
	 */
	public RMessageEvent(MMessageEvent modelelement, Map<String, Object> msg, MessageType mt)
	{
		super(modelelement, null);
		this.msg = msg;
		this.mt = mt;
	}
	
	//-------- methods --------
	
	/**
	 *  Get all parameters.
	 *  @return All parameters.
	 */
	public IParameter[] getParameters()
	{
		return null;
	}

	/**
	 *  Get all parameter sets.
	 *  @return All parameter sets.
	 */
	public IParameterSet[] getParameterSets()
	{
		return null;
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
		return new RParameter(mp, name);
	}

	/**
	 *  Get the parameter set element.
 	 *  @param name The name.
	 *  @return The param set.
	 */
	public IParameterSet getParameterSet(String name)
	{
		return null;
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
	
	/**
	 *  Get the element type (i.e. the name declared in the ADF).
	 *  @return The element type.
	 */
	public String getType()
	{
		return getModelElement().getName();
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
	public class RParameter extends RElement implements IParameter
	{
		/** The name. */
		protected String name;

		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RParameter(MElement modelelement, String name)
		{
			super(modelelement);
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

	//todo: set finished
	
	/**
	 *  Test if element is succeeded.
	 */
	public boolean isSucceeded()
	{
		return finished && exception==null;
	}
	
	/**
	 *  Test if element is failed.
	 */
	public boolean isFailed()
	{
		return finished && exception!=null;
	}
}
