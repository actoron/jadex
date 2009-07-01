package jadex.bpmn.examples.helloworld;

import jadex.bpmn.model.MBpmnDiagram;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.xml.BeanObjectHandler;
import jadex.commons.xml.Reader;

import java.io.File;
import java.util.HashSet;
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
			Reader reader = new Reader(new BeanObjectHandler(), MBpmnDiagram.getXMLMapping(), MBpmnDiagram.getXMLLinkInfos(), ignored);
//			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/HelloWorldProcess.bpmn", null);
			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/test.bpmn", null);
			MBpmnDiagram	model	= (MBpmnDiagram) reader.read(rinfo.getInputStream(), null, null);
			String	name	= new File(rinfo.getFilename()).getName();
			name	= name.substring(0, name.length()-5);
			model.setName(name);
			rinfo.getInputStream().close();
			System.out.println("Loaded model: "+model);
			
			// Create and execute instance.
			BpmnInstance	instance	= new BpmnInstance(model);
			while(!instance.isFinished())
			{
				System.out.println("Executing step: "+instance);
				instance.executeStep();
			}
			System.out.println("Finished: "+instance);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
