package jadex.bridge.nonfunctional;


import jadex.bridge.IExternalAccess;
import jadex.bridge.component.INFPropertyComponentFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Property reference. Delegates calls to the real
 *  property source. 
 */
public class NFPropertyRef<T, U> extends AbstractNFProperty<T, U>
{
	/** The value source. */
	protected INFPropertyProvider source;
	
	/** The component of the ref. */
	protected IExternalAccess comp;
	
	/**
	 *  Create a new property ref.
	 */
	public NFPropertyRef(INFPropertyProvider source, IExternalAccess comp, NFPropertyMetaInfo mi)
	{
//		super(new NFPropertyMetaInfo(CPULoadProperty.CPULOAD, double.class, null, true, -1, Target.Root));
		super(mi);
		this.source = source;
		this.comp = comp;
	}

	/**
	 *  Returns the current value of the property, performs unit conversion if necessary.
	 *  
	 *  @param type Type of the value.
	 *  @param unit Unit of the returned value.
	 *  
	 *  @return The current value of the property.
	 */
//	public IFuture<T> getValue(Class<U> unit)
	public IFuture<T> getValue(U unit)
	{
		final Future<T> ret = new Future<T>();
		IFuture<T> fut = source.getNFPropertyValue(getName(), unit);
		fut.addResultListener(new IResultListener<T>()
		{
			public void resultAvailable(T result)
			{
				ret.setResult(result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				((INFPropertyProvider)comp.getExternalComponentFeature(INFPropertyComponentFeature.class)).removeNFProperty(getName());
//				SNFPropertyProvider.removeNFProperty(comp, name)
				
				// todo: remote case?
				source.removeNFProperty(getName());
				ret.setException(exception);
			}
		});
		return ret;
	}
}

