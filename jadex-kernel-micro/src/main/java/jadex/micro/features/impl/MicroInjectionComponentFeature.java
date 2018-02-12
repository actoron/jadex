package jadex.micro.features.impl;

import java.lang.reflect.Field;
import java.util.Map;

import jadex.base.Starter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.FieldInfo;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;
import jadex.micro.MicroModel;
import jadex.micro.features.IMicroInjectionFeature;

/**
 *  Inject agent arguments into annotated field values.
 */
public class MicroInjectionComponentFeature extends	AbstractComponentFeature	implements IMicroInjectionFeature
{
	//-------- constants ---------
	
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(
		IMicroInjectionFeature.class, MicroInjectionComponentFeature.class,
		new Class<?>[]{IPojoComponentFeature.class, IArgumentsResultsFeature.class, IRequiredServicesFeature.class}, new Class<?>[]{IProvidedServicesFeature.class});

	//-------- constructors --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public MicroInjectionComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}

	/**
	 *  Initialize the feature.
	 *  Empty implementation that can be overridden.
	 */
	public IFuture<Void> init()
	{
		final Future<Void> ret = new Future<Void>();
				
		Map<String, Object>	args = getComponent().getComponentFeature(IArgumentsResultsFeature.class).getArguments();
		Map<String, Object>	results	= getComponent().getComponentFeature(IArgumentsResultsFeature.class).getResults();
		final MicroModel model = (MicroModel)getComponent().getModel().getRawModel();
		final Object agent = getComponent().getComponentFeature(IPojoComponentFeature.class).getPojoAgent();

		// Inject agent fields.
		FieldInfo[] fields = model.getAgentInjections();
		for(int i=0; i<fields.length; i++)
		{
			try
			{
				Field f = fields[i].getField(getComponent().getClassLoader());
				f.setAccessible(true);
				f.set(agent, getComponent());
			}
			catch(Exception e)
			{
				getComponent().getLogger().warning("Agent injection failed: "+e);
			}
		}

		// Inject argument values
		if(args!=null)
		{
			String[] names = model.getArgumentInjectionNames();
			if(names.length>0)
			{
				for(int i=0; i<names.length; i++)
				{
					Object val = args.get(names[i]);
					
//					if(val!=null || getModel().getArgument(names[i]).getDefaultValue()!=null)
					final Tuple2<FieldInfo, String>[] infos = model.getArgumentInjections(names[i]);
					
					try
					{
						for(int j=0; j<infos.length; j++)
						{
							Field field = infos[j].getFirstEntity().getField(getComponent().getClassLoader());
							String convert = infos[j].getSecondEntity();
//							System.out.println("seting arg: "+names[i]+" "+val);
							setFieldValue(val, field, convert);
						}
					}
					catch(Exception e)
					{
						getComponent().getLogger().warning("Field injection failed: "+e);
						if(!ret.isDone())
							ret.setException(e);
					}
				}
			}
		}
		
		// Inject default result values
		if(results!=null)
		{
			String[] names = model.getResultInjectionNames();
			if(names.length>0)
			{
				for(int i=0; i<names.length; i++)
				{
					if(results.containsKey(names[i]))
					{
						Object val = results.get(names[i]);
						final Tuple3<FieldInfo, String, String> info = model.getResultInjection(names[i]);
						
						try
						{
							Field field = info.getFirstEntity().getField(getComponent().getClassLoader());
							String convert = info.getSecondEntity();
//							System.out.println("seting res: "+names[i]+" "+val);
							setFieldValue(val, field, convert);
						}
						catch(Exception e)
						{
							getComponent().getLogger().warning("Field injection failed: "+e);
							if(!ret.isDone())
								ret.setException(e);
						}
					}
				}
			}
		}
		
		// Inject feature fields.
		fields = model.getFeatureInjections();
		for(int i=0; i<fields.length; i++)
		{
			try
			{
				Class<?> iface = getComponent().getClassLoader().loadClass(fields[i].getTypeName());
				Object feat = getComponent().getComponentFeature(iface);
				Field f = fields[i].getField(getComponent().getClassLoader());
				f.setAccessible(true);
				f.set(agent, feat);
			}
			catch(Exception e)
			{
				getComponent().getLogger().warning("Feature injection failed: "+e);
				if(!ret.isDone())
					ret.setException(e);
			}
		}
		
		// Inject parent
		final FieldInfo[]	pis	= model.getParentInjections();
		if(pis.length>0)
		{
			IComponentManagementService cms = SServiceProvider.getLocalService(getComponent(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			cms.getExternalAccess(getComponent().getComponentIdentifier().getParent())
				.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
			{
				public void customResultAvailable(IExternalAccess exta)
				{
					final CounterResultListener<Void> lis = new CounterResultListener<Void>(pis.length, new DelegationResultListener<Void>(ret));
					
					for(int i=0; i<pis.length; i++)
					{
						final Field	f	= pis[i].getField(getComponent().getClassLoader());
						if(IExternalAccess.class.equals(f.getType()))
						{
							try
							{
								f.setAccessible(true);
								f.set(agent, exta);
								lis.resultAvailable(null);
							}
							catch(Exception e)
							{
								exceptionOccurred(e);
							}
						}
						else if(getComponent().getComponentDescription().isSynchronous())
						{
							exta.scheduleStep(new IComponentStep<Void>()
							{
								public IFuture<Void> execute(IInternalAccess ia)
								{
									Object pagent = ia.getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
									if(SReflect.isSupertype(f.getType(), pagent.getClass()))
									{
										try
										{
											f.setAccessible(true);
											f.set(agent, pagent);
											lis.resultAvailable(null);
										}
										catch(Exception e)
										{
											exceptionOccurred(e);
										}
									}
									else
									{
										throw new RuntimeException("Incompatible types for parent injection: "+pagent+", "+f);													
									}
									return IFuture.DONE;
								}
							});
						}
						else
						{
							exceptionOccurred(new RuntimeException("Non-external parent injection for non-synchronous subcomponent not allowed: "+f));
						}
					}
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
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
	
	//-------- helper methods --------
	
	/**
	 *  Set an injected field value.
	 */
	protected void setFieldValue(Object val, Field field, String convert)
	{
		if(val!=null || !SReflect.isBasicType(field.getType()))
		{
			try
			{
				Object agent = getComponent().getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
				if(convert!=null)
				{
					SimpleValueFetcher fetcher = new SimpleValueFetcher(getComponent().getFetcher());
					fetcher.setValue("$value", val);
					val = SJavaParser.evaluateExpression(convert, getComponent().getModel().getAllImports(), fetcher, getComponent().getClassLoader());
				}
				field.setAccessible(true);
				field.set(agent, val);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}
