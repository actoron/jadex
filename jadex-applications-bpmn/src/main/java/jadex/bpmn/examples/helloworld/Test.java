package jadex.bpmn.examples.helloworld;

import jadex.bpmn.BpmnXMLReader;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.IBpmnExecutor;
import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.ThreadPoolFactory;
import jadex.commons.xml.Reader;

import java.io.File;

public class Test
{
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		try
		{
			// Load model.
			Reader reader = BpmnXMLReader.getReader();
			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/HelloWorldProcess.bpmn", null);
//			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/test_parallel.bpmn", null);
//			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/test2.bpmn", null);
//			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/all_activities.bpmn", null);
//			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/XOR.bpmn", null);
//			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/Exception.bpmn", null);
//			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/test_rule.bpmn", null);
//			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/SubProcess.bpmn", null);
//			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/UserInteraction.bpmn", null);

			MBpmnModel model = (MBpmnModel)reader.read(rinfo.getInputStream(), null, null);
			String name = new File(rinfo.getFilename()).getName();
			name = name.substring(0, name.length()-5);
			model.setName(name);
			rinfo.getInputStream().close();
			System.out.println("Loaded model: "+model);
			
			// Create and execute instance.
			final IThreadPool	pool	= ThreadPoolFactory.createThreadPool();
			final Executor	exe	= new Executor(pool);
			final BpmnInstance	instance	= new BpmnInstance(model);
			instance.setExecutor(new IBpmnExecutor()
			{
				public void wakeUp()
				{
					exe.execute();
				}
			});
			
			exe.setExecutable(new IExecutable()
			{	
				public boolean execute()
				{
					if(instance.isReady(null, null))
					{						
						System.out.println("Executing step: "+instance);
						instance.executeStep(null, null);
					}
					
					if(instance.isFinished(null, null))
					{
						System.out.println("Finished: "+instance);
						exe.shutdown(null);
						pool.dispose();
					}
					
					return instance.isReady(null, null);
				}
			});
			
			exe.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
