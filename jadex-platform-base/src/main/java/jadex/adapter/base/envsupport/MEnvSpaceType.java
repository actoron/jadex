package jadex.adapter.base.envsupport;

import jadex.adapter.base.appdescriptor.MApplicationType;
import jadex.adapter.base.appdescriptor.MSpaceType;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.MultiCollection;
import jadex.commons.xml.BasicTypeConverter;
import jadex.commons.xml.BeanAttributeInfo;
import jadex.commons.xml.IPostProcessor;
import jadex.commons.xml.ITypeConverter;
import jadex.commons.xml.LinkInfo;
import jadex.commons.xml.TypeInfo;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.StyleSheet;

/**
 *  Java representation of environemnt space type for xml description.
 */
public class MEnvSpaceType	extends MSpaceType
{
	//-------- attributes --------
	
	/** The properties. */
	protected Map properties;
	
	//-------- methods --------
	
	/**
	 *  Add a property.
	 *  @param key The key.
	 *  @param value The value.
	 */
	public void addProperty(String key, Object value)
	{
		if(properties==null)
			properties = new MultiCollection();
		properties.put(key, value);
	}
	
	/**
	 *  Get a property.
	 *  @param key The key.
	 *  @return The value.
	 */
	public List getPropertyList(String key)
	{
		return properties!=null? (List)properties.get(key):  null;
	}
	
	/**
	 *  Get the properties.
	 *  @return The properties.
	 */
	public Map getProperties()
	{
		return properties;
	}
	
//	/** The dimensions. */
//	protected List dimensions;
//	
//	/** The space action types. */
//	protected List spaceactiontypes;
//	
//	/** The action types. */
//	protected List agentactiontypes;
//	
//	/** The process types. */
//	protected List processtypes;
//	
//	/** The percept generator types. */
//	protected List perceptgeneratortypes;
//	
//	/** The views. */
//	protected List views;
//	
//	/** The implementation class. */
//	protected Class clazz;
//	
//	/** The space executor expression. */
//	protected IParsedExpression spaceexecutor;
//	
//	//-------- methods --------
//		
//	/**
//	 *  Add a dimension.
//	 */
//	public void addDimension(Double d)
//	{
//		if(dimensions==null)
//			dimensions = new ArrayList();
//		dimensions.add(d);	
//	}
//	
//	/**
//	 *  Get the dimensions.
//	 *  @return The dimensions.
//	 */
//	public List getDimensions()
//	{
//		return dimensions;
//	}
//	
//	/**
//	 *  Add a agent action type.
//	 *  @param action The action.
//	 */
//	public void addMEnvAgentActionType(MEnvAgentActionType action)
//	{
//		if(agentactiontypes==null)
//			agentactiontypes = new ArrayList();
//		agentactiontypes.add(action);	
//	}
//	
//	/**
//	 *  Get the agent action types.
//	 *  @return The agent action types.
//	 */
//	public List getMEnvAgentActionTypes()
//	{
//		return agentactiontypes;
//	}
//	
//	/**
//	 *  Add a space action type.
//	 *  @param action The action.
//	 */
//	public void addMEnvSpaceActionType(MEnvAgentActionType action)
//	{
//		if(spaceactiontypes==null)
//			spaceactiontypes = new ArrayList();
//		spaceactiontypes.add(action);	
//	}
//	
//	/**
//	 *  Get the action types.
//	 *  @return The action types.
//	 */
//	public List getMEnvSpaceActionTypes()
//	{
//		return spaceactiontypes;
//	}
//	
//	/**
//	 *  Add a process type.
//	 *  @param process The process.
//	 */
//	public void addMEnvProcessType(MEnvProcessType process)
//	{
//		if(processtypes==null)
//			processtypes = new ArrayList();
//		processtypes.add(process);	
//	}
//	
//	/**
//	 *  Get the process types.
//	 *  @return The process types.
//	 */
//	public List getMEnvProcessTypes()
//	{
//		return processtypes;
//	}
//	
//	/**
//	 *  Add a percept generator type.
//	 *  @param perceptgen The percept generator.
//	 */
//	public void addMEnvPerceptGeneratorType(MEnvPerceptGeneratorType perceptgen)
//	{
//		if(perceptgeneratortypes==null)
//			perceptgeneratortypes = new ArrayList();
//		perceptgeneratortypes.add(perceptgen);	
//	}
//	
//	/**
//	 *  Get the percept generator types.
//	 *  @return The percept generator types.
//	 */
//	public List getMEnvPerceptGeneratorTypes()
//	{
//		return perceptgeneratortypes;
//	}
//	
//	/**
//	 *  Add a view.
//	 *  @param view The view.
//	 */
//	public void addMEnvView(MEnvView view)
//	{
//		if(views==null)
//			views = new ArrayList();
//		views.add(view);	
//	}
//	
//	/**
//	 *  Get the view.
//	 *  @return The view.
//	 */
//	public List getMEnvViews()
//	{
//		return views;
//	}
//	
//	/**
//	 *  Get the clazz.
//	 *  @return The clazz.
//	 */
//	public Class getClazz()
//	{
//		return this.clazz;
//	}
//
//	/**
//	 *  Set the class name.
//	 *  @param name The class name to set.
//	 */
//	public void setClazz(Class clazz)
//	{
//		this.clazz = clazz;
//	}
//	
//	/**
//	 *  Set the space executor.
//	 */
//	public void setSpaceExecutor(IParsedExpression spaceexecutor)
//	{
//		this.spaceexecutor = spaceexecutor;
//	}
//	
//	/**
//	 * @return the spaceexecutor
//	 */
//	public IParsedExpression getSpaceExecutor()
//	{
//		return this.spaceexecutor;
//	}
//
//	/**
//	 *  Get a string representation of this AGR space type.
//	 *  @return A string representation of this AGR space type.
//	 */
//	public String	toString()
//	{
//		StringBuffer	sbuf	= new StringBuffer();
//		sbuf.append(SReflect.getInnerClassName(getClass()));
//		sbuf.append("(name=");
//		sbuf.append(getName());
//		sbuf.append(", dimensions=");
//		sbuf.append(getDimensions());
//		sbuf.append(", agent action types=");
//		sbuf.append(getMEnvAgentActionTypes());
//		sbuf.append(", space action types=");
//		sbuf.append(getMEnvSpaceActionTypes());
//		sbuf.append(", class=");
//		sbuf.append(getClazz());
//		sbuf.append(")");
//		return sbuf.toString();
//	}
	
