package jadex.gpmn.editor.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;

import jadex.gpmn.editor.GpmnEditor;
import jadex.gpmn.editor.gui.controllers.DeletionController;
import jadex.gpmn.editor.gui.controllers.FoldController;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;

public class ModelContainer
{
	/** Select Edit Mode */
	public static final String SELECT_MODE			= "Select";
	
	/** Control Point Mode */
	public static final String CONTROL_POINT_MODE	= "Control_Points";
	
	/** Achieve Goal Edit Mode */
	public static final String ACHIEVE_GOAL_MODE	= "Goal_Achieve";
	
	/** Perform Goal Edit Mode */
	public static final String PERFORM_GOAL_MODE	= "Goal_Perform";
	
	/** Maintain Goal Edit Mode */
	public static final String MAINTAIN_GOAL_MODE	= "Goal_Maintain";
	
	/** Query Goal Edit Mode */
	public static final String QUERY_GOAL_MODE		= "Goal_Query";
	
	/** Ref Plan Edit Mode */
	public static final String REF_PLAN_MODE		= "Plan_Ref";
	
	/** Activation Plan Edit Mode */
	public static final String ACTIVATION_PLAN_MODE = "Plan_Activation";
	
	/** Suppression Edge Edit Mode */
	public static final String SUPPRESSION_EDGE_MODE = "Edge_Suppression";
	
	/** Node Creation Modes */
	public static final Set<String> NODE_CREATION_MODES = new HashSet<String>(Arrays.asList(new String[] 
	{
		ACHIEVE_GOAL_MODE,
		PERFORM_GOAL_MODE,
		MAINTAIN_GOAL_MODE,
		QUERY_GOAL_MODE,
		REF_PLAN_MODE,
		ACTIVATION_PLAN_MODE
	}));
	
	/** The mode file. */
	protected File file;
	
	/** The graph component. */
	protected mxGraphComponent graphcomponent;
	
	/** The current model. */
	protected IGpmnModel model;
	
	/** The edit mode tool bar. */
	protected AbstractEditingToolbar editingtoolbar;
	
	/** The dirty flag. */
	protected boolean dirty;
	
	/** The project root. */
	protected File projectroot;
	
	/** The model factory. */
	protected IGpmnModelFactory modelfactory;
	
	/** The image provider. */
	protected ImageProvider imageprovider;
	
	/** The property panel container. */
	protected JPanel propertypanelcontainer;
	
	/** The deletion controller */
	protected DeletionController deletioncontroller;
	
	/** The fold controller */
	protected FoldController foldcontroller;
	
