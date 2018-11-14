package jadex.bdiv3x;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.commons.AbstractModelLoader;
import jadex.commons.ICacheableModel;
import jadex.commons.ResourceInfo;
import jadex.kernelbase.CacheableKernelModel;

/**
 *  Loader for application files.
 */
public class BDIXModelLoader extends AbstractModelLoader
{
	//-------- constants --------
	
	/** The agent file extension. */
	public static final String	FILE_EXTENSION_AGENT = ".agent.xml";
	
	/** The agent file extension. */
	public static final String	FILE_EXTENSION_CAPABILITY = ".capability.xml";
	
	//-------- attributes --------
	
	/** The xml reader. */
	protected BDIXMLReader reader;
	
	//-------- constructors --------
	
	/**
	 *  Create a new BPMN model loader.
	 */
	public  BDIXModelLoader()
	{
		super(new String[]{FILE_EXTENSION_AGENT, FILE_EXTENSION_CAPABILITY});
		this.reader = new BDIXMLReader(this);
	}

	//-------- methods --------
	
	/**
	 *  Load an agent model.
	 *  @param name	The filename or logical name (resolved via imports and extensions).
	 *  @param imports	The imports, if any.
	 */
	public CacheableKernelModel loadAgentModel(String name, String[] imports, IResourceIdentifier clkey, ClassLoader classloader, Object context) throws Exception
	{
		return (CacheableKernelModel)loadModel(name, FILE_EXTENSION_AGENT, imports, clkey, classloader, context);
	}
	
	/**
	 *  Load a capability model.
	 *  @param name	The filename or logical name (resolved via imports and extensions).
	 *  @param imports	The imports, if any.
	 */
	public CacheableKernelModel loadCapabilityModel(String name, String[] imports, IResourceIdentifier clkey, ClassLoader classloader, Object context) throws Exception
	{
		return (CacheableKernelModel)loadModel(name, FILE_EXTENSION_CAPABILITY, imports, clkey, classloader, context);
	}
	
	//-------- AbstractModelLoader methods --------
		
	/**
	 *  Load a model.
	 *  @param name	The original name (i.e. not filename).
	 *  @param info	The resource info.
	 */
	protected ICacheableModel doLoadModel(String name, String[] imports, ResourceInfo info, ClassLoader classloader, Object context) throws Exception
	{
		return (ICacheableModel)reader.read(info, classloader, 
			(IResourceIdentifier)((Object[])context)[0], (IComponentIdentifier)((Object[])context)[1]);
	}
}