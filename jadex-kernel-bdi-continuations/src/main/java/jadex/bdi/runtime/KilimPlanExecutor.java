package jadex.bdi.runtime;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.JavaStandardPlanExecutor.PlanExecutionTask;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.SReflect;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.ThreadPool;
import jadex.commons.concurrent.ThreadPoolFactory;
import jadex.rules.state.IOAVState;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import kilim.Pausable;

/**
 *  A plan executor for plans that run on their own thread
 *  and therefore may perform blocking wait operations.
 *  Plan bodies have to inherit from @link{Plan}.
 *  
 *  This implementation makes the {@link ThreadPool} transient and 
 *  therefore avoids problems in J2EE persistent agents.
 *  The {@link ThreadPool} is obtained by the {@link ThreadPoolFactory}
 *  using the platform name as identifier.
 *  
 */
// todo: move somewhere else (impl???).
public class KilimPlanExecutor	implements IPlanExecutor, Serializable
{
	//-------- constants --------
	
	/** debug this PlanExecutor */
	protected static final boolean DEBUG = true;

	public static final String MAX_PLANSTEP_TIME = "max_planstep_time";
	
	//-------- attributes --------

	/** The maximum execution time per plan step in millis. */
	protected Number	maxexetime;

	/** The pool for the planinstances -> execution tasks. */
	protected Map	tasks;
	
	/** The executing thread. */
	protected transient Thread thread;
	
	
	//-------- constructor --------

	/**
	 *  Create a new threadbased plan executor.
	 */
	public KilimPlanExecutor()
	{
		this.tasks = Collections.synchronizedMap(SCollection.createHashMap());
//		this.continuations = Collections.synchronizedMap(SCollection.createHashMap());
	}

	//-------- Simple debug Infos ----------
	
	/** private simple debug method - use logger?*/
	protected static void debug(Object caller, String msg, KilimPlanExecutionTask task)
	{
		if(DEBUG)
		{
			final String line = "-----------------------------------------";
			
			StringBuffer b = new StringBuffer();
			b.append(line + "\n");
			b.append(caller + ":" + "\n");
			b.append("\t" + "Message: " + "\t" + msg + "\n");
			b.append("\t" + "Task: " + "\t" + task + "\n");
			System.out.println(b.toString());
			
			b = null;
		}
	}

	//-------- IPlanExecutor interface --------

