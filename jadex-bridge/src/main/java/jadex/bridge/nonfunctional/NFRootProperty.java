package jadex.bridge.nonfunctional;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public abstract class NFRootProperty<T, U> extends SimpleValueNFProperty<T, U>
{
	/** The root access. */
	protected IExternalAccess root;
	
	/**
	 *  Create a new property.
	 */
	public NFRootProperty(final IInternalAccess comp, final NFPropertyMetaInfo mi)
	{
		super(comp, mi);
		
		// Add property to root component
		SServiceProvider.getService(comp.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DefaultResultListener<IComponentManagementService>()
		{
			public void resultAvailable(IComponentManagementService cms)
			{
				cms.getExternalAccess(comp.getComponentIdentifier().getRoot()).addResultListener(new DefaultResultListener<IExternalAccess>()
				{
					public void resultAvailable(IExternalAccess root)
					{
						NFRootProperty.this.root = root;
						NFPropertyMetaInfo cmi = new NFPropertyMetaInfo(mi.getName(), mi.getType(), mi.getUnit(), mi.isDynamic(), mi.getUpdateRate(), Target.Root);
						root.addNFProperty(new NFPropertyRef<T, U>(comp.getExternalAccess(), root, cmi));
					}
				});
			}
		});
	}
	
	/**
	 *  Property was removed and should be disposed.
	 */
	public IFuture<Void> dispose()
	{
//		final Future<Void> ret = new Future<Void>();
		if(root!=null)
		{
			root.removeNFProperty(getName());//.addResultListener(new DelegationResultListener<Void>(ret));
		}
		return super.dispose();
	}
}

