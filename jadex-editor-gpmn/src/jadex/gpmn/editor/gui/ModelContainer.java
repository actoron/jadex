package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.model.gpmn.IGpmnModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.mxgraph.swing.mxGraphComponent;

public class ModelContainer implements IModelContainer
{
	/** The mode file. */
	protected File file;
	
	/** The graph component. */
	protected mxGraphComponent graphcomponent;
	
	/** The current model. */
	protected IGpmnModel model;
	
	/** The dirty flag. */
	protected boolean dirty;
	
	/** The project root. */
	protected File projectroot;
	
	/**
	 *  Creates a new container.
	 */
	public ModelContainer(mxGraphComponent graphcomponent, IGpmnModel model)
	{
		this.graphcomponent = graphcomponent;
		this.model = model;
	}
	
	/**
	 *  Returns the current visual graph component.
	 *  @return The graph.
	 */
	public mxGraphComponent getGraphComponent()
	{
		return graphcomponent;
	}
	
	/**
	 *  Returns the current visual graph.
	 *  @return The graph.
	 */
	public GpmnGraph getGraph()
	{
		return (GpmnGraph) graphcomponent.getGraph();
	}
	
	/**
	 *  Returns the GPMN intermediate model.
	 *  @return GPMN model.
	 */
	public IGpmnModel getGpmnModel()
	{
		return model;
	}
	
	/**
	 *  Sets the current visual graph.
	 *  @param graph The graph.
	 */
	public void setGraph(GpmnGraph graph)
	{
		graphcomponent.setGraph(graph);
	}
	
	/**
	 *  Sets the visual graph component.
	 *  @param component The component.
	 */
	public void setGraphComponent(mxGraphComponent component)
	{
		graphcomponent = component;
	}
	
	/**
	 *  Sets the GPMN model.
	 *  @param model The model.
	 */
	public void setGpmnModel(IGpmnModel model)
	{
		this.model = model;
		
	}
	
	/** 
	 *  Sets the dirty model state.
	 *  
	 *  @param dirty The dirty state.
	 */
	public void setDirty(boolean dirty)
	{
		this.dirty = dirty;
	}
	
	/**
	 *  Tests if the state is dirty.
	 *  
	 *  @return True, if dirty.
	 */
	public boolean isDirty()
	{
		return dirty;
	}
	
	/**
	 *  Gets the project root.
	 *  
	 *  @return The project root.
	 */
	public File getProjectRoot()
	{
		return projectroot;
	}
	
	/**
	 *  Sets the project root.
	 *  
	 *  @param root The project root.
	 */
	public void setProjectRoot(File root)
	{
		projectroot = root;
	}
	
	/**
	 *  Gets the model file.
	 *  
	 *  @return The model file.
	 */
	public File getFile()
	{
		return file;
	}
	
	/**
	 *  Sets the model file.
	 *  
	 *  @param file The model file.
	 */
	public void setFile(File file)
	{
		this.file = file;
		
		if (projectroot == null)
		{
			findProjectRoot();
		}
	}
	
	/**
	 *  Returns all available Java classes in the project.
	 *  
	 *  @return Array of class names, null if unknown.
	 */
	public String[] getProjectClasses()
	{
		return null;
	}
	
	/**
	 *  Recursive search for class files.
	 */
	public List<String> searchForClasses(File dir)
	{
		List<String> ret = new ArrayList<String>();
		
		File[] files = dir.listFiles();
		
		for (int i = 0; i < files.length; ++i)
		{
			if (files[i].isDirectory())
			{
				//ret.addAll(c)
			}
		}
		
		return ret;
	}
	
	/** Attempts to find the project root. */
	protected void findProjectRoot()
	{
		String pkg = model.getPackage();
		String filepath = file.getAbsolutePath();
		int ind = filepath.lastIndexOf(File.separator);
		boolean correctpath = false;
		if (ind >= 0)
		{
			filepath = filepath.substring(0, ind);
			StringTokenizer tok = new StringTokenizer(pkg, ".");
			while (!correctpath)
			{
				String pkgfrag = tok.nextToken();
				ind = filepath.lastIndexOf(File.separator);
				if (ind >= 0 && filepath.length() > 1)
				{
					String pathfrag = filepath.substring(ind + 1);
					
					if (!pathfrag.equals(pkgfrag))
					{
						break;
					}
					
					filepath = filepath.substring(0, ind);
				}
				
				if (!tok.hasMoreElements())
				{
					correctpath = true;
				}
			}
		}
		
		if (correctpath)
		{
			setProjectRoot(new File(filepath));
		}
	}
}
