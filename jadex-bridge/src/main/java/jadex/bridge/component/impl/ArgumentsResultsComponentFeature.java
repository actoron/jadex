package jadex.bridge.component.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.IValueFetcher;
import jadex.commons.Tuple2;
import jadex.commons.collection.wrappers.MapWrapper;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.javaparser.SJavaParser;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *  This feature provides arguments.
 */
public class ArgumentsResultsComponentFeature	extends	AbstractComponentFeature	implements IArgumentsResultsFeature, IValueFetcher
{
	//-------- attributes --------
	
	/** The arguments. */
	protected Map<String, Object>	arguments;
	
	/** The results. */
	protected Map<String, Object>	results;
	
	/** The result subscription, if any. */
	protected Set<SubscriptionIntermediateFuture<Tuple2<String, Object>>>	resfuts;
	
	//-------- constructors --------
	
	/**
	 *  Create the feature.
	 */
	public ArgumentsResultsComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Initialize the feature.
	 */
	public IFuture<Void> init()
	{
//		System.out.println("cl: "+getComponent().getClassLoader());
		
		if(cinfo.getArguments()!=null)
		{
			for(Iterator<Map.Entry<String, Object>> it=cinfo.getArguments().entrySet().iterator(); it.hasNext(); )
			{
				Map.Entry<String, Object> entry = it.next();
				if(arguments==null)
				{
					this.arguments	= new LinkedHashMap<String, Object>();
				}
				arguments.put(entry.getKey(), entry.getValue());
			}
		}
		
		// Init the arguments with initial or default values.
		ConfigurationInfo	ci	= component.getConfiguration()!=null ? component.getModel().getConfiguration(component.getConfiguration()) : null;
		if(ci!=null)
		{
			UnparsedExpression[]	upes	= ci.getArguments();
			for(int i=0; i<upes.length; i++)
			{
				if(arguments==null || !arguments.containsKey(upes[i].getName()))
				{
					if(arguments==null)
					{
						this.arguments	= new LinkedHashMap<String, Object>();
					}
					arguments.put(upes[i].getName(), SJavaParser.getParsedValue(upes[i], component.getModel().getAllImports(), component.getFetcher(), component.getClassLoader()));
				}
			}
		}
		IArgument[] margs = component.getModel().getArguments();
		for(int i=0; i<margs.length; i++)
		{
			// Prevents unset arguments being added to be able to check whether a user has
			// set an argument explicitly to null or if it just is null (e.g. for field injections)
			if((arguments==null || !arguments.containsKey(margs[i].getName())) && margs[i].getDefaultValue().getValue()!=null)
			{
				if(arguments==null)
				{
					this.arguments	= new LinkedHashMap<String, Object>();
				}
				arguments.put(margs[i].getName(), SJavaParser.getParsedValue(margs[i].getDefaultValue(), component.getModel().getAllImports(), component.getFetcher(), component.getClassLoader()));
			}
		}
		
		// Init the results with initial or default values.
		
		// Hack?! add component identifier to result as long as we don't have better future type for results
		// could one somehow use the CallLocal for that purpose instead?
		this.results	= new MapWrapper<String, Object>(new LinkedHashMap<String, Object>())
		{
			protected void entryAdded(String key, Object value)
			{
				postEvent(key, value);
			}

			protected void entryRemoved(String key, Object value)
			{
				postEvent(key, null);
			}

			protected void entryChanged(String key, Object oldvalue, Object newvalue)
			{
				postEvent(key, newvalue);
			}
		};
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
	 *  Shutdown the feature.
	 */
	public IFuture<Void>	shutdown()
	{
		if(resfuts!=null)
		{
			Exception	ex	= getComponent().getException();
			if(ex!=null)
			{
				for(SubscriptionIntermediateFuture<Tuple2<String, Object>> fut: resfuts)
				{
					fut.setExceptionIfUndone(ex);
				}
			}
			else
			{
				for(SubscriptionIntermediateFuture<Tuple2<String, Object>> fut: resfuts)
				{
					fut.setFinishedIfUndone();
				}				
			}
		}
		
		return IFuture.DONE;
	}

	
	//-------- IValueFetcher interface --------
	
	/**
	 *  The feature can inject parameters for expression evaluation
	 *  by providing an optional value fetcher. The fetch order is the reverse
	 *  init order, i.e., later features can override values from earlier features.
	 */
	public IValueFetcher	getValueFetcher()
	{
		return this;
	}
	
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
			throw new RuntimeException("Value not found: "+name);
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
		return arguments==null? Collections.EMPTY_MAP: arguments;
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
		if(resfuts==null)
		{
			resfuts	= new LinkedHashSet<SubscriptionIntermediateFuture<Tuple2<String,Object>>>();
		}
		final SubscriptionIntermediateFuture<Tuple2<String, Object>>	ret	= new SubscriptionIntermediateFuture<Tuple2<String,Object>>();
		resfuts.add(ret);
		ret.setTerminationCommand(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				if(getComponent().getComponentFeature(IExecutionFeature.class).isComponentThread())
				{
					resfuts.remove(ret);
				}
				else
				{
					getComponent().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							resfuts.remove(ret);
							return IFuture.DONE;
						}
					});
				}
			}
		});
		return ret;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Post an event to subscribed listeners.
	 */
	protected void	postEvent(String result, Object value)
	{
		if(resfuts!=null)
		{
			Tuple2<String, Object>	event	= new Tuple2<String, Object>(result, value);
			for(SubscriptionIntermediateFuture<Tuple2<String, Object>> fut: resfuts)
			{
				fut.addIntermediateResultIfUndone(event);
			}
		}
	}
}
