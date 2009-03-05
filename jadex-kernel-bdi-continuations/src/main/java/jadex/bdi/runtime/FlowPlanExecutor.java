package jadex.bdi.runtime;

import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.ThreadPool;
import jadex.commons.concurrent.ThreadPoolFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.javaflow.Continuation;

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
public class FlowPlanExecutor	implements IPlanExecutor, Serializable
{
	//-------- constants --------
	
	/** debug this PlanExecutor */
	protected static final boolean DEBUG = false;

	public static final String MAX_PLANSTEP_TIME = "max_planstep_time";
	
	//-------- attributes --------

	/** The maximum execution time per plan step in millis. */
	protected Number	maxexetime;

	/** The pool for the planinstances -> execution tasks. */
	protected Map	tasks;
	
	/** The pool for the tasks -> continuations. */
	protected Map	continuations;
	
	/** The executing thread. */
	protected transient Thread thread;
	
	
	//-------- constructor --------

	/**
	 *  Create a new threadbased plan executor.
	 */
	public FlowPlanExecutor()
	{
		this.tasks = Collections.synchronizedMap(SCollection.createHashMap());
		this.continuations = Collections.synchronizedMap(SCollection.createHashMap());
	}

	//-------- Simple debug Infos ----------
	
	/** private simple debug method - use logger?*/
	protected static void debug(Object caller, String msg, FlowPlanExecutionTask task, Continuation cont)
	{
		if (DEBUG)
		{
			final String line = "-----------------------------------------";
			
			StringBuffer b = new StringBuffer();
			b.append(line + "\n");
			b.append(caller + ":" + "\n");
			b.append("\t" + "Message: " + "\t" + msg + "\n");
			b.append("\t" + "Task: " + "\t" + task + "\n");
			if (null != task && task.getExecutionThread() != null)
			{
				b.append("\t\t" + "Task-Thread: " + "\t" + task.getExecutionThread().toString() + "\n");
			}
			b.append("\t" + "Continuation: " + "\t" + cont + "\n");
			if (null != cont)
			{
				b.append("\t\t" + "Continuation: " + "\t" + cont.hashCode() + "\n");
			}
			
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

		Object	mplan	= interpreter.getState().getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
		Object	mbody	= interpreter.getState().getAttributeValue(mplan, OAVBDIMetaModel.plan_has_body);
		Class	clazz	= (Class)interpreter.getState().getAttributeValue(mbody, OAVBDIMetaModel.body_has_class);
		
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
			catch (VerifyError ve)
			{
				System.out.println("JVM verification of continuable class failed!");
				ve.printStackTrace();
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
		debug(this, "Method: executeStep() - START", null, null);
		
		// Save the execution thread for this task. - need this?
		this.thread = Thread.currentThread();
		
		// Get or create new a continuation for the plan instance info.
		boolean newcontinuation = false;
		FlowPlanExecutionTask task = (FlowPlanExecutionTask)tasks.get(rplan);
		if(task==null)
		{
			task = new FlowPlanExecutionTask(this, interpreter, rcapability, rplan);
			tasks.put(rplan, task);
			newcontinuation = true;
		}

		task.setStepType(steptype);
		task.setState(FlowPlanExecutionTask.STATE_RUNNING);
		task.setFlowPlanExecutor(this);
		//task.setThread(Thread.currentThread());
		Continuation cont = null;
		
		if(newcontinuation)
		{
			cont = Continuation.startSuspendedWith(task);
			continuations.put(task, cont);
			debug(this, "Method: executeStep() - created new suspended contiunation", task, cont);
		}
		else
		{
			cont = (Continuation)continuations.get(task);
			debug(this, "Method: executeStep() - prepare to continue a contiunation", task,cont);
		}

		// execute the continuation
		try
		{
			debug(this, "Method: executeStep() - continue a contiunation", task, cont);
			
			// Start (or resume) the excution of the task
			Continuation c = Continuation.continueWith(cont);
			cont = c;
			
			debug(this, "Method: executeStep() - continuation finished/suspended", task, cont);
			
		}
		catch(Throwable e)
		{
			// Shouldn't happen (Occurs only when something goes wrong with the continuation)
			System.err.println("Warning, continuation aborted with Throwable: " + e.getClass().getName());
			e.printStackTrace(System.err);
		}
		
		// save continuation after executing the task
		// cont == null only if plan execution finished
		if (cont != null)
		{
			debug(this, "Method: executeStep() - saving new continuation for task", task, cont);
			
			// update map with new continuation
			continuations.put(task, cont); 
		}
		
		if(task.getThrowable() instanceof Exception)
    		throw (Exception)task.getThrowable();
    	else if(task.getThrowable()!=null)
    		throw new RuntimeException(task.getThrowable());

		debug(this, "Method: executeStep() - RETURN", null, null);

		// reset thread - need this?
		this.thread = null;
		
		return task.getState().equals(FlowPlanExecutionTask.STATE_INTERRUPTED);
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
	public void	interruptPlanStep(Object rplan)
	{
		// TODO: check use, implement continuable version!
		
		FlowPlanExecutionTask task = (FlowPlanExecutionTask)tasks.get(rplan);
		assert task!=null;
//		assert task.getExecutionThread()==Thread.currentThread() : rplan+", "+Thread.currentThread();
		task.giveBackControl(FlowPlanExecutionTask.STATE_INTERRUPTED, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
	}

	/**
	 *  Called on termination of a plan.
	 *  Free all associated ressources, stop threads, etc.
	 */
	public void cleanup(Object rplan)
	{
		// Save the execution thread for this task. - need this?
		this.thread = Thread.currentThread();
		
		debug(this, "Method: cleanup() - START", null, null);
		
		FlowPlanExecutionTask task = (FlowPlanExecutionTask)tasks.get(rplan);
		if(task!=null)
		{
			
			task.setState(FlowPlanExecutionTask.STATE_RUNNING);
			task.setTerminate(true);
			task.setFlowPlanExecutor(this);
			
			Continuation cont = null;

			try
			{
				cont = (Continuation)continuations.get(task);
				if (null != cont)
				{
					debug(this, "Method: cleanup() - Running cleanup for task", task, cont);
					
					cont = Continuation.continueWith(cont);
					// assert cont == null ?
					
					// TODO: implement maxExecutionTime
					//if(getMaxExecutionTime()==0)
					//	monitor.wait();
					//else
					//	monitor.wait(getMaxExecutionTime());
					
					debug(this, "Method: cleanup() - Cleanup for task finished", task, cont);
				}
				// print warning when no continuation was resumed?
				
			}
			catch(Throwable e)
			{
				// Shouldn't happen (Occurs only when something goes wrong with the continuation)
				System.err.println("Warning, continuation aborted with Throwable: " + e.getClass().getName());
				e.printStackTrace(System.err);
			}

			if(FlowPlanExecutionTask.STATE_RUNNING.equals(task.getState()))
			{
				// todo
				//task.getPlan().getRootGoal().fail(null);
				
				// Todo: wait for plan termination
				// (otherwise there are two threads running at once).
			}
			
			// remove references from maps
			continuations.remove(task);
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
		FlowPlanExecutionTask task =  (FlowPlanExecutionTask)tasks.get(rplan);
		return task==null ? null : task;
	}
	
	/**
	 *  Called from a plan.
	 *  Registers the plan to wait for a event.
	 *  Returns the 
	 */
	public void	eventWaitFor(BDIInterpreter interpreter, Object rplan)
	{
		
		//throw new RuntimeException("Unsupported operation! Cannot suspend a JavaStandardPlan");
		
		// moved to "FlowPlanRules.doWait()"
		
		if(interpreter.isAtomic())
			throw new RuntimeException("WaitFor not allowed in atomic block.");

		Object	rbody	= interpreter.getState().getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body);
		if(rbody==null)
			throw new RuntimeException("Plan body nulls. waitFor() calls from plan constructors not allowed.");


		boolean failure = false;
		FlowPlanExecutionTask task = (FlowPlanExecutionTask)tasks.get(rplan);
     	if(task.getExecutionThread()==Thread.currentThread())
		{
     		// Transfer execution to agent thread and wait until the plan is scheduled again.
			task.giveBackControl(FlowPlanExecutionTask.STATE_WAITING, OAVBDIRuntimeModel.PLANPROCESSINGTATE_WAITING);
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
		
		// todo:
		/*
		if(maxexetime==null)
		{
			maxexetime = (Number)agent.getPropertybase().getProperty(MAX_PLANSTEP_TIME);
			if(maxexetime==null)
				maxexetime = new Long(0);
		}
		return maxexetime.longValue();*/
	}

	
	
}
