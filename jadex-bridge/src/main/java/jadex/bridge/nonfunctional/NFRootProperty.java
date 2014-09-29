package jadex.bridge.nonfunctional;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public abstract class NFRootProperty<T, U> extends SimpleValueNFProperty<T, U>
{
	/** The root access. */
	protected IExternalAccess root;
	
	/** The flag if the property has been injected to the root component. */
	protected boolean injected;
	
	/**
	 *  Create a new property.
	 */
	public NFRootProperty(final IInternalAccess comp, final NFPropertyMetaInfo mi)
	{
		this(comp, mi, true);
	}
	
	/**
	 *  Create a new property.
	 */
	public NFRootProperty(final IInternalAccess comp, final NFPropertyMetaInfo mi, boolean inject)
	{
		super(comp, mi);
		if(inject)
		{
			injectPropertyToRootComponent();
		}
	}
	
	/**
	 *  Inject the property to the root component.
	 */
	protected IFuture<Void> injectPropertyToRootComponent()
	{
		final Future<Void> ret = new Future<Void>();

		if(!injected)
		{
			this.injected = true;
			
			// Add property to root component
			SServiceProvider.getService(comp, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
			{
				public void customResultAvailable(IComponentManagementService cms)
				{
					cms.getExternalAccess(comp.getComponentIdentifier().getRoot()).addResultListener(new DefaultResultListener<IExternalAccess>()
					{
						public void resultAvailable(IExternalAccess root)
						{
							NFRootProperty.this.root = root;
							INFPropertyMetaInfo mi = getMetaInfo();
							NFPropertyMetaInfo cmi = new NFPropertyMetaInfo(mi.getName(), mi.getType(), mi.getUnit(), mi.isDynamic(), mi.getUpdateRate(), mi.isRealtime(), Target.Root);
							root.addNFProperty(new NFPropertyRef<T, U>(comp.getExternalAccess(), root, cmi)).addResultListener(new DelegationResultListener<Void>(ret));
						}
					});
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Property was removed and should be disposed.
	 */
	public IFuture<Void> dispose()
	{
		if(root!=null && injected)
		{
			root.removeNFProperty(getName());//.addResultListener(new DelegationResultListener<Void>(ret));
		}
		return super.dispose();
	}

	/**
	 *  Get the injected.
	 *  @return The injected.
	 */
	public boolean isInjected()
	{
		return injected;
	}
}

