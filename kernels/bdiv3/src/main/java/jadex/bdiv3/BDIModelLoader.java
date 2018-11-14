package jadex.bdiv3;

import java.util.List;

import jadex.bdiv3.model.BDIModel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.commons.AbstractModelLoader;
import jadex.commons.ICacheableModel;
import jadex.commons.ResourceInfo;

/**
 * 
 */
public class BDIModelLoader extends AbstractModelLoader
{
	//-------- constants --------
	
	/** The component file extension. */
	public static final String	FILE_EXTENSION_BDIV3_FIRST = "BDI";
	public static final String	FILE_EXTENSION_BDIV3_SECOND = ".class";
	public static final String	FILE_EXTENSION_BDIV3 = FILE_EXTENSION_BDIV3_FIRST + FILE_EXTENSION_BDIV3_SECOND;
	
	//-------- attributes --------
	
	/** The xml reader. */
	protected BDIClassReader reader;
	
	//-------- constructors --------
	
	/**
	 *  Create a new BPMN model loader.
	 */
	public BDIModelLoader()
	{
		super(new String[]{FILE_EXTENSION_BDIV3});
		this.reader = BDIClassGeneratorFactory.getInstance().createBDIClassReader(this);
	}

	//-------- methods --------
	
	/**
	 *  Load a component model.
	 *  @param name	The filename or logical name (resolved via imports and extensions).
	 *  @param imports	The imports, if any.
	 */
	public BDIModel loadComponentModel(String name, String[] imports, IResourceIdentifier clkey, ClassLoader classloader, Object context) throws Exception
	{
		return (BDIModel)loadModel(name, FILE_EXTENSION_BDIV3, imports, clkey, classloader, context);
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
//		System.out.println("cache miss: "+name);
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
			// Try to find directly as absolute path.
//			Class clazz = SReflect.findClass0(name, imports, classloader);
//			if(clazz!=null)
				ret = new ResourceInfo(name, null, 0L);
	
//			if(ret==null)
//				throw new IOException("File "+name+" not found in imports");//: "+SUtil.arrayToString(imports));
		}
		return ret;
	}
	
}
