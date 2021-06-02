package jadex.micro.features.impl;

import java.lang.reflect.Field;
import java.util.Map;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.FieldInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;
import jadex.micro.InjectionInfoHolder;
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
		final Object agent = getComponent().getFeature(IPojoComponentFeature.class).getPojoAgent();
		
		return injectStuff(component, agent, ((MicroModel)component.getModel().getRawModel()).getInjectionInfoHolder());
	}
	
	/**
	 *  Inject according to the annotations.
	 *  @param component The component.
	 *  @param target The target.
	 */
	public static IFuture<Void> injectStuff(IInternalAccess component, Object target, InjectionInfoHolder holder)
	{
		final Future<Void> ret = new Future<>();
		
		Map<String, Object>	args = component.getFeature(IArgumentsResultsFeature.class).getArguments();
		Map<String, Object>	results	= component.getFeature(IArgumentsResultsFeature.class).getResults();
		//final MicroModel model = (MicroModel)component.getModel().getRawModel();

		try
		{
			// Inject agent fields.
			FieldInfo[] fields = holder.getAgentInjections();
			for(int i=0; i<fields.length; i++)
			{
				Field f = fields[i].getField(component.getClassLoader());
				f.setAccessible(true);
				f.set(target, component);
			}
	
			// Inject argument values
			if(args!=null)
			{
				String[] names = holder.getArgumentInjectionNames();
				if(names.length>0)
				{
					for(int i=0; i<names.length; i++)
					{
						if(args.containsKey(names[i]))
						{
							Object val = args.get(names[i]);
							
		//					if(val!=null || getModel().getArgument(names[i]).getDefaultValue()!=null)
							final Tuple2<FieldInfo, String>[] infos = holder.getArgumentInjections(names[i]);
							
							for(int j=0; j<infos.length; j++)
							{
								Field field = infos[j].getFirstEntity().getField(component.getClassLoader());
								String convert = infos[j].getSecondEntity();
//								System.out.println("setting arg: "+names[i]+" "+val);
								setFieldValue(component, target, val, field, convert);
							}
						}
					}
				}
			}
			
			// Inject default result values
			if(results!=null)
			{
				String[] names = holder.getResultInjectionNames();
				if(names.length>0)
				{
					for(int i=0; i<names.length; i++)
					{
						if(results.containsKey(names[i]))
						{
							Object val = results.get(names[i]);
							final Tuple3<FieldInfo, String, String> info = holder.getResultInjection(names[i]);
							
							Field field = info.getFirstEntity().getField(component.getClassLoader());
							String convert = info.getSecondEntity();
//							System.out.println("seting res: "+names[i]+" "+val);
							setFieldValue(component, target, val, field, convert);
						}
					}
				}
			}
			
			// Inject feature fields.
			fields = holder.getFeatureInjections();
			for(int i=0; i<fields.length; i++)
			{
				Class<?> iface = component.getClassLoader().loadClass(fields[i].getTypeName());
				Object feat = component.getFeature(iface);
				Field f = fields[i].getField(component.getClassLoader());
				f.setAccessible(true);
				f.set(target, feat);
			}
			
			// Inject parent
			final FieldInfo[]	pis	= holder.getParentInjections();
			if(pis.length>0)
			{
//				IComponentManagementService cms = getComponent().getFeature(IRequiredServicesFeature.class).getLocalService(new ServiceQuery<>(IComponentManagementService.class));
				IExternalAccess exta = component.getExternalAccess(component.getId().getParent());
				final CounterResultListener<Void> lis = new CounterResultListener<Void>(pis.length, new DelegationResultListener<Void>(ret));
				
				for(int i=0; i<pis.length; i++)
				{
					final Field	f	= pis[i].getField(component.getClassLoader());
					if(IExternalAccess.class.equals(f.getType()))
					{
						try
						{
							f.setAccessible(true);
							f.set(target, exta);
							lis.resultAvailable(null);
						}
						catch(Exception e)
						{
							ret.setException(e);
						}
					}
					else if(component.getDescription().isSynchronous())
					{
						exta.scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								Object pagent = ia.getFeature(IPojoComponentFeature.class).getPojoAgent();
								if(SReflect.isSupertype(f.getType(), pagent.getClass()))
								{
									try
									{
										f.setAccessible(true);
										f.set(target, pagent);
										lis.resultAvailable(null);
									}
									catch(Exception e)
									{
										ret.setException(e);
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
						ret.setException(new RuntimeException("Non-external parent injection for non-synchronous subcomponent not allowed: "+f));
					}
				}
			}
			else
			{
				ret.setResult(null);
			}
		}
		catch(Exception e)
		{
			ret.setException(e);
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
	protected static void setFieldValue(IInternalAccess component, Object target, Object val, Field field, String convert)
	{
		if(val!=null || !SReflect.isBasicType(field.getType()))
		{
			try
			{
				//Object agent = component.getFeature(IPojoComponentFeature.class).getPojoAgent();
				if(convert!=null)
				{
					SimpleValueFetcher fetcher = new SimpleValueFetcher(component.getFetcher());
					fetcher.setValue("$value", val);
					val = SJavaParser.evaluateExpression(convert, component.getModel().getAllImports(), fetcher, component.getClassLoader());
				}
				field.setAccessible(true);
//				if(field.getName().equals("address"))
//					System.out.println("setVal: "+getComponent().getId()+" "+val+" "+field.getName());
				//field.set(agent, val);
				field.set(target, val);
			}
			catch(Exception e)
			{
				throw SUtil.throwUnchecked(e);
			}
		}
	}
}
