package jadex.bdi.runtime;

import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.interpreter.PlanRules;
import jadex.commons.SReflect;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.ThreadPool;
import jadex.commons.concurrent.ThreadPoolFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

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
 *  THIS IS ONLY A HACK - Thread management in JavaEE is not allowed for EnterpriseBeans
 *  and this class is used by PlatformBean ...
 *  TODO: implement a J2EEPlanExecutor as SessionBean with separate server ThreadPool?
 */
// todo: move somewhere else (impl???).
public class J2EEStandardPlanExecutor	implements IPlanExecutor, Serializable
{
	//-------- constants --------

	public static final String MAX_PLANSTEP_TIME = "max_planstep_time";
	
	//-------- attributes --------

	/** The bdi interpreter. */
	//protected BDIInterpreter interpreter;
	
	/** The maximum execution time per plan step in millis. */
	protected Number	maxexetime;

	/** The pool for the planinstances -> execution tasks. */
	protected Map	tasks;
	
	/** The threadpool. */
	// transient for EJB persistence
	protected transient IThreadPool threadpool;
	
	/** The identifier for the ThreadPool */
	protected String threadpoolIdentifier;

	//-------- constructor --------

	/**
	 *  Create a new threadbased plan executor.
	 */
	public J2EEStandardPlanExecutor(/*BDIInterpreter interpreter,*/ IThreadPool threadpool)
	{
		//this.interpreter = interpreter;
		this.threadpool = threadpool;
		this.tasks = Collections.synchronizedMap(SCollection.createHashMap());
	}
	
	/**
	 *  Create a new threadbased plan executor.
	 */
	public J2EEStandardPlanExecutor(String threadpoolIdentifier)
	{
		// todo: refactor to use threadpoolservice
		this(ThreadPoolFactory.getThreadPool(threadpoolIdentifier));
		this.threadpoolIdentifier = threadpoolIdentifier;
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
//		Class	clazz	= (Class)interpreter.getState().getAttributeValue(mbody, OAVBDIMetaModel.body_has_class);
		String clname = (String)interpreter.getState().getAttributeValue(mbody, OAVBDIMetaModel.body_has_impl);
		if(clname==null)
			throw new RuntimeException("Classname must not be null: "+interpreter.getState().getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model));
		Class clazz = SReflect.findClass(clname, OAVBDIMetaModel.getImports(interpreter.getState(), interpreter.getState().getAttributeValue
			(rcapability, OAVBDIRuntimeModel.element_has_model)), interpreter.getState().getTypeModel().getClassLoader());
	
