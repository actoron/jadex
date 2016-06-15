package jadex.bridge.service.search;

import jadex.bridge.IExternalAccess;
import jadex.bridge.nonfunctional.SNFPropertyProvider;
import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.IService;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 *  Tag filter class. Allows for filtering accoring to a collection of tags.
 *  Includes only services that contain all the tags.
 *  Replaces variables to dynamic values and uses TagProperty.createRuntimeTags() for that.
 */
public class TagFilter<T> implements IAsyncFilter<T>
{
	/** The component. */
	protected IExternalAccess component;
	
	/** The search tags. */
	protected Collection<String> tags;
	
	/**
	 *  Create a new tag filter.
	 */
	public TagFilter(IExternalAccess component, String[] tags)
	{
		this(component, tags==null? Collections.EMPTY_LIST: Arrays.asList(tags));
	}
	
	/**
	 *  Create a new tag filter.
	 */
	public TagFilter(IExternalAccess component, Collection<String> tags)
	{
		this.component = component;
		this.tags = TagProperty.createRuntimeTags(tags, component);
	}

	/**
	 *  Filter if a service contains all the tags.
	 */
	public IFuture<Boolean> filter(T ts)
	{
		final Future<Boolean> ret = new Future<Boolean>();
		IFuture<Collection<String>> fut = SNFPropertyProvider.getNFPropertyValue(component, ((IService)ts).getServiceIdentifier(), TagProperty.NAME);
		fut.addResultListener(new ExceptionDelegationResultListener<Collection<String>, Boolean>(ret)
		{
			public void customResultAvailable(Collection<String> result)
			{
				System.out.println("ser tag check: "+result);
				ret.setResult(result!=null && result.containsAll(tags));
			}
		});
		return ret;
	}
}