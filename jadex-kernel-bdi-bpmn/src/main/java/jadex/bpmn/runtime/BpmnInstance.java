package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.handler.DefaultActivityHandler;
import jadex.bpmn.runtime.handler.EventIntermediateMultipleActivityHandler;
import jadex.bpmn.runtime.handler.GatewayParallelActivityHandler;
import jadex.bpmn.runtime.handler.GatewayXORActivityHandler;
import jadex.bpmn.runtime.handler.SubProcessActivityHandler;
import jadex.bpmn.runtime.handler.TaskActivityHandler;
import jadex.bpmn.runtime.handler.basic.EventIntermediateTimerActivityHandler;
import jadex.bpmn.runtime.handler.basic.UserInteractionActivityHandler;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SReflect;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.IValueFetcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Representation of a running BPMN process.
 */
public class BpmnInstance	implements IProcessInstance
{
	//-------- static part --------
	
	/** The activity execution handlers (activity type -> handler). */
	public static final Map	DEFAULT_HANDLERS;
	
	static
	{
		Map	defhandlers	= new HashMap();
		
		defhandlers.put(MBpmnModel.TASK, new TaskActivityHandler());
		defhandlers.put(MBpmnModel.SUBPROCESS, new SubProcessActivityHandler());
		
		defhandlers.put(MBpmnModel.GATEWAY_PARALLEL, new GatewayParallelActivityHandler());
		defhandlers.put(MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE, new GatewayXORActivityHandler());

		defhandlers.put(MBpmnModel.EVENT_START_EMPTY, new DefaultActivityHandler());
		defhandlers.put(MBpmnModel.EVENT_END_EMPTY, new DefaultActivityHandler());
		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_ERROR, new DefaultActivityHandler());
		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_RULE, new UserInteractionActivityHandler());
		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_TIMER, new EventIntermediateTimerActivityHandler());
		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_MULTIPLE, new EventIntermediateMultipleActivityHandler());
		
		DEFAULT_HANDLERS	= Collections.unmodifiableMap(defhandlers);
	}
	
	//-------- attributes --------
	
	/** The activity handlers. */
	protected Map	handlers;

	/** The executor. */
	protected IBpmnExecutor	executor;

	/** The global value fetcher. */
	protected IValueFetcher	fetcher;

	/** The thread context. */
	protected ThreadContext	context;
	
	/** The external entries (i.e. runnables to execute external notifications during executeStep). */
	protected List	extentries;
	
	/** The execution history. */
	protected List history;
	
	/** The change listeners. */
	protected List listeners;
	
	/** The step number. */
	protected int stepnumber;
	
	/** The context variables. */
	protected Map	variables;
	
	//-------- constructors --------
	
	/**
	 *  Create a new BPMN process instance using default handler.
	 *  @param model	The BMPN process model.
	 */
	public BpmnInstance(MBpmnModel model)
	{
		this(model, DEFAULT_HANDLERS, null);
	}

	/**
	 *  Create a new BPMN process instance.
	 *  @param model	The BMPN process model.
	 *  @param handlers	The activity handlers.
	 */
	public BpmnInstance(MBpmnModel model, Map handlers, IValueFetcher fetcher)
	{
		this.handlers	= handlers;
		this.fetcher	= new BpmnInstanceFetcher(this, fetcher);
		this.context	= new ThreadContext(model);
		this.extentries	= new ArrayList();
		
//		setHistoryEnabled(true);
		
		// Initialize context variables.
		Set	vars	= model.getContextVariables();
		for(Iterator it=vars.iterator(); it.hasNext(); )
		{
			String	name	= (String)it.next();
			Object	value	= null;
			IParsedExpression	exp	= model.getContextVariableExpression(name);
			if(exp!=null)
			{
				value	= exp.getValue(fetcher);
			}
			if(variables==null)
				variables	= new HashMap();
			variables.put(name, value);
		}
		
		// Create initial thread(s). 
		List	startevents	= model.getStartActivities();
		for(int i=0; startevents!=null && i<startevents.size(); i++)
		{
			context.addThread(new ProcessThread((MActivity)startevents.get(i), context, this));
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Set the executor.
	 *  The executor will receive call backs, e.g. when the instance becomes ready again after waiting for an event.
	 *  @param executor	The executor.
	 */
	public void	setExecutor(IBpmnExecutor executor)
	{
		this.executor	= executor;
	}
	
	/**
	 *  Get the model of the BPMN process instance.
	 *  @return The model.
	 */
	public MBpmnModel	getModelElement()
	{
		return (MBpmnModel)context.getModelElement();
	}
	
	/**
	 *  Get the thread context.
	 *  @return The thread context.
	 */
	public ThreadContext getThreadContext()
	{
		return context;
	}
	
	/**
	 *  Check, if the process has terminated.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 *  @return True, when the process instance is finished with regards to the specified pool/lane. When both pool and lane are null, true is returned only when all pools/lanes are finished.
	 */
	public boolean isFinished(String pool, String lane)
	{
		return context.isFinished(pool, lane);
	}

	/**
	 *  Execute one step of the process.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 */
	public void executeStep(String pool, String lane)
	{
		if(isFinished(pool, lane))
			throw new UnsupportedOperationException("Cannot execute a finished process: "+this);
		
		if(!isReady(pool, lane))
			throw new UnsupportedOperationException("Cannot execute a process with only waiting threads: "+this);
		
		// Todo: execute only external entries belonging to pool/lane
		Runnable[]	exta	= null;
		synchronized(extentries)
		{
			if(!extentries.isEmpty())
			{
				exta	= (Runnable[])extentries.toArray(new Runnable[extentries.size()]);
				extentries.clear();
			}
		}
		if(exta!=null)
		{
			for(int i=0; i<exta.length; i++)
				exta[i].run();
		}
		
		ProcessThread	thread	= context.getExecutableThread(pool, lane);
		
		// Thread may be null when external entry has not changed waiting state of any active plan. 
		if(thread!=null)
		{
			// Update parameters based on edge inscriptions and initial values.
			thread.updateParameters(this);
			
			// Find handler and execute activity.
			IActivityHandler handler = (IActivityHandler)handlers.get(thread.getActivity().getActivityType());
			if(handler==null)
				throw new UnsupportedOperationException("No handler for activity: "+thread);
			handler.execute(thread.getActivity(), this, thread);
			if(history!=null)
				history.add(new HistoryEntry(stepnumber++, thread.getId(), thread.getActivity()));
			
			notifyListeners(new ChangeEvent(this, "step_executed"));
		}
	}

	/**
	 *  Check if the process is ready, i.e. if at least one process thread can currently execute a step.
	 *  @param pool	The pool to be executed or null for any.
	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
	 */
	public boolean	isReady(String pool, String lane)
	{
		boolean	ready;
		// Todo: consider only external entries belonging to pool/lane
		synchronized(extentries)
		{
			ready	= !extentries.isEmpty();
		}
		ready	= ready || context.getExecutableThread(pool, lane)!=null;
		return ready;
	}
	
	/**
	 *  Add an external entry to be invoked during the next executeStep.
	 *  This method may be called from external threads.
	 *  @param code	The external code. 
	 */
	public void	invokeLater(Runnable code)
	{
		synchronized(extentries)
		{
			extentries.add(code);
		}
		if(executor!=null)
			executor.wakeUp();
	}
	
	/**
	 *  Get the activity handler for an activity.
	 *  @param actvity The activity.
	 *  @return The activity handler.
	 */
	public IActivityHandler getActivityHandler(MActivity activity)
	{
		return (IActivityHandler)handlers.get(activity.getActivityType());
	}

	/**
	 *  Get the global value fetcher.
	 *  @return The value fetcher (if any).
	 */
	public IValueFetcher getValueFetcher()
	{
		return this.fetcher;
	}
	
	/**
	 *  Get the history mode.
	 */
	public boolean isHistoryEnabled()
	{
		return history!=null;
	}
	
	/**
	 *  Get the history.
	 *  @return The history.
	 */
	public List getHistory()
	{
		return this.history;
	}

	/**
	 *  Set the history mode.
	 */
	public void	setHistoryEnabled(boolean enabled)
	{
		// Hack!!! synchronized because of ProcessViewPanel.
		if(enabled && history==null)
			history	= Collections.synchronizedList(new ArrayList());
		else if(!enabled && history!=null)
			history	= null;
	}
	
	/**
	 *  Add a change listener.
	 *  @param listener The listener.
	 */
	public void addChangeListener(IChangeListener listener)
	{
		if(listeners==null)
			listeners = new ArrayList();
		listeners.add(listener);
	}
	
	/**
	 *  Remove a change listener.
	 *  @param listener The listener.
	 */
	public void removeChangeListener(IChangeListener listener)
	{
		if(listeners!=null)
			listeners.remove(listener);
	}
	
	/**
	 *  Notify the change listeners.
	 */
	public void notifyListeners(ChangeEvent event)
	{
		if(listeners!=null)
		{
			for(int i=0; i<listeners.size(); i++)
			{
				((IChangeListener)listeners.get(i)).changeOccurred(event);
			}
		}
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(SReflect.getInnerClassName(this.getClass()));
		buf.append("(name=");
		buf.append(getModelElement().getName());
		buf.append(", context=");
		buf.append(context);
		buf.append(")");
		return buf.toString();
	}

	/**
	 *  Test if the given context variable is declared.
	 *  @param name	The variable name.
	 *  @return True, if the variable is declared.
	 */
	public boolean hasContextVariable(String name)
	{
		return variables!=null && variables.containsKey(name);
	}
	
	/**
	 *  Get the value of the given context variable.
	 *  @param name	The variable name.
	 *  @return The variable value.
	 */
	public Object getContextVariable(String name)
	{
		if(variables!=null && variables.containsKey(name))
		{
			return variables.get(name);			
		}
		else
		{
			throw new RuntimeException("Undeclared context variable: "+name+", "+this);
		}
	}
	
	/**
	 *  Set the value of the given context variable.
	 *  @param name	The variable name.
	 *  @param value	The variable value.
	 */
	public void setContextVariable(String name, Object value)
	{
		if(variables!=null && variables.containsKey(name))
		{
			variables.put(name, value);			
		}
		else
		{
			throw new RuntimeException("Undeclared context variable: "+name+", "+this);
		}
	}
}

