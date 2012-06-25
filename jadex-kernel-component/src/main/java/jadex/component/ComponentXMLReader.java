package jadex.component;

import jadex.bridge.AbstractErrorReportBuilder;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IErrorReport;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.ResourceInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.collection.IndexMap;
import jadex.commons.collection.MultiCollection;
import jadex.kernelbase.CacheableKernelModel;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeConverter;
import jadex.xml.AttributeInfo;
import jadex.xml.IContext;
import jadex.xml.IObjectStringConverter;
import jadex.xml.IPostProcessor;
import jadex.xml.IStringObjectConverter;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.StackElement;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.reader.IObjectReaderHandler;
import jadex.xml.reader.AReader;
import jadex.xml.reader.ReadContext;
import jadex.xml.reader.Reader;
import jadex.xml.reader.XMLReaderFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.xml.stax.QName;
import jadex.xml.stax.ILocation;
import jadex.xml.stax.XMLReporter;


/**
 *  Reader for loading component XML models into a Java representation states.
 */
public class ComponentXMLReader
{
	//-------- constants --------
	
	/** Key for error entries in read context. */
	public static final String CONTEXT_ENTRIES = "entries";
	
	//-------- attributes --------
	
	/** The reader instance. */
	protected AReader reader;
	
	/** The manager. */
	protected TypeInfoPathManager manager;
	
	/** The handler. */
	protected IObjectReaderHandler handler;
	
	/** The mappings. */
	protected Set[] mappings;
	
	//-------- post processors and converters --------
	
//	public static IStringObjectConverter exconv = new IStringObjectConverter()
//	{
//		public Object convertString(String val, IContext context)
//		{
//			return SJavaParser.evaluateExpression((String)val, ((IModelInfo)context.getRootObject()).getAllImports(), null, context.getClassLoader());
//		}
//	};
	
	public static IStringObjectConverter classconv = new IStringObjectConverter()
	{
		public Object convertString(String val, IContext context) throws Exception
		{
			return new ClassInfo(SReflect.findClass((String)val, ((IModelInfo)context.getRootObject()).getAllImports(), context.getClassLoader()));
		}
	};
	
	public static IObjectStringConverter reclassconv = new IObjectStringConverter()
	{
		public String convertObject(Object val, IContext context)
		{
			String ret = null;
			if(val instanceof ClassInfo)
			{
				ret = ((ClassInfo)val).getTypeName();
				if(ret==null)
				{
					throw new RuntimeException("Class not found: "+val);
				}
			}
			return ret;
		}
	};
	
