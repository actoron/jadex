package jadex.adapter.base.envsupport;

import jadex.adapter.base.appdescriptor.MApplicationType;
import jadex.adapter.base.appdescriptor.MSpaceType;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.xml.IPostProcessor;
import jadex.commons.xml.ITypeConverter;
import jadex.commons.xml.LinkInfo;
import jadex.commons.xml.TypeInfo;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  Java representation of environemnt space type for xml description.
 */
public class MEnvSpaceType	extends MSpaceType
{
	//-------- attributes --------
	
	/** The dimensions. */
	protected List dimensions;
	
	/** The space action types. */
	protected List spaceactiontypes;
	
	/** The action types. */
	protected List agentactiontypes;
	
	/** The process types. */
	protected List processtypes;
	
	/** The percept generator types. */
	protected List perceptgeneratortypes;
	
	/** The views. */
	protected List views;
	
	/** The implementation class. */
	protected Class clazz;
	
	/** The space executor expression. */
	protected IParsedExpression spaceexecutor;
	
	//-------- methods --------
		
	/**
	 *  Add a dimension.
	 */
	public void addDimension(Double d)
	{
		if(dimensions==null)
			dimensions = new ArrayList();
		dimensions.add(d);	
	}
	
	/**
	 *  Get the dimensions.
	 *  @return The dimensions.
	 */
	public List getDimensions()
	{
		return dimensions;
	}
	
	/**
	 *  Add a agent action type.
	 *  @param action The action.
	 */
	public void addMEnvAgentActionType(MEnvAgentActionType action)
	{
		if(agentactiontypes==null)
			agentactiontypes = new ArrayList();
		agentactiontypes.add(action);	
	}
	
	/**
	 *  Get the agent action types.
	 *  @return The agent action types.
	 */
	public List getMEnvAgentActionTypes()
	{
		return agentactiontypes;
	}
	
	/**
	 *  Add a space action type.
	 *  @param action The action.
	 */
	public void addMEnvSpaceActionType(MEnvAgentActionType action)
	{
		if(spaceactiontypes==null)
			spaceactiontypes = new ArrayList();
		spaceactiontypes.add(action);	
	}
	
	/**
	 *  Get the action types.
	 *  @return The action types.
	 */
	public List getMEnvSpaceActionTypes()
	{
		return spaceactiontypes;
	}
	
	/**
	 *  Add a process type.
	 *  @param process The process.
	 */
	public void addMEnvProcessType(MEnvProcessType process)
	{
		if(processtypes==null)
			processtypes = new ArrayList();
		processtypes.add(process);	
	}
	
	/**
	 *  Get the process types.
	 *  @return The process types.
	 */
	public List getMEnvProcessTypes()
	{
		return processtypes;
	}
	
	/**
	 *  Add a percept generator type.
	 *  @param perceptgen The percept generator.
	 */
	public void addMEnvPerceptGeneratorType(MEnvPerceptGeneratorType perceptgen)
	{
		if(perceptgeneratortypes==null)
			perceptgeneratortypes = new ArrayList();
		perceptgeneratortypes.add(perceptgen);	
	}
	
	/**
	 *  Get the percept generator types.
	 *  @return The percept generator types.
	 */
	public List getMEnvPerceptGeneratorTypes()
	{
		return perceptgeneratortypes;
	}
	
	/**
	 *  Add a view.
	 *  @param view The view.
	 */
	public void addMEnvView(MEnvView view)
	{
		if(views==null)
			views = new ArrayList();
		views.add(view);	
	}
	
	/**
	 *  Get the view.
	 *  @return The view.
	 */
	public List getMEnvViews()
	{
		return views;
	}
	
	/**
	 *  Get the clazz.
	 *  @return The clazz.
	 */
	public Class getClazz()
	{
		return this.clazz;
	}

	/**
	 *  Set the class name.
	 *  @param name The class name to set.
	 */
	public void setClazz(Class clazz)
	{
		this.clazz = clazz;
	}
	
	/**
	 *  Set the space executor.
	 */
	public void setSpaceExecutor(IParsedExpression spaceexecutor)
	{
		this.spaceexecutor = spaceexecutor;
	}
	
	/**
	 * @return the spaceexecutor
	 */
	public IParsedExpression getSpaceExecutor()
	{
		return this.spaceexecutor;
	}

	/**
	 *  Get a string representation of this AGR space type.
	 *  @return A string representation of this AGR space type.
	 */
	public String	toString()
	{
		StringBuffer	sbuf	= new StringBuffer();
		sbuf.append(SReflect.getInnerClassName(getClass()));
		sbuf.append("(name=");
		sbuf.append(getName());
		sbuf.append(", dimensions=");
		sbuf.append(getDimensions());
		sbuf.append(", agent action types=");
		sbuf.append(getMEnvAgentActionTypes());
		sbuf.append(", space action types=");
		sbuf.append(getMEnvSpaceActionTypes());
		sbuf.append(", class=");
		sbuf.append(getClazz());
		sbuf.append(")");
		return sbuf.toString();
	}
	
	//-------- static part --------
	
	/**
	 *  Get the XML mapping.
	 */
	public static Set getXMLMapping()
	{
		Set types = new HashSet();
		ExpressionProcessor exproc = new ExpressionProcessor();
		
		ITypeConverter conv = new ClassConverter();
		
		types.add(new TypeInfo("envspacetype", MEnvSpaceType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, new String[]{"clazz"}),
			SUtil.createHashMap(new String[]{"class"}, new ITypeConverter[]{conv}), null));
		
		types.add(new TypeInfo("envspace", MEnvSpaceInstance.class, null, null,
			SUtil.createHashMap(new String[]{"type"}, new String[]{"typeName"}), null, null));

		types.add(new TypeInfo("agentactiontype", MEnvAgentActionType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, new String[]{"clazz"}),
			SUtil.createHashMap(new String[]{"class"}, new ITypeConverter[]{conv}), null));
		
		types.add(new TypeInfo("spaceactiontype", MEnvSpaceActionType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, new String[]{"clazz"}),
			SUtil.createHashMap(new String[]{"class"}, new ITypeConverter[]{conv}), null));
		
		types.add(new TypeInfo("processtype", MEnvProcessType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, new String[]{"clazz"}), 
			SUtil.createHashMap(new String[]{"class"}, new ITypeConverter[]{conv}), null));
		
		types.add(new TypeInfo("perceptgeneratortype", MEnvPerceptGeneratorType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, new String[]{"clazz"}),
			SUtil.createHashMap(new String[]{"class"}, new ITypeConverter[]{conv}), null));
		
		types.add(new TypeInfo("spaceexecutor", String.class, null, null, null, null, exproc));

		types.add(new TypeInfo("object", MEnvObject.class));
		return types;
	}
	
	/**
	 *  Get the XML link infos.
	 */
	public static Set getXMLLinkInfos()
	{
		Set linkinfos = new HashSet();
		
		linkinfos.add(new LinkInfo("spaceexecutor", "spaceExecutor"));
		
		return linkinfos;
	}
	
	/**
	 *  Parse expression text.
	 */
	static class ExpressionProcessor	implements IPostProcessor
	{
		// Hack!!! Should be configurable.
		protected static IExpressionParser	exp_parser	= new JavaCCExpressionParser();

		/**
		 *  Parse expression text.
		 */
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
		public Object convertObject(String val, Object root, ClassLoader classloader)
		{
			Class ret = SReflect.findClass0(val, ((MApplicationType)root).getAllImports(), classloader);
			if(ret==null)
				throw new RuntimeException("Could not parse class: "+val);
			return ret;
		}
	}
}
