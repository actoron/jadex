package jadex.bridge.sensor.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import jadex.bridge.IInternalAccess;
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
			if(params.containsKey(NAME))
			{
				ret.setResult(convertToCollection(params.get(NAME)));
				found = true;
			}
			else if(params.containsKey("argument"))
			{
				Map<String, Object> args = component.getComponentFeature(IArgumentsResultsFeature.class).getArguments();
				ret.setResult(convertToCollection(args.get((String)params.get("argument"))));
				found = true;
			}
		}
		
		// directly search argument "tag"
		if(!found)
		{
			Map<String, Object> args = component.getComponentFeature(IArgumentsResultsFeature.class).getArguments();
			if(args.containsKey(NAME))
			{
				ret.setResult(convertToCollection(args.get(NAME)));
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
	protected Collection<String> convertToCollection(Object obj)
	{
		Collection<String> ret = null;
		
		if(obj instanceof String)
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
}