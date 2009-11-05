package jadex.gpmn.model;

import jadex.bridge.IArgument;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.IReport;
import jadex.commons.ICacheableModel;
import jadex.commons.SReflect;
import jadex.commons.SUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Java representation of a bpmn model for xml description.
 */
public class MGpmnModel extends MProcess implements ICacheableModel, ILoadableComponentModel
{
	//-------- attributes --------
	
	/** The processes. */
//	protected List processes;
	
	/** The artifacts. */
	protected List artifacts;
	
	/** The name of the model. */
//	protected String name;

	/** The description. */
	protected String description;
	
	//-------- init structures --------
	
	/** The cached edges of the model. */
	protected Map alledges;
	
	//-------- added structures --------
	
	/** The package. */
	protected String packagename;
	
	/** The imports. */
	protected String[] imports;
	
	//-------- model management --------
	
	/** The filename. */
	protected String filename;
	
	/** The last modified date. */
	protected long lastmodified;
	
	/** The last check date. */
	protected long lastchecked;
	
	/** The classloader. */
	protected ClassLoader classloader;
	
	//-------- methods --------

//	/**
//	 *  Get the processs.
//	 *  @return The processs.
//	 */
//	public List getProcesses()
//	{
//		return processes;
//	}
//	
//	/**
//	 *  Add a process.
//	 *  @param process The process. 
//	 */
//	public void addProcess(MProcess process)
//	{
//		if(processes==null)
//			processes = new ArrayList();
//		processes.add(process);
//	}
//	
//	/**
//	 *  Remove a process.
//	 *  @param process The process.
//	 */
//	public void removeProcess(MProcess process)
//	{
//		if(processes!=null)
//			processes.remove(process);
//	}
	
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
	 * /
	public String	getName()
	{
		return name;
	}*/
	
	/**
	 *  Set the name of the model.
	 *  @param name	The name to set.
	 * /
	public void	setName(String name)
	{
		this.name	= name;
	}*/
	
	/**
	 *  Get the description of the model.
	 *  @return The description of the model.
	 */
	public String	getDescription()
	{
		return description;
	}
	
	/**
	 *  Set the description of the model.
	 *  @param description	The description to set.
	 */
	public void	setDescription(String description)
	{
		this.description	= description;
	}
	
	/**
	 *  Get all imports.
	 *  @return The imports.
	 */
	public String[] getImports()
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
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename()
	{
		return this.filename;
	}

	/**
	 *  Set the filename.
	 *  @param filename The filename to set.
	 */
	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	/**
	 *  Get the lastmodified.
	 *  @return The lastmodified.
	 */
	public long getLastModified()
	{
		return this.lastmodified;
	}

	/**
	 *  Set the lastmodified.
	 *  @param lastmodified The lastmodified to set.
	 */
	public void setLastModified(long lastmodified)
	{
		this.lastmodified = lastmodified;
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
			
			addEdges(getSequenceEdges(), alledges);
			
//			List procs = getProcesses();
//			if(procs!=null)
//			{
//				for(int i=0; i<procs.size(); i++)
//				{
//					MProcess tmp = (MProcess)processes.get(i);
//					addEdges(tmp.getSequenceEdges(), alledges);
//				}
//			}
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
	 *  Get the last checked date.
	 *  @return The last checked date
	 */
	public long getLastChecked()
	{
		return this.lastchecked;
	}

	/**
	 *  Set the last checked date.
	 *  @param lastchecked The last checked date to set.
	 */
	public void setLastChecked(long lastchecked)
	{
		this.lastchecked = lastchecked;
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
	
	/**
	 *  Get the configurations.
	 *  @return The configuration.
	 */
	public String[] getConfigurations()
	{
		// todo: implement me
		
		String[] ret = SUtil.EMPTY_STRING;
		return ret;
	}
	
	/**
	 *  Is the model startable.
	 *  @return True, if startable.
	 */
	public boolean isStartable()
	{
		return true;
	}
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IArgument[] getArguments()
	{		
		// todo: 
		
		IArgument[] ret = new IArgument[0];
		return ret;
	}
	
	/**
	 *  Get the report.
	 *  @return The report.
	 */
	public IReport getReport()
	{
		// todo: 
		
		return new IReport()
		{
			public Map getDocuments()
			{
				return null;
			}
			
			public boolean isEmpty()
			{
				return true;
			}
			
			public String toHTMLString()
			{
				return "";
			}
		};
	}
	
	/**
	 *  Return the class loader corresponding to the micro agent class.
	 */
	public ClassLoader getClassLoader()
	{
		return classloader;
	}

	/**
	 *  Set the classloader.
	 *  @param classloader The classloader to set.
	 */
	public void setClassloader(ClassLoader classloader)
	{
		this.classloader = classloader;
	}
}
