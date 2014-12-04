package jadex.micro.features.impl;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroModel;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.features.IMicroLifecycleFeature;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *  Feature that ensures the agent created(), body() and killed() are called on the pojo. 
 */
public class MicroLifecycleComponentFeature extends	AbstractComponentFeature implements IMicroLifecycleFeature
{
	//-------- constants --------
	
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(IMicroLifecycleFeature.class, MicroLifecycleComponentFeature.class,
		new Class<?>[]{IRequiredServicesFeature.class}, null);
	
	//-------- attributes --------
	
	/** The pojo agent. */
	protected Object pojoagent;
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public MicroLifecycleComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
		
		try
		{
			// Create the pojo agent
			MicroModel model = (MicroModel)getComponent().getModel().getRawModel();
			this.pojoagent = model.getPojoClass().getType(model.getClassloader()).newInstance();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 *  Get the pojoagent.
	 *  @return The pojoagent
	 */
	public Object getPojoAgent()
	{
		return pojoagent;
	}

	/**
	 *  Initialize the feature.
	 *  Empty implementation that can be overridden.
	 */
	public IFuture<Void> init()
	{
		return invokeMethod(getComponent(), AgentCreated.class, null);
	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public IFuture<Void> body()
	{
		return invokeMethod(getComponent(), AgentBody.class, null);
	}

	/**
	 *  Called just before the agent is removed from the platform.
	 *  @return The result of the component.
	 */
	public IFuture<Void> shutdown()
	{
		return invokeMethod(getComponent(), AgentKilled.class, null);
	}
	
	/**
	 *  Add $pojoagent to fetcher.
	 */
	public Object fetchValue(String name)
	{
		if("$pojoagent".equals(name))
		{
			return getPojoAgent();
		}
		else
		{
			return super.fetchValue(name);
		}
	}

	//-------- helper methods --------
	
	/**
	 *  Invoke an agent method by injecting required arguments.
	 */
	public static IFuture<Void> invokeMethod(IInternalAccess component, Class<? extends Annotation> ann, Object[] args)
	{
		MicroModel	model = (MicroModel)component.getModel().getRawModel();
		MethodInfo	mi	= model.getAgentMethod(ann);
		if(mi!=null)
		{
			final Future<Void> ret = new Future<Void>();
			Method	method	= mi.getMethod(component.getClassLoader());
			
			// Try to guess parameters from given args or component internals.
			Object[]	iargs	= new Object[method.getParameterTypes().length];
			for(int i=0; i<method.getParameterTypes().length; i++)
			{
				Class<?>	clazz	= method.getParameterTypes()[i];
				boolean found	= false;
				for(int j=0; !found && args!=null && j<args.length; j++)
				{
					if(args[j]!=null && SReflect.isSupertype(clazz, args[j].getClass()))
					{
						iargs[i]	= args[j];
						found	= true;
					}
				}
				
				// Todo: other injections...				
				if(!found && SReflect.isSupertype(clazz, IInternalAccess.class))
				{
					iargs[i]	= component;
				}
				else if(!found && SReflect.isSupertype(clazz, IExternalAccess.class))
				{
					iargs[i]	= component.getExternalAccess();
				}
			}
			
			try
			{
				Object res = method.invoke(component.getComponentFeature(IMicroLifecycleFeature.class).getPojoAgent(), iargs);
				if(res instanceof IFuture)
				{
					((IFuture<Void>)res).addResultListener(component.getComponentFeature(IExecutionFeature.class)
						.createResultListener(new DelegationResultListener<Void>(ret)));
				}
				else
				{
					ret.setResult(null);
				}
			}
			catch(Exception e)
			{
				e = (Exception)(e instanceof InvocationTargetException && ((InvocationTargetException)e)
					.getTargetException() instanceof Exception? ((InvocationTargetException)e).getTargetException(): e);
				ret.setException(e);
			}
			return ret;
		}
		else
		{
			return IFuture.DONE;
		}
	}
}
