package jadex.application;

import jadex.application.ApplicationComponentFactory.ExpressionProcessor;
import jadex.application.model.MApplicationInstance;
import jadex.application.model.MApplicationType;
import jadex.application.model.MArgument;
import jadex.application.model.MComponentInstance;
import jadex.application.model.MComponentType;
import jadex.application.model.MExpressionType;
import jadex.application.model.MSpaceInstance;
import jadex.application.model.MSpaceType;
import jadex.bridge.Argument;
import jadex.commons.ResourceInfo;
import jadex.javaparser.SJavaParser;
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

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 *  Reader for loading Application XML models into a Java representation states.
 */
public class ApplicationXMLReader
{
	//-------- attributes --------
	
	/** The reader instance. */
	protected Reader reader;
//	protected static Map readers = new HashMap();
	
	/** The mappings. */
	protected Set[] mappings;
	
	//-------- constructors --------
	
	/**
	 *  Create a new reader.
	 */
	public ApplicationXMLReader(Set[] mappings)
	{
		this.reader = new Reader(new BeanObjectReaderHandler(getXMLMapping(mappings)));
	}
	
	//-------- methods --------
	
	// Initialize reader instance.
//	static
//	{
//		reader = new Reader(new BeanObjectReaderHandler(getXMLMapping()));
//	}
	
//	/**
//	 *  Get the reader instance.
//	 */
//	public synchronized static Reader getReader(Set[] mappings)
//	{
//		Reader ret = (Reader)readers.get(SUtil.arrayToList(mappings));
//		if(ret==null)
//		{
//			System.out.println("Created new app loader: "+SUtil.arrayToString(mappings));
//			ret = new Reader(new BeanObjectReaderHandler(getXMLMapping(mappings)));
//		}
//		return ret;
//	}
	
	/**
	 *  Read properties from xml.
	 *  @param info	The resource info.
	 *  @param classloader The classloader.
 	 */
	public MApplicationType read(ResourceInfo rinfo, ClassLoader classloader) throws Exception
	{
		MApplicationType ret = (MApplicationType)reader.read(rinfo.getInputStream(), classloader, null);
		
		ret.setFilename(rinfo.getFilename());
		ret.setLastModified(rinfo.getLastModified());
		ret.setFilename(rinfo.getFilename());
		ret.setLastModified(rinfo.getLastModified());
		ret.setClassloader(classloader);
		String name = new File(rinfo.getFilename()).getName();
		name = name.substring(0, name.length()-5);
		ret.setName(name);	
		ret.initModelInfo();
		
		rinfo.getInputStream().close();
		return ret;
	}
	
	/**
	 *  Get the XML mapping.
	 */
	public static Set getXMLMapping(Set[] mappings)
	{
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
					Argument arg = (Argument)mapp.getModelInfo().getArgument(overridenarg.getName());
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
				
		return types;
	}
}
