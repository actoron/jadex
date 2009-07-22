package jadex.bdi.bpmn;

import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.interpreter.EventProcessingRules;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.interpreter.PlanRules;
import jadex.bdi.runtime.IPlanExecutor;
import jadex.bpmn.BpmnXMLReader;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MPool;
import jadex.bpmn.runtime.ProcessThread;
import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.xml.Reader;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  A plan executor for plans modeled in BPMN. These plan steps 
 *  are executed on the agent thread.
 */
public class BpmnPlanExecutor implements IPlanExecutor, Serializable
{
	//-------- attributes --------
	
	/** Hack! Map for the parsed plan body objects (mplan -> bodyImpl) */
	protected Map planmodelcache;

	//-------- constructor --------

	/**
	 *  Create a new BPMN plan executor.
	 */
	public BpmnPlanExecutor()
	{
		this.planmodelcache = SCollection.createHashMap();
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
		MBpmnModel bodymodel = (MBpmnModel)planmodelcache.get(mbody);
		
		
		if(bodymodel == null && impl!=null)
		{
			try
			{
				// Read the file and parse the state machine
				bodymodel = (MBpmnModel)BpmnXMLReader.read(impl, interpreter.getState().getTypeModel().getClassLoader(), null);

				// HACK! Cache parsed body models
				planmodelcache.put(mbody, bodymodel);
			}
			catch(Exception e)
			{
				// Use only RuntimeException from below
				e.printStackTrace();
			}
		}

		if(bodymodel==null)
		{
			throw new RuntimeException("Plan body could not be created: "+impl);
		}
		
		// Check names of lanes, if any.
		else
		{
			List	pools	= bodymodel.getPools();
			if(pools==null || pools.size()!=1)
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
			}
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
			bodyinstance = (BpmnPlanBodyInstance)createPlanBody(interpreter, rcapa, rplan);
			interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body, bodyinstance);
		}
		else
		{
			bodyinstance.updateWaitingThreads();
		}
		
		// Find lane to execute.
		String lane = bodyinstance.getLane(steptype);
//		System.out.println("Executing plan step: "+rplan+", "+lane+", "+bodyinstance);
		Throwable throwable = null;
		try
		{
			// Abort threads from the previous lane (i.e. body), when the lifecyclestate has changed.
			if(bodyinstance.getLastState()!=null && !bodyinstance.getLastState().equals(steptype))
			{
				String	abortlane	= bodyinstance.getLane(bodyinstance.getLastState());
				if(!BpmnPlanBodyInstance.LANE_UNDEFINED.equals(abortlane))
				{
					Set	threads	= bodyinstance.getThreadContext().getAllThreads();
					if(threads!=null && !threads.isEmpty())
					{
						for(Iterator it=threads.iterator(); it.hasNext(); )
						{
							ProcessThread	thread	= (ProcessThread)it.next();
							if(thread.belongsTo(null, abortlane))
							{
								thread.getThreadContext().removeThread(thread);
							}
						}
					}
				}
			}
			
			// Find lane to execute.
//			System.out.println("Executing plan step: "+rplan+", "+lane+", "+bodyinstance);
			
			// Execute a step.
			if(!BpmnPlanBodyInstance.LANE_UNDEFINED.equals(lane))
			{
				if(bodyinstance.isReady(null, lane))
				{
					// Set processing state to "running"
					interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_RUNNING);
					bodyinstance.setLastState(steptype);
					bodyinstance.executeStep(null, lane);
				}
				else if(/*steptype.equals(bodyinstance.getLastState()) ||*/ !bodyinstance.isFinished(null, lane))
				{
					throw new RuntimeException("Invalid plan step: BPMN process instance is not ready: "+bodyinstance);
				}
			}
		}
		catch(Throwable t)
		{
			// Throwable in body task will be thrown later
			throwable = t;
		}
		
		// check for errors / exception / final state
		if(throwable==null && !BpmnPlanBodyInstance.LANE_UNDEFINED.equals(lane) && !bodyinstance.isFinished(null, lane))
		{
			long	timeout	= bodyinstance.getTimeout();
			Object	wa	= bodyinstance.getWaitAbstraction();
			
			// Set waitqueue of plan.
			interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueuewa, wa);
			
			if(bodyinstance.isReady(null, lane))
			{
				// Bpmn plan is ready and can directly be executed (no event).
				EventProcessingRules.schedulePlanInstanceCandidate(interpreter.getState(), null, rplan, rcapa);
			}
			else if(timeout!=-1 || wa!=null)
			{
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
			if(throwable instanceof Exception)
			{
	    		throw (Exception) throwable;
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
}
