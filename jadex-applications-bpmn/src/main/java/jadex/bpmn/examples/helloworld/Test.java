package jadex.bpmn.examples.helloworld;

import jadex.bpmn.model.MBpmnDiagram;
import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.xml.BeanObjectHandler;
import jadex.commons.xml.Reader;

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
			Set ignored = new HashSet();
			ignored.add("xmi");
			ignored.add("iD");
			ignored.add("version");
			Reader reader = new Reader(new BeanObjectHandler(), MBpmnDiagram.getXMLMapping(), MBpmnDiagram.getXMLLinkInfos(), ignored);
			ResourceInfo rinfo = SUtil.getResourceInfo0("jadex/bpmn/examples/helloworld/HelloWorldProcess.bpmn", null);
			Object ret = reader.read(rinfo.getInputStream(), null, null);
			rinfo.getInputStream().close();
			System.out.println("Loaded model: "+ret);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
