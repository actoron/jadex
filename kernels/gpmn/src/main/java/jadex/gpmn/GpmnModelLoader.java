package jadex.gpmn;

import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.AbstractModelLoader;
import jadex.commons.ICacheableModel;
import jadex.commons.ResourceInfo;
import jadex.gpmn.model.MGpmnModel;

/**
 *  Loader for eclipse STP GPMN models (.gpmn files).
 */
public class GpmnModelLoader extends AbstractModelLoader
{
	//-------- constants --------
	
	/** The GPMN file extension. */
	public static final String	FILE_EXTENSION_GPMN	= ".gpmn";
	
	//-------- constructors --------
	
	/**
	 *  Create a new GPMN model loader.
	 */
	public GpmnModelLoader()
	{
		super(new String[]{FILE_EXTENSION_GPMN});
	}

	//-------- methods --------
	
	/**
	 *  Load a GPMN model.
	 *  @param name	The filename or logical name (resolved via imports and extensions).
	 *  @param imports	The imports, if any.
	 */
	public IModelInfo	loadGpmnModel(String name, String[] imports, ClassLoader classloader, Object context) throws Exception
	{
		return ((MGpmnModel)loadModel(name, FILE_EXTENSION_GPMN, imports, classloader, classloader, context)).getModelInfo(); // Todo: RID as clkey.
	}
	
	//-------- AbstractModelLoader methods --------
		
	/**
	 *  Load a model.
	 *  @param name	The original name (i.e. not filename).
	 *  @param info	The resource info.
	 */
	protected ICacheableModel doLoadModel(String name, String[] imports, ResourceInfo info, 
		ClassLoader classloader, Object context) throws Exception
	{
		return (ICacheableModel)GpmnXMLReader.read(info, classloader, (IResourceIdentifier)context);
	}
}
