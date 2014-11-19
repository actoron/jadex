package jadex.micro.features.impl;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroModel;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.features.IMicroLifecycleFeature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *  Feature that ensures the agent created(), body() and killed() are called on the pojo. 
 */
public class MicroLifecycleFeature extends	AbstractComponentFeature implements IMicroLifecycleFeature
{
	//-------- constants --------
	
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(IMicroLifecycleFeature.class, MicroLifecycleFeature.class);
	
	//-------- attributes --------
	
	/** The pojo agent. */
	protected Object pojoagent;
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public MicroLifecycleFeature(IInternalAccess component, ComponentCreationInfo cinfo)
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
	 *  The pojoagent to set.
	 *  @param pojoagent The pojoagent to set
	 */
	public void setPojoAgent(Object pojoagent)
	{
		this.pojoagent = pojoagent;
	}
	
	/**
	 *  Initialize the feature.
	 *  Empty implementation that can be overridden.
	 */
	public IFuture<Void> init()
	{
		MicroModel	model = (MicroModel)component.getModel().getRawModel();
		MethodInfo	mi	= model.getAgentMethod(AgentCreated.class);
		if(mi!=null)
		{
			final Future<Void> ret = new Future<Void>();
			Method	m	= mi.getMethod(component.getClassLoader());
			invokeMethod(m)
				.addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
			{
				public void customResultAvailable(Object result)
				{
					ret.setResult(null);
				}
			});
			return ret;
		}
		else
		{
			return IFuture.DONE;
		}
	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public IFuture<Void> body()
	{
		MicroModel	model = (MicroModel)component.getModel().getRawModel();
		MethodInfo	mi	= model.getAgentMethod(AgentBody.class);
		if(mi!=null)
		{
			final Future<Void> ret = new Future<Void>();
			final Method	m	= mi.getMethod(component.getClassLoader());
			invokeMethod(m)
				.addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
			{
				public void customResultAvailable(Object result)
				{
					// Only end body if future or void and kill is true 
					boolean kill = false;
					if(SReflect.isSupertype(IFuture.class, m.getReturnType()))
					{
						kill = true;
					}
					else if(void.class.equals(m.getReturnType()))
					{
						AgentBody ab = m.getAnnotation(AgentBody.class);
						kill = !ab.keepalive();
					}
					
					if(kill)
					{
						ret.setResult(null);
					}
				}
			});
			return ret;
		}
		else
		{
			Agent ag = getPojoAgent().getClass().getAnnotation(Agent.class);
			if(!ag.keepalive())
			{
				return IFuture.DONE;
			}
			else
			{
				return new Future<Void>();
			}
		}
	}

	/**
	 *  Called just before the agent is removed from the platform.
	 *  @return The result of the component.
	 */
	public IFuture<Void> shutdown()
	{
		MicroModel	model = (MicroModel)component.getModel().getRawModel();
		MethodInfo	mi	= model.getAgentMethod(AgentKilled.class);
		if(mi!=null)
		{
			final Future<Void> ret = new Future<Void>();
			Method	m	= mi.getMethod(component.getClassLoader());
			invokeMethod(m)
				.addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
			{
				public void customResultAvailable(Object result)
				{
					ret.setResult(null);
				}
			});
			return ret;
		}
		else
		{
			return IFuture.DONE;
		}
	}
	
	/**
	 *  Invoke an agent method by injecting required arguments.
	 */
	protected IFuture<Object> invokeMethod(Method method)
	{
		final Future<Object> ret = new Future<Object>();
		
		// Try to guess parameters as internal or external access.
		// Todo: other injections...
		Object[]	args	= new Object[method.getParameterTypes().length];
		for(int i=0; i<method.getParameterTypes().length; i++)
		{
			Class<?>	clazz	= method.getParameterTypes()[i];
			if(SReflect.isSupertype(clazz, IInternalAccess.class))
			{
				args[i]	= getComponent();
			}
			else if(SReflect.isSupertype(clazz, IExternalAccess.class))
			{
				args[i]	= getComponent().getExternalAccess();
			}
		}
		
		try
		{
			Object res = method.invoke(getPojoAgent(), args);
			if(res instanceof IFuture)
			{
				((IFuture<Object>)res).addResultListener(createResultListener(
					new DelegationResultListener<Object>(ret)));
			}
			else
			{
				ret.setResult(res);
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
	
	/**
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public <T> IResultListener<T> createResultListener(IResultListener<T> listener)
	{
		return getComponent().getComponentFeature(IExecutionFeature.class).createResultListener(listener);
	}
	
}
