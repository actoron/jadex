package jadex.bdi.runtime;

import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.interpreter.PlanRules;
import jadex.bdi.interpreter.bpmn.model.IBpmnState;
import jadex.bdi.interpreter.bpmn.model.IBpmnTransition;
import jadex.bdi.interpreter.bpmn.model.ParsedStateMachine;
import jadex.bdi.interpreter.bpmn.parser.BpmnParser;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  A plan executor for plans modeled in BPMN. These plan steps 
 *  are executed on the agent thread.
 */
public class BpmnPlanExecutor	implements IPlanExecutor, Serializable
{
	
	//-------- attributes --------
	
	/** Hack! Map for the parsed plan body objects (mplan -> bodyImpl) */
	protected Map	planModelCache;

	//-------- constructor --------

	/**
	 *  Create a new BPMN plan executor.
	 */
	public BpmnPlanExecutor()
	{
		this.planModelCache = Collections.synchronizedMap(SCollection.createHashMap());
	}

	//-------- IPlanExecutor interface --------

	/**
	 *  Create the body of a plan.
	 *  @param rplan The rplan.
	 *  @return	The created body state.
	 *  May throw any kind of exception, when the body creation fails
	 */
	public Object	createPlanBody(BDIInterpreter interpreter, Object rcapability, Object rplan) throws Exception
	{
		// Create plan body object.
		Object	mplan	= interpreter.getState().getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
		Object	mbody	= interpreter.getState().getAttributeValue(mplan, OAVBDIMetaModel.plan_has_body);
		String	impl	= (String)interpreter.getState().getAttributeValue(mbody, OAVBDIMetaModel.body_has_impl);
		
		// HACK!
		// check the model cache for already parsed plan
		Object	planModel = planModelCache.get(mbody);
		
		if(planModel == null && impl!=null)
		{
			// Save current classloader
			Thread thread = Thread.currentThread();
			ClassLoader oldcl = thread.getContextClassLoader();
			
			try
			{
				assert interpreter.getState().getTypeModel().getClassLoader() != null;
				thread.setContextClassLoader(interpreter.getState().getTypeModel().getClassLoader());
				
				// read the file and parse the state machine
				InputStream planIS = SUtil.getResource(impl, interpreter.getState().getTypeModel().getClassLoader());
				BpmnParser parser = BpmnParser.getInstance(planIS);
				planModel = parser.parseFile();
				if(!(planModel instanceof ParsedStateMachine))
					throw new RuntimeException("Parsing of BPMN implementation failed");

				// HACK! Cache parsed body models
				planModelCache.put(mbody, planModel);
			}
			catch(Exception e)
			{
				// Use only RuntimeException from below
				e.printStackTrace();
			}
			finally
			{
				// Restore classloader
				thread.setContextClassLoader(oldcl);
			}
		}

		if(planModel==null)
			throw new RuntimeException("Plan body could not be created: "+impl);

		// create the body data structure and update state
		BpmnPlanContext context = new BpmnPlanContext(interpreter, rcapability, rplan, planModel);
		interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body, context);
		