	/**
	 *  Creates a new container.
	 */
	public ModelContainer(IGpmnModelFactory modelfactory, mxGraphComponent graphcomponent, IGpmnModel model)
	{
		this.modelfactory = modelfactory;
		this.graphcomponent = graphcomponent;
		this.model = model;
		this.imageprovider = new ImageProvider();
		this.foldcontroller = new FoldController(this);
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
	 *  Gets the model factory.
	 *
	 *  @return The model factory.
	 */
	public IGpmnModelFactory getModelFactory()
	{
		return modelfactory;
	}

	/**
	 *  Sets the model factory.
	 *
	 *  @param modelfactory The model factory.
	 */
	public void setModelFactory(IGpmnModelFactory modelfactory)
	{
		this.modelfactory = modelfactory;
	}
	
	/**
	 *  Gets the image provider.
	 *
	 *  @return The image provider.
	 */
	public ImageProvider getImageProvider()
	{
		return imageprovider;
	}
	
	/**
	 *  Gets the edit mode.
	 *
	 *  @return The edit mode.
	 */
	public String getEditMode()
	{
		return editingtoolbar.getEditMode();
	}

	/**
	 *  Sets the edit mode.
	 *
	 *  @param editmode The edit mode.
	 */
	public void setEditMode(String editmode)
	{
		editingtoolbar.setEditMode(editmode);
	}
	
	/**
	 *  Gets the editing tool bar.
	 *
	 *  @return The editing tool bar.
	 */
	public AbstractEditingToolbar getEditingToolbar()
	{
		return editingtoolbar;
	}
	
	/**
	 *  Sets the editing tool bar.
	 *
	 *  @param toolbar The editing tool bar.
	 */
	public void setEditingToolbar(AbstractEditingToolbar toolbar)
	{
		this.editingtoolbar = toolbar;
	}
	
	/**
	 *  Sets the current property panel.
	 *  
	 *  @param panel The panel.
	 */
	public void setPropertyPanel(JComponent panel)
	{
		propertypanelcontainer.removeAll();
		propertypanelcontainer.add(panel, BorderLayout.CENTER);
	}
	
	/**
	 *  Returns the controller for folding.
	 *   
	 *  @return Fold controller.
	 */
	public FoldController getFoldController()
	{
		return foldcontroller;
	}
	
	/**
	 *  Disables deletion controller,
	 *  desynchronizing visual and business model.
	 */
	public void desynchModels()
	{
		getGraph().removeListener(deletioncontroller, mxEvent.REMOVE_CELLS);
		deletioncontroller = null;
	}
	
	/**
	 *  Enables deletion controller,
	 *  synchronizing visual and business model.
	 */
	public void synchModels()
	{
		deletioncontroller = new DeletionController(this);
		getGraph().addListener(mxEvent.REMOVE_CELLS, deletioncontroller);
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
	 *  Handles unsaved model deletions.
	 *  
	 *  @param parent Parent component.
	 *  @param modelcontainer The model container.
	 */
	public boolean checkUnsaved(Component parent)
	{
		boolean ret = true;
		if (isDirty())
		{
			int result = JOptionPane.showConfirmDialog(parent, "The model contains unsaved changes, proceed anyway?", GpmnEditor.APP_NAME, JOptionPane.YES_NO_OPTION);
	        switch(result)
	        {
	            case JOptionPane.NO_OPTION:
	                ret = false;
	            case JOptionPane.CLOSED_OPTION:
	                ret = false;
	            case JOptionPane.YES_OPTION:
	            default:
	        }
		}
		return ret;
	}
	
	/**
	 *  Gets the property panel container.
	 *
	 *  @return The property panel container.
	 */
	protected JPanel getPropertyPanelContainer()
	{
		return propertypanelcontainer;
	}

	/**
	 *  Sets the property panel container.
	 *
	 *  @param propertypanelcontainer The property panel container.
	 */
	protected void setPropertyPanelContainer(JPanel propertypanelcontainer)
	{
		this.propertypanelcontainer = propertypanelcontainer;
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
		String[] ret = null;
		if (projectroot != null)
		{
			List<String> list = searchForClasses(projectroot);
			ret = list.toArray(new String[list.size()]);
		}
		return ret;
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
			File file = files[i];
			if (file.isDirectory() && !file.getName().equals(".") && !file.getName().equals(".."))
			{
				ret.addAll(searchForClasses(file));
			}
			else if (file.getAbsolutePath().endsWith(".java") ||
					 file.getAbsolutePath().endsWith(".class"))
			{
				String classname = file.getAbsolutePath().substring(projectroot.getAbsolutePath().length());
				while (classname.startsWith(File.separator))
				{
					classname = classname.substring(1);
				}
				classname = classname.substring(0, classname.lastIndexOf('.'));
				classname = classname.replaceAll(File.separator, ".");
				ret.add(classname);
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
		if (ind >= 0)
		{
			filepath = filepath.substring(0, ind);
			StringTokenizer tok = new StringTokenizer(pkg, ".");
			
			LinkedList<String> tokenstack = new LinkedList<String>();
			while (tok.hasMoreTokens())
			{
				tokenstack.add(tok.nextToken());
			}
			
			while (!tokenstack.isEmpty())
			{
				String pkgfrag = tokenstack.removeLast();
				ind = filepath.lastIndexOf(File.separator);
				if (ind >= 0 && filepath.length() > 1)
				{
					String pathfrag = filepath.substring(ind + 1);
					
					if (!pathfrag.equals(pkgfrag))
					{
						// Package/Directory mismatch
						return;
					}
					
					filepath = filepath.substring(0, ind);
				}
				else
				{
					return;
				}
			}
		}
		
		setProjectRoot(new File(filepath));
	}
}
