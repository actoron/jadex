package jadex.application;

import jadex.application.model.MApplicationInstance;
import jadex.application.model.MApplicationType;
import jadex.application.model.MComponentInstance;
import jadex.application.model.MComponentType;
import jadex.application.model.MExpressionType;
import jadex.application.model.MProvidedServiceType;
import jadex.application.model.MRequiredServiceType;
import jadex.application.model.MSpaceInstance;
import jadex.application.model.MSpaceType;
import jadex.bridge.Argument;
import jadex.commons.ResourceInfo;
import jadex.commons.SReflect;
import jadex.commons.Tuple;
import jadex.commons.collection.IndexMap;
import jadex.commons.collection.MultiCollection;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeConverter;
import jadex.xml.AttributeInfo;
import jadex.xml.IContext;
import jadex.xml.IPostProcessor;
import jadex.xml.IStringObjectConverter;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.StackElement;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.reader.ReadContext;
import jadex.xml.reader.Reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;

/**
 *  Reader for loading Application XML models into a Java representation states.
 */
public class ApplicationXMLReader
{
	//-------- attributes --------
	
	/** The reader instance. */
	protected Reader reader;
	
	/** The mappings. */
	protected Set[] mappings;
	
	//-------- constructors --------
	
	/**
	 *  Create a new reader.
	 */
	public ApplicationXMLReader(Set[] mappings)
	{
		this.reader = new Reader(new BeanObjectReaderHandler(getXMLMapping(mappings)), false, false, new XMLReporter()
		{
			public void report(String msg, String type, Object info, Location location) throws XMLStreamException
			{
//				System.out.println("XML error: "+msg+", "+type+", "+info+", "+location);
				IContext	context	= (IContext)Reader.READ_CONTEXT.get();
				MultiCollection	report	= (MultiCollection)context.getUserContext();
				String	pos;
				Tuple	stack	= new Tuple(((ReadContext)context).getStack().toArray());
				if(stack.getEntities().length>0)
				{
					StackElement	se	= (StackElement)stack.get(stack.getEntities().length-1);
					pos	= " (line "+se.getLocation().getLineNumber()+", column "+se.getLocation().getColumnNumber()+")";
				}
				else
				{
					pos	= " (line 0, column 0)";			
				}
				report.put(stack, msg+pos);
				report.size();
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  Read properties from xml.
	 *  @param info	The resource info.
	 *  @param classloader The classloader.
 	 */
	public MApplicationType read(ResourceInfo rinfo, ClassLoader classloader) throws Exception
	{
		MultiCollection	report	= new MultiCollection(new IndexMap().getAsMap(), LinkedHashSet.class);
		MApplicationType ret = (MApplicationType)reader.read(rinfo.getInputStream(), classloader, report);
		
		ret.setFilename(rinfo.getFilename());
		ret.setLastModified(rinfo.getLastModified());
		ret.setClassloader(classloader);
		ret.initModelInfo(report);
		
		// Exclude IApplicationExternalAccess 
		Map props = ret.getModelInfo().getProperties();
		if(props==null)
		{
			props = new HashMap();
			ret.getModelInfo().setProperties(props);
		}
		addMethodInfos(props, "remote_excluded", new String[]{
			"getServiceProvider", "getSpace"});
		
		rinfo.getInputStream().close();
		return ret;
	}
	
	/**
	 *  Add method info.
	 */
	public static void addMethodInfos(Map props, String type, String[] names)
	{
		Object ex = props.get(type);
		if(ex!=null)
		{
			List newex = new ArrayList();
			for(Iterator it=SReflect.getIterator(ex); it.hasNext(); )
			{
				newex.add(it.next());
			}
			for(int i=0; i<names.length; i++)
			{
				newex.add(names[i]);
			}
		}
		else
		{
			props.put(type, names);
		}
	}
	
	/**
	 *  Get the XML mapping.
	 */
	public static Set getXMLMapping(Set[] mappings)
	{
		Set types = new HashSet();
		
		// Convert expression directly into value.
		IStringObjectConverter exconv = new IStringObjectConverter()
		{
			public Object convertString(String val, IContext context)
			{
				Object	ret	= null;
				try
				{
					ret	= SJavaParser.evaluateExpression((String)val, ((MApplicationType)context.getRootObject()).getAllImports(), null, context.getClassLoader());
				}
				catch(RuntimeException e)
				{
					Object	se	= new Tuple(((ReadContext)context).getStack().toArray());
					MultiCollection	report	= (MultiCollection)context.getUserContext();
					report.put(se, e.toString());
				}
				return  ret;
			}
		};
		
		// Convert expression into parsed expression object.
		IStringObjectConverter pexconv = new IStringObjectConverter()
		{
			public Object convertString(String val, IContext context)
			{
				Object	ret	= null;
				try
				{
					ret	= SJavaParser.parseExpression((String)val, ((MApplicationType)context.getRootObject()).getAllImports(), context.getClassLoader());
				}
				catch(RuntimeException e)
				{
					Object	se	= new Tuple(((ReadContext)context).getStack().toArray());
					MultiCollection	report	= (MultiCollection)context.getUserContext();
					report.put(se, e.toString());
				}
				return  ret;
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
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "application")), new ObjectInfo(MApplicationInstance.class, new IPostProcessor()
		{
			public Object postProcess(IContext context, Object object)
			{
				MApplicationInstance app = (MApplicationInstance)object;
				MApplicationType mapp = (MApplicationType)context.getRootObject();
				
				List margs = app.getArguments();
				for(int i=0; i<margs.size(); i++)
				{
					try
					{
						MExpressionType overridenarg = (MExpressionType)margs.get(i);
						Argument arg = (Argument)mapp.getModelInfo().getArgument(overridenarg.getName());
						if(arg==null)
							throw new RuntimeException("Overridden argument not declared in application type: "+overridenarg.getName());
						
						Object val = overridenarg.getParsedValue().getValue(null);
						arg.setDefaultValue(app.getName(), val);
					}
					catch(RuntimeException e)
					{
						Object	se	= new Tuple(((ReadContext)context).getStack().toArray());
						MultiCollection	report	= (MultiCollection)context.getUserContext();
						report.put(se, e.toString());
					}
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
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "applicationtype"), new QName(uri, "arguments"), new QName(uri, "argument")}), new ObjectInfo(Argument.class), 
			new MappingInfo(null, "description", new AttributeInfo(new AccessInfo((String)null, "defaultValue"), new AttributeConverter(exconv, null)))));
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "import")), new ObjectInfo(String.class)));
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "application"), new QName(uri, "arguments"), new QName(uri, "argument")}), new ObjectInfo(MExpressionType.class, new ExpressionProcessor()), 
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "className"))
			}, null)));
		
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "componenttype")), new ObjectInfo(MComponentType.class)));
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "component")), new ObjectInfo(MComponentInstance.class),
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("type", "typeName")),
				new AttributeInfo(new AccessInfo("number"), new AttributeConverter(pexconv, null))
			}, null)));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "component"), new QName(uri, "arguments"), new QName(uri, "argument")}), new ObjectInfo(MExpressionType.class, new ExpressionProcessor()), 
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "className"))
			}, null)));
		
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "providedservice")), new ObjectInfo(MProvidedServiceType.class, new ExpressionProcessor()), 
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "className")),
				new AttributeInfo(new AccessInfo("componentname", "componentName")),
				new AttributeInfo(new AccessInfo("componenttype", "componentType"))
			}, null)));
		
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "requiredservice")), new ObjectInfo(MRequiredServiceType.class, new ExpressionProcessor()), 
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
					Object	se	= new Tuple(((ReadContext)context).getStack().toArray());
					MultiCollection	report	= (MultiCollection)context.getUserContext();
					report.put(se, e.toString());
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
					catch(RuntimeException e)
					{
						Object	se	= new Tuple(((ReadContext)context).getStack().toArray());
						MultiCollection	report	= (MultiCollection)context.getUserContext();
						report.put(se, e.toString());
					}
				}	
				else
				{
					Object	se	= new Tuple(((ReadContext)context).getStack().toArray());
					MultiCollection	report	= (MultiCollection)context.getUserContext();
					report.put(se, "Unknown condition language: "+lang);
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