		return context;
	}

	/**
	 *  Execute a step of a BPMN plan.
	 *  Executing a step should cause the latest event to be handled.
	 *  Will be called by the scheduler for every event to be handled.
	 *  May throw any kind of exception, when the plan execution fails
	 *  @return True, if the plan step was interrupted (interrupted flag).
	 */
	public boolean	executeStep(BDIInterpreter interpreter, Object rcapability, Object rplan, String steptype)	throws Exception
	{
		// Get or create a new plan body for the plan instance info.
		Object	tmp	= interpreter.getState().getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body);
		if(tmp == null)
		{
			tmp = createPlanBody(interpreter, rcapability, rplan);
			// Set processing state to "running"
			interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);

		}
		BpmnPlanContext context = (BpmnPlanContext) tmp;
		
		Throwable throwable = null;
		
		// HACK!
		IBpmnState state = context.getCurrentState();
		
		try
		{
			// execute a body step
			if(steptype.equals(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY))
			{
			
				if (state != null)
				{
					// Set processing state to "running"
					interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_RUNNING);

					
					context = (BpmnPlanContext) state.execute(context);
					
					// executed, remove from executable
					context.getExecutableStateIds().remove(state.getId());
					
					// FIXME: for test, simply activate all edges
					// TODO: check preconditions of last returned successors and add to activated
					List activatedStates = state.getOutgoingEdges();
					for (Iterator edges = activatedStates.iterator(); edges.hasNext();)
					{
						IBpmnTransition edge = (IBpmnTransition) edges.next();
						context.getExecutableStateIds().add(edge.getTargetId());
						
						System.out.println("Activate a BPMN state: " + edge.getTargetId());
						
					}

				}
				else
				{
					// TODO: change exception to something useful
					throw new NullPointerException("Missing state for '" + context 
							+ "' in BpmnPlanModel '" + context.getMbody() + "'");
				}
			
			}
			else if(steptype.equals(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED))
			{
				// Abort plan, free resources
			}
			else
			{
				// TO DO: check exception --> remove (simply ignore other step types?)
				throw new RuntimeException("Invalid steptype='"+steptype+"'. "
						+"currently only steptype='"
						+OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY+"' and '"
						+OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED+"' are supported");
			}
		}
		catch(Throwable t)
		{
			// Throwable in body task will be thrown later
			throwable = t;
		}
		
		// check for errors / exception / final state
		if (throwable == null && !state.isFinalState())
		{
			// no exception nor a final state, resume plan
			
			// activate next state
			// FIXME: Remove linear activation of states to support pseudo parallel execution of tasks
			// HACK!
			context.setCurrentStateId((String)context.getExecutableStateIds().get(0));

			// Set processing state to "ready"
			interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);

			// A BPMN plan step is not interruptible
			return false;
		}
		else
		{
			// Exception or final state, finish plan
			PlanRules.endPlanPart(interpreter.getState(), rcapability, rplan, true);
			
			// Set plan processing state.
			interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED);
			
			// Hack!!! Should not change state?
			// set plan lifecycle state
			interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate,
				throwable==null? OAVBDIRuntimeModel.PLANLIFECYCLESTATE_PASSED : OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED);

			// in case of an error / exception, throw it now
			if(throwable instanceof Exception)
			{
	    		throw (Exception) throwable;
			}
	    	else if(throwable != null)
	    	{
	    		throw new RuntimeException(throwable);
	    	}
		}

		// return false, step is not interruptible
    	return false;
    	
	}
	
	/**
	 *  Execute a step of a BPMN plan.
	 *  Executing a step should cause the latest event to be handled.
	 *  Will be called by the scheduler for every event to be handled.
	 *  May throw any kind of exception, when the plan execution fails
	 *  @return True, if the plan step was interrupted (interrupted flag).
	 * /
	public boolean	executeStep(BDIInterpreter interpreter, Object rcapability, Object rplan, String steptype)	throws Exception
	{
		// Get or create a new task for the plan instance info.
		boolean newtask = false;
		PlanExecutionTask task = (PlanExecutionTask) tasks.get(rplan);
		if(task==null)
		{
			task = new PlanExecutionTask(interpreter, rcapability, rplan);
			tasks.put(rplan, task);
			newtask = true;
		}

		if (newtask)
		{
			
		}
		
		task.setStepType(steptype);
		task.setState(PlanExecutionTask.STATE_RUNNING);
		task.execute();

		if(PlanExecutionTask.STATE_RUNNING.equals(task.getState()))
		{
			
			// we may start a second thread in a stateTask to provide IO, 
			// in this case, the execution state of the task should remain in running!
			
			// TO DO: check if other agents are allowed to execute something during IO
			
		}
		
    	if(task.getThrowable() instanceof Exception)
    		throw (Exception)task.getThrowable();
    	else if(task.getThrowable()!=null)
    		throw new RuntimeException(task.getThrowable());

    	// will always return false? A BPMN step is not interruptible (expect of a second IO thread??)
    	//return task.getState().equals(PlanExecutionTask.STATE_INTERRUPTED);
    	return false;
    	
	} */

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
//		assert tasks.containsKey(rplan);
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
//		assert tasks.containsKey(rplan);
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
//		assert tasks.containsKey(rplan);
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
		// BPMN plan steps are not interruptible
	}

	/**
	 *  Called on termination of a plan.
	 *  Free all associated ressources, stop threads, etc.
	 */
	public void cleanup(Object rplan)
	{
		// TODO: implement cleanup if needed 
	}

	/**
	 *  Called from a plan.
	 *  Registers the plan to wait for a event.
	 *  Blocks plan when body method is finished.
	 *  Note: This method cannot be synchronized, because when
	 *  a thread comes in and waits it still owns the
	 *  BDIAgent lock.
	 *  @param interpreter The bdi interpreter.
	 *  @param rplan The planinstance.
	 */
	public void	eventWaitFor(BDIInterpreter interpreter, Object rplan)
	{
		// TO DO: implement passive wait
		// currently only an active wait (check for event each execution) in a plan step is implemented
	}

	/**
	 *  Get the maximum execution time.
	 *  0 indicates no maximum execution time.
	 *  @return The max execution time.
	 */
	protected long getMaxExecutionTime()
	{
		// BPMN plan step isn't interruptible
		return 0;
	}

	//-------- The thread for a plan instance ---------

