package jadex.bdi.runtime;


import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;

import org.apache.commons.javaflow.Continuation;

/**
	 *  The task for executing a plan instance. Will
	 *  be executed in its own thread.
	 */
	public class FlowPlanExecutionTask implements Runnable
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

		/** The plan thread's state. */
		protected String exestate;

		/** The plan step type to execute (body/passed/failed/aborted). */
		protected String steptype;

		/** The execution result (when a problem occurred). */
		protected Throwable throwable;

		/** The thread executing this task. */
		protected transient Thread thread;
		
		/** The executor executing this task */
		protected transient FlowPlanExecutor flowPlanExecutor;

		
		/** Flag indicating that the plan should terminate immediately (set from agent thread). */
		protected boolean	terminate;

		//-------- constructors --------

		/**
		 *  Create a new plan exeution thread.
		 *  @param rplan The plan instance info.
		 * @param flowPlanExecutor TODO
		 */
		public FlowPlanExecutionTask(FlowPlanExecutor flowPlanExecutor, BDIInterpreter interpreter, Object rcapability, Object rplan)
		{
			this.flowPlanExecutor = flowPlanExecutor;
			assert rcapability!=null;
			this.interpreter = interpreter;
			this.rcapability = rcapability;
			this.rplan = rplan;
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
			
			FlowPlanExecutor.debug(this, "Method: run() - START", this, null);
						
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
					tmp = this.flowPlanExecutor.createPlanBody(interpreter, rcapability, rplan);
				pi = (Plan)tmp;
				pi.body();
			}
			
			catch(FlowPlanExecutionTask.BodyAborted e)
			{
				// The body method has been interrupted by the plan step action for abort.
				aborted	= true;
			}
			catch(FlowPlanExecutionTask.PlanTerminated e)
			{
				// Plan is interrupted (e.g. due to excessive step length)
				// -> ignore and cleanup.
				interrupted	= true;
			}
			catch (VerifyError ve)
			{
				System.err.println("caught = " + ve);
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
				catch(FlowPlanExecutionTask.PlanTerminated e)
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

			// Set plan processing state.
			interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED);
			
			// Cleanup the plan execution thread.
			this.flowPlanExecutor.tasks.remove(rplan);
			exestate	= FlowPlanExecutionTask.STATE_TERMINATED;

			
			// Finally, transfer execution back to PlanExececutor and exit continuation.
			Continuation.exit();
			
			FlowPlanExecutor.debug(this, "Method: run() - RETURN", this, null);
		}

//		/**
//		 *  Get the pool monitor.
//		 *  @return The monitor.
//		 */
//		public Object getMonitor()
//		{
//			return monitor;
//		}

		/**
		 *  Interrupt the plan execution.
		 *  Stop and notify the scheduler.
		 *  Continues when monitor is notified from the scheduler again.
		 */
		public void	giveBackControl(String exestate, String procstate)
		{
			
//			// HACK! Dont use this method! Move Continuation.suspend to plan helper class.
//			throw new RuntimeException("Unsupported Operation. Cannot suspend a JavaStandardPlan");
			
			
			FlowPlanExecutor.debug(this, "Method: giveBackControl() - START", this, null);
			
			// Remember current step type (might get overwritten from agent thread).
			String planstate	= steptype;
			
			// Set processing state to "waiting"
			interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, procstate);

			// Transfer execution from plan to agent using continuation suspend
			this.exestate	= exestate;
			
			try
			{
				interpreter.setPlanThread(null);
				
				FlowPlanExecutor.debug(this, "Method: giveBackControl() - Calling suspend", this, null);
				
				// suspend the execution, all methods on the call-stack down to ContinuationPlanExecutor.executeStep() return immediately
				// the current thread state will thereby be stored in a continuation-object for later reinvokements.
				Continuation.suspend();
				
				FlowPlanExecutor.debug(this, "Method: giveBackControl() - Continuation resumed after suspend", this, null);
			}
			catch(Throwable e)
			{
				// Shouldn't happen (Occurs only when something goes wrong with the continuation)
				System.err.println("Warning, continuation aborted with Throwable: " + e.getClass().getName());
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
			return "FlowPlanExecutionTask("+rplan+")";
		}
		
		// ------ getter / setter for transient fields ------
		
//		/**
//		 * Get the (current) thread. The thread is a transient field that
//		 * have to be be set for each step.
//		 * @return thread used as executing thread for this Task.
//		 */
//		public Thread getThread()
//		{
//			return thread;
//		}
//
//		/**
//		 * Set the executing thread. The thread is a transient field that
//		 * have to be be set for each step.
//		 * @param thread to use as executing thread for this Task.
//		 */
//		public void setThread(Thread thread)
//		{
//			this.thread = thread;
//		}
		
		/**
		 * Get the (current) executor. The executor is a transient field that
		 * have to be be set for each step.
		 * @return the IExecutor executing this Task.
		 */
		public FlowPlanExecutor getFlowPlanExecutor()
		{
			return flowPlanExecutor;
		}

		/**
		 * Set the executor. The executor is a transient field that
		 * have to be be set for each step.
		 * @param flowPlanExecutor
		 */
		public void setFlowPlanExecutor(FlowPlanExecutor flowPlanExecutor)
		{
			this.flowPlanExecutor = flowPlanExecutor;
		}
		
		
		
		/**
		 *  An error thrown to abort the execution of the plan body.
		 */
		public static class BodyAborted	extends	ThreadDeath 
		{
//			public BodyAborted()
//			{
//				System.err.print(this+": ");
//				Thread.dumpStack();
//			}
//			
//			public String toString()
//			{
//				return super.toString()+"@"+hashCode();
//			}
		}
		
		/**
		 *  An error allowing the agent to terminate the execution of a plan.
		 */
		public static class PlanTerminated	extends	ThreadDeath 
		{
		}
		
	}