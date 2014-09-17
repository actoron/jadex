package jadex.bridge.component.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IArgumentsFeature;
import jadex.bridge.component.IComponentFeature;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.javaparser.SJavaParser;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  This feature provides arguments.
 */
public class ArgumentsComponentFeature	extends	AbstractComponentFeature	implements IArgumentsFeature
{
	//-------- attributes --------
	
	/** The arguments. */
	protected Map<String, Object>	arguments;
	
	/** The results. */
	protected Map<String, Object>	results;
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor.
	 */
	public ArgumentsComponentFeature()
	{
	}
	
	protected ArgumentsComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	//-------- IComponentFeature interface --------
	
	/**
	 *  Get the user interface type of the feature.
	 */
	public Class<?>	getType()
	{
		return IArgumentsFeature.class;
	}
	
	/**
	 *  Create an instance of the feature.
	 */
	public IComponentFeature createInstance(IInternalAccess access, ComponentCreationInfo info)
	{
		return new ArgumentsComponentFeature(access, info);
	}
	
	/**
	 *  Initialize the feature.
	 */
	public IFuture<Void> init()
	{
		this.arguments	= new LinkedHashMap<String, Object>();
		
		if(cinfo.getArguments()!=null)
		{
			for(Iterator<Map.Entry<String, Object>> it=cinfo.getArguments().entrySet().iterator(); it.hasNext(); )
			{
				Map.Entry<String, Object> entry = it.next();
				arguments.put(entry.getKey(), entry.getValue());
			}
		}
		
		// Init the arguments with initial or default values.
		ConfigurationInfo	ci	= cinfo.getConfiguration()!=null ? component.getModel().getConfiguration(cinfo.getConfiguration()) : null;
		if(ci!=null)
		{
			UnparsedExpression[]	upes	= ci.getArguments();
			for(int i=0; i<upes.length; i++)
			{
				if(!arguments.containsKey(upes[i].getName()))
				{
					arguments.put(upes[i].getName(), SJavaParser.getParsedValue(upes[i], component.getModel().getAllImports(), component.getFetcher(), component.getClassLoader()));
				}
			}
		}
		IArgument[] margs = component.getModel().getArguments();
		for(int i=0; i<margs.length; i++)
		{
			// Prevents unset arguments being added to be able to check whether a user has
			// set an argument explicitly to null or if it just is null (e.g. for field injections)
			if(!arguments.containsKey(margs[i].getName()) && margs[i].getDefaultValue().getValue()!=null)
			{
				arguments.put(margs[i].getName(), SJavaParser.getParsedValue(margs[i].getDefaultValue(), component.getModel().getAllImports(), component.getFetcher(), component.getClassLoader()));
			}
		}
		
		// Init the results with initial or default values.
		
		// Hack?! add component identifier to result as long as we don't have better future type for results
		// could one somehow use the CallLocal for that purpose instead?
		// Todo: generate event.
		results.put(IComponentIdentifier.RESULTCID, getComponent().getComponentIdentifier());
		
		
		if(ci!=null)
		{
			UnparsedExpression[]	upes	= ci.getResults();
			for(int i=0; i<upes.length; i++)
			{
				results.put(upes[i].getName(), SJavaParser.getParsedValue(upes[i], component.getModel().getAllImports(), component.getFetcher(), component.getClassLoader()));
			}
		}
		IArgument[] res = component.getModel().getResults();
		for(int i=0; i<res.length; i++)
		{
			// Prevents unset results being added to be able to check whether a user has
			// set an argument explicitly to null or if it just is null (e.g. for field injections)
			if(!results.containsKey(res[i].getName()) && res[i].getDefaultValue().getValue()!=null)
			{
				results.put(res[i].getName(), SJavaParser.getParsedValue(res[i].getDefaultValue(), component.getModel().getAllImports(), component.getFetcher(), component.getClassLoader()));
			}
		}

		return IFuture.DONE;
	}
	
	/**
	 *  Execute the main activity of the feature.
	 */
	public IFuture<Void>	body()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Shutdown the feature.
	 */
	public IFuture<Void>	shutdown()
	{
		return IFuture.DONE;
	}
	
	//-------- IValueFetcher interface --------
	
	/**
	 *  Fetch the arguments.
	 */
	public Object fetchValue(String name)
	{
		Object	ret;
		if("$args".equals(name) || "$arguments".equals(name))
		{
			ret	= arguments;
		}
		else
		{
			throw new UnsupportedOperationException();
		}
		
		return ret;
	}
	
	//-------- IArgumentsFeature interface --------
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public Map<String, Object> getArguments()
	{
		return arguments;
	}
	
	/**
	 *  Get the current results.
	 *  @return The current result values (if any).
	 */
	public Map<String, Object> getResults()
	{
		return results;
	}
	
	/**
	 * Subscribe to receive results.
	 */
	public ISubscriptionIntermediateFuture<Tuple2<String, Object>> subscribeToResults()
	{
		throw new UnsupportedOperationException("todo");
	}
}
