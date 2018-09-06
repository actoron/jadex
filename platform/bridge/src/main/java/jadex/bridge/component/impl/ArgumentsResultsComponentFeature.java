package jadex.bridge.component.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
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
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.collection.wrappers.MapWrapper;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.javaparser.IMapAccess;
import jadex.javaparser.SJavaParser;

/**
 *  This feature provides arguments.
 */
public class ArgumentsResultsComponentFeature extends AbstractComponentFeature	implements IArgumentsResultsFeature, IValueFetcher, IInternalArgumentsResultsFeature, IMapAccess
{
	//-------- attributes --------
	
	/** The arguments. */
	protected Map<String, Object>	arguments;
	
	/** The results. */
	protected Map<String, Object>	results;
	
	/** The result subscription, if any. */
	protected Set<SubscriptionIntermediateFuture<Tuple2<String, Object>>>	resfuts;
	
	/** Flag to remember when an exception was notified to a listener. */
	protected boolean	notified;
	
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
		
//		if(getComponent().getComponentIdentifier().getName().indexOf("secu")!=-1)
//			System.out.println("here");
		
		// Init the arguments with parameters.
		if(cinfo.getArguments()!=null)
		{
			for(Iterator<Map.Entry<String, Object>> it=cinfo.getArguments().entrySet().iterator(); it.hasNext(); )
			{
				Map.Entry<String, Object> entry = it.next();
				if(arguments==null)
					this.arguments	= new LinkedHashMap<String, Object>();
				arguments.put(entry.getKey(), entry.getValue());
			}
		}
		
		// Get the reverse name (agent1@app1.plat1 -> app1.agent1.<argname>)
		IComponentIdentifier cid = getComponent().getId();
		String dotname = cid.getDotName();
		int idx = dotname.lastIndexOf(".");
		if(idx!=-1)
		{
			dotname = dotname.substring(0, idx);
			dotname = getReverseName(dotname);
		}
		
		Map<String, Object>	platformargs = (Map<String, Object>)Starter.getPlatformValue(getComponent().getId().getRoot(),  IPlatformConfiguration.PLATFORMARGS);
		if(platformargs!=null)
		{
			IArgument[] margs = component.getModel().getArguments();
			for(int i=0; i<margs.length; i++)
			{
				if((arguments==null || !arguments.containsKey(margs[i].getName())))
				{
					if(arguments==null)
						this.arguments	= new LinkedHashMap<String, Object>();
					
					String argname = margs[i].getName();
					
//					if(dotname.toLowerCase().indexOf("cli")!=-1)
//						System.out.println("sdfhjsdf");
					
					// Test different versions of argument names
					// a) name directly contained
					if(platformargs.containsKey(argname))
					{
						arguments.put(argname, platformargs.get(argname));
					}
					// b1) agent name hierarchy aarg
					else if(platformargs.containsKey(cid.getLocalName()+argname))
					{
						arguments.put(argname, platformargs.get(cid.getLocalName()+argname));
					}
					// b2) agent name hierarchy a.arg
					else if(platformargs.containsKey(cid.getLocalName()+"."+argname))
					{
						arguments.put(argname, platformargs.get(cid.getLocalName()+"."+argname));
					}
					// c) agent name hierarchy b.a.arg
					else if(platformargs.containsKey(dotname+"."+argname))
					{
						arguments.put(argname, platformargs.get(dotname+"."+argname));
					}
					// d) agent type name
					else if(platformargs.containsKey(getComponent().getModel().getName()+"."+argname))
					{
						arguments.put(argname, platformargs.get(getComponent().getModel().getName()+"."+argname));
					}
//					// todo: e) agent type hierarchy name
//					else if(platformargs.containsKey(getComponent().getModel().getName()))
//					{
//						
//					}
				}
			}
		}
		
		initDefaultArguments();
		
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
		results.put(IComponentIdentifier.RESULTCID, getComponent().getId());
		
		initDefaultResults();

