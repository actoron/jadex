package jadex.bridge.sensor.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.VersionInfo;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.service.IService;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Tagging a service with a string for searching specifically
 *  tagged services.
 *  
 *  Allows tagging with single tags and tag collections.
 *  a) allows for tagging via creation parameters
 *  b) allows for tagging by referencing an argument (which is read to get the tags)
 */
public class TagProperty extends AbstractNFProperty<Collection<String>, Void>
{
	/** The name of the property. */
	public static final String NAME = "tag";
	
	/** The argument constant. */
	public static final String ARGUMENT = "argument";
	
	/** The key used to store the tags in the service property map. */
	public static final String SERVICE_PROPERTY_NAME = "__service_tags__";
	
	/** The platform name tag. */
	private static final String PLATFORM_NAME_INTERNAL = "platform_name";
	public static final String PLATFORM_NAME = "\""+PLATFORM_NAME_INTERNAL+"\"";
	
	/** The Jadex version tag. */
	private static final String JADEX_VERSION_INTERNAL = "jadex_version";
	public static final String JADEX_VERSION = "\""+JADEX_VERSION_INTERNAL+"\"";
	
	/** The component. */
	protected IInternalAccess component;
	
	/** The parameters. */
	protected Map<String, Object> params;
	
	/**
	 * Creates the property.
	 */
	public TagProperty(IInternalAccess comp, IService service, MethodInfo method, Map<String, Object> params)
	{
		super(new NFPropertyMetaInfo(NAME, String.class, Void.class, false));
		this.component = comp;
		this.params = params;
	}
	
	/**
	 *  Returns the current value of the property, performs unit conversion if necessary.
	 *  @param unit Unit of the returned value.
	 *  @return The current value of the property.
	 */
	public IFuture<Collection<String>> getValue(Void unit)
	{
		Future<Collection<String>> ret = new Future<Collection<String>>();
		
		boolean found = false;
		if(params!=null)
		{
			Collection<String> tags = null;
			
			// get values directyl from init parameters under TAG
			if(params.containsKey(NAME))
			{
				Object vals = params.get(NAME);
				tags = createRuntimeTags(vals, component.getExternalAccess());
				found = true;
			}
			
			// get values from component args under name specified in ARGUMENT
			if(params.containsKey(ARGUMENT))
			{
				Map<String, Object> args = component.getFeature(IArgumentsResultsFeature.class).getArguments();
				Collection<String> tags2 = convertToCollection(args.get((String)params.get(ARGUMENT)));
				if(tags==null)
				{
					tags = tags2;
				}
				else
				{
					tags.addAll(tags2);
				}
				found = true;
			}
			
			if(found)
				ret.setResult(tags);
		}
		
		// directly search argument "tag"
		if(!found)
		{
			Map<String, Object> args = component.getFeature(IArgumentsResultsFeature.class).getArguments();
			if(args.containsKey(NAME))
			{
				Collection<String> tags = createRuntimeTags(args.get(NAME), component.getExternalAccess());
				ret.setResult(tags);
				found = true;
			}
		}
		
		if(!found)
			ret.setException(new RuntimeException("Could not evaluate tag value, no hint given (value or argument name)"));
		
		return ret;
	}
	
	/**
	 *  Convert user defined tag(s) to collection.
	 */
	protected static Collection<String> convertToCollection(Object obj)
	{
		Collection<String> ret = null;
		
		if(obj==null)
		{
			ret = Collections.emptyList();
		}
		else if(obj instanceof String)
		{
			ret = new ArrayList<String>();
			ret.add((String)obj);
		}
		else if(obj instanceof Collection)
		{
			ret = (Collection<String>)obj; 
		}
		else if(SReflect.isIterable(obj))
		{
			ret = new ArrayList<String>();
			Iterator<String> it = (Iterator)SReflect.getIterable(obj).iterator();
			while(it.hasNext())
			{
				ret.add(it.next());
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create a collection of tags and replace the variable values.
	 */
	public static Collection<String> createRuntimeTags(Object vals, IExternalAccess component)
	{
		Collection<String> tags = convertToCollection(vals);
		Iterator<String> it = tags.iterator();
		List<String> ret = new ArrayList<String>();
		for(int i=0; i<tags.size(); i++)
		{
			String tag = it.next();
			if(PLATFORM_NAME_INTERNAL.equals(tag) || PLATFORM_NAME.equals(tag))
			{
				tag = component.getIdentifier().getPlatformPrefix();
			}
			else if(JADEX_VERSION_INTERNAL.equals(tag) || JADEX_VERSION.equals(tag))
			{
				tag = VersionInfo.getInstance().getVersion();
			}
			ret.add(tag);
		}
		return ret;
	}
	
	/**
	 *  Check if it is a reserved tag.
	 *  @param tag The tag.
	 *  @return True if is reserved.
	 */
	public static void checkReservedTags(String[] tags)
	{
		for(String tag: tags)
		{
			checkReservedTag(tag);
		}
	}
	
	/**
	 *  Check if it is a reserved tag.
	 *  @param tag The tag.
	 *  @return True if is reserved.
	 */
	public static void checkReservedTag(String tag)
	{
		if(PLATFORM_NAME_INTERNAL.equals(tag) || PLATFORM_NAME.equals(tag) || JADEX_VERSION_INTERNAL.equals(tag) 
			|| JADEX_VERSION.equals(tag))
		{
			throw new IllegalArgumentException("Tag name is reserved.");
		}
	}
}