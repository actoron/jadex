package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnDiagram;
import jadex.commons.SReflect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Representation of a running BPMN process.
 */
public class BpmnInstance
{
	//-------- static part --------
	
	/** The activity execution handlers (activity type -> handler). */
	protected static final Map	HANDLERS	= new HashMap();
	
	static
	{
		HANDLERS.put("EventStartEmpty", new DefaultActivityHandler());
		HANDLERS.put("EventEndEmpty", new DefaultActivityHandler());
		HANDLERS.put("Task", new DefaultActivityHandler());
		HANDLERS.put("GatewayParallel", new GatewayParallelActivityHandler());
	}
	
	//-------- attributes --------
	
	/** The model. */
	protected MBpmnDiagram model;

	/** The current threads. */
	protected Set threads;
	
	//-------- constructors --------
	
	/**
	 *  Create a new BPMN process instance.
	 *  @param model	The BMPN process model.
	 */
	public BpmnInstance(MBpmnDiagram model)
	{
		this.model = model;
//		this.threads = new LinkedHashSet();
		this.threads = new HashSet();
		
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
			ProcessThread	thread	= (ProcessThread)it.next();
			if(!thread.isWaiting())
			{
				IActivityHandler	handler	= (IActivityHandler) HANDLERS.get(thread.getNextActivity().getActivityType());
				if(handler==null)
					throw new UnsupportedOperationException("No handler for activity: "+thread);
				
				handler.execute(thread.getNextActivity(), this, thread);
				executed	= true;
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