//	/**
//	 *  The task for executing a plan instance. Will
//	 *  be executed in its own thread.
//	 */
//	protected class PlanExecutionTask // implements IBpmnPlanBody
//	{
//		//-------- constants --------
//
//		public static final String STATE_RUNNING = "running";
//		public static final String STATE_WAITING = "waiting";
//        public static final String STATE_INTERRUPTED = "interrupted";
//		public static final String STATE_TERMINATED = "terminated";
//
//		//-------- attributes --------
//
//		/** The interpreter. */
//		protected BDIInterpreter interpreter;
//
//		/** The capability. */
//		protected Object rcapability;
//		
//		/** The plan. */
//		protected Object rplan;
//
//		/** The plan thread's state. */
//		protected String exestate;
//
//		/** The plan step type to execute (body/passed/failed/aborted). */
//		protected String steptype;
//
//		/** The execution result (when a problem occurred). */
//		protected Throwable throwable;
//		
//		/** The BPMN plan states (string-identifier -> impl) */
//		protected Map states;
//		
//		/** The identifier for current BPMN state to execute */
//		protected String currentStateIdentifier;
//
//		//-------- constructors --------
//
//		/**
//		 *  Create a new BPMN plan execution task.
//		 *  @param rplan The plan instance info.
//		 */
//		public PlanExecutionTask(BDIInterpreter interpreter, Object rcapability, Object rplan)
//		{
//			assert rcapability!=null;
//			this.interpreter = interpreter;
//			this.rcapability = rcapability;
//			this.rplan = rplan;
//		}
//
//		//-------- methods --------
//		
//		/**
//		 * Evaluate a Jadex-OQL-Condition expression
//		 * @param the conditional expression
//		 * @return Boolean with evaluation result
//		 */
//		public Boolean evalJadexOQLCondition(String condition)
//		{
//			// FIX ME: implement evaluation!
//			return new Boolean(true);
//		}
//		
//		/**
//		 *  The execution method.
//		 */
//		public void execute()
//		{
//
//			// Save the execution thread for this task.
//			//this.thread = Thread.currentThread();
//			interpreter.setPlanThread(Thread.currentThread());
//			
//			ParsedStateMachine psm = null;
//			IBpmnState state = null;
//			
//			if(steptype.equals(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY))
//			{
//				
//				try
//				{
//					// if we run this method for the first time,
//					// we need to initialize the states.
//					// TO DO: check move into PLANLIFECYCLESTATE_NEW part.
//					if (this.states == null || this.currentStateIdentifier == null)
//					{
//						Object	tmp	= interpreter.getState().getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body);
//						if(tmp==null)
//							tmp = createPlanBody(interpreter, rcapability, rplan);
//						psm = (ParsedStateMachine) tmp;
//						
//						this.states = psm.getStateMap();
//						this.currentStateIdentifier = psm.getStartStateId();
//					}
//		
//					// Execute the next plan step.
//					// While the plan is executing its plan step
//					// it is not interruptible!
//					state = (IBpmnState) this.states.get(currentStateIdentifier);
//					if (state != null)
//					{
//						// TO DO: HACK! Replace this with data-struct
//						state.execute(this);
//					}
//					else
//					{
//						throw new NullPointerException("Missing state for '" + currentStateIdentifier 
//								+ "' in StateMachine '" + psm + "'");
//					}
//					
//
//				}
//				catch(Throwable t)
//				{
//					// Throwable in task/state will be rethrown in agent.
//					this.throwable	= t;
//				}
//				
//				
//				if (throwable != null || state.isFinalState())
//				{
//					// Exception or final state, finish body part
//					PlanRules.endPlanPart(interpreter.getState(), rcapability, rplan, false);
//					
//					// Hack!!! Should not change state?
//					interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate,
//						this.throwable==null? OAVBDIRuntimeModel.PLANLIFECYCLESTATE_PASSED : OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED);
//				}
//				else
//				{
//					// TO DO: Use data-struct for next stateId
//					// activate next state
//					currentStateIdentifier = state.getNextStateId();
//				}
//				
//				giveBackControl(STATE_WAITING, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
//
//				// return to agent execution
//				return;
//				
//			}
//			else 
//			{
//				// TO DO: check exception
//				this.throwable = 
//					new RuntimeException("Invalid steptype='"+steptype+"' currently only steptype='"
//						+OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY+"' is supported");
//			}
//			
//			
//			PlanRules.endPlanPart(interpreter.getState(), rcapability, rplan, true);
//			
//			// Set plan processing state.
//			interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED);
//
//			// Cleanup the plan execution task.
//			tasks.remove(rplan);
//			exestate	= PlanExecutionTask.STATE_TERMINATED;
//
//		}
//
//		
//
//		/**
//		 *  Interrupt the plan execution.
//		 *  Save some values into local attributes and AgentState
//		 */
//		public void	giveBackControl(String exestate, String procstate)
//		{
//			
//			// Set processing state to "waiting"
//			interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, procstate);
//
//			// Remember the current execution state. (why?)
//			this.exestate	= exestate;
//		
//		}
//
//		/**
//		 *  Get the plan.
//		 *  @return The plan.
//		 */
//		public Object getPlan()
//		{
//			return this.rplan;
//		}
//
//		/**
//		 *  Get the plan thread state.
//		 *  @return The plan thread state.
//		 */
//		public String getState()
//		{
//			return this.exestate;
//		}
//
//		/**
//		 *  Set the plan thread state.
//		 *  @param state The plan thread state.
//		 */
//		public void setState(String exestate)
//		{
//			this.exestate = exestate;
//		}
//
//		/**
//		 *  Set the plan step type.
//		 *  @param type The plan step type (body/passed/failed/aborted).
//		 */
//		public void setStepType(String type)
//		{
//			this.steptype = type;
//		}
//
//		/**
//		 *  Get the execution result.
//		 *  @return The execution result.
//		 */
//		public Throwable getThrowable()
//		{
//			return throwable;
//		}
//
//		/**
//		 *  Create a string representation of this element.
//		 */
//		public String	toString()
//		{
//			return "PlanExecutionTask("+rplan+")";
//		}
//	}

	
	
}
