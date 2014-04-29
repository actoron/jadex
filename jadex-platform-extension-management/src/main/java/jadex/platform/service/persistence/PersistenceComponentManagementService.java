package jadex.platform.service.persistence;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.factory.IComponentAdapterFactory;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.kernelbase.IBootstrapFactory;
import jadex.platform.service.cms.ComponentAdapterFactory;
import jadex.platform.service.cms.ComponentManagementService;
import jadex.platform.service.cms.IntermediateResultListener;
import jadex.platform.service.cms.StandaloneComponentAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *  CMS with additional persistence functionality.
 */
@Service
public class PersistenceComponentManagementService	extends ComponentManagementService
{
	//-------- constants --------
	
	/** The default minimum time span of inactivity after which a component is persisted. */
	public static final long DEFAULT_PERSIST_DELAY	= BasicService.getScaledLocalDefaultTimeout(1);
	
	/** The offset between minimum and maximum persist delay (e.g. delay=30 offset=0.5 -> maximum=30+30*0.5=45). */
	public static final double DEFAULT_PERSIST_OFFSET	= 0.5;
	
	//-------- attributes --------
	
	/** The inactive components sorted by last activity (most recent is last). */
	protected Set<IComponentIdentifier>	lrucomponents;
	
	/** The time span of inactivity after which a component is persisted. */
	protected long	persistdelay;
	
	/** The offset between minimum and maximum persist delay. */
	protected double persistoffset;
	
	/** The flag when the timer is active. */
	protected boolean	timerrunning;
	
	/** The external access of the cms component. */
	@ServiceComponent
	protected IExternalAccess	access;
	
	//-------- constructors --------
	
	/**
	 *  Static method for reflective creation to allow platform start without add-on.
	 */
	public static PersistenceComponentManagementService	create(IComponentAdapter root, IBootstrapFactory componentfactory,
		boolean copy, boolean realtime, boolean persist, boolean uniqueids)
	{
		return new PersistenceComponentManagementService(root, componentfactory, copy, realtime, persist, uniqueids);
	}
	
	/**
	 *  Create a persistence CMS.
	 */
	public PersistenceComponentManagementService(IComponentAdapter root, IBootstrapFactory componentfactory,
		boolean copy, boolean realtime, boolean persist, boolean uniqueids)
	{
		super(root, componentfactory, copy, realtime, persist, uniqueids);
		this.lrucomponents	= new LinkedHashSet<IComponentIdentifier>();
		this.persistdelay	= DEFAULT_PERSIST_DELAY;
		this.persistoffset	= DEFAULT_PERSIST_OFFSET;
	}
	
	//-------- methods --------
	
	/**
	 *  Create the adapter factory.
	 */
	protected IComponentAdapterFactory createAdapterFactory()
	{
		if(persist)
		{
			return new ComponentAdapterFactory()
			{
				public IComponentAdapter createComponentAdapter(IComponentDescription desc, IModelInfo model,
					IComponentInstance instance, IExternalAccess parent)
				{
					return new PersistentComponentAdapter(desc, model, instance, parent,
						PersistenceComponentManagementService.this, clockservice);
				}
			};
		}
		else
		{
			return super.createAdapterFactory();
		}
	}
	
