package jadex.base.gui;

import jadex.base.Starter;
import jadex.bridge.ICMSComponentListener;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.ChangeEvent;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.xml.annotation.XMLClassname;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.SwingUtilities;

/**
 *  A change handler which receives remote CMS events and delegates to the registered listeners.
 */
public class CMSUpdateHandler
{
	//-------- constants --------
	
	/** The event type for an added component (value is component description). */
	protected static final String	EVENT_COMPONENT_ADDED	= "component-added";
	
	/** The event type for a changed component (value is component description). */
	protected static final String	EVENT_COMPONENT_CHANGED	= "component-changed";
	
	/** The event type for a removed component (value is component description). */
	protected static final String	EVENT_COMPONENT_REMOVED	= "component-removed";
	
	/** The event type for a removed component (value is collection of change events). */
	protected static final String	EVENT_BULK	= "bulk-event";
	
	/** Update delay. */
	// todo: make configurable.
	// Used in RemoteCMSListener
	protected static final long UPDATE_DELAY	= 500;	
	
	/** Maximum number of events per delay period. */
	// todo: make configurable.
	protected static final int MAX_EVENTS	= 20;	
	
	//-------- attributes --------

	/** The local external access. */
	protected IExternalAccess	access;
	
	/** The change listener called from remote. */
	protected IRemoteChangeListener	rcl;
	
	/** The local listeners for the remote CMSs (cms cid->listeners). */
	protected MultiCollection	listeners;
	
	/** The futures of listeners registered during ongoing installation (cms cid->futures). */
	protected MultiCollection	futures;
	
	//-------- constructors --------
	
