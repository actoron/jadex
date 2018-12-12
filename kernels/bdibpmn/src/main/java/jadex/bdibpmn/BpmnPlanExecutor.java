package jadex.bdibpmn;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IPlanExecutor;
import jadex.bdi.runtime.PlanFailureException;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.EventProcessingRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.interpreter.PlanRules;
import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MPool;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.handler.EventEndErrorActivityHandler.EventEndErrorException;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *  A plan executor for plans modeled in BPMN. These plan steps 
 *  are executed on the agent thread.
 */
public class BpmnPlanExecutor implements IPlanExecutor //, Serializable
{
	//-------- attributes --------
	
	/** Model loader (todo: classpath issues?) */
	protected BpmnModelLoader loader;

	//-------- constructor --------

	/**
	 *  Create a new BPMN plan executor.
	 */
	public BpmnPlanExecutor()
	{
		this.loader = new BpmnModelLoader();
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
		String[] imports	= interpreter.getModel(rcapability).getAllImports();
		
		// is rid of parent ok?
		MBpmnModel bodymodel = loader.loadBpmnModel(impl, imports, interpreter.getClassLoader(), 
			new Object[]{interpreter.getModel().getResourceIdentifier(), interpreter.getComponentIdentifier().getRoot()}); 

		if(bodymodel==null)
		{
			throw new RuntimeException("Plan body could not be created: "+impl);
		}
		
		// Check names of pools, if any.
		else
		{
			List	pools	= bodymodel.getPools();
			if(pools!=null && !pools.isEmpty())
			{
				for(int i=0; i<pools.size(); i++)
				{
					String name	= ((MPool)pools.get(i)).getName();
					if(name==null)
					{
						throw new RuntimeException("Pools require a name (e.g. 'Body' or 'Aborted'): "+bodymodel);
					}
					name	= name.trim().toLowerCase();
					if(!OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY.equals(name)
							&& !OAVBDIRuntimeModel.PLANLIFECYCLESTATE_PASSED.equals(name)
						&& !OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED.equals(name)
						&& !OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED.equals(name))
					{
						throw new RuntimeException("Unsupported name of pool. Use one of 'Body', 'Passed', 'Failed', 'Aborted': "+name);
					}
				}
			}	
			/*if(pools==null || pools.size()!=1)
				throw new RuntimeException("Only one pool supported for BPMN plans: "+bodymodel);
			List	lanes	= ((MPool)pools.get(0)).getLanes();
			if(lanes!=null && !lanes.isEmpty())
			{
				for(int i=0; i<lanes.size(); i++)
				{
					String name	= ((MLane)lanes.get(i)).getName();
					if(name==null)
					{
						throw new RuntimeException("Lanes require a name (e.g. 'Body' or 'Aborted'): "+bodymodel);
					}
					name	= name.trim().toLowerCase();
					if(!OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY.equals(name)
							&& !OAVBDIRuntimeModel.PLANLIFECYCLESTATE_PASSED.equals(name)
						&& !OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED.equals(name)
						&& !OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED.equals(name))
					{
						throw new RuntimeException("Unsupported name of lane. Use one of 'Body', 'Passed', 'Failed', 'Aborted': "+name);
					}
				}
			}*/
		}

		// Create the body data structure and update state
		BpmnPlanBodyInstance bodyinstance = new BpmnPlanBodyInstance(bodymodel, interpreter, rcapability, rplan); 
		
		return bodyinstance;
	}

	/**
	 *  Execute a step of a BPMN plan.
	 *  Executing a step should cause the latest event to be handled.
	 *  Will be called by the scheduler for every event to be handled.
	 *  May throw any kind of exception, when the plan execution fails
	 *  @return True, if the plan step was interrupted (interrupted flag).
	 */
	public boolean	executeStep(BDIInterpreter interpreter, Object rcapa, Object rplan, String steptype)	throws Exception
	{
		// Get or create a new plan body for the plan instance info.
		BpmnPlanBodyInstance bodyinstance = (BpmnPlanBodyInstance)interpreter.getState().getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body);
		if(bodyinstance==null)
		{
//			System.out.println("Creating plan body for rplan: "+rplan+" "
//				+interpreter.getState().getAttributeValue(interpreter.getState().getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name));
			bodyinstance = (BpmnPlanBodyInstance)createPlanBody(interpreter, rcapa, rplan);
			interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body, bodyinstance);
		}
		else
		{
			bodyinstance.updateWaitingThreads();
		}
		
		// Find pool to execute.
		String pool = bodyinstance.getPool(steptype);
