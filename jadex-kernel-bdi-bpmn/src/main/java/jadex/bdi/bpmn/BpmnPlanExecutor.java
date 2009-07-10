package jadex.bdi.bpmn;

import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.interpreter.PlanRules;
import jadex.bdi.interpreter.bpmn.parser.BpmnParser;
import jadex.bdi.runtime.IPlanExecutor;
import jadex.bpmn.BpmnXMLReader;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.commons.SUtil;
import jadex.commons.xml.Reader;

import java.io.InputStream;
import java.io.Serializable;

/**
 *  A plan executor for plans modeled in BPMN. These plan steps 
 *  are executed on the agent thread.
 */
public class BpmnPlanExecutor implements IPlanExecutor, Serializable
{
	//-------- attributes --------
	
	/** Hack! Map for the parsed plan body objects (mplan -> bodyImpl) */
//	protected Map planmodelcache;

	//-------- constructor --------

	/**
	 *  Create a new BPMN plan executor.
	 */
	public BpmnPlanExecutor()
	{
//		this.planmodelcache = SCollection.createHashMap();
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
//		MBpmnModel planmodel = (MBpmnModel)planmodelcache.get(mbody);
		
		MBpmnModel bodymodel = null;
		
		if(bodymodel == null && impl!=null)
		{
			try
			{
				// Read the file and parse the state machine
				InputStream isplan = SUtil.getResource(impl, interpreter.getState().getTypeModel().getClassLoader());
				Reader reader = BpmnXMLReader.getReader(); 
				bodymodel = (MBpmnModel)reader.read(isplan, interpreter.getState().getTypeModel().getClassLoader(), null);

				// HACK! Cache parsed body models
//				planmodelcache.put(mbody, planmodel);
			}
			catch(Exception e)
			{
				// Use only RuntimeException from below
				e.printStackTrace();
			}
		}

		if(bodymodel==null)
			throw new RuntimeException("Plan body could not be created: "+impl);

		// Create the body data structure and update state
		BpmnInstance bodyinstance = new BpmnInstance(bodymodel); 
		
		return bodyinstance;
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
		BpmnInstance bodyinstance = (BpmnInstance)interpreter.getState().getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body);
		if(bodyinstance==null)
		{
			bodyinstance = (BpmnInstance)createPlanBody(interpreter, rcapability, rplan);
			interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_body, bodyinstance);
		}
		
		Throwable throwable = null;
		try
		{
			// execute a body step
			if(steptype.equals(OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY))
			{
			
				if(bodyinstance.isReady())
				{
					// Set processing state to "running"
					interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_RUNNING);
					bodyinstance.executeStep();
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
		if(throwable == null && !bodyinstance.isFinished())
		{
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
