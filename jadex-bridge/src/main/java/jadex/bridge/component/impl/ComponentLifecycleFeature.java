package jadex.bridge.component.impl;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IComponentLifecycleFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;

import java.util.ArrayList;
import java.util.List;

/**
 *  Feature that ensures the agent created(), body() and killed() are called on the pojo. 
 */
public class ComponentLifecycleFeature extends	AbstractComponentFeature implements IComponentLifecycleFeature
{
	//-------- constants --------
	
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(IComponentLifecycleFeature.class, ComponentLifecycleFeature.class,
		new Class<?>[]{IRequiredServicesFeature.class}, null);
	
	//-------- methods --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public ComponentLifecycleFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);

	}

	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public IFuture<Void> body()
	{
		IFuture<Void>	ret	= IFuture.DONE;
		
		ConfigurationInfo	ci	= getComponent().getConfiguration()!=null
			? getComponent().getModel().getConfiguration(getComponent().getConfiguration())
			: getComponent().getModel().getConfigurations().length>0 ? getComponent().getModel().getConfigurations()[0] : null;
		
		if(ci!=null)
		{
			UnparsedExpression[]	upes	= ci.getInitialSteps();
			if(upes.length>0)
			{
				Future<Void>	fut	= new Future<Void>();
				ret	= fut;
				List<IComponentStep>	steps	= new ArrayList<IComponentStep>();
				
				for(int i=0; !fut.isDone() && i<upes.length; i++)
				{
					Object	step	= null;
					if(upes[i].getValue()!=null)
					{
						step	= SJavaParser.getParsedValue(upes[i], getComponent().getModel().getAllImports(), getComponent().getFetcher(), getComponent().getClassLoader());
					}
					else
					{
						Class<?> clazz = upes[i].getClazz().getType(getComponent().getClassLoader(), getComponent().getModel().getAllImports());
						try
						{
							step	= clazz.newInstance();
						}
						catch(Exception e)
						{
							fut.setException(e);
						}
					}
					
					if(step instanceof IComponentStep)
					{
						steps.add((IComponentStep)step);
					}
					else if(step!=null)
					{
						fut.setException(new RuntimeException("Unsupported initial component step, class="+upes[i].getClazz()+", value="+upes[i].getValue()));
					}
				}
				
				if(!fut.isDone())
				{
					IResultListener	crl	= new CounterResultListener(steps.size(), new DelegationResultListener<Void>(fut));
					for(IComponentStep step: steps)
					{
						getComponent().getComponentFeature(IExecutionFeature.class).scheduleStep(step)
							.addResultListener(crl);
					}
				}
			}
		}
		
		return ret;
	}

//	/**
//	 *  Called just before the agent is removed from the platform.
//	 *  @return The result of the component.
//	 */
//	public IFuture<Void> shutdown()
//	{
//		return invokeMethod(AgentKilled.class);
//	}
}