	/**
	 *  Parse expression text.
	 */
	public static class ExpressionProcessor	implements IPostProcessor
	{
		public Object postProcess(IContext context, Object object)
		{
			ModelInfo cm = (ModelInfo)context.getRootObject();
			UnparsedExpression exp = (UnparsedExpression)object;
			exp.parseExpression(cm.getAllImports(), context.getClassLoader());
			return object;
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
		
	//-------- constructors --------
	
	/**
	 *  Create a new reader.
	 */
	public ComponentXMLReader(Set[] mappings)
	{
		this(getXMLMapping(mappings));
	}
	
	/**
	 *  Create a new reader.
	 */
	public ComponentXMLReader(Set mappings)
	{
		this.reader = XMLReaderFactory.getInstance().createReader(false, false, new XMLReporter()
		{
			public void report(String msg, String type, Object info, ILocation location) throws Exception
			{
//				System.out.println("XML error: "+msg+", "+type+", "+info+", "+location);
//				Thread.dumpStack();
				IContext	context	= (IContext)AReader.READ_CONTEXT.get();
				Map	user	= (Map)context.getUserContext();
				MultiCollection	report	= (MultiCollection)user.get(CONTEXT_ENTRIES);
				String	pos;
				Tuple	stack	= new Tuple(((ReadContext)context).getStack());
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
			}
		});
		
		manager = new TypeInfoPathManager(mappings);
		handler = new BeanObjectReaderHandler(mappings);
	}
	
	//-------- methods --------
	
	/**
	 *  Read properties from xml.
	 *  @param info	The resource info.
	 *  @param classloader The classloader.
 	 */
	public CacheableKernelModel read(ResourceInfo rinfo, ClassLoader classloader, IResourceIdentifier rid, IComponentIdentifier root) throws Exception
	{
		Map	user	= new HashMap();
		MultiCollection	report	= new MultiCollection(new IndexMap().getAsMap(), LinkedHashSet.class);
		user.put(CONTEXT_ENTRIES, report);
		ModelInfo mi = (ModelInfo)reader.read(manager, handler, rinfo.getInputStream(), classloader, user);
		CacheableKernelModel ret = new CacheableKernelModel(mi);
		
		if(mi!=null)
		{
			mi.setFilename(rinfo.getFilename());
			mi.setType(ComponentComponentFactory.FILETYPE_COMPONENT);
//			mi.setClassloader(classloader);
			mi.setStartable(true);
			if(rid==null)
			{
				String src = SUtil.getCodeSource(rinfo.getFilename(), mi.getPackage());
				URL url = SUtil.toURL(src);
				rid = new ResourceIdentifier(new LocalResourceIdentifier(root, url), null);
			}
			mi.setResourceIdentifier(rid);

			if(!mi.checkName())
			{
				report.put(new Tuple(new Object[]{new StackElement(new QName("BpmnDiagram"), ret)}), "Name '"+mi.getName()+"' does not match file name '"+ret.getModelInfo().getFilename()+"'.");				
			}
			if(!mi.checkPackage())
			{
				report.put(new Tuple(new Object[]{new StackElement(new QName("BpmnDiagram"), ret)}), "Package '"+mi.getPackage()+"' does not match file name '"+ret.getModelInfo().getFilename()+"'.");				
			}
		}
		ret.setLastModified(rinfo.getLastModified());
		
		// Todo: error report for component models.
//		ret.initModelInfo(report);
		
		
		rinfo.getInputStream().close();
//		else
//		{
//			String errtext = buildReport(rinfo.getFilename(), rinfo.getFilename(),
//				new String[]{"Component", "Configuration"}, report, null).getErrorText();
//			throw new RuntimeException("Model error: "+errtext);
//		}
		
		if(report.size()>0)
		{
//			System.out.println("Error loading model: "+rinfo.getFilename()+" "+report);
			mi.setReport(buildReport(mi.getFullName(), mi.getFilename(), report));
		}
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
		Set<TypeInfo> types = new HashSet<TypeInfo>();
		
		String uri = "http://jadex.sourceforge.net/jadex";
		
//		TypeInfo satype = new TypeInfo(null, new ObjectInfo(MStartable.class),
//			new MappingInfo(null, new AttributeInfo[]{
//				new AttributeInfo(new AccessInfo("autoshutdown", "autoShutdown")),
//			}, null));
		
//		Method getname = null; 
//		Method putprop = null;
//		try
//		{
//			getname = UnparsedExpression.class.getMethod("getName", null);
//			putprop = ModelInfo.class.getMethod("addProperty", new Class[]{String.class, Object.class});
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "componenttype")), new ObjectInfo(ModelInfo.class), 
			new MappingInfo(null, "description", null,
				new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("autoshutdown", "autoShutdown")),
				new AttributeInfo(new AccessInfo(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"), null, AccessInfo.IGNORE_READWRITE))
				}, 
				new SubobjectInfo[]{
				new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "arguments"), new QName(uri, "argument")}), new AccessInfo(new QName(uri, "argument"), "argument")),
				new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "arguments"), new QName(uri, "result")}), new AccessInfo(new QName(uri, "result"), "result")),
				new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "services"), new QName(uri, "container")}), new AccessInfo(new QName(uri, "container"), "container")),
				new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "services"), new QName(uri, "providedservice")}), new AccessInfo(new QName(uri, "providedservice"), "providedService")),
				new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "services"), new QName(uri, "requiredservice")}), new AccessInfo(new QName(uri, "requiredservice"), "requiredService")),
				new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "componenttype")}), new AccessInfo(new QName(uri, "componenttype"), "subcomponentType")),
				new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "property")}), new AccessInfo(new QName(uri, "property"), "property", null, null))//, new BeanAccessInfo(putprop, null, "map", getname))),
			}), null, new BeanObjectReaderHandler()));
		
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "configuration")), new ObjectInfo(ConfigurationInfo.class), 
			new MappingInfo(null, "description", null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("type", "typeName")),
				new AttributeInfo(new AccessInfo("autoshutdown", "autoShutdown"))},
				new SubobjectInfo[]{
				new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "component")}), new AccessInfo(new QName(uri, "component"), "componentInstance")),
				new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "arguments"), new QName(uri, "result")}), new AccessInfo(new QName(uri, "result"), "result")),
				new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "services"), new QName(uri, "providedservice")}), new AccessInfo(new QName(uri, "providedservice"), "providedService")),
				new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "services"), new QName(uri, "requiredservice")}), new AccessInfo(new QName(uri, "requiredservice"), "requiredService")),
				new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "steps"), new QName(uri, "initialstep")}), new AccessInfo(new QName(uri, "initialstep"), "initialStep")),
				new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "steps"), new QName(uri, "endstep")}), new AccessInfo(new QName(uri, "endstep"), "endStep")),
			}), null, new BeanObjectReaderHandler()));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "componenttype"), new QName(uri, "arguments"), new QName(uri, "argument")}), new ObjectInfo(Argument.class, new ExpressionProcessor()), 
			new MappingInfo(null, "description", "value",
			new AttributeInfo[]{new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))}, null), null, new BeanObjectReaderHandler()));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "componenttype"), new QName(uri, "arguments"), new QName(uri, "result")}), new ObjectInfo(Argument.class, new ExpressionProcessor()), 
			new MappingInfo(null, "description", "value",
			new AttributeInfo[]{new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))}, null), null, new BeanObjectReaderHandler()));
			
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "import")), new ObjectInfo(String.class), null, null, new BeanObjectReaderHandler()));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "configuration"), new QName(uri, "arguments"), new QName(uri, "argument")}), new ObjectInfo(UnparsedExpression.class, new ExpressionProcessor()), 
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
			}, null)));
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "configuration"), new QName(uri, "arguments"), new QName(uri, "result")}), new ObjectInfo(UnparsedExpression.class, new ExpressionProcessor()), 
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
			}, null)));
			
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "componenttypes"), new QName(uri, "componenttype")}), new ObjectInfo(SubcomponentTypeInfo.class),
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("autoshutdown", "autoShutdown")),
			}, null), null, new BeanObjectReaderHandler()));		
		
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "component")), new ObjectInfo(ComponentInstanceInfo.class),
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("type", "typeName")),
				new AttributeInfo(new AccessInfo("autoshutdown", "autoShutdown")),
				new AttributeInfo(new AccessInfo("number"))
			}, null)));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "component"), new QName(uri, "arguments"), new QName(uri, "argument")}), new ObjectInfo(UnparsedExpression.class, new ExpressionProcessor()), 
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
			}, null)));
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "providedservice")}), 
			new ObjectInfo(ProvidedServiceInfo.class),
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "type"), new AttributeConverter(classconv, reclassconv)),
			}, null), null, new BeanObjectReaderHandler()));
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "implementation")), new ObjectInfo(ProvidedServiceImplementation.class, new ExpressionProcessor()), 
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv)),
			}, null)));
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "publish")), new ObjectInfo(PublishInfo.class),
			new MappingInfo(null, null, null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("publishid", "publishId")),
				new AttributeInfo(new AccessInfo("publishtype", "publishType")),
				new AttributeInfo(new AccessInfo("mapping", "mapping"), new AttributeConverter(classconv, reclassconv)),
			}, null)));
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "publish"), new QName(uri, "property")}), new ObjectInfo(UnparsedExpression.class, new ExpressionProcessor()), 
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
			}, null)));
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "interceptor")}), new ObjectInfo(UnparsedExpression.class, new ExpressionProcessor()), 
			new MappingInfo(null, null, "value", new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
			}, null)));
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "requiredservice")), new ObjectInfo(RequiredServiceInfo.class), 
			new MappingInfo(null, null, null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("class", "type"), new AttributeConverter(classconv, reclassconv))
			}, new SubobjectInfo[]{
			new SubobjectInfo(new XMLInfo(new QName[]{new QName(uri, "binding")}), new AccessInfo(new QName(uri, "binding"), "defaultBinding")),
			}), null, new BeanObjectReaderHandler()));
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "binding")), new ObjectInfo(RequiredServiceBinding.class), 
			new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("componentname", "componentName")),
				new AttributeInfo(new AccessInfo("componenttype", "componentType")),
			})));
		
