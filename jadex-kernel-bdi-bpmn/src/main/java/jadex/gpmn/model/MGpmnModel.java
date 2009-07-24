package jadex.gpmn.model;

import jadex.commons.SReflect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Java representation of a bpmn model for xml description.
 */
public class MGpmnModel extends MIdElement
{
	//-------- constants --------
	
//	/** Constant for maintain goal. */
//	public static final String MAINTAIN_GOAL = "MaintainGoal";
//
//	/** Constant for achieve goal. */
//	public static final String ACHIEVE_GOAL = "AchieveGoal";
//
//	/** Constant for query goal. */
//	public static final String QUERY_GOAL = "QueryGoal";
//
//	/** Constant for perform goal. */
//	public static final String PERFORM_GOAL = "PerformGoal";

	/** Constant for plan. */
	public static final String PLAN = "Plan";
	
	//-------- attributes --------
	
	/** The processes. */
	protected List processes;
	
	/** The artifacts. */
	protected List artifacts;
	
	/** The name of the model. */
	protected String name;
	
	//-------- init structures --------
	
	/** The cached edges of the model. */
	protected Map alledges;
	
	//-------- added structures --------
	
	/** The package. */
	protected String packagename;
	
	/** The imports. */
	protected String[] imports;
	
	//-------- methods --------

	/**
	 *  Get the processs.
	 *  @return The processs.
	 */
	public List getProcesses()
	{
		return processes;
	}
	
	/**
	 *  Add a process.
	 *  @param process The process. 
	 */
	public void addProcess(MProcess process)
	{
		if(processes==null)
			processes = new ArrayList();
		processes.add(process);
	}
	
	/**
	 *  Remove a process.
	 *  @param process The process.
	 */
	public void removeProcess(MProcess process)
	{
		if(processes!=null)
			processes.remove(process);
	}
	
	/**
	 *  Get the artifacts.
	 *  @return The artifacts. 
	 */
	public List getArtifacts()
	{
		return artifacts;
	}
	
	/**
	 *  Add an artifact.
	 *  @param artifact The artifact.  
	 */
	public void addArtifact(MArtifact artifact)
	{
		if(artifacts==null)
			artifacts = new ArrayList();
		artifacts.add(artifact);
	}
	
	/**
	 *  Remove an artifact.
	 *  @param artifact The artifact.
	 */
	public void removeArtifact(MArtifact artifact)
	{
		if(artifacts!=null)
			artifacts.remove(artifact);
	}
	
	//-------- helper init methods --------
	
	/**
	 *  Get the name of the model.
	 *  @return The name of the model.
	 */
	public String	getName()
	{
		return name;
	}
	
	/**
	 *  Set the name of the model.
	 *  @param name	The name to set.
	 */
	public void	setName(String name)
	{
		this.name	= name;
	}
	
	/**
	 *  Get all imports.
	 *  @return The imports.
	 */
	public String[] getAllImports()
	{
		return imports;
	}
	
	/**
	 *  Set the imports.
	 *  @param imports The imports.
	 */
	public void setImports(String[] imports)
	{
		this.imports = imports;
	}
	
	/**
	 *  Get the package name.
	 *  @return The package name.
	 */
	public String getPackage()
	{
		return packagename;
	}
	
	/**
	 *  Set the package name.
	 *  @param packagename The package name to set.
	 */
	public void setPackage(String packagename)
	{
		this.packagename = packagename;
	}

	/**
	 *  Get all sequence edges.
	 *  @return The sequence edges (id -> edge).
	 */
	public Map getAllSequenceEdges()
	{
		if(this.alledges==null)
		{
			this.alledges = new HashMap();
			// todo: hierarchical search also in lanes of pools?!
			
			List procs = getProcesses();
			if(procs!=null)
			{
				for(int i=0; i<procs.size(); i++)
				{
					MProcess tmp = (MProcess)processes.get(i);
					addEdges(tmp.getSequenceEdges(), alledges);
				}
			}
		}
		
		return alledges;
	}
	
	/**
	 *  Add edges to the result map.
	 *  @param tmp The list of edges.
	 *  @param edges The result map (id -> edge).
	 */
	protected void addEdges(List tmp, Map edges)
	{
		if(tmp!=null)
		{
			for(int i=0; i<tmp.size(); i++)
			{
				MSequenceEdge edge = (MSequenceEdge)tmp.get(i);
				edges.put(edge.getId(), edge);
			}
		}
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
		sbuf.append(")");
		return sbuf.toString();
	}
	
}
