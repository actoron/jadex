package jadex.platform.service.sensor;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class CPULoadProperty extends AbstractNFProperty<Double, Void>
{
	public static final String CPULOAD = "cpu load";
	
	/** The current cpu load. */
	protected double load;
	
	/** The component. */
	protected IInternalAccess comp;
	
	
	/**
	 *  Create a new property.
	 */
	public CPULoadProperty(final IInternalAccess comp)
	{
		super(new NFPropertyMetaInfo(CPULOAD, double.class, null, true, -1, Target.Root));
		this.comp = comp;
		
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
						root.addNFProperty(new CPULoadPropertyRef(comp.getExternalAccess(), root));
					}
				});
			}
		});
	}

	/**
	 *  Get the value.
	 */
	public IFuture<Double> getValue(Class<Void> unit)
	{
		return new Future<Double>(new Double(load));
	}

	/**
	 *  Get the load.
	 *  @return The load.
	 */
	public double getLoad()
	{
		return load;
	}
	
	/**
	 *  Set the load.
	 *  @param load The load to set.
	 */
	public void setLoad(double load)
	{
		this.load = load;
	}
}