	/**
	 *  Add a component to the LRU table.
	 */
	public void addLRUComponent(IComponentIdentifier cid)
	{
		boolean	starttimer;
		synchronized(lrucomponents)
		{
			starttimer	= !timerrunning && lrucomponents.isEmpty();
			if(starttimer)
			{
				timerrunning	= true;
			}
			lrucomponents.add(cid);
		}
		
		if(starttimer)
		{
			access.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(final IInternalAccess ia)
				{
					ia.waitForDelay(persistdelay)
						.addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								long	current	= clockservice.getTime();
								List<IComponentIdentifier>	persistables	= null;
								long	nexttime	= -1;
								synchronized(lrucomponents)
								{
									for(IComponentIdentifier cid: lrucomponents)
									{
										PersistentComponentAdapter	pca	= (PersistentComponentAdapter)internalGetComponentAdapter(cid);
										if(pca!=null)
										{
											long	last	= pca.getLastStepTime();
											if(last+persistdelay>=current)
											{
												if(persistables==null)
												{
													persistables	= new ArrayList<IComponentIdentifier>();
												}
												persistables.add(cid);
											}
											else
											{
												nexttime	= last - current + persistdelay + (long)(persistdelay*persistoffset);
												break;
											}
										}
									}
									
									if(nexttime==-1)
									{
										timerrunning	= false;
									}
								}
								
								if(persistables!=null)
								{
									for(final IComponentIdentifier cid: persistables)
									{
										getComponentInstance(internalGetComponentAdapter(cid)).getPersistableState()
											.addResultListener(new IResultListener<IPersistInfo>()
										{
											public void resultAvailable(IPersistInfo pi)
											{
												System.out.println("Got persist info for "+cid+": "+pi);
											}

											public void exceptionOccurred(Exception exception)
											{
												if(!(exception instanceof ComponentTerminatedException))
												{
													ia.getLogger().severe("Exception when persisting component "+cid+": "+exception);
												}
											}
										});
									}
								}
								
								if(nexttime!=-1)
								{
									ia.waitForDelay(nexttime).addResultListener(this);
								}
							}
							
							public void exceptionOccurred(Exception exception)
							{
								if(!(exception instanceof ComponentTerminatedException))
								{
									ia.getLogger().severe("Exception in persistence timer: "+exception);
								}
							}
						});
					return IFuture.DONE;
				}
			});
		}
	}
	
	/**
	 *  Remove a component from the LRU table.
	 */
	public void removeLRUComponent(IComponentIdentifier cid)
	{
		synchronized(lrucomponents)
		{
			lrucomponents.remove(cid);
		}
	}
	
	/**
	 *  Gets the component state.
	 *  
	 *  @param cid The component.
	 *  @return The component state.
	 */
	public IFuture<IPersistInfo> getPersistableState(IComponentIdentifier cid)
	{
		final Future<IPersistInfo> ret = new Future<IPersistInfo>();
		
		final IComponentAdapter adapter = adapters.get(cid);
		adapter.invokeLater(new Runnable()
		{
			public void run()
			{
				final IComponentInstance instance = ((StandaloneComponentAdapter)adapter).getComponentInstance();
				instance.getPersistableState().addResultListener(new DelegationResultListener<IPersistInfo>(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Resurrect a persisted component.
	 */
	public IFuture<Void>	resurrectComponent(final IPersistInfo pi)
	{
		final Future<Void>	ret	= new Future<Void>();
				
		// Todo: allow unpersisting at a different parent? 
		getExternalAccess(pi.getComponentDescription().getName().getParent())
			.addResultListener(createResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
		{
			public void customResultAvailable(final IExternalAccess parent)
			{
				// cinfo only needed for imports -> can be empty as model name is fully qualified.
				getComponentFactory(pi.getModelFileName(), new CreationInfo(), pi.getComponentDescription().getResourceIdentifier())
					.addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentFactory, Void>(ret)
				{
					public void customResultAvailable(final IComponentFactory factory)
					{
						factory.loadModel(pi.getModelFileName(), null, pi.getComponentDescription().getResourceIdentifier())
							.addResultListener(createResultListener(new ExceptionDelegationResultListener<IModelInfo, Void>(ret)
						{
							public void customResultAvailable(final IModelInfo model)
							{
								
								IntermediateResultListener	reslis;
								if(resultlisteners.containsKey(pi.getComponentDescription().getName()))
								{
									reslis	= resultlisteners.get(pi.getComponentDescription().getName());
								}
								else
								{
									reslis	= new IntermediateResultListener(null);	
									resultlisteners.put(pi.getComponentDescription().getName(), reslis);
								}

								// Todo: allow adapting component identifier (e.g. to changed platform suffix).
								Future<Void>	init	= new Future<Void>();
								final IFuture<Tuple2<IComponentInstance, IComponentAdapter>>	tupfut	=
									factory.createComponentInstance(pi.getComponentDescription(), getComponentAdapterFactory(), model, 
									null, null, parent, null, copy, realtime, persist, pi, reslis, init);
								
								init.addResultListener(new ExceptionDelegationResultListener<Void, Void>(ret)
								{
									public void customResultAvailable(Void result)
									{
										tupfut.addResultListener(createResultListener(new ExceptionDelegationResultListener<Tuple2<IComponentInstance, IComponentAdapter>, Void>(ret)
										{
											public void customResultAvailable(final Tuple2<IComponentInstance, IComponentAdapter> tup)
											{
												IComponentAdapter	pad	= internalGetComponentAdapter(parent.getComponentIdentifier());
												if(Arrays.asList(((CMSComponentDescription)pad.getDescription()).getChildren()).contains(pi.getComponentDescription().getName()))
												{
													done(tup);											
												}
												
												// If component hull no longer present, readd component at parent.
												else
												{
													addSubcomponent(pad, pi.getComponentDescription(), model)
														.addResultListener(new ExceptionDelegationResultListener<Void, Void>(ret)
													{
														public void customResultAvailable(Void result)
														{
															notifyListenersAdded(pi.getComponentDescription().getName(), pi.getComponentDescription());
															done(tup);
														}
													});
												}
											}
											
											public void done(Tuple2<IComponentInstance, IComponentAdapter> tup)
											{
												adapters.put(pi.getComponentDescription().getName(), tup.getSecondEntity());
												getComponentAdapterFactory().initialWakeup(tup.getSecondEntity());
												
												ret.setResult(null);
											}
										}));
									}
								});
							}
						}));
					}
				}));
			}
		}));

		return ret;
	}
}
