package jadex.base.service.remote;

import jadex.bridge.ICMSComponentListener;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.service.IServiceContainer;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.xml.annotation.XMLClassname;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 *  A proxy agent is a pseudo component that mirrors services of a remote platform (or component)
 *  and allows listening for remote CMS events.
 */
@Description("This agent represents a proxy for a remote component.")
@Arguments(@Argument(name="component", typename="jadex.bridge.IComponentIdentifier", defaultvalue="null", description="The component id of the remote component/platform."))
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, scope=RequiredServiceInfo.SCOPE_PLATFORM))
public class ProxyAgent extends MicroAgent
{
	//-------- constants --------
	
	/** The event type for an added component (value is component description). */
	public static final String	EVENT_COMPONENT_ADDED	= "component-added";
	
	/** The event type for a removed component (value is component description). */
	public static final String	EVENT_COMPONENT_REMOVED	= "component-removed";
	
	/** The event type for a removed component (value is collection of change events). */
	public static final String	EVENT_BULK	= "bulk-event";
	
	//-------- attributes --------
	
	/**  The remote component identifier. */
	protected IComponentIdentifier	rcid;
	
	/** The remote change listener (if installed). */
	protected UpdateHandler	handler;
	
	//-------- methods --------
	
	/**
	 *  Dispose handler to trigger remote listener removal on next update.
	 */
	public IFuture	agentKilled()
	{
		IFuture	ret;
		if(handler!=null)
		{
			ret	= handler.dispose();
		}
		else
		{
			ret	= IFuture.DONE;
		}
		return ret; 
	}
	
	/**
	 *  Get the service container.
	 *  @return The service container.
	 */
	public IServiceContainer createServiceContainer()
	{
		// Hack!!! Can not be done in agentCreated, because service container is created first. 
		this.rcid	= (IComponentIdentifier)getArgument("component");
		
		return new RemoteServiceContainer(rcid, getAgentAdapter());
	}
	
	/**
	 *  Get the platform identifier.
	 *  @return The platform identifier.
	 */
	public IComponentIdentifier getRemotePlatformIdentifier()
	{
		return rcid;
	}
	
	/**
	 *  Add a CMS listener.
	 */
	public IFuture	addCMSListener(ICMSComponentListener listener)
	{
		if(handler==null)
			handler	= new UpdateHandler();
		
		return handler.addCMSListener(listener);
	}
	
	/**
	 *  Remove a CMS listener.
	 */
	public IFuture	removeCMSListener(ICMSComponentListener listener)
	{
		return handler.removeCMSListener(listener);		
	}
	
	//-------- helper classes --------
	
	/**
	 *  The change handler which receives remote change events and delegates to the registered listeners.
	 */
	public class UpdateHandler	implements IRemoteChangeListener
	{
		//-------- attributes --------
		
		/** The local listeners for the remote CMS. */
		protected List	listeners;
		
		/** The unique id for remote listener deregistration. */
		protected String	id	= SUtil.createUniqueId(getAgentName());
		
		/** The futures of listeners registered during ongoing installation. */
		protected List	futures;
		
		//-------- IRemoteChangeListener interface --------
		
		/**
		 *  Called when a change occurs.
		 *  Signature has a return value for understanding when an exception 
		 *  occurs so that there is a chance to remove the listener:
		 *  @param event The event.
		 */
		public IFuture changeOccurred(final ChangeEvent event)
		{
			final Future	ret	= new Future();
			scheduleStep(new IComponentStep()
			{
				public Object execute(IInternalAccess ia)
				{
					if(listeners!=null)
					{
						ICMSComponentListener[]	cls	= (ICMSComponentListener[])listeners.toArray(new ICMSComponentListener[listeners.size()]);
						informListeners(event, cls);
						ret.setResult(null);
					}
					else
					{
						// Set exception to trigger listener removal.
						ret.setException(new RuntimeException("No more listeners."));
					}
					return null;
				}
			});
			return ret;
		}
		
		//-------- methods --------
		
		/**
		 *  Dispose the handler for triggering remote listener removal on next update.
		 */
		public IFuture	dispose()
		{
			this.listeners	= null;
			ProxyAgent.this.handler	= null;
			return deregisterRemoteCMSListener();
		}
		
