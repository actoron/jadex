package jadex.bpmn;

import jadex.bpmn.model.MBpmnModel;
import jadex.commons.xml.BeanObjectHandler;
import jadex.commons.xml.Reader;

import java.util.HashSet;
import java.util.Set;

/**
 *  Reader for loading Bpmn XML models into a Java representation states.
 */
public class BpmnXMLReader
{
	//-------- attributes --------
	
	/** The singleton reader instance. */
	protected static Reader	reader;
	
	//-------- methods --------
	
	// Initialize reader instance.
	static
	{
		Set ignored = new HashSet();
		ignored.add("xmi");
		ignored.add("iD");
		ignored.add("version");
		reader = new Reader(new BeanObjectHandler(), MBpmnModel.getXMLMapping(), MBpmnModel.getXMLLinkInfos(), ignored);
	}
	
	/**
	 *  Get the reader instance.
	 */
	public static Reader	getReader()
	{
		return reader;
	}
}
