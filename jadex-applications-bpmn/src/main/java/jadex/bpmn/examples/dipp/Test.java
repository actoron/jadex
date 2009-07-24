package jadex.bpmn.examples.dipp;

import java.util.Collections;

import jadex.bdi.interpreter.OAVBDIModelLoader;
import jadex.commons.SUtil;
import jadex.gpmn.GpmnBDIConverter;
import jadex.gpmn.GpmnXMLReader;
import jadex.gpmn.model.MGpmnModel;

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
			
			String filename = "jadex/bpmn/examples/dipp/dipp.gpmn";
			
			MGpmnModel model = GpmnXMLReader.read(filename, null, null);
			GpmnBDIConverter conv = new GpmnBDIConverter(new OAVBDIModelLoader(Collections.EMPTY_MAP));
			Object[] ret = conv.convertGpmnModelToBDIAgents(model, null);
			System.out.println("Converted: "+SUtil.arrayToString(ret));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
