package jadex.bridge.nonfunctional;


import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.MethodInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Property reference. Delegates calls to the real
 *  property source. 
 */
public class NFPropertyRef<T, U> extends AbstractNFProperty<T, U>
{
//	/** The value source. */
//	protected INFPropertyProvider source;
	
	/** The component of the ref. */
	protected IExternalAccess comp;
	
	/** The service identifier. */
	protected IServiceIdentifier sid;
	
	/** The method. */
	protected MethodInfo method;
	
	/**
	 *  Create a new property ref.
	 */
//	public NFPropertyRef(INFPropertyProvider source, IExternalAccess comp, NFPropertyMetaInfo mi)
	public NFPropertyRef(IExternalAccess comp, NFPropertyMetaInfo mi, IServiceIdentifier sid, MethodInfo method)
	{
//		super(new NFPropertyMetaInfo(CPULoadProperty.CPULOAD, double.class, null, true, -1, Target.Root));
		super(mi);
//		this.source = source;
		this.sid = sid;
		this.method = method;
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
//		IFuture<T> fut = source.getNFPropertyValue(getName(), unit);
		
		IFuture<T> fut;
		if(sid==null && method==null)
		{
			fut = comp.getNFPropertyValue(getName(), unit);
		}
		else if(sid!=null && method==null)
		{
			fut = comp.getNFPropertyValue(sid, getName(), unit);
		}
		else
		{
			fut = comp.getMethodNFPropertyValue(sid, method, getName(), unit);
		}
		
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
//				source.removeNFProperty(getName());
				
				if(sid==null && method==null)
				{
					comp.removeNFProperty(getName());
				}
				else if(sid!=null && method==null)
				{
					comp.removeNFProperty(sid, getName());
				}
				else
				{
					comp.removeMethodNFProperty(sid, method, getName());
				}
				
				ret.setException(exception);
			}
		});
		return ret;
	}
}

