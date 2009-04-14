package jadex.adapter.base.envsupport;

import jadex.adapter.base.appdescriptor.MApplicationType;
import jadex.adapter.base.appdescriptor.MSpaceType;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.xml.IPostProcessor;
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
	
	/** The implementation class name. */
	protected String classname;
	
	/** The space executor expression. */
	protected String spaceexecutorstring;
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
	 *  Get the class name.
	 *  @return The class name.
	 */
	public String getClassName()
	{
		return this.classname;
	}

	/**
	 *  Set the class name.
	 *  @param classname The class name to set.
	 */
	public void setClassName(String classname)
	{
		this.classname = classname;
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
	 * @return the spaceexecutorstring
	 */
	public String getSpaceexEcutorString()
	{
		return this.spaceexecutorstring;
	}

	/**
	 * @param spaceexecutorstring the spaceexecutorstring to set
	 */
	public void setSpaceExecutorString(String spaceexecutorstring)
	{
		this.spaceexecutorstring = spaceexecutorstring;
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
		sbuf.append(getClassName());
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
		types.add(new TypeInfo("envspacetype", MEnvSpaceType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, new String[]{"setClassName"}), null));
		types.add(new TypeInfo("envspace", MEnvSpaceInstance.class, null, null,
			SUtil.createHashMap(new String[]{"type"}, new String[]{"setTypeName"}), null));
		types.add(new TypeInfo("agentactiontype", MEnvAgentActionType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, new String[]{"setClassName"}), null));
		types.add(new TypeInfo("spaceactiontype", MEnvSpaceActionType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, new String[]{"setClassName"}), null));
		types.add(new TypeInfo("processtype", MEnvProcessType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, new String[]{"setClassName"}), null));
		types.add(new TypeInfo("perceptgeneratortype", MEnvPerceptGeneratorType.class, null, null,
			SUtil.createHashMap(new String[]{"class"}, new String[]{"setClassName"}), null));
		
		types.add(new TypeInfo("spaceexecutor", String.class, null, null, null, exproc));

		types.add(new TypeInfo("object", MEnvObject.class));
		return types;
	}
	
	/**
	 *  Get the XML link infos.
	 */
	public static Set getXMLLinkInfos()
	{
		Set linkinfos = new HashSet();
		
		linkinfos.add(new LinkInfo("spaceexecutor", "setSpaceExecutor"));
		
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
		public Object postProcess(Object context, Object object, Object root)
		{
			Object ret = null;
			
			System.out.println("Found expression: "+object);
			try
			{
				ret = exp_parser.parseExpression((String)object, ((MApplicationType)root).getAllImports(), null, null); // todo: classloader???
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return ret;
		}
	}
}