	//-------- static part --------
	
	/**
	 *  Get the XML mapping.
	 * /
	public static Set getXMLMapping()
	{
		Set types = new HashSet();
		ExpressionProcessor exproc = new ExpressionProcessor();
		
		ITypeConverter typeconv = new ClassConverter();
		ITypeConverter colorconv = new ColorConverter();
		
		types.add(new TypeInfo("envspacetype", MEnvSpaceType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("clazz", typeconv, null)}), null));
		
		types.add(new TypeInfo("envspace", MEnvSpaceInstance.class, null, null,
			SUtil.createHashMap(new String[]{"type"},
			new BeanAttributeInfo[]{new BeanAttributeInfo("typeName")}), null));

		types.add(new TypeInfo("agentactiontype", MEnvAgentActionType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("clazz", typeconv, null)}), null));
		
		types.add(new TypeInfo("spaceactiontype", MEnvSpaceActionType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("clazz", typeconv, null)}), null));
		
		types.add(new TypeInfo("processtype", MEnvProcessType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("clazz", typeconv, null)}), null));
		
		types.add(new TypeInfo("perceptgeneratortype", MEnvPerceptGeneratorType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("clazz", typeconv, null)}), null));

		types.add(new TypeInfo("view", MEnvView.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("clazz", typeconv, null)}), null));

		types.add(new TypeInfo("theme", MEnvTheme.class));

		types.add(new TypeInfo("drawable", MEnvDrawable.class, null, null,
			SUtil.createHashMap(new String[]{"objecttype"}, new String[]{"objectType"}), null));

		types.add(new TypeInfo("texturedrectangle", MEnvTexturedRectangle.class, null, null,
			SUtil.createHashMap(new String[]{"imagepath"}, new String[]{"imagePath"}), null));

		types.add(new TypeInfo("gridlayer", MEnvGridLayer.class, null, null,
			SUtil.createHashMap(new String[]{"color"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo(null, colorconv, null)}), null));
		
		types.add(new TypeInfo("tiledlayer", MEnvTiledLayer.class, null, null, 
			SUtil.createHashMap(new String[]{"imagepath"}, new String[]{"imagePath"}), null));
		
		types.add(new TypeInfo("spaceexecutor", String.class, null, null, null, exproc));

		types.add(new TypeInfo("object", MEnvObject.class));
		return types;
	}*/
	