	/**
	 *  Create the body of a plan.
	 *  @param rplan The rplan.
	 *  @return	The created body.
	 *  May throw any kind of exception, when the body creation fails
	 */
	public Object	createPlanBody(BDIInterpreter interpreter, Object rcapability, Object rplan) throws Exception
	{
		// Create plan body object.
		// Hack!!! Not an elegant way by using a static hashtable!
		// Needed for passing the rplan to the abstract plan instance.
		String refname= ""+Thread.currentThread()+"_"+Thread.currentThread().hashCode();
		AbstractPlan.planinit.put(refname, new Object[]{interpreter, rplan, rcapability});

		IOAVState state = interpreter.getState();
		Object	mplan	= state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
		Object	mbody	= state.getAttributeValue(mplan, OAVBDIMetaModel.plan_has_body);
//		Class	clazz	= (Class)interpreter.getState().getAttributeValue(mbody, OAVBDIMetaModel.body_has_impl);
		String clname = (String)state.getAttributeValue(mbody, OAVBDIMetaModel.body_has_impl);
		if(clname==null)
			throw new RuntimeException("Classname must not be null: "+state.getAttributeValue(state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
		Class clazz = SReflect.findClass(clname, OAVBDIMetaModel.getImports(interpreter.getState(), interpreter.getState().getAttributeValue
			(rcapability, OAVBDIRuntimeModel.element_has_model)), state.getTypeModel().getClassLoader());
		
		Object	body = null;
		if(clazz!=null)
		{
			try
			{
				body = clazz.newInstance();
				if(!(body instanceof Plan)) // FlowPlan
					throw new RuntimeException("User plan has wrong baseclass. Expected jadex.bdi.runtime.Plan for continuable plan.");
				interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body, body);
			}
			catch(Exception e)
			{
				// Use only RuntimeException from below
				e.printStackTrace();
			}
		}

		AbstractPlan.planinit.remove(refname);

		if(body==null)
			throw new RuntimeException("Plan body could not be created: "+clazz);

		return body;
	}

	/**
	 *  Execute a step of a plan.
	 *  Executing a step should cause the latest event to be handled.
	 *  Will be called by the scheduler for every event to be handled.
	 *  May throw any kind of exception, when the plan execution fails
	 *  @return True, if the plan step was interrupted (interrupted flag).
	 */
	public boolean	executeStep(BDIInterpreter interpreter, Object rcapability, Object rplan, String steptype)	throws Exception
	{
		debug(this, "Method: executeStep() - START", null);
		
		// Save the execution thread for this task. - need this?
		this.thread = Thread.currentThread();
		
		// Get or create new a continuation for the plan instance info.
		KilimPlanExecutionTask task = (KilimPlanExecutionTask)tasks.get(rplan);
		if(task==null)
		{
			task = new KilimPlanExecutionTask(this, interpreter, rcapability, rplan);
			tasks.put(rplan, task);
		}

		task.setStepType(steptype);
		task.setState(KilimPlanExecutionTask.STATE_RUNNING);
		task.setFlowPlanExecutor(this);
		//task.setThread(Thread.currentThread());
		
		// execute the continuation
		try
		{
			debug(this, "Method: executeStep() - continue a contiunation", task);
			
			// Start (or resume) the excution of the task
			task.executeStep();
			
			debug(this, "Method: executeStep() - continuation finished/suspended", task);
		}
		catch(Throwable e)
		{
			// Shouldn't happen (Occurs only when something goes wrong with the continuation)
			System.err.println("Warning, continuation aborted with Throwable: " + e.getClass().getName());
			e.printStackTrace(System.err);
		}
		
		if(task.getThrowable() instanceof Exception)
    		throw (Exception)task.getThrowable();
    	else if(task.getThrowable()!=null)
    		throw new RuntimeException(task.getThrowable());

		debug(this, "Method: executeStep() - RETURN", null);

		// reset thread - need this?
		this.thread = null;
		
		return task.getState().equals(KilimPlanExecutionTask.STATE_INTERRUPTED);
	}

	/**
	 *  Execute a step of a plan.
	 *  Executing a step should cause the latest event to be handled.
	 *  
	 *  Will be called by the scheduler for every event to be handled.
	 *  May throw any kind of exception, when the plan execution fails
	 *  @return True, if plan was interrupted (micro plan step).
	 */
	public boolean	executePlanStep(BDIInterpreter interpreter, Object rcapability, Object rplan)	throws Exception
	{
		return executeStep(interpreter, rcapability, rplan, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY);
	}

	/**
	 *  Execute a step of the plans passed() code.
	 *  This method is called, after the plan has finished
	 *  successfully (i.e. without exception).
	 *  
	 *  Will be called by the scheduler for the first time and 
	 *  every subsequent event to be handled.
	 *  May throw any kind of exception, when the execution fails
	 *  @return True, if execution was interrupted (micro plan step).
	 */
	public boolean	executePassedStep(BDIInterpreter interpreter, Object rplan)	throws Exception
	{
		return executeStep(interpreter, null, rplan, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_PASSED);
	}

	/**
	 *  Execute a step of the plans failed() code.
	 *  This method is called, when the plan has failed
	 *  (i.e. due to an exception occurring in the plan body).
	 *  
	 *  Will be called by the scheduler for the first time and 
	 *  every subsequent event to be handled.
	 *  May throw any kind of exception, when the execution fails
	 *  @return True, if execution was interrupted (micro plan step).
	 */
	public boolean	executeFailedStep(BDIInterpreter interpreter, Object rplan)	throws Exception
	{
		return executeStep(interpreter, null, rplan, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED);		
	}

	/**
	 *  Execute a step of the plans aborted() code.
	 *  This method is called, when the plan is terminated
	 *  from the outside (e.g. when the corresponding goal is
	 *  dropped or the context condition of the plan becomes invalid)
	 *  
	 *  Will be called by the scheduler for the first time and 
	 *  every subsequent event to be handled.
	 *  May throw any kind of exception, when the execution fails
	 *  @return True, if execution was interrupted (micro plan step).
	 */
	public boolean	executeAbortedStep(BDIInterpreter interpreter, Object rplan)	throws Exception
	{
		boolean	micro	= false;
		if(tasks.containsKey(rplan))
			micro	= executeStep(interpreter, null, rplan, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED);
		return micro;
	}

	/**
	 *  Interrupt a plan step during execution.
	 *  The plan is requested to stop the execution to allow
	 *  consequences of performed plan actions (like belief changes)
	 *  taking place. If the method is not implemented the
	 *  plan step will be NOT be interrupted.
	 */
	public void	interruptPlanStep(Object rplan) throws Pausable
	{
		// TODO: check use, implement continuable version!
		
		KilimPlanExecutionTask task = (KilimPlanExecutionTask)tasks.get(rplan);
		assert task!=null;
//		assert task.getExecutionThread()==Thread.currentThread() : rplan+", "+Thread.currentThread();
		try
		{
			task.giveBackControl(KilimPlanExecutionTask.STATE_INTERRUPTED, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 *  Called on termination of a plan.
	 *  Free all associated ressources, stop threads, etc.
	 */
	public void cleanup(BDIInterpreter interpreter, Object rplan)
	{
		// Save the execution thread for this task. - need this?
		this.thread = Thread.currentThread();
		
		debug(this, "Method: cleanup() - START", null);
		
		KilimPlanExecutionTask task = (KilimPlanExecutionTask)tasks.get(rplan);
		if(task!=null)
		{
			
			task.setState(KilimPlanExecutionTask.STATE_RUNNING);
			task.setTerminate(true);
			task.setFlowPlanExecutor(this);
			
			try
			{
				debug(this, "Method: cleanup() - Running cleanup for task", task);
				
				task.executeStep();
				
				debug(this, "Method: cleanup() - Cleanup for task finished", task);
			}
			catch(Throwable e)
			{
				// Shouldn't happen (Occurs only when something goes wrong with the continuation)
				System.err.println("Warning, continuation aborted with Throwable: " + e.getClass().getName());
				e.printStackTrace(System.err);
			}

			// remove references from maps
			tasks.remove(rplan);
			
			// reset thread
			this.thread = null;
			
		}
	}

	/**
	 *  Get the executing tasks of a plan.
	 *  @param rplan The plan.
	 *  @return The executing task.
	 */
	public Object getExecutionTask(Object rplan)
	{
		KilimPlanExecutionTask task = (KilimPlanExecutionTask)tasks.get(rplan);
		return task==null ? null : task;
	}
	
	/**
	 *  Called from a plan.
	 *  Registers the plan to wait for a event.
	 *  Returns the 
	 */
	public void	eventWaitFor(BDIInterpreter interpreter, Object rplan) throws Pausable
	{
		// moved to "FlowPlanRules.doWait()"
		
		if(interpreter.isAtomic())
			throw new RuntimeException("WaitFor not allowed in atomic block.");

		Object	rbody	= interpreter.getState().getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body);
		if(rbody==null)
			throw new RuntimeException("Plan body nulls. waitFor() calls from plan constructors not allowed.");


		boolean failure = false;
		KilimPlanExecutionTask task = (KilimPlanExecutionTask)tasks.get(rplan);
     	if(task.getExecutionThread()==Thread.currentThread())
		{
     		// Transfer execution to agent thread and wait until the plan is scheduled again.
			try
			{
				task.giveBackControl(KilimPlanExecutionTask.STATE_WAITING, OAVBDIRuntimeModel.PLANPROCESSINGTATE_WAITING);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			failure = true;
		}

		if(failure)
			throw new RuntimeException("ThreadedWaitFor error, not plan thread: "+Thread.currentThread());

	}

	/**
	 *  Get the maximum execution time.
	 *  0 indicates no maximum execution time.
	 *  @return The max execution time.
	 */
	protected long getMaxExecutionTime()
	{
		return 0;
	}

	/**
	 *  Get the monitor of a plan.
	 *  @return The monitor.
	 */
	public Object getMonitor(Object rplan)
	{
		return null;
	}
	
}
