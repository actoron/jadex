package jadex.adapter.base.appdescriptor;

import jadex.commons.SUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *  Application type representation.
 */
public class MApplicationType
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The package. */
	protected String packagename;
	
	/** The imports. */
	protected List imports;
	
	/** The list of contained space types. */
	protected List spacetypes;
	
	/** The list of contained agent types. */
	protected List agenttypes;
	
	/** The list of contained application descriptions. */
	protected List applications;
	
	/** The description. */
	protected String description;
	
	//-------- constructors --------
	
	/**
	 *  Create a new application type.
	 */
	public MApplicationType()
	{
		this.imports = new ArrayList();
		this.spacetypes = new ArrayList();
		this.agenttypes = new ArrayList();
		this.applications = new ArrayList();
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
	 * @return the packagename
	 */
	public String getPackage()
	{
		return this.packagename;
	}

	/**
	 * @param packagename the packagename to set
	 */
	public void setPackage(String packagename)
	{
		this.packagename = packagename;
	}
	
	/**
	 *  Get the model description.
	 *  @return The model description.
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 *  Set the description.
	 *  @param desc The description.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 *  Add an import.
	 *  @param import The import.
	 */
	public void addImport(String importstr)
	{
		this.imports.add(importstr);
	}
	
	/**
	 *  Add an agent type.
	 *  @param agenttype The agent type.
	 */
	public void addMAgentType(MAgentType agenttype)
	{
		this.agenttypes.add(agenttype);
	}
	
	/**
	 *  Add a space type.
	 *  @param agenttype The space type.
	 */
	public void addMSpaceType(MSpaceType spacetype)
	{
		this.spacetypes.add(spacetype);
	}

	/**
	 *  Add an application.
	 *  @param application The application.
	 */
	public void addMApplicationInstance(MApplicationInstance application)
	{
		this.applications.add(application);
	}

	/**
	 *  Get the imports.
	 *  @return The imports.
	 */
	public List getImports()
	{
		return this.imports;
	}
	
	/**
	 *  Get the agenttypes.
	 *  @return The agenttypes.
	 */
	public List getMAgentTypes()
	{
		return this.agenttypes;
	}
	
	/**
	 *  Get the space types.
	 *  @return The spacetypes.
	 */
	public List getMSpaceTypes()
	{
		return this.spacetypes;
	}

	/**
	 *  Get a named agenttype.
	 *  @param name The agent type name.
	 *  @return The agenttype (if any).
	 */
	public MAgentType getMAgentType(String name)
	{
		MAgentType	ret	= null;
		for(int i=0; ret==null && i<agenttypes.size(); i++)
		{
			MAgentType	at	= (MAgentType)agenttypes.get(i);
			if(at.getName().equals(name))
				ret	= at;
		}
		return ret;
	}

	/**
	 *  Get a named spacetype.
	 *  @param name The space type name.
	 *  @return The spacetype (if any).
	 */
	public MSpaceType getMSpaceType(String name)
	{
		MSpaceType ret	= null;
		for(int i=0; ret==null && i<spacetypes.size(); i++)
		{
			MSpaceType st = (MSpaceType)spacetypes.get(i);
			if(st.getName().equals(name))
				ret	= st;
		}
		return ret;
	}
	
	/**
	 *  Get the applications.
	 *  @return The applications.
	 */
	public List getMApplicationInstances()
	{
		return this.applications;
	}
	
	/**
	 *  Get complete imports (including own package).
	 *  @return An array of imports;
	 */
	public String[] getAllImports()
	{
		String[] ret = SUtil.EMPTY_STRING;
		
		if(imports!=null)
		{
			if(packagename!=null)
			{
				ret = new String[imports.size()+1];
				ret[imports.size()] = packagename+".*";
			}
			else
			{
				ret = new String[imports.size()];
			}
			for(int i=0; i<imports.size(); i++)
				ret[i] = (String)imports.get(i);
		}
		else if(packagename!=null)
		{
			ret = new String[]{packagename+".*"};
		}
		
		return ret;
	}
}
