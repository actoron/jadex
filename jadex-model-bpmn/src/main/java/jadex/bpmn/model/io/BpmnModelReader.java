package jadex.bpmn.model.io;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MIdElement;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSubProcess;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeInfo;
import jadex.xml.IAttributeConverter;
import jadex.xml.IContext;
import jadex.xml.IPostProcessor;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.reader.AReadContext;
import jadex.xml.reader.AReader;
import jadex.xml.reader.XMLReaderFactory;
import jadex.xml.stax.QName;

public class BpmnModelReader
{
	/** The element mapper. */
	protected static final ElementMapper E_MAPPER = new ElementMapper();
	
	/** The activity processor */
	protected static final ActivityProcessor ACT_PROCESSOR = new ActivityProcessor();
	
	/** The class info converter */
	protected static final IAttributeConverter CLASS_INFO_CONVERTER = new ClassInfoConverter();
	
	protected static final ExpressionConverter EXP_CONVERTER = new ExpressionConverter();
	
	public BpmnModelReader()
	{
	}
	
	public void read(File file) throws Exception
	{
		FileInputStream is = new FileInputStream(file);
		AReader reader = XMLReaderFactory.getInstance().createReader(false, false, null);
		Set types = getXMLMapping();
		reader.read(new TypeInfoPathManager(types),
			new BeanObjectReaderHandler(types)
			{
				public void handleAttributeValue(Object object, QName xmlattrname,
						List attrpath, String attrval, Object attrinfo,
						AReadContext context) throws Exception
				{
					if (attrinfo != null)
					{
						super.handleAttributeValue(object, xmlattrname, attrpath, attrval, attrinfo, context);
					}
				}
			}, is, BpmnModelReader.class.getClassLoader(), new BpmnReadContext());
	}
	
	protected static final Set getXMLMapping()
	{
		Set<TypeInfo> types = new HashSet<TypeInfo>();
		
		String semuri = "http://www.omg.org/spec/BPMN/20100524/MODEL";
		String exturi = "http://www.activecomponents.org/bpmnextensions";
		
		TypeInfo type	= createTypeInfo(MBpmnModel.class, semuri, "definitions", null,
								 		 new AccessInfo[] { new AccessInfo("process", "pool") },
								 		 new QName[] { new QName("targetNamespace") },
								 		 new QName[] { new QName(semuri, "flowNodeRf"), new QName(semuri, "outgoing"), new QName(semuri, "incoming") });
//		type.setReaderHandler(new BeanObjectReaderHandler();
		types.add(type);
		
		type = createTypeInfo(MPool.class, null, semuri, "process", null,
				new Object[] { new AccessInfo(new QName(semuri, "sProcess"), "activity") },
				null, null);
		types.add(type);
		
//		types.add(createTypeInfo(MLane.class, E_MAPPER, semuri, "lane",
//								, null));
		
		type = createTypeInfo(MLane.class,
							  new ElementMapper()
							  {
							      public Object postProcess(IContext context,
							    		Object object)
							    {
							    	object = super.postProcess(context, object);
							    	BpmnReadContext rc = (BpmnReadContext) context.getUserContext();
							    	for (String flowref : rc.getFlowRefs())
							    	{
							    		System.out.println("Adding to map: " + flowref);
							    		rc.lanemap.put(flowref, (MLane) object);
							    	}
							    	rc.getFlowRefs().clear();
							    	return object;
							    }
							  },
							  semuri,
							  "lane", null,
							  new Object[] { new AccessInfo(new QName(semuri, "subProcess"), "activity") },
							  null, null);
		types.add(type);
		
		type = createTypeInfo(String.class,
							  new IPostProcessor()
							  {
							      public Object postProcess(IContext context, Object object)
							      {
							    	  System.out.println("Triggered");
							    	  BpmnReadContext rc = (BpmnReadContext) context.getUserContext();
							    	  rc.getFlowRefs().add((String) object);
							    	  return IPostProcessor.DISCARD_OBJECT;
							      }
							      
							    public int getPass()
							    {
							    	return 0;
							    }
							  },
							  semuri,
							  "flowNodeRef",
							  null, null, null, null);
		types.add(type);
		
		type = createTypeInfo(MSubProcess.class,
							  ACT_PROCESSOR,
							  semuri,
							  "subProcess",
							  null,//new Object[] { new AccessInfo("id", "id") },
							  new Object[] { new AccessInfo(new QName(semuri, "task"), "activity") },
							  null,
							  new QName[] { new QName(semuri, "incoming"), new QName(semuri, "outgoing") });
		types.add(type);
		
		type = createTypeInfo(MActivity.class,
				  ACT_PROCESSOR,
				  semuri,
				  "task",
				  null,
				  new Object[] { new AccessInfo(new QName(exturi, "taskclass"), "clazz") },
				  null,
				  new QName[] { new QName(semuri, "incoming"), new QName(semuri, "outgoing") });
		types.add(type);
		
		type = createTypeInfo(ClassInfo.class,
							  null, exturi, "taskclass", null,
							  null,"typeName", null, null);
		types.add(type);
		
		type = createTypeInfo(MParameter.class,
				new IPostProcessor()
				{
					public Object postProcess(IContext context, Object object)
					{
						MParameter param = (MParameter) object;
						param.getInitialValue().setName(param.getName());
						param.getInitialValue().getClazz().setTypeName(param.getClazz().getTypeName());
						return object;
					}
					
					public int getPass()
					{
						return 0;
					}
				},
				exturi, "parameter",
				new Object[] { new AttributeInfo(new AccessInfo("type", "clazz"), CLASS_INFO_CONVERTER) },
				null,
				new AttributeInfo(new AccessInfo((String) null, "initialValue"), EXP_CONVERTER),
				null, null);
		types.add(type);
		
//		TypeInfo info = new TypeInfo(new XMLInfo(new QName(semuri, "flowNodeRef")), new ObjectInfo(String.class, new IPostProcessor()
//		{
//			public Object postProcess(IContext context, Object object)
//			{
//				System.out.println("Got" + String.valueOf(object));
//				return null;
//			}
//			
//			public int getPass()
//			{
//				return 0;
//			}
//		}));
		//types.add(info);
		
		return types;
	}
	
//	public static void main(String[] args) throws Exception
//	{
//		
//		BpmnModelReader mr = new BpmnModelReader();
//		mr.read(new File("/home/jander/readersample.bpmn2"));
//		
//	}
	
