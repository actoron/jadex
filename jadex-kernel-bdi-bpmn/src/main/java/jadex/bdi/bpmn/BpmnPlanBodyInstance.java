package jadex.bdi.bpmn;

import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IClockService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  A BPMN instance that is executed as a plan body.
 */
public class BpmnPlanBodyInstance extends BpmnInstance
{
	//-------- static part --------
	
	/** The activity execution handlers (activity type -> handler). */
	public static final Map	DEFAULT_HANDLERS;
	
	static
	{
		Map	defhandlers	= new HashMap(BpmnInstance.DEFAULT_HANDLERS);
		defhandlers.put("EventIntermediateTimer", new EventIntermediateTimerActivityHandler());
		defhandlers.put("EventIntermediateMessage", new EventIntermediateMessageActivityHandler());
		DEFAULT_HANDLERS	= Collections.unmodifiableMap(defhandlers);
	}
	
	//-------- attributes --------
	
	/** The bdi interpreter. */
	protected BDIInterpreter	interpreter;
	
	/** The last plan lifecycle state. */
	protected String	state;
	
	/** The wait times of waiting threads (thread -> absolute timepoint). */
	protected Map	waittimes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new BPMN process instance using default handler.
	 *  @param model	The BMPN process model.
	 */
	public BpmnPlanBodyInstance(MBpmnModel model, BDIInterpreter interpreter)
	{
		super(model, DEFAULT_HANDLERS);
		this.interpreter	= interpreter;
	}
	
	//-------- methods --------

	/**
	 *  Get the last plan lifecycle state.
	 *  @return The plan lifecycle state.
	 */
	public String	getLastState()
	{
		return state;
	}
	
	/**
	 *  Set the plan lifecycle state.
	 *  @param state	The plan lifecycle state.
	 */
	public void	setLastState(String state)
	{
		this.state	= state;
	}
	
	/**
	 *  Add a timer for a thread.
	 *  @param thread	The process thread that should wait.
	 *  @param duration	The duration to wait for.
	 */
	public void	addTimer(ProcessThread thread, long duration)
	{
		assert duration>0;
		if(waittimes==null)
			waittimes	= new HashMap();

		IClockService	clock	= (IClockService)interpreter.getAgentAdapter().getPlatform().getService(IClockService.class);
		waittimes.put(thread, new Long(clock.getTime()+duration));
	}
	
	/**
	 *  Update all timers that have become due.
	 */
	public void	updateTimers()
	{
		if(waittimes!=null)
		{
			IClockService	clock	= (IClockService)interpreter.getAgentAdapter().getPlatform().getService(IClockService.class);
			
			for(Iterator it=waittimes.keySet().iterator(); it.hasNext(); )
			{
				ProcessThread	thread	= (ProcessThread)it.next();
				if(((Number)waittimes.get(thread)).longValue()<=clock.getTime())
				{
					it.remove();
					assert thread.isWaiting();
					
					// Hack!!! How to call handler!?
					new EventIntermediateTimerActivityHandler().notify(thread.getModelElement(), this, thread, context.getThreadContext(thread));
				}
			}
		}
	}

	/**
	 *  Get the current timeout of the process, i.e. the
	 *  remaining time of the closest due intermediate timer event. 
	 *  @return The current timeout or -1 for no timeout.
	 */
	public long getTimeout()
	{
		long	mindur	= -1;
		if(waittimes!=null)
		{
			IClockService	clock	= (IClockService)interpreter.getAgentAdapter().getPlatform().getService(IClockService.class);
			for(Iterator it=waittimes.values().iterator(); it.hasNext(); )
			{
				long	time	= Math.max(((Number)it.next()).longValue()-clock.getTime(), 0);
				mindur	= mindur==-1 ? time : time<mindur ? time : mindur; 
			}
		}
		return mindur;
	}
	
	/**
	 *  Get the thread context of a thread.
	 */
}
