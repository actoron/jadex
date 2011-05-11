package jadex.micro;

import jadex.commons.AbstractModelLoader;
import jadex.commons.ICacheableModel;
import jadex.commons.ResourceInfo;

import java.io.IOException;

/**
 * 
 */
public class MicroModelLoader extends AbstractModelLoader
{
	//-------- constants --------
	
	/** The component file extension. */
	public static final String	FILE_EXTENSION_MICRO = "Agent.class";
	
	//-------- attributes --------
	
	/** The xml reader. */
	protected MicroClassReader reader;
	
	//-------- constructors --------
	
	/**
	 *  Create a new BPMN model loader.
	 */
	public MicroModelLoader()
	{
		super(new String[]{FILE_EXTENSION_MICRO});
		this.reader = new MicroClassReader();
	}

	//-------- methods --------
	
	/**
	 *  Load a component model.
	 *  @param name	The filename or logical name (resolved via imports and extensions).
	 *  @param imports	The imports, if any.
	 */
	public MicroModel loadComponentModel(String name, String[] imports, ClassLoader classloader) throws Exception
	{
		return (MicroModel)loadModel(name, FILE_EXTENSION_MICRO, imports, classloader);
	}
	
	//-------- AbstractModelLoader methods --------
		
	/**
	 *  Load a model.
	 *  @param name	The original name (i.e. not filename).
	 *  @param info	The resource info.
	 */
	protected ICacheableModel doLoadModel(String name, String[] imports, ResourceInfo info, ClassLoader classloader) throws Exception
	{
//		System.out.println("cache miss: "+name);
		return (ICacheableModel)reader.read(name, imports, classloader);
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
			// Try to find directly as absolute path.
//			Class clazz = SReflect.findClass0(name, imports, classloader);
//			if(clazz!=null)
				ret = new ResourceInfo(name, null, 0L);
	
			if(ret==null)
				throw new IOException("File "+name+" not found in imports");//: "+SUtil.arrayToString(imports));
		}
		return ret;
	}
	
}
