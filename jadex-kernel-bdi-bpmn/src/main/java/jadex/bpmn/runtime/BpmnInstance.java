package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnDiagram;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SReflect;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *  Representation of a running BPMN process.
 */
public class BpmnInstance
{
	//-------- static part --------
	
	/** The activity execution handlers (activity type -> handler). */
	public static final Map	DEFAULT_HANDLERS;
	
	static
	{
		Map	defhandlers	= new HashMap();
		defhandlers.put("EventStartEmpty", new DefaultActivityHandler());
		defhandlers.put("EventEndEmpty", new DefaultActivityHandler());
		defhandlers.put("Task", new DefaultActivityHandler());
		defhandlers.put("GatewayParallel", new GatewayParallelActivityHandler());
		DEFAULT_HANDLERS	= Collections.unmodifiableMap(defhandlers);
	}
	
	//-------- attributes --------
	
	/** The model. */
	protected MBpmnDiagram model;

	/** The activity handlers. */
	protected Map	handlers;

	/** The current threads. */
	protected Set threads;
	
	/** The change listeners. */
	protected List	listeners;
	
	//-------- constructors --------
	
	/**
	 *  Create a new BPMN process instance.
	 *  @param model	The BMPN process model.
	 *  @param handlers	The activity handlers.
	 */
	public BpmnInstance(MBpmnDiagram model, Map handlers)
	{
		this.model = model;
		this.handlers = handlers;
		this.threads = new LinkedHashSet();
		
		// Create initial thread(s). 
		List	startevents	= model.getStartEvents();
		for(int i=0; startevents!=null && i<startevents.size(); i++)
		{
			threads.add(new ProcessThread((MActivity) startevents.get(i)));
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Get the model of the BPMN process instance.
	 *  @return The model.
	 */
	public MBpmnDiagram	getModel()
	{
		return model;
	}
	
	/**
	 *  Get the currently executing control flows of the process instance.
	 *  @return A set of currently running threads.
	 */
	public Set	getThreads()
	{
		return threads;
	}
	
	/**
	 *  Check, if the process has terminated.
	 *  @return True, when the process instance is finished.
	 */
	public boolean isFinished()
	{
		return threads.isEmpty();
	}

	/**
	 *  Execute one step of the process.
	 */
	// Todo: What about diagrams with multiple pools/lanes etc?
	public void executeStep()
	{
		if(threads.isEmpty())
			throw new UnsupportedOperationException("No more threads to execute: "+this);
		
		boolean	executed	= false;
		for(Iterator it=threads.iterator(); !executed && it.hasNext(); )
		{
			final ProcessThread	thread	= (ProcessThread)it.next();
			if(!thread.isWaiting())
			{
				// Handle parameter passing in edge inscriptions.
				if(thread.getLastEdge()!=null && thread.getLastEdge().getName()!=null)
				{
					final Map	oldvalues	= thread.getContext(thread.getLastEdge().getSource().getName());
					StringTokenizer	stok	= new StringTokenizer(thread.getLastEdge().getName(), "\r\n");
					while(stok.hasMoreTokens())
					{
						String	stmt	= stok.nextToken();
						int	idx	= stmt.indexOf("=");
						if(idx!=-1)
						{
							String	name	= stmt.substring(0, idx).trim();
							String	exp	= stmt.substring(idx+1).trim();
							Object	val	= new JavaCCExpressionParser().parseExpression(exp, null, null, getClass().getClassLoader()).getValue(new IValueFetcher()
							{
								public Object fetchValue(String name, Object object)
								{
									if(object instanceof Map)
										return ((Map)object).get(name);
									else
										throw new UnsupportedOperationException();
								}
								
								public Object fetchValue(String name)
								{
									Object	value	= oldvalues!=null ? oldvalues.get(name) : null;
									if(value==null)
									{
										value	= thread.getContext(name);
									}
									return value;
								}
							});
							thread.setParameterValue(name, val);
						}
						else
						{
							System.err.println("Don't know what to do with edge inscription: "+stmt);
						}
					}
				}
				
				IActivityHandler	handler	= (IActivityHandler) handlers.get(thread.getNextActivity().getActivityType());
				if(handler==null)
					throw new UnsupportedOperationException("No handler for activity: "+thread);
				
				handler.execute(thread.getNextActivity(), this, thread);
				executed	= true;
			}
		}
	}
	
	/**
	 *  Check if the process is ready, i.e. if at least one process thread can currently execute a step.
	 */
	public boolean	isReady()
	{
		boolean	ready	= false;
		for(Iterator it=threads.iterator(); !ready && it.hasNext(); )
		{
			ProcessThread	thread	= (ProcessThread)it.next();
			ready	= !thread.isWaiting();
		}
		return ready;
	}
	
	//-------- listener handling --------
	
	/**
	 *  Add a change listener.
	 *  The listener is informed, whenever the ready state of the process becomes true.
	 */
	public void	addChangeListener(IChangeListener listener)
	{
		if(listeners==null)
			listeners	= new ArrayList();
		
		listeners.add(listener);
	}

	
	/**
	 *  Remove a change listener.
	 */
	public void	removeChangeListener(IChangeListener listener)
	{
		if(listeners!=null)
		{
			listeners.remove(listener);
			if(listeners.isEmpty())
				listeners	= null;
		}
	}
	
	/**
	 *  Wake up the instance.
	 *  Called from activity handlers when external events re-activate waiting process threads.
	 *  Propagated to change listeners.
	 */
	public void	wakeUp()
	{
		if(listeners!=null)
		{
			ChangeEvent	ce	= new ChangeEvent(this, "ready");	
			for(int i=0; i<listeners.size(); i++)
			{
				((IChangeListener)listeners.get(i)).changeOccurred(ce);
			}
		}
	}

	/**
	 *  Get the dispatched element, i.e. the element that caused
	 *  the current plan step being executed.
	 *  @return The dispatched element.
	 * /
	public IElement getDispatchedElement(); */
	
	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 * /
	public IWaitqueue getWaitqueue(); */

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(SReflect.getInnerClassName(this.getClass()));
		buf.append("(model=");
		buf.append(getModel());
		buf.append(", threads=");
		buf.append(getThreads());
		buf.append(")");
		return buf.toString();
	}

	//-------- parameter handling --------

	/**
	 *  Get all parameters.
	 *  @return All parameters.
	 * /
	public IParameter[]	getParameters(); */

	/**
	 *  Get all parameter sets.
	 *  @return All parameter sets.
	 * /
	public IParameterSet[]	getParameterSets(); */

	/**
	 *  Get a parameter.
	 *  @param name The name.
	 *  @return The parameter.
	 * /
	public IParameter getParameter(String name); */

	/**
	 *  Get a parameter.
	 *  @param name The name.
	 *  @return The parameter set.
	 * /
	public IParameterSet getParameterSet(String name); */

	/**
	 *  Has the element a parameter element.
	 *  @param name The name.
	 *  @return True, if it has the parameter.
	 * /
	public boolean hasParameter(String name); */

	/**
	 *  Has the element a parameter set element.
	 *  @param name The name.
	 *  @return True, if it has the parameter set.
	 * /
	public boolean hasParameterSet(String name); */
}
