package jadex.bridge.nonfunctional;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;

/**
 * 
 */
public abstract class NFRootProperty<T, U> extends SimpleValueNFProperty<T, U>
{
	/**
	 *  Create a new property.
	 */
	public NFRootProperty(final IInternalAccess comp, final NFPropertyMetaInfo mi)
	{
//		super(new NFPropertyMetaInfo(CPULOAD, double.class, null, true, -1, Target.Root));
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
						NFPropertyMetaInfo cmi = new NFPropertyMetaInfo(mi.getName(), mi.getType(), mi.getUnit(), mi.isDynamic(), mi.getUpdateRate(), Target.Root);
						root.addNFProperty(new NFPropertyRef<T, U>(comp.getExternalAccess(), root, cmi));
					}
				});
			}
		});
	}
}

