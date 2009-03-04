package jadex.bdi.examples;

import jadex.adapter.base.clock.ClockService;
import jadex.adapter.base.clock.SystemClock;
import jadex.adapter.base.fipa.FIPAMessageType;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.interpreter.OAVAgentModel;
import jadex.bdi.interpreter.OAVBDIModelLoader;
import jadex.bridge.IAgentAdapter;
import jadex.bridge.IAgentFactory;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IApplicationFactory;
import jadex.bridge.IClockService;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.IPlatform;
import jadex.bridge.MessageType;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.ThreadPoolFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
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
			
			OAVBDIModelLoader loader = new OAVBDIModelLoader(config);
			OAVAgentModel loaded = loader.loadAgentModel(model, null);
	
			// Initialize agent interpreter.
			final IClockService clock = new ClockService(new SystemClock("system", 1, ThreadPoolFactory.createThreadPool()), null);
			clock.start();
			final Executor exe = new Executor(ThreadPoolFactory.createThreadPool());
			final BDIInterpreter[]	interpreters	= new BDIInterpreter[1];
			final BDIInterpreter interpreter = new BDIInterpreter(new IAgentAdapter()
			{
				public void	wakeup()
				{
					exe.execute();
				}
	
				public void killAgent()
				{
					System.out.println("clean");
					interpreters[0].killAgent(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							clock.shutdown(null);
							exe.shutdown(null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							exception.printStackTrace();
						}
					});
				}
	
				public IPlatform getPlatform()
				{
					return new IPlatform()
					{
						public IApplicationFactory getApplicationFactory()
						{
							// TODO Auto-generated method stub
							return null;
						}
						
						public IAgentFactory getAgentFactory()
						{
							// TODO Auto-generated method stub
							return null;
						}
						
						public String getName()
						{
							return "test";
						}
						
						public Collection getServices(Class type)
						{
							return type==IClockService.class ? Collections.singleton(clock) : null;
						}
						
						public Object getService(Class type, String name)
						{
							return type==IClockService.class ? clock : null;
						}
						
						public Object getService(Class type)
						{
							return type==IClockService.class ? clock : null;
						}
						
						public MessageType getMessageType(String type)
						{
							return SFipa.FIPA_MESSAGE_TYPE.getName().equals(type)? SFipa.FIPA_MESSAGE_TYPE: null;
						}
					};
				}
	
				public IAgentIdentifier getAgentIdentifier()
				{
					return new IAgentIdentifier()
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
				
				public void sendMessage(IMessageAdapter msg)
				{
				}
				
//				public IClock getClock()
//				{
//					return clock;
//				}
			}, loaded.getState(), loaded, null, null, config);
			
			exe.setExecutable(new IExecutable()
			{
				public boolean execute()
				{
					// Execute agent.
	//				System.out.println("Executed agent step.");
					return interpreter.executeAction();
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