		Object	body = null;
		if(clazz!=null)
		{
			try
			{
				body = clazz.newInstance();
				if(!(body instanceof Plan))
					throw new RuntimeException("User plan has wrong baseclass. Expected jadex.runtime.Plan for standard plan.");
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
		// Get or create new a thread for the plan instance info.
		boolean newthread = false;
		PlanExecutionTask task = (PlanExecutionTask)tasks.get(rplan);
		if(task==null)
		{
			task = new PlanExecutionTask(interpreter, rcapability, rplan);
			tasks.put(rplan, task);
			newthread = true;
		}
		Object monitor = task.getMonitor();

		// Lock the pool monitor and start it.
		// Because it needs its monitor to run, it starts
		// not until the scheduler has called wait().
		//System.out.println("now scheduling: "+plinfo);
		synchronized(monitor)
		{
			task.setStepType(steptype);
			task.setState(PlanExecutionTask.STATE_RUNNING);
			if(newthread)
			{
				// obtain the ThreadPool if this PlanExecutor was persistent
				// and the ThreadPool is not already set
				if (null == threadpool)
					threadpool = ThreadPoolFactory.getThreadPool(threadpoolIdentifier);
				
				assert threadpool != null : this;
				
				// It must be avoided that the new thread
				// immediately starts. Therefore its first
				// instruction is synchronized(monitor){}
				threadpool.execute(task);
			}
			else
			{
				monitor.notify();
			}

			try
			{
				// Wait causes to free the monitor
				// and awakens the plan thread which needs
				// the monitor to execute
				if(getMaxExecutionTime()==0)
					monitor.wait();
				else
					monitor.wait(getMaxExecutionTime());
			}
			catch(InterruptedException e)
			{
				// Shouldn't happen (agent thread shouldn't be interrupted)
				System.err.println("Warning, agent thread was interrupted");
				e.printStackTrace(System.err);
			}
			
			if(PlanExecutionTask.STATE_RUNNING.equals(task.getState()))
			{
				// todo:
				
//				agent.getLogger().warning(" plan step is running longer than maximum " +
//						"execution time, plan will be terminated: "+agent+" "+task);
//				task.getPlan().getRootGoal().fail(null);
				// Todo: wait for plan termination
				// (otherwise there are two threads running at once).
			}
//		}
//		task.lock.unlock();

//        if(task.getState().equals(PlanExecutionTask.STATE_TERMINATED))
//        {
            //plan.setCleanupFinished(true);
        	if(task.getThrowable() instanceof Error)
        		throw (Error)task.getThrowable();
        	else if(task.getThrowable() instanceof Exception)
        		throw (Exception)task.getThrowable();
        	else if(task.getThrowable()!=null)
        		// Neither Error nor Exception !?
        		throw new RuntimeException("Unexpected Throwable type: "+task.getThrowable().getClass(), task.getThrowable());
        }

		return task.getState().equals(PlanExecutionTask.STATE_INTERRUPTED);
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
		PlanExecutionTask task = (PlanExecutionTask)tasks.get(rplan);
		assert task!=null;
//		assert task.getExecutionThread()==Thread.currentThread() : rplan+", "+Thread.currentThread();
		task.giveBackControl(PlanExecutionTask.STATE_INTERRUPTED, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
	}

	/**
	 *  Called on termination of a plan.
	 *  Free all associated ressources, stop threads, etc.
	 */
	public void cleanup(Object rplan)
	{
		PlanExecutionTask task = (PlanExecutionTask)tasks.get(rplan);
		if(task!=null)
		{
			Object monitor = task.getMonitor();

			// Because plan thread needs its monitor to run, it starts
			// not until the executor has called wait().
			synchronized(monitor)
			{
				task.setState(PlanExecutionTask.STATE_RUNNING);
				task.setTerminate(true);
				monitor.notify();

				try
				{
					// Wait causes to free the monitor
					// and awakens the plan thread which needs
					// the monitor to execute
					if(getMaxExecutionTime()==0)
						monitor.wait();
					else
						monitor.wait(getMaxExecutionTime());
				}
				catch(InterruptedException e)
				{
					// Shouldn't happen (agent thread shouldn't be interrupted)
					System.err.println("Warning, agent thread was interrupted");
					e.printStackTrace(System.err);
				}

				if(PlanExecutionTask.STATE_RUNNING.equals(task.getState()))
				{
//					agent.getLogger().warning(" plan step is running longer than maximum " +
//							"execution time, plan will be terminated: "+agent+" "+task);
					
					// todo
					//task.getPlan().getRootGoal().fail(null);
					
					// Todo: wait for plan termination
					// (otherwise there are two threads running at once).
				}
			}
		}
	}

	/**
	 *  Get the executing thread of a plan.
	 *  @param rplan The plan.
	 *  @return The executing thread (if any).
	 * /
	public Thread getExecutionThread(Object rplan)
	{
		PlanExecutionTask task =  (PlanExecutionTask)tasks.get(rplan);
		return task==null? null: task.getExecutionThread();
	}*/

	/**
	 *  Called from a plan.
	 *  Registers the plan to wait for a event.
	 *  Blocks plan when body method is finished.
	 *  Note: This method cannot be synchronized, because when
	 *  a thread comes in and waits it still owns the
	 *  BDIAgent lock.
	 *  @param rplan The planinstance.
	 *  @param wa The wait abstraction.
	 * /
	public IREvent	eventWaitFor(Object rplan, WaitAbstraction wa)
	{
		// todo!
//		if(agent.isAtomic())
//			throw new RuntimeException("WaitFor not allowed in atomic block.");

		Object	rbody	= state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body);
		if(rbody==null)
			throw new RuntimeException("Plan body nulls. waitFor() calls from plan constructors not allowed.");

		// Set wait filter settings in plan.
		rplan.waitFor(wa);
		
		IREvent ret = null;
		boolean failure = false;
		PlanExecutionTask task = (PlanExecutionTask)tasks.get(rplan);
     	if(task.getExecutionThread()==Thread.currentThread())
		{
     		// Transfer execution to agent thread and wait until the plan is scheduled again.
			task.giveBackControl(PlanExecutionTask.STATE_WAITING);

			// When timout event occurred, throw as TimeoutException.
			ret = rplan.getLatestEvent();
			if(ret instanceof StandardEvent && IStandardEvent.TYPE_TIMEOUT.equals(ret.getType())
				&& ((StandardEvent)ret).getException()!=null)
			{
				throw ((StandardEvent)ret).getException();
			}
		}
		else
		{
			failure = true;
		}

		//System.out.println(":::"+myid+" "+this);

//		if(failure)
//			agent.getLogger().log(Level.SEVERE, "ThreadedWaitFor error, not plan thread: "+Thread.currentThread());

		//assert ret!=null: rplan.getName();
		return ret;
	}*/
	
	/**
	 *  Called from a plan.
	 *  Registers the plan to wait for a event.
	 *  Blocks plan when body method is finished.
	 *  Note: This method cannot be synchronized, because when
	 *  a thread comes in and waits it still owns the
	 *  BDIAgent lock.
	 *  @param rplan The planinstance.
	 *  @param wa The wait abstraction.
	 */
	public void	eventWaitFor(BDIInterpreter interpreter, Object rplan)
	{
		if(interpreter.isAtomic())
			throw new RuntimeException("WaitFor not allowed in atomic block.");

		Object	rbody	= interpreter.getState().getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body);
		if(rbody==null)
			throw new RuntimeException("Plan body nulls. waitFor() calls from plan constructors not allowed.");

		// Set wait filter settings in plan.
//		rplan.waitFor(wa);
		
//		IREvent ret = null;
		boolean failure = false;
		PlanExecutionTask task = (PlanExecutionTask)tasks.get(rplan);
     	if(task.getExecutionThread()==Thread.currentThread())
		{
     		// Transfer execution to agent thread and wait until the plan is scheduled again.
			task.giveBackControl(PlanExecutionTask.STATE_WAITING, OAVBDIRuntimeModel.PLANPROCESSINGTATE_WAITING);

			// When timout event occurred, throw as TimeoutException.
//			ret = rplan.getLatestEvent();
//			if(ret instanceof StandardEvent && IStandardEvent.TYPE_TIMEOUT.equals(ret.getType())
//				&& ((StandardEvent)ret).getException()!=null)
//			{
//				throw ((StandardEvent)ret).getException();
//			}
		}
		else
		{
			failure = true;
		}

		//System.out.println(":::"+myid+" "+this);

		if(failure)
			throw new RuntimeException("ThreadedWaitFor error, not plan thread: "+Thread.currentThread());
//			agent.getLogger().log(Level.SEVERE, "ThreadedWaitFor error, not plan thread: "+Thread.currentThread());

//		return ret;
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

	//-------- The thread for a plan instance ---------

	/**
	 *  The task for executing a plan instance. Will
	 *  be executed in its own thread.
	 */
	protected class PlanExecutionTask implements Runnable
	{
		//-------- constants --------

		public static final String STATE_RUNNING = "running";
		public static final String STATE_WAITING = "waiting";
        public static final String STATE_INTERRUPTED = "interrupted";
		public static final String STATE_TERMINATED = "terminated";

		//-------- attributes --------

		/** The interpreter. */
		protected BDIInterpreter interpreter;

		/** The capability. */
		protected Object rcapability;
		
		/** The plan. */
		protected Object rplan;

		/** The thread to wakeup. */
		protected final Object monitor;

		/** The plan thread's state. */
		protected String exestate;

		/** The plan step type to execute (body/passed/failed/aborted). */
		protected String steptype;

		/** The execution result (when a problem occurred). */
		protected Throwable throwable;

		/** The thread executing this task. */
		protected Thread thread;
		
		/** Flag indicating that the plan should terminate immediately (set from agent thread). */
		protected boolean	terminate;

		//-------- constructors --------

		/**
		 *  Create a new plan exeution thread.
		 *  @param rplan The plan instance info.
		 */
		public PlanExecutionTask(BDIInterpreter interpreter, Object rcapability, Object rplan)
		{
			assert rcapability!=null;
			this.interpreter = interpreter;
			this.rcapability = rcapability;
			this.rplan = rplan;
			this.monitor = new Object();
		}

		//-------- methods --------

		/**
		 *  The thread method.
		 */
		public void run()
		{
			// When the thread is new it has to wait till the
			// scheduler is finished (called wait).
			// The plan is not allowed to hold the monitor for
			// the whole plan execution because this would not
			// allow the scheduler (agent) thread to wakeup
			// whenever the planstep execution time of the plan
			// thread exceeds.
						
			// Save the execution thread for this task.
			this.thread = Thread.currentThread();
			interpreter.setPlanThread(thread);

			// Execute the plan (interrupted by pause() calls).
			// While the plan is executing its plan steps
			// it does not hold the monitor! Therefore another
			// task can grab the monitor and call notify on
			// it. This wakes up the agent thread.
			Plan pi = null;
			boolean	aborted	= false;	// Body is aborted, continue to aborted() method.
			boolean	interrupted	= false;	// Plan is interrupted, exit plan thread.
			try
			{
				Object	tmp	= interpreter.getState().getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body);
				if(tmp==null)
					tmp = createPlanBody(interpreter, rcapability, rplan);
				pi = (Plan)tmp;
				pi.body();
			}
			catch(BodyAborted e)
			{
				// The body method has been interrupted by the plan step action for abort.
				aborted	= true;
			}
			catch(PlanTerminated e)
			{
				// Plan is interrupted (e.g. due to excessive step length)
				// -> ignore and cleanup.
				interrupted	= true;
			}
			catch(Throwable t)
			{
				// Throwable in plan thread will be rethrown in agent thread.
				this.throwable	= t;
				t.printStackTrace();
			}

			// Skip plan cleanup code, when plan is interrupted.
			if(!interrupted)
			{
				// Wait until scheduler calls plan cleanup.
				// Abort can only happen when plan is not running. Is aborted externally e.g.
				// when rootgoal is dropped. Therefore is abort case no further giveBackControl
				// is necessary.
				if(!aborted)
				{
					PlanRules.endPlanPart(interpreter.getState(), rcapability, rplan, false);
					// Hack!!! Should not change state?
					interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate,
						this.throwable==null? OAVBDIRuntimeModel.PLANLIFECYCLESTATE_PASSED : OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED);
					
//					if(throwable!=null)
//						throwable.printStackTrace();
					giveBackControl(STATE_WAITING, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
				}
				
				// Execute cleanup code.
				throwable	= null;
				try
				{
					if(steptype.equals(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_PASSED))
					{
						pi.passed();
					}
					else if(steptype.equals(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED))
					{
						// Check if body has been created (can be null when body creation fails).
						if(pi!=null)
						{
							pi.failed();
						}
					}
					else if(steptype.equals(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED))
					{
						pi.aborted();
					}
				}
				catch(PlanTerminated e)
				{
					// Plan is interrupted (e.g. due to excessive step length)
					// -> ignore and cleanup.
				}
				catch(Throwable t)
				{
					// Throwable in plan thread will be rethrown in agent thread.
					this.throwable	= t;
				}
			}

			PlanRules.endPlanPart(interpreter.getState(), rcapability, rplan, true);
			// Set plan processing state.
			interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED);
			
			// Cleanup the plan execution thread.
			tasks.remove(rplan);
			exestate	= PlanExecutionTask.STATE_TERMINATED;

			// Finally, transfer execution back to agent thread.
			synchronized(monitor)
			{
				interpreter.setPlanThread(null);
				monitor.notify();
				//System.out.println("Execution of plan finished: "+type+", "+this+": "+rplan);
			}
		}

		/**
		 *  Get the pool monitor.
		 *  @return The monitor.
		 */
		public Object getMonitor()
		{
			return monitor;
		}

		/**
		 *  Interrupt the plan execution.
		 *  Stop and notify the scheduler.
		 *  Continues when monitor is notified from the scheduler again.
		 */
		public void	giveBackControl(String exestate, String procstate)
		{
			//System.out.println("waiting for lock: "+n);
			synchronized(monitor)
			{
				// Remember current step type (might get overwritten from agent thread).
				String planstate	= steptype;
				
				// Set processing state to "waiting"
				interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, procstate);

				// Transfer execution from plan thread to agent thread using notify/wait pair.
				this.exestate	= exestate;
				monitor.notify();
				try
				{
					interpreter.setPlanThread(null);
					monitor.wait();
				}
				catch(InterruptedException e)
				{
					// Shouldn't happen (plan thread shouldn't be interrupted)
					System.err.println("Warning, plan thread was interrupted: "+rplan);
					e.printStackTrace(System.err);
				}

				// Execution continues when the executors executeStep() transfers
				// execution from agent thread to plan thread (using another notify/wait pair).
				interpreter.setPlanThread(thread);
				
				// When plan must be terminated unconditionally stop execution.
				if(terminate)
				{
					throw new PlanTerminated();
				}
				// When planstate has changed from body to aborted, leave body method
				// by throwing an error, which is catched by plan execution task.
				else if(planstate.equals(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY) 
					&& interpreter.getState().getAttributeValue(rplan, OAVBDIRuntimeModel
					.plan_has_lifecyclestate).equals(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED))
				{
					throw new BodyAborted();
				}
			}
		}