//		types.add(new TypeInfo(new XMLInfo(new QName(uri, "container")), new ObjectInfo(MExpressionType.class, new ExpressionProcessor()), 
//			new MappingInfo(null, null, "value", new AttributeInfo[]{
//				new AttributeInfo(new AccessInfo("class", "className"))
//			}, null)));
					
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "initialstep")), new ObjectInfo(UnparsedExpression.class, new ExpressionProcessor()), 
				new MappingInfo(null, null, "value", new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
				}, null), null, new BeanObjectReaderHandler()));
		
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "endstep")), new ObjectInfo(UnparsedExpression.class, new ExpressionProcessor()), 
				new MappingInfo(null, null, "value", new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
				}, null), null, new BeanObjectReaderHandler()));
		
		types.add(new TypeInfo(new XMLInfo(new QName(uri, "property")), new ObjectInfo(UnparsedExpression.class, new ExpressionProcessor()), 
				new MappingInfo(null, null, "value", new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("class", "clazz"), new AttributeConverter(classconv, reclassconv))
				}, null), null, new BeanObjectReaderHandler()));
		
		for(int i=0; mappings!=null && i<mappings.length; i++)
		{
			types.addAll(mappings[i]);
		}
		
		return types;
	}

	/**
     *  Build the error report.
     */
    public static IErrorReport buildReport(String modelname, String filename, MultiCollection entries)
    {
        return new AbstractErrorReportBuilder(modelname, filename,
            new String[]{"Component", "Configuration"}, entries, null)
        {
            public boolean isInCategory(Object obj, String category)
            {
                return "Component".equals(category) && obj instanceof SubcomponentTypeInfo
                    || "Configuration".equals(category) && obj instanceof ConfigurationInfo;
            }

            public Object getPathElementObject(Object element)
            {
                return ((StackElement)element).getObject();
            }

            public String getObjectName(Object obj)
            {
                String    name    = null;
                String    type    = obj!=null ? SReflect.getInnerClassName(obj.getClass()) : null;
                if(obj instanceof SubcomponentTypeInfo)
                {
                    name    = ((SubcomponentTypeInfo)obj).getName();
                }
                else if(obj instanceof ConfigurationInfo)
                {
                    name    = ((ConfigurationInfo)obj).getName();
                    type    = "Configuration";
                }
                else if(obj instanceof UnparsedExpression)
                {
                    name    = ((UnparsedExpression)obj).getName();
                }
//                else if(obj instanceof MExpressionType)
//                {
//                    IParsedExpression    pexp    = ((MExpressionType)obj).getParsedValue();
//                    String    exp    = pexp!=null ? pexp.getExpressionText() : null;
//                    name    = exp!=null ? ""+exp : null;
//                }

//                if(type!=null && type.startsWith("M") && type.endsWith("Type"))
//                {
//                    type    = type.substring(1, type.length()-4);
//                }
                if(type!=null && type.endsWith("Info"))
                {
                    type    = type.substring(0, type.length()-4);
                }

                return type!=null ? name!=null ? type+" "+name : type : name!=null ? name : "";
            }
        }.buildErrorReport();
    }	
}
