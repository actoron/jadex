package jadex.bpmn.examples.dipp;

import jadex.gpmn.GpmnXMLReader;

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
			
			Object model = GpmnXMLReader.read(filename, null, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