	/**
	 *  Get the XML link infos.
	 * /
	public static Set getXMLLinkInfos()
	{
		Set linkinfos = new HashSet();
		
		linkinfos.add(new LinkInfo("spaceexecutor", "spaceExecutor"));
		linkinfos.add(new LinkInfo("texturedrectangle", "part"));
		linkinfos.add(new LinkInfo("prelayers/gridlayer", "preLayer"));
		linkinfos.add(new LinkInfo("postlayers/gridlayer", "postLayer"));
		linkinfos.add(new LinkInfo("prelayers/tiledlayer", "preLayer"));
		linkinfos.add(new LinkInfo("postlayers/tiledlayer", "postLayer"));
		
		return linkinfos;
	}*/
	
	/**
	 *  Get the XML mapping.
	 */
	public static Set getXMLMapping()
	{
		Set types = new HashSet();
		
		ITypeConverter typeconv = new ClassConverter();
		ITypeConverter colorconv = new ColorConverter();
		
		types.add(new TypeInfo("envspacetype", MEnvSpaceType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("clazz", typeconv, "property")}), null));
		
		types.add(new TypeInfo("envspace", MEnvSpaceInstance.class, null, null,
			SUtil.createHashMap(new String[]{"type"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("typeName")}), null));
		
		types.add(new TypeInfo("agentactiontype", MultiCollection.class, null, null,
			SUtil.createHashMap(new String[]{"class", "name"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("clazz", typeconv, ""),
			new BeanAttributeInfo(null, null, "")}), null));
		
		types.add(new TypeInfo("spaceactiontype", MultiCollection.class, null, null,
			SUtil.createHashMap(new String[]{"class", "name"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("clazz", typeconv, ""),
			new BeanAttributeInfo(null, null, "")}), null));
		
		types.add(new TypeInfo("processtype", MultiCollection.class, null, null,
			SUtil.createHashMap(new String[]{"class", "name"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("clazz", typeconv, ""),
			new BeanAttributeInfo(null, null, "")}), null));
		
		types.add(new TypeInfo("perceptgeneratortype", MultiCollection.class, null, null,
			SUtil.createHashMap(new String[]{"class", "name"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("clazz", typeconv, ""),
			new BeanAttributeInfo(null, null, "")}), null));
		
		types.add(new TypeInfo("view", MultiCollection.class, null, null,
			SUtil.createHashMap(new String[]{"class", "name"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("clazz", typeconv, ""),
			new BeanAttributeInfo(null, null, "")}), null));
		
		types.add(new TypeInfo("theme", MultiCollection.class, null, null,
			SUtil.createHashMap(new String[]{"name"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo(null, null, "")}), null));
		
		types.add(new TypeInfo("drawable", MultiCollection.class, null, null,
			SUtil.createHashMap(new String[]{"objecttype", "width", "height"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo(null, BasicTypeConverter.DOUBLE_CONVERTER, "")}), null));

		types.add(new TypeInfo("texturedrectangle", MultiCollection.class, null, null,
			SUtil.createHashMap(new String[]{"imagepath", "width", "height", "shiftx", "shifty", "rotating"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo(null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo(null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo(null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo(null, BasicTypeConverter.BOOLEAN_CONVERTER, "")
			}), null));
		
		types.add(new TypeInfo("gridlayer", MultiCollection.class, null, null,
			SUtil.createHashMap(new String[]{"color", "width", "height"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo(null, colorconv, ""),
			new BeanAttributeInfo(null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo(null, BasicTypeConverter.DOUBLE_CONVERTER, "")
			}), null));

		types.add(new TypeInfo("tiledlayer", MultiCollection.class, null, null,
			SUtil.createHashMap(new String[]{"imagepath", "width", "height"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, BasicTypeConverter.DOUBLE_CONVERTER, ""),
			new BeanAttributeInfo(null, BasicTypeConverter.DOUBLE_CONVERTER, "")
			}), null));
		
			
//		types.add(new TypeInfo("spaceexecutor", String.class, null, null, null, exproc));

//		types.add(new TypeInfo("object", MEnvObject.class));
		
		types.add(new TypeInfo("object", MultiCollection.class, null, null,
			SUtil.createHashMap(new String[]{"name", "type", "owner"}, 
			new BeanAttributeInfo[]{new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, ""),
			new BeanAttributeInfo(null, null, "")
			}), null));
		
		return types;
	}
	
	/**
	 *  Get the XML link infos.
	 */
	public static Set getXMLLinkInfos()
	{
		Set linkinfos = new HashSet();

		ITypeConverter expconv = new ExpressionConverter();
		
		// applicationtype
		linkinfos.add(new LinkInfo("envspacetype", new BeanAttributeInfo("MSpaceType")));
		
		// application
		linkinfos.add(new LinkInfo("envspace", new BeanAttributeInfo("MSpaceInstance")));
	
		// spacetype
		linkinfos.add(new LinkInfo("dimension", new BeanAttributeInfo("dimensions", BasicTypeConverter.DOUBLE_CONVERTER, "property")));
		linkinfos.add(new LinkInfo("agentactiontype", new BeanAttributeInfo("agentactiontypes", null, "property")));
		linkinfos.add(new LinkInfo("spaceactiontype", new BeanAttributeInfo("spaceactiontypes", null, "property")));
		linkinfos.add(new LinkInfo("processtype", new BeanAttributeInfo("processtypes", null, "property")));
		linkinfos.add(new LinkInfo("perceptgeneratortype", new BeanAttributeInfo("perceptgeneratortypes", null, "property")));
		linkinfos.add(new LinkInfo("view", new BeanAttributeInfo("views", null, "property")));
		linkinfos.add(new LinkInfo("spaceexecutor", new BeanAttributeInfo(null, expconv, "property")));
		
		// view
		linkinfos.add(new LinkInfo("theme", new BeanAttributeInfo("themes", null, "")));
		
		// theme
		linkinfos.add(new LinkInfo("drawable", new BeanAttributeInfo("drawables", null, "")));
		linkinfos.add(new LinkInfo("prelayers/gridlayer", new BeanAttributeInfo("prelayers", null, "")));
		linkinfos.add(new LinkInfo("prelayers/tiledlayer", new BeanAttributeInfo("prelayers", null, "")));
		linkinfos.add(new LinkInfo("postlayers/gridlayer", new BeanAttributeInfo("postlayers", null, "")));
		linkinfos.add(new LinkInfo("postlayers/tiledlayer", new BeanAttributeInfo("postlayers", null, "")));
		
		// drawable
		linkinfos.add(new LinkInfo("texturedrectangle", new BeanAttributeInfo("parts", null, "")));		
		
		// space instance
		linkinfos.add(new LinkInfo("object", new BeanAttributeInfo("objects", null, "property")));
		
		return linkinfos;
	}
	
	/**
	 *  Parse expression text.
	 * /
	static class ExpressionProcessor	implements IPostProcessor
	{
		// Hack!!! Should be configurable.
		protected static IExpressionParser	exp_parser	= new JavaCCExpressionParser();

		/**
		 *  Parse expression text.
		 * /
		public Object postProcess(Object context, Object object, Object root, ClassLoader classloader)
		{
			Object ret = null;
			
			System.out.println("Found expression: "+object);
			try
			{
				ret = exp_parser.parseExpression((String)object, ((MApplicationType)root).getAllImports(), null, classloader);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return ret;
		}
	}*/
	
	/**
	 *  Parse class names.
	 */
	static class ExpressionConverter implements ITypeConverter
	{
		// Hack!!! Should be configurable.
		protected static IExpressionParser	exp_parser	= new JavaCCExpressionParser();

		/**
		 *  Convert a string value to a type.
		 *  @param val The string value to convert.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader)
		{
			if(!(val instanceof String))
				throw new RuntimeException("Source value must be string: "+val);
			
			Object ret = null;
			
			System.out.println("Found expression: "+val);
			try
			{
				ret = exp_parser.parseExpression((String)val, ((MApplicationType)root).getAllImports(), null, classloader);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return ret;
		}
	}
	
	/**
	 *  Parse class names.
	 */
	static class ClassConverter	implements ITypeConverter
	{
		/**
		 *  Convert a string value to a type.
		 *  @param val The string value to convert.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader)
		{
			if(!(val instanceof String))
				throw new RuntimeException("Source value must be string: "+val);
			Class ret = SReflect.findClass0((String)val, ((MApplicationType)root).getAllImports(), classloader);
			if(ret==null)
				throw new RuntimeException("Could not parse class: "+val);
			return ret;
		}
	}
	
	/**
	 *  Parse class names.
	 */
	static class ColorConverter	implements ITypeConverter
	{
		protected StyleSheet ss = new StyleSheet();
		
		/**
		 *  Convert a string value to a type.
		 *  @param val The string value to convert.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader)
		{
			if(!(val instanceof String))
				throw new RuntimeException("Source value must be string: "+val);
			// Cannot use CSS.stringToColor() because they haven't made it public :-(
			return ss.stringToColor((String)val);
		}
	}
}
