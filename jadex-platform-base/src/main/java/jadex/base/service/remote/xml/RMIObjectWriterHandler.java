package jadex.base.service.remote.xml;

import jadex.base.service.remote.CallContext;
import jadex.base.service.remote.ProxyInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.IProxyable;
import jadex.commons.SReflect;
import jadex.xml.IContext;
import jadex.xml.TypeInfo;
import jadex.xml.bean.BeanObjectWriterHandler;
import jadex.xml.writer.WriteContext;
import jadex.xml.writer.WriteObjectInfo;

import java.util.Set;

import javax.xml.namespace.QName;

/**
 *  RMI version of Java bean object writer handler.
 *  Has the task to 
 *  a) replace IProxyable objects with ProxyInfo objects
 *  b) link ProxyInfo objects with IProxyable target objects
 *  (they are added to the targetobjects map of the context) 
 */
public class RMIObjectWriterHandler extends BeanObjectWriterHandler
{
	//-------- attributes --------
	
	/** The rms. */
	protected IComponentIdentifier rms;
	
	//-------- constructors --------
	
	/**
	 *  Create a new writer (gentypetags=false, prefertags=true, flattening=true).
	 */
	public RMIObjectWriterHandler(Set typeinfos, IComponentIdentifier rms)
	{
		super(typeinfos, true);
		this.rms = rms;
	}
	
	//-------- methods --------

	/**
	 *  Get the tag name for an object.
	 */
	public QName getTagName(Object object, IContext context)
	{
		return  super.getTagName(getProxyClass(object)==null? object: new ProxyInfo(), context); 
	}
		
	/**
	 *  Get write info for an object.
	 */
	public WriteObjectInfo getObjectWriteInfo(Object object, TypeInfo typeinfo, IContext context)
	{
		Class proxyclass = getProxyClass(object);
		
		if(proxyclass!=null)
		{
			WriteContext wc = (WriteContext)context;
			CallContext cc = (CallContext)wc.getUserContext();
			String tid = cc.putTargetObject(object);
			object = new ProxyInfo(rms, tid, proxyclass);
			// todo: more proxy information (caching, exclude, ...)
			typeinfo = null;
		}
		
		return super.getObjectWriteInfo(object, typeinfo, context);
	}
	
	/**
	 *  Get the proxy interface (null if none).
	 */
	protected Class getProxyClass(Object object)
	{
		Class proxyable = null;
		
		if(object!=null)
		{
			// todo?! search super types etc.
			Class[] interfaces = object.getClass().getInterfaces();
			for(int i=0; i<interfaces.length && proxyable==null; i++)
			{
				if(SReflect.isSupertype(IProxyable.class, interfaces[i]))
					proxyable = interfaces[i];
			}
		}
		
		return proxyable;
	}
}
