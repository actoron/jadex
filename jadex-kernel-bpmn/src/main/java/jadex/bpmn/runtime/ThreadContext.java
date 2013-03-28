package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MIdElement;
import jadex.bpmn.model.MSubProcess;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.commons.SReflect;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
	
	/** The currently running threads (thread -> context or null, if leaf thread). */
	protected Map<ProcessThread, ThreadContext>	threads;
	
	//-------- constructors --------
	
	/**
	 *  Create a new top-level thread context.
	 *  @param model	The process model element.
	 */
	public ThreadContext(MBpmnModel model)
	{
		this.model	= model;
		this.initiator	= null;
	}
	
	/**
	 *  Create a new sub-level thread context.
	 *  Should not be invoked from the outside
	 *  @param model	The sub process model element.
	 *  @param initiator	The initiating thread.
	 */
	public ThreadContext(MSubProcess model, ProcessThread initiator)
	{
		this.model	= model;
		this.initiator	= initiator;
	}

	//-------- methods --------
	
	/**
	 *  Get the model element.
	 *  @return	The process or sub process element.
	 */
	public MIdElement getModelElement()
	{
		return model;
	}
	
	/**
	 *  Get the parent context.
	 *  @return	The parent context, if any.
	 */
	public ThreadContext getParent()
	{
		return initiator!=null ? initiator.getThreadContext() : null;
	}
	
	/**
	 *  Get the initiating thread.
	 *  @return	The initiator, if any.
	 */
	public ProcessThread getInitiator()
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
			threads	= new LinkedHashMap<ProcessThread, ThreadContext>();
		
		threads.put(thread, null);
		
		thread.getInstance().publishEvent(thread.getInstance().createThreadEvent(IMonitoringEvent.EVENT_TYPE_CREATION, thread));
//		System.out.println("add: "+thread);
	}
	
	/**
	 *  Add an external thread to this context.
	 *  @param thread	The thread to be added.
	 */
	// Hack!!! Make external threads execute before others.
	public void	addExternalThread(ProcessThread thread)
	{
		Map<ProcessThread, ThreadContext>	oldthreads	= threads;
		threads	= new LinkedHashMap<ProcessThread, ThreadContext>();		
		threads.put(thread, null);
		if(oldthreads!=null)
			threads.putAll(oldthreads);
		
		thread.getInstance().publishEvent(thread.getInstance().createThreadEvent(IMonitoringEvent.EVENT_TYPE_CREATION, thread));
//		System.out.println("add: "+thread);
	}
	
	/**
	 *  Remove a thread from this context.
	 *  @param thread	The thread to be removed.
	 */
	public void removeThread(ProcessThread thread)
	{
		if(threads!=null)
		{
			if(getSubcontext(thread)!=null)
				removeSubcontext(getSubcontext(thread));
			
			// Cancel activity (e.g. timer).
			MActivity	act	= thread.getActivity();
			if(act!=null && thread.isWaiting())
				thread.getInstance().getActivityHandler(act).cancel(act, thread.getInstance(), thread);
			
			boolean rem = threads.containsKey(thread);
			threads.remove(thread);
			thread.setThreadContext(null);
			
			if(threads.isEmpty())
				threads	= null;
		
			if(rem)
			{
//				System.out.println("remove1: "+thread);
				BpmnInterpreter in = thread.getInstance();
				in.publishEvent(in.createThreadEvent(IMonitoringEvent.EVENT_TYPE_DISPOSAL, thread));
			}
		}
		
//		System.out.println("remove0: "+thread);
	}
	
	/**
	 *  Get all threads of this context.
	 *  @return All threads of this context, but not from sub context.
	 */
	public Set<ProcessThread>	getThreads()
	{
		return threads!=null ? threads.keySet() : null;
	}
	
	/**
	 *  Get all threads of the context and all subcontexts.
	 */
	public Set<ProcessThread> getAllThreads()
	{
		Set<ProcessThread> ret = new HashSet<ProcessThread>();
		if(threads!=null)
		{
			for(Iterator<ProcessThread> it=threads.keySet().iterator(); it.hasNext(); )
			{
				ProcessThread pc = it.next();
				ret.add(pc);
				ThreadContext tc = (ThreadContext)threads.get(pc);
				if(tc!=null)
					ret.addAll(tc.getAllThreads());
			}
		}
		return ret;
	}	
	
	/**
	 *  Get the sub context corresponding to a given thread.
	 *  @param thread	 The thread.
	 *  @return The corresponding thread context.
	 * /
	public ThreadContext	getThreadContext(ProcessThread thread)
	{
		List	contexts	= new ArrayList();
		contexts.add(this);
		ThreadContext	ret	= null;
		while(ret==null && !contexts.isEmpty())
		{
			ThreadContext	context	= (ThreadContext)contexts.remove(0);
			if(context.threads!=null && context.threads.containsKey(thread))
			{
				ret	= context;
			}
			else if(context.threads!=null)
			{
				for(Iterator it=context.threads.values().iterator(); it.hasNext(); )
				{
					Object	subcontext	= it.next();
					if(subcontext!=null)
						contexts.add(subcontext);
				}
			}
		}
		return ret;
	}*/
	
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
		
		Set<ProcessThread>	subthreads	= context.getThreads();
		if(subthreads!=null)
		{
			ProcessThread[] subt = (ProcessThread[])subthreads.toArray(new ProcessThread[subthreads.size()]);
			for(int i=0; i<subt.length; i++)
			{
				context.removeThread(subt[i]);
			}
		}

		threads.put(context.getInitiator(), null);
	}
	
	/**
	 *  Get the subcontext of a thread.
	 *  @param thread The thread which owns the subcontext.
	 *  @return	The subcontext (if any).
	 */
	public ThreadContext getSubcontext(ProcessThread thread)
	{
		return (ThreadContext)threads.get(thread);
	}
	
	/**
	 *  The context is finished, when there are no (more) threads to execute.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 *  @return True, when the process instance is finished with regards to the specified pool/lane. When both pool and lane are null, true is returned only when all pools/lanes are finished.
	 */
	public boolean	isFinished(String pool, String lane)
	{
		boolean	finished	= true;
		if(threads!=null && !threads.isEmpty())
		{
			for(Iterator<ProcessThread> it=threads.keySet().iterator(); finished && it.hasNext(); )
			{
				finished	= !it.next().belongsTo(pool, lane);
			}
		}
		return finished;
	}

	/**
	 *  Get an executable thread in the context or its sub contexts.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 *  @return	An executable thread (if any).
	 */
	public ProcessThread getExecutableThread(String pool, String lane)
	{
		// Iterate over all thread contexts and find executable thread
		ProcessThread	ret	= null;
		if(threads!=null)
		{
			for(Iterator<ProcessThread> it=threads.keySet().iterator(); ret==null && it.hasNext(); )
			{
				ProcessThread	thread	= (ProcessThread)it.next();
				if(threads.get(thread)!=null)
				{
					ThreadContext	context	= (ThreadContext)threads.get(thread);
					ret	= context.getExecutableThread(pool, lane);
				}
				else if(!thread.isWaiting() && thread.belongsTo(pool, lane))
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
