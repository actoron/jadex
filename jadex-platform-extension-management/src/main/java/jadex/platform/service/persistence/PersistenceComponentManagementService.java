package jadex.platform.service.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.bridge.service.types.persistence.IIdleHook;
import jadex.bridge.service.types.persistence.IPersistenceService;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.kernelbase.IBootstrapFactory;
import jadex.platform.service.cms.ComponentManagementService;

/**
 *  CMS with additional persistence functionality.
 */
@Service
public class PersistenceComponentManagementService	extends ComponentManagementService	implements IPersistenceService
{
	//-------- attributes --------
	
	/** The idle hook, if any. */
	protected IIdleHook	hook;
	
	/** The persist flag. */
	// Todo: move to platform data ?
	protected boolean persist;
	
	//-------- constructors --------
	
	/**
	 *  Static method for reflective creation to allow platform start without add-on.
	 */
	public static PersistenceComponentManagementService	create(IPlatformComponentAccess access,	IBootstrapFactory componentfactory,
		boolean persist, boolean uniqueids)
	{
		return new PersistenceComponentManagementService(access, componentfactory, persist, uniqueids);
	}
	
	/**
	 *  Create a persistence CMS.
	 */
	public PersistenceComponentManagementService(IPlatformComponentAccess access, IBootstrapFactory componentfactory,
		boolean persist, boolean uniqueids)
	{
		super(access, componentfactory, uniqueids);
		
		this.persist	= persist;
	}
	
	//-------- methods --------
	
//	/**
//	 *  Create the adapter factory.
//	 */
//	protected IPlatformComponentFactory createAdapterFactory()
//	{
//		if(persist)
//		{
//			return new ComponentAdapterFactory()
//			{
//				public IComponentAdapter createComponentAdapter(IComponentDescription desc, IModelInfo model,
//					IComponentInterpreter instance, IExternalAccess parent)
//				{
//					return new PersistentComponentAdapter(desc, model, instance, parent, PersistenceComponentManagementService.this);
//				}
//			};
//		}
//		else
//		{
//			return super.createAdapterFactory();
//		}
//	}	
	
	//-------- recovery methods --------
	
