package jadex.bdi.testcases.semiautomatic;

import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.interpreter.OAVAgentModel;
import jadex.bdi.interpreter.OAVBDIModelLoader;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.ThreadPoolFactory;
import jadex.service.BasicServiceContainer;
import jadex.service.IServiceContainer;
import jadex.service.clock.ClockService;
import jadex.service.clock.IClockService;
import jadex.service.clock.SystemClock;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
	//		String	model	= "/jadex/bdi/examples/HelloWorldGoal.agent.xml";
			if(args.length==1)
			{
				model	= args[0];
			}
			else if(args.length>1)
			{
				System.out.println("USAGE: InterpreterTest <model>");
			}
			
			// Load agent model.
			
//			Properties config = new Properties("", "", "");
//			Properties kernelprops = new Properties("", "", "");
//			kernelprops.addProperty(new Property("", "messagetype", "new jadex.adapter.base.fipa.FIPAMessageType()"));
//			kernelprops.addProperty(new Property("standard", "planexecutor", "new jadex.bdi.runtime.JavaStandardPlanExecutor("+ 
//				"jadex.commons.concurrent.ThreadPoolFactory.getThreadPool(\"test\"))"));
//			config.addSubproperties(kernelprops);
			
			Map config = new HashMap();
			config.put("messagetype_fipa", new jadex.adapter.base.fipa.FIPAMessageType());
			config.put("planexecutor_standard", new jadex.bdi.runtime.JavaStandardPlanExecutor(
				jadex.commons.concurrent.ThreadPoolFactory.createThreadPool()));
			
			OAVBDIModelLoader loader = new OAVBDIModelLoader();
			OAVAgentModel loaded = loader.loadAgentModel(model, null);
	
			// Initialize agent interpreter.
			final IClockService clock = new ClockService(new SystemClock("system", 1, ThreadPoolFactory.createThreadPool()));
			clock.start();
			final Executor exe = new Executor(ThreadPoolFactory.createThreadPool());
			final BDIInterpreter[]	interpreters	= new BDIInterpreter[1];
			
			final BasicServiceContainer container = new BasicServiceContainer();
			container.addService(IClockService.class, "clock_service", clock);
			
			final BDIInterpreter interpreter = new BDIInterpreter(new IComponentAdapter()
			{
				public void	wakeup()
				{
					exe.execute();
				}
				
				public void invokeLater(Runnable action)
				{
					// TODO Auto-generated method stub
					throw new UnsupportedOperationException();
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
			}, loaded.getState(), loaded, null, null, config);
			
			exe.setExecutable(new IExecutable()
			{
				public boolean execute()
				{
					// Execute agent.
	//				System.out.println("Executed agent step.");
					return interpreter.executeStep();
				}
			});
			interpreters[0]	= interpreter;
			exe.execute();
			
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