	protected static final TypeInfo createTypeInfo(Class<?> clazz,
			   String uri, String localpart,
			   Object[] attributemappings,
			   Object[] soaccessmappings,
			   QName[] ignoreattrs,
			   QName[] ignoreso)
	{
		return createTypeInfo(clazz, null, uri, localpart, attributemappings, soaccessmappings, ignoreattrs, ignoreso);
	}
	
	protected static final TypeInfo createTypeInfo(Class<?> clazz,
			   IPostProcessor postproc,
			   String uri, String localpart,
			   Object[] attributemappings,
			   Object[] soaccessmappings,
			   QName[] ignoreattrs,
			   QName[] ignoreso)
	{
		return createTypeInfo(clazz, postproc, uri, localpart, attributemappings, soaccessmappings, null, ignoreattrs, ignoreso);
	}
	
	protected static final TypeInfo createTypeInfo(Class<?> clazz,
												   IPostProcessor postproc,
												   String uri, String localpart,
												   Object[] attributemappings,
												   Object[] soaccessmappings,
												   Object contentinfo,
												   QName[] ignoreattrs,
												   QName[] ignoreso)
	{
		XMLInfo xi = new XMLInfo(new QName(uri, localpart));
		
		List<AttributeInfo> ailist = new ArrayList<AttributeInfo>();
		for (int i = 0; attributemappings != null && i < attributemappings.length; ++i)
		{
			if (attributemappings[i] instanceof AccessInfo)
			{
				ailist.add(new AttributeInfo((AccessInfo) attributemappings[i]));
			}
			else if (attributemappings[i] instanceof AttributeInfo)
			{
				ailist.add((AttributeInfo) attributemappings[i]);
			}
		}
		for (int i = 0; ignoreattrs != null && i < ignoreattrs.length; ++i)
		{
			ailist.add(new AttributeInfo(new AccessInfo(ignoreattrs[i], null, AccessInfo.IGNORE_READ)));
		}
		AttributeInfo[] ais = ailist.toArray(new AttributeInfo[ailist.size()]);
		
		List<SubobjectInfo> solist = new ArrayList<SubobjectInfo>();
		for (int i = 0; soaccessmappings != null && i < soaccessmappings.length; ++i)
		{
			if (soaccessmappings[i] instanceof AccessInfo)
			{
				solist.add(new SubobjectInfo((AccessInfo) soaccessmappings[i]));
			}
			else if (soaccessmappings[i] instanceof SubobjectInfo)
			{
				solist.add((SubobjectInfo) soaccessmappings[i]);
			}
		}
		for (int i = 0; ignoreso != null && i < ignoreso.length; ++i)
		{
			solist.add(new SubobjectInfo(new AccessInfo(ignoreso[i], null, AccessInfo.IGNORE_READ)));
		}
		SubobjectInfo[] soinfos = solist.toArray(new SubobjectInfo[solist.size()]);
		
		if (contentinfo instanceof String)
		{
			contentinfo = new AttributeInfo(new AccessInfo((String) null, contentinfo));
		}
		
		MappingInfo mi = new MappingInfo(null, null, contentinfo, ais, soinfos);
		
		ObjectInfo oi = postproc == null? new ObjectInfo(clazz) : new ObjectInfo(clazz, postproc);
		
		TypeInfo ret = new TypeInfo(xi, oi, mi);
		return ret;
	}
	
