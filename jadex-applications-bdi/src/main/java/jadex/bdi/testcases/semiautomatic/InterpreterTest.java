package jadex.bdi.testcases.semiautomatic;

import jadex.bdi.OAVBDIModelLoader;
import jadex.bdi.model.OAVAgentModel;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.ThreadPoolFactory;
import jadex.service.BasicServiceContainer;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.service.clock.ClockCreationInfo;
import jadex.service.clock.ClockService;
import jadex.service.clock.IClock;
import jadex.service.clock.IClockService;
import jadex.service.threadpool.IThreadPoolService;
import jadex.service.threadpool.ThreadPoolService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  Test execution of an agent.
 */
public class InterpreterTest
{
	/**
	 *  Main method for testing.
	 * @throws IOException 
	 */
	public static void	main(String[] args) throws IOException
	{
		try
		{
			String	model	= "/jadex/bdi/examples/helloworld/HelloWorld.agent.xml";
			if(args.length==1)
			{
				model	= args[0];
			}
			else if(args.length>1)
			{
				System.out.println("USAGE: InterpreterTest <model>");
			}
			
			// Load agent model.
			Map config = new HashMap();
			config.put("messagetype_fipa", new jadex.base.fipa.FIPAMessageType());
			config.put("planexecutor_standard", new jadex.bdi.runtime.impl.JavaStandardPlanExecutor(
				jadex.commons.concurrent.ThreadPoolFactory.createThreadPool()));
			
			OAVBDIModelLoader loader = new OAVBDIModelLoader();
			OAVAgentModel loaded = loader.loadAgentModel(model, null, null);
	
			
			
			// Initialize agent interpreter.
			Future ret = new Future();
			ret.addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
//					interpreter.getAgentAdapter().wakeup();
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
				}
			});
			BDIInterpreter interpreter = new BDIInterpreter(null, new ComponentAdapterFactory(), loaded.getState(), loaded, null, null, null, config, ret);
			interpreter.getAgentAdapter().wakeup();
			
	//		System.out.println("Agent execution finished.");
			
	//		OAVTreeModel.createOAVFrame("Agent State", state, interpreter.getAgentInstanceReference()).setVisible(true);
	//		
	//		RetePanel.createReteFrame("Agent Rules", ((RetePatternMatcherFunctionality)interpreter.getRuleSystem().getMatcherFunctionality()).getReteNode(), 
	//			((RetePatternMatcherState)interpreter.getRuleSystem().getMatcherState()).getReteMemory(), new Object());
	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

class ComponentAdapterFactory implements IComponentAdapterFactory
{
	public boolean executeStep(IComponentAdapter adapter)
	{
		((ComponentAdapter)adapter).wakeup();
		return false;
	}
	
	public IComponentAdapter createComponentAdapter(IComponentDescription desc,
		ILoadableComponentModel model, IComponentInstance instance, IExternalAccess parent)
	{
		return new ComponentAdapter(instance);
	}
}

class ComponentAdapter implements IComponentAdapter
{
	final Executor exe;
	final IServiceContainer container;
	
	public ComponentAdapter(final IComponentInstance interpreter)
	{
		container = new BasicServiceContainer("platform");
		ThreadPoolService tps = new ThreadPoolService(ThreadPoolFactory.createThreadPool(), container);
		container.addService(IThreadPoolService.class, tps);
		final IClockService clock = new ClockService(new ClockCreationInfo(IClock.TYPE_SYSTEM, "system"), container);
		container.addService(IClockService.class, (IService)clock);
		
		exe = new Executor(tps);
		exe.setExecutable(new IExecutable()
		{
			public boolean execute()
			{
				// Execute agent.
//				System.out.println("Executed agent step.");
				return ((BDIInterpreter)interpreter).executeStep();
			}
		});
	}
	
	public void	wakeup()
	{
		exe.execute();
	}
	
	public void invokeLater(Runnable action)
	{
		action.run();
	}
	
	public IExternalAccess getParent()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public IFuture getChildren()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public IServiceContainer getServiceContainer()
	{
		return container;
	}

	public IComponentIdentifier getComponentIdentifier()
	{
		return new IComponentIdentifier()
		{
			public String getName()
			{
				return "noname";
			}
			
			public String getLocalName()
			{
				return "noname";
			}

			public String getPlatformName()
			{
				return "noplatform";
			}
			
			public String[] getAddresses()
			{
				return new String[0];
			}
			
		};
	}
	
	public boolean isExternalThread()
	{
		return false;
	}
	
	public Logger getLogger()
	{
		return Logger.getAnonymousLogger();
	}
}
