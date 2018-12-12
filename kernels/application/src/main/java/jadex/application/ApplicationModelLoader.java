package jadex.application;

import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.commons.AbstractModelLoader;
import jadex.commons.ICacheableModel;
import jadex.commons.ResourceInfo;
import jadex.kernelbase.CacheableKernelModel;
import jadex.xml.TypeInfo;

/**
 *  Loader for application files.
 */
public class ApplicationModelLoader extends AbstractModelLoader
{
	//-------- constants --------
	
	/** The application file extension. */
	public static final String	FILE_EXTENSION_APPLICATION = ".application.xml";
	
	//-------- attributes --------
	
	/** The xml reader. */
	protected ApplicationXMLReader reader;
	
	//-------- constructors --------
	
	/**
	 *  Create a new BPMN model loader.
	 */
	public  ApplicationModelLoader(Set<TypeInfo>[] mappings)
	{
		super(new String[]{FILE_EXTENSION_APPLICATION});
		this.reader = new ApplicationXMLReader(mappings);
	}

	//-------- methods --------
	
	/**
	 *  Load a BPMN model.
	 *  @param name	The filename or logical name (resolved via imports and extensions).
	 *  @param imports	The imports, if any.
	 */
	public CacheableKernelModel loadApplicationModel(String name, String[] imports, IResourceIdentifier clkey, ClassLoader classloader, Object context) throws Exception
	{
		return (CacheableKernelModel)loadModel(name, FILE_EXTENSION_APPLICATION, imports, clkey, classloader, context);
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