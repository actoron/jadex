package jadex.bpmn.runtime;

import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MIdElement;
import jadex.bpmn.model.MSubProcess;
import jadex.commons.SReflect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *  The thread context represents the current execution context of a thread
 *  including sibling threads and parent contexts (for nested sub processes).
 */
public class ThreadContext
{
	//-------- attributes --------
	
	/** The model element (process or sub process). */
	protected MIdElement	model;
	
	/** The initiating thread (if any). */
	protected ProcessThread	initiator;
	
	/** The parent context (if any). */
	protected ThreadContext	parent;
	
	/** The currently running threads (thread -> context or null, if leaf thread). */
	protected Map	threads;
	
	//-------- constructors --------
	
	/**
	 *  Create a new top-level thread context.
	 *  @param model	The process model element.
	 */
	public ThreadContext(MBpmnModel model)
	{
		this.model	= model;
		this.initiator	= null;
		this.parent	= null;
	}
	
	/**
	 *  Create a new sub-level thread context.
	 *  Should not be invoked from the outside
	 *  @param model	The sub process model element.
	 *  @param initiator	The initiating thread.
	 *  @param parent	The parent context.
	 */
	public ThreadContext(MSubProcess model, ProcessThread initiator, ThreadContext parent)
	{
		this.model	= model;
		this.initiator	= initiator;
		this.parent	= parent;
	}

	//-------- methods --------
	
	/**
	 *  Get the model element.
	 *  @return	The process or sub process element.
	 */
	public MIdElement	getModelElement()
	{
		return model;
	}
	
	/**
	 *  Get the parent context.
	 *  @return	The parent context, if any.
	 */
	public ThreadContext	getParent()
	{
		return parent;
	}
	
	/**
	 *  Get the initiating thread.
	 *  @return	The initiator, if any.
	 */
	public ProcessThread	getInitiator()
	{
		return initiator;
	}
	
	/**
	 *  Add a thread to this context.
	 *  @param thread	The thread to be added.
	 */
	public void	addThread(ProcessThread thread)
	{
		if(threads==null)
			threads	= new HashMap();
		
		threads.put(thread, null);
	}
	
	/**
	 *  Remove a thread from this context.
	 *  @param thread	The thread to be removed.
	 */
	public void removeThread(ProcessThread thread)
	{
		if(threads!=null)
		{
			threads.remove(thread);
			
			if(threads.isEmpty())
				threads	= null;
		}
	}
	
	/**
	 *  Get all threads of this context.
	 *  @return All threads of this context, but not from sub context.
	 */
	public Set	getThreads()
	{
		return threads.keySet();
	}
	
	/**
	 *  Add a sub context.
	 *  @param context	The sub context to be added.
	 */
	public void addSubcontext(ThreadContext context)
	{
		assert threads!=null && threads.containsKey(context.getInitiator());

		threads.put(context.getInitiator(), context);
	}
	
	/**
	 *  Remove a sub context but keep the corresponding thread.
	 *  E.g. when a sub process terminates, the sub context is removed
	 *  and the initiating thread continues in the outer context.
	 *  @param context	The sub context to be removed.
	 */
	public void removeSubcontext(ThreadContext context)
	{
		assert threads!=null && threads.containsKey(context.getInitiator());

		threads.put(context.getInitiator(), null);
	}
	
	/**
	 *  The context is finished, when there are no (more) threads to execute.
	 *  @return True, if the context is finished.
	 */
	public boolean	isFinished()
	{
		return threads==null || threads.isEmpty();
	}
	
	/**
	 *  Get a subcontext (or the context itself) that directly contains an executable thread.
	 *  @return	A context with an executable thread (if any).
	 */
	public ThreadContext getExecutableContext()
	{
		ThreadContext	ret	= null;
		if(threads!=null)
		{
			for(Iterator it=threads.keySet().iterator(); ret==null && it.hasNext(); )
			{
				ProcessThread	thread	= (ProcessThread) it.next();
				if(threads.get(thread)!=null)
				{
					ThreadContext	context	= (ThreadContext) threads.get(thread);
					ret	= context.getExecutableContext();
				}
				else if(!thread.isWaiting())
				{
					ret	= this;
				}
			}
		}
		return ret;
	}

	/**
	 *  Get an executable thread that is contained directly in this context.
	 *  @return	An executable thread of this context (if any).
	 */
	public ProcessThread	getExecutableThread()
	{
		ProcessThread	ret	= null;
		if(threads!=null)
		{
			for(Iterator it=threads.keySet().iterator(); ret==null && it.hasNext(); )
			{
				ProcessThread	thread	= (ProcessThread) it.next();
				if(threads.get(thread)==null && !thread.isWaiting())
				{
					ret	= thread;
				}
			}
		}
		return ret;
	}
	

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(SReflect.getInnerClassName(this.getClass()));
		buf.append("(model=");
		buf.append(model);
		buf.append(", threads=");
		buf.append(threads);
		buf.append(")");
		return buf.toString();
	}
}
