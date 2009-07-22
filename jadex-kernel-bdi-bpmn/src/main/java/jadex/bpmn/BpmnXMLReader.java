package jadex.bpmn;

import jadex.bpmn.model.MBpmnModel;
import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.xml.BeanObjectHandler;
import jadex.commons.xml.Reader;

import java.io.File;
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
	 * /
	public static Reader	getReader()
	{
		return reader;
	}*/
	
	/**
	 *  Read properties from xml.
	 *  @param input The input stream.
	 *  @param classloader The classloader.
	 * 	@param context The context.
 	 */
	public static MBpmnModel read(String filename, final ClassLoader classloader, final Object context) throws Exception
	{
		ResourceInfo rinfo = SUtil.getResourceInfo0(filename, classloader);
		if(rinfo==null)
			throw new RuntimeException("Could not find resource: "+filename);
		MBpmnModel ret = (MBpmnModel)reader.read(rinfo.getInputStream(), classloader, context);
		String name = new File(rinfo.getFilename()).getName();
		name = name.substring(0, name.length()-5);
		ret.setName(name);
		rinfo.getInputStream().close();
		System.out.println("Loaded model: "+ret);
		return ret;
	}
	

}
