package jadex.adapter.base.appdescriptor;

import jadex.bridge.IPlatform;
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
	protected int number;
	
	/** The master flag. */
	protected boolean master;
	
	/** The list of contained parameters. */
	protected List parameters;
	
	/** The argument parser. */
	protected JavaCCExpressionParser parser;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent.
	 */
	public MAgentInstance()
	{
		this.parameters = new ArrayList();
		this.start = true;
		this.number = 1;
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
	 *  Get the number of agents to start.
	 *  @return The number.
	 */
	public int getNumber()
	{
		return this.number;
	}

	/**
	 *  Set the number of agents.
	 *  @param number The number to set.
	 */
	public void setNumber(int number)
	{
		this.number = number;
	}

	/**
	 *  Add an argument.
	 *  @param arg The argument.
	 */
	public void addMArgument(MArgument arg)
	{
		this.parameters.add(arg);
	}
	
	/**
	 *  Add an argument set.
	 */
	public void addMArgumentSet(MArgumentSet argset)
	{
		this.parameters.add(argset);
	}

	/**
	 *  Get the list of paparameters and parameter sets.
	 *  @return The parameters and parameter sets.
	 */
	public List getMArguments()
	{
		return this.parameters;
	}
	
	/**
	 *  Get the arguments.
	 *  @return The arguments as a map of name-value pairs.
	 */
	public Map getArguments(IPlatform platform, MApplicationType apptype, ClassLoader classloader)
	{
		Map ret = null;

		if(parameters!=null)
		{
			ret = new HashMap();
			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			fetcher.setValue("$platform", platform);

			String[] imports = (String[])apptype.getImports().toArray(new String[apptype.getImports().size()]);
			for(int i=0; i<parameters.size(); i++)
			{
				Object tmp = parameters.get(i);
				if(tmp instanceof MArgument)
				{
					MArgument p = (MArgument)tmp;
					String valtext = p.getValue();
					
					if(parser==null)
						parser = new JavaCCExpressionParser();
					
					Object val = parser.parseExpression(valtext, imports, null, classloader).getValue(fetcher);
					ret.put(p.getName(), val);
				}
				else //if(tmp instanceof ParameterSet)
				{
					MArgumentSet ps = (MArgumentSet)tmp;
					List vals = new ArrayList();
					List textvals = ps.getValues();
					if(textvals!=null)
					{
						if(parser==null)
							parser = new JavaCCExpressionParser();
						for(int j=0; j<textvals.size(); j++)
						{
							Object val = parser.parseExpression((String)textvals.get(j), imports, null, classloader).getValue(fetcher);
							vals.add(val);
						}
					}
					ret.put(ps.getName(), vals);
				}
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