	/**
	 *  Create a CMS update handler.
	 */
	public CMSUpdateHandler(IExternalAccess access)
	{
		this.access	= access;
		this.rcl	= new IRemoteChangeListener()
		{
			public IFuture changeOccurred(final ChangeEvent event)
			{
				final Future	ret	= new Future();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						if(listeners!=null && listeners.containsKey(event.getSource()))
						{
							Collection	clis	= listeners.getCollection(event.getSource());
//							System.out.println("cmshandler: "+CMSUpdateHandler.this+" "+event+" "+clis);
							informListeners(event, (ICMSComponentListener[])clis.toArray(new ICMSComponentListener[clis.size()]));
							ret.setResult(null);
						}
						else
						{
							// Set exception to trigger listener removal.
							ret.setException(new RuntimeException("No more listeners."));
						}
					}
				});
				return ret;
			}
		};
	}
	
	//-------- methods --------
	
	/**
	 *  Dispose the handler for triggering remote listener removal.
	 */
	public IFuture	dispose()
	{
		assert SwingUtilities.isEventDispatchThread() ||  Starter.isShutdown();
		
		IFuture	ret;
		if(listeners!=null)
		{
			Future	fut	= new Future();
			CounterResultListener	crl	= new CounterResultListener(listeners.keySet().size(), true, new DelegationResultListener(fut));
			for(Iterator it=listeners.keySet().iterator(); it.hasNext(); )
			{
				IComponentIdentifier	cid	= (IComponentIdentifier)it.next();
				// Deregister if not registration in progress.
				if(futures==null || !futures.containsKey(cid))
					deregisterRemoteCMSListener(cid).addResultListener(crl);
				else
					crl.resultAvailable(null);
			}
			listeners	= null;
			ret	= fut;
		}
		else
		{
			ret	= IFuture.DONE;
		}
		return ret;
	}
	
	/**
	 *  Add a CMS listener.
	 */
	public IFuture	addCMSListener(final IComponentIdentifier cid, final ICMSComponentListener listener)
	{
		assert SwingUtilities.isEventDispatchThread();
		
//		System.out.println("added: "+cid+" "+listener+" "+this);
//		if(cid==null)
//			System.out.println("cid: "+cid);
//		if(cid.getPlatformName()==null)
//			System.out.println("platformname: "+cid.getPlatformName());
//		if(access==null)
//			System.out.println("access: "+access);
//		if(access.getComponentIdentifier()==null)
//			System.out.println("accesscid"+access.getComponentIdentifier());
//		if(access.getComponentIdentifier().getPlatformName()==null)
//			System.out.println("accesscidpfname: "+access.getComponentIdentifier().getPlatformName());
		
		
		// For local component use direct listener.
		if(cid.getPlatformName().equals(access.getComponentIdentifier().getPlatformName()))
		{
			return installLocalCMSListener(listener);
		}
		
		Future	ret	= new Future();
		if(listeners==null)
		{
			this.listeners	= new MultiCollection();
		}
		
		// Already registered
		if(listeners.containsKey(cid))
		{
			listeners.put(cid, listener);
			// Ongoing registration.
			if(futures!=null && futures.containsKey(cid))
			{
				futures.put(cid, ret);				
			}
			// Registration already finished.
			else
			{
				ret.setResult(null);
			}
		}
		
		// First registration for cid.
		else
		{
			if(futures==null)
			{
				this.futures	= new MultiCollection();
			}
			futures.put(cid, ret);
			listeners.put(cid, listener);

			installRemoteCMSListener(cid).addResultListener(new SwingDefaultResultListener()
			{
				public void customResultAvailable(Object result)
				{
//					if(cid==null)
//						System.out.println("result cid: "+cid);
//					if(futures==null)
//						System.out.println("result futures: "+futures);
					Collection coll	= futures.getCollection(cid);
//					if(coll==null)
//						System.out.println("result coll: "+coll);
					for(Iterator it=coll.iterator(); it.hasNext(); )
					{
						((Future)it.next()).setResult(null);
					}
					futures.remove(cid);
					if(futures.isEmpty())
						futures	= null;
				}
				public void customExceptionOccurred(Exception exception)
				{
//					System.out.println("remove: "+cid+", "+listener+", "+this);
					if(listeners!=null)
						listeners.remove(cid, listener);
					
					Collection coll	= futures.getCollection(cid);
					for(Iterator it=coll.iterator(); it.hasNext(); )
					{
						((Future)it.next()).setException(exception);
					}
					futures.remove(cid);
					if(futures.isEmpty())
						futures	= null;
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Remove a CMS listener.
	 */
	public IFuture	removeCMSListener(IComponentIdentifier cid, ICMSComponentListener listener)
	{
		assert SwingUtilities.isEventDispatchThread() ||  Starter.isShutdown();
		
//		System.out.println("removed lis: "+cid+" "+listener+" "+this);
		
		IFuture	ret	= IFuture.DONE;
		
		// For local component use direct listener.
		if(cid.getPlatformName().equals(access.getComponentIdentifier().getPlatformName()))
		{
			ret	= removeLocalCMSListener(listener);
		}

		else if(listeners!=null)
		{
//			System.out.println("remove: "+cid+", "+listener+", "+this);
			listeners.remove(cid, listener);
			if(!listeners.containsKey(cid))
			{
				ret	= deregisterRemoteCMSListener(cid);
				if(listeners.isEmpty())
					listeners	= null;
			}
		}
		return ret;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Inform listeners about an event.
	 */
	protected void informListeners(final ChangeEvent event, ICMSComponentListener[] cls)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		if(EVENT_COMPONENT_ADDED.equals(event.getType()))
		{
			for(int i=0; i<cls.length; i++)
			{
				final ICMSComponentListener lis = cls[i];
				lis.componentAdded((IComponentDescription)event.getValue())
					.addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object result)
					{
					}
					
					public void customExceptionOccurred(Exception exception)
					{
						removeCMSListener(((IComponentDescription)event.getValue()).getName(), lis);
					}
				});
			}
		}
		else if(EVENT_COMPONENT_CHANGED.equals(event.getType()))
		{
			for(int i=0; i<cls.length; i++)
			{
				final ICMSComponentListener lis = cls[i];
				lis.componentChanged((IComponentDescription)event.getValue())
					.addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object result)
					{
					}
					
					public void customExceptionOccurred(Exception exception)
					{
						removeCMSListener(((IComponentDescription)event.getValue()).getName(), lis);
					}
				});
			}
		}
		else if(EVENT_COMPONENT_REMOVED.equals(event.getType()))
		{
			for(int i=0; i<cls.length; i++)
			{
				// Todo: component results?
				final ICMSComponentListener lis = cls[i];
//				System.out.println("rem compo: "+((IComponentDescription)event.getValue()).getName());
				cls[i].componentRemoved((IComponentDescription)event.getValue(), null)
					.addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object result)
					{
					}
					
					public void customExceptionOccurred(Exception exception)
					{
						removeCMSListener(((IComponentDescription)event.getValue()).getName(), lis);
					}
				});
			}
		}
		else if(EVENT_BULK.equals(event.getType()))
		{
			Collection	events	= (Collection)event.getValue();
			for(Iterator it=events.iterator(); it.hasNext(); )
			{
				informListeners((ChangeEvent)it.next(), cls);
			}
		}
	}
	
	/**
	 *  Install a local listener.
	 *  @param listener	The local listener.
	 */
	protected IFuture	installLocalCMSListener(final ICMSComponentListener listener)
	{
		final Future	ret	= new Future();
		SServiceProvider.getService(access.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IComponentManagementService	cms	= (IComponentManagementService)result;
				cms.addComponentListener(null, listener);
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Remove a local listener.
	 *  @param listener	The local listener.
	 */
	protected IFuture	removeLocalCMSListener(final ICMSComponentListener listener)
	{
		final Future	ret	= new Future();
		SServiceProvider.getService(access.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IComponentManagementService	cms	= (IComponentManagementService)result;
				cms.removeComponentListener(null, listener);
				ret.setResult(null);
			}
		});
		return ret;
	}

	/**
	 *  Install the remote listener.
	 *  @param cid	The remote component id.
	 */
	protected IFuture	installRemoteCMSListener(final IComponentIdentifier cid)
	{
		final Future	ret	= new Future();
		SServiceProvider.getService(access.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IComponentManagementService	cms	= (IComponentManagementService)result;
				cms.getExternalAccess(cid).addResultListener(new SwingDelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IExternalAccess	exta	= (IExternalAccess)result;
						final IComponentIdentifier	icid	= cid;	// internal reference to cid, because java compiler stores final references in outmost object (grrr.)
						final String	id	= buildId(cid);
						final IRemoteChangeListener	rcl	= CMSUpdateHandler.this.rcl;
						exta.scheduleStep(new IComponentStep<Void>()
						{
							@XMLClassname("installListener")
							public IFuture<Void> execute(IInternalAccess ia)
							{
								final Future<Void>	ret	= new Future<Void>();
								SServiceProvider.getService(ia.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
									.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
										IComponentManagementService	cms	= (IComponentManagementService)result;
										RemoteCMSListener	rcmsl	= new RemoteCMSListener(icid, id, cms, rcl);
										cms.addComponentListener(null, rcmsl);
										ret.setResult(null);
									}
								}));
								return ret;
							}
						}).addResultListener(new SwingDelegationResultListener(ret));
					}
				});
			}
		});
		return ret;
	}

	/**
	 *  Deregister the remote listener.
	 */
	protected IFuture	deregisterRemoteCMSListener(final IComponentIdentifier cid)
	{
		final Future	ret	= new Future();
		SServiceProvider.getService(access.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IComponentManagementService	cms	= (IComponentManagementService)result;
				cms.getExternalAccess(cid).addResultListener(new SwingDelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IExternalAccess	exta	= (IExternalAccess)result;
						final String	id	= buildId(cid);
						exta.scheduleStep(new IComponentStep<Void>()
						{
							@XMLClassname("deregisterListener")
							public IFuture<Void> execute(IInternalAccess ia)
							{
								final Future	ret	= new Future();
								SServiceProvider.getService(ia.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
									.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
//										System.out.println("Removing listener: "+id);
										IComponentManagementService	cms	= (IComponentManagementService)result;
										try
										{
											cms.removeComponentListener(null, new RemoteCMSListener(cid, id, cms, null));
										}
										catch(RuntimeException e)
										{
		//									System.out.println("Listener already removed: "+id);
										}
										ret.setResult(null);
									}
								}));
								return ret;
							}
						}).addResultListener(new DelegationResultListener(ret));
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 *  Build an id to be used for remote listener (de-)registration.
	 *  @param cid	The remote component id.
	 *  @return	An id for remote listener (de-)registration.
	 */
	protected String buildId(final IComponentIdentifier cid)
	{
		return "cmslistener_"+hashCode()+"_"+access.getComponentIdentifier().toString()+"_"+cid;
	}
}