		/**
		 *  Get the plan.
		 *  @return The plan.
		 */
		public Object getPlan()
		{
			return this.rplan;
		}

		/**
		 *  Get the plan thread state.
		 *  @return The plan thread state.
		 */
		public String getState()
		{
			return this.exestate;
		}

		/**
		 *  Set the plan thread state.
		 *  @param state The plan thread state.
		 */
		public void setState(String exestate)
		{
			this.exestate = exestate;
		}

		/**
		 *  Set the plan step type.
		 *  @param type The plan step type (body/passed/failed/aborted).
		 */
		public void setStepType(String type)
		{
			this.steptype = type;
		}

		/**
		 *  Get the execution result.
		 *  @return The execution result.
		 */
		public Throwable getThrowable()
		{
			return throwable;
		}

		/**
		 *  Get the execution thread.
		 *  @return thread The thread.
		 */
		public Thread getExecutionThread()
		{
			return this.thread;
		}
		
		/**
		 *  Set the terminate flag.
		 */
		public void	setTerminate(boolean terminate)
		{
			this.terminate	= terminate;
		}

		/**
		 *  Create a string representation of this element.
		 */
		public String	toString()
		{
			return "PlanExecutionTask("+rplan+")";
		}
	}
	
	/**
	 *  An error thrown to abort the execution of the plan body.
	 */
	public static class BodyAborted	extends	ThreadDeath 
	{
//		public BodyAborted()
//		{
//			System.err.print(this+": ");
//			Thread.dumpStack();
//		}
//		
//		public String toString()
//		{
//			return super.toString()+"@"+hashCode();
//		}
	}
	
	/**
	 *  An error allowing the agent to terminate the execution of a plan.
	 */
	public static class PlanTerminated	extends	ThreadDeath 
	{
	}
	
	// todo remove me
	public static int n;
}
