package jadex.gpmn.model;

import jadex.bridge.modelinfo.ModelInfo;
import jadex.commons.ICacheableModel;
import jadex.commons.SReflect;
import jadex.gpmn.GpmnFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MGpmnModel implements ICacheableModel//, IModelInfo
{
	//-------- attributes --------
	
//	/** The model file name. */
//	protected String filename;
	
//	/** The model name. */
//	protected String name;
	
//	/** The description. */
//	protected String description;
	
	/** The last modified date. */
	protected long lastmodified;
	
	/** The last check date. */
	protected long lastchecked;
	
//	/** The classloader. */
//	protected ClassLoader classloader;
	
//	/** The model package. */
//	protected String modelpackage;
	
	/** The imports. */
	//protected List imports;
	
	/** The goals. */
	protected Map goals;
	
	/** The activation plans. */
	protected Map activationplans;
	
	/** The BPMN plans. */
	protected Map bpmnplans;
	
	/** The subprocesses. */
	protected Map subprocesses;
	
	/** The activation edges. */
	protected List activationedges;
	
	/** The plan edges. */
	protected List planedges;
	
	/** The context. */
	protected MContext context;
	
	/** The model info. */
	protected ModelInfo modelinfo;
	
	/**
	 *  Create a new model.
	 */
	public MGpmnModel()
	{
		//this.imports = new ArrayList();
		this.goals = new HashMap();
		this.activationplans = new HashMap();
		this.bpmnplans = new HashMap();
		this.subprocesses = new HashMap();
		this.activationedges = new ArrayList();
		this.planedges = new ArrayList();
		this.modelinfo = new ModelInfo();
		modelinfo.internalSetRawModel(rawmodel);
		modelinfo.setType(GpmnFactory.FILETYPE_GPMNPROCESS);
	}
	
	/**
	 *  Init the model info.
	 */
	public void initModelInfo()
	{
		// todo: breakpoints and configurations
//		modelinfo.addProperty("debugger.breakpoints", names);
//		modelinfo.setConfigurations(getConfigurations());
		
		if(getModelInfo().getProperties()==null)
			getModelInfo().setProperties(new HashMap());
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
	 *  Get the name.
	 *  @return The name.
	 * /
	public String getName()
	{
		return name;
	}*/

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		modelinfo.setName(name);
	}

	/**
	 *  Get the full model name (package.name)
	 *  @return The full name.
	 * /
	public String getFullName()
	{
		String pkg = getPackage();
		return pkg!=null && pkg.length()>0? pkg+"."+getName(): getName();
	}*/
	
	/**
	 *  Get the filename.
	 *  @return The filename.
	 * /
	public String getFilename()
	{
		return filename;
	}*/

	/**
	 *  Set the filename.
	 *  @param filename The filename to set.
	 */
	public void setFilename(String filename)
	{
		modelinfo.setFilename(filename);
	}
	
	/**
	 *  Get the description of the model.
	 *  @return The description of the model.
	 * /
	public String	getDescription()
	{
		return description;
	}*/
	
	/**
	 *  Set the description of the model.
	 *  @param description	The description to set.
	 */
	public void	setDescription(String description)
	{
		modelinfo.setDescription(description);
	}

	/**
	 *  Get lastmodified.
	 *  @return The lastmodified.
	 */
	public long getLastModified()
	{
		return lastmodified;
	}

	/**
	 *  Set lastmodified.
	 *  @param lastmodified The lastmodified to set.
	 */
	public void setLastModified(long lastmodified)
	{
		this.lastmodified = lastmodified;
	}
	
	/**
	 *  Get the lastchecked.
	 *  @return The lastchecked.
	 */
	public long getLastChecked()
	{
		return lastchecked;
	}

	/**
	 *  Set the lastchecked.
	 *  @param lastchecked The lastchecked to set.
	 */
	public void setLastChecked(long lastchecked)
	{
		this.lastchecked = lastchecked;
	}
	
	/**
	 *  Return the class loader.
	 */
	public ClassLoader getClassLoader()
	{
		return modelinfo.getClassLoader();
	}

	/**
	 *  Set the classloader.
	 *  @param classloader The classloader to set.
	 */
	public void setClassLoader(ClassLoader classloader)
	{
		modelinfo.setClassloader(classloader);
	}
	
	/**
	 *  Get the results.
	 *  @return The results.
	 * /
	public IArgument[] getResults()
	{
		// todo:
		return new IArgument[0];
	}*/
	
	/**
	 *  Get the configurations.
	 *  @return The configuration.
	 * /
	public String[] getConfigurations()
	{
		// todo: implement me
		
		String[] ret = SUtil.EMPTY_STRING_ARRAY;
		return ret;
	}*/
	
	/**
	 *  Is the model startable.
	 *  @return True, if startable.
	 * /
	public boolean isStartable()
	{
		return true;
	}*/
	
	/**
	 *  Get the properties.
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools. 
	 *  @return The properties.
	 * /
	public Map	getProperties()
	{
		// Todo: implement me.
		return Collections.EMPTY_MAP;
	}*/
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 * /
	public IArgument[] getArguments()
	{		
		// todo: 
		
		IArgument[] ret = new IArgument[0];
		return ret;
	}*/
	
	/**
	 *  Get the report.
	 *  @return The report.
	 * /
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
	}*/
	
	// --------------------------------------------------------------------

	/**
	 *  Get the package.
	 *  @return The package.
	 * /
	public String getPackage()
	{
		return modelpackage;
	}*/

	/**
	 *  Set the package.
	 *  @param modelpackage The package to set.
	 */
	public void setPackage(String modelpackage)
	{
		modelinfo.setPackage(modelpackage);
	}
	
	/**
	 *  Adds an import.
	 *  @param theImport The import.
	 */
	public void addImport(String theImport)
	{
		//imports.add(theImport);
		modelinfo.addImport(theImport);
	}
	
	/**
	 *  Removes an import.
	 *  @param theImport The import.
	 */
	//public void removeImport(String theImport)
	//{
		//imports.remove(theImport);
		//modelinfo.removeImport(theImport);
	//}
	
	/**
	 *  Adds a goal.
	 *  @param goal The goal.
	 */
	public void addGoal(MGoal goal)
	{
		goals.put(goal.getId(), goal);
	}
	
	/**
	 *  Removes a goal.
	 *  @param goal The goal.
	 */
	public void removeGoal(MGoal goal)
	{
		goals.remove(goal.getId());
	}
	
	/**
	 *  Adds an activation plan.
	 *  @param plan The activation plan.
	 */
	public void addActivationPlan(MActivationPlan plan)
	{
		activationplans.put(plan.getId(), plan);
	}
	
	/**
	 *  Removes an activation plan.
	 *  @param plan The activation plan.
	 */
	public void removeActivationPlan(MActivationPlan plan)
	{
		activationplans.remove(plan.getId());
	}
	
	/**
	 *  Adds a BPMN plan.
	 *  @param plan The BPMN plan.
	 */
	public void addBpmnPlan(MBpmnPlan plan)
	{
		bpmnplans.put(plan.getId(), plan);
	}
	
	/**
	 *  Removes a BPMN plan.
	 *  @param plan The BPMN plan.
	 */
	public void removeBpmnPlan(MBpmnPlan plan)
	{
		bpmnplans.remove(plan.getId());
	}
	
	/**
	 *  Adds a subprocess.
	 *  @param subprocess The subprocess.
	 */
	public void addSubprocess(MSubprocess subprocess)
	{
		subprocesses.put(subprocess.getId(), subprocess);
	}
	
	/**
	 *  Removes a subprocess.
	 *  @param subprocess The subprocess.
	 */
	public void removeSubprocess(MSubprocess subprocess)
	{
		subprocesses.remove(subprocess.getId());
	}
	
	/**
	 *  Adds an activation edge.
	 *  @param edge The edge.
	 */
	public void addActivationEdge(MActivationEdge edge)
	{
		activationedges.add(edge);
	}
	
	/**
	 *  Removes an activation edge.
	 *  @param edge The edge.
	 */
	public void removeActivationEdge(MActivationEdge edge)
	{
		activationedges.remove(edge);
	}
	
	/**
	 *  Adds a plan edge.
	 *  @param edge The edge.
	 */
	public void addPlanEdge(MPlanEdge edge)
	{
		planedges.add(edge);
	}
	
	/**
	 *  Removes a plan edge.
	 *  @param edge The edge.
	 */
	public void removePlanEdge(MPlanEdge edge)
	{
		planedges.remove(edge);
	}

	/**
	 *  Get the activationedges.
	 *  @return The activationedges.
	 */
	public List getActivationEdges()
	{
		return activationedges;
	}

	/**
	 *  Get the planedges.
	 *  @return The planedges.
	 */
	public List getPlanEdges()
	{
		return planedges;
	}

	/**
	 *  Get the imports.
	 *  @return The imports.
	 */
	public List getImports()
	{
		return Arrays.asList(modelinfo.getAllImports());
	}

	/**
	 *  Get the goals.
	 *  @return The goals.
	 */
	public Map getGoals()
	{
		return goals;
	}

	/**
	 *  Get the activationplans.
	 *  @return The activationplans.
	 */
	public Map getActivationPlans()
	{
		return activationplans;
	}

	/**
	 *  Get the bpmnplans.
	 *  @return The bpmnplans.
	 */
	public Map getBpmnPlans()
	{
		return bpmnplans;
	}
	
	/**
	 *  Get the subprocesses.
	 *  @return The subprocesses.
	 */
	public Map getSubprocesses()
	{
		return subprocesses;
	}

	/**
	 *  Get the context.
	 *  @return The context.
	 */
	public MContext getContext()
	{
		return context;
	}

	/**
	 *  Set the context.
	 *  @param context The context to set.
	 */
	public void setContext(MContext context)
	{
		this.context = context;
	}
	
	/**
	 *  Get the modelinfo.
	 *  @return the modelinfo.
	 */
	public ModelInfo getModelInfo()
	{
		return modelinfo;
	}
	
	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename()
	{
		return modelinfo.getFilename();
	}
}
