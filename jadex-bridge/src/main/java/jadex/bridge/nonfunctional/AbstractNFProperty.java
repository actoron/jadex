package jadex.bridge.nonfunctional;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.service.IService;
import jadex.commons.MethodInfo;
import jadex.commons.future.IFuture;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *  A non-functional property.
 *  
 *  NOTE: Implementing classes must implement a constructor with
 *  the signature INFProperty(String name) to allow the service
 *  to initialize the property during creation.
 */
public abstract class AbstractNFProperty<T, U> implements INFProperty<T, U>
{
	/** Name of the property. */
	protected NFPropertyMetaInfo metainfo;
	
	/**
	 *  Creates the property.
	 *  @param name Name of the property.
	 */
	public AbstractNFProperty(NFPropertyMetaInfo metainfo)
	{
		this.metainfo = metainfo;
	}
	
	/**
	 *  Gets the name of the property.
	 *  @return The name of the property.
	 */
	public String getName()
	{
		return metainfo.getName();
	}
	
	/**
	 *  Returns the current value of the property.
	 *  @return The current value of the property.
	 */
	public IFuture<T> getValue()
	{
		return getValue(null);
	}

	/**
	 *  Get the metainfo.
	 *  @return The metainfo.
	 */
	public NFPropertyMetaInfo getMetaInfo()
	{
		return metainfo;
	}
	
	/**
	 *  Create nf properties form a class with nf annotations.
	 */
	public static List<INFProperty<?, ?>> readNFProperties(Class<?> type, IInternalAccess comp, IService ser, MethodInfo mi)
	{
		List<INFProperty<?, ?>> ret = null;
		
		if(type.isAnnotationPresent(NFProperties.class))
		{
			List<NFProperty> nfprops = new ArrayList<NFProperty>();
			NFProperties typenfprops = type.getAnnotation(NFProperties.class);
			if(typenfprops != null)
				nfprops.addAll((Collection<? extends NFProperty>)Arrays.asList(typenfprops.value()));
			
			for(NFProperty nfprop : nfprops)
			{
				Class<?> clazz = nfprop.value();
				INFProperty<?, ?> prop = createProperty(clazz, comp, ser, mi);
				
				if(ret==null)
					ret = new ArrayList<INFProperty<?,?>>();
				ret.add(prop);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create a property instance from its type.
	 */
	public static INFProperty<?, ?> createProperty(Class<?> clazz, IInternalAccess comp, IService service, MethodInfo mi)
	{
		INFProperty<?, ?> prop = null;
		try
		{
			Constructor<?> con = clazz.getConstructor();
			prop = (INFProperty<?, ?>)con.newInstance();
		}
		catch(NoSuchMethodException e)
		{
			try
			{
				Constructor<?> con = clazz.getConstructor(new Class[]{IInternalAccess.class});
				prop = (INFProperty<?, ?>)con.newInstance(comp);
			}
			catch(NoSuchMethodException ex)
			{
				try
				{
					Constructor<?> con = clazz.getConstructor(new Class[]{IInternalAccess.class, IService.class, MethodInfo.class});
					prop = (INFProperty<?, ?>)con.newInstance(comp, service, mi);
				}
				catch(NoSuchMethodException ex2)
				{
					throw new RuntimeException("No suitable constructor: "+clazz, e);
				}
				catch(Exception eee)
				{
					throw new RuntimeException("Property creation exception: "+clazz, eee);
				}
			}
			catch(Exception ee)
			{
				throw new RuntimeException("Property creation exception: "+clazz, ee);
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException("Property creation exception: "+clazz, e);
		}
		
//		if(prop==null)
//			System.out.println("Property cannot be created: "+clazz);
//			throw new RuntimeException("Property cannot be created: "+clazz);
		
		return prop;
	}
	
	/**
	 *  Property was removed and should be disposed.
	 */
	public IFuture<Void> dispose()
	{
		return IFuture.DONE;
	}
}
