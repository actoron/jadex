package jadex.base.gui;

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
import jadex.commons.SUtil;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.SwingDefaultResultListener;
import jadex.commons.future.SwingDelegationResultListener;
import jadex.xml.annotation.XMLClassname;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
	protected static final long UPDATE_DELAY	= 1000;	
	
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
		assert SwingUtilities.isEventDispatchThread();
		
		IFuture	ret;
		if(listeners!=null)
		{
			Future	fut	= new Future();
			CounterResultListener	crl	= new CounterResultListener(listeners.keySet().size(), true, new DelegationResultListener(fut));
			for(Iterator it=listeners.keySet().iterator(); it.hasNext(); )
			{
				IComponentIdentifier	cid	= (IComponentIdentifier)it.next();
				deregisterRemoteCMSListener(cid).addResultListener(crl);
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
	public IFuture	addCMSListener(final IComponentIdentifier cid, ICMSComponentListener listener)
	{
		assert SwingUtilities.isEventDispatchThread();
		
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

			installRemoteCMSListener(cid).addResultListener(new SwingDefaultResultListener()
			{
				public void customResultAvailable(Object result)
				{
					Collection coll	= futures.getCollection(cid);
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
		
		listeners.put(cid, listener);
		
		return ret;
	}
	
	/**
	 *  Remove a CMS listener.
	 */
	public IFuture	removeCMSListener(IComponentIdentifier cid, ICMSComponentListener listener)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		IFuture	ret	= IFuture.DONE;
		
		// For local component use direct listener.
		if(cid.getPlatformName().equals(access.getComponentIdentifier().getPlatformName()))
		{
			ret	= removeLocalCMSListener(listener);
		}

		else if(listeners!=null)
		{
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
		if(EVENT_COMPONENT_ADDED.equals(event.getType()))
		{
			for(int i=0; i<cls.length; i++)
			{
				cls[i].componentAdded((IComponentDescription)event.getValue());
			}
		}
		else if(EVENT_COMPONENT_CHANGED.equals(event.getType()))
		{
			for(int i=0; i<cls.length; i++)
			{
				cls[i].componentChanged((IComponentDescription)event.getValue());
			}
		}
		else if(EVENT_COMPONENT_REMOVED.equals(event.getType()))
		{
			for(int i=0; i<cls.length; i++)
			{
				// Todo: component results?
				cls[i].componentRemoved((IComponentDescription)event.getValue(), null);
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
						final String	id	= buildId(cid);
						final IRemoteChangeListener	rcl	= CMSUpdateHandler.this.rcl;
						exta.scheduleStep(new IComponentStep()
						{
							@XMLClassname("installListener")
							public Object execute(IInternalAccess ia)
							{
								final Future	ret	= new Future();
								SServiceProvider.getService(ia.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
									.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
//										System.out.println("Installing listener: "+id);
										IComponentManagementService	cms	= (IComponentManagementService)result;
										cms.addComponentListener(null, new CMSUpdateHandler.RemoteCMSListener(cid, id, cms, rcl));
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
						exta.scheduleStep(new IComponentStep()
						{
							@XMLClassname("deregisterListener")
							public Object execute(IInternalAccess ia)
							{
								final Future	ret	= new Future();
								SServiceProvider.getService(ia.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
									.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
//										System.out.println("Removing listener: "+id);
										IComponentManagementService	cms	= (IComponentManagementService)result;
										try
										{
											cms.removeComponentListener(null, new CMSUpdateHandler.RemoteCMSListener(cid, id, cms, null));
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
	
	//-------- helper classes --------

	/**
	 *  The component listener installed at the remote CMS.
	 */
	public static class RemoteCMSListener	implements ICMSComponentListener
	{
		//-------- attributes --------
		
		/** The local platform cid used as event source. */
		protected IComponentIdentifier	cid;
		
		/** The id for remote listener deregistration. */
		protected String	id;
		
		/** The CMS used for automatic removal of listener. */
		protected IComponentManagementService	cms;
		
		/** The change listener (proxy) to be informed about important changes. */
		protected IRemoteChangeListener	rcl;
		
		/** The update timer (if any). */
		protected Timer	timer;
		
		/** The added components, if any (cid->desc). */
		protected Map	added;
				
		/** The changed components, if any (cid->desc). */
		protected Map	changed;
				
		/** The removed components, if any (cid->desc). */
		protected Map	removed;
				
		//-------- constructors --------
		
		/**
		 *  Create a CMS listener sending updates to a remote change listener.
		 */
		public RemoteCMSListener(IComponentIdentifier cid, String id, IComponentManagementService cms, IRemoteChangeListener rcl)
		{
			this.cid	= cid;
			this.id	= id;
			this.cms	= cms;
			this.rcl	= rcl;
		}
		
		//-------- ICMSComponentListener interface --------
		
		/**
		 *  Called when a new element has been added.
		 *  @param id The identifier.
		 */
		public synchronized IFuture componentAdded(final IComponentDescription desc)
		{
			if(removed!=null && removed.containsKey(desc.getName()))
			{
				removed.remove(desc.getName());
			}
			else
			{
				if(added==null)
				{
					added	= new LinkedHashMap();
				}
				added.put(desc.getName(), desc);
			}
			
			startTimer();
			return IFuture.DONE;
		}		
		
		/**
		 *  Called when a component has changed its state.
		 *  @param id The identifier.
		 */
		public IFuture componentChanged(IComponentDescription desc)
		{
			if(added!=null && added.containsKey(desc.getName()))
			{
				added.put(desc.getName(), desc);
			}
			else
			{
				if(changed==null)
				{
					changed	= new LinkedHashMap();
				}
				changed.put(desc.getName(), desc);
			}
			
			startTimer();
			return IFuture.DONE;
		}
		
		/**
		 *  Called when a new element has been removed.
		 *  @param id The identifier.
		 */
		public synchronized IFuture componentRemoved(final IComponentDescription desc, Map results)
		{
			if(changed!=null && changed.containsKey(desc.getName()))
			{
				changed.remove(desc.getName());
			}
			
			if(added!=null && added.containsKey(desc.getName()))
			{
				added.remove(desc.getName());
			}
			else
			{
				if(removed==null)
				{
					removed	= new LinkedHashMap();
				}
				removed.put(desc.getName(), desc);
			}
			
			startTimer();
			return IFuture.DONE;
		}

		protected void startTimer()
		{
			if(timer==null)
			{
				timer	= new Timer(true);
				timer.schedule(new TimerTask()
				{
					public void run()
					{
						List	events	= new ArrayList();
						synchronized(RemoteCMSListener.this)
						{
							timer	= null;
							if(removed!=null)
							{
								for(Iterator it=removed.values().iterator(); events.size()<MAX_EVENTS && it.hasNext(); )
								{
									events.add(new ChangeEvent(cid, EVENT_COMPONENT_REMOVED, it.next()));
									it.remove();
								}
							}
							if(added!=null)
							{
								for(Iterator it=added.values().iterator(); events.size()<MAX_EVENTS && it.hasNext(); )
								{
									events.add(new ChangeEvent(cid, EVENT_COMPONENT_ADDED, it.next()));
									it.remove();
								}
							}
							if(changed!=null)
							{
								for(Iterator it=changed.values().iterator(); events.size()<MAX_EVENTS && it.hasNext(); )
								{
									events.add(new ChangeEvent(cid, EVENT_COMPONENT_CHANGED, it.next()));
									it.remove();
								}
							}
							
							if(removed!=null && removed.isEmpty())
								removed	= null;
							if(added!=null && added.isEmpty())
								added	= null;
							if(changed!=null && changed.isEmpty())
								changed	= null;
							
							if(removed!=null || added!=null || changed!=null)
							{
								startTimer();
							}
						}
						
						if(!events.isEmpty())
						{
//							System.out.println("events: "+events.size());
							rcl.changeOccurred(new ChangeEvent(cid, EVENT_BULK, events)).addResultListener(new IResultListener()
							{
								public void resultAvailable(Object result)
								{
//									System.out.println("update succeeded: "+desc);
								}
								public void exceptionOccurred(Exception exception)
								{
//									exception.printStackTrace();
									if(cms!=null)
									{
//										System.out.println("Removing listener due to failed update: "+RemoteCMSListener.this.id);
										try
										{
											cms.removeComponentListener(null, RemoteCMSListener.this);
										}
										catch(RuntimeException e)
										{
//											System.out.println("Listener already removed: "+id);
										}
										RemoteCMSListener.this.cms	= null;	// Set to null to avoid multiple removal due to delayed errors. 
									}
								}
							});
						}
					}
				}, UPDATE_DELAY);
			}
		}
		
		//-------- methods --------
		
		/**
		 *  Test if two objects are equal.
		 */
		public boolean	equals(Object obj)
		{
			return obj instanceof RemoteCMSListener && SUtil.equals(((RemoteCMSListener)obj).id, id);
		}
		
		/**
		 *  Get the hashcode
		 */
		public int	hashCode()
		{
			return 31+id.hashCode();
		}
	}
}
