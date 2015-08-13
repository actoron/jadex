package jadex.bpmn.features.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bpmn.features.IBpmnComponentFeature;
import jadex.bpmn.features.IInternalBpmnComponentFeature;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MIdElement;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MNamedIdElement;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.ExecuteProcessThread;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple3;
import jadex.commons.future.IFuture;

/**
 *  Bpmn execution logic.
 */
public class BpmnExecutionFeature extends ExecutionComponentFeature
{
	/** The started flag. */
	protected boolean started;

	/** The finished flag. */
	protected boolean finishing;
	
	/**
	 *  Create the feature.
	 */
	public BpmnExecutionFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Execute the main activity of the feature.
	 */
	public IFuture<Void> body()
	{
		assert getComponent().getComponentFeature(IExecutionFeature.class).isComponentThread();
		
		IInternalBpmnComponentFeature bcf = (IInternalBpmnComponentFeature)getComponent().getComponentFeature(IBpmnComponentFeature.class);
		
		// Check if triggered by external event
		// eventtype, mactid, event
        Tuple3<String, String, Object> trigger = (Tuple3<String, String, Object>)getComponent()
        	.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get(MBpmnModel.TRIGGER);
        MSubProcess triggersubproc = null;
        MActivity triggeractivity = null;
        
        // Search and add trigger activity for event processes (that have trigger event in a subprocess)
        List<MActivity> startacts = new ArrayList<MActivity>();
        boolean found = false;
        if(getComponent().getConfiguration()!=null)
        {
        	List<MNamedIdElement> elems = getModel().getStartElements(getComponent().getConfiguration());
        	if(elems!=null && !elems.isEmpty())
        	{
        		found = true;
        		for(MNamedIdElement elem: elems)
	        	{
	        		if(elem instanceof MActivity)
	        		{
	        			startacts.add((MActivity)elem);
	        		}
	        		else if(elem instanceof MPool)
	        		{
	        			startacts.addAll(getModel().getStartActivities(elem.getName(), null));
	        		}
	        		else if(elem instanceof MLane)
	        		{
	        			MLane lane = (MLane)elem;
	        			
	        			MIdElement tmp = lane;
	        			for(; tmp!=null && !(tmp instanceof MPool); tmp = getModel().getParent(tmp))
	        			{
	        			}
	        			String poolname = tmp==null? null: ((MPool)tmp).getName();
	        			
	          			startacts.addAll(getModel().getStartActivities(poolname, elem.getName()));
	        		}
	        	}
        	}
        }
        
        if(!found)
        {
        	startacts = getModel().getStartActivities();
        }
        
        Set<MActivity> startevents = startacts!=null ? new HashSet<MActivity>(startacts) : new HashSet<MActivity>();
        if(trigger != null)
        {
        	Map<String, MActivity> allacts = getModel().getAllActivities();
        	triggeractivity = allacts.get(trigger.getSecondEntity());
        	for(Map.Entry<String, MActivity> act : allacts.entrySet())
        	{
        		if(act instanceof MSubProcess)
        		{
        			MSubProcess subproc = (MSubProcess)act;
        			if(subproc.getActivities() != null && subproc.getActivities().contains(triggeractivity));
        			{
        				triggersubproc = subproc;
        				break;
        			}
        		}
        	}
        	
        	startevents.add(triggeractivity);
        }
        
        for(MActivity mact: startevents)
        {
            if(trigger!=null && trigger.getSecondEntity().equals(mact.getId()))
            {
            	if(triggersubproc != null)
            	{
            		ProcessThread thread = new ProcessThread(triggersubproc, bcf.getTopLevelThread(), getComponent(), true);
            		bcf.getTopLevelThread().addThread(thread);
					ProcessThread subthread = new ProcessThread(triggeractivity, thread, getComponent());
					thread.addThread(subthread);
					subthread.setOrCreateParameterValue("$event", trigger.getThirdEntity());
            	}
            	else
            	{
                    ProcessThread thread = new ProcessThread(mact, bcf.getTopLevelThread(), getComponent());
                    thread.setOrCreateParameterValue("$event", trigger.getThirdEntity());
                    bcf.getTopLevelThread().addThread(thread);
            	}
            }
            else if(!MBpmnModel.EVENT_START_MESSAGE.equals(mact.getActivityType())
            	&& !MBpmnModel.EVENT_START_MULTIPLE.equals(mact.getActivityType())
            	&& !MBpmnModel.EVENT_START_RULE.equals(mact.getActivityType())
            	&& !MBpmnModel.EVENT_START_SIGNAL.equals(mact.getActivityType())
            	&& !MBpmnModel.EVENT_START_TIMER.equals(mact.getActivityType()))
            {
                ProcessThread thread = new ProcessThread(mact, bcf.getTopLevelThread(), getComponent());
                bcf.getTopLevelThread().addThread(thread);
            }
        } 
        
        started = true;
        
        return IFuture.DONE;
	}
	
