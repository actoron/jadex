package jadex.component;

import jadex.commons.AbstractModelLoader;
import jadex.commons.ICacheableModel;
import jadex.commons.ResourceInfo;
import jadex.component.model.MApplicationType;

import java.util.Set;

/**
 *  Loader for application files.
 */
public class ComponentModelLoader extends AbstractModelLoader
{
	//-------- constants --------
	
	/** The application file extension. */
	public static final String	FILE_EXTENSION_APPLICATION = ".application.xml";
	
	//-------- attributes --------
	
	/** The xml reader. */
	protected ComponentXMLReader reader;
	
	//-------- constructors --------
	
	/**
	 *  Create a new BPMN model loader.
	 */
	public  ComponentModelLoader(Set[] mappings)
	{
		super(new String[]{FILE_EXTENSION_APPLICATION});
		this.reader = new ComponentXMLReader(mappings);
	}

	//-------- methods --------
	
	/**
	 *  Load a BPMN model.
	 *  @param name	The filename or logical name (resolved via imports and extensions).
	 *  @param imports	The imports, if any.
	 */
	public MApplicationType loadApplicationModel(String name, String[] imports, ClassLoader classloader) throws Exception
	{
		return (MApplicationType)loadModel(name, FILE_EXTENSION_APPLICATION, imports, classloader);
	}
	
	//-------- AbstractModelLoader methods --------
		
	/**
	 *  Load a model.
	 *  @param name	The original name (i.e. not filename).
	 *  @param info	The resource info.
	 */
	protected ICacheableModel doLoadModel(String name, ResourceInfo info, ClassLoader classloader) throws Exception
	{
		return (ICacheableModel)reader.read(info, classloader);
	}
}