package jadex.bdi.runtime.impl;

import jadex.bdi.features.IBDIAgentFeature;
import jadex.bdi.features.impl.IInternalBDIAgentFeature;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IPlanExecutor;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.interpreter.PlanRules;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.SReflect;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.rules.state.IOAVState;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 *  A plan executor for plans that run on their own thread
 *  and therefore may perform blocking wait operations.
 *  Plan bodies have to inherit from @link{Plan}.
 */
// todo: move somewhere else (impl???).
public class JavaStandardPlanExecutor	implements IPlanExecutor, Serializable
{
	//-------- constants --------

	public static final String MAX_PLANSTEP_TIME = "max_planstep_time";
	
	//-------- attributes --------

	/** The bdi interpreter. */
	//protected BDIInterpreter interpreter;
	
	/** The maximum execution time per plan step in millis. */
//	protected Number maxexetime;

	/** The pool for the planinstances -> execution tasks. */
	protected Map	tasks;
	
	/** The threadpool. */
	protected IThreadPool threadpool;

	//-------- constructor --------

	/**
	 *  Create a new threadbased plan executor.
	 */
	public JavaStandardPlanExecutor(IThreadPool threadpool)
	{
		this.threadpool = threadpool;
		this.tasks = Collections.synchronizedMap(SCollection.createHashMap());
	}

	//-------- IPlanExecutor interface --------

