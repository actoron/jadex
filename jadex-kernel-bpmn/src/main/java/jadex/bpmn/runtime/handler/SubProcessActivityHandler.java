package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ProcessThreadValueFetcher;
import jadex.bpmn.runtime.ThreadContext;
import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	public void execute(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread)
	{
//		System.out.println(instance.getComponentIdentifier().getLocalName()+": sub "+activity);

		MSubProcess	proc	= (MSubProcess) activity;
		List<MActivity> start = proc.getStartActivities();
		final String	file	= (String)thread.getPropertyValue("file");
	
		// Internal subprocess (when no file is given and has start activities).
		// Todo: cancel timer on normal/exception exit
		if(start!=null && file==null)
		{
//			thread.setWaitingState(ProcessThread.WAITING_FOR_SUBPROCESS);
//			thread.setWaiting(true);
			
			boolean	wait	= true;
			
			if(MSubProcess.SUBPROCESSTYPE_PARALLEL.equals(proc.getSubprocessType()))
			{
				Iterator<Object>	it	= SReflect.getIterator(thread.getPropertyValue("items"));
				// If empty parallel activity (i.e. no items at all) continue process.
				if(!it.hasNext())
				{
					wait	= false;
				}
				else
				{
					ThreadContext subcontext = new ThreadContext(proc, thread);
					thread.getThreadContext().addSubcontext(subcontext);
					while(it.hasNext())
					{
						Object	value	= it.next();
						for(int i=0; i<start.size(); i++)
						{
							ProcessThread subthread = new ProcessThread(thread.getId()+":"+thread.idcnt++, (MActivity)start.get(i), subcontext, instance);
							subthread.setParameterValue("item", value);	// Hack!!! parameter not declared?
							subcontext.addThread(subthread);
//							ComponentChangeEvent cce = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, BpmnInterpreter.TYPE_THREAD, subthread.getClass().getName(), 
//								subthread.getId(), instance.getComponentIdentifier(), instance.getCreationTime(), instance.createProcessThreadInfo(subthread));
//							instance.notifyListeners(cce);
						}
					}
				}
			}
			
			// Todo: support LOOPING in editor.
			else if(MSubProcess.SUBPROCESSTYPE_LOOPING.equals(proc.getSubprocessType()) || thread.hasPropertyValue("items"))
			{
				throw new UnsupportedOperationException("Looping subprocess not yet supported: "+activity+", "+instance);
//				Iterator<Object>	it	= SReflect.getIterator(thread.getPropertyValue("items"));
//				// If empty looping activity (i.e. no items at all) continue process.
//				if(!it.hasNext())
//				{
//					wait	= false;
//				}
//				else
//				{
//					ThreadContext subcontext = new ThreadContext(proc, thread);
//					thread.getThreadContext().addSubcontext(subcontext);
//					boolean	first	= true;
//					while(it.hasNext())
//					{
//						Object	value	= it.next();
//						for(int i=0; i<start.size(); i++)
//						{
//							ProcessThread subthread = new ProcessThread(thread.getId()+":"+thread.idcnt++, (MActivity)start.get(i), subcontext, instance);
//							subthread.setParameterValue("item", value);	// Hack!!! parameter not declared?
//							subcontext.addThread(subthread);
//							if(!first)
//							{
//								subthread.setWaiting(true);
//							}
////							ComponentChangeEvent cce = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, BpmnInterpreter.TYPE_THREAD, subthread.getClass().getName(), 
////								subthread.getId(), instance.getComponentIdentifier(), instance.getCreationTime(), instance.createProcessThreadInfo(subthread));
////							instance.notifyListeners(cce);
//						}
//						first	= false;
//					}
//				}
			}
			else
			{
				ThreadContext subcontext = new ThreadContext(proc, thread);
				thread.getThreadContext().addSubcontext(subcontext);
				for(int i=0; i<start.size(); i++)
				{
					ProcessThread subthread = new ProcessThread(thread.getId()+":"+thread.idcnt++, (MActivity)start.get(i), subcontext, instance);
					subcontext.addThread(subthread);
//					ComponentChangeEvent cce = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, BpmnInterpreter.TYPE_THREAD, subthread.getClass().getName(), 
//						subthread.getId(), instance.getComponentIdentifier(), instance.getCreationTime(), instance.createProcessThreadInfo(subthread));
//					instance.notifyListeners(cce);
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
					instance.getActivityHandler(timer).execute(timer, instance, thread);
				}
				else
				{
					thread.setWaiting(true);
				}
			}
			else
			{
				thread.setNonWaiting();
				instance.step(activity, instance, thread, null);				
			}
		}
		
		// External subprocess
		else if((start==null || start.isEmpty()) && file!=null)
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
			
			instance.getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new DefaultResultListener<IComponentManagementService>()
			{
				public void resultAvailable(IComponentManagementService cms)
				{
					// Todo: If remote remember subprocess and kill on cancel.

					final CreationInfo	info = thread.hasPropertyValue("creation info")? 
						(CreationInfo)thread.getPropertyValue("creation info"): new CreationInfo();
					
					// todo: other properties of creation info like
					// instance name and flags like suspend

					if(info.getArguments()==null && args.size()>0)
						info.setArguments(args);
					
					IComponentIdentifier	parent	= thread.hasPropertyValue("parent")
						? (IComponentIdentifier)thread.getPropertyValue("parent")
						: instance.getComponentIdentifier();
					if(info.getParent()==null && parent!=null)
						info.setParent(parent);
					
					String[] imps = instance.getModelElement().getModelInfo().getAllImports();
					if(info.getImports()==null && imps!=null)
						info.setImports(imps);
						
//					System.out.println("parent is: "+parent.getAddresses());	
						
					IFuture<IComponentIdentifier> ret = cms.createComponent(null, file, info, 
						instance.createResultListener(new IIntermediateResultListener<Tuple2<String, Object>>()
					{
						public void intermediateResultAvailable(Tuple2<String, Object> result)
						{
							Map<String, Object> res = (Map<String, Object>)thread.getParameterValue("$results");
							if(res==null)
							{
								res = new HashMap<String, Object>();
								thread.setParameterValue("$results", res);
							}
							res.put(result.getFirstEntity(), result.getSecondEntity());
//							System.out.println("inter: "+instance.getComponentIdentifier()+" "+file+" "+thread.getParameterValue("$results"));
							
							List<MActivity> handlers = activity.getEventHandlers();
							if(handlers!=null)
							{
								for(int i=0; i<handlers.size(); i++)
								{
									MActivity act = handlers.get(i);
									if(act.getActivityType().equals(MBpmnModel.EVENT_INTERMEDIATE_SIGNAL))
									{
										ProcessThread newthread	= thread.createCopy();
										updateParameters(newthread);
										// todo: allow this, does not work because handler is used for waiting for service calls!
//										newthread.setActivity(act);
										newthread.setLastEdge((MSequenceEdge)act.getOutgoingSequenceEdges().get(0));
										thread.getThreadContext().addThread(newthread);
										ComponentChangeEvent cce = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, BpmnInterpreter.TYPE_THREAD, thread.getClass().getName(), 
											thread.getId(), instance.getComponentIdentifier(), instance.getComponentDescription().getCreationTime(), instance.createProcessThreadInfo(newthread));
										instance.notifyListeners(cce);
									}
								}
							}
						}

						public void finished()
						{
//							System.out.println("end0: "+instance.getComponentIdentifier()+" "+file+" "+thread.getParameterValue("$results"));
							updateParameters(thread);
							
							thread.setNonWaiting();
							instance.step(activity, instance, thread, null);
						}
						
						public void resultAvailable(final Collection<Tuple2<String, Object>> results)
						{
							// Store results in out parameters.
							Map<String, Object> res = new HashMap<String, Object>();
							if(results!=null)
							{
								for(Iterator<Tuple2<String, Object>> it=results.iterator(); it.hasNext(); )
								{
									Tuple2<String, Object> tup = it.next();
									res.put(tup.getFirstEntity(), tup.getSecondEntity());
								}
							}
							thread.setParameterValue("$results", res);	// Hack???
							
//							System.out.println("end1: "+instance.getComponentIdentifier()+" "+file+" "+res);
							
							updateParameters(thread);
							
							thread.setNonWaiting();
							instance.step(activity, instance, thread, null);
						}
						
						public void exceptionOccurred(final Exception exception)
						{
//							System.out.println("end2: "+instance.getComponentIdentifier()+" "+file+" "+exception);
							thread.setNonWaiting();
							thread.setException(exception);
							instance.step(activity, instance, thread, null);
						}
						
						protected void updateParameters(ProcessThread thread)
						{
							Map<String, Object> res = (Map<String, Object>)thread.getParameterValue("$results");
							
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
											fetcher	= new ProcessThreadValueFetcher(thread, false, instance.getFetcher());
										try
										{
											thread.setParameterValue(param.getName(), param.getInitialValue().getValue(fetcher));
										}
										catch(RuntimeException e)
										{
											throw new RuntimeException("Error evaluating parameter value: "+instance+", "+activity+", "+param.getName()+", "+param.getInitialValue(), e);
										}
									}
									else if(res!=null && res.containsKey(param.getName()))
									{
										thread.setParameterValue(param.getName(), res.get(param.getName()));
									}
								}
							}
						}
						
						public String toString()
						{
							return "lis: "+instance.getComponentIdentifier()+" "+file;
						}
					}));
					
					IResultListener<IComponentIdentifier> lis = new IResultListener<IComponentIdentifier>()
					{
						public void resultAvailable(IComponentIdentifier result)
						{
							// todo: save component id
//							System.out.println("created: "+result);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							// Hack!!! Ignore exception, when component already terminated.
							if(!(exception instanceof ComponentTerminatedException)
								|| !instance.getComponentIdentifier().equals(((ComponentTerminatedException)exception).getComponentIdentifier()))
							{
								thread.setNonWaiting();
								thread.setException(exception);
								instance.step(activity, instance, thread, null);
	//							System.out.println("exception: "+exception);
	//							exception.printStackTrace();
							}
						}
					};
					
					ret.addResultListener(lis);
				}
			});
		}
		
		// Empty subprocess.
		else if((start==null || start.isEmpty()) && file==null)
		{
			// If no activity in sub process, step immediately. 
			instance.step(activity, instance, thread, null);
		}
		
		// Inconsistent subprocess.
		else
		{
			throw new RuntimeException("External subprocess may not have inner activities: "+activity+", "+instance);
		}
	}
}
