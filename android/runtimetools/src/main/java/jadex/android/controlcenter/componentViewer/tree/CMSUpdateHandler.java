// TODO: change to new subscription style

//package jadex.android.controlcenter.componentViewer.tree;
//
//import jadex.base.RemoteCMSListener;
//import jadex.base.SRemoteGui;
//import jadex.bridge.IComponentIdentifier;
//import jadex.bridge.IExternalAccess;
//import jadex.bridge.service.RequiredServiceInfo;
//import jadex.bridge.service.search.SServiceProvider;
//import jadex.bridge.service.types.cms.ICMSComponentListener;
//import jadex.bridge.service.types.cms.IComponentDescription;
//import jadex.bridge.service.types.cms.IComponentManagementService;
//import jadex.commons.ChangeEvent;
//import jadex.commons.IRemoteChangeListener;
//import jadex.commons.collection.MultiCollection;
//import jadex.commons.future.CounterResultListener;
//import jadex.commons.future.DefaultResultListener;
//import jadex.commons.future.DelegationResultListener;
//import jadex.commons.future.ExceptionDelegationResultListener;
//import jadex.commons.future.Future;
//import jadex.commons.future.IFuture;
//
//import java.util.Collection;
//import java.util.Iterator;
//
///**
// * A change handler which receives remote CMS events and delegates to the
// * registered listeners.
// */
//public class CMSUpdateHandler
//{
//	// -------- attributes --------
//
//	/** The local external access. */
//	protected IExternalAccess access;
//
//	/** The change listener called from remote. */
//	protected IRemoteChangeListener<?> rcl;
//
//	/** The local listeners for the remote CMSs (cms cid->listeners). */
//	protected MultiCollection<IComponentIdentifier, ICMSComponentListener> listeners;
//
//	/**
//	 * The futures of listeners registered during ongoing installation (cms
//	 * cid->futures).
//	 */
//	protected MultiCollection<IComponentIdentifier, IFuture<Void>> futures;
//
//	// -------- constructors --------
//
//	/**
//	 * Create a CMS update handler.
//	 */
//	public CMSUpdateHandler(IExternalAccess access)
//	{
//		this.access = access;
//		this.rcl = new IRemoteChangeListener<IComponentDescription>()
//		{
//			public IFuture<Void> changeOccurred(final ChangeEvent<IComponentDescription> event)
//			{
//				final Future<Void> ret = new Future<Void>();
//				if (listeners != null && listeners.containsKey(event.getSource()))
//				{
//					Collection<?> clis = listeners.getCollection(event.getSource());
//					// System.out.println("cmshandler: "+CMSUpdateHandler.this+" "+event+" "+clis);
//					informListeners(event, (ICMSComponentListener[]) clis.toArray(new ICMSComponentListener[clis.size()]));
//					ret.setResult(null);
//				} else
//				{
//					// Set exception to trigger listener removal.
//					ret.setException(new RuntimeException("No more listeners."));
//				}
//				return ret;
//			}
//		};
//	}
//
//	// -------- methods --------
//
//	/**
//	 * Dispose the handler for triggering remote listener removal.
//	 */
//	public IFuture<Void> dispose()
//	{
//
//		IFuture<Void> ret;
//		if (listeners != null)
//		{
//			Future<Void> fut = new Future<Void>();
//			CounterResultListener<Void> crl = new CounterResultListener<Void>(listeners.keySet().size(), true, new DelegationResultListener<Void>(fut));
//			for (Iterator<?> it = listeners.keySet().iterator(); it.hasNext();)
//			{
//				IComponentIdentifier cid = (IComponentIdentifier) it.next();
//				// Deregister if not registration in progress.
//				if (futures == null || !futures.containsKey(cid))
//					SRemoteGui.deregisterRemoteCMSListener(access, cid, buildId(cid)).addResultListener(crl);
//				else
//					crl.resultAvailable(null);
//			}
//			listeners = null;
//			ret = fut;
//		} else
//		{
//			ret = IFuture.DONE;
//		}
//		return ret;
//	}
//
//	/**
//	 * Add a CMS listener.
//	 */
//	public IFuture<Void> addCMSListener(final IComponentIdentifier cid, final ICMSComponentListener listener)
//	{
//
//		// System.out.println("added: "+cid+" "+listener+" "+this);
//		// if(cid==null)
//		// System.out.println("cid: "+cid);
//		// if(cid.getPlatformName()==null)
//		// System.out.println("platformname: "+cid.getPlatformName());
//		// if(access==null)
//		// System.out.println("access: "+access);
//		// if(access.getComponentIdentifier()==null)
//		// System.out.println("accesscid"+access.getComponentIdentifier());
//		// if(access.getComponentIdentifier().getPlatformName()==null)
//		// System.out.println("accesscidpfname: "+access.getComponentIdentifier().getPlatformName());
//
//		// For local component use direct listener.
//		if (cid.getPlatformName().equals(access.getComponentIdentifier().getPlatformName()))
//		{
//			return installLocalCMSListener(listener);
//		}
//
//		Future<Void> ret = new Future<Void>();
//		if (listeners == null)
//		{
//			this.listeners = new MultiCollection<IComponentIdentifier, ICMSComponentListener>();
//		}
//
//		// Already registered
//		if (listeners.containsKey(cid))
//		{
//			listeners.add(cid, listener);
//			// Ongoing registration.
//			if (futures != null && futures.containsKey(cid))
//			{
//				futures.add(cid, ret);
//			}
//			// Registration already finished.
//			else
//			{
//				ret.setResult(null);
//			}
//		}
//
//		// First registration for cid.
//		else
//		{
//			if (futures == null)
//			{
//				this.futures = new MultiCollection<IComponentIdentifier, IFuture<Void>>();
//			}
//			futures.add(cid, ret);
//			listeners.add(cid, listener);
//
//			SRemoteGui.installRemoteCMSListener(access, cid, rcl, buildId(cid)).addResultListener(new DefaultResultListener<Void>()
//			{
//				public void resultAvailable(Void result)
//				{
//					if (futures != null) // Todo: can be null?
//					{
//						Collection<?> coll = futures.getCollection(cid);
//						for (Iterator<?> it = coll.iterator(); it.hasNext();)
//						{
//							((Future<?>) it.next()).setResult(null);
//						}
//						futures.remove(cid);
//						if (futures.isEmpty())
//							futures = null;
//					}
//				}
//
//				public void customExceptionOccurred(Exception exception)
//				{
//					// System.out.println("remove: "+cid+", "+listener+", "+this);
//					if (listeners != null)
//						listeners.removeObject(cid, listener);
//
//					if (futures != null) // Todo: why can be null?
//					{
//						Collection<?> coll = futures.getCollection(cid);
//						for (Iterator<?> it = coll.iterator(); it.hasNext();)
//						{
//							((Future<?>) it.next()).setException(exception);
//						}
//						futures.remove(cid);
//						if (futures.isEmpty())
//							futures = null;
//					}
//				}
//			});
//		}
//
//		return ret;
//	}
//
//	/**
//	 * Remove a CMS listener.
//	 */
//	public IFuture<Void> removeCMSListener(IComponentIdentifier cid, ICMSComponentListener listener)
//	{
//
//		// System.out.println("removed lis: "+cid+" "+listener+" "+this);
//
//		IFuture<Void> ret = IFuture.DONE;
//
//		// For local component use direct listener.
//		if (cid.getPlatformName().equals(access.getComponentIdentifier().getPlatformName()))
//		{
//			ret = removeLocalCMSListener(listener);
//		}
//
//		else if (listeners != null)
//		{
//			// System.out.println("remove: "+cid+", "+listener+", "+this);
//			listeners.removeObject(cid, listener);
//			if (!listeners.containsKey(cid))
//			{
//				Future<Void> fut = new Future<Void>();
//				ret = fut;
//				SRemoteGui.deregisterRemoteCMSListener(access, cid, buildId(cid)).addResultListener(new DelegationResultListener<Void>(fut));
//				if (listeners.isEmpty())
//					listeners = null;
//			}
//		}
//		return ret;
//	}
//
//	/**
//	 * Get the local CMS.
//	 * 
//	 * @return The local CMS.
//	 */
//	public IFuture<IComponentManagementService> getLocalCMS()
//	{
//		IFuture<IComponentManagementService> ret = SServiceProvider.getService(access, IComponentManagementService.class,
//				RequiredServiceInfo.SCOPE_PLATFORM);
//		return ret;
//	}
//
//	// -------- helper methods --------
//
//	/**
//	 * Inform listeners about an event.
//	 */
//	protected void informListeners(final ChangeEvent<?> event, ICMSComponentListener[] cls)
//	{
//		if (RemoteCMSListener.EVENT_COMPONENT_ADDED.equals(event.getType()))
//		{
//			for (int i = 0; i < cls.length; i++)
//			{
//				final ICMSComponentListener lis = cls[i];
//				lis.componentAdded((IComponentDescription) event.getValue()).addResultListener(new DefaultResultListener<Void>()
//				{
//					public void resultAvailable(Void result)
//					{
//					}
//
//					public void exceptionOccurred(Exception exception)
//					{
//						removeCMSListener(((IComponentDescription) event.getValue()).getName(), lis);
//					}
//				});
//			}
//		} else if (RemoteCMSListener.EVENT_COMPONENT_CHANGED.equals(event.getType()))
//		{
//			for (int i = 0; i < cls.length; i++)
//			{
//				final ICMSComponentListener lis = cls[i];
//				lis.componentChanged((IComponentDescription) event.getValue()).addResultListener(new DefaultResultListener<Void>()
//				{
//					public void resultAvailable(Void result)
//					{
//					}
//
//					public void exceptionOccurred(Exception exception)
//					{
//						removeCMSListener(((IComponentDescription) event.getValue()).getName(), lis);
//					}
//				});
//			}
//		} else if (RemoteCMSListener.EVENT_COMPONENT_REMOVED.equals(event.getType()))
//		{
//			for (int i = 0; i < cls.length; i++)
//			{
//				// Todo: component results?
//				final ICMSComponentListener lis = cls[i];
//				// System.out.println("rem compo: "+((IComponentDescription)event.getValue()).getName());
//				cls[i].componentRemoved((IComponentDescription) event.getValue(), null).addResultListener(new DefaultResultListener<Void>()
//				{
//					public void resultAvailable(Void result)
//					{
//					}
//
//					public void exceptionOccurred(Exception exception)
//					{
//						removeCMSListener(((IComponentDescription) event.getValue()).getName(), lis);
//					}
//				});
//			}
//		} else if (RemoteCMSListener.EVENT_BULK.equals(event.getType()))
//		{
//			Collection<ChangeEvent<?>> events = (Collection<ChangeEvent<?>>) event.getValue();
//			for (Iterator<ChangeEvent<?>> it = events.iterator(); it.hasNext();)
//			{
//				informListeners(it.next(), cls);
//			}
//		}
//	}
//
//	/**
//	 * Install a local listener.
//	 * 
//	 * @param listener
//	 *            The local listener.
//	 */
//	protected IFuture<Void> installLocalCMSListener(final ICMSComponentListener listener)
//	{
//		final Future<Void> ret = new Future<Void>();
//		SServiceProvider.searchService(access, new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).addResultListener(
//				new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
//				{
//					public void customResultAvailable(IComponentManagementService cms)
//					{
//						// IComponentManagementService cms =
//						// (IComponentManagementService)result;
//						cms.addComponentListener(null, listener);
//						ret.setResult(null);
//					}
//
//				});
//		return ret;
//	}
//
//	/**
//	 * Remove a local listener.
//	 * 
//	 * @param listener
//	 *            The local listener.
//	 */
//	protected IFuture<Void> removeLocalCMSListener(final ICMSComponentListener listener)
//	{
//		final Future<Void> ret = new Future<Void>();
//		SServiceProvider.searchService(access, new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).addResultListener(
//				new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
//				{
//					public void customResultAvailable(IComponentManagementService cms)
//					{
//						// IComponentManagementService cms =
//						// (IComponentManagementService)result;
//						cms.removeComponentListener(null, listener);
//						ret.setResult(null);
//					}
//				});
//		return ret;
//	}
//
//	/**
//	 * Build an id to be used for remote listener (de-)registration.
//	 * 
//	 * @param cid
//	 *            The remote component id.
//	 * @return An id for remote listener (de-)registration.
//	 */
//	protected String buildId(final IComponentIdentifier cid)
//	{
//		return "cmslistener_" + hashCode() + "_" + access.getComponentIdentifier().toString() + "_" + cid;
//	}
//}
