package jadex.application.model;

import jadex.application.runtime.Application;
import jadex.javaparser.SimpleValueFetcher;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Agent instance representation. 
 */
public class MAgentInstance
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The type name. */
	protected String typename;
	
	/** The configuration. */
	protected String configuration;

	/** The start flag. */
	protected boolean start;
	
	/** The number of agents. */
//	protected int number;
	protected String numbertext;
	
	/** The master flag. */
	protected boolean master;
	
	/** The list of contained arguments. */
	protected List arguments;
	
	/** The argument parser. */
	protected JavaCCExpressionParser parser;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent.
	 */
	public MAgentInstance()
	{
		this.arguments = new ArrayList();
		this.start = true;
//		this.number = 1;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the type name.
	 *  @return The type name.
	 */
	public String getTypeName()
	{
		return this.typename;
	}

	/**
	 *  Set the type name.
	 *  @param type The type name to set.
	 */
	public void setTypeName(String typename)
	{
		this.typename = typename;
	}

	/**
	 *  Get the configuration.
	 *  @return The configuration.
	 */
	public String getConfiguration()
	{
		return this.configuration;
	}

	/**
	 *  Set the configuration.
	 *  @param configuration The configuration to set.
	 */
	public void setConfiguration(String configuration)
	{
		this.configuration = configuration;
	}
	
	/**
	 *  Test if agent should be started (not only created).
	 *  @return True, if should be started.
	 */
	public boolean isStart()
	{
		return this.start;
	}

	/**
	 *  Set if the agent should also be started.
	 *  @param start The start flag to set.
	 */
	public void setStart(boolean start)
	{
		this.start = start;
	}
	
	/**
	 *  Get the master flag.
	 *  @return True, if master.
	 */
	public boolean isMaster()
	{
		return this.master;
	}

	/**
	 *  Set the master flag..
	 *  @param start The master flag.
	 */
	public void setMaster(boolean master)
	{
		this.master = master;
	}
	
	/**
	 *  Set the number text.
	 *  @param numbertext The number text.
	 */
	public void setNumberText(String numbertext)
	{
		this.numbertext = numbertext;
	}
	
	/**
	 * 
	 */
	public String getNumberText()
	{
		return this.numbertext;
	}
	
	/**
	 *  Get the number of agents to start.
	 *  @return The number.
	 */
	public int getNumber(Application context, ClassLoader classloader)
	{
		SimpleValueFetcher fetcher = new SimpleValueFetcher();
		fetcher.setValue("$platform", context.getServiceContainer());
		fetcher.setValue("$args", context.getArguments());
		fetcher.setValue("$results", context.getResults());

		String[] imports = context.getApplicationType().getAllImports();
		if(parser==null)
			parser = new JavaCCExpressionParser();
			
		Object val = numbertext!=null? parser.parseExpression(numbertext, imports, null, classloader).getValue(fetcher): null;
		
		return val instanceof Integer? ((Integer)val).intValue(): 1;
	}
	
	/**
	 *  Get the number of agents to start.
	 *  @return The number.
	 * /
	public int getNumber()
	{
		return this.number;
	}*/

	/**
	 *  Set the number of agents.
	 *  @param number The number to set.
	 * /
	public void setNumber(int number)
	{
//		this.number = number;
	}*/

	/**
	 *  Add an argument.
	 *  @param arg The argument.
	 */
	public void addMArgument(MArgument arg)
	{
		this.arguments.add(arg);
	}

	/**
	 *  Get the list of arguments.
	 *  @return The arguments.
	 */
	public List getMArguments()
	{
		return this.arguments;
	}
	
	/**
	 *  Get the arguments.
	 *  @return The arguments as a map of name-value pairs.
	 */
	public Map getArguments(Application context, ClassLoader classloader)
	{
		Map ret = null;

		if(arguments!=null)
		{
			ret = new HashMap();

			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			fetcher.setValue("$platform", context.getServiceContainer());
			fetcher.setValue("$args", context.getArguments());
			fetcher.setValue("$results", context.getResults());

			String[] imports = context.getApplicationType().getAllImports();
			for(int i=0; i<arguments.size(); i++)
			{
				MArgument p = (MArgument)arguments.get(i);
				String valtext = p.getValue();
				
				if(parser==null)
					parser = new JavaCCExpressionParser();
				
				Object val = parser.parseExpression(valtext, imports, null, classloader).getValue(fetcher);
				ret.put(p.getName(), val);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the model of the agent instance.
	 *  @param apptype The application type this agent is used in.
	 *  @return The name of the agent type.
	 */
	public MAgentType getType(MApplicationType apptype)
	{
		MAgentType ret = null;
		List agenttypes = apptype.getMAgentTypes();
		for(int i=0; ret==null && i<agenttypes.size(); i++)
		{
			MAgentType at = (MAgentType)agenttypes.get(i);
			if(at.getName().equals(getTypeName()))
				ret = at;
		}
		return ret;
	}
}