		return IFuture.DONE;
	}
	
	/**
	 *  Get the reverse name of a dot name (component id).
	 *  @return The reverse name as string.
	 */
	public static String getReverseName(String dotname)
	{
		List<String> res = new ArrayList<String>();
		StringTokenizer stok = new StringTokenizer(dotname, "@,");
		while(stok.hasMoreTokens())
		{
			res.add(0, stok.nextToken());
		}
		StringBuilder b = new StringBuilder();
		for(int i=0; i<res.size(); i++)
		{
			b.append(res.get(0));
			if(i+1<res.size())
				b.append(".");
		}
		return b.toString();
	}
	
	/**
	 *  Check if the feature potentially executed user code in body.
	 *  Allows blocking operations in user bodies by using separate steps for each feature.
	 *  Non-user-body-features are directly executed for speed.
	 *  If unsure just return true. ;-)
	 */
	public boolean	hasUserBody()
	{
		return false;
	}

	/**
	 *  Init unset arguments from default values.
	 */
	protected void initDefaultArguments()
	{
		// Init the remaining arguments with initial or default values.
		ConfigurationInfo	ci	= component.getConfiguration()!=null ? component.getModel().getConfiguration(component.getConfiguration()) : null;
		if(ci!=null)
		{
			UnparsedExpression[]	upes	= ci.getArguments();
			for(int i=0; i<upes.length; i++)
			{
				if(arguments==null || !arguments.containsKey(upes[i].getName()))
				{
					if(arguments==null)
						this.arguments	= new LinkedHashMap<String, Object>();
					arguments.put(upes[i].getName(), SJavaParser.getParsedValue(upes[i], component.getModel().getAllImports(), component.getFetcher(), component.getClassLoader()));
				}
			}
		}
		IArgument[] margs = component.getModel().getArguments();
		for(int i=0; i<margs.length; i++)
		{
			// Prevents unset arguments being added to be able to check whether a user has
			// set an argument explicitly to null or if it just is null (e.g. for field injections)
			if((arguments==null || !arguments.containsKey(margs[i].getName())))
			{
				if(arguments==null)
					this.arguments	= new LinkedHashMap<String, Object>();
				
//				Class<?> argclass = margs[i].getClazz().getType(getComponent().getClassLoader());
				if(margs[i].getDefaultValue().getValue()!=null && !arguments.containsKey(margs[i].getName()))
				{
					arguments.put(margs[i].getName(), SJavaParser.getParsedValue(margs[i].getDefaultValue(), component.getModel().getAllImports(), component.getFetcher(), component.getClassLoader()));
				}
				
				// Don't set basic type default values here, as they would overwrite java field initializer values.
				// Done lazy in get() instead.
//				else if(SReflect.isBasicType(argclass))
//				{
//					arguments.put(margs[i].getName(), SReflect.getDefaultValue(argclass));
//				}
			}
		}
	}
	
	/**
	 *  Init unset results from default values.
	 */
	protected void initDefaultResults()
	{
		// Init the results with initial or default values.
		ConfigurationInfo	ci	= component.getConfiguration()!=null ? component.getModel().getConfiguration(component.getConfiguration()) : null;
		
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
	}
	
	/**
	 *  Shutdown the feature.
	 */
	public IFuture<Void>	shutdown()
	{
		doCleanup();
		
		return IFuture.DONE;
	}
	
	/**
	 *  Kill is only invoked, when shutdown of some (e.g. other) feature does not return due to timeout.
	 *  The feature should do any kind of possible cleanup, but no asynchronous operations.
	 */
	public void kill()
	{
		doCleanup();
	}
	
	/**
	 *  Perform cleanup in shutdown or kill.
	 */
	protected void doCleanup()
	{
		if(resfuts!=null)
		{
			Exception ex = getComponent().getException();
			if(ex!=null)
			{
				notified = true;
				for(SubscriptionIntermediateFuture<Tuple2<String, Object>> fut: resfuts)
				{
					fut.setExceptionIfUndone(ex);
				}
			}
			else
			{
//				System.out.println("setFinished "+getComponent().getId());
				for(SubscriptionIntermediateFuture<Tuple2<String, Object>> fut: resfuts)
				{
					fut.setFinishedIfUndone();
				}				
			}
			resfuts	= null;
		}
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
			ret	= this;	// Use map access interface to provide default values for unset basic type args.
		}
		else
		{
			throw new RuntimeException("Value not found: "+name);
		}
		
		return ret;
	}
	
	//-------- IMapAccess --------
	
	/**
	 *  Provide default values for basic types, if not set. 
	 */
	public Object get(Object key)
	{
		Object	ret	= null;
		if(arguments.containsKey(key))
		{
			ret	= arguments.get(key);
		}
		else if(key instanceof String)
		{
			IArgument	arg	=	getComponent().getModel().getArgument((String)key);
			if(arg!=null)
			{
				Class<?> argclass = arg.getClazz().getType(getComponent().getClassLoader());
				if(SReflect.isBasicType(argclass))
				{
					ret	= SReflect.getDefaultValue(argclass);
				}
			}
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
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IFuture<Map<String, Object>> getArgumentsAsync()
	{
		return new Future<Map<String, Object>>(getArguments());
	}
	
	/**
	 *  Get the current results.
	 *  @return The current result values (if any).
	 */
	public IFuture<Map<String, Object>> getResultsAsync()
	{
		return new Future<Map<String, Object>>(getResults());
	}
	
	/**
	 * Subscribe to receive results.
	 */
	public ISubscriptionIntermediateFuture<Tuple2<String, Object>> subscribeToResults()
	{
		if(resfuts==null)
			resfuts	= new LinkedHashSet<SubscriptionIntermediateFuture<Tuple2<String,Object>>>();
		final SubscriptionIntermediateFuture<Tuple2<String, Object>>	ret	= new SubscriptionIntermediateFuture<Tuple2<String,Object>>();
		resfuts.add(ret);
		ret.setTerminationCommand(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				if(getComponent().getFeature(IExecutionFeature.class).isComponentThread())
				{
					resfuts.remove(ret);
				}
				else
				{
					getComponent().getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
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

	//-------- internal interface --------
	
	/**
	 *  Check if there is somebody waiting for this component to finish.
	 *  Used to decide if a fatal error needs to be printed to the console.
	 */
	public boolean	exceptionNotified()
	{
		return notified;
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
