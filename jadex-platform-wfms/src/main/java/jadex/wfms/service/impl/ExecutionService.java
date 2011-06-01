package jadex.wfms.service.impl;

import jadex.base.SComponentFactory;
import jadex.bridge.CreationInfo;
import jadex.bridge.ICMSComponentListener;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.wfms.IProcess;
import jadex.wfms.client.ClientInfo;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.listeners.IAuthenticationListener;
import jadex.wfms.service.listeners.IProcessListener;
import jadex.wfms.service.listeners.ProcessEvent;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smack.util.Base64;

/**
 *  The meta execution service wraps all specific process execution services.
 */
public class ExecutionService implements IExecutionService
{
	//-------- attributes --------
	
	/** Component access. */
	@ServiceComponent
	protected IInternalAccess ia;
	
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
	 *  @param filename The file name.
	 *  @return The process model.
	 */
	public IFuture loadModel(String filename, String[] imports)
	{
		return SComponentFactory.loadModel(ia.getExternalAccess(), filename);
	}
	
	/**
	 *  Start a process instance.
	 */
	public IFuture startProcess(final String modelname, final Object id, final Map arguments)
	{
		final Future ret = new Future();
		SServiceProvider.getService(ia.getServiceContainer(), (Class) IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService) result;
				
				CreationInfo ci = new CreationInfo(null, arguments, ia.getComponentIdentifier(), true);
				ci.setPlatformloader(true);
				String prefix = modelname.substring(Math.max(modelname.lastIndexOf("/"), 0) + 1);
				prefix = prefix.substring(0, Math.min(prefix.lastIndexOf("."), prefix.length()));
				ByteBuffer b = ByteBuffer.allocate(8);
				b.putLong(random.nextLong());
				cms.createComponent(prefix + "_" + Base64.encodeBytes(b.array()), modelname, ci, null).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
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
	 *  @param model The model.
	 *  @return True, if model can be loaded.
	 */
	public IFuture isLoadable(String name)
	{
		return SComponentFactory.isLoadable(ia.getExternalAccess(), name);
	}
	
	/**
	 * Adds a process listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture addProcessListener(IComponentIdentifier client, IProcessListener listener)
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
	public IFuture removeProcessListener(IComponentIdentifier client, IProcessListener listener)
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
			SServiceProvider.getService(ia.getServiceContainer(), IAAAService.class, RequiredServiceInfo.SCOPE_GLOBAL).addResultListener(ia.createResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					IAAAService as = (IAAAService) result;
					final IExternalAccess exta = ia.getExternalAccess();
					as.addAuthenticationListener(new IAuthenticationListener()
					{
						public IFuture deauthenticated(final IComponentIdentifier client, ClientInfo info)
						{
							return exta.scheduleStep(new IComponentStep()
							{
								public Object execute(IInternalAccess ia)
								{
									procListeners.remove(client);
									return null;
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
