package jadex.bpmn.examples.helloworld;

import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.ThreadPoolFactory;
import jadex.commons.xml.BeanObjectHandler;
import jadex.commons.xml.Reader;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
			Set ignored = new HashSet();
			ignored.add("xmi");
			ignored.add("iD");
			ignored.add("version");
			Reader reader = new Reader(new BeanObjectHandler(), MBpmnModel.getXMLMapping(), MBpmnModel.getXMLLinkInfos(), ignored);
			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/HelloWorldProcess.bpmn", null);
//			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/test.bpmn", null);
//			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/test2.bpmn", null);
//			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/all_activities.bpmn", null);
//			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/XOR.bpmn", null);

			MBpmnModel model = (MBpmnModel)reader.read(rinfo.getInputStream(), null, null);
			String name = new File(rinfo.getFilename()).getName();
			name = name.substring(0, name.length()-5);
			model.setName(name);
			rinfo.getInputStream().close();
			System.out.println("Loaded model: "+model);
			
			// Create and execute instance.
			Map	handlers	= new HashMap(BpmnInstance.DEFAULT_HANDLERS);
			handlers.put("EventIntermediateTimer", new EventIntermediateTimerActivityHandler());
			final BpmnInstance	instance	= new BpmnInstance(model, handlers);
			
			final IThreadPool	pool	= ThreadPoolFactory.createThreadPool();
			final Executor	exe	= new Executor(pool);
			exe.setExecutable(new IExecutable()
			{	
				public boolean execute()
				{
					if(instance.isReady())
					{						
						System.out.println("Executing step: "+instance);
						instance.executeStep();
					}
					
					if(instance.isFinished())
					{
						System.out.println("Finished: "+instance);
						exe.shutdown(null);
						pool.dispose();
					}
					
					return instance.isReady();
				}
			});
			
			instance.addChangeListener(new IChangeListener()
			{				
				public void changeOccurred(ChangeEvent event)
				{
					exe.execute();
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
