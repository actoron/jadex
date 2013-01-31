package jadex.kernelbase;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.bridge.modelinfo.IExtensionInstance;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;

import java.lang.reflect.Method;
import java.util.Map;

/**
 *  External access for applications.
 */
public class ExternalAccess implements IExternalAccess
{
	//-------- attributes --------

	/** The component. */
	protected StatelessAbstractInterpreter interpreter;

	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	/** The toString value. */
	protected String tostring;
	
	/** The provider. */
	protected IServiceProvider provider;
	
	// -------- constructors --------

	/**
	 *	Create an external access.
	 */
	public ExternalAccess(StatelessAbstractInterpreter interpreter)
	{
		this.interpreter = interpreter;
		this.adapter = interpreter.getComponentAdapter();
		this.tostring = interpreter.getComponentIdentifier().getLocalName();
		this.provider = interpreter.getServiceContainer();
	}

	//-------- methods --------
	
	/**
	 *  Get the model.
	 *  @return The model.
	 */
	public IModelInfo getModel()
	{
		return interpreter.getModel();
	}
	
	/**
	 *  Get the component identifier.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return adapter.getComponentIdentifier();
	}
	
//	/**
//	 *  Get a space of the application.
//	 *  @param name	The name of the space.
//	 *  @return	The space.
//	 */
//	public ISpace getSpace(final String name)
//	{
//		// Application getSpace() is synchronized
//		return application.getSpace(name);
//		
////		final Future ret = new Future();
////		
////		if(adapter.isExternalThread())
////		{
////			adapter.invokeLater(new Runnable() 
////			{
////				public void run() 
////				{
////					ret.setResult(application.getSpace(name));
////				}
////			});
////		}
////		else
////		{
////			ret.setResult(application.getSpace(name));
////		}
////		
////		return ret;
//	}
	
	/**
	 *  Get the application component.
	 */
	public IServiceProvider getServiceProvider()
	{
		return provider;
	}

