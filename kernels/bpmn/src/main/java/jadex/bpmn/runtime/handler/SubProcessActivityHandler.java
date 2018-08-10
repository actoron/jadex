package jadex.bpmn.runtime.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MDataEdge;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.IActivityHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ProcessThreadValueFetcher;
import jadex.bridge.ClassInfo;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSIntermediateResultEvent;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSStatusEvent;
import jadex.commons.IResultCommand;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.future.IIntermediateResultListener;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;

/**
 *  Handler for (embedded) sub processes.
 */
public class SubProcessActivityHandler extends DefaultActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(final MActivity activity, final IInternalAccess instance, final ProcessThread thread)
	{
//		System.out.println(instance.getComponentIdentifier().getLocalName()+": sub "+activity);

		MSubProcess	proc	= (MSubProcess)activity;
		final List<MActivity> start = proc.getStartActivities();
		String tmpfile = (String)thread.getPropertyValue("file");
		if(tmpfile == null)
		{
			tmpfile = (String)thread.getPropertyValue("filename");
		}
		final String	file	= tmpfile;
	
		// Internal subprocess (when no file is given and has start activities).
		// Todo: cancel timer on normal/exception exit
		if(start!=null && file==null)
		{
//			thread.setWaitingState(ProcessThread.WAITING_FOR_SUBPROCESS);
//			thread.setWaiting(true);
			
			boolean wait	= true;
			
			if(MSubProcess.SUBPROCESSTYPE_PARALLEL.equals(proc.getSubprocessType()))
			{
				final String itername = proc.getPropertyValue(MSubProcess.MULTIINSTANCE_ITERATOR).getValue();
				Object val = thread.getParameterValue(itername);
				final Iterator<Object> it = SReflect.getIterator(val);
		
//				System.out.println("parallel: "+thread.getInstance().getComponentIdentifier().getLocalName()+" "+thread.getId()+" "+activity+" "+it.hasNext()+" "+val);
				
				// If empty parallel activity (i.e. no items at all) continue process.
				if(!it.hasNext())
				{
					wait = false;
				}
				else
				{
					while(it.hasNext())
					{
						Object	value	= it.next();
						for(int i=0; i<start.size(); i++)
						{
							ProcessThread subthread = new ProcessThread((MActivity)start.get(i), thread, instance, false);
							subthread.setOrCreateParameterValue(itername, value);	// Hack!!! parameter not declared?
							thread.addThread(subthread);
//							System.out.println("val in t: "+subthread+" "+itername+"="+value);
						}
					}
				}
			}
			else if(MSubProcess.SUBPROCESSTYPE_SEQUENTIAL.equals(proc.getSubprocessType()))// || thread.hasPropertyValue("items"))
			{
//				throw new UnsupportedOperationException("Looping subprocess not yet supported: "+activity+", "+instance);
				final String itername = proc.getPropertyValue(MSubProcess.MULTIINSTANCE_ITERATOR).getValue();
				Object val = thread.getParameterValue(itername);
				final Iterator<Object> it = SReflect.getIterator(val);
				// If empty looping activity (i.e. no items at all) continue process.
				
				IResultCommand<Boolean, Void> cmd = new IResultCommand<Boolean, Void>()
				{
					public Boolean execute(Void args)
					{
						Boolean ret = it.hasNext()? Boolean.TRUE: Boolean.FALSE;
						if(it.hasNext())
						{
							Object elem = it.next();
							for(MActivity st: start)
							{
								ProcessThread subthread = new ProcessThread(st, thread, instance, false);
								thread.addThread(subthread);
								subthread.setOrCreateParameterValue(itername, elem); // Hack!!! parameter not declared?
							}
						}
						return ret;
					}
				};
				
				// After all subthreads have finished set wait to false and continue main
				if(!cmd.execute(null).booleanValue())
				{
					wait = false;
				}
				else
				{
					thread.setLoopCommand(cmd);
				}
			}
			// just simple process without multi property
			else
			{
				for(int i=0; i<start.size(); i++)
				{
					ProcessThread subthread = new ProcessThread((MActivity)start.get(i), thread, instance, false);
					thread.addThread(subthread);
				}
			}	
			
			if(wait)
			{
				// todo: support more than one timer?
				MActivity	timer	= null;
				List<MActivity> handlers = activity.getEventHandlers();
				for(int i=0; timer==null && handlers!=null && i<handlers.size(); i++)
				{
					MActivity	handler	= handlers.get(i);
					if(handler.getActivityType().equals("EventIntermediateTimer"))
					{
						timer	= handler;
					}
				}
				
				if(timer!=null)
				{
					final IActivityHandler th = getBpmnFeature(instance).getActivityHandler(timer);
					// handler sets timer as waitinfo (should maybe add cancelables)
					th.execute(timer, instance, thread);
				}
				else
				{
					thread.setWaiting(true);
				}
			}
			else
			{
				// Restart the main thread and step
				thread.setNonWaiting();
				
				getBpmnFeature(instance).step(activity, instance, thread, null);				
			}
		}
		
		// External subprocess
		else if((start==null || start.isEmpty()) && file!=null && file.length()>0)
		{
			// Extract arguments from in/inout parameters.
			final Map<String, Object>	args	= new HashMap<String, Object>();
			List<MParameter> params	= activity.getParameters(new String[]{MParameter.DIRECTION_IN, MParameter.DIRECTION_INOUT});
			if(params!=null && !params.isEmpty())
			{
//				args	= new HashMap();
				for(int i=0; i<params.size(); i++)
				{
					MParameter	param	= params.get(i);
					args.put(param.getName(), thread.getParameterValue(param.getName()));
				}
			}
			
//			System.out.println("start: "+instance.getComponentIdentifier()+" "+file);

			thread.setWaiting(true);
			
//			IComponentManagementService cms = instance.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IComponentManagementService.class));
			// Todo: If remote remember subprocess and kill on cancel.

			final CreationInfo	info = thread.hasPropertyValue("creation info")? 
				(CreationInfo)thread.getPropertyValue("creation info"): new CreationInfo();
			
			// todo: other properties of creation info like
			// instance name and flags like suspend

			if(info.getArguments()==null && args.size()>0)
				info.setArguments(args);
			
			IComponentIdentifier	parent	= thread.hasPropertyValue("parent")
				? (IComponentIdentifier)thread.getPropertyValue("parent")
				: instance.getId();
			if(info.getParent()==null && parent!=null)
				info.setParent(parent);
			
			String[] imps = instance.getModel().getAllImports();
			if(info.getImports()==null && imps!=null)
				info.setImports(imps);
			info.setFilename(file);	
//					System.out.println("parent is: "+parent.getAddresses());	

			instance.createComponentWithResults(null, info)
				.addResultListener(instance.getFeature(IExecutionFeature.class).createResultListener(new IIntermediateResultListener<CMSStatusEvent>()
			{
				protected SubprocessResultHandler handler = new SubprocessResultHandler(thread, activity);	
					
				public void intermediateResultAvailable(CMSStatusEvent cse)
				{
					if(cse instanceof CMSIntermediateResultEvent)
					{
						String	param	= ((CMSIntermediateResultEvent)cse).getName();
						Object	value	= ((CMSIntermediateResultEvent)cse).getValue();
						
						if(activity.getParameters()!=null && activity.getParameters().get(param)!=null)
						{
							String	dir	= activity.getParameters().get(param).getDirection();
							if(MParameter.DIRECTION_INOUT.equals(dir) || MParameter.DIRECTION_OUT.equals(dir))
							{
								thread.setParameterValue(param, value);
							}
						}
						
						// todo: need to distinguish between collection and normal parameters
						handler.handleProcessResult(param, null, value);
					}
				}
				
				public void finished()
				{
//							System.out.println("end0: "+instance.getComponentIdentifier()+" "+file+" "+thread.getParameterValue("$results"));
					handler.updateParameters(thread, activity);
					
					thread.setNonWaiting();
					getBpmnFeature(instance).step(activity, instance, thread, null);
				}
				
				public void resultAvailable(Collection<CMSStatusEvent> cses)
				{
					for(CMSStatusEvent cse: cses)
					{
						intermediateResultAvailable(cse);
					}
					finished();
				}
				
				public void exceptionOccurred(final Exception exception)
				{
					// Hack!!! Ignore exception, when component already terminated.
					if(!(exception instanceof ComponentTerminatedException)
						|| !instance.getId().equals(((ComponentTerminatedException)exception).getComponentIdentifier()))
					{
//								System.out.println("end2: "+instance.getComponentIdentifier()+" "+file+" "+exception);
//								exception.printStackTrace();
						thread.setNonWaiting();
						thread.setException(exception);
						getBpmnFeature(instance).step(activity, instance, thread, null);
					}
				}
				
				public String toString()
				{
					return "lis: "+instance.getId()+" "+file;
				}
			}));
		}
		
		// Empty subprocess.
		else if((start==null || start.isEmpty()) && (file==null || file.length()==0))
		{
			// If no activity in sub process, step immediately. 
			getBpmnFeature(instance).step(activity, instance, thread, null);
		}
		
		// Inconsistent subprocess.
		else
		{
			throw new RuntimeException("External subprocess may not have inner activities: "+activity+", "+instance);
		}
	}

	
	public static class SubprocessResultHandler
	{
		protected ProcessThread thread;
		protected MActivity activity;
		
//		protected boolean finished = false;
//		protected int opencalls = 0;
		final List<ProcessThread> queue;

		/**
		 * 
		 */
		public SubprocessResultHandler(ProcessThread thread, MActivity activity)
		{
			this.thread = thread;
			this.activity = activity;
			this.queue = new ArrayList<ProcessThread>();
		}
		
		/**
		 * 
		 */
		public void handleProcessResult(String param, Object key, Object value)
		{
//			opencalls++;

			// Todo: event handlers should also react to internal subprocesses???
			List<MActivity> handlers = activity.getEventHandlers();
			
			MActivity handler = null;
			if(handlers!=null)
			{
				for(int i=0; i<handlers.size() && handler==null; i++)
				{
					MActivity act = handlers.get(i);
					
					if(act.isMessageEvent())
					{
						String trig = null;
						if(act.hasProperty(MActivity.RESULTNAME))
						{
							trig = (String)act.getPropertyValueString(MActivity.RESULTNAME);
						}
						if(trig == null || param.equals(trig))
						{
							if(value!=null && act.hasProperty(MActivity.RESULTTYPE))
							{
								trig = (String)act.getPropertyValueString(MActivity.RESULTTYPE);
								String typename = (String)SJavaParser.parseExpression(act.getPropertyValue(MActivity.RESULTTYPE), thread.getInstance().getModel().getAllImports(), null).getValue(thread.getInstance().getFetcher());
								Class<?> type = new ClassInfo(typename).getType(thread.getInstance().getClassLoader());
								if(SReflect.isSupertype(type, value.getClass()))
								{
									handler = act;
								}
							}
							else
							{
								handler = act;
							}
						}
					}
					else
					{
						String trig = null;
						if(act.hasProperty(MBpmnModel.SIGNAL_EVENT_TRIGGER))
						{
							trig = (String)thread.getPropertyValue(MBpmnModel.SIGNAL_EVENT_TRIGGER, act);
						}
						
						if(act.getActivityType().equals(MBpmnModel.EVENT_INTERMEDIATE_SIGNAL) &&
						   (trig == null || param.equals(trig)))
						{
							handler = act;
						}
					}
				}
			}		
	
			if(handler!=null)
			{
				final boolean isseq = handler.hasProperty(MActivity.ISSEQUENTIAL);
				
				// parent in seq mode is subproc thread to let it wait until all handler processing is done (hack?)
				ProcessThread newthread = new ProcessThread(handler, isseq? thread: thread.getParent(), thread.getInstance())
				{
					public void notifyFinished() 
					{
//						opencalls--;
						
						if(isseq)
						{
							queue.remove(this);
							ProcessThread next = queue.size()>0? queue.get(0): null;
							if(next!=null)
							{
								next.setWaiting(false);
							}
//							else if(opencalls==0 && finished)
//							{
////								if(fresultparam!=null)
////									context.setParameterValue(fresultparam, results);
//								ret.setResult(null);
//							}
						}
					}
				};
				thread.copy(newthread);
//				ProcessThread newthread	= thread.createCopy();
				
//				updateParameters(newthread, activity);
				
				if(isseq)
				{
					// Set waiting if not first thread
					if(queue.size()>0)
					{	
						newthread.setWaiting(true);
					}
					queue.add(newthread);
				}
				
				newthread.setActivity(handler);
				if(handler.hasParameter(MActivity.RETURNPARAM))
				{
					newthread.setParameterValue(MActivity.RETURNPARAM, value);
				}
				
				// add newthread on suprocess thread in case of sequential execution.
				// this will let the subprocess wait until all child processes have
				// been finished - is kind of a hack
				if(isseq)
				{
					thread.addThread(newthread);
				}
				else
				{
					thread.getParent().addThread(newthread);
				}
			}
		}
		
		/**
		 *  Update the parameter values after a step.
		 * @param activity
		 * @param thread
		 * @param instance
		 */
		protected static void updateParameters(ProcessThread thread, MActivity activity)
		{
			List<MParameter>	params	= activity.getParameters(new String[]{MParameter.DIRECTION_OUT, MParameter.DIRECTION_INOUT});
			if(params!=null && !params.isEmpty())
			{
				IValueFetcher fetcher	=null;
	
				for(int i=0; i<params.size(); i++)
				{
					MParameter	param	= params.get(i);
					if(param.getInitialValue()!=null)
					{
						if(fetcher==null)
							fetcher	= new ProcessThreadValueFetcher(thread, false, thread.getInstance().getFetcher());
						try
						{
							thread.setParameterValue(param.getName(), ((IParsedExpression)param.getInitialValue().getParsed()).getValue(fetcher));
						}
						catch(RuntimeException e)
						{
							throw new RuntimeException("Error evaluating parameter value: "+thread+", "+activity+", "+param.getName()+", "+param.getInitialValue(), e);
						}
					}
				}
				
				if(activity.getOutgoingDataEdges() != null)	
				{
					for(MDataEdge de : activity.getOutgoingDataEdges())
					{
						thread.setDataEdgeValue(de.getId(), thread.getParameterValue(de.getSourceParameter()));
					}
				}
			}
		}
	}
}