	/**
	 *  Create the body of a plan.
	 *  @param rplan The rplan.
	 *  @return	The created body.
	 *  May throw any kind of exception, when the body creation fails
	 */
	public Object	createPlanBody(IInternalAccess interpreter, Object rcapability, Object rplan) throws Exception
	{
		// Create plan body object.
		// Hack!!! Not an elegant way by using a static hashtable!
		// Needed for passing the rplan to the abstract plan instance.
		String refname= ""+Thread.currentThread()+"_"+Thread.currentThread().hashCode();
		AbstractPlan.planinit.put(refname, new Object[]{interpreter, rplan, rcapability});

		IInternalBDIAgentFeature bdif = (IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class);
		IOAVState state = bdif.getState();
		
		Object	mplan	= state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
		Object	mbody	= state.getAttributeValue(mplan, OAVBDIMetaModel.plan_has_body);
//		Class	clazz	= (Class)interpreter.getState().getAttributeValue(mbody, OAVBDIMetaModel.body_has_class);
		String clname = (String)state.getAttributeValue(mbody, OAVBDIMetaModel.body_has_impl);
		String sername = (String)state.getAttributeValue(mbody, OAVBDIMetaModel.body_has_service);
		
		Object	body = null;
		if(clname!=null)
		{
			Class clazz = SReflect.findClass(clname, bdif.getModel(rcapability).getAllImports(), state.getTypeModel().getClassLoader());
			
			if(clazz!=null)
			{
				try
				{
					body = clazz.newInstance();
					if(!(body instanceof Plan))
						throw new RuntimeException("User plan has wrong baseclass. Expected jadex.bdi.runtime.Plan for standard plan.");
					bdif.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body, body);
				}
				catch(Exception e)
				{
					// Use only RuntimeException from below
					e.printStackTrace();
				}
			}
		}
		else if(sername!=null)
		{
			String methodname = (String)state.getAttributeValue(mbody, OAVBDIMetaModel.body_has_method);
			body = new ServiceCallPlan(sername, methodname);
			bdif.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body, body);
		}
		else 
		{
			throw new RuntimeException("Classname must not be null: "+state.getAttributeValue(state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
		}
		

		AbstractPlan.planinit.remove(refname);

		if(body==null)
			throw new RuntimeException("Plan body could not be created: "+clname!=null? clname: sername);

		return body;
	}

	/**
	 *  Execute a step of a plan.
	 *  Executing a step should cause the latest event to be handled.
	 *  Will be called by the scheduler for every event to be handled.
	 *  May throw any kind of exception, when the plan execution fails
	 *  @return True, if the plan step was interrupted (interrupted flag).
	 */
	public boolean	executeStep(IInternalAccess interpreter, Object rcapability, Object rplan, String steptype)	throws Exception
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
//		System.out.println("plan step started: "+rplan+" "+interpreter.getState()
//			.getAttributeValue(interpreter.getState().getAttributeValue(rplan, 
//			OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
		synchronized(monitor)
		{
			task.setStepType(steptype);
			task.setState(PlanExecutionTask.STATE_RUNNING);
			if(newthread)
			{
				// It must be avoided that the new thread
				// immediately starts. Therefore its first
				// instruction is synchronized(monitor){}
//				System.out.println("execute: "+rplan);
				threadpool.execute(task);
			}
			else
			{
//				System.out.println("notify: "+rplan);
				monitor.notify();
			}

			try
			{
				// Wait causes to free the monitor
				// and awakens the plan thread which needs
				// the monitor to execute
//				System.out.println("wait: "+rplan);
				if(getMaxExecutionTime(interpreter)==0)
					monitor.wait();
				else
					monitor.wait(getMaxExecutionTime(interpreter));
//				System.out.println("resumed: "+rplan);
			}
			catch(InterruptedException e)
			{
				// Shouldn't happen (agent thread shouldn't be interrupted)
				System.err.println("Warning, agent thread was interrupted");
				e.printStackTrace(System.err);
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
			
//			if(PlanExecutionTask.STATE_RUNNING.equals(task.getState()))
//			{
//				// todo:
//				
////				agent.getLogger().warning(" plan step is running longer than maximum " +
////						"execution time, plan will be terminated: "+agent+" "+task);
////				task.getPlan().getRootGoal().fail(null);
//				// Todo: wait for plan termination
//				// (otherwise there are two threads running at once).
//			}
//		}
//		task.lock.unlock();

//        if(task.getState().equals(PlanExecutionTask.STATE_TERMINATED))
//        {
		
//		System.out.println("plan step finished: "+rplan+" "+interpreter.getState()
//			.getAttributeValue(interpreter.getState().getAttributeValue(rplan, 
//			OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
			
            //plan.setCleanupFinished(true);
        	if(task.getThrowable() instanceof Exception)
        		throw (Exception)task.getThrowable();
        	else if(task.getThrowable()!=null)
        		throw new RuntimeException(task.getThrowable());
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
	public boolean	executePlanStep(IInternalAccess interpreter, Object rcapability, Object rplan)	throws Exception
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
	public boolean	executePassedStep(IInternalAccess interpreter, Object rplan)	throws Exception
	{
		assert tasks.containsKey(rplan);
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
	public boolean	executeFailedStep(IInternalAccess interpreter, Object rplan)	throws Exception
	{
		assert tasks.containsKey(rplan);
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
	public boolean	executeAbortedStep(IInternalAccess interpreter, Object rplan)	throws Exception
	{
		assert tasks.containsKey(rplan);
		return executeStep(interpreter, null, rplan, OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED);
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
//		System.out.println("interruptPlanStep: Setting plan to ready: "
//				+task.interpreter.getAgentAdapter().getComponentIdentifier().getLocalName()
//				+", "+rplan);
	}

	/**
	 *  Called on termination of a plan.
	 *  Free all associated ressources, stop threads, etc.
	 */
	public void cleanup(IInternalAccess interpreter, Object rplan)
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
					if(getMaxExecutionTime(interpreter)==0)
						monitor.wait();
					else
						monitor.wait(getMaxExecutionTime(interpreter));
				}
				catch(InterruptedException e)
				{
					// Shouldn't happen (agent thread shouldn't be interrupted)
					System.err.println("Warning, agent thread was interrupted");
					e.printStackTrace(System.err);
				}

//				if(PlanExecutionTask.STATE_RUNNING.equals(task.getState()))
//				{
////					agent.getLogger().warning(" plan step is running longer than maximum " +
////							"execution time, plan will be terminated: "+agent+" "+task);
//					
//					// todo
//					//task.getPlan().getRootGoal().fail(null);
//					
//					// Todo: wait for plan termination
//					// (otherwise there are two threads running at once).
//				}
			}
		}
	}
	
	/**
	 *  Get the monitor of a plan.
	 *  @return The monitor.
	 */
	public Object getMonitor(Object rplan)
	{
		PlanExecutionTask task =  (PlanExecutionTask)tasks.get(rplan);
		return task==null? null: task.getMonitor();
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
	public void	eventWaitFor(IInternalAccess interpreter, Object rplan)
	{
		if(((IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class)).isAtomic())
			throw new RuntimeException("WaitFor not allowed in atomic block.");

		Object	rbody	= ((IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class)).getState().getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body);
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
//     		System.out.println("GiveBackControl: "+interpreter.getComponentIdentifier()+", "+rplan);
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
	protected long getMaxExecutionTime(IInternalAccess interpreter)
	{
		// todo: properties?
		Map	props	= null;//interpreter.getProperties(); 
		Number max = props!=null ? (Number)props.get(MAX_PLANSTEP_TIME) : null;
		return max!=null? max.longValue(): 0;
		
//		if(maxexetime==null)
//		{
//			maxexetime = (Number)interpreter.getState().getAttributeValue(interpreter.getAgent(), OAVBDIRuntimeModel.capability_has_properties, MAX_PLANSTEP_TIME);
////			maxexetime = (Number)component.getPropertybase().getProperty(MAX_PLANSTEP_TIME);
//			if(maxexetime==null)
//				maxexetime = new Long(0);
//		}
//		return maxexetime.longValue();
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
		protected IInternalAccess interpreter;

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
		public PlanExecutionTask(IInternalAccess interpreter, Object rcapability, Object rplan)
		{
//			if(rcapability==null)
//			{
//				System.out.println("plan already finished: "
//					+interpreter.getAgentAdapter().getComponentIdentifier().getLocalName()
//					+", "+rplan);
//			}
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
//			System.out.println("start: "+rplan);
			// When the thread is new it has to wait till the
			// scheduler is finished (called wait).
			// The plan is not allowed to hold the monitor for
			// the whole plan execution because this would not
			// allow the scheduler (agent) thread to wakeup
			// whenever the planstep execution time of the plan
			// thread exceeds.
			synchronized(monitor)
			{				
			}
//			System.out.println("start2: "+rplan);
			
			// Save the execution thread for this task.
			this.thread = Thread.currentThread();
			((IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class)).setPlanThread(thread);
			ClassLoader oldcl = thread.getContextClassLoader();
			assert ((IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class)).getState().getTypeModel().getClassLoader()!=null;
			thread.setContextClassLoader(((IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class)).getState().getTypeModel().getClassLoader());

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
				Object	tmp	= ((IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class)).getState().getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body);
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
//				t.printStackTrace();
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
					PlanRules.endPlanPart(((IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class)).getState(), rcapability, rplan, false);
					// Hack!!! Should not change state?
					((IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class)).getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate,
						this.throwable==null? OAVBDIRuntimeModel.PLANLIFECYCLESTATE_PASSED : OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED);
					
//					if(throwable!=null)
//						throwable.printStackTrace();
					giveBackControl(STATE_WAITING, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
//					System.out.println("PlanExecutionTask.STATE_WAITING: Setting plan to ready: "
//							+interpreter.getAgentAdapter().getComponentIdentifier().getLocalName()
//							+", "+rplan);
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

			PlanRules.endPlanPart(((IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class)).getState(), rcapability, rplan, true);

			// Set plan processing state.
			((IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class)).getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED);
			
			// Cleanup the plan execution thread.
			tasks.remove(rplan);
			exestate	= PlanExecutionTask.STATE_TERMINATED;

			// Finally, transfer execution back to agent thread.
			synchronized(monitor)
			{
//				System.out.println("finish1: "+rplan);
				((IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class)).setPlanThread(null);
				thread.setContextClassLoader(oldcl);
				monitor.notify();
//				System.out.println("finish2: "+rplan);
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
				((IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class)).getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, procstate);

				// Transfer execution from plan thread to agent thread using notify/wait pair.
				this.exestate	= exestate;
				monitor.notify();
				try
				{
					((IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class)).setPlanThread(null);
//					System.out.println("givebackcontrol: "+rplan);
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
				((IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class)).setPlanThread(thread);
				
				// When plan must be terminated unconditionally stop execution.
				if(terminate)
				{
//					System.out.println("terminate plan: "+rplan);
					throw new PlanTerminated();
				}
				// When planstate has changed from body to aborted, leave body method
				// by throwing an error, which is catched by plan execution task.
				else if(planstate.equals(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY) 
					&& ((IInternalBDIAgentFeature)interpreter.getComponentFeature(IBDIAgentFeature.class)).getState().getAttributeValue(rplan, OAVBDIRuntimeModel
					.plan_has_lifecyclestate).equals(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED))
				{
//					System.out.println("abort plan: "+rplan);
					throw new BodyAborted();
				}
				
//				else
//				{
//					System.out.println("continue plan: "+rplan+", "+planstate+", "
//						+interpreter.getState().getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate));
//				}
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
		
		public void printStackTrace()
		{
			Thread.dumpStack();
			super.printStackTrace();
		}
	}
	
	/**
	 *  An error allowing the agent to terminate the execution of a plan.
	 */
	public static class PlanTerminated	extends	ThreadDeath 
	{
	}
	
	/**
	 *  Create the plan executor.
	 */
	public static IFuture<IPlanExecutor>	createPlanExecutor(IInternalAccess comp)
	{
		final Future<IPlanExecutor>	ret	= new Future<IPlanExecutor>();
		SServiceProvider.getService(comp, IThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IThreadPoolService, IPlanExecutor>(ret)
		{
			public void customResultAvailable(IThreadPoolService threadpool)
			{
				ret.setResult(new JavaStandardPlanExecutor(threadpool));
			}
		});
		return ret;
	}
	
	// todo remove me
//	public static int n;
}