	/**
	 *  Add a check to ensure that no thread gets scheduled twice.
	 */
	protected void addStep(StepInfo step)
	{
		if(DEBUG)
		{
			if(step.getStep() instanceof ExecuteProcessThread)
			{
				ProcessThread thread = ((ExecuteProcessThread)step.getStep()).getThread();
				synchronized(this)
				{
					if(steps!=null)
					{
						for(StepInfo si: steps)
						{
							if(si.getStep() instanceof ExecuteProcessThread)
							{
								if(thread.equals(((ExecuteProcessThread)si.getStep()).getThread()))
								{
									throw new RuntimeException("Internal error, must not schedule thread twice");
								}
							}
						}
					}
				}
			}
		}
		
		super.addStep(step);
		
//		synchronized(this)
//		{
//			System.out.println("steps: "+steps);
//		}
	}
	
	/**
	 *  Components with autonomous behavior may override this method
	 *  to implement a recurring execution cycle.
	 *  @return true, if the execution should continue, false, if the component may become idle. 
	 */
	protected boolean	executeCycle()
	{
		if(!started)
			return false;
		
		BpmnComponentFeature bcf = (BpmnComponentFeature)getComponent().getComponentFeature(IBpmnComponentFeature.class);

//		if(!bcf.isFinished() && bcf.isReady())
//		{
//			if(getComponent().getComponentFeature0(IMonitoringComponentFeature.class)!=null && getComponent().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
//			{
//				getComponent().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(new MonitoringEvent(
//					getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), 
//					IMonitoringEvent.EVENT_TYPE_CREATION+"."+IMonitoringEvent.SOURCE_CATEGORY_EXECUTION, 
//					System.currentTimeMillis(), PublishEventLevel.FINE), PublishTarget.TOALL);
//			}
//			
////			executeStep(pool, lane);
//			executeStep(null, null);
//			
//			if(getComponent().getComponentFeature0(IMonitoringComponentFeature.class)!=null && getComponent().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
//			{
//				getComponent().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(new MonitoringEvent(
//					getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), 
//					IMonitoringEvent.EVENT_TYPE_DISPOSAL+"."+IMonitoringEvent.SOURCE_CATEGORY_EXECUTION, 
//					System.currentTimeMillis(), PublishEventLevel.FINE), PublishTarget.TOALL);
//			}
//		}
		
//		System.out.println("After step: "+this.getComponentAdapter().getComponentIdentifier().getName()+" "+isFinished(pool, lane));
		
		// todo: started
		if(!finishing && bcf.isFinished() && !getModel().isKeepAlive() && started 
			&& getModel().getEventSubProcessStartEvents().isEmpty()) // keep alive also process with event subprocesses
		{
//				System.out.println("terminating: "+getComponentIdentifier());
			finishing = true;
//				((IComponentManagementService)variables.get("$cms")).destroyComponent(adapter.getComponentIdentifier());
			
			IComponentManagementService cms = SServiceProvider.getLocalService(getComponent(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			cms.destroyComponent(getComponent().getComponentIdentifier()); // todo: listener?
		}
		
//			System.out.println("Process wants: "+this.getComponentAdapter().getComponentIdentifier().getLocalName()+" "+!isFinished(null, null)+" "+isReady(null, null));
		
		return !bcf.isFinished() && bcf.isReady();
	}
//	
//	/**
//	 *  Execute one step of the process.
//	 *  @param pool	The pool to be executed or null for any.
//	 *  @param lane	The lane to be executed or null for any. Nested lanes may be addressed by dot-notation, e.g. 'OuterLane.InnerLane'.
//	 */
//	public void executeStep(String pool, String lane)
//	{
////		assert getComponent().getComponentFeature(IExecutionFeature.class).isComponentThread();
//		
//		BpmnComponentFeature bcf = (BpmnComponentFeature)getComponent().getComponentFeature(IBpmnComponentFeature.class);
//
//		if(bcf.isFinished(pool, lane))
//			throw new UnsupportedOperationException("Cannot execute a finished process: "+this);
//		
//		if(!bcf.isReady(pool, lane))
//			throw new UnsupportedOperationException("Cannot execute a process with only waiting threads: "+this);
//		
//		ProcessThread thread = null;
//		
//		// Selects step according to stepinfo, is thread id :-(
//		
////		String stepinfo = null;
////		if(getComponent().getComponentDescription().getState().equals(IComponentDescription.STATE_SUSPENDED))
////		{
////			CMSComponentDescription desc = (CMSComponentDescription)getComponent().getComponentDescription();
////			stepinfo = desc.getStepInfo();
////			if(stepinfo!=null)
////			{
////				desc.setStepInfo(null);
////			}
////		}
////		
////		if(stepinfo!=null)
////		{
////			thread = bcf.getTopLevelThread().getThread(stepinfo);
////			if(thread.isWaiting())
////			{
////				thread = null;
////			}
////		}
//		
//		if(thread==null)
//		{
//			thread = bcf.getTopLevelThread().getExecutableThread(pool, lane);
//		}
//		
//		// Thread may be null when external entry has not changed waiting state of any active plan. 
//		if(thread!=null)
//		{
////			if("End".equals(thread.getActivity().getName()))
////				System.out.println("end: "+thread);
//			// Update parameters based on edge inscriptions and initial values.
//			thread.updateParametersBeforeStep(getComponent());
//			
//			// Find handler and execute activity.
//			IActivityHandler handler = (IActivityHandler)bcf.getActivityHandler(thread.getActivity());
////			IActivityHandler handler = (IActivityHandler)activityhandlers.get(thread.getActivity().getActivityType());
//			if(handler==null)
//				throw new UnsupportedOperationException("No handler for activity: "+thread);
//
////			System.out.println("step: "+getComponentIdentifier()+" "+thread.getId()+" "+thread.getActivity()+" "+thread.getActivity().getId());
//			MActivity act = thread.getActivity();
//			
////			notifyListeners(createActivityEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, thread, thread.getActivity()));
//			if(getComponent().getComponentFeature0(IMonitoringComponentFeature.class)!=null && getComponent().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
//			{
//				getComponent().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(bcf.createActivityEvent(IMonitoringEvent.EVENT_TYPE_CREATION, thread, thread.getActivity()), PublishTarget.TOALL);
//			}
//			
////			thread = handler.execute(act, this, thread);
//			handler.execute(act, getComponent(), thread);
//
//			// Moved to StepHandler
////			thread.updateParametersAfterStep(act, this);
//			
//			// Check if thread now waits for a message and there is at least one in the message queue.
//			// Todo: check if thread directly or indirectly (multiple events!) waits for a message event before checking waitqueue
//			List<Object> messages = bcf.getMessages();
//			if(thread.isWaiting() && messages.size()>0 /*&& MBpmnModel.EVENT_INTERMEDIATE_MESSAGE.equals(thread.getActivity().getActivityType()) 
//				&& (thread.getPropertyValue(EventIntermediateMessageActivityHandler.PROPERTY_MODE)==null 
//					|| EventIntermediateMessageActivityHandler.MODE_RECEIVE.equals(thread.getPropertyValue(EventIntermediateMessageActivityHandler.PROPERTY_MODE)))*/)
//			{
//				boolean processed = false;
//				for(int i=0; i<messages.size() && !processed; i++)
//				{
//					Object message = messages.get(i);
//					IFilter<Object> filter = thread.getWaitFilter();
//					if(filter!=null && filter.filter(message))
//					{
//						processed = true;
//						messages.remove(i);
////						System.out.println("Dispatched from waitqueue: "+messages.size()+" "+System.identityHashCode(message)+", "+message);
//						bcf.notify(thread.getActivity(), thread, message);
//					}
//				}
//			}
//			List<IConnection> streams = bcf.getStreams();
//			if(thread.isWaiting() && streams.size()>0) 
//			{
//				boolean processed = false;
//				for(int i=0; i<streams.size() && !processed; i++)
//				{
//					Object stream = streams.get(i);
//					IFilter<Object> filter = thread.getWaitFilter();
//					if(filter!=null && filter.filter(stream))
//					{
//						processed = true;
//						streams.remove(i);
//						bcf.notify(thread.getActivity(), thread, stream);
////						System.out.println("Dispatched from stream: "+messages.size()+" "+message);
//					}
//				}
//			}
//			
//			if(getComponent().getComponentFeature0(IMonitoringComponentFeature.class)!=null && thread.getActivity()!=null && getComponent().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
//			{
//				getComponent().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(bcf.createThreadEvent(IMonitoringEvent.EVENT_TYPE_MODIFICATION, thread), PublishTarget.TOALL);
//			}
////			notifyListeners(createThreadEvent(IComponentChangeEvent.EVENT_TYPE_MODIFICATION, thread));
//		}
//	}
	
	/**
	 *  Get the model.
	 */
	protected MBpmnModel getModel()
	{
		return (MBpmnModel)getComponent().getModel().getRawModel();
	}
	
//	/**
//	 *  Called before blocking the component thread.
//	 */
//	protected void	beforeBlock()
//	{
//		RPlan	rplan	= ExecutePlanStepAction.RPLANS.get();
//		if(rplan!=null)
//		{
//			rplan.beforeBlock();
//		}
//	}
//	
//	/**
//	 *  Called after unblocking the component thread.
//	 */
//	protected void	afterBlock()
//	{
//		RPlan	rplan	= ExecutePlanStepAction.RPLANS.get();
//		if(rplan!=null)
//		{
//			rplan.afterBlock();
//		}
//	}
	
	/**
	 *  Kernel specific test if the step is a breakpoint.
	 */
	public boolean testIfBreakpoint(String[] breakpoints)
	{
		boolean	isatbreakpoint	= false;
		Set<String>	bps	= new HashSet<String>(Arrays.asList(breakpoints));	// Todo: cache set across invocations for speed?
		
		IInternalBpmnComponentFeature bcf = (IInternalBpmnComponentFeature)getComponent().getComponentFeature(IBpmnComponentFeature.class);

		for(Iterator<ProcessThread> it = bcf.getTopLevelThread().getAllThreads().iterator(); !isatbreakpoint && it.hasNext(); )
		{
			ProcessThread	pt	= it.next();
			
			isatbreakpoint	= bps.contains(pt.getActivity().getId());
		}
		
		return isatbreakpoint;
	}
}
