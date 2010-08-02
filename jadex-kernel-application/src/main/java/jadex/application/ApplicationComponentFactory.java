package jadex.application;

import jadex.application.model.ApplicationModel;
import jadex.application.model.MApplicationInstance;
import jadex.application.model.MApplicationType;
import jadex.application.model.MArgument;
import jadex.application.model.MComponentInstance;
import jadex.application.model.MComponentType;
import jadex.application.model.MExpressionType;
import jadex.application.model.MSpaceInstance;
import jadex.application.model.MSpaceType;
import jadex.application.runtime.impl.Application;
import jadex.bridge.Argument;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.ResourceInfo;
import jadex.commons.SGUI;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.service.BasicService;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeConverter;
import jadex.xml.AttributeInfo;
import jadex.xml.IContext;
import jadex.xml.IPostProcessor;
import jadex.xml.IStringObjectConverter;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.reader.Reader;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.UIDefaults;
import javax.xml.namespace.QName;

/**
 *  Factory for default contexts.
 *  No special properties supported, yet.
 */
public class ApplicationComponentFactory extends BasicService implements IComponentFactory
{
	//-------- constants --------
	
	/** The application agent file type. */
	public static final String	FILETYPE_APPLICATION = "Application";
	
	/** The application file extension. */
	public static final String	FILE_EXTENSION_APPLICATION	= ".application.xml";

	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"application", SGUI.makeIcon(ApplicationComponentFactory.class, "/jadex/application/images/application.png"),
	});
	
	//-------- attributes --------
	
	/** The xml reader. */
	protected Reader reader;
	
	//-------- constructors --------
	
	/**
	 *  Create a new application factory.
	 */
	public ApplicationComponentFactory(Object providerid)
	{
		this(null, providerid);
	}
	
	/**
	 *  Create a new application factory.
	 *  @param platform	The agent platform.
	 *  @param mappings	The XML reader mappings of supported spaces (if any).
	 */
	public ApplicationComponentFactory(Set[] mappings, Object providerid)
	{
		super(BasicService.createServiceIdentifier(providerid, ApplicationComponentFactory.class));

		Set types = new HashSet();
		
		IStringObjectConverter exconv = new IStringObjectConverter()
		{
			public Object convertString(String val, IContext context)
			{
				return SJavaParser.evaluateExpression((String)val, ((MApplicationType)context.getRootObject()).getAllImports(), null, context.getClassLoader());
			}
		};
		
		String uri = "http://jadex.sourceforge.net/jadex-application";
		
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "applicationtype")), new ObjectInfo(MApplicationType.class), 
			new MappingInfo(null, "description", null,
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("autoshutdown", "autoShutdown")),
			new AttributeInfo(new AccessInfo(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"), null, AccessInfo.IGNORE_READWRITE))
			}, 
			new SubobjectInfo[]{
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "arguments"), new QName(uri, "argument")}), new AccessInfo(new QName(uri, "argument"), "argument")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "arguments"), new QName(uri, "result")}), new AccessInfo(new QName(uri, "result"), "result")),
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "services"), new QName(uri, "container")}), new AccessInfo(new QName(uri, "container"), "container"))
			})));
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "spacetype")), new ObjectInfo(MSpaceType.class)));
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "agenttype")), new ObjectInfo(MComponentType.class)));
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "application")), new ObjectInfo(MApplicationInstance.class, new IPostProcessor()
		{
			public Object postProcess(IContext context, Object object)
			{
				MApplicationInstance app = (MApplicationInstance)object;
				MApplicationType mapp = (MApplicationType)context.getRootObject();
				
				List margs = app.getArguments();
				for(int i=0; i<margs.size(); i++)
				{
					MArgument overridenarg = (MArgument)margs.get(i);
					Argument arg = (Argument)mapp.getArgument(overridenarg.getName());
					if(arg==null)
						throw new RuntimeException("Overridden argument not declared in application type: "+overridenarg.getName());
					
					Object val = SJavaParser.evaluateExpression(overridenarg.getValue(), ((MApplicationType)context.getRootObject()).getAllImports(), null, context.getClassLoader());
					arg.setDefaultValue(app.getName(), val);
				}
				
				return null;
			}
			
			public int getPass()
			{
				return 0;
			}
		}), 
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("type", "typeName"))})));
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "space")), new ObjectInfo(MSpaceInstance.class)));
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "agent")), new ObjectInfo(MComponentInstance.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("type", "typeName")),
			new AttributeInfo(new AccessInfo("number", "numberText"))
			}, null)));
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "agent"), new QName(uri, "arguments"), new QName(uri, "argument")}), new ObjectInfo(MArgument.class), 
			new MappingInfo(null, null, "value")));
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "applicationtype"), new QName(uri, "arguments"), new QName(uri, "argument")}), new ObjectInfo(Argument.class), 
			new MappingInfo(null, "description", new AttributeInfo(new AccessInfo((String)null, "defaultValue"), new AttributeConverter(exconv, null)))));
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "import")), new ObjectInfo(String.class)));
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "application"), new QName(uri, "arguments"), new QName(uri, "argument")}), new ObjectInfo(MArgument.class), 
			new MappingInfo(null, null, "value")));
		
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "componenttype")), new ObjectInfo(MComponentType.class)));
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "component")), new ObjectInfo(MComponentInstance.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("type", "typeName")),
			new AttributeInfo(new AccessInfo("number", "numberText"))
			}, null)));
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "component"), new QName(uri, "arguments"), new QName(uri, "argument")}), new ObjectInfo(MArgument.class), 
			new MappingInfo(null, null, "value")));
		
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "service")), new ObjectInfo(MExpressionType.class, new ExpressionProcessor()), 
			new MappingInfo(null, null, "value", new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", "className"))
			}, null)));
		
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "container")), new ObjectInfo(MExpressionType.class, new ExpressionProcessor()), 
			new MappingInfo(null, null, "value", new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", "className"))
			}, null)));
					
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "property")), new ObjectInfo(MExpressionType.class, new ExpressionProcessor()), 
			new MappingInfo(null, null, "value", new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("class", "className"))
			}, null)));
					
		for(int i=0; mappings!=null && i<mappings.length; i++)
		{
			types.addAll(mappings[i]);
		}
				
		this.reader = new Reader(new BeanObjectReaderHandler(types));
	}
	
	/**
	 *  Start the service.
	 * /
	public synchronized IFuture	startService()
	{
		return super.startService();
	}*/
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 * /
	public synchronized IFuture	shutdownService()
	{
		return super.shutdownService();
	}*/
	
	//-------- IComponentFactory interface --------
	
	/**
	 * Create a component instance.
	 * @param adapter The component adapter.
	 * @param model The component model.
	 * @param config The name of the configuration (or null for default configuration) 
	 * @param arguments The arguments for the agent as name/value pairs.
	 * @param parent The parent component (if any).
	 * @return An instance of a component.
	 */
	public Object[] createComponentInstance(IComponentDescription desc, IComponentAdapterFactory factory, ILoadableComponentModel model, String config, Map arguments, IExternalAccess parent)
	{
		Application	context = null;
		
		MApplicationType apptype = ((ApplicationModel)model).getApplicationType();
		List apps = apptype.getMApplicationInstances();
				
		// Select application instance according to configuraion.
		MApplicationInstance app = null;
		if(config==null && apps.size()>0)
			app = (MApplicationInstance)apps.get(0);
		
		for(int i=0; app==null && i<apps.size(); i++)
		{
			MApplicationInstance tmp = (MApplicationInstance)apps.get(i);
			if(config.equals(tmp.getName()))
				app = tmp;
		}
		
		if(app==null)
			app = new MApplicationInstance("default");

		// Create context for application.
		context	= new Application(desc, (ApplicationModel)model, app, factory, parent, arguments);
		
		// todo: result listener?
		
		// todo: create application context as return value?!
				
//		return context;
		return new Object[]{context, context.getComponentAdapter()};
	}
		
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public ILoadableComponentModel loadModel(String model, String[] imports, ClassLoader classloader)
	{
		ILoadableComponentModel ret = null;
		
		if(model!=null && model.toLowerCase().endsWith(FILE_EXTENSION_APPLICATION))
		{
			MApplicationType apptype = null;
			ResourceInfo	rinfo	= null;
			try
			{
//				ClassLoader cl = ((ILibraryService)container.getService(ILibraryService.class)).getClassLoader();
//				ClassLoader cl = libservice.getClassLoader();
				rinfo	= getResourceInfo(model, FILE_EXTENSION_APPLICATION, imports, classloader);
				apptype = (MApplicationType)reader.read(rinfo.getInputStream(), classloader, null);
				ret = new ApplicationModel(apptype, rinfo.getFilename(), classloader);
//				System.out.println("Loaded application type: "+apptype);
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			finally
			{
				if(rinfo!=null)
					rinfo.cleanup();				
			}
		}
		
		return ret;
	}
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if model can be loaded.
	 */
	public boolean isLoadable(String model, String[] imports, ClassLoader classloader)
	{
		return model.endsWith(FILE_EXTENSION_APPLICATION);
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public boolean isStartable(String model, String[] imports, ClassLoader classloader)
	{
		return model.endsWith(FILE_EXTENSION_APPLICATION);
	}

	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getComponentTypes()
	{
		return new String[]{FILETYPE_APPLICATION};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public Icon getComponentTypeIcon(String type)
	{
		return type.equals(FILETYPE_APPLICATION)? icons.getIcon("application"): null;
	}

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 */
	public String getComponentType(String model, String[] imports, ClassLoader classloader)
	{
		return model.toLowerCase().endsWith(FILE_EXTENSION_APPLICATION)? FILETYPE_APPLICATION: null;
	}

	/**
	 *  Get the properties.
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools.
	 *  @param type	The component type. 
	 *  @return The properties or null, if the component type is not supported by this factory.
	 */
	public Map	getProperties(String type)
	{
		return FILETYPE_APPLICATION.equals(type)
			? Collections.EMPTY_MAP : null;
	}

	//-------- helper methods --------
	
	/**
	 *  Load an xml Jadex model.
	 *  Creates file name when specified with or without package.
	 *  @param xml The filename | fully qualified classname
	 *  @return The loaded model.
	 */
	// Todo: fix directory stuff!???
	// Todo: Abstract model loader unifying app/bdi loading
	public static ResourceInfo getResourceInfo(String xml, String suffix, String[] imports, ClassLoader classloader) throws IOException
	{
		if(xml==null)
			throw new IllegalArgumentException("Required ADF name nulls.");
		if(suffix==null && !xml.endsWith(FILE_EXTENSION_APPLICATION))
			throw new IllegalArgumentException("Required suffix nulls.");

		if(suffix==null)
			suffix="";
		
		// Try to find directly as absolute path.
		String resstr = xml;
		ResourceInfo ret = SUtil.getResourceInfo0(resstr, classloader);

		if(ret==null || ret.getInputStream()==null)
		{
			// Fully qualified package name? Can also be full package name with empty package ;-)
			//if(xml.indexOf(".")!=-1)
			//{
				resstr	= SUtil.replace(xml, ".", "/") + suffix;
				//System.out.println("Trying: "+resstr);
				ret	= SUtil.getResourceInfo0(resstr, classloader);
			//}

			// Try to find in imports.
			for(int i=0; (ret==null || ret.getInputStream()==null) && imports!=null && i<imports.length; i++)
			{
				// Package import
				if(imports[i].endsWith(".*"))
				{
					resstr = SUtil.replace(imports[i].substring(0,
						imports[i].length()-1), ".", "/") + xml + suffix;
					//System.out.println("Trying: "+resstr);
					ret	= SUtil.getResourceInfo0(resstr, classloader);
				}
				// Direct import
				else if(imports[i].endsWith(xml))
				{
					resstr = SUtil.replace(imports[i], ".", "/") + suffix;
					//System.out.println("Trying: "+resstr);
					ret	= SUtil.getResourceInfo0(resstr, classloader);
				}
			}
		}

		if(ret==null || ret.getInputStream()==null)
			throw new IOException("File "+xml+" not found in imports: "+SUtil.arrayToString(imports));

		return ret;
	}	
	
	//-------- helper classes --------
	
	/**
	 *  Parse expression text.
	 */
	public static class ExpressionProcessor	implements IPostProcessor
	{
		// Hack!!! Should be configurable.
		protected static IExpressionParser	exp_parser	= new JavaCCExpressionParser();
		
		/**
		 *  Parse expression text.
		 */
		public Object postProcess(IContext context, Object object)
		{
			MApplicationType app = (MApplicationType)context.getRootObject();
			MExpressionType exp = (MExpressionType)object;
			
			String classname = exp.getClassName();
			if(classname!=null)
			{
				try
				{
					Class clazz = SReflect.findClass(classname, app.getAllImports(), context.getClassLoader());
					exp.setClazz(clazz);
				}
				catch(Exception e)
				{
	//					report.put(se, e.toString());
					e.printStackTrace();
				}
			}
			
			String lang = exp.getLanguage();
			String value = exp.getValue(); 
			if(value!=null)
			{
				if(lang==null || "java".equals(lang))
				{
					try
					{
						IParsedExpression pexp = exp_parser.parseExpression(value, app.getAllImports(), null, context.getClassLoader());
						exp.setParsedValue(pexp);
					}
					catch(Exception e)
					{
	//					report.put(se, e.toString());
						e.printStackTrace();
					}
				}	
				else
				{
					throw new RuntimeException("Unknown condition language: "+lang);
				}
			}
			
			return null;
		}
		
		/**
		 *  Get the pass number.
		 *  @return The pass number.
		 */
		public int getPass()
		{
			return 0;
		}
	}
}
