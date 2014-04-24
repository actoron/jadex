package jadex.platform.service.persistence;

import java.util.Arrays;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.kernelbase.IBootstrapFactory;
import jadex.platform.service.cms.DecoupledComponentManagementService;
import jadex.platform.service.cms.StandaloneComponentAdapter;

/**
 *  CMS with additional persistence functionality.
 */
@Service
public class PersistenceComponentManagementService	extends DecoupledComponentManagementService
{
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
								// Todo: allow adapting component identifier (e.g. to changed platform suffix).
								Future<Void>	init	= new Future<Void>();
								final IFuture<Tuple2<IComponentInstance, IComponentAdapter>>	tupfut	=
									factory.createComponentInstance(pi.getComponentDescription(), getComponentAdapterFactory(), model, 
									null, null, parent, null, copy, realtime, persist, pi, null, init);
								
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
															done(tup);
														}
													});
												}
											}
											
											public void done(Tuple2<IComponentInstance, IComponentAdapter> tup)
											{
												
												adapters.put(pi.getComponentDescription().getName(), tup.getSecondEntity());
												tup.getSecondEntity().wakeup();
												
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