	/**
	 *  Get the component state.
	 *  
	 *  @param cid The component to be saved.
	 *  @param recursive	True, if subcomponents should be saved as well.
	 *  @return The component(s) state.
	 */
	public IFuture<IPersistInfo> snapshot(IComponentIdentifier cid)
	{
		final Future<IPersistInfo>	ret	= new Future<IPersistInfo>();
		
		snapshot(Collections.singleton(cid), false)
			.addResultListener(new ExceptionDelegationResultListener<Collection<IPersistInfo>, IPersistInfo>(ret)
		{
			public void customResultAvailable(Collection<IPersistInfo> result)
			{
				assert result.size()==1;
				ret.setResult(result.iterator().next());
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the component states.
	 *  
	 *  @param cids The components to be saved.
	 *  @param recursive	True, if subcomponents should be saved as well.
	 *  @return The component state(s).
	 */
	public IFuture<Collection<IPersistInfo>> snapshot(Collection<IComponentIdentifier> cids, boolean recursive)
	{
		final Future<Collection<IPersistInfo>> ret = new Future<Collection<IPersistInfo>>();
		
		// Todo: locking of component structure, etc.
		
		if(recursive)
		{
			// Expand the list of components to save.
			List<IComponentIdentifier>	list	= new ArrayList<IComponentIdentifier>(cids);
			Set<IComponentIdentifier>	included	= new HashSet<IComponentIdentifier>(list);
			for(int i=0; i<list.size(); i++)
			{
				for(IComponentIdentifier child: internalGetChildren(list.get(i)))
				{
					if(!included.contains(child))
					{
						list.add(child);
						included.add(child);
					}
				}
			}
			cids	= list;
		}

		CollectionResultListener<IPersistInfo>	crl	= new CollectionResultListener<IPersistInfo>(cids.size(),
			new DelegationResultListener<Collection<IPersistInfo>>(ret));
		
		for(final IComponentIdentifier cid: cids)
		{
			final Future<IPersistInfo>	fut	= new Future<IPersistInfo>();
			fut.addResultListener(crl);
			
			getExternalAccess(cid)
				.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, IPersistInfo>(fut)
			{
				public void customResultAvailable(IExternalAccess exta)
				{
					// Todo
//					final IComponentInterpreter	instance	= getComponentInstance(internalGetComponentAdapter(cid));
//					
//					// Fetch persistable state on component thread.
//					exta.scheduleImmediate(new IComponentStep<IPersistInfo>()
//					{
//						public IFuture<IPersistInfo> execute(IInternalAccess ia)
//						{
//							return new Future<IPersistInfo>(instance.getPersistableState());
//						}
//					})
//						.addResultListener(new DelegationResultListener<IPersistInfo>(fut));
				}
			});
		}
		
		return ret;

	}
	
	/**
	 *  Restore a component from a snapshot.
	 *  
	 *  @param pi	The component snapshot.
	 */
	public IFuture<Void>	restore(IPersistInfo pi)
	{
		return restore(Collections.singleton(pi));
	}
	
	/**
	 *  Restore components from a snapshot.
	 *  
	 *  @param pis	The component snapshots.
	 */
	public IFuture<Void>	restore(Collection<IPersistInfo> pis)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		// Todo: locking of component structure, etc.
		
		CounterResultListener<Void>	crl	= new CounterResultListener<Void>(pis.size(),
			new DelegationResultListener<Void>(ret));
		
		for(final IPersistInfo pi: pis)
		{
			final Future<Void>	fut	= new Future<Void>();
			fut.addResultListener(crl);
			
			// Todo: allow unpersisting at a different parent? 
			getExternalAccess(pi.getComponentDescription().getName().getParent())
				.addResultListener(createResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(fut)
			{
				public void customResultAvailable(final IExternalAccess parent)
				{
					// cinfo only needed for imports -> can be empty as model name is fully qualified.
					getComponentFactory(pi.getModelFileName(), new CreationInfo(), pi.getComponentDescription().getResourceIdentifier(), false, false)
						.addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentFactory, Void>(fut)
					{
						public void customResultAvailable(final IComponentFactory factory)
						{
							factory.loadModel(pi.getModelFileName(), null, pi.getComponentDescription().getResourceIdentifier())
								.addResultListener(createResultListener(new ExceptionDelegationResultListener<IModelInfo, Void>(fut)
							{
								public void customResultAvailable(final IModelInfo model)
								{
									
//									IntermediateResultListener	reslis;
//									if(resultlisteners.containsKey(pi.getComponentDescription().getName()))
//									{
//										reslis	= resultlisteners.get(pi.getComponentDescription().getName());
//									}
//									else
//									{
//										reslis	= new IntermediateResultListener(null);	
//										resultlisteners.put(pi.getComponentDescription().getName(), reslis);
//									}
		
									// Todo: allow adapting component identifier (e.g. to changed platform suffix).
//									Future<Void>	init	= new Future<Void>();
//									final IFuture<Tuple2<IComponentInstance, IComponentAdapter>>	tupfut	=
//										factory.createComponentInstance(pi.getComponentDescription(), getComponentAdapterFactory(), model, 
//										null, null, parent, null, null, copy, realtime, persist, pi, reslis, init, agent.getComponentFeature(IRequiredServicesFeature.class).getServiceRegistry());
//									
//									init.addResultListener(new ExceptionDelegationResultListener<Void, Void>(fut)
//									{
//										public void customResultAvailable(Void result)
//										{
//											tupfut.addResultListener(createResultListener(new ExceptionDelegationResultListener<Tuple2<IComponentInstance, IComponentAdapter>, Void>(fut)
//											{
//												public void customResultAvailable(final Tuple2<IComponentInstance, IComponentAdapter> tup)
//												{
//													IComponentAdapter	pad	= internalGetComponentAdapter(parent.getComponentIdentifier());
//													if(Arrays.asList(((CMSComponentDescription)pad.getDescription()).getChildren()).contains(pi.getComponentDescription().getName()))
//													{
//														done(tup);											
//													}
//													
//													// If component hull no longer present, readd component at parent.
//													else
//													{
//														addSubcomponent(pad, pi.getComponentDescription(), model)
//															.addResultListener(new ExceptionDelegationResultListener<Void, Void>(fut)
//														{
//															public void customResultAvailable(Void result)
//															{
//																notifyListenersAdded(pi.getComponentDescription().getName(), pi.getComponentDescription());
//																done(tup);
//															}
//														});
//													}
//												}
//												
//												public void done(Tuple2<IComponentInstance, IComponentAdapter> tup)
//												{
//													adapters.put(pi.getComponentDescription().getName(), tup.getSecondEntity());
//													getComponentAdapterFactory().initialWakeup(tup.getSecondEntity());
//													
//													fut.setResult(null);
//												}
//											}));
//										}
//									});
								}
							}));
						}
					}));
				}
			}));
		}

		return ret;
	}
	
	//-------- swap methods --------
	
	/**
	 *  Fetch the component state and transparently remove it from memory.
	 *  Keeps the component available in CMS to allow restoring it on access.
	 *  
	 *  @param cid	The component identifier.
	 *  @return The component state.
	 */
	public IFuture<IPersistInfo>	swapToStorage(IComponentIdentifier cid)
	{
		throw new UnsupportedOperationException("todo");
	}
	
	/**
	 *  Transparently restore the component state of a previously
	 *  swapped component.
	 *  
	 *  @param pi	The persist info.
	 */
	public IFuture<Void>	swapFromStorage(IPersistInfo pi)
	{
		throw new UnsupportedOperationException("todo");
	}
	
	/**
	 *  Set the idle hook to be called when a component becomes idle.
	 */
	@Excluded
	public IFuture<Void>	addIdleHook(@Reference IIdleHook hook)
	{
		IFuture<Void>	ret;
		
		if(this.hook!=null)
		{
			ret	= new Future<Void>(new RuntimeException("Only one idle hook allowed: "+this.hook+", "+hook));
		}
		else
		{
			this.hook	= hook;
			ret	= IFuture.DONE;
		}
		
		return ret;
	}
	
	/**
	 *  Called when a component becomes idle.
	 */
	protected void componentIdle(IComponentIdentifier cid)
	{
		if(hook!=null)
		{
			hook.componentIdle(cid);
		}
	}
	
	/**
	 *  Called when a component becomes active.
	 */
	protected void componentActive(IComponentIdentifier cid)
	{
		if(hook!=null)
		{
			hook.componentActive(cid);
		}
	}
}
