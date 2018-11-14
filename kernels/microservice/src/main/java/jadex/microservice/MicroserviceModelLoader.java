package jadex.microservice;

import java.util.List;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.commons.AbstractModelLoader;
import jadex.commons.ICacheableModel;
import jadex.commons.ResourceInfo;
import jadex.micro.MicroModel;

/**
 *  Loader for micro models.
 */
public class MicroserviceModelLoader extends AbstractModelLoader
{
	//-------- constants --------
	
	/** The component file extension. */
	public static final String	FILE_EXTENSION_MICROSERVICE = ".class";
	
	//-------- attributes --------
	
	/** The xml reader. */
	protected MicroserviceClassReader reader;
	
	//-------- constructors --------
	
	/**
	 *  Create a new BPMN model loader.
	 */
	public MicroserviceModelLoader()
	{
		super(new String[]{FILE_EXTENSION_MICROSERVICE});
		this.reader = new MicroserviceClassReader();
	}

	//-------- methods --------
	
	/**
	 *  Load a component model.
	 *  @param name	The filename or logical name (resolved via imports and extensions).
	 *  @param imports	The imports, if any.
	 */
	public MicroModel loadComponentModel(String name, String[] imports, IResourceIdentifier clkey, ClassLoader classloader, Object context) throws Exception
	{
		return (MicroModel)loadModel(name, FILE_EXTENSION_MICROSERVICE, imports, clkey, classloader, context);
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
//		if(name.indexOf("HelloWorldAgent")!=-1)
//			System.out.println("cache miss: "+name);
		
		return (ICacheableModel)reader.read(name, imports, classloader, 
			(IResourceIdentifier)((Object[])context)[0], (IComponentIdentifier)((Object[])context)[1], (List<IComponentFeatureFactory>)((Object[])context)[2]);
	}
	
	//-------- constructors --------
	
	/**
	 *  Find the file for a given name.
	 *  @param name	The filename or logical name (resolved via imports and extension).
	 *  @param extension	The required extension.
	 *  @param imports	The imports, if any.
	 *  @return The resource info identifying the file.
	 */
	protected ResourceInfo	getResourceInfo(String name, String extension, String[] imports, ClassLoader classloader) throws Exception
	{
		ResourceInfo ret = null;
		if(registered.containsKey(name))
		{
			// Hack!!! ignore file handling for registered models.
			ICacheableModel	model	= (ICacheableModel)registered.get(name);
			ret	= new ResourceInfo(name, null, model.getLastModified());
		}
		else
		{
			ret = new ResourceInfo(name, null, 0L);
		}
		return ret;
	}
	
}