		/**
		 *  Add a CMS listener.
		 */
		public IFuture	addCMSListener(final ICMSComponentListener listener)
		{
			Future	ret	= new Future();
			if(listeners==null)
			{
				listeners	= new ArrayList();
				futures	= new ArrayList();
				futures.add(ret);
				installRemoteCMSListener(this).addResultListener(createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						for(int i=0; i<futures.size(); i++)
						{
							((Future)futures.get(i)).setResult(null);
						}
						futures	= null;
					}
					public void exceptionOccurred(Exception exception)
					{
						for(int i=0; i<futures.size(); i++)
						{
							((Future)futures.get(i)).setException(exception);
						}
						futures	= null;
					}
				}));
			}
			else if(futures!=null)
			{
				futures.add(ret);
			}
			else
			{
				ret.setResult(null);
			}
			listeners.add(listener);
			return ret;
		}
		
		/**
		 *  Remove a CMS listener.
		 */
		public IFuture	removeCMSListener(ICMSComponentListener listener)
		{
			listeners.remove(listener);
			if(listeners.isEmpty())
				dispose();
			return IFuture.DONE;
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
		 *  Install the remote listener.
		 *  @param rcl	The local change listener to be notified by the remote CMS listener.
		 */
		protected IFuture	installRemoteCMSListener(final IRemoteChangeListener rcl)
		{
			final Future	ret	= new Future();
			getRequiredService("cms").addResultListener(createResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					IComponentManagementService	cms	= (IComponentManagementService)result;
					cms.getExternalAccess(rcid).addResultListener(createResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							IExternalAccess	exta	= (IExternalAccess)result;
							final String	id	= UpdateHandler.this.id;
							exta.scheduleStep(new IComponentStep()
							{
								@XMLClassname("installListener")
								public Object execute(IInternalAccess ia)
								{
									final Future	ret	= new Future();
									SServiceProvider.getService(ia.getServiceProvider(), IComponentManagementService.class)
										.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
									{
										public void customResultAvailable(Object result)
										{
//											System.out.println("Installing listener: "+id);
											IComponentManagementService	cms	= (IComponentManagementService)result;
											cms.addComponentListener(null, new RemoteCMSListener(id, cms, rcl));
											ret.setResult(null);
										}
									}));
									return ret;
								}
							}).addResultListener(new DelegationResultListener(ret));
						}
					}));
				}
			}));
			return ret;
		}

		/**
		 *  Deregister the remote listener.
		 */
		protected IFuture	deregisterRemoteCMSListener()
		{
			// Note: cannot use createResultListener as agent will not be executed any more when killed.
			final Future	ret	= new Future();
			getRequiredService("cms").addResultListener(createResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					IComponentManagementService	cms	= (IComponentManagementService)result;
					cms.getExternalAccess(rcid).addResultListener(createResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							IExternalAccess	exta	= (IExternalAccess)result;
							final String	id	= UpdateHandler.this.id;
							exta.scheduleStep(new IComponentStep()
							{
								@XMLClassname("deregisterListener")
								public Object execute(IInternalAccess ia)
								{
									final Future	ret	= new Future();
									SServiceProvider.getService(ia.getServiceProvider(), IComponentManagementService.class)
										.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
									{
										public void customResultAvailable(Object result)
										{
//											System.out.println("Removing listener: "+id);
											IComponentManagementService	cms	= (IComponentManagementService)result;
											try
											{
												cms.removeComponentListener(null, new RemoteCMSListener(id, cms, null));
											}
											catch(RuntimeException e)
											{
//												System.out.println("Listener already removed: "+id);
											}
											ret.setResult(null);
										}
									}));
									return ret;
								}
							}).addResultListener(new DelegationResultListener(ret));
						}
					}));
				}
			}));
			return ret;
		}
	}
	
	/**
	 *  The component listener installed at the remote CMS.
	 */
	public static class RemoteCMSListener	implements ICMSComponentListener
	{
		//-------- attributes --------
		
		/** The id for remote listener deregistration. */
		protected String	id;
		
		/** The CMS used for automatic removal of listener. */
		protected IComponentManagementService	cms;
		
		/** The change listener (proxy) to be informed about important changes. */
		protected IRemoteChangeListener	rcl;
				
		//-------- constructors --------
		
		/**
		 *  Create a CMS listener sending updates to a remote change listener.
		 */
		public RemoteCMSListener(String id, IComponentManagementService cms, IRemoteChangeListener rcl)
		{
			this.id	= id;
			this.cms	= cms;
			this.rcl	= rcl;
		}
		
		//-------- ICMSComponentListener interface --------
		
		/**
		 *  Called when a new element has been added.
		 *  @param id The identifier.
		 */
		public IFuture componentAdded(final IComponentDescription desc)
		{
//			System.out.println("Local added: "+desc);
			// Todo: collect events and send as bulk
			ChangeEvent	ce	= new ChangeEvent(null, "component-added", desc);
			rcl.changeOccurred(ce).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
//					System.out.println("update succeeded: "+desc);
				}
				public void exceptionOccurred(Exception exception)
				{
					if(cms!=null)
					{
//						System.out.println("Removing listener due to failed update: "+RemoteCMSListener.this.id);
						try
						{
							cms.removeComponentListener(null, RemoteCMSListener.this);
						}
						catch(RuntimeException e)
						{
//							System.out.println("Listener already removed: "+id);
						}
						RemoteCMSListener.this.cms	= null;	// Set to null to avoid multiple removal due to delayed errors. 
					}
				}
			});
			return IFuture.DONE;
		}
		
		/**
		 *  Called when a component has changed its state.
		 *  @param id The identifier.
		 */
		public IFuture componentChanged(IComponentDescription desc)
		{
			// ignored
			return IFuture.DONE;
		}
		
		/**
		 *  Called when a new element has been removed.
		 *  @param id The identifier.
		 */
		public IFuture componentRemoved(final IComponentDescription desc, Map results)
		{
//			System.out.println("Local removed: "+desc);
			// Todo: collect events and send as bulk
			ChangeEvent	ce	= new ChangeEvent(null, "component-removed", desc);
			rcl.changeOccurred(ce).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
//					System.out.println("update succeeded: "+desc);
				}
				public void exceptionOccurred(Exception exception)
				{
					if(cms!=null)
					{
//						System.out.println("Removing listener due to failed update: "+RemoteCMSListener.this.id);
						try
						{
							cms.removeComponentListener(null, RemoteCMSListener.this);
						}
						catch(RuntimeException e)
						{
//							System.out.println("Listener already removed: "+id);
						}
						RemoteCMSListener.this.cms	= null;	// Set to null to avoid multiple removal due to delayed errors. 
					}
				}
			});
			return IFuture.DONE;
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