//		String planname = (String)interpreter.getState().getAttributeValue(interpreter.getState().getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model), OAVBDIMetaModel.modelelement_has_name);
//		System.out.println("Executing plan step: "+rplan+" "+planname+", "+pool+", "+bodyinstance);

		Throwable throwable = null;
		try
		{
			// Abort threads from the previous pool (i.e. body), when the lifecyclestate has changed.
			if(bodyinstance.getLastState()!=null && !bodyinstance.getLastState().equals(steptype))
			{
				String	abortpool	= bodyinstance.getPool(bodyinstance.getLastState());
				if(!BpmnPlanBodyInstance.POOL_UNDEFINED.equals(abortpool))
				{
					Set<ProcessThread>	threads	= bodyinstance.getTopLevelThread().getAllThreads();
					if(threads!=null && !threads.isEmpty())
					{
						for(Iterator<ProcessThread> it=threads.iterator(); it.hasNext(); )
						{
							ProcessThread	thread	= (ProcessThread)it.next();
							if(thread.belongsTo(abortpool, null) && thread.getParent()!=null)
							{
								thread.getParent().removeThread(thread);
							}
						}
					}
				}
			}
			
			// Execute a step.
			if(!BpmnPlanBodyInstance.POOL_UNDEFINED.equals(pool))
			{
				if(bodyinstance.isReady(pool, null))
				{
					// Set processing state to "running"
					interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_RUNNING);
					bodyinstance.setLastState(steptype);
					
					bodyinstance.executeStep(pool, null);
				}
				
				// When the bodyinstance is not ready, it means that a task
				// has completed and notified the handler to continue.
//				else if(/*steptype.equals(bodyinstance.getLastState()) ||*/ !bodyinstance.isFinished(null, lane))
//				{
//					throw new RuntimeException("Invalid plan step: BPMN process instance is not ready: "+bodyinstance);
//				}
			}
		}
		catch(Throwable t)
		{
			// Throwable in body task will be thrown later
			throwable = t;
		}
		// If exception has occurred during step() in notify() of task finishing then exception 
		// is remembered and set here to determine the further plan state.
		Throwable t = (Throwable)interpreter.getState().getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_exception);
		if(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY.equals(interpreter.getState()
			.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate)) && t!=null)
		{
			throwable = t;
		}
		

		// Check for errors / exception / final state
		if(throwable==null && !BpmnPlanBodyInstance.POOL_UNDEFINED.equals(pool) && !bodyinstance.isFinished(pool, null))
		{
			long timeout = bodyinstance.getTimeout();
			Object	wa	= bodyinstance.getWaitAbstraction();
			
			if(bodyinstance.isReady(pool, null))
			{
				// Set waitqueue of plan.
				bodyinstance.updateWaitqueue(wa);

				// Bpmn plan is ready and can directly be executed (no event).
				EventProcessingRules.schedulePlanInstanceCandidate(interpreter.getState(), null, rplan, rcapa);
			}
			else if(timeout!=-1 || wa!=null)
			{
				// Reset waitqueue of plan.
				bodyinstance.updateWaitqueue(null);
				PlanRules.cleanupPlanWait(interpreter.getState(), rcapa, rplan, false);

				PlanRules.initializeWait(wa, timeout, interpreter.getState(), rcapa, rplan);
				interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_WAITING);
			}
			
			// Plan may be waiting for subgoal (todo: put in wa?) 
//			else
//			{
//				throw new RuntimeException("Plan not finished, not ready and not waiting !?: "+bodyinstance);
//			}
		}
		else
		{
			// Exception or final state, finish plan
			PlanRules.endPlanPart(interpreter.getState(), rcapa, rplan, true);
			
			if(steptype.equals(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY))
			{
				// Hack!!! Should not change state?
				// set plan lifecycle state
				interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate,
					throwable==null? OAVBDIRuntimeModel.PLANLIFECYCLESTATE_PASSED : OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED);	

				// Bpmn plan is ready and can directly be executed (no event).
				EventProcessingRules.schedulePlanInstanceCandidate(interpreter.getState(), null, rplan, rcapa);				
			}
			else
			{
				// Set plan processing state.
				interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED);
			}

			// in case of an error / exception, throw it now
			if(throwable instanceof EventEndErrorException)
			{
	    		throw new PlanFailureException(throwable.getMessage(), throwable);
			}
			if(throwable instanceof Exception)
			{
	    		throw (Exception)throwable;
			}
	    	else if(throwable!=null)
	    	{
	    		throw new RuntimeException(throwable);
	    	}
		}

		// return false, step is not interruptible
    	return false;
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
	 *  Block a plan until an event matching the wait abstraction occurs.
	 *  Only used for standard plans, which block during execution.
	 */
	public void eventWaitFor(BDIInterpreter interpreter, Object rplan)
	{
		// todo
	}

	/**
	 *  Called on termination of a plan.
	 *  Free all associated ressources, stop threads, etc.
	 */
	public void cleanup(BDIInterpreter interpreter, Object rplan)
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
	 * /
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
	
	/**
	 *  Get the monitor of a plan.
	 *  @return The monitor.
	 */
	public Object getMonitor(Object rplan)
	{
		return null;
	}
}