	/**
	 *  Post processor which handles activities.
	 *
	 */
	protected static class ActivityProcessor extends ElementMapper
	{
		/**
		 *  Processes the element.
		 */
		public Object postProcess(IContext context, Object object)
		{
			object = super.postProcess(context, object);
			BpmnReadContext rc = (BpmnReadContext) context.getUserContext();
			MActivity act = (MActivity) object;
			
			MLane lane = rc.getLaneMap().get(act.getId());
			if (lane != null)
			{
				lane.addActivity(act);
				object = IPostProcessor.DISCARD_OBJECT;
			}
			else
			{
				System.out.println("Not found: " + act.getId());
			}
			
			return object;
		}
	}
	
	/**
	 *  Post processor which puts all elements in the element map.
	 *
	 */
	protected static class ElementMapper implements IPostProcessor
	{
		/**
		 *  Processes the element.
		 */
		public Object postProcess(IContext context, Object object)
		{
			System.out.println("EMapper got: " + String.valueOf(object));
			BpmnReadContext rc = (BpmnReadContext) context.getUserContext();
			if (object instanceof MIdElement)
			{
				MIdElement element = (MIdElement) object;
				rc.getElementMap().put(element.getId(), element);
			}
			
			return object;
		}

		/**
		 *  Pass.
		 */
		public int getPass()
		{
			return 0;
		}
	}
	
	/**
	 *  Converts string to class infos.
	 *
	 */
	protected static class ClassInfoConverter implements IAttributeConverter
	{
		public Object convertString(String val, Object context)
				throws Exception
		{
			return new ClassInfo(val);
		}

		public String convertObject(Object val, Object context)
		{
			return ((ClassInfo) val).getTypeName();
		}
	}
	
	/**
	 *  Converts string to expressions.
	 *
	 */
	protected static class ExpressionConverter implements IAttributeConverter
	{
		public Object convertString(String val, Object context)
				throws Exception
		{
			return new UnparsedExpression(null, "java.lang.Object", val, null);
		}

		public String convertObject(Object val, Object context)
		{
			return ((UnparsedExpression) val).getValue();
		}
	}
	
	/**
	 * Concatenates two arrays.
	 */
	protected static <T> T[] concatArrays(Class<T> clazz, T[] array0, T[] array1)
	{
		T[] ret = (T[]) Array.newInstance(clazz, array0.length + array1.length);
		System.arraycopy(array0, 0, ret, 0, array0.length);
		System.arraycopy(array1, 0, ret, array0.length, array1.length);
		return ret;
	}
	
	protected class BpmnReadContext
	{
		/** The element map. */
		protected Map<String, MIdElement> elementmap = new HashMap<String, MIdElement>();
		
		/** List of flow references. */
		protected List<String> flowrefs = new ArrayList<String>();
		
		/** Sub-element ID to lanes map. */
		protected Map<String, MLane> lanemap = new HashMap<String, MLane>();
		
		/**
		 *  Returns the element map.
		 *  
		 *  @return The element map.
		 */
		public Map<String, MIdElement> getElementMap()
		{
			return elementmap;
		}
		
		/**
		 *  Gets the current flow references.
		 *  
		 *  @return The current flow references.
		 */
		public List<String> getFlowRefs()
		{
			return flowrefs;
		}
		
		/**
		 *  Returns the lane map.
		 *  
		 *  @return The lane map.
		 */
		public Map<String, MLane> getLaneMap()
		{
			return lanemap;
		}
		
		
	}
}
