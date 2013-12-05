package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEElement;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.rules.state.IOAVState;

/**
 *  Model element flyweight.
 */
public class MElementFlyweight implements IMElement, IMEElement
{
	//-------- attributes --------
	
	/** The state. */
	private IOAVState state;
	
	/** The object handle for the element. */
	private Object handle;

	/** The object handle for the element's scope. */
	// Todo: remove???
	private Object scope;
	
	/** The interpreter. */
	private BDIInterpreter interpreter;
	
	/** Flag to indicate if the flyweight was already cleaned up. */
	private boolean	cleanedup;
	
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param handle	The element handle.
	 *  @param rplan	The calling plan (if called from plan)
	 */
	public MElementFlyweight(IOAVState state, Object scope, Object handle)
	{
		assert !(scope instanceof ElementFlyweight);
		assert handle!=null;
		
		this.state = state;
		this.scope = scope;
		if(scope!=null)
			state.addExternalObjectUsage(scope, this);
		
		this.interpreter = BDIInterpreter.getInterpreter(state);
		setHandle(handle);
	}
	
	//-------- element methods ---------

	/**
	 *  Get the name.
	 *  @return The name. 
	 */
	public String getName()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.modelelement_has_name);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.modelelement_has_name);
		}
	}
	
	/**
	 *  Get the description.
	 *  @return The description. 
	 */
	public String getDescription()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.modelelement_has_description);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.modelelement_has_description);
		}
	}
	
	/**
	 *  The hash code.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (!hasHandle() ? 0 : getHandle().hashCode());
		result = prime * result + ((getScope() == null) ? 0 : getScope().hashCode());
		result = prime * result + ((getState() == null) ? 0 : getState().hashCode());
		return result;
	}

	/**
	 *  Test equality.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof MElementFlyweight)
		{
			MElementFlyweight other = (MElementFlyweight)obj;
			ret = SUtil.equals(getHandle(), other.getHandle()) 
//				&& SUtil.equals(getScope(), other.getScope()) 
				&& SUtil.equals(getState(), other.getState());
		}
		return ret;
	}
	
	/**
	 *  Get the state.
	 *  @return The state.
	 */
	public IOAVState getState()
	{
		return state;
	}
	
	/**
	 *  Test, if the handle has already been set.
	 */
	public boolean	hasHandle()
	{
		return handle!=null;
	}

	/**
	 *  Get the handle.
	 *  @return The handle.
	 */
	public Object getHandle()
	{
		if(handle==null)
			throw new UnsupportedOperationException("Cannot get handle before it is set: "+this.getClass());
		return handle;
	}
	
	/**
	 *  Set the handle.
	 *  @param handle The handle to set.
	 */
	protected void setHandle(Object handle)
	{
		assert this.handle==null;
		this.handle = handle;
		// Only called from synchronized code -> no agent invocation necessary 
		if(handle!=null)
			state.addExternalObjectUsage(handle, this);
	}

	/**
	 *  Get the scope.
	 *  @return The scope.
	 */
	public Object getScope()
	{
		return scope;
	}
	
	/**
	 *  Get the interpreter.
	 *  @return The interpreter.
	 */
	public BDIInterpreter getInterpreter()
	{
		return interpreter;
	}
	
	/**
	 *  Set the name.
	 *  @param name The name. 
	 */
	public void setName(final String name)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.modelelement_has_name, name);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.modelelement_has_name, name);
		}
	}
	
	/**
	 *  Set the description.
	 *  @param desc The description. 
	 */
	public void setDescription(final String desc)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.modelelement_has_description, desc);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.modelelement_has_description, desc);
		}
	}
	
	/**
	 *  Remove the external usage preventing
	 *  the state object from being garbage
	 *  collected.
	 * /
	// Hack!!! finalize() is required to call state.removeExternalObjectUsage(...)
	// unless ContentIDState is used, which ignores external object usages.
	protected void finalize() throws Throwable
	{
		// Must be done on agent thread, not
		// on gc thread.
		if(!cleanedup)
		{
//			String	id	= ""+handle;
//			System.err.println("cleaning up: "+id+", "+cnt[0]);
			try
			{
				interpreter.invokeSynchronized(new Runnable()
				{
					String element	= ""+handle;
					public void run()
					{
						cleanup();
					}
					
					public String toString()
					{
						return super.toString()+", "+element;
					}
				});
			}
			catch(Throwable e)
			{
				// Exception can occur if agent is already terminated.
				// So nothing to do in that case.
//				System.out.println("Agent already terminated: "+id);
//				e.printStackTrace();
			}
//			System.err.println("cleaned up: "+id+", "+cnt[0]);
		}
		super.finalize();
	}*/
	
	/**
	 *  Cleanup the flyweight.
	 *  Must be called on agent thread.
	 */
	public final void	cleanup()
	{
		if(!cleanedup)
			doCleanup();
		cleanedup	= true;
	}
	
	/**
	 *  Actual cleanup code.
	 *  When overriding this method, super.doCleanup() has to be called. 
	 */
	protected void	doCleanup()
	{
		if(handle!=null)
		{
			state.removeExternalObjectUsage(handle, this);
			handle	= null;
		}
		if(scope!=null)
		{
			state.removeExternalObjectUsage(scope, this);
			scope	= null;
		}		
	}

	/**
	 *  Get a string representation of this element.
	 */
	public String toString()
	{
		String string	= SReflect.getUnqualifiedClassName(this.getClass());
		if(string.endsWith("Flyweight"))
			string	= string.substring(0, string.length()-9);
		return string;
		
//		if(isExternalThread())
//		{
//			AgentInvocation	invoc	= new AgentInvocation()
//			{
//				public void run()
//				{
//					string	= SReflect.getUnqualifiedClassName(this.getClass());
//					if(string.endsWith("Flyweight"))
//						string	= string.substring(0, string.length()-9);
//					string	+= "("+getTypeName()+"-"+getHandle()+")";
//				}
//			};
//			return invoc.string;
//		}
//		else
//		{
//			String ret	= SReflect.getUnqualifiedClassName(this.getClass());
//			if(ret.endsWith("Flyweight"))
//				ret	= ret.substring(0, ret.length()-9);
//			return ret+"("+getTypeName()+"-"+getHandle()+")";
//		}
	}
	
	//-------- inner classes --------

	/**
	 *  An action to be executed on the agent thread.
	 *  Provides predefined variables to store results.
	 *  Directly invokes agenda in construcor.
	 */
	public abstract class AgentInvocation	implements Runnable
	{
		//-------- attributes --------

		/** Argument. */
		public Object arg;
		
		/** Arguments. */
		public Object[] args;
		
		//-------- out parameters --------
		
		/** The object result variable. */
		public Object	object;

		/** The string result variable. */
		public String	string;

		/** The int result variable. */
		public int	integer;

		/** The long result variable. */
		public long	longint;

		/** The boolean result variable. */
		public boolean	bool;

		/** The object array result variable. */
		public Object[]	oarray;

		/** The string result variable. */
		public String[]	sarray;

		/** The class result variable. */
		public Class	clazz;

		/** The exception. */
		public Exception exception;
		
		//-------- constructors --------

		/**
		 *  Create an action to be executed in sync with the agent thread.
		 */
		public AgentInvocation()
		{
			this(null);
		}
		
		/**
		 *  Create an action to be executed in sync with the agent thread.
		 */
		public AgentInvocation(Object arg)
		{
			this.arg = arg;
			getInterpreter().invokeSynchronized(this);
		}
		
		/**
		 *  Create an action to be executed in sync with the agent thread.
		 */
		public AgentInvocation(Object[] args)
		{
			this.args = args;
			getInterpreter().invokeSynchronized(this);
		}
	}
	
	public boolean	isExternalThread()
	{
		boolean	ret	= false;	// Default for models during creation.
		if(getInterpreter()!=null)
		{
			ret	= getInterpreter().getComponentAdapter().isExternalThread();
		}
		return ret;
	}
}
