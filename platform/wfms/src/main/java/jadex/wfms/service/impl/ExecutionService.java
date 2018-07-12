package jadex.wfms.service.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.ICMSComponentListener;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.wfms.IProcess;
import jadex.wfms.client.ClientInfo;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.ProcessResourceInfo;
import jadex.wfms.service.listeners.IAuthenticationListener;
import jadex.wfms.service.listeners.IProcessListener;
import jadex.wfms.service.listeners.ProcessEvent;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 *  The meta execution service wraps all specific process execution services.
 */
@Service
public class ExecutionService implements IExecutionService
{
	//-------- attributes --------
	
	/** Component access. */
	@ServiceComponent
	protected IInternalAccess ia;
	
	/** The service identifier */
	@ServiceIdentifier
	protected IServiceIdentifier id;
	
	/** Running process instances (id -> IProcess) */
	protected Map processes;
	
	/** Counter for instances of processes (ILoadableComponentModel -> num)*/
	protected Map instancecnt;
	
	/** The process listeners */
	private Map<IComponentIdentifier, Set<IProcessListener>> procListeners;
	
	private SecureRandom random = new SecureRandom();
	
	//-------- constructors --------
	
	/**
	 *  Create a new execution service.
	 */
	public ExecutionService()
	{
		this.processes = new HashMap();
	}
	
	//-------- methods --------
	
	/**
	 *  Load a process model.
	 *  @param info The process resource information.
	 *  @return The process model.
	 */
	public IFuture<IModelInfo> loadModel(final ProcessResourceInfo info)
	{
		
		final Future<IModelInfo> ret = new Future<IModelInfo>();
		if (!info.getExecutionServiceId().equals(id))
		{
			ia.getServiceContainer().getServices("execution_services").addResultListener(new ExceptionDelegationResultListener<Collection<Object>, IModelInfo>(ret)
			{
				public void customResultAvailable(Collection<Object> result)
				{
					IExecutionService targetservice = null;
					for (Object obj : result)
					{
						IExecutionService service = (IExecutionService) obj;
						if (((IService) service).getId().equals(info.getExecutionServiceId()))
						{
							targetservice = service;
							break;
						}
					}
					if (targetservice == null)
					{
						ret.setException(new RuntimeException("Execution Service on component " + 
							String.valueOf(ia.getComponentIdentifier()) +
							" is unable to find resource managed by " +
							String.valueOf(info.getRepositoryId())));
					}
					else
					{
						targetservice.loadModel(info).addResultListener(new DelegationResultListener<IModelInfo>(ret));
					}
				}
			});
			
		}
		else
		{
			SComponentFactory.loadModel(ia.getExternalAccess(), info.getPath(), info.getResourceId()).addResultListener(new DelegationResultListener<IModelInfo>(ret));
		}
		return ret;
	}
	
	/**
	 *  Start a process instance.
	 *  
	 *  @param info The process resource information.
	 *  @param id ID of the process instance
	 *  @param arguments arguments for the process
	 *  @return assigned process instance ID
	 */
	public IFuture<IComponentIdentifier> startProcess(final ProcessResourceInfo info, Object id, final Map arguments)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		ia.getServiceContainer().getService("cms").addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService) result;
				
				CreationInfo ci = new CreationInfo(null, arguments, ia.getComponentIdentifier(), true);
				// Todo: what is platform loader for!?
//				ci.setPlatformloader(true);
				String prefix = info.getPath().substring(Math.max(info.getPath().lastIndexOf("/"), 0) + 1);
				prefix = prefix.substring(0, Math.min(prefix.lastIndexOf("."), prefix.length()));
				ByteBuffer b = ByteBuffer.allocate(8);
				b.putLong(random.nextLong());
				//prefix + "_" + Base64.encodeBytes(b.array())
				cms.createComponent(null, info.getPath(), ci, null).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final IComponentIdentifier id = ((IComponentIdentifier) result);
						cms.addComponentListener(id, new ICMSComponentListener()
						{
							public IFuture componentRemoved(IComponentDescription desc, Map results)
							{
								fireProcessFinished(id);
								return IFuture.DONE;
							}
							
							public IFuture componentChanged(IComponentDescription desc)
							{
								return IFuture.DONE;
							}
							
							public IFuture componentAdded(IComponentDescription desc)
							{
								return IFuture.DONE;
							}
						});
						
						ret.setResult(id);
						cms.resumeComponent(id);
					}
				}));
			}
		}));
		
		return ret;
	}

	/**
	 *  Stop a process instance.
	 *  @param name The model name.
	 */
	public void stopProcess(IProcess id)
	{
		//TODO: implement
	}
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param info The process resource information.
	 *  @return True, if model can be loaded.
	 */
	public IFuture<Boolean> isLoadable(ProcessResourceInfo info)
	{
		return SComponentFactory.isLoadable(ia.getExternalAccess(), info.getPath(), info.getResourceId());
	}
	
	/**
	 * Adds a process listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture<Void> addProcessListener(IComponentIdentifier client, IProcessListener listener)
	{
		Map<IComponentIdentifier, Set<IProcessListener>> processListeners =  getListeners();
		Future ret = new Future();
		
		Set<IProcessListener> listeners = processListeners.get(client);
		if (listeners == null)
		{
			listeners = new HashSet<IProcessListener>();
			processListeners.put(client, listeners);
		}
		listeners.add(listener);
		
		ret.setResult(null);
		return ret;
	}
	
	/**
	 * Removes a process listener from the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture<Void> removeProcessListener(IComponentIdentifier client, IProcessListener listener)
	{
		Map<IComponentIdentifier, Set<IProcessListener>> processListeners =  getListeners();
		Future ret = new Future();
		Set<IProcessListener> listeners = processListeners.get(client);
		if (listeners != null)
			listeners.remove(listener);
		ret.setResult(null);
		return ret;
	}
	
	protected Map<IComponentIdentifier, Set<IProcessListener>> getListeners()
	{
		if (procListeners == null)
		{
			procListeners = new HashMap<IComponentIdentifier, Set<IProcessListener>>();
			ia.getServiceContainer().getService("aaa_service").addResultListener(ia.createResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					IAAAService as = (IAAAService) result;
					final IExternalAccess exta = ia.getExternalAccess();
					as.addAuthenticationListener(new IAuthenticationListener()
					{
						public IFuture deauthenticated(final IComponentIdentifier client, ClientInfo info)
						{
							return exta.scheduleStep(new IComponentStep<Void>()
							{
								public IFuture<Void> execute(IInternalAccess ia)
								{
									procListeners.remove(client);
									return IFuture.DONE;
								}
							});
						}
						
						public IFuture authenticated(IComponentIdentifier client, ClientInfo info)
						{
							return IFuture.DONE;
						}
					});
				}
			}));
		}
		return procListeners;
	}
	
	protected void fireProcessFinished(Object id)
	{
		Map<IComponentIdentifier, Set<IProcessListener>> processListeners =  getListeners();
		for (Iterator it = processListeners.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) it.next();
			final Set<IProcessListener> listeners = (Set<IProcessListener>) entry.getValue();
			ProcessEvent evt = new ProcessEvent(String.valueOf(id));
			IProcessListener[] ls = listeners.toArray(new IProcessListener[listeners.size()]);
			for (int i = 0; i < ls.length; ++i)
			{
				final IProcessListener listener = ls[i];
				listener.processFinished(evt).addResultListener(ia.createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
					}
					
					public void exceptionOccurred(Exception exception)
					{
						listeners.remove(listener);
					}
				}));
			}
		}
	}
}