	/**
	 *  Kill the component.
	 */
	public IFuture<Map<String, Object>> killComponent()
	{
		final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
		
		if(adapter.isExternalThread())
		{
			try
			{
//				if(adapter.getComponentIdentifier().getParent()==null)
//				{
//					System.out.println("platform e: "+adapter.getComponentIdentifier().getName());
//					Thread.dumpStack();
//				}
				adapter.invokeLater(new Runnable() 
				{
					public void run() 
					{
						interpreter.killComponent().addResultListener(new DelegationResultListener<Map<String, Object>>(ret));
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(adapter.getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						ret.setException(e);
					}
				});
			}
		}
		else
		{
//			if(adapter.getComponentIdentifier().getParent()==null)
//			{
//				System.err.println("platform i: "+adapter.getComponentIdentifier().getName());
//				Thread.dumpStack();
//			}
			interpreter.killComponent().addResultListener(new DelegationResultListener<Map<String, Object>>(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Create a result listener that will be 
	 *  executed on the component thread.
	 *  @param listener The result listener.
	 *  @return A result listener that is called on component thread.
	 * /
	public IResultListener createResultListener(IResultListener listener)
	{
		return application.createResultListener(listener);
	}*/
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture<IComponentIdentifier[]> getChildren(final String type)
	{
		final Future<IComponentIdentifier[]> ret = new Future<IComponentIdentifier[]>();
		
		if(adapter.isExternalThread())
		{
			try
			{
				adapter.invokeLater(new Runnable() 
				{
					public void run() 
					{
						interpreter.getChildren(type).addResultListener(new DelegationResultListener<IComponentIdentifier[]>(ret));
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(adapter.getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						ret.setException(e);
					}
				});
			}
		}
		else
		{
			interpreter.getChildren(type).addResultListener(new DelegationResultListener<IComponentIdentifier[]>(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Create a subcomponent.
	 *  @param component The instance info.
	 */
	public IFuture<IComponentIdentifier> createChild(final ComponentInstanceInfo component)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		if(adapter.isExternalThread())
		{
			try
			{
				adapter.invokeLater(new Runnable() 
				{
					public void run() 
					{
						interpreter.createChild(component).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(adapter.getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						ret.setException(e);
					}
				});
			}
		}
		else
		{
			interpreter.createChild(component).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
		}
		
		return ret;
	}

	
	/**
	 *  Get the file name of a component type.
	 *  @param ctype The component type.
	 *  @return The file name of this component type.
	 */
	public IFuture<String> getFileName(final String ctype)
	{
		final Future<String> ret = new Future<String>();
		
		if(adapter.isExternalThread())
		{
			try
			{
				adapter.invokeLater(new Runnable() 
				{
					public void run() 
					{
						String fn = interpreter.getComponentFilename(ctype);
						if(fn!=null)
						{
							ret.setResult(fn);
						}
						else
						{
							ret.setException(new RuntimeException("Unknown component type: "+ctype));
						}
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(adapter.getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						ret.setException(e);
					}
				});
			}
		}
		else
		{
			String fn = interpreter.getComponentFilename(ctype);
			if(fn!=null)
			{
				ret.setResult(fn);
			}
			else
			{
				ret.setException(new RuntimeException("Unknown component type: "+ctype));
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the local type name of this component as defined in the parent.
	 *  @return The type of this component type.
	 */
	public String getLocalType()
	{
		return interpreter.getLocalType();
	}
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 *  @return The result of the step.
	 */
	public <T> IFuture<T> scheduleStep(final IComponentStep<T> step)
	{
		boolean im = false;
		try
		{
			Method m = step.getClass().getMethod("execute", new Class[]{IInternalAccess.class});
			im = IIntermediateFuture.class.isAssignableFrom(m.getReturnType());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(im)
		{
			final IntermediateFuture ret = new IntermediateFuture();
			if(adapter.isExternalThread())
			{
				try
				{
					adapter.invokeLater(new Runnable() 
					{
						public void run() 
						{
							interpreter.scheduleStep(step).addResultListener(new IntermediateDelegationResultListener(ret));
						}
					});
				}
				catch(final Exception e)
				{
					Starter.scheduleRescueStep(adapter.getComponentIdentifier(), new Runnable()
					{
						public void run()
						{
							ret.setException(e);
						}
					});
				}
			}
			else
			{
				interpreter.scheduleStep(step).addResultListener(new IntermediateDelegationResultListener(ret));
			}
			return ret;
		}
		else
		{
			final Future<T> ret = new Future<T>();
			if(adapter.isExternalThread())
			{
				try
				{
					adapter.invokeLater(new Runnable() 
					{
						public void run() 
						{
							interpreter.scheduleStep(step).addResultListener(new DelegationResultListener<T>(ret));
						}
					});
				}
				catch(final Exception e)
				{
					Starter.scheduleRescueStep(adapter.getComponentIdentifier(), new Runnable()
					{
						public void run()
						{
							ret.setException(e);
						}
					});
				}
			}
			else
			{
				interpreter.scheduleStep(step).addResultListener(new DelegationResultListener<T>(ret));
			}
			return ret;
		}
	}
	
	/**
	 *  Execute some code on the component's thread.
	 *  Unlike scheduleStep(), the action will also be executed
	 *  while the component is suspended.
	 *  @param action	Code to be executed on the component's thread.
	 *  @return The result of the step.
	 */
	public <T>	IFuture<T> scheduleImmediate(final IComponentStep<T> step)
	{
		final Future<T> ret = new Future<T>();
		
		try
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					try
					{
						IFuture<T>	result	= step.execute(interpreter.getInternalAccess());
						result.addResultListener(new DelegationResultListener<T>(ret));
					}
					catch(Exception e)
					{
						if(!ret.isDone())
						{
							ret.setException(e);
						}
						else
						{
							adapter.getLogger().warning("Exception occurred: "+e);
							e.printStackTrace();
						}
					}
				}
			});
		}
		catch(final Exception e)
		{
			Starter.scheduleRescueStep(adapter.getComponentIdentifier(), new Runnable()
			{
				public void run()
				{
					ret.setException(e);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Schedule a step of the component.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the component.
	 *  @param delay The delay to wait before step should be done.
	 *  @return The result of the step.
	 */
	public <T>	IFuture<T> scheduleStep(final IComponentStep<T> step, final long delay)
	{
		final Future<T> ret = new Future<T>();
		
		SServiceProvider.getService(interpreter.getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(interpreter.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IClockService cs = (IClockService)result;
				cs.createTimer(delay, new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						scheduleStep(step).addResultListener(new DelegationResultListener(ret));
					}
				});
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Execute some code on the component's thread.
	 *  Unlike scheduleStep(), the action will also be executed
	 *  while the component is suspended.
	 *  @param action	Code to be executed on the component's thread.
	 *  @param delay The delay to wait before step should be done.
	 *  @return The result of the step.
	 */
	public <T> IFuture<T> scheduleImmediate(final IComponentStep<T> step, final long delay)
	{
		final Future<T> ret = new Future<T>();
		
		SServiceProvider.getService(interpreter.getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(interpreter.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IClockService cs = (IClockService)result;
				cs.createTimer(delay, new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						scheduleImmediate(step).addResultListener(new DelegationResultListener(ret));
					}
				});
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Get a space of the application.
	 *  @param name	The name of the space.
	 *  @return	The space.
	 */
	public IFuture<IExtensionInstance> getExtension(final String name)
	{
		final Future<IExtensionInstance> ret = new Future<IExtensionInstance>();
		
		if(adapter.isExternalThread())
		{
			try
			{
				adapter.invokeLater(new Runnable() 
				{
					public void run() 
					{
						ret.setResult(interpreter.getExtension(name));
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(adapter.getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						ret.setException(e);
					}
				});
			}
		}
		else
		{
			ret.setResult(interpreter.getExtension(name));
		}
		
		return ret;
	}

	/**
	 *  Add an component listener.
	 *  @param listener The listener.
	 */
	public IFuture<Void> addComponentListener(final IComponentListener listener)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(adapter.isExternalThread())
		{
			try
			{
				adapter.invokeLater(new Runnable() 
				{
					public void run() 
					{
						interpreter.addComponentListener(listener)
							.addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(adapter.getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						ret.setException(e);
					}
				});
			}
		}
		else
		{
			interpreter.addComponentListener(listener)
				.addResultListener(new DelegationResultListener<Void>(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Remove a component listener.
	 *  @param listener The listener.
	 */
	public IFuture<Void> removeComponentListener(final IComponentListener listener)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(adapter.isExternalThread())
		{
			try
			{
				adapter.invokeLater(new Runnable() 
				{
					public void run() 
					{
						interpreter.removeComponentListener(listener)
							.addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(adapter.getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						ret.setException(e);
					}
				});
			}
		}
		else
		{
			interpreter.removeComponentListener(listener)
				.addResultListener(new DelegationResultListener<Void>(ret));
		}
		
		return ret;
	}

	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IFuture<Map<String, Object>> getArguments()
	{
		final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
		
		if(adapter.isExternalThread())
		{
			try
			{
				adapter.invokeLater(new Runnable() 
				{
					public void run() 
					{
						ret.setResult(interpreter.getArguments());
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(adapter.getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						ret.setException(e);
					}
				});
			}
		}
		else
		{
			ret.setResult(interpreter.getArguments());
		}
		
		return ret;
	}
	
	/**
	 *  Get the component results.
	 *  @return The results.
	 */
	public IFuture<Map<String, Object>> getResults()
	{
		final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
		
		if(adapter.isExternalThread())
		{
			try
			{
				adapter.invokeLater(new Runnable() 
				{
					public void run() 
					{
						ret.setResult(interpreter.getResults());
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(adapter.getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						ret.setException(e);
					}
				});
			}
		}
		else
		{
			ret.setResult(interpreter.getResults());
		}
		
		return ret;
	}
	
	/**
	 *  Get the interpreter.
	 *  @return the interpreter.
	 */
	public StatelessAbstractInterpreter getInterpreter()
	{
		return interpreter;
	}
	
	/**
	 *  Test if current thread is external thread.
	 *  @return True if the current thread is not the component thread.
	 */
	public boolean isExternalThread()
	{
		return adapter.isExternalThread();
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ExternalAccess(comp=" + tostring + ")";
	}
}
