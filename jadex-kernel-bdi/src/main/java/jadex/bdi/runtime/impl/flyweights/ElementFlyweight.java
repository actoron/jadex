package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.features.IBDIAgentFeature;
import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.features.impl.IInternalBDIAgentFeature;
import jadex.bdi.runtime.IElement;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.rules.state.IOAVState;

import java.util.Collection;

/**
 *  Flyweight for all runtime elements.
 */
public abstract class ElementFlyweight implements IElement
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
	private IInternalAccess interpreter;
	
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
	public ElementFlyweight(IOAVState state, Object scope, Object handle)
	{
		assert !(scope instanceof ElementFlyweight);
//		assert !BDIAgentFeature.getInterpreter(state).isExternalThread();
		assert BDIAgentFeature.getInternalAccess(state).getComponentFeature(IExecutionFeature.class).isComponentThread();
//		assert !BDIAgentFeature.getInterpreter(state).getComponentAdapter().isExternalThread();
//		if(handle==null && !(this instanceof ParameterFlyweight || this instanceof ParameterSetFlyweight))
//			Thread.dumpStack();
		assert handle!=null || this instanceof ParameterFlyweight || this instanceof ParameterSetFlyweight
			|| this instanceof ProcessableElementFlyweight: this;
		
		this.state = state;
		this.scope = scope;
		if(scope!=null)
			state.addExternalObjectUsage(scope, this);
		
		this.interpreter = BDIAgentFeature.getInternalAccess(state);
		if(interpreter==null)
			throw new RuntimeException("No interpreter");
		setHandle(handle);
	}
	
	//-------- element methods ---------

	/**
	 *  Get the name.
	 *  @return The name.
	 * /
	public String getName()
	{
		return (String)state.getAttributeValue(handle, OAVBDIRuntimeModel.element_has_name);
	}*/
	
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
		if(obj instanceof ElementFlyweight)
		{
			ElementFlyweight other = (ElementFlyweight)obj;
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
	public IInternalAccess getInterpreter()
	{
		return interpreter;
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
		return string+"("+handle+")";
		
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
	
	/**
	 *  Add an event listener.
	 *  @param listener The listener.
	 *  @param handle The handle.
	 */
	protected void addEventListener(Object listener, Object handle)
	{
		assert handle!=null;
		
		IOAVState	state	= getState();
		Object	scope	= getScope();
		
		addEventListener(listener, handle, state, scope);
		
//		System.out.println("addLis: "+getScope()+" "+listener+" "+handle+" "+getState().getAttributeValues(getScope(), OAVBDIRuntimeModel.capability_has_listeners));
	}

	/**
	 *  Add an event listener to the agent.
	 */
	public static void addEventListener(Object listener, Object handle, IOAVState state, Object scope)
	{
		Object le = state.getAttributeValue(scope, OAVBDIRuntimeModel.capability_has_listeners, listener);
		if(le==null)
		{
			le = state.createObject(OAVBDIRuntimeModel.listenerentry_type);
			state.setAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_listener, listener);
			state.setAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_scope, scope);
			state.addAttributeValue(scope, OAVBDIRuntimeModel.capability_has_listeners, le);
		}			
		state.addAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_relevants, handle);
		BDIAgentFeature.getInterpreter(state).getEventReificator().addObservedElement(handle);
	}
	
	/**
	 *  Remove an event listener.
	 *  @param listener The listener.
	 * @param handle The handle.
	 * @param failsafe Don't throw exception, when listener could not be removed (e.g. for finished goals).
	 */
	protected void removeEventListener(Object listener, Object handle, boolean failsafe)
	{
		assert handle!=null;
		
//		System.out.println("remLis: "+getScope()+" "+listener+" "+getState().getAttributeValues(getScope(), OAVBDIRuntimeModel.capability_has_listeners));
		IOAVState	state	= getState();
		Object	scope	= getScope();

		removeEventListener(listener, handle, failsafe, state, scope);
	}

	/**
	 *  Remove an event listener.
	 */
	public static void removeEventListener(Object listener, Object handle, boolean failsafe, IOAVState state, Object scope)
	{
		Object le = state.getAttributeValue(scope, OAVBDIRuntimeModel.capability_has_listeners, listener);
		if(le==null && !failsafe)
		{
			throw new RuntimeException("Listener not found: "+listener);
		}
		else if(le!=null)
		{
			Collection coll = state.getAttributeValues(le, OAVBDIRuntimeModel.listenerentry_has_relevants);
			if(!coll.contains(handle) && !failsafe)
			{
				throw new RuntimeException("Listener could not be removed properly: "+listener);
			}
			else if(coll.contains(handle))
			{
				state.removeAttributeValue(le, OAVBDIRuntimeModel.listenerentry_has_relevants, handle);
				coll = state.getAttributeValues(le, OAVBDIRuntimeModel.listenerentry_has_relevants);
				if(coll==null || coll.isEmpty())
					state.removeAttributeValue(scope, OAVBDIRuntimeModel.capability_has_listeners, listener);
				BDIAgentFeature.getInterpreter(state).getEventReificator().removeObservedElement(handle);
			}
		}
	}
	
	/**
	 *  Get the BDI agent feature.
	 */
	public IInternalBDIAgentFeature getBDIFeature()
	{
		return (IInternalBDIAgentFeature)getInterpreter().getComponentFeature(IBDIAgentFeature.class);
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
			getBDIFeature().invokeSynchronized(this);
		}
		
		/**
		 *  Create an action to be executed in sync with the agent thread.
		 */
		public AgentInvocation(Object[] args)
		{
			this.args = args;
			getBDIFeature().invokeSynchronized(this);
		}
	}

	/**
	 * 
	 */
	protected boolean isExternalThread()
	{
		return !BDIAgentFeature.getInternalAccess(state).getComponentFeature(IExecutionFeature.class).isComponentThread();
	}
	
}
