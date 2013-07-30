package jadex.bridge.nonfunctional;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *  A non-functional property.
 *  
 *  NOTE: Implementing classes must implement a constructor with
 *  the signature INFProperty(String name) to allow the service
 *  to initialize the property during creation.
 */
public abstract class AbstractNFProperty<T extends Object, U extends Object> implements INFProperty<T, U>
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
	 *  @param type Type of the value.
	 *  @return The current value of the property.
	 */
	public T getValue(Class<T> type)
	{
		return getValue(type, null);
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
	public static List<INFProperty<?, ?>> readNFProperties(Class<?> type)
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
				Class<?> clazz = nfprop.type();
				try
				{
					Constructor<?> con = clazz.getConstructor(String.class);
					INFProperty<?, ?> prop = (INFProperty<?, ?>)con.newInstance(nfprop.name());
					
					if(ret==null)
						ret = new ArrayList<INFProperty<?,?>>();
					ret.add(prop);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return ret;
	}
}
