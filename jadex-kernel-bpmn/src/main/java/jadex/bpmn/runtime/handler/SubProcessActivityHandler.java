package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ProcessThreadValueFetcher;
import jadex.bpmn.runtime.ThreadContext;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import jadex.commons.SReflect;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.javaparser.IValueFetcher;

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
		List start = proc.getStartActivities();
		final String	file	= (String)thread.getPropertyValue("file");
		
	
		// Internal subprocess.
		// Todo: cancel timer on normal/exception exit
		if(start!=null && file==null)
		{
//			thread.setWaitingState(ProcessThread.WAITING_FOR_SUBPROCESS);
//			thread.setWaiting(true);
			
			boolean	wait	= true;
			
			if(MSubProcess.SUBPROCESSTYPE_PARALLEL.equals(proc.getSubprocessType()))
			{
				// Todo: use subcontext?
				Iterator	it	= SReflect.getIterator(thread.getPropertyValue("items"));
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
							ProcessThread subthread = new ProcessThread((MActivity)start.get(i), subcontext, instance);
							subthread.setParameterValue("item", value);	// Hack!!! parameter not declared?
							subcontext.addThread(subthread);
						}
					}
				}
			}
			else if(MSubProcess.SUBPROCESSTYPE_LOOPING.equals(proc.getSubprocessType()))
			{
				throw new UnsupportedOperationException("Looping subprocess not yet supported: "+activity+", "+instance);
			}
			else
			{
				ThreadContext subcontext = new ThreadContext(proc, thread);
				thread.getThreadContext().addSubcontext(subcontext);
				for(int i=0; i<start.size(); i++)
				{
					ProcessThread subthread = new ProcessThread((MActivity)start.get(i), subcontext, instance);
					subcontext.addThread(subthread);
				}
			}
			
			if(wait)
			{
				// todo: support more than one timer?
				MActivity	timer	= null;
				List handlers = activity.getEventHandlers();
				for(int i=0; timer==null && handlers!=null && i<handlers.size(); i++)
				{
					MActivity	handler	= (MActivity)handlers.get(i);
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
				instance.getStepHandler(activity).step(activity, instance, thread, null);				
			}
		}
		
		// External subprocess
		else if((start==null || start.isEmpty()) && file!=null)
		{
			// Extract arguments from in/inout parameters.
			final Map	args	= new HashMap();
			List params	= activity.getParameters(new String[]{MParameter.DIRECTION_IN, MParameter.DIRECTION_INOUT});
			if(params!=null && !params.isEmpty())
			{
//				args	= new HashMap();
				for(int i=0; i<params.size(); i++)
				{
					MParameter	param	= (MParameter)params.get(i);
					args.put(param.getName(), thread.getParameterValue(param.getName()));
				}
			}
			
//			System.out.println("start: "+instance.getComponentIdentifier()+" "+file);

			thread.setWaiting(true);
			
			SServiceProvider.getService(instance.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(instance.createResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					IComponentManagementService cms = (IComponentManagementService)result;
					IFuture ret = cms.createComponent(null, file,
						new CreationInfo(null, args, instance.getComponentIdentifier(), false, instance.getModelElement().getAllImports()), 
						new IResultListener()
						{
							public void resultAvailable(final Object result)
							{
								instance.getComponentAdapter().invokeLater(new Runnable()
								{
									public void run()
									{
//										System.out.println("end1: "+instance.getComponentIdentifier()+" "+file);
										
										// Store results in out parameters.
										Map	results	= (Map)result;
										thread.setParameterValue("$results", results);	// Hack???
										
										List	params	= activity.getParameters(new String[]{MParameter.DIRECTION_OUT, MParameter.DIRECTION_INOUT});
										if(params!=null && !params.isEmpty())
										{
											IValueFetcher fetcher	=null;
			
											for(int i=0; i<params.size(); i++)
											{
												MParameter	param	= (MParameter)params.get(i);
												if(param.getInitialValue()!=null)
												{
													if(fetcher==null)
														fetcher	= new ProcessThreadValueFetcher(thread, false, instance.getValueFetcher());
													try
													{
														thread.setParameterValue(param.getName(), param.getInitialValue().getValue(fetcher));
													}
													catch(RuntimeException e)
													{
														throw new RuntimeException("Error evaluating parameter value: "+instance+", "+activity+", "+param.getName()+", "+param.getInitialValue(), e);
													}
												}
												else if(results.containsKey(param.getName()))
												{
													thread.setParameterValue(param.getName(), results.get(param.getName()));
												}
											}
										}
										thread.setNonWaiting();
										instance.getStepHandler(activity).step(activity, instance, thread, null);
									}
								});
							}
							
							public void exceptionOccurred(final Exception exception)
							{
								instance.getComponentAdapter().invokeLater(new Runnable()
								{
									public void run()
									{
//										System.out.println("end2: "+instance.getComponentIdentifier()+" "+file+" "+exception);
										thread.setNonWaiting();
										thread.setException(exception);
										instance.getStepHandler(activity).step(activity, instance, thread, null);
									}
								});
							}
							
							public String toString()
							{
								return "lis: "+instance.getComponentIdentifier()+" "+file;
							}
						});
					
					IResultListener lis = new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							// todo: save component id
						}
						
						public void exceptionOccurred(Exception exception)
						{
						}
					};
					
					ret.addResultListener(lis);
				}
			}));
		}
		
		// Empty subprocess.
		else if((start==null || start.isEmpty()) && file==null)
		{
			// If no activity in sub process, step immediately. 
			instance.getStepHandler(activity).step(activity, instance, thread, null);
		}
		
		// Inconsistent subprocess.
		else
		{
			throw new RuntimeException("External subprocess may not have inner activities: "+activity+", "+instance);
		}
		
	}
}
