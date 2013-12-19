package jadex.bdi.testcases.semiautomatic;

import jadex.bdi.OAVBDIModelLoader;
import jadex.bdi.model.OAVAgentModel;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.component.ComponentServiceContainer;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.factory.IComponentAdapterFactory;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.ThreadPool;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

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
			config.put("messagetype_fipa", new jadex.bridge.fipa.FIPAMessageType());
			config.put("planexecutor_standard", new jadex.bdi.runtime.impl.JavaStandardPlanExecutor(
				jadex.commons.concurrent.ThreadPoolFactory.createThreadPool()));
			
			OAVBDIModelLoader loader = new OAVBDIModelLoader(config, null);
			OAVAgentModel loaded = loader.loadAgentModel(model, null, null, null); // todo: rid?
			
			// Initialize agent interpreter.
			Future ret = new Future();
			ret.addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
//					interpreter.getAgentAdapter().wakeup();
				}
				
				public void exceptionOccurred(Exception exception)
				{
				}
			});
			BDIInterpreter interpreter = new BDIInterpreter(null, new ComponentAdapterFactory(), loaded.getState(), loaded, null, null, null, null, config, true, true, null, ret);
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
	
	public void	initialWakeup(IComponentAdapter adapter)
	{
		((ComponentAdapter)adapter).wakeup();
	}

	
	public IComponentAdapter createComponentAdapter(IComponentDescription desc,
		IModelInfo model, IComponentInstance instance, IExternalAccess parent)
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
		container = new ComponentServiceContainer(this, "platform", null, false);
		// Todo: move test to somewhere more useful? (jadex-launch?)
		// Todo: actually test something useful...
//		ThreadPoolService tps = new ThreadPoolService(ThreadPoolFactory.createThreadPool(), container);
//		container.addService(tps, null);
//		ClockService clock = new ClockService(new ClockCreationInfo(IClock.TYPE_SYSTEM, "system"), container, null);
//		container.addService(clock, null);
		
//		exe = new Executor(tps);
		exe	= new Executor(new ThreadPool());
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
	
	public IServiceContainer getServiceContainer()
	{
		return container;
	}

	public IComponentIdentifier getComponentIdentifier()
	{
		return new ComponentIdentifier("nonname@noname");
	}
	
	public boolean isExternalThread()
	{
		return false;
	}
	
	public Logger getLogger()
	{
		return Logger.getAnonymousLogger();
	}

	public Exception getException()
	{
		return null;
	}
	
	public IComponentDescription getDescription()
	{
		return null;
	}
	
	public void block(Object monitor)
	{
	}
	
	public void unblock(Object monitor, boolean kill)
	{
	}
}
